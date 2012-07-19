package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import models.Usuario;

import play.api.mvc.Session;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.*;

import views.html.*;
import views.html.agenda.*;

public class Agenda extends Controller {

	
	public static class ConfiguraCuenta {
        
        public String nombre;
        public String pass;
        public String passNew;
           
    }
	
	
	private static ConexionJDBC conexion = ConexionJDBC.getInstancia();

	public static Result index() throws SQLException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			
			return ok(contactos.render("Bienvenido", session("nombre")));
			
		}
	}
	
	// Configurar cuenta
	public static Result miCuenta() {
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(miCuenta.render(session("nombre"), session("email"), "", ""));
		}
	}
	
	public static Result configuraCuenta() throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql = "";
		
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<ConfiguraCuenta> formCuenta = form(ConfiguraCuenta.class).bindFromRequest();
			
			if(formCuenta.hasErrors()) {
	            return badRequest(login.render(""));
			} else {
				
				ConfiguraCuenta user = formCuenta.get();
				String nombre = user.nombre;
				String pass = user.pass;
				String passNew = user.passNew;
			
				try {
					Connection con = conexion.abre();
					
				
					if(pass == "" && passNew == "") {
						sql = "UPDATE usuario SET nombre = '"+nombre+"' WHERE correo = '"+session("email")+"'";
						st = con.prepareStatement(sql);
						st.execute(sql);
						session("nombre", nombre);
						return redirect (routes.Agenda.miCuenta());
					} else {
 						
						sql = "SELECT password FROM usuario WHERE correo = '"+session("email")+"'";
						st = con.prepareStatement(sql);
						rs = st.executeQuery();
						
						rs.next();
						if(pass.equals(rs.getString("password"))) {
							if(passNew != "") {
								sql = "UPDATE usuario SET nombre = '"+nombre+"', password = '"+passNew+"' WHERE correo = '"+session("email")+"'";
								st = con.prepareStatement(sql);
								st.execute(sql);
								session("nombre", nombre);
								return redirect (routes.Agenda.miCuenta());
							} else {
								return ok(miCuenta.render(session("nombre"), session("email"), "", "Ingrese una nueva Contraseña"));
							}								
						} else {
							return ok(miCuenta.render(session("nombre"), session("email"), "La contraseña es incorrecta", ""));
						}
					}
						
				}catch(Exception e){
					e.printStackTrace();
				}
				finally{
					conexion.cierra();
				}
				
			}
			return ok();
//			return ok(test.render(nombre, n, passNew, sql));
//			return ok(miCuenta.render(session("nombre"), session("email")));
		}
		
	}
	
	public static boolean verificaSession() {
//		user = session("email");
		if(session("email") == null) 
			return false;
		else
			return true;
	}
}
