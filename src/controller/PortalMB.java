package controller;

import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import util.Mensagens;
import model.Portal;
import dao.PortalDAO;

@ManagedBean
@SessionScoped
public class PortalMB {
	private String username;
	private String termo;
	private Portal p;
	public String logar(){
		PortalDAO portalDAO = new PortalDAO();
		p = null;
		try {
			p = portalDAO.getLogin(this.username);
			if(p != null){
				ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
				HttpSession session = (HttpSession) ec.getSession(false);
				session.setAttribute("usuarioLogado", p.getLogin());
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
	
	public String logout(){
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) ec.getSession(false);
		session.removeAttribute("usuarioLogado");
		return "/index.xhtml?faces-redirect=true";
	}

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
	
	
	
}
