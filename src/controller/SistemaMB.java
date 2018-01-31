package controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import dao.SistemaDAO;
import dao.VisitaPrevDAO;
import model.LotacaoVisitaPrev;
import model.Sistema;
import model.Visita;
import util.Mensagens;

@ManagedBean
@ViewScoped
public class SistemaMB {
	final static Logger logger = Logger.getLogger(SistemaMB.class);
	private Sistema sistema = new Sistema();
	@SuppressWarnings("unused")
	private List<Sistema> sistemas = new ArrayList<Sistema>();
	private List<Sistema> filteredSistema;
	
	public void salvar() throws ClassNotFoundException, NamingException{
		SistemaDAO dao = new SistemaDAO();
		try{
			if(this.sistema.getId() > 0){
				if(dao.atualizar(this.sistema)){
					Mensagens.setMessage(1, "Sistema alterado com sucesso.");
					this.sistema = new Sistema();
				}else{
					Mensagens.setMessage(3, "Não foi possível alteraro nome do sistema:  "+this.sistema.getNome());
					
				}
				
			}else{
				if(dao.salvar(this.sistema)){
					Mensagens.setMessage(1, "Sistema salvo com sucesso.");
					this.sistema = new Sistema();
				}else{
					Mensagens.setMessage(3, "Não foi possível salvar o sistema: "+this.sistema.getNome());
					
				}
			}
			
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas ao salvar a lotação para visita preventiva: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());

		}
	}
	
	public void excluir(Sistema s) throws ClassNotFoundException, NamingException{
		SistemaDAO dao = new SistemaDAO();
		try {
			if(dao.excluir(s)){
				Mensagens.setMessage(1, "Sistema '"+ s.getNome() + "' inativado com sucesso.");
				
			}else{
				Mensagens.setMessage(3, "Não foi possível inativar o sistema '" + s.getNome() + "'.");
			}
		} catch (SQLException e) {
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			Mensagens.setMessage(3, "Não foi possível inativar o sistema '" + s.getNome() + "'.");
		}
	}
	
	public void buscar(int id){
		SistemaDAO dao = new SistemaDAO();
		try{
			this.sistema = dao.buscar(id);
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
		}
	}

	public Sistema getSistema() {
		return sistema;
	}
	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}
	
	//flag = 0 ativos e inativos / 1 - só ativos
	public List<Sistema> getSistemas() throws NamingException{
		SistemaDAO dao = new SistemaDAO();
		
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String uri = request.getRequestURI();
		
		try{
			if(uri.endsWith("faq.jsf"))
				return dao.listarSistemas(0);
			else
				return dao.listarSistemas(1);
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas ao listar sistemas: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			return null;
		}
	}
	public void setSistemas(List<Sistema> sistemas) {
		this.sistemas = sistemas;
	}

	public List<Sistema> getFilteredSistema() {
		return filteredSistema;
	}

	public void setFilteredSistema(List<Sistema> filteredSistema) {
		this.filteredSistema = filteredSistema;
	}
	
	
}
