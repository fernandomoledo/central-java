package model;

public class PalavraChave {
	private int id;
	private String nome;
	private Lotacao lotacao;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Lotacao getLotacao() {
		return lotacao;
	}
	public void setLotacao(Lotacao lotacao) {
		this.lotacao = lotacao;
	}
		
}
