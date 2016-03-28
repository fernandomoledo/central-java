package model;

public class Chamado {
	private long id;
	private int numero;
	private String status;
	private String urgente;
	private Portal solicitante;
	private Assunto assunto;
	private Portal responsavel;
	private Lotacao lotacaoSolicitante;
	private Lotacao lotacaoDestino;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUrgente() {
		return urgente;
	}
	public void setUrgente(String urgente) {
		this.urgente = urgente;
	}
	public Portal getSolicitante() {
		return solicitante;
	}
	public void setSolicitante(Portal solicitante) {
		this.solicitante = solicitante;
	}
	public Assunto getAssunto() {
		return assunto;
	}
	public void setAssunto(Assunto assunto) {
		this.assunto = assunto;
	}
	public Portal getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(Portal responsavel) {
		this.responsavel = responsavel;
	}
	public Lotacao getLotacaoSolicitante() {
		return lotacaoSolicitante;
	}
	public void setLotacaoSolicitante(Lotacao lotacaoSolicitante) {
		this.lotacaoSolicitante = lotacaoSolicitante;
	}
	public Lotacao getLotacaoDestino() {
		return lotacaoDestino;
	}
	public void setLotacaoDestino(Lotacao lotacaoDestino) {
		this.lotacaoDestino = lotacaoDestino;
	}
	
	
}
