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
	//armazena a lotação verdadeira do usuário, pois se ele for coordenador, pode alterar sua lotação para ver os chamados
	private Lotacao original = new Lotacao();
	/*
	 * Este método é responsável por autenticar o login do usuário e criar a sessão, que será controlada pela classe filters.ControleDeAcesso.java
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
				System.out.println("Usuário ou senha inválidos...");
				Mensagens.setMessage(3, "Usuário e/ou senha inválidos");
			}
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "Erro no banco de dados UNA: "+e.getMessage());
			e.printStackTrace();
		}
		return null;
		
	}
	
	/*
	 * Este método serve para alterar a lotação de um usuário coordenador que deseje ver chamados de uma das suas seções
	 */
	public void mudaLotacao(String lotacao) throws ClassNotFoundException, SQLException{
		System.out.println("Nova lotação: "+lotacao);
		LotacaoDAO dao = new LotacaoDAO();
		Lotacao l = new Lotacao();
		if(lotacao.length() > 5){	
			l = dao.getLotacaoByName(lotacao);
			this.p.setLotacao(l);
		}
	}
	
	/*
	 * Este método destrói a sessão e encerra a execução do sistema para o usuário
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
