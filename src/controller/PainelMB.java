package controller;

import java.sql.SQLException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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
	
	public boolean verificaLotacao(String nome) throws SQLException{
		return new LotacaoDAO().verificaLotacao(nome);
	}
	
	public List<Andamento> getChamadosToDo(int lotacao){
		ChamadoDAO dao = new ChamadoDAO();
		List<Andamento> chamadosToDo;
		try {
			chamadosToDo = dao.getTodo(lotacao);
			this.qtdeToDo = chamadosToDo.size();
			return chamadosToDo;
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "Não foi possível obter a lista de chamados a fazer...");
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Andamento> getChamadosDoing(int lotacao){
		ChamadoDAO dao = new ChamadoDAO();
		List<Andamento> chamadosDoing;
		try {
			chamadosDoing = dao.getDoing(lotacao);
			this.qtdeDoing = chamadosDoing.size();
			return chamadosDoing;
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "Não foi possível obter a lista de chamados em andamento...");
			e.printStackTrace();
			return null;
		}
	}

	public List<Andamento> getChamadosDone(int lotacao){
		ChamadoDAO dao = new ChamadoDAO();
		List<Andamento> chamadosDone;
		try {
			chamadosDone = dao.getDone(lotacao, this.periodo);
			this.qtdeDone = chamadosDone.size();
			return chamadosDone;
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "Não foi possível obter a lista de chamados concluídos...");
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Chat> getLastChats(){
		ChatDAO dao = new ChatDAO();
		List<Chat> chats;
		try{
			chats = dao.getLastChats();
			return chats;
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Não foi possível obter as conversas do chat");
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Chat> getChats(){
		ChatDAO dao = new ChatDAO();
		List<Chat> chats;
		try{
			chats = dao.getChats();
			return chats;
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Não foi possível obter as conversas do chat");
			e.printStackTrace();
			return null;
		}
	}
	
	public void salvar(){
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) ec.getSession(false);
		String user = session.getAttribute("usuarioLogado").toString();
		if(!this.chat.getTextoChat().equals("")){
	 		this.chat.setUsuarioChat(user);
			ChatDAO dao = new ChatDAO();
			try{
				if(dao.salvar(this.chat)){
					System.out.println("Usuário "+user+" says: "+this.chat.getTextoChat());
				
				}
			}catch(ClassNotFoundException | SQLException e){
				Mensagens.setMessage(3, "Não foi possível enviar a conversa do chat");
				e.printStackTrace();
			
			}
			
			this.chat = new Chat();
		}
	}
	
	public List<LotacaoLotacao> getLotacoesAmarradasPorPai(String nome){
		LotacaoDAO dao = new LotacaoDAO();
		try {
			return dao.getLotacoesAmarradasPorPai(nome);
		} catch (SQLException e) {
			Mensagens.setMessage(3, "Não foi possível obter a lista de lotações amarradas: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
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
