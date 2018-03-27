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
import util.ConexaoOracle;
import model.Lotacao;
import model.LotacaoVisitaPrev;
import model.Sistema;
import model.Tombo;

public class SistemaDAO {
	private String sql;
	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	HttpSession session = (HttpSession) ec.getSession(false);
	String secao = session.getAttribute("secao").toString();
	
	
	public boolean salvar(Sistema s) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "INSERT INTO sistema(nome) VALUES(?)";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, s.getNome());
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public boolean atualizar(Sistema s) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "UPDATE sistema SET nome = ?, ativo=? WHERE id = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, s.getNome());
			ps.setString(2, s.getAtivo());
			ps.setInt(3, s.getId());
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public boolean excluir(Sistema s) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "UPDATE sistema SET ativo='N' WHERE id = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setInt(1, s.getId());
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public List<Sistema> listarSistemas(int flag) throws SQLException, ClassNotFoundException, NamingException{
		List<Sistema> sistemas = new ArrayList<Sistema>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		if(flag == 0)
			sql = "SELECT * FROM sistema ORDER BY nome ";
		else
			sql = "SELECT * FROM sistema WHERE ativo='S' ORDER BY nome ";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				Sistema s = new Sistema();
				s.setId(rs.getInt("id"));
				s.setNome(rs.getString("nome"));
				s.setAtivo(rs.getString("ativo"));
				sistemas.add(s);
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return sistemas;
	}
	
	public Sistema buscar(int id) throws SQLException, ClassNotFoundException, NamingException{
		Sistema s = new Sistema();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT * FROM sistema WHERE id = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while(rs.next()){				
				s.setId(rs.getInt("id"));
				s.setNome(rs.getString("nome"));
				s.setAtivo(rs.getString("ativo"));
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return s;
	}
	
	public Sistema buscarPorNome(String nome) throws SQLException, ClassNotFoundException, NamingException{
		Sistema s = new Sistema();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT * FROM sistema WHERE nome = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setString(1, nome);
			rs = ps.executeQuery();
			while(rs.next()){				
				s.setId(rs.getInt("id"));
				s.setNome(rs.getString("nome"));
				s.setAtivo(rs.getString("ativo"));
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return s;
	}
	
	
}
