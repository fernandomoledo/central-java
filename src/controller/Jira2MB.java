package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

import dao.AndamentoDAO;
import dao.ChamadoDAO;
import dao.JiraDAO;
import dao.TomboDAO;
import model.Andamento;
import model.Assyst;
import model.Chamado;
import model.ComponenteJIRA;
import model.DeParaJIRA;
import model.IssueJIRA;
import model.ModuloJIRA;
import model.RetornoJIRA;
import model.Tombo;
import util.Mensagens;

@ManagedBean
@ViewScoped
public class Jira2MB {
	private List<Assyst> chamadosJira = this.listaChamados();
	private Andamento selecionado; 
	private Andamento chamadoDetalhe = new Andamento();
	private List<Tombo> tombosDetalhe = new ArrayList<Tombo>();
	private List<Andamento> andamentosDetalhe = new ArrayList<Andamento>();
	private Andamento ultimoAndamento = new Andamento();
	private boolean mostrar = false;
	private String modulo = "", componente="", projeto = "", projetoId="", subsistema = "", processo = "", resumo = "", tipo= "", versao = "", ambiente = "", usuario = "", descricao="";
	private ModuloJIRA moduloJira = new ModuloJIRA();
	private List<ModuloJIRA> filteredModulos;
	private ComponenteJIRA componenteJIRA = new ComponenteJIRA();
	private List<ComponenteJIRA> filteredComponentes;
	private DeParaJIRA deParaJIRA = new DeParaJIRA();
	private List<DeParaJIRA> filteredDeParaJIRA;
	private IssueJIRA issueJira = new IssueJIRA();
	private boolean geraIssue = true;
	private String idIssue = "";
	private String req = "R";
	

	final static Logger logger = Logger.getLogger(JiraMB.class);
	
	public Jira2MB(){
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
		if(!params.isEmpty()){
			try {
				this.preparaIssue();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String abrir(){
		switch(req.substring(1).length()){
		case 1:
			return "detalhe-jira2.jsf?id=500000"+req.substring(1)+"&faces-redirect=true"; 
		case 2:
			return "detalhe-jira2.jsf?id=50000"+req.substring(1)+"&faces-redirect=true"; 
		case 3:
			return "detalhe-jira2.jsf?id=5000"+req.substring(1)+"&faces-redirect=true"; 
		default:
			return "detalhe-jira2.jsf?id=500"+req.substring(1)+"&faces-redirect=true"; 
		}
	}
	
	public List<Assyst> listaChamados(){
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) ec.getSession(false);
		String login = session.getAttribute("usuarioLogado").toString();
		
		String[] logins = {"adrianacavaggioni","douglasfracalossi","filipepiga","gustavomilanezi","leandragenka","lucianodavini","mauriciofontana","victorbarros"};
		String[] uids = {"282","300","392","309","388","387","391","389"};
		try{
			System.getProperties().put("http.proxyHost", "proxy.trt15.jus.br");
		 	System.getProperties().put("http.proxyPort", "3128");
		 	System.getProperties().put("http.proxyUser", "oab");
		 	System.getProperties().put("http.proxyPassword", "oab15");
			System.getProperties().put("https.proxyHost", "proxy.trt15.jus.br");
		 	System.getProperties().put("https.proxyPort", "3128");
		 	System.getProperties().put("https.proxyUser", "oab");
		 	System.getProperties().put("https.proxyPassword", "oab15");
		 	
		 	/*
		 	 * For para percorrer logins e trazer uids
		 	 */
		 	String pos = "";
		 	
		 	for(int i = 0; i < logins.length; i++){
		 		if(logins[i].equals(login)){
		 			pos = uids[i];
		 			break;
		 		}else{
		 			pos = "-1";
		 		}
		 	}
		 
			String url = "https://www.trt15.jus.br/assystREST/v1/events?eventType=4&eventStatus=open&affectedUserName=Nucleo%20Apoiopje&assignedUserId="+pos+"&callbackRequired=true";
			System.out.println("UID: "+pos+" / URL: "+url);
			
		 	//String url = "https://www.trt15.jus.br/assystREST/v1/events?eventType=4&eventStatus=open&affectedUserName=Nucleo%20Apoiopje";
		 	//String url = "https://www.trt15.jus.br/assystREST/v1/events/5000421";
		    URL obj = new URL(url);
		    HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
	
		    conn.setRequestProperty("Content-Type", "application/xml");
		    conn.setDoOutput(true);
	
		    conn.setRequestMethod("GET");
		  
		    String userpass = "assyst" + ":" + "axios";
		    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		    conn.setRequestProperty ("Authorization", basicAuth);
		           
	        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
	
	        String xml = "";
	        String s = "";
	        while(( s = reader.readLine()) != null){
				xml += s;
			}
	        
	        System.out.println("XML: "+xml);
	        
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    InputSource is = new InputSource();
		    is.setCharacterStream(new StringReader(xml));
		
		    System.out.println(is.getEncoding());
		    Document doc = db.parse(is);
		    NodeList nodes = doc.getElementsByTagName("event");
		    
		    List<Assyst> lista = new ArrayList<Assyst>();
		    for (int i = 0; i < nodes.getLength(); i++) {
		      Element element = (Element) nodes.item(i);
	
		      Assyst assyst = new Assyst();
		      assyst.setId(element.getElementsByTagName("id").item(0).getTextContent());
		      assyst.setChamado(element.getElementsByTagName("formattedReference").item(0).getTextContent());
		      assyst.setDataAbertura(element.getElementsByTagName("dateLogged").item(0).getTextContent().replace("T", " ").replace("-03:00", ""));
		      lista.add(assyst);
		      
		    }
		    return lista;
		}catch(SAXException|ParserConfigurationException|IOException s){
			s.printStackTrace();
			return null;
		}
	}
	
	public List<Assyst> getChamadosJira() {
		return chamadosJira;
	}

	public void getDetalhe() throws NamingException{
			long id = this.selecionado.getChamado().getId();
			ChamadoDAO cDao = new ChamadoDAO();
			TomboDAO tDao = new TomboDAO();
			AndamentoDAO aDao = new AndamentoDAO();
			try {
				this.chamadoDetalhe = cDao.getInfoChamado(id);				
				this.tombosDetalhe = tDao.getTombosPorChamado(id);				
				this.andamentosDetalhe = aDao.getAndamentosPorChamado(id);				
				this.ultimoAndamento = this.andamentosDetalhe.get(this.andamentosDetalhe.size()-1);
				this.setMostrar(true);
			
			} catch (ClassNotFoundException | SQLException e) {
				Mensagens.setMessage(3, "Não foi possível obter o detalhe do chamado. "+e.getMessage());
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				logger.error("ERRO: " + stack.toString());
			} 
		
	}
	
	public void salvarModulo(){
		JiraDAO dao = new JiraDAO();
		
		if(this.moduloJira.getId() == 0){
			try{
				if(dao.salvarModulo(this.moduloJira)){
					Mensagens.setMessage(1, "Módulo '"+this.moduloJira.getLabel()+"' salvo com sucesso!");
				}
			}catch(Exception e){
				Mensagens.setMessage(3, "Erro ao salvar módulo '"+this.moduloJira.getLabel()+"': "+e.getMessage());
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				logger.error("ERRO: " + stack.toString());
			}
		}else{
			try{
				if(dao.atualizarModulo(this.moduloJira)){
					Mensagens.setMessage(1, "Módulo atualizado com sucesso!");
				}
			}catch(Exception e){
				Mensagens.setMessage(3, "Erro ao atualizar módulo: "+e.getMessage());
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				logger.error("ERRO: " + stack.toString());
			}
		}
		this.moduloJira = new ModuloJIRA();
		this.filteredModulos = null;
	}
	
	public List<ModuloJIRA> listarModulos(){
		JiraDAO dao = new JiraDAO();
		try{
			return dao.listarModulos();
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			return new ArrayList<ModuloJIRA>();
		}
	}
	
	public void excluirModulo(int id){
		JiraDAO dao = new JiraDAO();
		try{
			if(dao.excluirModulo(id)){
				Mensagens.setMessage(1, "Módulo excluído com sucesso!");
			}
		}catch(Exception e){
			Mensagens.setMessage(3, "Erro ao excluir módulo: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
		}
	}
	
	public void buscarModulo(int id){
		JiraDAO dao = new JiraDAO();
		try{
			this.moduloJira = dao.getModuloById(id);
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
		}
	}
	
	public void salvarComponente(){
		JiraDAO dao = new JiraDAO();
		
		if(this.componenteJIRA.getId() == 0){
			try{
				if(dao.salvarComponente(this.componenteJIRA)){
					Mensagens.setMessage(1, "Componente '"+this.componenteJIRA.getComponente()+"' salvo com sucesso!");
				}
			}catch(Exception e){
				Mensagens.setMessage(3, "Erro ao salvar componente '"+this.componenteJIRA.getComponente()+"': "+e.getMessage());
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				logger.error("ERRO: " + stack.toString());
			}
		}else{
			try{
				if(dao.atualizarComponente(this.componenteJIRA)){
					Mensagens.setMessage(1, "Componente atualizado com sucesso!");
				}
			}catch(Exception e){
				Mensagens.setMessage(3, "Erro ao atualizar componente: "+e.getMessage());
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				logger.error("ERRO: " + stack.toString());
			}
		}
		this.componenteJIRA = new ComponenteJIRA();
		this.filteredComponentes = null;
	}
	
	public List<ComponenteJIRA> listarComponentes(){
		JiraDAO dao = new JiraDAO();
		try{
			return dao.listarComponentes();
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			return new ArrayList<ComponenteJIRA>();
		}
	}
	
	public void excluirComponente(int id){
		JiraDAO dao = new JiraDAO();
		try{
			if(dao.excluirComponente(id)){
				Mensagens.setMessage(1, "Componente excluído com sucesso!");
			}
		}catch(Exception e){
			Mensagens.setMessage(3, "Erro ao excluir componente: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
		}
	}
	
	public void buscarComponente(int id){
		JiraDAO dao = new JiraDAO();
		try{
			this.componenteJIRA = dao.getComponenteById(id);
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
		}
	}
	
	public void salvarDePara(){
		JiraDAO dao = new JiraDAO();
		this.deParaJIRA.setModulo(this.moduloJira);
		this.deParaJIRA.setComponente(this.componenteJIRA);
		
		if(this.deParaJIRA.getId() == 0){
			try{
				if(dao.salvarDePara(this.deParaJIRA)){
					Mensagens.setMessage(1, "Vinculação salva com sucesso!");
				}
			}catch(Exception e){
				Mensagens.setMessage(3, "Erro ao salvar vinculação: "+e.getMessage());
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				logger.error("ERRO: " + stack.toString());
			}
		}else{
			try{
				if(dao.atualizarDePara(this.deParaJIRA)){
					Mensagens.setMessage(1, "Vinculação atualizada com sucesso!");
				}
			}catch(Exception e){
				Mensagens.setMessage(3, "Erro ao atualizar vinculação: "+e.getMessage());
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				logger.error("ERRO: " + stack.toString());
			}
		}
		this.deParaJIRA = new DeParaJIRA();
		this.moduloJira = new ModuloJIRA();
		this.componenteJIRA = new ComponenteJIRA();
	}
	
	public List<DeParaJIRA> listarDePara(){
		JiraDAO dao = new JiraDAO();
		try{
			return dao.listarDePara();
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			return new ArrayList<DeParaJIRA>();
		}
	}
	
	public void excluirDePara(int id){
		JiraDAO dao = new JiraDAO();
		try{
			if(dao.excluirDePara(id)){
				Mensagens.setMessage(1, "Vinculação excluída com sucesso!");
			}
		}catch(Exception e){
			Mensagens.setMessage(3, "Erro ao excluir vinculação: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
		}
	}
	
	public void buscarDePara(int id){
		JiraDAO dao = new JiraDAO();
		try{
			this.deParaJIRA = dao.getDeParaById(id);
			this.moduloJira = this.deParaJIRA.getModulo();
			this.componenteJIRA = this.deParaJIRA.getComponente();
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
		}
	}
	
	public void redirect(String chamado) throws IOException{
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.redirect("https://centraldetic.trt15.jus.br/assystweb/application.do#eventsearch/EventSearchDelegatingDispatchAction.do?dispatch=monitorInit&ajaxMonitor=false&eventSearchContext&queryProfileForm.columnProfileId=0&queryProfileForm.queryProfileId=0&event.lookup.eventRefRange="+chamado);
	}
	public void preparaIssue() throws IOException{
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();

		if(!params.isEmpty()){
			idIssue = params.get("id");
		}
		
		System.getProperties().put("http.proxyHost", "proxy.trt15.jus.br");
	 	System.getProperties().put("http.proxyPort", "3128");
	 	System.getProperties().put("http.proxyUser", "oab");
	 	System.getProperties().put("http.proxyPassword", "oab15");
		System.getProperties().put("https.proxyHost", "proxy.trt15.jus.br");
	 	System.getProperties().put("https.proxyPort", "3128");
	 	System.getProperties().put("https.proxyUser", "oab");
	 	System.getProperties().put("https.proxyPassword", "oab15");
	 
		String url = "https://www.trt15.jus.br/assystREST/v1/events/"+idIssue;
	    URL obj = new URL(url);
	    HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

	    conn.setRequestProperty("Content-Type", "application/xml");
	    conn.setDoOutput(true);

	    conn.setRequestMethod("GET");
	  
	    String userpass = "assyst" + ":" + "axios";
	    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
	    conn.setRequestProperty ("Authorization", basicAuth);
	           
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));

        String xml = "";
        String s = "";
        while(( s = reader.readLine()) != null){
			xml += s + "\n";
		}
        
        if(!xml.contains("Tipo de Pendência") && !xml.contains("Incidente")){
        	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        	ec.redirect("erro-issue.html");
        }else{
        
	        String descrOriginal = xml.substring(xml.indexOf("<remarks>")+9,xml.indexOf("&lt;=="));     
	        String issue = xml.substring(xml.indexOf("<remarks>")+9,xml.indexOf("</remarks>")-1).replace("\n", "");
	        
	        
	        //issue exemplo sem modulo e componente
	        //issue = "Após clicar no botão ''Grava'' na tarefa ''Minutar despacho - conversão em diligência'', no processo 0012061-78.2015.5.15.0015 , o sistema não exibe o botão ''Enviar para assinatura'', apesar de informar que o documento foi salvo.<==# ADDITIONAL INFORMATION (DO NOT EDIT) #=** *Tipo de PendênciaIncidente** *ResumoTarefa - Erro de Movimentação - 0012061-78.2015.5.15.0015** *Versão do PJE1.13.2** *Urgência4** *Subsistema1º Grau** *Qual é o Ambiente?Produção** *Perfil do usuárioJose Aparecido de Alcantara Tavares / 070.027.238-04 / Assessor / 1ª Vara do Trabalho de Franca** Número dos processos0012061-78.2015.5.15.0015#==>";
	        
	        //System.out.println(descrOriginal);
	        // ends here
			
			try{
					issueJira.setId("10311");
					issueJira.setNome("PJE-JT");
					
					issueJira.setDescricao(this.substitui(descrOriginal));
			        issueJira.setTipoErro(issue.substring(issue.indexOf("** *")+21,issue.indexOf("** *R")));
			        issueJira.setResumo(this.substitui(issue.substring(issue.indexOf("*Resumo")+7,issue.indexOf("** *V")).replace("\"", "'")));
			        issueJira.setUrgencia(String.valueOf(Integer.parseInt(issue.substring(issue.indexOf("*Urgência")+9,issue.indexOf("** *S")))));
			        if(issue.indexOf("** M") > -1){
				        issueJira.setSubsistema(issue.substring(issue.indexOf("*Subsistema")+11,issue.indexOf("** M")));
				        System.out.println("Módulo: " + issue.substring(issue.indexOf("** Módulo")+9,issue.indexOf("** C")));
				        System.out.println("Componentes: " + issue.substring(issue.indexOf("** Componentes")+14,issue.indexOf("** *Q")));
			        }else{
			        	issueJira.setSubsistema(issue.substring(issue.indexOf("*Subsistema")+11,issue.indexOf("** *Q")));
			        }
			        issueJira.setAmbiente(issue.substring(issue.indexOf("Ambiente?")+9,issue.indexOf("** *P")));
			        issueJira.setVersao(issue.substring(issue.indexOf("*Versão do PJE")+14,issue.indexOf("** *U"))+ " - "+issueJira.getAmbiente());
			        issueJira.setServidor(this.substitui(issue.substring(issue.indexOf("*Perfil do usuário")+18,issue.indexOf("** N"))));
			        issueJira.setProcesso(this.substitui(issue.substring(issue.indexOf("** Número dos processos")+23,issue.indexOf("#=="))));
			        issueJira.setChamado(xml.substring(xml.indexOf("<formattedReference>")+20,xml.indexOf("</formattedReference>")));
			        
			       
			
				 
				 JiraDAO daoJira = new JiraDAO();
				 
				 for(DeParaJIRA dp : daoJira.listarDePara()){
					if(issueJira.getResumo().contains(dp.getPalavraChave())){
						issueJira.setModulo(String.valueOf(dp.getModulo().getValue()));
						issueJira.setComponente(dp.getComponente().getComponente());
					}
				 }
				 System.out.println("Módulo - "+issueJira.getModulo()+" / " + issueJira.getComponente());
				
			}catch(Exception e){
				Mensagens.setMessage(3, "Não foi possível preparar o chamado para criação de issue. "+e.getMessage());
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				logger.error("ERRO: " + stack.toString());
			}
        }
	}
	
	public void criarIssue() throws IOException, InterruptedException{
		/*String cmd = "curl -D- -u trt15:pje@alemanha -X POST --data '{\"fields\":{\"project\": {\"id\":\""+this.issue.getId()+"\"}, \"summary\":\""+this.issue.getResumo()+
				"\", \"issuetype\":{\"id\": \"59\"}, \"versions\":[{\"name\":\""+this.issue.getVersao()+"\"}], \"environment\":\""+this.issue.getAmbiente()+
				"\", \"description\":\""+this.issue.getDescricao()+"\",\"components\":[{\"name\":\""+this.issue.getComponente()+"\"}],\"customfield_11741\":{\"value\":\""+this.issue.getSubsistema()+
				"\"}, \"customfield_12243\":{\"id\":\""+this.issue.getModulo()+"\",\"value\":\""+this.issue.getModulo()+"\"},\"customfield_11441\":\""+this.issue.getServidor()+
				"\", \"customfield_12241\":\""+this.issue.getChamado()+"\",\"customfield_11542\":\""+this.issue.getProcesso()+"\"}}' -H \"Content-Type:application/json\" https://pje.csjt.jus.br/jira/rest/api/2/issue/ -k";
		*/
		try{
			/*
			 * 
			 * Antes de qualquer coisa, adicionar o certificado do site do Jira, seguindo os passos abaixo 
			 * no servidor da aplicaÃ§Ã£o:
			 * https://azure.microsoft.com/pt-br/documentation/articles/java-add-certificate-ca-store/
			 * ou
			 * cd /usr/lib/jvm/java-8-oracle/jre/lib/security e
			 * keytool -keystore cacerts -importcert -alias aliascertificado -file Secure_Certificate_Authority.cer
			 * senha changeit
			 * para verificar: keytool -list -keystore cacerts | grep JIRA
			 */
			System.getProperties().put("http.proxyHost", "proxy.trt15.jus.br");
		 	System.getProperties().put("http.proxyPort", "3128");
		 	System.getProperties().put("http.proxyUser", "oab");
		 	System.getProperties().put("http.proxyPassword", "oab15");
			System.getProperties().put("https.proxyHost", "proxy.trt15.jus.br");
		 	System.getProperties().put("https.proxyPort", "3128");
		 	System.getProperties().put("https.proxyUser", "oab");
		 	System.getProperties().put("https.proxyPassword", "oab15");
		 
		 	
			String url = "https://pje.csjt.jus.br/jira/rest/api/2/issue/";
		    URL obj = new URL(url);
		    HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

		    conn.setRequestProperty("Content-Type", "application/json");
		    conn.setDoOutput(true);

		    conn.setRequestMethod("POST");
		  
		    String userpass = "trt15" + ":" + "pje@alemanha";
		    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		    conn.setRequestProperty ("Authorization", basicAuth);

		    String data = "{\"fields\":{\"project\": {\"id\":\""+this.issueJira.getId()+"\"}, \"summary\":\""+this.issueJira.getResumo()+
				"\", \"issuetype\":{\"id\": \"59\"}, \"versions\":[{\"name\":\""+this.issueJira.getVersao()+"\"}], \"customfield_11940\":"+this.issueJira.getUrgencia()+",\"environment\":\""+this.issueJira.getAmbiente()+
				"\", \"description\":\""+this.issueJira.getDescricao().trim()+"\",\"components\":[{\"name\":\""+this.issueJira.getComponente()+"\"}],\"customfield_11741\":{\"value\":\""+this.issueJira.getSubsistema()+
				"\"}, \"customfield_12243\":{\"id\":\""+this.issueJira.getModulo()+"\",\"value\":\""+this.issueJira.getModulo()+"\"},\"customfield_11441\":\""+this.issueJira.getServidor()+
				"\", \"customfield_12241\":\""+this.issueJira.getChamado()+"\",\"customfield_11542\":\""+this.issueJira.getProcesso()+"\"}}";
		    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		    System.out.println(data.replace("\\", "\\\\").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		    out.write(data.replace("\\", "\\\\").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		    out.close();
		    //System.out.println(data.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		    //InputStream is = conn.getInputStream();
            //InputStreamReader isr = new InputStreamReader(is);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));

            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();
            //while ((numCharsRead = isr.read(charArray)) > 0) {
            while ((numCharsRead = reader.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            String result = sb.toString();

            System.out.println("*** BEGIN ***");
            System.out.println(result);
            System.out.println("*** END ***"); 
        	Gson gson = new Gson();
    		RetornoJIRA ret = gson.fromJson(result, RetornoJIRA.class);
    		String msgSaida = "https://pje.csjt.jus.br/jira/browse/"+ret.getKey();
    		
    		 Runtime rt = Runtime.getRuntime();
			 Process p = rt.exec("curl -D- -X GET http://10.15.199.183:8989/assyst/assystEJB/Action/new?eventId="+idIssue+"&actionTypeId=3");
			 if(!p.waitFor(30,TimeUnit.SECONDS)){
				 Mensagens.setMessage(3, "Não foi possível dar o andamento 'Iniciar atendimento' no chamado "+issueJira.getChamado());
				 p.destroy();
			 }
			 Thread.sleep(1000);
			 System.out.println(p.toString());
			p = rt.exec("curl -D- -X GET http://10.15.199.183:8989/assyst/assystEJB/Action/new?eventId="+idIssue+"&actionTypeId=121&remarks=\"Criada%20a%20issue%20-%20" + msgSaida +"\"");
			if(!p.waitFor(30,TimeUnit.SECONDS)){
				 Mensagens.setMessage(3, "Não foi possível dar o andamento 'Pendente de terceiros' no chamado "+issueJira.getChamado());
				 p.destroy();
			 }
			System.out.println(p.toString());
    		Mensagens.setMessage(1, "Criada a issue: " + msgSaida+". Chamado "+issueJira.getChamado() +" alterado para \"Pendente de terceiros\".");
    		
		 	System.out.println("Chamado Assyst: "+idIssue);
    		this.geraIssue = false;
		 } catch (Exception e) {
			 System.out.println(e.toString());
			 Mensagens.setMessage(3, "Erro ao criar a issue para o chamado "+issueJira.getChamado()+": "+e.getMessage());
			 StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				logger.error("ERRO: " + stack.toString());
		 }
	
	}
	
	public String substitui(String t){
		//return t.replace("\"", "''").replace("&amp;", "\\&").replace("&lt;","<").replace("&gt;",">").replace("<empty>", "-");
		return t.replace("\"", "''").replace("&amp;", "\\&").replace("<empty>", "-");
	}
	
	public String getProjeto() {
		return projeto;
	}

	public void setProjeto(String projeto) {
		this.projeto = projeto;
	}

	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public String getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getComponente() {
		return componente;
	}

	public void setComponente(String componente) {
		this.componente = componente;
	}

	public void setChamadosJira(List<Assyst> chamadosJira) {
		this.chamadosJira = chamadosJira;
	}

	public Andamento getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Andamento selecionado) {
		this.selecionado = selecionado;
	}

	public List<Tombo> getTombosDetalhe() {
		return tombosDetalhe;
	}

	public void setTombosDetalhe(List<Tombo> tombosDetalhe) {
		this.tombosDetalhe = tombosDetalhe;
	}

	public Andamento getChamadoDetalhe() {
		return chamadoDetalhe;
	}

	public void setChamadoDetalhe(Andamento chamadoDetalhe) {
		this.chamadoDetalhe = chamadoDetalhe;
	}

	public Andamento getUltimoAndamento() {
		return ultimoAndamento;
	}

	public void setUltimoAndamento(Andamento ultimoAndamento) {
		this.ultimoAndamento = ultimoAndamento;
	}

	public List<Andamento> getAndamentosDetalhe() {
		return andamentosDetalhe;
	}

	public void setAndamentosDetalhe(List<Andamento> andamentosDetalhe) {
		this.andamentosDetalhe = andamentosDetalhe;
	}

	public boolean isMostrar() {
		return mostrar;
	}

	public void setMostrar(boolean mostrar) {
		this.mostrar = mostrar;
	}

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public String getProjetoId() {
		return projetoId;
	}

	public void setProjetoId(String projetoId) {
		this.projetoId = projetoId;
	}

	public String getProcesso() {
		return processo;
	}

	public void setProcesso(String processo) {
		this.processo = processo;
	}

	public String getSubsistema() {
		return subsistema;
	}

	public void setSubsistema(String subsistema) {
		this.subsistema = subsistema;
	}

	public ModuloJIRA getModuloJira() {
		return moduloJira;
	}

	public void setModuloJira(ModuloJIRA moduloJira) {
		this.moduloJira = moduloJira;
	}

	public List<ModuloJIRA> getFilteredModulos() {
		return filteredModulos;
	}

	public void setFilteredModulos(List<ModuloJIRA> filteredModulos) {
		this.filteredModulos = filteredModulos;
	}

	public ComponenteJIRA getComponenteJIRA() {
		return componenteJIRA;
	}

	public void setComponenteJIRA(ComponenteJIRA componenteJIRA) {
		this.componenteJIRA = componenteJIRA;
	}

	public List<ComponenteJIRA> getFilteredComponentes() {
		return filteredComponentes;
	}

	public void setFilteredComponentes(List<ComponenteJIRA> filteredComponentes) {
		this.filteredComponentes = filteredComponentes;
	}

	public DeParaJIRA getDeParaJIRA() {
		return deParaJIRA;
	}

	public void setDeParaJIRA(DeParaJIRA deParaJIRA) {
		this.deParaJIRA = deParaJIRA;
	}

	public List<DeParaJIRA> getFilteredDeParaJIRA() {
		return filteredDeParaJIRA;
	}

	public void setFilteredDeParaJIRA(List<DeParaJIRA> filteredDeParaJIRA) {
		this.filteredDeParaJIRA = filteredDeParaJIRA;
	}

	public IssueJIRA getIssueJira() {
		return issueJira;
	}

	public void setIssueJira(IssueJIRA issueJira) {
		this.issueJira = issueJira;
	}

	public boolean isGeraIssue() {
		return geraIssue;
	}

	public void setGeraIssue(boolean geraIssue) {
		this.geraIssue = geraIssue;
	}

	public String getReq() {
		return req;
	}

	public void setReq(String req) {
		this.req = req;
	}	
	
}
