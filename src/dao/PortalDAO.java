package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.naming.NamingException;

import util.ConexaoMySQL;
import util.ConexaoOracle;
import model.Portal;


public class PortalDAO {
	private String sql;
	
	public Portal getLogin(String username) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT * FROM portal WHERE login = ?";
		Portal p = null;
		try{
			con = ConexaoOracle.abreConexao();
			ps = con.prepareStatement(sql);
				ps.setString(1, username);
				rs = ps.executeQuery();
				while(rs.next()){
					p = new Portal();
					p.setId(rs.getInt("id"));
					p.setTipo(rs.getString("tipo"));
					p.setCodigo(rs.getString("codigo"));
					p.setLogin(rs.getString("login"));
					p.setTimeout(rs.getInt("timeout"));
					p.setAtivo(rs.getString("ativo"));
					LotacaoDAO lotacaoDao = new LotacaoDAO();
					p.setLotacao(lotacaoDao.getLotacaoById(rs.getInt("lotacao")));
				}
		}finally{		
			rs.close();
			ps.close();
			con.close();
		}
		
		return p;
	}
	
	public boolean insereUsuarioMySQL(String user, String senha) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		sql = "INSERT INTO autenticacao (login,senha) VALUES(?, md5(?))";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, user);
			ps.setString(2, senha);
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public boolean verificaUsuarioMySQL(String user, String senha) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int achou = 0;
		sql = "SELECT * FROM autenticacao WHERE login = ? AND senha = md5(?)";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, user);
			ps.setString(2, senha);
			rs = ps.executeQuery();
			
			while(rs.next()){
				achou++;
			}
		}finally{		
			rs.close();
			ps.close();
			con.close();
		}
		if(achou > 0) return true;  return false;
	}
	
	public boolean verificaUsuarioMySQL(String user) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int achou = 0;
		sql = "SELECT * FROM autenticacao WHERE login = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, user);
			rs = ps.executeQuery();
			
			while(rs.next()){
				achou++;
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		if(achou > 0) return true;  return false;
	}
	
	public boolean atualizaUsuarioMySQL(String user, String senha) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		sql = "UPDATE autenticacao SET senha = md5(?) WHERE login = ? ";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, senha);
			ps.setString(2, user);
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
}
