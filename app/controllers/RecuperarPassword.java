package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import models.Usuario;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.*;

import org.apache.commons.mail.*;

public class RecuperarPassword extends Controller{
	
	private static ConexionJDBC conexion = ConexionJDBC.getInstancia();
	
	public static Result index() {		
		return ok(olvidoPassword.render(""));
	}
	
	public static Result enviaDatos() throws SQLException {
		String correo;
		String sql;
		ResultSet rs = null;
		
		Form<Usuario> formLogin = form(Usuario.class).bindFromRequest();
		
		if(formLogin.hasErrors()) {
            return badRequest(login.render(""));
		} else {			
			Usuario user = formLogin.get();
			correo = user.correo;
			
			try {				
				Connection con = conexion.abre();
				
				sql = "SELECT password FROM usuario WHERE correo = '"+correo+"' ";
				PreparedStatement st = con.prepareStatement(sql); 
				rs = st.executeQuery();
				
				rs.next();
				if(rs.getRow() == 1) {
										
					// Envia un correo al usuario registrado para recuperar contraseña
					Email email = new SimpleEmail();
				    email.setSmtpPort(587);
				    email.setAuthenticator(new DefaultAuthenticator("orgrup.service@gmail.com", "orgrup2012"));
				    email.setDebug(false);
				    email.setHostName("smtp.gmail.com");
				    email.setFrom("orgrup.service@gmail.com");
				    email.setSubject("Recuperar Contraseña");
				    email.setMsg("Ud a solicitado su clave de ingreso, su clave es '"+rs.getString("password")+"'");
				    email.addTo(correo);
				    email.setTLS(true);
				    email.send();
										
					return redirect(routes.Application.index());
				} else {
					
					return ok(olvidoPassword.render("Correo incorrecto"));
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

}
