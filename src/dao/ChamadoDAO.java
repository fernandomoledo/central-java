package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import model.Andamento;
import model.Chamado;
import model.Lotacao;
import model.Portal;
import util.ConexaoOracle;

public class ChamadoDAO {
private String sql;
	
	public List<Andamento> getTodo(int secao) throws ClassNotFoundException, SQLException{
		sql = "SELECT CH.ID, CH.NUMERO, CH.LOTACAODESTINO, an.dt_andamento, AST.DESCRICAO, AN.TEXTO, SV.NOME, CH.STATUS, LT.NOME, CH.LOTACAOSOLICITANTE FROM CHAMADOS CH, ASSUNTOS AST, ANDAMENTOS AN, PORTAL PT, SERVIDORES SV, LOTACAO LT WHERE CH.ASSUNTO = AST.ID AND LT.ID = CH.LOTACAOSOLICITANTE AND CH.ID = AN.CHAMADO AND AN.CLASSIFICACAO = 'ABE' AND PT.ID(+) = CH.RESPONSAVEL AND SV.CODISERV(+) = SUBSTR(PT.CODIGO, 1, LENGTH(PT.CODIGO) - 2) AND CH.STATUS = 'AF' AND CH.LOTACAODESTINO = ? ORDER BY CH.ID, CH.LOTACAODESTINO, SV.NOME, AN.DT_ANDAMENTO";
		List<Andamento> andamentos = new ArrayList<Andamento>();	
		
			PreparedStatement ps = ConexaoOracle.abreConexao().prepareStatement(sql);
			ps.setInt(1, secao);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Andamento a = new Andamento();
				a.setDtAndamento(rs.getDate("DT_ANDAMENTO"));
				a.setTexto(rs.getString("TEXTO"));
				Chamado c = new Chamado();
				c.setId(rs.getLong("ID"));
				c.setStatus(rs.getString("STATUS"));
				c.setNumero(rs.getInt("NUMERO"));
				Lotacao l = new Lotacao();
				LotacaoDAO lotacaoDao = new LotacaoDAO();
				l = lotacaoDao.getLotacaoById(rs.getInt("LOTACAOSOLICITANTE"));
				c.setLotacaoSolicitante(l);
				a.setChamado(c);
				andamentos.add(a);
			}
		
		ConexaoOracle.fechaConexao();
		
		return andamentos;
	}
	
	public List<Andamento> getDoing(int secao) throws ClassNotFoundException, SQLException{
		sql = "SELECT CH.ID, CH.NUMERO, CH.LOTACAODESTINO, an.dt_andamento, AST.DESCRICAO, AN.TEXTO, SV.NOME, CH.STATUS, LT.NOME, CH.LOTACAOSOLICITANTE FROM CHAMADOS CH, ASSUNTOS AST, ANDAMENTOS AN, PORTAL PT, SERVIDORES SV, LOTACAO LT WHERE CH.ASSUNTO = AST.ID AND LT.ID = CH.LOTACAOSOLICITANTE AND CH.ID = AN.CHAMADO AND AN.CLASSIFICACAO = 'ABE' AND PT.ID(+) = CH.RESPONSAVEL AND SV.CODISERV(+) = SUBSTR(PT.CODIGO, 1, LENGTH(PT.CODIGO) - 2) AND CH.STATUS = 'AN' AND CH.LOTACAODESTINO = ? ORDER BY CH.ID, CH.LOTACAODESTINO, SV.NOME, AN.DT_ANDAMENTO";
		List<Andamento> andamentos = new ArrayList<Andamento>();	
		
			PreparedStatement ps = ConexaoOracle.abreConexao().prepareStatement(sql);
			ps.setInt(1, secao);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Andamento a = new Andamento();
				a.setDtAndamento(rs.getDate("DT_ANDAMENTO"));
				a.setTexto(rs.getString("TEXTO"));
				Chamado c = new Chamado();
				c.setId(rs.getLong("ID"));
				c.setStatus(rs.getString("STATUS"));
				c.setNumero(rs.getInt("NUMERO"));
				Lotacao l = new Lotacao();
				LotacaoDAO lotacaoDao = new LotacaoDAO();
				l = lotacaoDao.getLotacaoById(rs.getInt("LOTACAOSOLICITANTE"));
				c.setLotacaoSolicitante(l);
				a.setChamado(c);
				andamentos.add(a);
			}
		
		ConexaoOracle.fechaConexao();
		
		return andamentos;
	}
	
	public List<Andamento> getDone(int secao, int periodo) throws ClassNotFoundException, SQLException{
		sql = "SELECT CH.ID, CH.NUMERO, CH.LOTACAODESTINO, an.dt_andamento, AST.DESCRICAO, AN.TEXTO, SV.NOME, CH.STATUS, LT.NOME, CH.LOTACAOSOLICITANTE FROM CHAMADOS CH, ASSUNTOS AST, ANDAMENTOS AN, PORTAL PT, SERVIDORES SV, LOTACAO LT WHERE CH.ASSUNTO = AST.ID AND LT.ID = CH.LOTACAOSOLICITANTE AND CH.ID = AN.CHAMADO AND AN.CLASSIFICACAO = 'CON' AND trunc(AN.DT_ANDAMENTO) >= TRUNC(sysdate - ?) and PT.ID(+) = CH.RESPONSAVEL AND SV.CODISERV(+) = SUBSTR(PT.CODIGO, 1, LENGTH(PT.CODIGO) - 2) AND CH.STATUS = 'CO' AND CH.LOTACAODESTINO = ? ORDER BY CH.ID, CH.LOTACAODESTINO, SV.NOME, AN.DT_ANDAMENTO";
		List<Andamento> andamentos = new ArrayList<Andamento>();			
			PreparedStatement ps = ConexaoOracle.abreConexao().prepareStatement(sql);
			ps.setInt(1, periodo);
			ps.setInt(2, secao);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Andamento a = new Andamento();
				a.setDtAndamento(rs.getDate("DT_ANDAMENTO"));
				a.setTexto(rs.getString("TEXTO"));
				Chamado c = new Chamado();
				c.setId(rs.getLong("ID"));
				c.setStatus(rs.getString("STATUS"));
				c.setNumero(rs.getInt("NUMERO"));
				Lotacao l = new Lotacao();
				LotacaoDAO lotacaoDao = new LotacaoDAO();
				l = lotacaoDao.getLotacaoById(rs.getInt("LOTACAOSOLICITANTE"));
				c.setLotacaoSolicitante(l);
				a.setChamado(c);
				andamentos.add(a);
			}
		
		ConexaoOracle.fechaConexao();
		
		return andamentos;
	}
	
	public List<Chamado> getChamadosComTombo(String termo, String equipamento) throws ClassNotFoundException, SQLException{
		List<Chamado> chamados = new ArrayList<Chamado>();
		sql = " SELECT DISTINCT(c.id) as id, l.nome as nome, c.numero as numero FROM chamados c, andamentos a, lotacao l, chamado_tombo ct, tombos t WHERE contains (a.texto, ? ,1) > 0 AND "+      
	" UPPER(t.descricao) like ? AND "+
         " c.lotacaodestino in(187, 192, 631, 632, 633, 634, 635, 636, 637, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 1302, 904, 971, 898, 1308, 1309, 1317, 1319) AND "+
         " ct.tombo = t.id AND c.lotacaosolicitante = l.id AND c.id = ct.chamado AND c.id = a.chamado ORDER BY c.id DESC";
		PreparedStatement ps = ConexaoOracle.abreConexao().prepareStatement(sql);
		ps.setString(1, termo);
		ps.setString(2, "%"+equipamento+"%");
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			Chamado c = new Chamado();
			c.setId(rs.getLong(1));
			Lotacao l = new Lotacao();
			l.setNome(rs.getString(2));
			c.setLotacaoSolicitante(l);
			c.setNumero(rs.getInt(3));
			chamados.add(c);
		}
		System.out.println("CONSULTA getChamadosComTombo()");
		ConexaoOracle.fechaConexao();
		return chamados;
		
	}
	
	public List<Chamado> getChamadosSemTombo(String termo) throws ClassNotFoundException, SQLException{
		List<Chamado> chamados = new ArrayList<Chamado>();
		sql = "SELECT DISTINCT(c.id), l.nome, c.numero FROM chamados c, andamentos a, lotacao l WHERE contains (a.texto, ? ,1) > 0  AND "+
         " c.lotacaodestino in(187, 192, 631, 632, 633, 634, 635, 636, 637, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 1302, 904, 971, 898, 1308, 1309, 1317, 1319) AND "+
         " c.lotacaosolicitante = l.id AND c.id = a.chamado ORDER BY c.id DESC";
		PreparedStatement ps = ConexaoOracle.abreConexao().prepareStatement(sql);
		ps.setString(1, termo);
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			Chamado c = new Chamado();
			c.setId(rs.getLong(1));
			Lotacao l = new Lotacao();
			l.setNome(rs.getString(2));
			c.setLotacaoSolicitante(l);
			c.setNumero(rs.getInt(3));
			chamados.add(c);
		}
		System.out.println("CONSULTA getChamadosSemTombo()");
		ConexaoOracle.fechaConexao();
		return chamados;
		
	}
	
	
	public Andamento getInfoChamado(long id) throws SQLException, ClassNotFoundException{		
		sql = " SELECT c.id, c.numero, to_char(a.dt_andamento,'YYYY') as ano, a.dt_andamento, l.nome, to_char(a.texto) as texto "+
			" FROM chamados c, andamentos a, lotacao l WHERE c.id = a.chamado AND a.classificacao='ABE' AND c.lotacaosolicitante = l.id AND c.id = ?";
		PreparedStatement ps = ConexaoOracle.abreConexao().prepareStatement(sql);
		ps.setLong(1, id);
		ResultSet rs = ps.executeQuery();
		Andamento andamento = new Andamento();
		while(rs.next()){
			
			Chamado c = new Chamado();
			c.setNumero(rs.getInt("numero"));
			c.setId(rs.getLong("id"));
			Lotacao l = new Lotacao();
			l.setNome(rs.getString("nome"));
			c.setLotacaoSolicitante(l);
			andamento.setChamado(c);
			andamento.setDtAndamento(rs.getTimestamp("dt_andamento"));
			andamento.setTexto(rs.getString("texto"));
			
		}
		System.out.println("CONSULTA getInfoChamado()");
		ConexaoOracle.fechaConexao();
		return andamento;
	}
	
	public List<Chamado> getChamadosBuscaGeral(String termo) throws ClassNotFoundException, SQLException{
		List<Chamado> chamados = new ArrayList<Chamado>();
	 sql = "SELECT * FROM (SELECT distinct(c.id) as id, l.nome, c.numero FROM chamados c, chamado_tombo ct, tombos t, lotacao l WHERE (t.NRO_TOMBO = ? OR upper(t.descricao) like upper(?) OR upper(t.serie) like upper(?)) "+
            
           " AND c.lotacaodestino in(187, 192, 631, 632, 633, 634, 635, 636, 637, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 1302, 904, 971, 898, 1308, 1309, 1317, 1319) AND "+
          " c.id = ct.chamado AND ct.tombo = t.id AND c.lotacaosolicitante = l.id UNION ALL "+  
           
           " SELECT distinct(c.id) as id,l.nome, c.numero FROM chamados c, andamentos a, lotacao l "+
     " WHERE CONTAINS(to_char(a.texto),?,1) > 0  AND c.lotacaodestino in(187, 192, 631, 632, 633, 634, 635, 636, 637, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 1302, 904, 971, 898, 1308, 1309, 1317, 1319) AND "+
           " c.id = a.chamado AND c.lotacaosolicitante = l.id UNION ALL "+
           " SELECT distinct(c.id) as id,l.nome, c.numero  FROM  chamados c,  lotacao l WHERE (upper(l.nome) like upper(?) OR c.numero = ? ) AND "+
           " c.lotacaodestino in(187, 192, 631, 632, 633, 634, 635, 636, 637, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 1302, 904, 971, 898, 1308, 1309, 1317, 1319) AND "+
          " c.lotacaosolicitante = l.id) ORDER BY id DESC";
	 	PreparedStatement ps = ConexaoOracle.abreConexao().prepareStatement(sql);
	 	ps.setString(1, StringUtils.isNumeric(termo) ? termo : "");
	 	ps.setString(2, "%"+termo+"%");
	 	ps.setString(3, "%"+termo+"%");
	 	ps.setString(4, termo);
		ps.setString(5, "%"+termo+"%");
		ps.setString(6, StringUtils.isNumeric(termo) ? termo : "");
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			Chamado c = new Chamado();
			c.setId(rs.getLong(1));
			c.setNumero(rs.getInt(3));
			
			Lotacao l = new Lotacao();
			l.setNome(rs.getString(2));
			c.setLotacaoSolicitante(l);
			chamados.add(c);
			
		}
		ConexaoOracle.fechaConexao();
		return chamados;
	}
}
