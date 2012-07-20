package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import models.Usuario;

import play.*;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Request;

import views.html.*;

import org.apache.commons.mail.*;

public class Application extends Controller {
	
	// Instanciamos la conexion
	private static ConexionJDBC conexion = ConexionJDBC.getInstancia();
  
	public static Result index() {
		return ok(index.render());
	}
	
	public static Result login() {
		return ok(login.render(""));
	}
	
	public static Result registro() {
		return ok(registro.render(""));
	}
	
	public static Result comprobarLogin() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		String correo;
		String pass;
		String sql;
		ResultSet rs = null;
		
		Form<Usuario> formLogin = form(Usuario.class).bindFromRequest();
		
		if(formLogin.hasErrors()) {
            return badRequest(login.render(""));
		} else {			
			Usuario user = formLogin.get();
			correo = user.correo;
			pass = user.pass;
			
			try {				
				Connection con = conexion.abre();
				
				sql = "SELECT nombre, correo FROM usuario WHERE correo = '"+correo+"' AND password = '"+pass+"' ";
				PreparedStatement st = con.prepareStatement(sql); 
				rs = st.executeQuery();
				
				rs.next();
				if(rs.getRow() == 1) {
										
					session("email", correo);
					session("nombre", rs.getString("nombre"));
										
					return redirect(routes.Agenda.index());
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
	
	public static Result comprobarRegistro() throws SQLException {
		String nombre;
		String correo;
		String pass;
		String sql = "";
		Connection con = null;
		
		Form<Usuario> formRegistro = form(Usuario.class).bindFromRequest();
		
		if(formRegistro.hasErrors()) {
            return badRequest(registro.render(""));
		} else {
			Usuario user = formRegistro.get();
			pass = user.pass;
			nombre = user.nombre;
			correo = user.correo;
			
			
			try {
				con = conexion.abre();				
				
				sql = "SELECT correo FROM usuario WHERE correo = '"+correo+"'";
				PreparedStatement st = con.prepareStatement(sql);
				ResultSet rs = st.executeQuery();
				
				rs.next();
				if(rs.getRow() >= 1) {
							
					return ok(registro.render("El correo ya existe"));
				} else {
					try {
						sql = "INSERT INTO usuario(correo, nombre, password) VALUES('"+correo+"', '"+nombre+"', '"+pass+"')";
						st = con.prepareStatement(sql);
						st.executeUpdate();
						
						// Envia un correo al usuario registrado
//						Email email = new SimpleEmail();
//					    email.setSmtpPort(587);
//					    email.setAuthenticator(new DefaultAuthenticator("orgrup.service@gmail.com", "orgrup2012"));
//					    email.setDebug(false);
//					    email.setHostName("smtp.gmail.com");
//					    email.setFrom("orgrup.service@gmail.com");
//					    email.setSubject("Gracias por Registrarse en Orgrup");
//					    email.setMsg("Se a registrado en Orgrup. Muchas gracias");
//					    email.addTo(correo);
//					    email.setTLS(true);
//					    email.send();
						
						return ok(registrado.render());
					} catch (Exception e) {
						e.printStackTrace();
						
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}finally{
				conexion.cierra();
			}			
		}
		return ok();
	}
	
	public static Result logout() {
		session().clear();
        return redirect(routes.Application.index());
	}
	
	public static Result about() {
        return ok(about.render());
	}
	
  
}