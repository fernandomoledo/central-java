package model;

public class LotacaoLotacao {
	private int id;
	private Lotacao pai;
	private Lotacao filha;
	public Lotacao getPai() {
		return pai;
	}
	public void setPai(Lotacao pai) {
		this.pai = pai;
	}
	public Lotacao getFilha() {
		return filha;
	}
	public void setFilha(Lotacao filha) {
		this.filha = filha;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
