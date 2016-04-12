package controller;

import java.sql.SQLException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import util.Mensagens;
import dao.ChamadoDAO;
import dao.ChatDAO;
import dao.LotacaoDAO;
import model.Andamento;
import model.Chat;
import model.Lotacao;
import model.LotacaoLotacao;

@ManagedBean
@ViewScoped
public class PainelMB {
	private int qtdeToDo = 0;
	private int qtdeDoing = 0;
	private int qtdeDone = 0;
	private Chat chat = new Chat();
	private Lotacao lotacao = new Lotacao();
	private int periodo;
	
	/*
	 * Este m�todo verifica a lota��o em que o usu�rio encontra-se lotado
	 */
	public boolean verificaLotacao(String nome) throws SQLException, ClassNotFoundException, NamingException{
		return new LotacaoDAO().verificaLotacao(nome);
	}
	
	/*
	 * Este m�todo lista todos os chamados a fazer da lota��o do usu�rio
	 */
	public List<Andamento> getChamadosToDo(int lotacao) throws NamingException{
		ChamadoDAO dao = new ChamadoDAO();
		List<Andamento> chamadosToDo;
		try {
			chamadosToDo = dao.getTodo(lotacao);
			this.qtdeToDo = chamadosToDo.size();
			return chamadosToDo;
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "N�o foi poss�vel obter a lista de chamados a fazer...");
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Este m�todo lista todos os chamados em andamento da lota��o do usu�rio
	 */
	public List<Andamento> getChamadosDoing(int lotacao) throws NamingException{
		ChamadoDAO dao = new ChamadoDAO();
		List<Andamento> chamadosDoing;
		try {
			chamadosDoing = dao.getDoing(lotacao);
			this.qtdeDoing = chamadosDoing.size();
			return chamadosDoing;
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "N�o foi poss�vel obter a lista de chamados em andamento...");
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Este m�todo lista todos os chamados conclu�dos - em um determinado per�odo - da lota��o do usu�rio
	 */
	public List<Andamento> getChamadosDone(int lotacao) throws NamingException{
		ChamadoDAO dao = new ChamadoDAO();
		List<Andamento> chamadosDone;
		try {
			chamadosDone = dao.getDone(lotacao, this.periodo);
			this.qtdeDone = chamadosDone.size();
			return chamadosDone;
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "N�o foi poss�vel obter a lista de chamados conclu�dos...");
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Este m�todo retorna as �ltimas 5 intera��es do chat
	 */
	public List<Chat> getLastChats() throws NamingException{
		ChatDAO dao = new ChatDAO();
		List<Chat> chats;
		try{
			chats = dao.getLastChats();
			return chats;
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "N�o foi poss�vel obter as conversas do chat");
			e.printStackTrace();
			return null;
		}
	}
	
	
	/*
	 * Este m�todo retorna todo o hist�rico do chat
	 */
	public List<Chat> getChats() throws NamingException{
		ChatDAO dao = new ChatDAO();
		List<Chat> chats;
		try{
			chats = dao.getChats();
			return chats;
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "N�o foi poss�vel obter as conversas do chat");
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * Este m�todo � respons�vel por salvar a mensagem digitada no chat no banco de dados central MySQL
	 */
	public void salvar() throws NamingException{
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) ec.getSession(false);
		String user = session.getAttribute("usuarioLogado").toString();
		if(!this.chat.getTextoChat().equals("")){
	 		this.chat.setUsuarioChat(user);
			ChatDAO dao = new ChatDAO();
			try{
				if(dao.salvar(this.chat)){
					System.out.println("Usu�rio "+user+" says: "+this.chat.getTextoChat());
				
				}
			}catch(ClassNotFoundException | SQLException e){
				Mensagens.setMessage(3, "N�o foi poss�vel enviar a conversa do chat");
				e.printStackTrace();
			
			}
			
			this.chat = new Chat();
		}
	}
	
	/*
	 * Este m�todo retorna as lota��es amarradas por pai do banco central MySQL, caso o usu�rio seja administrador
	 */
	public List<LotacaoLotacao> getLotacoesAmarradasPorPai(String nome) throws ClassNotFoundException, NamingException{
		LotacaoDAO dao = new LotacaoDAO();
		try {
			return dao.getLotacoesAmarradasPorPai(nome);
		} catch (SQLException e) {
			Mensagens.setMessage(3, "N�o foi poss�vel obter a lista de lota��es amarradas: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * getters and setters
	 */
	
	public String retornaStatus(String s){
		switch(s){
			case "AF": return "A fazer";
			case "AN": return "Em andamento";
			case "CA": return "Cancelado";
			case "CO": return "Conclu�do";
			case "EX": return "Ch. externo";
			case "PR": return "Projeto";
			case "RE": return "Reaberto";
			case "US": return "Ag. usu�rio";
		}
		return s;
	}
	
	public int getQtdeToDo() {
		return qtdeToDo;
	}

	public void setQtdeToDo(int qtdeToDo) {
		this.qtdeToDo = qtdeToDo;
	}

	public int getQtdeDoing() {
		return qtdeDoing;
	}

	public void setQtdeDoing(int qtdeDoing) {
		this.qtdeDoing = qtdeDoing;
	}

	public int getQtdeDone() {
		return qtdeDone;
	}

	public void setQtdeDone(int qtdeDone) {
		this.qtdeDone = qtdeDone;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public Lotacao getLotacao() {
		return lotacao;
	}

	public void setLotacao(Lotacao lotacao) {
		this.lotacao = lotacao;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
}
