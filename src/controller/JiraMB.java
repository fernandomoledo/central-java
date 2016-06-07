package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import dao.AndamentoDAO;
import dao.ChamadoDAO;
import dao.JiraDAO;
import dao.TomboDAO;
import model.Andamento;
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
public class JiraMB {
	private List<Andamento> chamadosJira = this.listaChamados();
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
	private IssueJIRA issue = new IssueJIRA();
	private boolean geraIssue = true;

	final static Logger logger = Logger.getLogger(JiraMB.class);
	
	public JiraMB(){
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
		if(!params.isEmpty()){
			this.preparaIssue();
		}
	}
	
	public List<Andamento> listaChamados(){
		AndamentoDAO dao = new AndamentoDAO();
		try{
			this.chamadosJira = dao.getChamadosJIRA();
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			Mensagens.setMessage(3, "Houve um problema ao listar os chamados ref. ao NÃºcleo PJE");
		}
		return this.chamadosJira;
	}
	
	public List<Andamento> getChamadosJira() {
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
	
	public void preparaIssue(){
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
		long id = 0L;
		if(!params.isEmpty()){
			id = Long.parseLong(params.get("idChamado"));
		}
		AndamentoDAO dao = new AndamentoDAO();
		Andamento andamento = new Andamento();

		try{
			andamento = dao.getChamadoJIRA(id);
			String texto = andamento.getTexto();
			
			String[] linhas = andamento.getTexto().split(System.getProperty("line.separator"));

			if(linhas[0].toUpperCase().contains("PJE-JT")){
				issue.setId("10311");
				issue.setNome("PJE-JT");
			}
			if(linhas[4].substring(18).trim().equals("Incidente"))
				issue.setTipoErro(linhas[4].substring(18).trim());
			 issue.setResumo(linhas[2].substring(7).trim());
			 
			 ambiente = linhas[8].substring(10).trim();
			 
			 issue.setVersao(linhas[6].substring(16).trim() + " - " + ambiente.substring(0,ambiente.indexOf("-")-1).trim());
			 
			 issue.setAmbiente(ambiente);
			 
			 issue.setSubsistema(ambiente.substring(ambiente.indexOf("-")+1).trim());
			 
			 issue.setServidor(linhas[10].substring(8).trim());
			 
			 issue.setDescricao(texto.substring(texto.indexOf("Descrição:")+10,texto.indexOf("Prioridade:")).trim().replace("\"", "'"));
			 
			 issue.setProcesso(linhas[2].substring(linhas[2].indexOf("00")-1).trim());
			 
			 String data = new SimpleDateFormat("yyyy").format(andamento.getDtAndamento());
			 issue.setChamado(andamento.getChamado().getNumero()+ "/" + data);
			 
			 JiraDAO daoJira = new JiraDAO();
			 
			 for(DeParaJIRA dp : daoJira.listarDePara()){
				if(linhas[2].contains(dp.getPalavraChave())){
					issue.setModulo(String.valueOf(dp.getModulo().getValue()));
					issue.setComponente(dp.getComponente().getComponente());
				}
			 }
			 System.out.println("Módulo - "+issue.getModulo()+" / " + issue.getComponente());
		}catch(Exception e){
			Mensagens.setMessage(3, "Não foi possível preparar o chamado para criação de issue. "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
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

		    String data = "{\"fields\":{\"project\": {\"id\":\""+this.issue.getId()+"\"}, \"summary\":\""+this.issue.getResumo()+
				"\", \"issuetype\":{\"id\": \"59\"}, \"versions\":[{\"name\":\""+this.issue.getVersao()+"\"}], \"environment\":\""+this.issue.getAmbiente()+
				"\", \"description\":\""+this.issue.getDescricao().trim()+"\",\"components\":[{\"name\":\""+this.issue.getComponente()+"\"}],\"customfield_11741\":{\"value\":\""+this.issue.getSubsistema()+
				"\"}, \"customfield_12243\":{\"id\":\""+this.issue.getModulo()+"\",\"value\":\""+this.issue.getModulo()+"\"},\"customfield_11441\":\""+this.issue.getServidor()+
				"\", \"customfield_12241\":\""+this.issue.getChamado()+"\",\"customfield_11542\":\""+this.issue.getProcesso()+"\"}}";
		    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		    out.write(data.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		    out.close();
		    System.out.println(data.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
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
    		Mensagens.setMessage(1, "Criada a Issue: https://pje.csjt.jus.br/jira/browse/"+ret.getKey());
    		this.geraIssue = false;
		 } catch (Exception e) {
			 Mensagens.setMessage(3, "Erro ao criar a issue para o chamado "+issue.getChamado()+": "+e.getMessage());
			 StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				logger.error("ERRO: " + stack.toString());
		 }
	
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

	public void setChamadosJira(List<Andamento> chamadosJira) {
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

	public IssueJIRA getIssue() {
		return issue;
	}

	public void setIssue(IssueJIRA issue) {
		this.issue = issue;
	}

	public boolean isGeraIssue() {
		return geraIssue;
	}

	public void setGeraIssue(boolean geraIssue) {
		this.geraIssue = geraIssue;
	}
	
	
}
