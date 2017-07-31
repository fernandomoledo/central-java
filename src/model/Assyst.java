package model;

import java.sql.Timestamp;
import java.util.Date;

public class Assyst {
	private String id;
	private String chamado;
	private Timestamp dataAbertura;
	public String getChamado() {
		return chamado;
	}
	public void setChamado(String chamado) {
		this.chamado = chamado;
	}
	public Timestamp getDataAbertura() {
		return dataAbertura;
	}
	public void setDataAbertura(Timestamp dataAbertura) {
		this.dataAbertura = dataAbertura;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}
