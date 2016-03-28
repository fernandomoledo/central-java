package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.ConexaoOracle;
import model.Tombo;

public class TomboDAO {
	private String sql;
	
	public List<Tombo> getTombosPorChamado(long id) throws ClassNotFoundException, SQLException{
		List<Tombo> tombos = new ArrayList<Tombo>();
		sql = " SELECT t.NRO_TOMBO, t.serie, t.dt_garantia, t.descricao from chamados c, chamado_tombo ct, tombos t  where c.id = ? and c.id = ct.chamado and ct.tombo = t.id";
		PreparedStatement ps = ConexaoOracle.abreConexao().prepareStatement(sql);
		ps.setLong(1, id);
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			Tombo t = new Tombo();
			t.setNroTombo(rs.getLong("NRO_TOMBO"));
			t.setSerie(rs.getString("serie"));
			t.setDtGarantia(rs.getTimestamp("dt_garantia"));
			t.setDescricao(rs.getString("descricao"));
			tombos.add(t);
		}
		System.out.println("CONSULTA getTombosPorChamado()");
		ConexaoOracle.fechaConexao();
		return tombos;
	}
}
