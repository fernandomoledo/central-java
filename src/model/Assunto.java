package model;

public class Assunto {
	private long id;
	private String descricao;
	private Lotacao lotacao;
	private String ativo;
	private long grupoAssunto;
	private String problema;
	private String tomboObrigatorio;
	private String privativo;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Lotacao getLotacao() {
		return lotacao;
	}
	public void setLotacao(Lotacao lotacao) {
		this.lotacao = lotacao;
	}
	public String getAtivo() {
		return ativo;
	}
	public void setAtivo(String ativo) {
		this.ativo = ativo;
	}
	public long getGrupoAssunto() {
		return grupoAssunto;
	}
	public void setGrupoAssunto(long grupoAssunto) {
		this.grupoAssunto = grupoAssunto;
	}
	public String getProblema() {
		return problema;
	}
	public void setProblema(String problema) {
		this.problema = problema;
	}
	public String getTomboObrigatorio() {
		return tomboObrigatorio;
	}
	public void setTomboObrigatorio(String tomboObrigatorio) {
		this.tomboObrigatorio = tomboObrigatorio;
	}
	public String getPrivativo() {
		return privativo;
	}
	public void setPrivativo(String privativo) {
		this.privativo = privativo;
	}
	
	
}
