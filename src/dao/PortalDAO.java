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
		sql = "SELECT * FROM portal WHERE login = ?";
		Portal p = null;
		Connection con = null;
		con = ConexaoOracle.abreConexao();
		PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
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
		
		con.close();
		
		return p;
	}
	
	public boolean insereUsuarioMySQL(String user, String senha) throws SQLException, ClassNotFoundException, NamingException{
		sql = "INSERT INTO autenticacao (login,senha) VALUES(?, md5(?))";
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		ps.setString(1, user);
		ps.setString(2, senha);
		ps.execute();
		con.close();
		return true;
	}
	
	public boolean verificaUsuarioMySQL(String user, String senha) throws SQLException, ClassNotFoundException, NamingException{
		sql = "SELECT * FROM autenticacao WHERE login = ? AND senha = md5(?)";
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		ps.setString(1, user);
		ps.setString(2, senha);
		ResultSet rs = ps.executeQuery();
		int achou = 0;
		while(rs.next()){
			achou++;
		}
		
		con.close();
		if(achou > 0) return true;  return false;
	}
	
	public boolean verificaUsuarioMySQL(String user) throws SQLException, ClassNotFoundException, NamingException{
		sql = "SELECT * FROM autenticacao WHERE login = ?";
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		ps.setString(1, user);
		ResultSet rs = ps.executeQuery();
		int achou = 0;
		while(rs.next()){
			achou++;
		}
		
		con.close();
		if(achou > 0) return true;  return false;
	}
	
	public boolean atualizaUsuarioMySQL(String user, String senha) throws SQLException, ClassNotFoundException, NamingException{
		sql = "UPDATE autenticacao SET senha = md5(?) WHERE login = ? ";
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		ps.setString(1, senha);
		ps.setString(2, user);
		ps.execute();
		con.close();
		return true;
	}
	
}
