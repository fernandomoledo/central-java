package model;

import java.util.Date;

public class Servidor {
	private int codiServ;
	private String nome;
	private Date dtNasc;
	private String sexo;
	public int getCodiServ() {
		return codiServ;
	}
	public void setCodiServ(int codiServ) {
		this.codiServ = codiServ;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Date getDtNasc() {
		return dtNasc;
	}
	public void setDtNasc(Date dtNasc) {
		this.dtNasc = dtNasc;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

}
