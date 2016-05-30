package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import model.Lotacao;
import model.LotacaoLotacao;
import util.ConexaoMySQL;
import util.ConexaoOracle;

public class LotacaoDAO {
	private String sql;
	
	public Lotacao getLotacaoById(int id) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT ID, to_char(NOME) as NOME FROM lotacao WHERE id = ?";
		Lotacao l = new Lotacao();
		try{
			con = ConexaoOracle.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while(rs.next()){
				l.setId(rs.getInt("ID"));
				l.setNome(rs.getString("NOME"));
			}
			System.out.println("CONSULTA getLotacaoByID()");
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return l;
	}
	
	public Lotacao getLotacaoByName(String nome) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT ID, to_char(NOME) as NOME FROM lotacao WHERE NOME = ?";
		Lotacao l = new Lotacao();
		try{
			con = ConexaoOracle.abreConexao();
			ps = con.prepareStatement(sql);
				ps.setString(1, nome);
				rs = ps.executeQuery();
				while(rs.next()){
					l.setId(rs.getInt("ID"));
					l.setNome(rs.getString("NOME"));
				}
				System.out.println("CONSULTA getLotacaoByID()");
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return l;
	}
	
	public List<Lotacao> getLotacoes() throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Lotacao> lotacoes = new ArrayList<Lotacao>();
		sql = "SELECT id, nome FROM lotacao ORDER BY nome";
		try{
			con = ConexaoOracle.abreConexao();
			 ps = con.prepareStatement(sql);
			 rs = ps.executeQuery();
		
			while(rs.next()){
				Lotacao l = new Lotacao();
				l.setId(rs.getInt("id"));
				l.setNome(rs.getString("nome"));
				lotacoes.add(l);
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return lotacoes;
		
	}
	
	public boolean verificaLotacao(String nome) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int qtde = 0;
		if(nome.equals("SEÇÃO DE ATENDIMENTO") || nome.equals("SEÇÃO DE ATENDIMENTO ESPECIALIZADO"))
			sql = "select * from lotacao_lotacao where pai = (select pai from lotacao_lotacao where filha = ?)";
		else
			sql = "select * from lotacao_lotacao where pai = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, nome);
			rs = ps.executeQuery();
		
			while(rs.next()){
				qtde++;
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		if(qtde >0) return true; return false;
	}
	
	public boolean salvar(String lprin, String lsec) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		sql = "INSERT INTO lotacao_lotacao(pai,filha) VALUES(?,?)";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, lprin);
			ps.setString(2, lsec);
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public List<LotacaoLotacao> getLotacoesAmarradas() throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<LotacaoLotacao> lotacoes = new ArrayList<LotacaoLotacao>();
		sql = "select id, pai, filha from lotacao_lotacao order by pai, filha";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				LotacaoLotacao ll = new LotacaoLotacao();
				ll.setId(rs.getInt("id"));
				Lotacao l = new Lotacao();
				l.setNome(rs.getString("pai"));
				ll.setPai(l);
				Lotacao l1 = new Lotacao();
				l1.setNome(rs.getString("filha"));
				ll.setFilha(l1);
				lotacoes.add(ll);
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return lotacoes;
	}
	
	public List<LotacaoLotacao> getLotacoesAmarradasPorPai(String nome) throws SQLException, ClassNotFoundException, NamingException{
		List<LotacaoLotacao> lotacoes = new ArrayList<LotacaoLotacao>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		if(nome.equals("SEÇÃO DE ATENDIMENTO") || nome.equals("SEÇÃO DE ATENDIMENTO ESPECIALIZADO"))
			sql = "select id, pai, filha from lotacao_lotacao where pai = (select pai from lotacao_lotacao where filha= ?) and filha = ? union select id, pai , pai AS filha from lotacao_lotacao where filha = ? order by pai, filha";
		else
			sql = "select id, pai, filha from lotacao_lotacao where pai = ? order by pai, filha";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, nome);
			if(nome.equals("SEÇÃO DE ATENDIMENTO") || nome.equals("SEÇÃO DE ATENDIMENTO ESPECIALIZADO")){
					ps.setString(2, nome);
					ps.setString(3, nome);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				LotacaoLotacao ll = new LotacaoLotacao();
				ll.setId(rs.getInt("id"));
				Lotacao l = new Lotacao();
				l.setNome(rs.getString("pai"));
				ll.setPai(l);
				Lotacao l1 = new Lotacao();
				l1.setNome(rs.getString("filha"));
				ll.setFilha(l1);
				lotacoes.add(ll);
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return lotacoes;
	}
	public boolean excluir(int id) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		
		sql = "DELETE FROM lotacao_lotacao WHERE id = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setInt(1, id);
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
}
