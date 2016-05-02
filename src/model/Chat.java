package model;

import java.sql.Timestamp;
import java.util.Date;

public class Chat {
	private int idChat;
	private String textoChat;
	private String usuarioChat;
	private Timestamp dataChat;
	private String secao;
	
	public int getIdChat() {
		return idChat;
	}
	public void setIdChat(int idChat) {
		this.idChat = idChat;
	}
	public String getTextoChat() {
		return textoChat;
	}
	public void setTextoChat(String textoChat) {
		this.textoChat = textoChat;
	}
	public String getUsuarioChat() {
		return usuarioChat;
	}
	public void setUsuarioChat(String usuarioChat) {
		this.usuarioChat = usuarioChat;
	}
	public Date getDataChat() {
		return dataChat;
	}
	public void setDataChat(Timestamp dataChat) {
		this.dataChat = dataChat;
	}
	public String getSecao() {
		return secao;
	}
	public void setSecao(String secao) {
		this.secao = secao;
	}
	
	
}
