package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Lotacao;
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
}
