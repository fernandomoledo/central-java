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
import model.Assunto;
import model.Chamado;
import model.Portal;

public class AndamentoDAO {
	private String sql;
	
	public List<Andamento> getAndamentosPorChamado(long id) throws SQLException, ClassNotFoundException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Andamento> andamentos = new ArrayList<Andamento>();
		
		
			sql = "SELECT a.dt_andamento, a.classificacao, s.nome, to_char(a.texto) as texto "+
	          " from chamados c, andamentos a, portal p, servidores s where c.id = ? and c.id = a.chamado and a.usuario = p.id and "+
	          " substr(p.codigo,1,length(p.codigo)-2) = s.codiserv  ORDER BY a.dt_andamento ASC";
			try{
				con = ConexaoOracle.abreConexao();
				ps = con.prepareStatement(sql);
				ps.setLong(1, id);
				rs = ps.executeQuery();
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
			}finally{	
				rs.close();
				ps.close();
				con.close();
			}
		
		return andamentos;
	}
	
	public List<Andamento> getChamadosJIRA() throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Andamento> andamentos = new ArrayList<Andamento>();
		sql = "SELECT c.id, c.numero, a.dt_andamento, to_char(a.texto) as texto, an.descricao "+
		" FROM chamados c, andamentos a, assuntos an WHERE c.id = a.chamado and c.lotacaosolicitante = 1351 and c.assunto = an.id "+
				" and c.lotacaodestino = 639 and c.status='AF' "+
				" and upper(to_char(a.texto)) like 'PROJETO: PJE-JT%'";
		try{
			con = ConexaoOracle.abreConexao();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				Andamento a = new Andamento();
				a.setDtAndamento(rs.getTimestamp("dt_andamento"));
				a.setTexto(rs.getString("texto"));
				Chamado c = new Chamado();
				c.setId(rs.getLong("id"));
				c.setNumero(rs.getInt("numero"));
				Assunto as = new Assunto();
				as.setDescricao(rs.getString("descricao"));
				c.setAssunto(as);
				a.setChamado(c);
				andamentos.add(a);
			}
			System.out.println("CONSULTA NÚCLEO PJE-JT");
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
	
	return andamentos;
	}
	
	public Andamento getChamadoJIRA(long id) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Andamento a = new Andamento();
		sql = "SELECT c.id, c.numero, a.dt_andamento, to_char(a.texto) as texto, an.descricao "+
		" FROM chamados c, andamentos a, assuntos an WHERE c.id = ? and c.id = a.chamado  and c.lotacaosolicitante = 1351 and c.assunto = an.id "+
				//and a.classificacao = 'ABE' and c.status='AB' "+
				" and upper(to_char(a.texto)) like 'PROJETO: PJE-JT%'";
		try{
			con = ConexaoOracle.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setLong(1,id);
			rs = ps.executeQuery();
			while(rs.next()){
			
				a.setDtAndamento(rs.getTimestamp("dt_andamento"));
				a.setTexto(rs.getString("texto"));
				Chamado c = new Chamado();
				c.setId(rs.getLong("id"));
				c.setNumero(rs.getInt("numero"));
				Assunto as = new Assunto();
				as.setDescricao(rs.getString("descricao"));
				c.setAssunto(as);
				a.setChamado(c);
				
			}
			System.out.println("CONSULTA NÚCLEO PJE-JT");
		}finally{
	
			rs.close();
			ps.close();	
			con.close();
		}
	return a;
	}
}
