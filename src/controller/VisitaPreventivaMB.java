package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.naming.NamingException;
import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

import dao.CategoriaDAO;
import dao.JiraDAO;
import dao.LotacaoDAO;
import dao.VisitaPrevDAO;
import model.Atendente;
import model.Categoria;
import model.Exclusao;
import model.Lotacao;
import model.LotacaoVisitaPrev;
import model.Tombo;
import model.Visita;
import util.Mensagens;

@ManagedBean
@ViewScoped
public class VisitaPreventivaMB {
	final static Logger logger = Logger.getLogger(AdminMB.class);
	private String periodo = new SimpleDateFormat("MMMMM/yyyy").format(new Date());
	private LotacaoVisitaPrev lotacaoVP = new LotacaoVisitaPrev();
	private List<LotacaoVisitaPrev> selectedLotVP = new ArrayList<LotacaoVisitaPrev>();
	private List<LotacaoVisitaPrev> filteredLotVP;
	private List<Visita> filteredVisita;
	private List<Atendente> filteredAtendente;
	private Visita visita = new Visita();
	private Atendente atendente = new Atendente();
	private Exclusao exclusao = new Exclusao();
	
	//***PRODUÇÃO ***
	//private String servidor = "https://www.trt15.jus.br/assystREST/v1/events"; 
	//private String oferta = "1221";
	//private String servidor2 = "http://10.15.199.183:8989"; 
	
	//***HOMOLOGAÇÃO ***
	private String servidor = "https://www-hm.trt15.jus.br/assystREST/v1/events";
	private String servidor2 = "http://10.15.228.141:8989";
	
	//prod e hm
	private String credenciais = "assyst:axios";

	private String oferta = "1171";
	private String curlW = "C:\\curl\\curl\\bin\\curl.exe ", curlL = "curl ";
	
	public void redirect(String chamado) throws IOException{
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.redirect("https://centraldetic.trt15.jus.br/assystweb/application.do#eventsearch/EventSearchDelegatingDispatchAction.do?dispatch=monitorInit&ajaxMonitor=false&eventSearchContext&queryProfileForm.columnProfileId=0&queryProfileForm.queryProfileId=0&event.lookup.eventRefRange="+chamado);
	}
	
	public List<Visita> getRelatorio() throws NamingException{
		VisitaPrevDAO dao = new VisitaPrevDAO();
		try{
			if(this.atendente.getId() == 0) {
				Mensagens.setMessage(3, "Escolha um atendente para o relatório!");
				return null;
			}else {
				return dao.listarVisitas(this.atendente.getId());
			}
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas ao listar visitas preventivas: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			return null;
		}
	}
	
	public void salvar() throws ClassNotFoundException, NamingException{
		VisitaPrevDAO dao = new VisitaPrevDAO();
		try{
			if(this.lotacaoVP.getId() > 0){
				if(dao.atualizarLotacaoVP(this.lotacaoVP)){
					Mensagens.setMessage(1, "Lotação de visita preventiva alterada com sucesso.");
					this.lotacaoVP = new LotacaoVisitaPrev();
				}else{
					Mensagens.setMessage(3, "Não foi possível alterar a lotação de visita preventiva:  "+this.lotacaoVP.getLotacao());
					
				}
				
			}else{
				if(dao.salvarLotacaoVP(this.lotacaoVP)){
					Mensagens.setMessage(1, "Lotação de visita preventiva salva com sucesso.");
					this.lotacaoVP = new LotacaoVisitaPrev();
				}else{
					Mensagens.setMessage(3, "Não foi possível salvar a lotação de visita preventiva: "+this.lotacaoVP.getLotacao());
					
				}
			}
			
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas ao salvar a lotação para visita preventiva: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());

		}
	}
	
	public void salvarExclusao() throws ClassNotFoundException, NamingException{
		VisitaPrevDAO dao = new VisitaPrevDAO();
		try{
				this.exclusao.setAtendente(this.atendente);
				this.exclusao.setLotacao(this.lotacaoVP);
				
				if(dao.salvarExclusao(this.exclusao)){
					Mensagens.setMessage(1, "Exclusão de usuário para visita em um determinado gabinete realizada com sucesso.");
					this.lotacaoVP = new LotacaoVisitaPrev();
					this.atendente = new Atendente();
				}else{
					Mensagens.setMessage(3, "Não foi possível excluir o atendente para o gabinete selecionado: "+this.lotacaoVP.getLotacao());
					
				}
			
			
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Não foi possível excluir o atendente para o gabinete selecionado: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());

		}
	}
	
	public void excluirExclusao(int id) throws ClassNotFoundException, NamingException{
		VisitaPrevDAO dao = new VisitaPrevDAO();
		try{
						
				if(dao.excluirExclusao(id)){
					Mensagens.setMessage(1, "Veto ao atendimento removido com sucesso.");
					this.lotacaoVP = new LotacaoVisitaPrev();
				}else{
					Mensagens.setMessage(3, "Não foi possível excluir o veto ao atendimento selecionado: "+this.lotacaoVP.getLotacao());
					
				}
			
			
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Não foi possível excluir o veto ao atendimento selecionado: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());

		}
	}
	
	
	public void salvarAtendente() throws ClassNotFoundException, NamingException{
		VisitaPrevDAO dao = new VisitaPrevDAO();
		try{
			if(this.atendente.getId() > 0){
				if(dao.atualizarAtendente(this.atendente)){
					Mensagens.setMessage(1, "Cadastro do atendente alterado com sucesso.");
					this.atendente = new Atendente();
				}else{
					Mensagens.setMessage(3, "Não foi possível alterar o cadastro do atendente:  "+this.lotacaoVP.getLotacao());
					
				}
				
			}else{
				if(dao.salvarAtendente(this.atendente)){
					Mensagens.setMessage(1, "Atendente salvo com sucesso.");
					this.atendente = new Atendente();
				}else{
					Mensagens.setMessage(3, "Não foi possível realizar o cadastro do atendente: "+this.lotacaoVP.getLotacao());
					
				}
			}
			
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas no cadastro do atendente: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());

		}
	}
	
	
	public void buscarLotacao(int id){
		VisitaPrevDAO dao = new VisitaPrevDAO();
		try{
			this.lotacaoVP = dao.getLotacaoVP(id);
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
		}
	}
	
	public void excluir(LotacaoVisitaPrev l) throws ClassNotFoundException, NamingException{
		VisitaPrevDAO dao = new VisitaPrevDAO();
		try {
			if(dao.excluirLotacaoVP(l.getId())){
				Mensagens.setMessage(1, "Lotação '"+ l.getLotacao() + "' inativada com sucesso.");
				
			}else{
				Mensagens.setMessage(3, "Não foi possível inativar a lotação '" + l.getLotacao() + "'.");
			}
		} catch (SQLException e) {
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			Mensagens.setMessage(3, "Não foi possível inativar a lotação '" + l.getLotacao() + "'.");
		}
	}
	
	public void buscarAtendente(int id){
		VisitaPrevDAO dao = new VisitaPrevDAO();
		try{
			this.atendente = dao.buscaAtendente(id);
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
		}
	}
	
	public void excluirAtendente(int id) throws ClassNotFoundException, NamingException{
		VisitaPrevDAO dao = new VisitaPrevDAO();
		try {
			if(dao.excluirAtendente(id)){
				Mensagens.setMessage(1, "Atendente excluído com sucesso.");
				
			}else{
				Mensagens.setMessage(3, "Não foi possível excluir o atendente.");
			}
		} catch (SQLException e) {
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			Mensagens.setMessage(3, "Não foi possível excluir o atendente: " + e.getMessage());
		}
	}
	
	public void verificaSelecao() throws ClassNotFoundException, NamingException, SQLException, IOException, InterruptedException {
		int counter = 0;
		System.out.println("count Lotações: " + this.selectedLotVP.size());
		if(this.selectedLotVP.size() == 0) {
			Mensagens.setMessage(3, "Ao menos uma lotação deve ser selecionada e confirmada para ser visitada pelo atendente.");
		}else {
			for(LotacaoVisitaPrev l : this.selectedLotVP) {
				VisitaPrevDAO dao = new VisitaPrevDAO();
				this.visita.setPeriodo(this.periodo);
				this.atendente = dao.buscaAtendente(this.atendente.getId());
				this.visita.setAtendente(this.atendente);
				this.visita.setLotacao(l);
				try {
					String url = servidor;
				    URL obj = new URL(url);
				    HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
			
				    conn.setRequestProperty("Content-Type", "application/xml");
				    conn.setDoOutput(true);
			
				    conn.setRequestMethod("POST");
				  
				    String userpass = credenciais;
				    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
				    conn.setRequestProperty ("Authorization", basicAuth);
			
				    String data = "<event>";
				    data += "<serviceOfferingId>" + oferta + "</serviceOfferingId>";
				    data += "<remarks>Visita preventiva - " + periodo + "- " + l.getLotacao() + " - " + l.getAndar() + "o Andar (" + l.getPredio() + ")</remarks>";
				    data += "<shortDescription>Visita preventiva - " + periodo + "- " + l.getLotacao() + " - " + l.getAndar() + "o Andar (" + l.getPredio() + ")</shortDescription>";
				    data += "<affectedUserId>" + this.atendente.getAffectedUserId() + "</affectedUserId>";
				    data += "</event>";
				    
				    System.out.println(removerAcentos(data));
				    
				    if(System.getProperty("os.name").toLowerCase().equals("windows 7"))
				    	data = removerAcentos(data);
				    
				    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			
				    out.write(data);
				    out.close();
			        
			        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
			
			        int numCharsRead;
			        char[] charArray = new char[1024];
			        StringBuffer sb = new StringBuffer();
			        //while ((numCharsRead = isr.read(charArray)) > 0) {
			        while ((numCharsRead = reader.read(charArray)) > 0) {
			            sb.append(charArray, 0, numCharsRead);
			        }
			        String result = sb.toString();
			
			        String ch = result.substring(result.indexOf("<formattedReference>")+20, result.indexOf("</formattedReference>"));
			        this.visita.setChamado(ch);
			        
			        Long id = Long.parseLong(ch.substring(1)) + 5000000;
			        
			    
				        Process p;
				        Runtime rt = Runtime.getRuntime();
				        if(System.getProperty("os.name").toLowerCase().equals("windows 7"))
				        	p = rt.exec(curlW + " -D- -X  GET " + servidor2 + "/assyst/assystEJB/Action/new?eventId="+id+"&actionTypeId=1&assignedServDeptId=42&assignedUserId="+this.atendente.getAssignedUserId());
				        else
				        	p = rt.exec(curlL +" -D- -X  GET "  + servidor2 + "/assyst/assystEJB/Action/new?eventId="+id+"&actionTypeId=1&assignedServDeptId=42&assignedUserId="+this.atendente.getAssignedUserId());
						p.waitFor(5,TimeUnit.SECONDS);
			        
					
					if(dao.salvarVP(this.visita)){
						counter++;
											
					}
					
				} catch (SQLException e) {
					StringWriter stack = new StringWriter();
					e.printStackTrace(new PrintWriter(stack));
					logger.error("ERRO: " + stack.toString());
					Mensagens.setMessage(3, "Não foi possível inativar a lotação '" + l.getLotacao() + "'.");
				}
			}
			if(counter == this.selectedLotVP.size()) {
				Mensagens.setMessage(1, "Visitas de " + this.periodo + " geradas com sucesso para " + this.atendente.getNome() + ".");
				this.atendente = new Atendente();
				this.selectedLotVP = new ArrayList<LotacaoVisitaPrev>();
			}
			else {
				Mensagens.setMessage(3, "Erro ao gerar as visitas. As operações realizadas serão desfeitas.");
			}
		}
	}
	
	public static String removerAcentos(String str) {
	    return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}
	
	public List<LotacaoVisitaPrev> getLotacoesVP() throws NamingException{
		VisitaPrevDAO dao = new VisitaPrevDAO();
		try{
			return dao.listarLotacoesVP();
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas ao listar lotações de visita preventiva: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			return null;
		}
	}
	
	public List<LotacaoVisitaPrev> getLotacoesVPAtrib() throws NamingException{
		VisitaPrevDAO dao = new VisitaPrevDAO();
		try{
			return dao.listarLotacoesVPAtrib(this.periodo, this.atendente.getId());
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas ao listar lotações de visita preventiva: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			return null;
		}
	}
	
	public List<Atendente> getAtendentes() throws NamingException{
		VisitaPrevDAO dao = new VisitaPrevDAO();
		try{
			return dao.listarAtendentes();
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas ao listar atendentes de visita preventiva: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			return null;
		}
	}
	
	public List<Exclusao> getExclusoes() throws NamingException{
		VisitaPrevDAO dao = new VisitaPrevDAO();
		try{
			return dao.listarExclusoes();
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas ao listar exclusões de visita preventiva: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			return null;
		}
	}
		
	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public LotacaoVisitaPrev getLotacaoVP() {
		return lotacaoVP;
	}

	public void setLotacaoVP(LotacaoVisitaPrev lotacaoVP) {
		this.lotacaoVP = lotacaoVP;
	}

	public List<LotacaoVisitaPrev> getSelectedLotVP() {
		return selectedLotVP;
	}

	public void setSelectedLotVP(List<LotacaoVisitaPrev> filteredLotVP) {
		this.selectedLotVP = filteredLotVP;
	}

	
	public Visita getVisita() {
		return visita;
	}

	public void setVisita(Visita visita) {
		this.visita = visita;
	}

	public Atendente getAtendente() {
		return atendente;
	}

	public void setAtendente(Atendente atendente) {
		this.atendente = atendente;
	}

	public List<LotacaoVisitaPrev> getFilteredLotVP() {
		return filteredLotVP;
	}

	public void setFilteredLotVP(List<LotacaoVisitaPrev> filteredLotVP) {
		this.filteredLotVP = filteredLotVP;
	}

	public List<Visita> getFilteredVisita() {
		return filteredVisita;
	}

	public void setFilteredVisita(List<Visita> filteredVisita) {
		this.filteredVisita = filteredVisita;
	}

	public List<Atendente> getFilteredAtendente() {
		return filteredAtendente;
	}

	public void setFilteredAtendente(List<Atendente> filteredAtendente) {
		this.filteredAtendente = filteredAtendente;
	}

	public Exclusao getExclusao() {
		return exclusao;
	}

	public void setExclusao(Exclusao exclusao) {
		this.exclusao = exclusao;
	}

	
	
	
	
}
