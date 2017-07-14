package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import util.ConexaoOracle;
import model.Lotacao;
import model.Tombo;

public class TomboDAO {
	private String sql;
	
	public List<Tombo> getTombosPorChamado(long id) throws ClassNotFoundException, SQLException, NamingException{
		List<Tombo> tombos = new ArrayList<Tombo>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = " SELECT t.NRO_TOMBO, t.serie, t.dt_garantia, t.descricao from chamados c, chamado_tombo ct, tombos t  where c.id = ? and c.id = ct.chamado and ct.tombo = t.id";
		try{
			con = ConexaoOracle.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setLong(1, id);
			rs = ps.executeQuery();
			while(rs.next()){
				Tombo t = new Tombo();
				t.setNroTombo(rs.getLong("NRO_TOMBO"));
				t.setSerie(rs.getString("serie"));
				t.setDtGarantia(rs.getTimestamp("dt_garantia"));
				t.setDescricao(rs.getString("descricao"));
				tombos.add(t);
			}
			System.out.println("CONSULTA getTombosPorChamado()");
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return tombos;
	}
	
	public Tombo getGarantia(String tombo) throws ClassNotFoundException, SQLException, NamingException{
		Tombo t = new Tombo();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT t.id, l.nome, t.NRO_TOMBO, t.serie, t.dt_garantia, t.descricao from tombos t , lotacao l where t.NRO_TOMBO = ? and t.LOTACAO = l.id";
		try{
			con = ConexaoOracle.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setString(1, tombo);
			rs = ps.executeQuery();
			while(rs.next()){
				t.setId(rs.getInt("id"));
				t.setNroTombo(rs.getLong("NRO_TOMBO"));
				t.setSerie(rs.getString("serie"));
				t.setDtGarantia(rs.getTimestamp("dt_garantia"));
				t.setDescricao(rs.getString("descricao"));
				Lotacao l = new Lotacao();
				l.setNome(rs.getString("nome"));
				t.setLotacao(l);
			}
			System.out.println("CONSULTA Garantia " + tombo);
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return t;
	}
	
	public List<Tombo> listarTombos() throws ClassNotFoundException, SQLException, NamingException{
		List<Tombo> tombos = new ArrayList<Tombo>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT t.id, l.nome, t.NRO_TOMBO, t.serie, t.dt_garantia, t.descricao from tombos t , lotacao l where t.LOTACAO = l.id and t.serie is not null and t.lotacao <> 676 and t.dt_garantia is not null order by t.NRO_TOMBO desc";
		try{
			con = ConexaoOracle.abreConexao();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				Tombo t = new Tombo();
				t.setId(rs.getInt("id"));
				t.setNroTombo(rs.getLong("NRO_TOMBO"));
				t.setSerie(rs.getString("serie"));
				t.setDtGarantia(rs.getTimestamp("dt_garantia"));
				t.setDescricao(rs.getString("descricao"));
				Lotacao l = new Lotacao();
				l.setNome(rs.getString("nome"));
				t.setLotacao(l);
				tombos.add(t);
			}
			System.out.println("CONSULTA listagem geral garantia");
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return tombos;
	}
}
