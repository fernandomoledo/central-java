package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConexaoMySQL {
	
private static Connection con = null;
	
	public static Connection abreConexao() throws ClassNotFoundException, SQLException, NamingException{
		InitialContext context = new InitialContext();
		DataSource ds = (DataSource) context.lookup("java:comp/env/jdbc/central2");
		try{
			con = ds.getConnection();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return con;
		
	}
}
