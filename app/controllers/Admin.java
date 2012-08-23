package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Administrador;
import models.Usuario;
import models.Correo;

import play.*;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Request;

import views.html.*;
import views.html.administrar.*;
import org.apache.commons.mail.*;

import controllers.Application.Login;

public class Admin extends Controller {
	
	//Crear clase para extraer contraseña antigua
	public static class CambioPass{
		public String old;
		public String password;
	}

	//Acceder a la base de datos	
	 public static class LoginAdmin {
	        
	        public String usuario;
	        public String password;
	        
	        public String validate() {
	            if(Administrador.authenticate(usuario, password) == null) {
	                return "usuario o contraseña invalida";
	            }
	            return null;
	        }        
	    }
	
	 //Comprobrar datos del login administrador
		public static Result comprobarLogin() {
			
		      Form<LoginAdmin> loginForm = form(LoginAdmin.class).bindFromRequest();
		      
		      if(loginForm.hasErrors()) {
		          return badRequest(administrador.render(loginForm, "Usuario o contraseña invalido"));
		      } else {
		    	  LoginAdmin user = loginForm.get();
		    	  session ("usuario",user.usuario);
		    		  return ok(mensaje.render(Administrador.find.byId(session("usuario")),Correo.listaCorreos()));
		    	  }	          
		      }
	
	//Redirecciona a login de administrador
	public static Result iniciar() {
		return ok(administrador.render(form(LoginAdmin.class), ""));
	}
	
	//Cerrar sesion administrador
	public static Result cerrarSesion() {
		session().clear();
		return redirect(routes.Application.index());
	}
	
	//Redirecciona a la pagina mensajes

	public static Result mensaje() throws SQLException {
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} 
		
		else{
		List<Correo> listaCorreo = new ArrayList<Correo>();
	
		return ok(mensaje.render(Administrador.find.byId(session("usuario")),Correo.listaCorreos()));
		}
	}

	//Redirecciona a la pagina cuentas
	public static Result cuentas() {
		

		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} 
		
		else {
		return ok(cuentas.render());
		}
	}
	
	
	//Redirecciona a cambiar contraseña
	public static Result cambiarPass() {
		
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} 
		
		else {
		return ok(cambiarpass.render(""));
		}
	}
	
	//Cambiar la contraseña
		public static Result cambioPass(){
			String usuario = session("usuario");
						
			Form<CambioPass> formPass = form(CambioPass.class).bindFromRequest();
			
			if(formPass.hasErrors()) {
	            return badRequest(cambiarpass.render("No se cambio la contraseña"));
			} else {
				CambioPass user = formPass.get();
				
				/*
				 * verifica que la contraseña anterior sea la misma de la BD.
				 */
				if(Administrador.getPassword(session("usuario")).equals(user.old)) {
							
				Administrador admin = new Administrador();
				admin.setPassword(usuario, user.password);
					
				}
				else{
					return badRequest(cambiarpass.render("Contraseña incorrecta"));
				}
			}
			return ok(cambiarpass.render(""));
		}
		
	
//valida la existencia de una sesion
	public static boolean verificaSession() {
		if(session("usuario") == null) 
			return false;
		else
			return true;
	}
}
