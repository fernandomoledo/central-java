package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.ConexaoMySQL;
import model.Categoria;
import model.CategoriaPai;

public class CategoriaDAO {
	private String sql;
	
	public boolean salvar(Categoria c)  throws ClassNotFoundException, SQLException{
		sql = "INSERT INTO categoria(nome_categoria,termo_categoria) VALUES(?,?)";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
		ps.setString(1, c.getNomeCategoria());
		ps.setString(2, c.getTermoCategoria());
		ps.execute();
		ConexaoMySQL.fechaConexao();
		return true;
	}
	
	public boolean atualizar(Categoria c)  throws ClassNotFoundException, SQLException{
		sql = "UPDATE categoria SET nome_categoria = ? , termo_categoria = ? WHERE id_categoria = ?";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
		ps.setString(1, c.getNomeCategoria());
		ps.setString(2, c.getTermoCategoria());
		ps.setInt(3, c.getIdCategoria());
		ps.executeUpdate();
		ConexaoMySQL.fechaConexao();
		return true;
	}
	
	public boolean inserirAmarracao(CategoriaPai cp) throws ClassNotFoundException, SQLException{
		sql = "INSERT INTO categoria_pai VALUES(?,?) ";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
		ps.setInt(1, cp.getPai().getIdCategoria());
		ps.setInt(2, cp.getFilha().getIdCategoria());
		ps.execute();
		ConexaoMySQL.fechaConexao();
		return true;
	}
	
	public boolean excluir(int pai, int filha)  throws ClassNotFoundException, SQLException{
		PreparedStatement ps = null;
		if(pai > 0){
			sql = "DELETE FROM categoria_pai WHERE pai = ? and filha = ?";
			ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
			ps.setInt(1, pai);
			ps.setInt(2, filha);
			ps.execute();
			ConexaoMySQL.fechaConexao();
			
		}
		sql = "DELETE FROM categoria WHERE id_categoria = ?";
		ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
		ps.setInt(1, filha);
		ps.execute();
		ConexaoMySQL.fechaConexao();
		return true;
	}
	
	public Categoria getCategoriaByID(int id) throws ClassNotFoundException, SQLException{
		sql = "SELECT * FROM categoria WHERE id_categoria = ?";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
		ps.setInt(1, id);
		Categoria c = new Categoria();
		
		ResultSet rs = ps.executeQuery();
		while(rs.next()){		
			c.setIdCategoria(rs.getInt("id_categoria"));
			c.setNomeCategoria(rs.getString("nome_categoria"));
			c.setTermoCategoria(rs.getString("termo_categoria"));
		}
		System.out.println("CONSULTA getCategoriaByID()");
		ConexaoMySQL.fechaConexao();
		return c;
	}
	
	public Categoria getCategoriaByName(String nome) throws ClassNotFoundException, SQLException{
		sql = "SELECT * FROM categoria WHERE nome_categoria = ?";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
		ps.setString(1, nome);
		System.out.println("Query: "+ps.toString());
		Categoria c = new Categoria();
		ResultSet rs = ps.executeQuery();
		while(rs.next()){	
		
			c.setIdCategoria(rs.getInt("id_categoria"));
			c.setNomeCategoria(rs.getString("nome_categoria"));
			c.setTermoCategoria(rs.getString("termo_categoria"));
			
		}
		System.out.println("CONSULTA getCategoriasVisitadas()");
		ConexaoMySQL.fechaConexao();
		return c;
	}
	
	public List<Categoria> listar() throws ClassNotFoundException, SQLException{
		sql = "SELECT * FROM categoria ORDER BY nome_categoria";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
		List<Categoria> categorias = new ArrayList<Categoria>();
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			Categoria c = new Categoria();
			c.setIdCategoria(rs.getInt("id_categoria"));
			c.setNomeCategoria(rs.getString("nome_categoria"));
			c.setTermoCategoria(rs.getString("termo_categoria"));
			categorias.add(c);
		}
		System.out.println("CONSULTA listar() em CategoriaDAO");
		ConexaoMySQL.fechaConexao();
		return categorias;
	}
	
	public List<CategoriaPai> listarCompleto() throws ClassNotFoundException, SQLException{
		List<CategoriaPai> categorias = new ArrayList<CategoriaPai>();
		sql = "SELECT c.* FROM categoria c WHERE c.id_categoria NOT IN (SELECT filha FROM categoria_pai) ORDER BY c.nome_categoria";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);		
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			CategoriaPai cp = new CategoriaPai();
			Categoria c = new Categoria();
			c.setIdCategoria(rs.getInt("id_categoria"));
			c.setNomeCategoria(rs.getString("nome_categoria"));
			c.setTermoCategoria(rs.getString("termo_categoria"));
			cp.setFilha(c);
			categorias.add(cp);
		}
		
		ConexaoMySQL.fechaConexao();
		
		sql = "SELECT c.id_categoria as id_pai, c.nome_categoria as nome_pai, c.termo_categoria as termo_pai, c1.id_categoria, c1.nome_categoria, c1.termo_categoria FROM categoria c, categoria c1, categoria_pai cp WHERE c.id_categoria = cp.pai AND c1.id_categoria = cp.filha";
		ps = ConexaoMySQL.abreConexao().prepareStatement(sql);		
		rs = ps.executeQuery();
		while(rs.next()){
			CategoriaPai cp = new CategoriaPai();
			Categoria c = new Categoria();
			c.setIdCategoria(rs.getInt("id_categoria"));
			c.setNomeCategoria(rs.getString("nome_categoria"));
			c.setTermoCategoria(rs.getString("termo_categoria"));
			cp.setFilha(c);
			c = new Categoria();
			c.setIdCategoria(rs.getInt("id_pai"));
			c.setNomeCategoria(rs.getString("nome_pai"));
			c.setTermoCategoria(rs.getString("termo_pai"));
			cp.setPai(c);
			categorias.add(cp);
		}
		System.out.println("CONSULTA listarCompleto()");
		ConexaoMySQL.fechaConexao();
		return categorias;
	}
	
	public List<CategoriaPai> listarCategorias(int pai) throws ClassNotFoundException, SQLException{
		List<CategoriaPai> categorias = new ArrayList<CategoriaPai>();
		if(pai == 0)
			sql = "SELECT c.* FROM categoria c WHERE c.id_categoria NOT IN (SELECT filha FROM categoria_pai) ORDER BY c.nome_categoria";
		else
			sql = "SELECT c.id_categoria as id_pai, c.nome_categoria as nome_pai, c.termo_categoria as termo_pai, c1.id_categoria, c1.nome_categoria, c1.termo_categoria FROM categoria c, categoria c1, categoria_pai cp WHERE c.id_categoria = cp.pai AND c1.id_categoria = cp.filha and cp.pai = ?";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);	
		if(pai > 0)
			ps.setInt(1, pai);
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			CategoriaPai cp = new CategoriaPai();
			Categoria c = new Categoria();
			c.setIdCategoria(rs.getInt("id_categoria"));
			c.setNomeCategoria(rs.getString("nome_categoria"));
			c.setTermoCategoria(rs.getString("termo_categoria"));
			cp.setFilha(c);
			if(pai > 0){
				c = new Categoria();
				c.setIdCategoria(rs.getInt("id_pai"));
				c.setNomeCategoria(rs.getString("nome_pai"));
				c.setTermoCategoria(rs.getString("termo_pai"));
			}			
				
			cp.setPai(c);			
			
			categorias.add(cp);
		}
		System.out.println("CONSULTA listarCategorias()");
		ConexaoMySQL.fechaConexao();
		return categorias;
	}
	
	public boolean ehPai(int id) throws SQLException{
		sql = "SELECT c.* FROM categoria c, categoria_pai cp WHERE c.id_categoria  = cp.filha and cp.pai = ? ORDER BY c.nome_categoria";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		int contador = 0;
		while(rs.next()){
			contador++;
		}
		System.out.println("CONSULTA ehPai()");
		ConexaoMySQL.fechaConexao();
		if(contador > 0) return true;
		return false;
		
	}
	
	
	
}
