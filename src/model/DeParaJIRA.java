package model;

public class DeParaJIRA {
	private int id;
	private String palavraChave;
	private ModuloJIRA modulo;
	private ComponenteJIRA componente;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPalavraChave() {
		return palavraChave;
	}
	public void setPalavraChave(String palavraChave) {
		this.palavraChave = palavraChave;
	}
	public ModuloJIRA getModulo() {
		return modulo;
	}
	public void setModulo(ModuloJIRA modulo) {
		this.modulo = modulo;
	}
	public ComponenteJIRA getComponente() {
		return componente;
	}
	public void setComponente(ComponenteJIRA componente) {
		this.componente = componente;
	}
}
