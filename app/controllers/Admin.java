package controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.util.StringTokenizer;

import models.Administrador;
import models.Mensaje;
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
		      } 
		      else {
		    	  LoginAdmin user = loginForm.get();
		    	  session ("usuario",user.usuario);
		    	  
		    	//Cuantas paginas de mensajes seran
					Integer cuentas = Correo.listaCorreos().size();
					
		    		  return ok(mensaje.render(Administrador.find.byId(session("usuario")),Correo.listaCorreos(),"",(cuentas/20)+1));
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
			
			//Cuantas paginas de mensajes seran
			Integer cuentas = Correo.listaCorreos().size();
			
		return ok(mensaje.render(Administrador.find.byId(session("usuario")),Correo.listaCorreos(),"",(cuentas/20)+1));
		}
	}
	
	//Leer los mensajes
	public static Result leerMensaje(Long id) {
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} 
				
		else{
		List<Correo> listaCorreo = new ArrayList<Correo>();
		Correo correo = new Correo();
		correo.setEstado(id);
		return ok(leermensaje.render(Administrador.find.byId(session("usuario")),Correo.muestraId(id),""));
		}
	}
	
	//Eliminar mensaje
	public static Result eliminarMensaje(Long id){
		Form<Correo> formElimina = form(Correo.class).bindFromRequest();
				
		if(formElimina.hasErrors()) {
			List<Correo> listaCorreo = new ArrayList<Correo>();
			return ok(leermensaje.render(Administrador.find.byId(session("usuario")),Correo.muestraId(id),""));
		} else {
					
			Correo correo = Correo.find.byId(id);
			correo.delete();
								
		}
		
		//Cuantas paginas de mensajes seran
		Integer cuentas = Correo.listaCorreos().size();
		return ok(mensaje.render(Administrador.find.byId(session("usuario")),Correo.listaCorreos(),"",(cuentas/20)+1));
				
				
			}
	
	//Eliminar varios mensajes
		public static Result eliminarVarios(){
			Form<Correo> formVarios= form(Correo.class).bindFromRequest();
					
			if(formVarios.hasErrors()) {
				List<Correo> listaCorreo = new ArrayList<Correo>();
				
				//Cuantas paginas de mensajes seran
				Integer cuentas = Correo.listaCorreos().size();
				
				return ok(mensaje.render(Administrador.find.byId(session("usuario")),Correo.listaCorreos(),"Error al eliminar",(cuentas/20)+1));
			} else {
						
				Correo correo = formVarios.get();
				separa(correo.id.toString());
						
			}
			
			//Cuantas paginas de mensajes seran
			Integer cuentas = Correo.listaCorreos().size();
			
			return ok(mensaje.render(Administrador.find.byId(session("usuario")),Correo.listaCorreos(),"",(cuentas/20)+1));
					
					
				}
	//Separa las id de los mensajes
		public static Result separa(String valor){
			StringTokenizer st = new StringTokenizer(valor, "00");
			while(st.hasMoreTokens()){
				String id = st.nextToken();
				Correo.eliminaMensaje(Long.parseLong(id));
			}
			return ok();	
		}
			
	//Redireccionar a responder
	public static Result responder(Long id) throws SQLException {
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} 
				
		else{
		Correo respuesta = Correo.find.byId(id);
		return ok(responder.render(respuesta,""));
		}
	}
	
	//Responder mensaje
	public static Result enviarRespuesta(Long id) throws IOException, EmailException {		
		Form<Correo> formRespuesta = form(Correo.class).bindFromRequest();
		
		if (formRespuesta.hasErrors()) {
			Correo respuesta = Correo.find.byId(id);
			return ok(responder.render(respuesta,"Problemas al enviar el correo"));
		} else {
			Correo respuesta = formRespuesta.get();
			
				// Envia un correo para responder el mensaje
				Email email = new SimpleEmail();
			    email.setSmtpPort(587);
			    email.setAuthenticator(new DefaultAuthenticator("orgrup.service@gmail.com", "orgrup2012"));
			    email.setDebug(false);
			    email.setHostName("smtp.gmail.com");
			    email.setFrom("orgrup.service@gmail.com");
			    email.setSubject(respuesta.asunto);
			    email.setMsg(respuesta.mensaje);
			    email.addTo(respuesta.correo);
			    email.setTLS(true);
			    email.send();
			    
			    respuesta.setRespuesta(id);
			    
			  //Cuantas paginas de mensajes seran
				Integer cuentas = Correo.listaCorreos().size();
				
			    return ok(mensaje.render(Administrador.find.byId(session("usuario")),Correo.listaCorreos(),"",(cuentas/20)+1));
			}		
		}
	
	
	//Redirecciona a la pagina cuentas
	public static Result cuentas() throws SQLException {
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} 
				
		else{
//			Date fecha = new Date();	
//			long [] dias;
//			int x = Usuario.listarUsuarios().size();
//			
//			for(int i=0; i < x; i++ ){
//				dias [i] = DateDiff("d",Usuario.listarUsuarios().get(i).inicioSesion , fecha);
//			}
			
			//Cuantas paginas de mensajes seran
			Integer  paginas = Correo.listaCorreos().size();
			
		return ok(cuentas.render(Administrador.find.byId(session("usuario")),Usuario.listarUsuarios(),(paginas/20)+1));
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
	
	
	
	//Desactivar la cuenta del usuario desde el administrador.
		
		public static Result adminElimina() {
			
			Form<Usuario> formCuenta = form(Usuario.class).bindFromRequest();

				if (formCuenta.hasErrors()) {
					return badRequest();
				} else {
					
					Usuario user = formCuenta.get();
					Usuario.bloquearCuenta(user.correo);
					
					//Cuantas paginas de mensajes seran
					Integer  paginas = Correo.listaCorreos().size();
					
				return ok(cuentas.render(Administrador.find.byId(session("usuario")),Usuario.listarUsuarios(),(paginas/20)+1));
				}
			}

	//Activar la cuenta del usuario desde el administrador.
		
		public static Result adminActiva(String correo) {
					
			Form<Usuario> formCuenta = form(Usuario.class).bindFromRequest();

				if (formCuenta.hasErrors()) {
					return badRequest();
				} else {
							
					Usuario.activarCuenta(correo);
							
					//Cuantas paginas de mensajes seran
					Integer  paginas = Correo.listaCorreos().size();
							
				return ok(cuentas.render(Administrador.find.byId(session("usuario")),Usuario.listarUsuarios(),(paginas/20)+1));
				}
			}
	
//valida la existencia de una sesion
	public static boolean verificaSession() {
		if(session("usuario") == null) 
			return false;
		else
			return true;
	}
}
