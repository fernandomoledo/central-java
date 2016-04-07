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

import javax.naming.NamingException;

import model.Chat;
import util.ConexaoMySQL;

public class ChatDAO {
	private String sql;
	
	public List<Chat> getLastChats() throws ClassNotFoundException, SQLException, NamingException{
		sql = "SELECT * FROM chat ORDER BY data_chat DESC LIMIT 5";
		List<Chat> chats = new ArrayList<Chat>();		
		
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
				Chat c = new Chat();
				c.setIdChat(rs.getInt("id_chat"));
				c.setTextoChat(rs.getString("texto_chat"));
				c.setUsuarioChat(rs.getString("usuario_chat"));
				c.setDataChat(rs.getTimestamp("data_chat"));
				chats.add(c);
		}
		
		Collections.reverse(chats);
		con.close();
		
		return chats;
	}
	
	public List<Chat> getChats() throws ClassNotFoundException, SQLException, NamingException{
		sql = "SELECT * FROM chat";
		List<Chat> chats = new ArrayList<Chat>();		
		
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
				Chat c = new Chat();
				c.setIdChat(rs.getInt("id_chat"));
				c.setTextoChat(rs.getString("texto_chat"));
				c.setUsuarioChat(rs.getString("usuario_chat"));
				c.setDataChat(rs.getTimestamp("data_chat"));
				chats.add(c);
		}
		
		con.close();
		
		return chats;
	}
	
	public boolean salvar(Chat c) throws ClassNotFoundException, SQLException, NamingException{
		sql = "INSERT INTO chat(texto_chat,usuario_chat) VALUES(?,?)";
		Connection con = null;
		con = ConexaoMySQL.abreConexao();
		PreparedStatement ps =con.prepareStatement(sql);
		ps.setString(1, c.getTextoChat());
		ps.setString(2, c.getUsuarioChat());
		ps.execute();
		con.close();
		return true;
	}
}
