package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;


public class ConexaoOracle {
	private static final String URL = "jdbc:oracle:thin:@unanova:1521:orac";
	private static final String USER = "luizmoledo_cntatd";
	private static final String PASS = "frinfofrinfo";
	private static Connection con;
	
	public static Connection abreConexao() throws ClassNotFoundException, SQLException{
		Class.forName("oracle.jdbc.driver.OracleDriver");
		con = DriverManager.getConnection(URL, USER, PASS);
		System.out.println("Conectado Oracle");
		
		return con;
	}
	
	public static void fechaConexao(){
		try{
			con.close();
			System.out.println("Fechando conexão com Oracle");
		}catch(SQLException ex){
			System.out.println("Erro ao fechar conexão com o Oracle: "+ex.getMessage());
		}
	}
}
