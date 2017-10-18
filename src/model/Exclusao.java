package model;

public class Exclusao {
	private int id;
	private Atendente atendente;
	private LotacaoVisitaPrev lotacao;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Atendente getAtendente() {
		return atendente;
	}
	public void setAtendente(Atendente atendente) {
		this.atendente = atendente;
	}
	public LotacaoVisitaPrev getLotacao() {
		return lotacao;
	}
	public void setLotacao(LotacaoVisitaPrev lotacao) {
		this.lotacao = lotacao;
	}
	
	
}
