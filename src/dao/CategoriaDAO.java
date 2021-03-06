package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

import util.ConexaoMySQL;
import model.Categoria;
import model.CategoriaPai;

public class CategoriaDAO {
	private String sql;
	
	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	HttpSession session = (HttpSession) ec.getSession(false);
	String user = session.getAttribute("usuarioLogado").toString();
	
	public boolean salvar(Categoria c)  throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		sql = "INSERT INTO categoria(nome_categoria,termo_categoria, usuario) VALUES(?,?,?)";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql); 
			ps.setString(1, c.getNomeCategoria());
			ps.setString(2, c.getTermoCategoria());
			ps.setString(3, user);
			ps.execute();
		}finally{		
			ps.close();
			con.close();
		}
		return true;
	}
	
	public boolean atualizar(Categoria c)  throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		sql = "UPDATE categoria SET nome_categoria = ? , termo_categoria = ?, usuario = ? WHERE id_categoria = ?";	
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql); 
			ps.setString(1, c.getNomeCategoria());
			ps.setString(2, c.getTermoCategoria());
			ps.setString(3, user);
			ps.setInt(4, c.getIdCategoria());
			ps.executeUpdate();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public boolean inserirAmarracao(CategoriaPai cp) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		sql = "INSERT INTO categoria_pai VALUES(?,?,?) ";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql); 
			ps.setInt(1, cp.getPai().getIdCategoria());
			ps.setInt(2, cp.getFilha().getIdCategoria());
			ps.setString(3, user);
			ps.execute();
		}finally{		
			ps.close();
			con.close();
		}
		return true;
	}
	
	public boolean excluir(int pai, int filha)  throws ClassNotFoundException, SQLException, NamingException{
		System.out.println("PAI: " + pai + " / Filha: " + filha);
		Connection con = null;
		PreparedStatement ps = null;
		if(pai > 0){
			sql = "DELETE FROM categoria_pai WHERE pai = ? and filha = ?";
			try{
				con = ConexaoMySQL.abreConexao();
				 ps =con.prepareStatement(sql);
				ps.setInt(1, pai);
				ps.setInt(2, filha);
				ps.execute();
			}finally{
				ps.close();
				con.close();
			}
			
		}else{
			sql = "DELETE FROM categoria WHERE id_categoria = ?";
			try{
				con = ConexaoMySQL.abreConexao();
				ps =con.prepareStatement(sql);
				ps.setInt(1, filha);
				ps.execute();
			}finally{			
				ps.close();
				con.close();
			}
		}
		return true;
	}
	
	public Categoria getCategoriaByID(int id) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Categoria c = new Categoria();
		sql = "SELECT * FROM categoria WHERE id_categoria = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setInt(1, id);			
			rs = ps.executeQuery();
			while(rs.next()){		
				c.setIdCategoria(rs.getInt("id_categoria"));
				c.setNomeCategoria(rs.getString("nome_categoria"));
				c.setTermoCategoria(rs.getString("termo_categoria"));
			}
			System.out.println("CONSULTA getCategoriaByID()");
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return c;
	}
	
	public Categoria getCategoriaByName(String nome) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Categoria c = new Categoria();
		
		sql = "SELECT * FROM categoria WHERE nome_categoria = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, nome);
			System.out.println("Query: "+ps.toString());	
			rs = ps.executeQuery();
			while(rs.next()){	
			
				c.setIdCategoria(rs.getInt("id_categoria"));
				c.setNomeCategoria(rs.getString("nome_categoria"));
				c.setTermoCategoria(rs.getString("termo_categoria"));
				
			}
			System.out.println("CONSULTA getCategoriasVisitadas()");
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return c;
	}
	
	public List<Categoria> listar() throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Categoria> categorias = new ArrayList<Categoria>();
		sql = "SELECT * FROM categoria ORDER BY nome_categoria";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);			
			rs = ps.executeQuery();
			while(rs.next()){
				Categoria c = new Categoria();
				c.setIdCategoria(rs.getInt("id_categoria"));
				c.setNomeCategoria(rs.getString("nome_categoria"));
				c.setTermoCategoria(rs.getString("termo_categoria"));
				categorias.add(c);
			}
			System.out.println("CONSULTA listar() em CategoriaDAO");
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return categorias;
	}
	
	public List<CategoriaPai> listarCompleto() throws ClassNotFoundException, SQLException, NamingException{
		List<CategoriaPai> categorias = new ArrayList<CategoriaPai>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT c.* FROM categoria c WHERE c.id_categoria NOT IN (SELECT filha FROM categoria_pai) ORDER BY c.nome_categoria";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);		
			rs = ps.executeQuery();
			while(rs.next()){
				CategoriaPai cp = new CategoriaPai();
				Categoria c = new Categoria();
				c.setIdCategoria(rs.getInt("id_categoria"));
				c.setNomeCategoria(rs.getString("nome_categoria"));
				c.setTermoCategoria(rs.getString("termo_categoria"));
				cp.setFilha(c);
				categorias.add(cp);
			}
		}finally{		
			rs.close();
			ps.close();
			con.close();
		}
		
		sql = "SELECT c.id_categoria as id_pai, c.nome_categoria as nome_pai, c.termo_categoria as termo_pai, c1.id_categoria, c1.nome_categoria, c1.termo_categoria FROM categoria c, categoria c1, categoria_pai cp WHERE c.id_categoria = cp.pai AND c1.id_categoria = cp.filha ";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);	
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
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return categorias;
	}
	
	public List<CategoriaPai> listarCategorias(int pai) throws ClassNotFoundException, SQLException, NamingException{
		List<CategoriaPai> categorias = new ArrayList<CategoriaPai>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		if(pai == 0)
			sql = "SELECT c.* FROM categoria c WHERE c.id_categoria NOT IN (SELECT filha FROM categoria_pai) ORDER BY c.nome_categoria";
		else
			sql = "SELECT c.id_categoria as id_pai, c.nome_categoria as nome_pai, c.termo_categoria as termo_pai, c1.id_categoria, c1.nome_categoria, c1.termo_categoria FROM categoria c, categoria c1, categoria_pai cp WHERE c.id_categoria = cp.pai AND c1.id_categoria = cp.filha and cp.pai = ? ORDER BY c1.nome_categoria";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);	
			if(pai > 0)
				ps.setInt(1, pai);
			rs = ps.executeQuery();
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
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return categorias;
	}
	
	public boolean ehPai(int id) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int contador = 0;
		sql = "SELECT c.* FROM categoria c, categoria_pai cp WHERE c.id_categoria  = cp.filha and cp.pai = ? ORDER BY c.nome_categoria";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			while(rs.next()){
				contador++;
			}
			System.out.println("CONSULTA ehPai()");
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		if(contador > 0) return true;
		return false;
		
	}
	
	
	
}
