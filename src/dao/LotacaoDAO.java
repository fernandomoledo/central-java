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
		sql = "SELECT ID, to_char(NOME) as NOME FROM lotacao WHERE id = ?";
		Lotacao l = new Lotacao();
		Connection con = null;
		con = ConexaoOracle.abreConexao();
		PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				l.setId(rs.getInt("ID"));
				l.setNome(rs.getString("NOME"));
			}
			System.out.println("CONSULTA getLotacaoByID()");
			con.close();
			
		return l;
	}
	
	public Lotacao getLotacaoByName(String nome) throws ClassNotFoundException, SQLException, NamingException{
		sql = "SELECT ID, to_char(NOME) as NOME FROM lotacao WHERE NOME = ?";
		Lotacao l = new Lotacao();
		
		
		Connection con = null;
		con = ConexaoOracle.abreConexao();
		PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, nome);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				l.setId(rs.getInt("ID"));
				l.setNome(rs.getString("NOME"));
			}
			System.out.println("CONSULTA getLotacaoByID()");
			con.close();
			
		return l;
	}
	
	public List<Lotacao> getLotacoes() throws SQLException, ClassNotFoundException, NamingException{
		sql = "SELECT id, nome FROM lotacao ORDER BY nome";
		Connection con = null;
		con = ConexaoOracle.abreConexao();
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		List<Lotacao> lotacoes = new ArrayList<Lotacao>();
		while(rs.next()){
			Lotacao l = new Lotacao();
			l.setId(rs.getInt("id"));
			l.setNome(rs.getString("nome"));
			lotacoes.add(l);
		}
		con.close();
		return lotacoes;
		
	}
	
	public boolean verificaLotacao(String nome) throws SQLException, ClassNotFoundException, NamingException{
		if(nome.equals("SEÇÃO DE ATENDIMENTO"))
			sql = "select * from lotacao_lotacao where pai = (select pai from lotacao_lotacao where filha = ?)";
		else
			sql = "select * from lotacao_lotacao where pai = ?";
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		ps.setString(1, nome);
		ResultSet rs = ps.executeQuery();
		int qtde = 0;
		while(rs.next()){
			qtde++;
		}
		con.close();
		if(qtde >0) return true; return false;
	}
	
	public boolean salvar(String lprin, String lsec) throws SQLException, ClassNotFoundException, NamingException{
		sql = "INSERT INTO lotacao_lotacao(pai,filha) VALUES(?,?)";
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		ps.setString(1, lprin);
		ps.setString(2, lsec);
		ps.execute();
		con.close();
		return true;
	}
	
	public List<LotacaoLotacao> getLotacoesAmarradas() throws SQLException, ClassNotFoundException, NamingException{
		List<LotacaoLotacao> lotacoes = new ArrayList<LotacaoLotacao>();
		sql = "select id, pai, filha from lotacao_lotacao order by pai, filha";
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
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
		con.close();
		return lotacoes;
	}
	
	public List<LotacaoLotacao> getLotacoesAmarradasPorPai(String nome) throws SQLException, ClassNotFoundException, NamingException{
		List<LotacaoLotacao> lotacoes = new ArrayList<LotacaoLotacao>();
		if(nome.equals("SEÇÃO DE ATENDIMENTO"))
			sql = "select id, pai, filha from lotacao_lotacao where pai = (select pai from lotacao_lotacao where filha= ?) and filha = ? union select id, pai , pai AS filha from lotacao_lotacao where filha = ? order by pai, filha";
		else
			sql = "select id, pai, filha from lotacao_lotacao where pai = ? order by pai, filha";
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		ps.setString(1, nome);
		if(nome.equals("SEÇÃO DE ATENDIMENTO")){
				ps.setString(2, nome);
				ps.setString(3, nome);
		}
		ResultSet rs = ps.executeQuery();
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
		con.close();
		return lotacoes;
	}
	public boolean excluir(int id) throws SQLException, ClassNotFoundException, NamingException{
		sql = "DELETE FROM lotacao_lotacao WHERE id = ?";
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		ps.setInt(1, id);
		ps.execute();
		con.close();
		return true;
	}
}
