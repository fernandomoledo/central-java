package controller;

import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import util.Mensagens;
import model.Lotacao;
import model.Portal;
import dao.LotacaoDAO;
import dao.PortalDAO;

@ManagedBean
@SessionScoped
public class PortalMB {
	private String username;
	private String termo;
	private Portal p;
	//armazena a lota��o verdadeira do usu�rio, pois se ele for coordenador, pode alterar sua lota��o para ver os chamados
	private Lotacao original = new Lotacao();
	/*
	 * Este m�todo � respons�vel por autenticar o login do usu�rio e criar a sess�o, que ser� controlada pela classe filters.ControleDeAcesso.java
	 */
	public String logar(){
		PortalDAO portalDAO = new PortalDAO();
		p = null;
		try {
			p = portalDAO.getLogin(this.username);
			if(p != null){
				ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
				HttpSession session = (HttpSession) ec.getSession(false);
				session.setAttribute("usuarioLogado", p.getLogin());
				this.setOriginal(p.getLotacao());
				return "/painel.xhtml?faces-redirect=true";
			}else{
				System.out.println("Usu�rio ou senha inv�lidos...");
				Mensagens.setMessage(3, "Usu�rio e/ou senha inv�lidos");
			}
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "Erro no banco de dados UNA: "+e.getMessage());
			e.printStackTrace();
		}
		return null;
		
	}
	
	/*
	 * Este m�todo serve para alterar a lota��o de um usu�rio coordenador que deseje ver chamados de uma das suas se��es
	 */
	public void mudaLotacao(String lotacao) throws ClassNotFoundException, SQLException{
		System.out.println("Nova lota��o: "+lotacao);
		LotacaoDAO dao = new LotacaoDAO();
		Lotacao l = new Lotacao();
		if(lotacao.length() > 5){	
			l = dao.getLotacaoByName(lotacao);
			this.p.setLotacao(l);
		}
	}
	
	/*
	 * Este m�todo destr�i a sess�o e encerra a execu��o do sistema para o usu�rio
	 */
	public String logout(){
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) ec.getSession(false);
		session.removeAttribute("usuarioLogado");
		return "/index.xhtml?faces-redirect=true";
	}

	/*
	 * getters and setters
	 */
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Portal getP() {
		return p;
	}

	public void setP(Portal p) {
		this.p = p;
	}

	public String getTermo() {
		return termo;
	}

	public void setTermo(String termo) {
		this.termo = termo;
	}

	public Lotacao getOriginal() {
		return original;
	}

	public void setOriginal(Lotacao original) {
		this.original = original;
	}
	
	
	
}
