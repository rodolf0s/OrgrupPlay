package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Contacto;
import models.Notificaciones;
import models.Usuario;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.agenda.*;

public class Contactos extends Controller {
	
	public static Result GO_INDEX = redirect(
			routes.Contactos.gestionaContactos(0, "")	
	);
	
	public static Result index4() {
        return GO_INDEX;
    }
	
	public static Result gestionaContactos(int page, String filter) {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(
				gestionarContactos.render(
					Usuario.find.byId(session("email")),
					Contacto.page(page, filter, session("email")),
					filter
					)
				);
		}
	}
	
	public static Result buscaContactos(int page) {		
		List<Usuario> usuarioVacio = new ArrayList<Usuario>();		
		Form<Usuario> formBuscaContactos = form(Usuario.class).bindFromRequest();	
		
		if (formBuscaContactos.hasErrors()) {
			return badRequest(contactos.render(
					Usuario.find.byId(session("email")), 
					Usuario.page2(page, " ")
				)
			);
		} else {
			Usuario amigos = formBuscaContactos.get();
			String filter = amigos.nombre;
			if(filter != null) { 
				contactos(page, filter);
			}
			return ok(
					contactos.render(
						Usuario.find.byId(session("email")), 
						Usuario.page2(page, filter)	
					)
				);
			
		}
	}
	
	public static Result contactos(int page, String filter) {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			try {
				if(filter.isEmpty()) {
					filter = " ";
				}
				return ok(
						contactos.render(
							Usuario.find.byId(session("email")), 
							Usuario.page2(page, filter)
						)
					);
			} catch(Exception e) {}
			return ok();
		}
	}
	
	public static Result muestraMensajeContacto() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(views.html.mensajes.mensajeAgregaContacto.render(Usuario.find.byId(session("email"))));
		}
	}
	
	public static Result agregaContacto() {		
		List<Usuario> usuarioVacio = new ArrayList<Usuario>();		
		Form<Contacto> formAgregaContacto = form(Contacto.class).bindFromRequest();
		
		if (formAgregaContacto.hasErrors()) {
			return badRequest(contactos.render(
					Usuario.find.byId(session("email")), 
					Usuario.page2(0, " ")
				)
			);
		} else {
			Contacto amigoEncontrado = formAgregaContacto.get();
			amigoEncontrado.amigos = "no";
			if (Notificaciones.getContacto(amigoEncontrado.usuario2.correo))
				amigoEncontrado.notificado = "no";
			else
				amigoEncontrado.notificado = "si";
			amigoEncontrado.save();
			try {
				return redirect(routes.Contactos.muestraMensajeContacto());
			} catch(Exception e) {}			
		}
		return ok("2222");
	}
	
	public static Result muestraAgregarContactos() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(agregaContactos.render(
					Usuario.find.byId(session("email")), 
					Contacto.listaContactosPendientes(session("email"))));
		}
	}
	
	public static Result agregaContactoBd() {
		Form<Contacto> formAgregaContactoBd = form(Contacto.class).bindFromRequest();
		
		if (formAgregaContactoBd.hasErrors()) {
			return badRequest(agregaContactos.render(
					Usuario.find.byId(session("email")), 
					Contacto.listaContactosPendientes(session("email"))));
		} else {
			Contacto amigoGuardar = formAgregaContactoBd.get();
			amigoGuardar.notificado = "si";
			amigoGuardar.save();
			Contacto.cambiaEstado(amigoGuardar.usuario2.correo, amigoGuardar.usuario1.correo);
			try {
				return ok(agregaContactos.render(
						Usuario.find.byId(session("email")), 
						Contacto.listaContactosPendientes(session("email"))));
			} catch(Exception e) {}
		}
		return ok();
	}
	
	public static Result eliminaSolicitud() {
		Form<Contacto> formEliminaSolicitud = form(Contacto.class).bindFromRequest();
		
		if (formEliminaSolicitud.hasErrors()) {
			return badRequest(agregaContactos.render(
					Usuario.find.byId(session("email")), 
					Contacto.listaContactosPendientes(session("email"))));
		} else {
			Contacto solicitudEliminar = formEliminaSolicitud.get();
			Contacto.eliminaSolicitudAmistad(Contacto.obtieneId(
					solicitudEliminar.usuario1.correo, 
					solicitudEliminar.usuario2.correo));
			try {
				return ok(agregaContactos.render(
						Usuario.find.byId(session("email")), 
						Contacto.listaContactosPendientes(session("email"))));
			} catch(Exception e) {}
		}
		return ok();
	}
	
//	public static Result gestionaContactos() {
//		if (!verificaSession()) {
//			return redirect(routes.Application.index());
//		} else {
//			return ok(gestionarContactos.render(
//					Usuario.find.byId(session("email")), 
//					Contacto.listaAmigos(session("email"))));
//		}
//	}
	
	public static Result eliminaContacto(int page, String filter) {
		Form<Contacto> formEliminaContacto = form(Contacto.class).bindFromRequest();
		
		if (formEliminaContacto.hasErrors()) {
			return ok(gestionarContactos.render(
					Usuario.find.byId(session("email")), 
					Contacto.page(page, filter, session("email")),
					filter
					)
				);
		} else {
			Contacto eliminaContacto = formEliminaContacto.get();
			Contacto.eliminaContacto(Contacto.obtieneId(eliminaContacto.usuario1.correo, eliminaContacto.usuario2.correo));
			Contacto.eliminaContacto(Contacto.obtieneId2(eliminaContacto.usuario1.correo, eliminaContacto.usuario2.correo));
			try {
				return ok(gestionarContactos.render(
						Usuario.find.byId(session("email")), 
						Contacto.page(page, filter, session("email")),
						filter
					)
				);
			} catch(Exception e) {}
		}
		return ok();
	}
	
	public static Result muestraElPerfil(String email) {
		return ok(muestraPerfil.render(
				Usuario.find.byId(session("email")), 
				Usuario.find.byId(email)));
	}
	
	/**
	 * Comprueba la variable de session del usuario.
	 * 
	 * @return true si es distinta de null, y false si no a
	 * iniciado session. 
	 */
	public static boolean verificaSession() {
		if (session("email") == null) 
			return false;
		else
			return true;
	}
	
	
}
