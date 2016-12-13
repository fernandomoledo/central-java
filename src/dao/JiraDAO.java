package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import model.ComponenteJIRA;
import model.DeParaJIRA;
import model.ModuloJIRA;
import util.ConexaoMySQL;

public class JiraDAO {
	private String sql;
	
	public boolean salvarModulo(ModuloJIRA m) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;		
			sql = "INSERT INTO modulo(label,valor_modulo) VALUES(?,?)";
			try{
				con = ConexaoMySQL.abreConexao();
				ps = con.prepareStatement(sql);
				ps.setString(1, m.getLabel());
				ps.setString(2, m.getValue());
				ps.execute();
				System.out.println("Módulo "+m.getLabel()+" salvo!");
			}finally{	
				ps.close();
				con.close();
			}
		
		return true;
	}
	
	public boolean atualizarModulo(ModuloJIRA m) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;		
			sql = "UPDATE modulo SET label = ? , valor_modulo = ? WHERE id = ?";
			try{
				con = ConexaoMySQL.abreConexao();
				ps = con.prepareStatement(sql);
				ps.setString(1, m.getLabel());
				ps.setString(2, m.getValue());
				ps.setInt(3, m.getId());
				ps.execute();
				System.out.println("Módulo "+m.getLabel()+" atualizado!");
			}finally{	
				ps.close();
				con.close();
			}
		
		return true;
	}
	
	public List<ModuloJIRA> listarModulos() throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;	
		ResultSet rs = null;
		List<ModuloJIRA> modulos = new ArrayList<ModuloJIRA>();
		sql = "SELECT * FROM modulo ORDER BY label";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				ModuloJIRA m = new ModuloJIRA();
				m.setId(rs.getInt("id"));
				m.setLabel(rs.getString("label"));
				m.setValue(rs.getString("valor_modulo"));
				modulos.add(m);
			}
		}finally{	
			rs.close();
			ps.close();
			con.close();
		}
		return modulos;
	}
	
	public ModuloJIRA getModuloById(int id) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;	
		ResultSet rs = null;
		ModuloJIRA m = new ModuloJIRA();
		sql = "SELECT * FROM modulo WHERE id = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while(rs.next()){				
				m.setId(rs.getInt("id"));
				m.setLabel(rs.getString("label"));
				m.setValue(rs.getString("valor_modulo"));
			}
		}finally{	
			rs.close();
			ps.close();
			con.close();
		}
		return m;
	}
	
	public boolean excluirModulo(int id) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;		
			sql = "DELETE FROM modulo WHERE id = ?";
			try{
				con = ConexaoMySQL.abreConexao();
				ps = con.prepareStatement(sql);
				ps.setInt(1, id);
				ps.execute();
			}finally{	
				ps.close();
				con.close();
			}
		
		return true;
	}
	
	public boolean salvarComponente(ComponenteJIRA c) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;		
			sql = "INSERT INTO componente(componente) VALUES(?)";
			try{
				con = ConexaoMySQL.abreConexao();
				ps = con.prepareStatement(sql);
				ps.setString(1, c.getComponente());
				ps.execute();
				System.out.println("Componente "+c.getComponente()+" salvo!");
			}finally{	
				ps.close();
				con.close();
			}
		
		return true;
	}
	
	public boolean atualizarComponente(ComponenteJIRA c) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;		
			sql = "UPDATE componente SET componente = ? WHERE id = ?";
			try{
				con = ConexaoMySQL.abreConexao();
				ps = con.prepareStatement(sql);
				ps.setString(1, c.getComponente());
				ps.setInt(2, c.getId());
				ps.execute();
				System.out.println("Componente "+c.getComponente()+" atualizado!");
			}finally{	
				ps.close();
				con.close();
			}
		
		return true;
	}
	
	public List<ComponenteJIRA> listarComponentes() throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;	
		ResultSet rs = null;
		List<ComponenteJIRA> componentes = new ArrayList<ComponenteJIRA>();
		sql = "SELECT * FROM componente ORDER BY componente";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				ComponenteJIRA c = new ComponenteJIRA();
				c.setId(rs.getInt("id"));
				c.setComponente(rs.getString("componente"));
				componentes.add(c);
			}
		}finally{	
			rs.close();
			ps.close();
			con.close();
		}
		return componentes;
	}
	
	public ComponenteJIRA getComponenteById(int id) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;	
		ResultSet rs = null;
		ComponenteJIRA c = new ComponenteJIRA();
		sql = "SELECT * FROM componente WHERE id = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while(rs.next()){				
				c.setId(rs.getInt("id"));
				c.setComponente(rs.getString("componente"));
			}
		}finally{	
			rs.close();
			ps.close();
			con.close();
		}
		return c;
	}
	
	public boolean excluirComponente(int id) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;		
			sql = "DELETE FROM componente WHERE id = ?";
			try{
				con = ConexaoMySQL.abreConexao();
				ps = con.prepareStatement(sql);
				ps.setInt(1, id);
				ps.execute();
			}finally{	
				ps.close();
				con.close();
			}
		
		return true;
	}
	
	
	public boolean salvarDePara(DeParaJIRA dp) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;		
			sql = "INSERT INTO depara(palavra_chave,id_modulo, id_componente) VALUES(?,?,?)";
			try{
				con = ConexaoMySQL.abreConexao();
				ps = con.prepareStatement(sql);
				ps.setString(1, dp.getPalavraChave());
				ps.setInt(2, dp.getModulo().getId());
				ps.setInt(3, dp.getComponente().getId());
				ps.execute();
			}finally{	
				ps.close();
				con.close();
			}
		
		return true;
	}
	
	public boolean atualizarDePara(DeParaJIRA dp) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;		
			sql = "UPDATE depara SET palavra_chave = ? , id_modulo = ?, id_componente = ? WHERE id = ?";
			try{
				con = ConexaoMySQL.abreConexao();
				ps = con.prepareStatement(sql);
				ps.setString(1, dp.getPalavraChave());
				ps.setInt(2, dp.getModulo().getId());
				ps.setInt(3, dp.getComponente().getId());
				ps.setInt(4, dp.getId());
				ps.execute();
			}finally{	
				ps.close();
				con.close();
			}
		
		return true;
	}
	
	public List<DeParaJIRA> listarDePara() throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;	
		ResultSet rs = null;
		List<DeParaJIRA> deparas = new ArrayList<DeParaJIRA>();
		sql = "SELECT * FROM depara ORDER BY palavra_chave";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				DeParaJIRA dp = new DeParaJIRA();
				dp.setId(rs.getInt("id"));
				dp.setPalavraChave(rs.getString("palavra_chave"));
				dp.setModulo(this.getModuloById(rs.getInt("id_modulo")));
				dp.setComponente(this.getComponenteById(rs.getInt("id_componente")));
				deparas.add(dp);
			}
		}finally{	
			rs.close();
			ps.close();
			con.close();
		}
		return deparas;
	}
	
	public DeParaJIRA getDeParaById(int id) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;	
		ResultSet rs = null;
		DeParaJIRA dp = new DeParaJIRA();
		sql = "SELECT * FROM depara WHERE id = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while(rs.next()){				
				dp.setId(rs.getInt("id"));
				dp.setPalavraChave(rs.getString("palavra_chave"));
				dp.setModulo(this.getModuloById(rs.getInt("id_modulo")));
				dp.setComponente(this.getComponenteById(rs.getInt("id_componente")));
			}
		}finally{	
			rs.close();
			ps.close();
			con.close();
		}
		return dp;
	}
	
	public boolean excluirDePara(int id) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;		
			sql = "DELETE FROM depara WHERE id = ?";
			try{
				con = ConexaoMySQL.abreConexao();
				ps = con.prepareStatement(sql);
				ps.setInt(1, id);
				ps.execute();
			}finally{	
				ps.close();
				con.close();
			}
		
		return true;
	}
	
}
