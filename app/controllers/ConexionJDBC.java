package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionJDBC {

	private Connection conexion;
	private static ConexionJDBC instancia;

	private ConexionJDBC() {
		conexion = null;
	}

	public static ConexionJDBC getInstancia() {
		if(instancia == null) {
			instancia = new ConexionJDBC();
		}
		return instancia;
	}

	public Connection abre() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// Conexion para Mysql
//		Class.forName("com.mysql.jdbc.Driver").newInstance();
//		String url = "jdbc:mysql://localhost/prueba2";
//		conexion = DriverManager.getConnection(url, "root", "orgrup");
		
		Class.forName("org.postgresql.Driver").newInstance();
		String url = "jdbc:postgresql://localhost:5432/orgrupdb";
		conexion = DriverManager.getConnection(url, "orgrup", "orgrup");
		
		return conexion;
	}

	public void cierra() throws SQLException {
		conexion.close();
	}
}