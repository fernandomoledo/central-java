package controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import util.ConexaoOracle;
import util.Mensagens;
import model.Lotacao;
import model.Portal;
import dao.LotacaoDAO;
import dao.PortalDAO;

@ManagedBean
@SessionScoped
public class PortalMB {
	private String username;
	private String senha;
	private String confSenha;
	private String senhaAtual;
	private String termo;
	private Portal p;
	final static Logger logger = Logger.getLogger(PortalMB.class);
	//armazena a lotação verdadeira do usuário, pois se ele for coordenador, pode alterar sua lotação para ver os chamados
	private Lotacao original = new Lotacao();
	private String url = "";
	
	public PortalMB(){
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
		if(!params.isEmpty()){
			url = params.get("returnUrl");
		}
	}
	/*
	 * Este método é responsável por autenticar o login do usuário e criar a sessão, que será controlada pela classe filters.ControleDeAcesso.java
	 */
	public String logar() throws NamingException{
		PortalDAO portalDAO = new PortalDAO();
		p = null;
		try {
			p = portalDAO.getLogin(this.username);
			if(p != null){
				if(portalDAO.verificaUsuarioMySQL(this.username, this.senha)){
					ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
					HttpSession session = (HttpSession) ec.getSession(false);
					session.setAttribute("usuarioLogado", p.getLogin());
					session.setAttribute("secao", p.getLotacao().getId());
					session.setAttribute("codUsuario", p.getId());
					this.setOriginal(p.getLotacao());
					this.senha = "";
					this.confSenha = "";
					this.senhaAtual = "";
					if(!url.equals(""))
						return url+"?faces-redirect=true";
					return "/painel.xhtml?faces-redirect=true";
				}else{
					System.out.println("Usuário ou senha inválidos...");
					Mensagens.setMessage(3, "Usuário e/ou senha inválidos");
				}
			}else{
				System.out.println("Usuário ou senha inválidos...");
				Mensagens.setMessage(3, "Usuário e/ou senha inválidos");
			}
		} catch (ClassNotFoundException | SQLException e) {
			
			Mensagens.setMessage(3, "Erro no banco de dados UNA: "+e.getMessage());
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
		}
		return null;
		
	}
	
	/*
	 * Este método serve para alterar a lotação de um usuário coordenador que deseje ver chamados de uma das suas seções
	 */
	public void mudaLotacao(String lotacao) throws ClassNotFoundException, SQLException, NamingException{
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
		this.url = "";
		session.removeAttribute("usuarioLogado");
		session.removeAttribute("secao");
		return "/index.xhtml?faces-redirect=true";
	}

	public String primeiroAcesso() throws NamingException{
		PortalDAO pdao = new PortalDAO();
		try {
			if(pdao.getLogin(this.username) != null){
				if(pdao.verificaUsuarioMySQL(this.username)){
					Mensagens.setMessage(3, "O usuário "+this.username+" já está cadastrado.");
					return null;
				}
				if(this.senha.equals(this.confSenha)){
					if(pdao.insereUsuarioMySQL(this.username, this.senha)){
						Mensagens.setMessage(1, "Usuário ativado com sucesso!");
						return "primeiroacesso";
					}else{
						Mensagens.setMessage(3, "Erro ao ativar usuário. Favor tentar novamente.");
						return null;
					}
				}else{
					Mensagens.setMessage(3, "- As senhas não conferem");
					return null;
				}
				
			}else{
				Mensagens.setMessage(3, "Usuário inexistente na Extranet");
				return null;
			}
		} catch (ClassNotFoundException | SQLException e) {
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			Mensagens.setMessage(3, "Erro no banco de dados. "+e.getMessage());
			return "index.jsf";
		}
	}
	
	public void alterarDados(){
		PortalDAO dao = new PortalDAO();
		try{
			if(dao.verificaUsuarioMySQL(this.username, this.senhaAtual)){
				if(dao.atualizaUsuarioMySQL(this.username, this.senha)){
					Mensagens.setMessage(1, "Senha alterada com sucesso!!!");
					this.senha = "";
					this.senhaAtual = "";
					this.confSenha = "";
				}else{
					Mensagens.setMessage(3, "Erro ao alterar a senha. Tente novamente!");
				}
			}else{
				Mensagens.setMessage(3, "A senha atual está incorreta!");
			}
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			Mensagens.setMessage(3, "Erro ao alterar a senha: "+e.getMessage());
		}
	}
	
	
	public void isAdm(ComponentSystemEvent event){
		
		FacesContext fc = FacesContext.getCurrentInstance();
		
		if (!(this.username.equals("ersilva") || this.username.equals("terciolopes") || this.username.equals("felipecury") || this.username.equals("luizmoledo") || this.username.equals("rleme") || this.username.equals("juliomoreno") || this.username.equals("marciozuchini"))){
			ConfigurableNavigationHandler nav 
			   = (ConfigurableNavigationHandler) 
				fc.getApplication().getNavigationHandler();
			
			nav.performNavigation("acesso-negado");
		}		
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

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getConfSenha() {
		return confSenha;
	}

	public void setConfSenha(String confSenha) {
		this.confSenha = confSenha;
	}

	public String getSenhaAtual() {
		return senhaAtual;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}
	
	
	
}
