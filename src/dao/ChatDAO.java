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

import model.Chat;
import util.ConexaoMySQL;

public class ChatDAO {
	private String sql;
	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	HttpSession session = (HttpSession) ec.getSession(false);
	String secao = session.getAttribute("secao").toString();
	
	public List<Chat> getLastChats() throws ClassNotFoundException, SQLException, NamingException{
		sql = "SELECT * FROM chat WHERE secao is null OR secao = ? ORDER BY data_chat DESC LIMIT 5";
		List<Chat> chats = new ArrayList<Chat>();		
		
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		ps.setString(1, secao);
		
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
				Chat c = new Chat();
				c.setIdChat(rs.getInt("id_chat"));
				c.setTextoChat(rs.getString("texto_chat"));
				c.setUsuarioChat(rs.getString("usuario_chat"));
				c.setDataChat(rs.getTimestamp("data_chat"));
				chats.add(c);
		}
		
		//Collections.reverse(chats);
		if(rs != null) rs.close();
		if(ps != null) ps.close();
		if(con != null) con.close();
		
		return chats;
	}
	
	public List<Chat> getChats() throws ClassNotFoundException, SQLException, NamingException{
		sql = "SELECT * FROM chat WHERE secao is null OR secao = ? ";
		List<Chat> chats = new ArrayList<Chat>();		
		
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		ps.setString(1, secao);
		
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
				Chat c = new Chat();
				c.setIdChat(rs.getInt("id_chat"));
				c.setTextoChat(rs.getString("texto_chat"));
				c.setUsuarioChat(rs.getString("usuario_chat"));
				c.setDataChat(rs.getTimestamp("data_chat"));
				chats.add(c);
		}
		
		if(rs != null) rs.close();
		if(ps != null) ps.close();
		if(con != null) con.close();
		
		return chats;
	}
	
	public boolean salvar(Chat c) throws ClassNotFoundException, SQLException, NamingException{
		sql = "INSERT INTO chat(texto_chat,usuario_chat, secao) VALUES(?,?,?)";
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		ps.setString(1, c.getTextoChat());
		ps.setString(2, c.getUsuarioChat());
		ps.setString(3, c.getSecao());
		ps.execute();
		
		if(ps != null) ps.close();
		if(con != null) con.close();
		return true;
	}
}
