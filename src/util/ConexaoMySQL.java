package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMySQL {
	private static final String URL = "jdbc:mysql://localhost:3306/central";
	private static final String USER = "central";
	private static final String PASS = "trt15";
	private static Connection con;
	
	public static Connection abreConexao(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(URL, USER, PASS);
			System.out.println("Conectado MySQL");
		}catch(SQLException e){
			System.out.println("Erro ao conectar ao MySQL: "+e.getMessage());
		}catch (ClassNotFoundException cnfe) {
			cnfe.getMessage();
		}
		return con;
	}
	
	public static void fechaConexao(){
		try{
			con.close();
			System.out.println("Fechando conexão com MySQL");
		}catch(SQLException ex){
			System.out.println("Erro ao fechar conexão com o MySQL: "+ex.getMessage());
		}
	}
}
