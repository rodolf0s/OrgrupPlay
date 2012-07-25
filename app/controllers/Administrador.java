package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import models.Admin;

import play.*;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Request;

import views.html.*;
import views.html.administrador.*;
import org.apache.commons.mail.*;

public class Administrador extends Controller {
	
	// Instanciamos la conexion
	private static ConexionJDBC conexion = ConexionJDBC.getInstancia();
		
	public static Result comprobarLogin() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		String usuario;
		String pass;
		String sql;
		ResultSet rs = null;
		
		Form<Admin> formLogin = form(Admin.class).bindFromRequest();
		
		if(formLogin.hasErrors()) {
            return badRequest(login.render(""));
		} else {			
			Admin user = formLogin.get();
			usuario = user.usuario;
			pass = user.password;
			
			try {				
				Connection con = conexion.abre();
				
				sql = "SELECT usuario FROM administrador WHERE usuario = '"+usuario+"' AND password = '"+pass+"' ";
				PreparedStatement st = con.prepareStatement(sql); 
				rs = st.executeQuery();
				
				rs.next();
				if(rs.getRow() == 1) {
										
					session("usuario", usuario);
										
					return redirect(routes.Administrador.mensaje());
				} else {
					
					return ok(login.render("Correo o contraseña incorrecta"));
				}
				
			} catch (Exception e){
				e.printStackTrace();
			}finally{
				if(rs != null)
					rs.close();
				}
			
		}
		return ok();
	}
	
	public static Result iniciar() {
		return ok(administrador.render());
	}
	
	public static Result mensaje() {
		return ok(index.render());
	}
}
