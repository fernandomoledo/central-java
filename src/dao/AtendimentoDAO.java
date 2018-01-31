package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

import util.ConexaoMySQL;
import util.ConexaoOracle;
import util.Utilidades;
import model.Atendente;
import model.Atendimento;
import model.Lotacao;
import model.LotacaoVisitaPrev;
import model.Sistema;
import model.Tombo;

public class AtendimentoDAO {
	private String sql;
	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	HttpSession session = (HttpSession) ec.getSession(false);
	String secao = session.getAttribute("secao").toString();
	
	
	public boolean salvar(Atendimento a) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "INSERT INTO atendimento(dt_cadastro,chamado,sistema,atendente,problema,solucao,encaminhamento,wiki) VALUES(?,?,?,?,?,?,?,?)";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1,Utilidades.formataData(a.getDtCadastro()));
			ps.setString(2, a.getChamado().trim().replace(" / ", "/").replace("/", " / "));
			ps.setInt(3, a.getSistema().getId());
			ps.setInt(4, a.getAtendente().getId());
			ps.setString(5, a.getProblema().trim());
			ps.setString(6, a.getSolucao());
			ps.setString(7, a.getEncaminhamento().trim());
			ps.setString(8, a.getWiki().trim());
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public boolean atualizar(Atendimento a) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "UPDATE atendimento set dt_cadastro = ?,chamado = ?,sistema = ? ,atendente = ?,problema = ?,solucao = ?,encaminhamento = ?,wiki = ? WHERE id = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1,Utilidades.formataData(a.getDtCadastro()));
			ps.setString(2, a.getChamado().trim().replace(" / ", "/").replace("/", " / "));
			ps.setInt(3, a.getSistema().getId());
			ps.setInt(4, a.getAtendente().getId());
			ps.setString(5, a.getProblema().trim());
			ps.setString(6, a.getSolucao());
			ps.setString(7, a.getEncaminhamento().trim());
			ps.setString(8, a.getWiki().trim());
			ps.setInt(9, a.getId());
			ps.execute();
			
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public boolean excluir(Atendimento a) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "DELETE FROM atendimento WHERE id = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setInt(1, a.getId());
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public List<Atendimento> listar() throws SQLException, ClassNotFoundException, NamingException{
		List<Atendimento> atend = new ArrayList<Atendimento>();
		
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT at.id, at.dt_cadastro, at.chamado, at.problema, at.solucao, at.encaminhamento, at.wiki, a.username as atendente, s.nome as sistema " + 
				" from atendimento at, atendente a, sistema s" + 
				" where at.sistema = s.id and at.atendente = a.id " + 
				" order by at.dt_cadastro desc, at.id desc";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				Atendimento at = new Atendimento();
				Atendente a = new Atendente();
				a.setNome(rs.getString("atendente"));
				Sistema s = new Sistema();
				s.setNome(rs.getString("sistema"));
				at.setId(rs.getInt("id"));
				at.setDtCadastro(Utilidades.formataData(rs.getString("dt_cadastro")));
				at.setChamado(rs.getString("chamado"));
				at.setProblema(rs.getString("problema"));
				at.setSolucao(rs.getString("solucao"));
				at.setEncaminhamento(rs.getString("encaminhamento"));
				at.setWiki(rs.getString("wiki"));
				at.setAtendente(a);
				at.setSistema(s);
				atend.add(at);
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return atend;
	}
	
	public Atendimento buscar(int id) throws SQLException, ClassNotFoundException, NamingException{
		Atendimento atend = new Atendimento();
		Atendente a = new Atendente();
		Sistema s = new Sistema();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "select a.id, a.dt_cadastro, a.chamado, a.sistema, a.atendente, a.problema, a.solucao, a.encaminhamento, a.wiki, u.nome, "
				+ "u.username, u.affected_user_id, u.assigned_user_id, s.nome as nm_sistema, s.ativo " + 
				" from atendimento a, atendente u, sistema s " + 
				" where a.id = ? and a.atendente = u.id and a.sistema = s.id";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while(rs.next()){		
				s.setId(rs.getInt("sistema"));
				s.setNome(rs.getString("nm_sistema"));
				a.setId(rs.getInt("atendente"));
				a.setNome(rs.getString("nome"));
				atend.setId(rs.getInt("id"));
				atend.setAtendente(a);
				atend.setSistema(s);
				atend.setChamado(rs.getString("chamado"));
				atend.setDtCadastro(Utilidades.formataData(rs.getString("dt_cadastro")));
				atend.setEncaminhamento(rs.getString("encaminhamento"));
				atend.setProblema(rs.getString("problema"));
				atend.setSolucao(rs.getString("solucao"));
				atend.setWiki(rs.getString("wiki"));
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return atend;
	}
	
	
	
}
