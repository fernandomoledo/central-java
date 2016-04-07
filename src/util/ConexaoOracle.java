package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class ConexaoOracle {
	private static Connection con = null;
	
	public static Connection abreConexao() throws ClassNotFoundException, SQLException, NamingException{
		InitialContext context = new InitialContext();
		DataSource ds = (DataSource) context.lookup("java:comp/env/jdbc/central");
		try{
			con = ds.getConnection();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return con;
		
	}
	
	
}
