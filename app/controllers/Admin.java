package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import models.Administrador;
import models.Usuario;
import models.Correo;

import play.data.Form;
import play.mvc.*;
import play.data.*;
import play.*;

import views.html.*;
import views.html.home.*;
import views.html.administrar.*;

import org.apache.commons.mail.*;

public class Admin extends Controller {
	public static Result GO_HOME = redirect(
			routes.Admin.cuentas(0, "")
	);
	
	public static Result GO_HOME2 = redirect(
			routes.Admin.mensaje(0, "")
	);
	
	public static Result index() {
        return GO_HOME;
    }
	
	public static Result index2() {
        return GO_HOME2;
    }
	
	public static class CambioPass{
		public String old;
		public String password;
	}

	//Acceder a la base de datos	
	public static class LoginAdmin {
		public String usuario;
		public String password;
	        
		public String validate() {
			if (Administrador.authenticate(usuario, password) == null) {
				return "usuario o contraseña invalida";
			}
			return null;
		}        
	}
	
	 //Comprobrar datos del login administrador
	public static Result comprobarLogin() {
		Form<LoginAdmin> loginForm = form(LoginAdmin.class).bindFromRequest();
		if (loginForm.hasErrors()) {
			return badRequest(administrador.render(
					loginForm, 
					"Usuario o contraseña invalido"));
		} else {
			LoginAdmin user = loginForm.get();
			session ("usuario",user.usuario);
		    	  
			return ok(cambiarpass.render(""));
		}	          
	}
	
	//Redirecciona a login de administrador
	public static Result iniciar() {
		return ok(administrador.render(
				form(LoginAdmin.class), 
				""));
	}
	
	//Cerrar sesion administrador
	public static Result cerrarSesion() {
		session().clear();
		return redirect(routes.Application.index());
	}
	
	//Redirecciona a la pagina mensajes
	public static Result mensaje(int page, String filter) {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else{
			return ok(mensaje.render(
					Administrador.find.byId(session("usuario")),
					Correo.page(page, filter),
					filter,
					""));
		}
	}
	
	//Leer los mensajes
	public static Result leerMensaje(Long id) {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else{
			List<Correo> listaCorreo = new ArrayList<Correo>();
			Correo correo = new Correo();
			correo.setEstado(id);
			return ok(leermensaje.render(
					Administrador.find.byId(session("usuario")),
					Correo.muestraId(id),
					""));
		}
	}
	
	//Eliminar mensaje
	public static Result eliminarMensaje(Long id,int page, String filter) {
		Form<Correo> formElimina = form(Correo.class).bindFromRequest();
				
		if (formElimina.hasErrors()) {
			List<Correo> listaCorreo = new ArrayList<Correo>();
			return ok(leermensaje.render(
					Administrador.find.byId(session("usuario")),
					Correo.muestraId(id),
					"error"));
		} else {
			Correo correo = Correo.find.byId(id);
			correo.delete();
		}		
		return ok(mensaje.render(
				Administrador.find.byId(session("usuario")),
				Correo.page(page, filter),
				filter,
				""));
	}
	
	//Eliminar varios mensajes
	public static Result eliminarVarios(int page, String filter) {
		Form<Correo> formVarios= form(Correo.class).bindFromRequest();
		
		if (formVarios.hasErrors()) {
			List<Correo> listaCorreo = new ArrayList<Correo>();
			return ok(mensaje.render(
					Administrador.find.byId(session("usuario")),
					Correo.page(page, filter),
					filter,
					"Error al eliminar"));
		} else {
			
			Correo correo = formVarios.get();
			
			if (correo.id != null){
			separa(correo.id.toString());
			}
		}			
		return ok(mensaje.render(
				Administrador.find.byId(session("usuario")),
				Correo.page(page, filter),
				filter,
				""));
	}
	
	//Separa las id de los mensajes
	public static Result separa(String valor) {
		StringTokenizer st = new StringTokenizer(valor, "00");
		while (st.hasMoreTokens()) {
			String id = st.nextToken();
			Correo.eliminaMensaje(Long.parseLong(id));
		}
		return ok();	
	}
			
	//Redireccionar a responder
	public static Result responder(Long id) {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Correo respuesta = Correo.find.byId(id);
			return ok(responder.render(respuesta,""));
		}
	}
	
	//Responder mensaje
	public static Result enviarRespuesta(Long id, int page, String filter) throws IOException, EmailException {		
		Form<Correo> formRespuesta = form(Correo.class).bindFromRequest();
		if (formRespuesta.hasErrors()) {
			Correo respuesta = Correo.find.byId(id);
			return ok(responder.render(
					respuesta,
					"Problemas al enviar el correo"));
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
			    
			return ok(mensaje.render(
					Administrador.find.byId(session("usuario")),
					Correo.page(page, filter),
					filter,
					""));
		}		
	}	
	
	//Redirecciona a la pagina cuentas
	public static Result cuentas(int page, String filter) {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {

			return ok(cuentas.render(
					Administrador.find.byId(session("usuario")),
					Usuario.page(page, filter),
					filter,
					""));
		}
	}	
	
	//Redirecciona a cambiar contraseña
	public static Result cambiarPass() {		
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(cambiarpass.render(""));
		}
	}
	
	//Cambiar la contraseña
	public static Result cambioPass() {
		String usuario = session("usuario");					
		Form<CambioPass> formPass = form(CambioPass.class).bindFromRequest();
		
		if (formPass.hasErrors()) {
			return badRequest(cambiarpass.render("No se cambio la contraseña"));
		} else {
			CambioPass user = formPass.get();				
			/*
			 * verifica que la contraseña anterior sea la misma de la BD.
			 */
			if (Administrador.getPassword(session("usuario")).equals(user.old)) {
				Administrador admin = new Administrador();
				admin.setPassword(usuario, user.password);
			} else {
				return badRequest(cambiarpass.render("Contraseña incorrecta"));
			}
		}
		return ok(cambiarpass.render(""));
	}	
	
	//Bloquear la cuenta del usuario desde el administrador.
	public static Result adminElimina(int page, String filter) {
		Form<Usuario> formCuenta = form(Usuario.class).bindFromRequest();
		
		if (formCuenta.hasErrors()) {
			return ok(cuentas.render(
					Administrador.find.byId(session("usuario")),
					Usuario.page(page, filter),
					filter,
					"Error al Bloquear cuenta"));
		} else {
			Usuario user = formCuenta.get();
			Usuario.bloquearCuenta(user.correo);
									
			return ok(cuentas.render(
					Administrador.find.byId(session("usuario")),
					Usuario.page(page, filter),
					filter,
					""));
		}
	}

	//Activar la cuenta del usuario desde el administrador.
	public static Result adminActiva(String correo, int page, String filter) {
		Form<Usuario> formCuenta = form(Usuario.class).bindFromRequest();
		
		if (formCuenta.hasErrors()) {
			return ok(cuentas.render(
					Administrador.find.byId(session("usuario")),
					Usuario.page(page, filter),
					filter,
					"Error al Activar cuenta"));
		} else {
			Usuario.activarCuenta(correo);
		
			return ok(cuentas.render(
					Administrador.find.byId(session("usuario")),
					Usuario.page(page, filter),
					filter,
					""));
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