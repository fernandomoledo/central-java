package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import util.ConexaoOracle;
import model.Andamento;
import model.Portal;

public class AndamentoDAO {
	private String sql;
	
	public List<Andamento> getAndamentosPorChamado(long id) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		List<Andamento> andamentos = new ArrayList<Andamento>();
		
		
			sql = "SELECT a.dt_andamento, a.classificacao, s.nome, to_char(a.texto) as texto "+
	          " from chamados c, andamentos a, portal p, servidores s where c.id = ? and c.id = a.chamado and a.usuario = p.id and "+
	          " substr(p.codigo,1,length(p.codigo)-2) = s.codiserv  ORDER BY a.dt_andamento ASC";
			
			con = ConexaoOracle.abreConexao();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Andamento a = new Andamento();
				a.setDtAndamento(rs.getTimestamp("dt_andamento"));
				a.setClassificacao(rs.getString("classificacao"));
				Portal p = new Portal();
				p.setNome(rs.getString("nome"));
				a.setUsuario(p);
				a.setTexto(rs.getString("texto"));
				andamentos.add(a);
			}
			System.out.println("CONSULTA getAndamentosPorChamado()");
	
			if(rs != null) rs.close();
			if(ps != null) ps.close();
		
			if(con != null) con.close();
		
		return andamentos;
	}
}
