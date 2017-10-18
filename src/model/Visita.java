package model;

public class Visita {
	private int id;
	private LotacaoVisitaPrev lotacao;
	private String periodo;
	private Atendente atendente;
	private String chamado;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public LotacaoVisitaPrev getLotacao() {
		return lotacao;
	}
	public void setLotacao(LotacaoVisitaPrev lotacao) {
		this.lotacao = lotacao;
	}
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	public Atendente getAtendente() {
		return atendente;
	}
	public void setAtendente(Atendente atendente) {
		this.atendente = atendente;
	}
	public String getChamado() {
		return chamado;
	}
	public void setChamado(String chamado) {
		this.chamado = chamado;
	}
	
	
}
