package controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import dao.AtendimentoDAO;
import dao.SistemaDAO;
import dao.VisitaPrevDAO;
import model.Atendente;
import model.Atendimento;
import model.LotacaoVisitaPrev;
import model.Sistema;
import model.Visita;
import util.Mensagens;
import util.Utilidades;

@ManagedBean
@ViewScoped
public class AtendimentoMB {
	final static Logger logger = Logger.getLogger(AtendimentoMB.class);
	@SuppressWarnings("unused")
	private List<Atendimento> atendimentos = new ArrayList<Atendimento>();
	private List<Atendimento> filteredAtendimentos;
	private Atendimento atendimento = new Atendimento();
	private Sistema sistema = new Sistema();
	private Atendente atendente = new Atendente();
	private int id;
	private String linkAssyst = "http://www.trt15.jus.br/assystweb/application.do#eventsearch/EventSearchDelegatingDispatchAction.do?dispatch=monitorInit&ajaxMonitor=false&eventSearchContext&queryProfileForm.columnProfileId=0&queryProfileForm.queryProfileId=0";

	public AtendimentoMB() {
		this.atendimento.setDtCadastro(this.getData());
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
		if(!params.isEmpty()){
			id = Integer.parseInt(params.get("id"));
			buscar(id);
		}
	}
	
	public String redirect() {
		return "faq.jsf&faces-redirect=true"; 
	}
	
	public String formataTickets(String tickets) {
		return Utilidades.formataTickets(tickets);
	}
	
	public String getData() {
		return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
	}
	public void salvar() throws ClassNotFoundException, NamingException, SQLException{
		AtendimentoDAO dao = new AtendimentoDAO();
		SistemaDAO daoSist = new SistemaDAO();
		VisitaPrevDAO daoVP = new VisitaPrevDAO();
		
		//this.sistema = daoSist.buscarPorNome(this.sistema.getNome());
		//this.atendente = daoVP.buscaAtendente(this.atendente.getNome());
		this.atendimento.setSistema(this.sistema);
		this.atendimento.setAtendente(this.atendente);
		try{
			if(this.atendimento.getId() > 0){
				if(dao.atualizar(this.atendimento)){
					Mensagens.setMessage(1, "Atendimento alterado com sucesso.");
				}else{
					Mensagens.setMessage(3, "Não foi possível alterar o atendimento do chamado:  "+this.atendimento.getChamado());
					
				}
				
			}else{
				if(dao.salvar(this.atendimento)){
					Mensagens.setMessage(1, "Atendimento registrado com sucesso.");
					this.atendimento = new Atendimento();
					this.atendente = new Atendente();
					this.sistema = new Sistema();
					this.atendimento.setDtCadastro(getData());
				}else{
					Mensagens.setMessage(3, "Não foi possível salvar o atendimento do chamado: "+this.atendimento.getChamado());
					
				}
			}
			
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas ao salvar o atendimento: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());

		}
	}

	public void buscar(int id){
		AtendimentoDAO dao = new AtendimentoDAO();
		try{
			Atendimento a = new Atendimento();
			a = dao.buscar(id);
			this.atendimento = a;
			this.atendente = a.getAtendente();
			this.sistema = a.getSistema();
			
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
		}
	}
	
	public void excluir(Atendimento a) throws ClassNotFoundException, NamingException{
		AtendimentoDAO dao = new AtendimentoDAO();
		try {
			if(dao.excluir(a)){
				Mensagens.setMessage(1, "Atendimento do ticket  '"+ a.getChamado() + "' excluído com sucesso.");
				
			}else{
				Mensagens.setMessage(3, "Não foi possível excluir o atendimento do ticket '" + a.getChamado() + "'.");
			}
		} catch (SQLException e) {
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			Mensagens.setMessage(3, "Não foi possível excluir o atendimento do ticket '" + a.getChamado() + "'.");
		}
	}
	
	
	public List<Atendimento> getAtendimentos() throws NamingException{
		AtendimentoDAO dao = new AtendimentoDAO();
		try{
			return dao.listar();
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas ao listar sistemas: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			return null;
		}
	}
	
	public List<String> completeSistemas(String query) throws ClassNotFoundException, SQLException, NamingException{
		SistemaDAO dao = new SistemaDAO();
		List<Sistema> sistemas = dao.listarSistemas(1);
		List<String> sistemasEncontrados = new ArrayList<String>();
		for(int i=0; i < sistemas.size(); i++) {
			Sistema s = sistemas.get(i);
			if(s.getNome().toLowerCase().contains(query)) {
				sistemasEncontrados.add(s.getNome());
			}
		}
		return sistemasEncontrados;
	}
	
	public List<String> completeAtend(String query) throws ClassNotFoundException, SQLException, NamingException{
		VisitaPrevDAO dao = new VisitaPrevDAO();
		List<Atendente> atendentes = dao.listarTodosAtendentes();
		List<String> atendEncontrados = new ArrayList<String>();
		for(int i=0; i < atendentes.size(); i++) {
			Atendente a = atendentes.get(i);
			if(a.getNome().toLowerCase().startsWith(query)) {
				atendEncontrados.add(a.getNome());
			}
		}
		return atendEncontrados;
	}
	
	public List<String> completeSolucao(String query) throws ClassNotFoundException, SQLException, NamingException{
		List<String> solucoes = new ArrayList<String>();
		solucoes.add("Repassado CDS");
		solucoes.add("Repassado CITIC");
		solucoes.add("Repassado Gabinete SETIC");
		solucoes.add("Repassado SA");
		solucoes.add("Repassado Zuchini");
		solucoes.add("Resolvido SA");
		solucoes.add("Resolvido SAE");
		List<String> solucoesEncontradas = new ArrayList<String>();
		for(int i=0; i < solucoes.size(); i++) {
			if(solucoes.get(i).toLowerCase().contains(query)) {
				solucoesEncontradas.add(solucoes.get(i));
			}
		}
		return solucoesEncontradas;
	}
	
	public void setAtendimentos(List<Atendimento> atendimentos) {
		this.atendimentos = atendimentos;
	}

	public Atendimento getAtendimento() {
		return atendimento;
	}


	public void setAtendimento(Atendimento atendimento) {
		this.atendimento = atendimento;
	}

	public Sistema getSistema() {
		return sistema;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}

	public Atendente getAtendente() {
		return atendente;
	}

	public void setAtendente(Atendente atendente) {
		this.atendente = atendente;
	}

	public List<Atendimento> getFilteredAtendimentos() {
		return filteredAtendimentos;
	}

	public void setFilteredAtendimentos(List<Atendimento> filteredAtendimentos) {
		this.filteredAtendimentos = filteredAtendimentos;
	}

	public String getLinkAssyst() {
		return linkAssyst;
	}

	public void setLinkAssyst(String linkAssyst) {
		this.linkAssyst = linkAssyst;
	}
	
	
	
}
