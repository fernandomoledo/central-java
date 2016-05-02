package model;

import java.util.Date;

public class Andamento {
	private long id;
	private Chamado chamado;
	private Portal usuario;
	private String classificacao;
	private Date dtAndamento;
	private String texto;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Chamado getChamado() {
		return chamado;
	}
	public void setChamado(Chamado chamado) {
		this.chamado = chamado;
	}
	public Portal getUsuario() {
		return usuario;
	}
	public void setUsuario(Portal usuario) {
		this.usuario = usuario;
	}
	public String getClassificacao() {
		return classificacao;
	}
	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}
	public Date getDtAndamento() {
		return dtAndamento;
	}
	public void setDtAndamento(Date dtAndamento) {
		this.dtAndamento = dtAndamento;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	
}
