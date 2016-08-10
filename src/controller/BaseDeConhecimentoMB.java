package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import util.Mensagens;
import dao.AndamentoDAO;
import dao.CategoriaDAO;
import dao.ChamadoDAO;
import dao.TomboDAO;
import model.Andamento;
import model.Assunto;
import model.Categoria;
import model.CategoriaPai;
import model.Chamado;
import model.ResultadosWiki;
import model.Tombo;


@ManagedBean
@ViewScoped
public class BaseDeConhecimentoMB {
	private TreeNode root; //armazena os nós do menu árvore
	private TreeNode select; //armazena um nó do menu árvore selecionado
	private boolean mostra = false; //flag para mostrar ou não a tabela listando os chamados encontrados
	private boolean mostrawiki = false; //flag para mostrar ou não a tabela listando os chamados encontrados
	private boolean mostraDetalhe = false; //flag para mostrar ou não o detalhe de um chamado selecionado
	private List<Chamado> chamados = new ArrayList<Chamado>();
	private List<Chamado> chamadosFiltrados;
	private Andamento chamadoDetalhe = new Andamento();
	private List<Tombo> tombosDetalhe = new ArrayList<Tombo>();
	private List<Andamento> andamentosDetalhe = new ArrayList<Andamento>();
	private Andamento ultimoAndamento = new Andamento();
	private Chamado selecionado; //armazena o chamado selecionado
	private String termoBusca = ""; //armazena o termo de busca digitado na busca geral
	private String termoDestaque = ""; //armazena o termo de busca com a marcação <mark></mark>
	private String termoTroca = ""; //armazena o termo que substituirá a busca, caso haja termo adicional cadastrado no banco
	private String termo="";
	private String[] termos;
	private List<ResultadosWiki> wiki;
	private String statusWiki;
	final static Logger logger = Logger.getLogger(BaseDeConhecimentoMB.class);
	
	/*
	 * O método construtor inicializa a árvore e verifica se há termo de busca para disparar a pesquisa
	 */
	public BaseDeConhecimentoMB() throws NamingException, IOException, ParseException, ParserConfigurationException, SAXException{
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
		if(!params.isEmpty()){
			if(params.get("filtro").equals("OU")){
				this.termoBusca = params.get("termo").trim().replace(" ", " | ");
			}else if(params.get("filtro").equals("E")){
				this.termoBusca = params.get("termo").trim().replace(" ", " & ");
			}else{
				this.termoBusca = params.get("termo").trim().replace("||", " & ");
			}
			
			this.termo = this.termoBusca;
			this.termoDestaque = this.termoBusca;
			this.termoTroca = "<mark>"+this.termoDestaque+"</mark>";
			this.termoBusca = this.termoBusca.replace("_", " ");
			this.termoBusca = this.termoBusca.replace("-", " ");
			/*
			this.termoBusca = this.termoBusca.replace("@", "\\@");
			this.termoBusca = this.termoBusca.replace("#", "\\#");
			this.termoBusca = this.termoBusca.replace("&", "\\&");
			this.termoBusca = this.termoBusca.replace("%", "\\%");
			this.termoBusca = this.termoBusca.replace("*", "\\*");
			*/
			this.termoBusca = this.termoBusca.replace("$$", "%");

			buscar();
		}
		System.out.println(params.toString());
		 
		 root = new DefaultTreeNode("Root", null);
		 montaNode(0, root);
		 
	}
	
	/*
	 * Este método recursivo constrói o menu árvore
	 */
	public TreeNode montaNode(int pai, TreeNode noPai) throws NamingException{
		List<CategoriaPai> cp = new ArrayList<CategoriaPai>();
		TreeNode node = null;
		 CategoriaDAO dao = new CategoriaDAO();
		 try {
			cp = dao.listarCategorias(pai);
		 }catch (ClassNotFoundException | SQLException e) {
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				logger.error("ERRO: " + stack.toString());
		 }
		 
		 for(int i = 0; i < cp.size(); i++){
			 node = new DefaultTreeNode(cp.get(i).getFilha().getNomeCategoria(), noPai);
			 montaNode(cp.get(i).getFilha().getIdCategoria(), node);
			 
		 }
		 return node;
	}

	/*
	 * Este método captura o nó da árvore selecionado e verifica se ele é pai ou não. Caso não seja, dispara a busca de chamados
	 */
	public void getSelecao() throws ClassNotFoundException, SQLException, NamingException{
		chamados = new ArrayList<Chamado>();
		chamadosFiltrados = null;
		selecionado = null;
		this.mostraDetalhe = false;
		this.termoBusca = "";
		if(this.chamados.size() == 0) this.mostra = false;
		if(this.select.getChildCount() == 0){
			getListaChamados();
		}
	}
	
	/*
	 * Este método retorna uma lista de todos os chamados encontrados com base na categoria selecionada na árvore
	 */
	public void getListaChamados() throws ClassNotFoundException, SQLException, NamingException{
		String equipamento;
		ChamadoDAO dao = new ChamadoDAO();
		CategoriaDAO cDao = new CategoriaDAO();
		Categoria c = new Categoria();
		System.out.println(this.select.getParent() + " - " + this.select.getParent().getParent() + " - " + this.select.getParent().getParent().getParent());
		if(this.select.getParent().toString().equals("Software") || this.select.getParent().getParent().toString().equals("Software") ||
				this.select.getParent().getParent().getParent().toString().equals("Software")){
			termo = this.select.toString();
			this.termoDestaque = termo;
			c = cDao.getCategoriaByName(termo);
			termo = c.getTermoCategoria().equals("") ? c.getNomeCategoria() : c.getTermoCategoria();
			this.chamados = dao.getChamadosSemTombo(termo);
			this.termoTroca = "<mark>"+this.termoDestaque+"</mark>";
			System.out.println(termo + " - " + termoTroca + this.chamados.size());
		}else{
			termo = this.select.getParent().toString();
			equipamento = this.select.toString();
			this.termoDestaque = termo;
			c = cDao.getCategoriaByName(termo);
			termo = c.getTermoCategoria().equals("") ? c.getNomeCategoria() : c.getTermoCategoria();
			c = cDao.getCategoriaByName(equipamento);
			equipamento = c.getTermoCategoria().equals("") ? c.getNomeCategoria() : c.getTermoCategoria();
			this.chamados = dao.getChamadosComTombo(termo, equipamento);
			this.termoTroca = "<mark>"+this.termoDestaque+"</mark>";
		}
		
		if(this.chamados.size() > 0) this.mostra = true;
		wiki = new ArrayList<ResultadosWiki>();
		if(this.wiki.size() > 0) this.setMostrawiki(true);
		else this.setMostrawiki(false);
	}
	
	/*
	 * Este método carrega todas as informações do chamado selecionado para exibí-lo na tela
	 */
	public void getDetalhe() throws NamingException{
		long id = this.selecionado.getId();
		ChamadoDAO cDao = new ChamadoDAO();
		TomboDAO tDao = new TomboDAO();
		AndamentoDAO aDao = new AndamentoDAO();
		try {
			this.chamadoDetalhe = cDao.getInfoChamado(id);
			termos = termo.split("(\\|)|(\\&)");
			for(int i = 0; i < termos.length; i++){
				System.out.println("Termo "+(i+1)+": "+termos[i].trim());
				Assunto a = new Assunto();
				a.setDescricao(chamadoDetalhe.getChamado().getAssunto().getDescricao().replaceAll("(?i)"+termos[i].trim(), "<mark>"+termos[i].trim()+"</mark>"));
				chamadoDetalhe.getChamado().setAssunto(a);
				chamadoDetalhe.setTexto(chamadoDetalhe.getTexto().replaceAll("(?i)"+termos[i].trim(), "<mark>"+termos[i].trim()+"</mark>"));
				chamadoDetalhe.getChamado().getLotacaoSolicitante().setNome(chamadoDetalhe.getChamado().getLotacaoSolicitante().getNome().toUpperCase().replace(termos[i].toUpperCase().trim(), "<mark>"+termos[i].toUpperCase().trim()+"</mark>"));
			}
			Assunto a = new Assunto();
			a.setDescricao(chamadoDetalhe.getChamado().getAssunto().getDescricao().replaceAll("(?i)"+this.termoDestaque.trim(), "<mark>"+this.termoDestaque.trim()+"</mark>"));
			chamadoDetalhe.getChamado().setAssunto(a);
			chamadoDetalhe.setTexto(chamadoDetalhe.getTexto().replaceAll("(?i)"+this.termoDestaque.trim(), "<mark>"+this.termoDestaque.trim()+"</mark>"));
			chamadoDetalhe.getChamado().getLotacaoSolicitante().setNome(chamadoDetalhe.getChamado().getLotacaoSolicitante().getNome().toUpperCase().replace(this.termoDestaque.toUpperCase().trim(), "<mark>"+this.termoDestaque.toUpperCase().trim()+"</mark>"));
			
			this.tombosDetalhe = tDao.getTombosPorChamado(id);
			for(int i = 0; i < tombosDetalhe.size(); i++){
				for(int j = 0; j<termos.length; j++){
					tombosDetalhe.get(i).setDescricao(tombosDetalhe.get(i).getDescricao().toUpperCase().replace(termos[j].toUpperCase().trim(), "<mark>"+termos[j].toUpperCase().trim()+"</mark>"));
				
				}
				tombosDetalhe.get(i).setDescricao(tombosDetalhe.get(i).getDescricao().toUpperCase().replace(termoDestaque.toUpperCase().trim(), "<mark>"+termoDestaque.toUpperCase().trim()+"</mark>"));
			}
			
			this.andamentosDetalhe = aDao.getAndamentosPorChamado(id);
			for(int i = 0; i < andamentosDetalhe.size(); i++){
				for(int j = 0; j < termos.length; j++){
					andamentosDetalhe.get(i).setTexto(andamentosDetalhe.get(i).getTexto().replaceAll("(?i)"+termos[j].trim(), "<mark>"+termos[j].trim()+"</mark>"));
					andamentosDetalhe.get(i).getUsuario().setNome(andamentosDetalhe.get(i).getUsuario().getNome().replaceAll("(?i)"+termos[j].trim(), "<mark>"+termos[j].trim()+"</mark>"));
				
				}
				andamentosDetalhe.get(i).setTexto(andamentosDetalhe.get(i).getTexto().replaceAll("(?i)"+termoDestaque.trim(), "<mark>"+termoDestaque.trim()+"</mark>"));
				andamentosDetalhe.get(i).getUsuario().setNome(andamentosDetalhe.get(i).getUsuario().getNome().replaceAll("(?i)"+termoDestaque.trim(), "<mark>"+termoDestaque.trim()+"</mark>"));
			}
			this.ultimoAndamento = this.andamentosDetalhe.get(this.andamentosDetalhe.size()-1);
			this.mostraDetalhe = true;
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "Não foi possível obter o detalhe do chamado. "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
		} 
	}
	
	/*
	 * Este método é responsável por buscar de forma completa no banco UNA o termo digitado na busca geral do site
	 */
	
	public void buscar() throws NamingException, IOException, ParseException, ParserConfigurationException, SAXException{
		ChamadoDAO dao = new ChamadoDAO();
		String server = "";
		String host = "10.15.199.151";
		
		try {
			this.chamados = dao.getChamadosBuscaGeral(this.termoBusca);
			
			
			String ipAddress = host;
			InetAddress inet = InetAddress.getByName(ipAddress);
			boolean reachable = inet.isReachable(5000);
			if(reachable)
				server = "http://"+host+"/api.php";
			else
				statusWiki = "Host "+host+" inacessível";
			
			
			//Wiki
	
		
			wiki = new ArrayList<ResultadosWiki>();
			
			if(!server.equals("")){
				PrintWriter pw = new PrintWriter("/tmp/retorno.txt");
				pw.print(Charset.defaultCharset().name());
				pw.close();
				String buscawiki = this.termo.replace("&", "+").replace("|", "+").replace(" ", "+");
				Runtime rt = Runtime.getRuntime();
				String s = "";
				String command = "";
				String token = "";
				Process p = null;
		
				if(System.getProperty("os.name").toUpperCase().startsWith("WINDOWS"))
					command = "C:\\curl\\curl\\bin\\curl.exe -c C:\\curl\\cookies.txt -d " + '"'+"lgname=cau_conhecimento&lgpassword=centralcau&action=login&format=xml" + '"'+ " http://10.15.199.151/api.php ";
				else
					command = "curl -c /tmp/cookies.txt -d lgname=cau_conhecimento&lgpassword=centralcau&action=login&format=xml "+server;
				p = rt.exec(command);
				String xml = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while((s = reader.readLine()) != null){
					System.out.println(s);
					xml += s;
				}
				
				System.out.println(xml);
				 DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				    InputSource is = new InputSource();
				    is.setCharacterStream(new StringReader(xml));
				
				    System.out.println(is.getEncoding());
				    Document doc = db.parse(is);
				    NodeList nodes = doc.getElementsByTagName("login");
				  		    
				    for (int i = 0; i < nodes.getLength(); i++) {
				      Element element = (Element) nodes.item(i);
				      
				    
				      System.out.println(element.getAttribute("token"));
				      token = element.getAttribute("token");
				    }
	
				if(System.getProperty("os.name").toUpperCase().startsWith("WINDOWS"))
					command = "C:\\curl\\curl\\bin\\curl.exe -b C:\\curl\\cookies.txt -d " + '"'+"lgname=cau_conhecimento&lgpassword=centralcau&action=login&lgtoken="+ 
					token+"&format=xml" + '"'+ " http://10.15.199.151/api.php ";
				else
					command = "curl -b /tmp/cookies.txt -d lgname=cau_conhecimento&lgpassword=centralcau&action=login&lgtoken="+token+"&format=xml "+server;
				p = rt.exec(command);
				System.out.println(command);
				reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				
				
				if(System.getProperty("os.name").toUpperCase().startsWith("WINDOWS"))
					command = "C:\\curl\\curl\\bin\\curl.exe -b C:\\curl\\cookies.txt -X POST http://10.15.199.151/api.php?action=query&generator=search&gsrsearch="+buscawiki+"&prop=info&inprop=url&format=xml";
				else
					command = "curl -b /tmp/cookies.txt -d action=query&generator=search&gsrsearch="+buscawiki+"&prop=info&inprop=url&format=xml "+server;
				p = rt.exec(command);
				
				/*
				System.out.println(command);
				reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				xml = "";
				while((s = reader.readLine()) != null)
					xml += s;
				System.out.println(xml);
				*/
				 db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				    // is = new InputSource();
				   //  is.setCharacterStream(new StringReader(p.getInputStream().toString()));
	
				     doc = db.parse(p.getInputStream());
				     nodes = doc.getElementsByTagName("page");
				  		    
				    for (int i = 0; i < nodes.getLength(); i++) {
				      Element element = (Element) nodes.item(i);
				      ResultadosWiki rw = new ResultadosWiki();
				    
				      System.out.println(element.getAttribute("title"));
				      rw.setTitulo(element.getAttribute("title"));
				      rw.setUrl(element.getAttribute("fullurl"));
				      rw.setEncontradoEm("Correspondência com o título da página");
				      wiki.add(rw);
				    }
				
				if(System.getProperty("os.name").toUpperCase().startsWith("WINDOWS"))
					command = "C:\\curl\\curl\\bin\\curl.exe -b C:\\curl\\cookies.txt -X POST http://10.15.199.151/api.php?action=query&generator=search&gsrwhat=text&gsrsearch="+buscawiki+"&prop=info&inprop=url&format=xml";
				else
					command = "curl -b /tmp/cookies.txt -d action=query&generator=search&gsrwhat=text&gsrsearch="+buscawiki+"&prop=info&inprop=url&format=xml "+server;
				p = rt.exec(command);
				/*
				System.out.println(command);
				reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				xml = "";
				while((s = reader.readLine()) != null)
					xml += s;
				System.out.println(xml);
				*/
				 db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				    // is = new InputSource();
				   //  is.setCharacterStream(new StringReader(p.getInputStream().toString()));
	
				     doc = db.parse(p.getInputStream());
				     nodes = doc.getElementsByTagName("page");
				  		    
				    for (int i = 0; i < nodes.getLength(); i++) {
				      Element element = (Element) nodes.item(i);
				      ResultadosWiki rw = new ResultadosWiki();
				      rw.setTitulo(element.getAttribute("title"));
				      rw.setUrl(element.getAttribute("fullurl"));
				      rw.setEncontradoEm("Corresponência com o texto da página");
				      wiki.add(rw);
				    }
			}
			    
		} catch (ClassNotFoundException | SQLException e) {
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
		}
		
		if(this.chamados.size() > 0) this.mostra = true;
		if(this.wiki.size() > 0) this.setMostrawiki(true);
		for(Chamado c : this.chamados){
			System.out.println("Chamado "+c.getNumero());
		}
	}
	
	public TreeNode getRoot() {
		return root;
	}

	public TreeNode getSelect() {
		return select;
	}

	public void setSelect(TreeNode select) {
		this.select = select;
	}

	public List<Chamado> getChamados() {
		return chamados;
	}

	public void setChamados(List<Chamado> chamados) {
		this.chamados = chamados;
	}

	public boolean isMostra() {
		return mostra;
	}

	public void setMostra(boolean mostra) {
		this.mostra = mostra;
	}

	public List<Chamado> getChamadosFiltrados() {
		return chamadosFiltrados;
	}

	public void setChamadosFiltrados(List<Chamado> chamadosFiltrados) {
		this.chamadosFiltrados = chamadosFiltrados;
	}

	public Andamento getChamadoDetalhe() {
		return chamadoDetalhe;
	}

	public void setChamadoDetalhe(Andamento chamadoDetalhe) {
		this.chamadoDetalhe = chamadoDetalhe;
	}

	public List<Tombo> getTombosDetalhe() {
		return tombosDetalhe;
	}

	public void setTombosDetalhe(List<Tombo> tombosDetalhe) {
		this.tombosDetalhe = tombosDetalhe;
	}

	public List<Andamento> getAndamentosDetalhe() {
		return andamentosDetalhe;
	}

	public void setAndamentosDetalhe(List<Andamento> andamentosDetalhe) {
		this.andamentosDetalhe = andamentosDetalhe;
	}

	public Andamento getUltimoAndamento() {
		return ultimoAndamento;
	}

	public void setUltimoAndamento(Andamento ultimoAndamento) {
		this.ultimoAndamento = ultimoAndamento;
	}

	public Chamado getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Chamado selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isMostraDetalhe() {
		return mostraDetalhe;
	}

	public void setMostraDetalhe(boolean mostraDetalhe) {
		this.mostraDetalhe = mostraDetalhe;
	}

	public String getTermoBusca() {
		return termoBusca;
	}

	public void setTermoBusca(String termoBusca) {
		this.termoBusca = termoBusca;
	}

	public String getTermoDestaque() {
		return termoDestaque;
	}

	public void setTermoDestaque(String termoDestaque) {
		this.termoDestaque = termoDestaque;
	}

	public String getTermoTroca() {
		return termoTroca;
	}

	public void setTermoTroca(String termoTroca) {
		this.termoTroca = termoTroca;
	}

	public String[] getTermos() {
		return termos;
	}

	public void setTermos(String[] termos) {
		this.termos = termos;
	}

	public boolean isMostrawiki() {
		return mostrawiki;
	}

	public void setMostrawiki(boolean mostrawiki) {
		this.mostrawiki = mostrawiki;
	}

	public List<ResultadosWiki> getWiki() {
		return wiki;
	}

	public void setWiki(List<ResultadosWiki> wiki) {
		this.wiki = wiki;
	}

	public String getStatusWiki() {
		return statusWiki;
	}

	public void setStatusWiki(String statusWiki) {
		this.statusWiki = statusWiki;
	}

	
	
	/*
	public void getDetalhe(long id){
		this.setCategorias(new ArrayList<Categoria>());
		this.setShowDetail(1);
		CategoriaDAO daoCat= new CategoriaDAO();
		try {
			if(!daoCat.ehPai(this.categoriasBreadcrumb.get(this.categoriasBreadcrumb.size()-1).getIdCategoria())){
				this.ehPai = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ChamadoDAO cDao = new ChamadoDAO();
		TomboDAO tDao = new TomboDAO();
		AndamentoDAO aDao = new AndamentoDAO();
		try {
			this.chamadoDetalhe = cDao.getInfoChamado(id);
			this.tombosDetalhe = tDao.getTombosPorChamado(id);
			this.andamentosDetalhe = aDao.getAndamentosPorChamado(id);
			this.ultimoAndamento = this.andamentosDetalhe.get(this.andamentosDetalhe.size()-1);
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "N�o foi poss�vel obter o detalhe do chamado. "+e.getMessage());
			e.printStackTrace();
		} 
	}
	
	*/
	
	
}
