package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;

import util.ConexaoOracle;
import model.Portal;


public class PortalDAO {
	private String sql;
	
	public Portal getLogin(String username) throws ClassNotFoundException, SQLException{
		sql = "SELECT * FROM portal WHERE login = ?";
		Portal p = null;
		
		
			PreparedStatement ps = ConexaoOracle.abreConexao().prepareStatement(sql);
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
		
		ConexaoOracle.fechaConexao();
		
		return p;
	}
}
