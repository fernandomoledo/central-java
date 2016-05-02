package model;

import java.util.Date;

public class Tombo {
	private int id;
	private Lotacao lotacao;
	private Date dtGarantia;
	private String descricao;
	private long nroTombo;
	private String serie;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Lotacao getLotacao() {
		return lotacao;
	}
	public void setLotacao(Lotacao lotacao) {
		this.lotacao = lotacao;
	}
	public Date getDtGarantia() {
		return dtGarantia;
	}
	public void setDtGarantia(Date dtGarantia) {
		this.dtGarantia = dtGarantia;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public long getNroTombo() {
		return nroTombo;
	}
	public void setNroTombo(long nroTombo) {
		this.nroTombo = nroTombo;
	}
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	
		
}
