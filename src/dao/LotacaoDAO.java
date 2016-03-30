package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Lotacao;
import model.LotacaoLotacao;
import util.ConexaoMySQL;
import util.ConexaoOracle;

public class LotacaoDAO {
	private String sql;
	
	public Lotacao getLotacaoById(int id) throws ClassNotFoundException, SQLException{
		sql = "SELECT ID, to_char(NOME) as NOME FROM lotacao WHERE id = ?";
		Lotacao l = new Lotacao();
		
		
			PreparedStatement ps = ConexaoOracle.abreConexao().prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				l.setId(rs.getInt("ID"));
				l.setNome(rs.getString("NOME"));
			}
			System.out.println("CONSULTA getLotacaoByID()");
			ConexaoOracle.fechaConexao();
			
		return l;
	}
	
	public Lotacao getLotacaoByName(String nome) throws ClassNotFoundException, SQLException{
		sql = "SELECT ID, to_char(NOME) as NOME FROM lotacao WHERE NOME = ?";
		Lotacao l = new Lotacao();
		
		
			PreparedStatement ps = ConexaoOracle.abreConexao().prepareStatement(sql);
			ps.setString(1, nome);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				l.setId(rs.getInt("ID"));
				l.setNome(rs.getString("NOME"));
			}
			System.out.println("CONSULTA getLotacaoByID()");
			ConexaoOracle.fechaConexao();
			
		return l;
	}
	
	public List<Lotacao> getLotacoes() throws SQLException, ClassNotFoundException{
		sql = "SELECT id, nome FROM lotacao ORDER BY nome";
		PreparedStatement ps = ConexaoOracle.abreConexao().prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		List<Lotacao> lotacoes = new ArrayList<Lotacao>();
		while(rs.next()){
			Lotacao l = new Lotacao();
			l.setId(rs.getInt("id"));
			l.setNome(rs.getString("nome"));
			lotacoes.add(l);
		}
		ConexaoOracle.fechaConexao();
		return lotacoes;
		
	}
	
	public boolean verificaLotacao(String nome) throws SQLException{
		sql = "select * from lotacao_lotacao where pai = ?";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
		ps.setString(1, nome);
		ResultSet rs = ps.executeQuery();
		int qtde = 0;
		while(rs.next()){
			qtde++;
		}
		ConexaoMySQL.fechaConexao();
		if(qtde >0) return true; return false;
	}
	
	public boolean salvar(String lprin, String lsec) throws SQLException{
		sql = "INSERT INTO lotacao_lotacao(pai,filha) VALUES(?,?)";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
		ps.setString(1, lprin);
		ps.setString(2, lsec);
		ps.execute();
		ConexaoMySQL.fechaConexao();
		return true;
	}
	
	public List<LotacaoLotacao> getLotacoesAmarradas() throws SQLException{
		List<LotacaoLotacao> lotacoes = new ArrayList<LotacaoLotacao>();
		sql = "select id, pai, filha from lotacao_lotacao order by pai, filha";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
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
		ConexaoMySQL.fechaConexao();
		return lotacoes;
	}
	
	public List<LotacaoLotacao> getLotacoesAmarradasPorPai(String nome) throws SQLException{
		List<LotacaoLotacao> lotacoes = new ArrayList<LotacaoLotacao>();
		sql = "select id, pai, filha from lotacao_lotacao where pai = ? order by pai, filha";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
		ps.setString(1, nome);
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
		ConexaoMySQL.fechaConexao();
		return lotacoes;
	}
	public boolean excluir(int id) throws SQLException{
		sql = "DELETE FROM lotacao_lotacao WHERE id = ?";
		PreparedStatement ps = ConexaoMySQL.abreConexao().prepareStatement(sql);
		ps.setInt(1, id);
		ps.execute();
		ConexaoMySQL.fechaConexao();
		return true;
	}
}
