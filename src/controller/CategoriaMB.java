package controller;

import java.sql.SQLException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import util.Mensagens;
import dao.CategoriaDAO;
import model.Categoria;
import model.CategoriaPai;

@ManagedBean
@ViewScoped
public class CategoriaMB {
	private Categoria categoria = new Categoria();
	private Categoria pai = new Categoria();
	private Categoria filha = new Categoria();
	
	/*
	 * Este método é responsável por salvar/alterar uma categoria no banco central do MySQL
	 */
	public void salvar(){
		CategoriaDAO dao = new CategoriaDAO();
		try{
			if(this.categoria.getIdCategoria() > 0){
				if(dao.atualizar(this.categoria)){
					Mensagens.setMessage(1, "Categoria alterada com sucesso.");
					System.out.println("Categoria "+this.categoria.getNomeCategoria()+"-"+this.categoria.getTermoCategoria()+" alterada.");
					this.categoria = new Categoria();
				}else{
					Mensagens.setMessage(3, "Não foi possível alterar a categoria "+this.categoria.getNomeCategoria());
					
				}
			}else{
				if(dao.salvar(this.categoria)){
					Mensagens.setMessage(1, "Categoria salva com sucesso.");
					System.out.println("Categoria "+this.categoria.getNomeCategoria()+"-"+this.categoria.getTermoCategoria()+" salva.");
					this.categoria = new Categoria();
				}else{
					Mensagens.setMessage(3, "Não foi possível salvar a categoria "+this.categoria.getNomeCategoria());
					
				}
			}
			
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas ao salvar a categoria: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*
	 * Este método serve para carregar o objeto do tipo Categoria com os dados da categoria desejada para alteração
	 */
	public void editar(int id){
		CategoriaDAO dao = new CategoriaDAO();
		try{
			this.categoria = dao.getCategoriaByID(id);
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Não foi possível buscar a categoria: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*
	 * Este método é responsável por excluir uma determinada categoria/amarração de categorias cadastrada
	 */
	public void excluir(int pai, int filha){
		CategoriaDAO dao = new CategoriaDAO();
		try{
			if(dao.excluir(pai, filha)){
				Mensagens.setMessage(1, "Categoria excluída com sucesso!");
			}else{
				Mensagens.setMessage(3, "Erro ao excluir a categoria. Verifique se a mesma não é pai de outra categoria.");
			}
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Erro ao excluir a categoria. Verifique se a mesma não é pai de outra categoria.");
			e.printStackTrace();
		}
	}
	
	/*
	 * Este método é responsável por amarrar as categorias pai e filha
	 */
	public void amarrar(){
		if(this.pai.getIdCategoria() == this.filha.getIdCategoria()){
			Mensagens.setMessage(3, "A categoria filha não pode ser igual a categoria pai");
		}else{
			CategoriaDAO dao = new CategoriaDAO();
			CategoriaPai cp = new CategoriaPai();
			cp.setFilha(this.filha);
			cp.setPai(this.pai);
			try{
				if(dao.inserirAmarracao(cp)){
					Mensagens.setMessage(1, "Amarração realizada com sucesso.");
					System.out.println("Amarração entre "+cp.getPai().getNomeCategoria()+" e "+cp.getFilha().getTermoCategoria()+" salva.");
					this.pai = new Categoria();
					this.filha = new Categoria();
				}else{
					Mensagens.setMessage(3, "Não foi possível realizar a amarração entre " +cp.getPai().getNomeCategoria()+" e "+cp.getFilha().getTermoCategoria());
					
				}
			}catch(ClassNotFoundException | SQLException e){
				Mensagens.setMessage(3, "Problemas ao amarrar as categorias: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	
	/*
	 * Este método lista todas as categorias amarradas do banco central MySQL
	 */
	public List<CategoriaPai> getCategoriasCompleto(){
		CategoriaDAO dao = new CategoriaDAO();
		try{
			return dao.listarCompleto();
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas ao listar categorias: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Este método lista todas as categorias do banco central MySQL
	 */	
	public List<Categoria> getCategorias(){
		CategoriaDAO dao = new CategoriaDAO();
		try{
			return dao.listar();
		}catch(ClassNotFoundException | SQLException e){
			Mensagens.setMessage(3, "Problemas ao listar categorias: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * getters and setters
	 */

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Categoria getPai() {
		return pai;
	}

	public void setPai(Categoria pai) {
		this.pai = pai;
	}

	public Categoria getFilha() {
		return filha;
	}

	public void setFilha(Categoria filha) {
		this.filha = filha;
	}
	
}
