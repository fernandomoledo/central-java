package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

import model.Atendente;
import model.Chat;
import model.Exclusao;
import model.Lotacao;
import model.LotacaoVisitaPrev;
import model.Tombo;
import model.Visita;
import util.ConexaoMySQL;
import util.ConexaoOracle;

public class VisitaPrevDAO {
	private String sql;
	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	HttpSession session = (HttpSession) ec.getSession(false);
	String secao = session.getAttribute("secao").toString();
	
	
	public boolean salvarLotacaoVP(LotacaoVisitaPrev l) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "INSERT INTO lotacao_visita(lotacao, andar, predio) VALUES(?,?,?)";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, l.getLotacao());
			ps.setInt(2, l.getAndar());
			ps.setString(3, l.getPredio());
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public boolean salvarVP(Visita v) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "INSERT INTO visita(periodo, id_atendente, chamado, lotacao) VALUES(?,?,?,?)";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, v.getPeriodo());
			ps.setInt(2, v.getAtendente().getId());
			ps.setString(3, v.getChamado());
			ps.setInt(4, v.getLotacao().getId());
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public boolean excluirLotacaoVP(int id) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "UPDATE lotacao_visita SET ativo = 'N' WHERE id = ?";
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
	
	public boolean atualizarLotacaoVP(LotacaoVisitaPrev l) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "UPDATE lotacao_visita SET lotacao = ?, andar = ?, predio = ?, ativo = ? WHERE id = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, l.getLotacao());
			ps.setInt(2, l.getAndar());
			ps.setString(3, l.getPredio());
			ps.setString(4, l.getAtivo());
			ps.setInt(5, l.getId());
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public LotacaoVisitaPrev getLotacaoVP(int id) throws SQLException, ClassNotFoundException, NamingException{
		LotacaoVisitaPrev l = new LotacaoVisitaPrev();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT * FROM lotacao_visita WHERE id = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while(rs.next()){				
				l.setId(rs.getInt("id"));
				l.setLotacao(rs.getString("lotacao"));
				l.setAndar(rs.getInt("andar"));
				l.setPredio(rs.getString("predio"));
				l.setAtivo(rs.getString("ativo"));
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return l;
	}
	
	public List<LotacaoVisitaPrev> listarLotacoesVP() throws SQLException, ClassNotFoundException, NamingException{
		List<LotacaoVisitaPrev> lotacoesVP = new ArrayList<LotacaoVisitaPrev>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT * FROM lotacao_visita ORDER BY ativo desc, predio DESC, andar DESC, lotacao ASC";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				LotacaoVisitaPrev l = new LotacaoVisitaPrev();
				l.setId(rs.getInt("id"));
				l.setLotacao(rs.getString("lotacao"));
				l.setAndar(rs.getInt("andar"));
				l.setPredio(rs.getString("predio"));
				l.setAtivo(rs.getString("ativo"));
				lotacoesVP.add(l);
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return lotacoesVP;
	}
	
	public List<LotacaoVisitaPrev> listarLotacoesVPAtrib(String periodo, int id) throws SQLException, ClassNotFoundException, NamingException{
		List<LotacaoVisitaPrev> lotacoesVP = new ArrayList<LotacaoVisitaPrev>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT distinct(l.id) id, l.predio predio, l.andar andar, l.lotacao lotacao FROM lotacao_visita l WHERE l.ativo = 'S' and (l.id not in(select lotacao from visita where periodo = ?) and l.id not in(select id_lotacao from exclusao where id_atendente = ?)) ORDER BY l.predio DESC, l.andar DESC, l.lotacao ASC ";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setString(1, periodo);
			ps.setInt(2, id);
			rs = ps.executeQuery();
			while(rs.next()){
				LotacaoVisitaPrev l = new LotacaoVisitaPrev();
				l.setId(rs.getInt("id"));
				l.setLotacao(rs.getString("lotacao"));
				l.setAndar(rs.getInt("andar"));
				l.setPredio(rs.getString("predio"));
				lotacoesVP.add(l);
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return lotacoesVP;
	}
	
	public List<LotacaoVisitaPrev> listarLotacoes() throws SQLException, ClassNotFoundException, NamingException{
		List<LotacaoVisitaPrev> lotacoesVP = new ArrayList<LotacaoVisitaPrev>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT * FROM lotacao_visita ORDER BY lotacao ASC";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				LotacaoVisitaPrev l = new LotacaoVisitaPrev();
				l.setId(rs.getInt("id"));
				l.setLotacao(rs.getString("lotacao"));
				l.setAndar(rs.getInt("andar"));
				l.setPredio(rs.getString("predio"));
				l.setAtivo(rs.getString("ativo"));
				lotacoesVP.add(l);
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return lotacoesVP;
	}
	
	public List<Visita> listarVisitas(int id) throws SQLException, ClassNotFoundException, NamingException{
		List<Visita> visitas = new ArrayList<Visita>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		if(id == -1)
			sql = "SELECT v.id id, a.nome nome, l.lotacao lotacao, l.andar andar, l.predio predio, v.periodo periodo, v.chamado chamado from atendente a, lotacao_visita l, visita v where v.id_atendente = a.id and v.lotacao = l.id order by a.nome, l.predio, l.andar, l.lotacao, v.chamado desc";
		else
			sql = "SELECT v.id id, a.nome nome, l.lotacao lotacao, l.andar andar, l.predio predio, v.periodo periodo, v.chamado chamado from atendente a, lotacao_visita l, visita v where v.id_atendente = ? and v.id_atendente = a.id and v.lotacao = l.id order by a.nome, l.predio, l.andar, l.lotacao, v.chamado desc";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			if(id != -1)
				ps.setInt(1, id);
			rs = ps.executeQuery();
			while(rs.next()){
				Visita v = new Visita();
				v.setId(rs.getInt("id"));
				Atendente a = new Atendente();
				a.setNome(rs.getString("nome"));
				v.setAtendente(a);
				LotacaoVisitaPrev l = new LotacaoVisitaPrev();
				l.setLotacao(rs.getString("lotacao"));
				l.setAndar(rs.getInt("andar"));
				l.setPredio(rs.getString("predio"));
				v.setLotacao(l);
				v.setPeriodo(rs.getString("periodo"));
				v.setChamado(rs.getString("chamado"));
				visitas.add(v);
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return visitas;
	
	}
	
	public boolean salvarExclusao(Exclusao e) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "INSERT INTO exclusao(id_atendente, id_lotacao) VALUES(?,?)";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setInt(1, e.getAtendente().getId());
			ps.setInt(2, e.getLotacao().getId());
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public boolean excluirExclusao(int id) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "DELETE FROM exclusao WHERE id = ?";
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
	
	public boolean salvarAtendente(Atendente a) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "INSERT INTO atendente(nome, affected_user_id, assigned_user_id, isAdm, username ) VALUES(?,?,?,?,?)";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, a.getNome());
			ps.setInt(2, a.getAffectedUserId());
			ps.setInt(3, a.getAssignedUserId());
			ps.setBoolean(4, a.isAdm());
			ps.setString(5, a.getUsername());
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public boolean atualizarAtendente(Atendente a) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "UPDATE atendente set nome=?, affected_user_id=?, assigned_user_id = ?, isAdm=?, username=? WHERE id = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps =con.prepareStatement(sql);
			ps.setString(1, a.getNome());
			ps.setInt(2, a.getAffectedUserId());
			ps.setInt(3, a.getAssignedUserId());
			ps.setBoolean(4, a.isAdm());
			ps.setString(5, a.getUsername());
			ps.setInt(6, a.getId());
			ps.execute();
		}finally{
			ps.close();
			con.close();
		}
		return true;
	}
	
	public boolean excluirAtendente(int id) throws ClassNotFoundException, SQLException, NamingException{
		Connection con = null;
		PreparedStatement ps = null;

		sql = "DELETE FROM atendente WHERE id = ?";
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
	public List<Atendente> listarAtendentes() throws SQLException, ClassNotFoundException, NamingException{
		List<Atendente> atendentes = new ArrayList<Atendente>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT * FROM atendente WHERE affected_user_id > 0 and assigned_user_id > 0 ORDER BY nome";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				Atendente a = new Atendente();
				a.setId(rs.getInt("id"));
				a.setNome(rs.getString("nome"));
				a.setAffectedUserId(rs.getInt("affected_user_id"));
				a.setAssignedUserId(rs.getInt("assigned_user_id"));
				a.setUsername(rs.getString("username"));
				a.setAdm(rs.getBoolean("isAdm"));
				atendentes.add(a);
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return atendentes;
	}
	
	public List<Atendente> listarTodosAtendentes() throws SQLException, ClassNotFoundException, NamingException{
		List<Atendente> atendentes = new ArrayList<Atendente>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT * FROM atendente ORDER BY nome";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				Atendente a = new Atendente();
				a.setId(rs.getInt("id"));
				a.setNome(rs.getString("nome"));
				a.setAffectedUserId(rs.getInt("affected_user_id"));
				a.setAssignedUserId(rs.getInt("assigned_user_id"));
				a.setUsername(rs.getString("username"));
				a.setAdm(rs.getBoolean("isAdm"));
				atendentes.add(a);
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return atendentes;
	}
	public Atendente buscaAtendente(int id) throws SQLException, ClassNotFoundException, NamingException{
		Atendente a = new Atendente();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT * FROM atendente WHERE id = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while(rs.next()){
				a.setId(rs.getInt("id"));
				a.setNome(rs.getString("nome"));
				a.setAffectedUserId(rs.getInt("affected_user_id"));
				a.setAssignedUserId(rs.getInt("assigned_user_id"));
				a.setUsername(rs.getString("username"));
				a.setAdm(rs.getBoolean("isAdm"));
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return a;
	}
	
	public Atendente buscaAtendente(String username) throws SQLException, ClassNotFoundException, NamingException{
		Atendente a = new Atendente();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT * FROM atendente WHERE username = ? or nome = ?";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, username);
			rs = ps.executeQuery();
			while(rs.next()){
				a.setId(rs.getInt("id"));
				a.setNome(rs.getString("nome"));
				a.setAffectedUserId(rs.getInt("affected_user_id"));
				a.setAssignedUserId(rs.getInt("assigned_user_id"));
				a.setAdm(rs.getBoolean("isAdm"));
				a.setUsername(rs.getString("username"));
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return a;
	}
	
	public List<Exclusao> listarExclusoes() throws SQLException, ClassNotFoundException, NamingException{
		List<Exclusao> exclusoes = new ArrayList<Exclusao>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "SELECT e.id id, l.lotacao lotacao, a.nome nome FROM exclusao e, atendente a, lotacao_visita l WHERE e.id_lotacao = l.id and e.id_atendente = a.id ORDER BY a.nome, l.lotacao";
		try{
			con = ConexaoMySQL.abreConexao();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				Exclusao e = new Exclusao();
				e.setId(rs.getInt("id"));
				Atendente a = new Atendente();
				a.setNome(rs.getString("nome"));
				LotacaoVisitaPrev l = new LotacaoVisitaPrev();
				l.setLotacao(rs.getString("lotacao"));
				e.setAtendente(a);
				e.setLotacao(l);
				exclusoes.add(e);
				
			}
		}finally{
			rs.close();
			ps.close();
			con.close();
		}
		return exclusoes;
	}
}
