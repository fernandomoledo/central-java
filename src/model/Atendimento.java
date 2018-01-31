package model;

import java.util.Calendar;

public class Atendimento {
	private int id;
	private String dtCadastro;
	private String chamado;
	private Sistema sistema;
	private Atendente atendente;
	private String problema;
	private String solucao;
	private String encaminhamento;
	private String wiki;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDtCadastro() {
		return dtCadastro;
	}
	public void setDtCadastro(String dtCadastro) {
		this.dtCadastro = dtCadastro;
	}
	public String getChamado() {
		return chamado;
	}
	public void setChamado(String chamado) {
		this.chamado = chamado;
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
	public String getProblema() {
		return problema;
	}
	public void setProblema(String problema) {
		this.problema = problema;
	}
	public String getSolucao() {
		return solucao;
	}
	public void setSolucao(String solucao) {
		this.solucao = solucao;
	}
	public String getEncaminhamento() {
		return encaminhamento;
	}
	public void setEncaminhamento(String encaminhamento) {
		this.encaminhamento = encaminhamento;
	}
	public String getWiki() {
		return wiki;
	}
	public void setWiki(String wiki) {
		this.wiki = wiki;
	}
}
