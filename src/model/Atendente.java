package model;

public class Atendente {
	private int id;
	private String nome;
	private int affectedUserId;
	private int assignedUserId;
	private boolean isAdm;
	private String username;
	
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
	public int getAffectedUserId() {
		return affectedUserId;
	}
	public void setAffectedUserId(int affectedUserId) {
		this.affectedUserId = affectedUserId;
	}
	public int getAssignedUserId() {
		return assignedUserId;
	}
	public void setAssignedUserId(int assignedUserId) {
		this.assignedUserId = assignedUserId;
	}
	public boolean isAdm() {
		return isAdm;
	}
	public void setAdm(boolean isAdm) {
		this.isAdm = isAdm;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
}
