package model;

public class ChamadoPalavra {
	private int id;
	private Chamado chamado;
	private PalavraChave palavra;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Chamado getChamado() {
		return chamado;
	}
	public void setChamado(Chamado chamado) {
		this.chamado = chamado;
	}
	public PalavraChave getPalavra() {
		return palavra;
	}
	public void setPalavra(PalavraChave palavra) {
		this.palavra = palavra;
	}
	
	
}
