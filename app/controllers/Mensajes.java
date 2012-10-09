package controllers;

import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;

import models.Contacto;
import models.Mensaje;
import models.Usuario;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.mensajes.*;

public class Mensajes extends Controller {
	
	public static Result mensajesRecibidos() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(mensajesRecibidos.render(
					Usuario.find.byId(session("email")), 
					Mensaje.listaMensajesRecibidos(session("email"))));
		}
	}
	
	public static Result mensajesEnviados() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(mensajesEnviados.render(
					Usuario.find.byId(session("email")), 
					Mensaje.listaMensajesEnviados(Usuario.find.byId(session("email")))));
		}
	}
	
	public static Result crearMensaje() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(crearMensaje.render(
					Usuario.find.byId(session("email")), 
					Contacto.listaAmigos(session("email"))));
		}
	}
	
	public static Result leerMensaje(Long id) {
		Mensaje.cambiaEstadoLeido(id, session("email"));
		return ok(muestraMensaje.render(
				Usuario.find.byId(session("email")), 
				Mensaje.muestraId(id)));
	}
	
	public static Result enviaMensaje() throws ParseException {
		Form<Mensaje> mensajeForm = form(Mensaje.class).bindFromRequest();
		
		if (mensajeForm.hasErrors()) {
			return badRequest();
		} else {
			Mensaje mensaje = mensajeForm.get();
			Date fecha2 = new Date();
			mensaje.fecha = fecha2;
			mensaje.save();
			Mensaje.copiaMensaje(
					mensaje.fecha,
					mensaje.remitente, 
					mensaje.destinatario, 
					mensaje.asunto, 
					mensaje.mensaje, 
					mensaje.leido);
			return ok(crearMensaje.render(
					Usuario.find.byId(session("email")), 
					Contacto.listaAmigos(session("email"))));
		}
	}
	
	public static Result enviaMensajeModal(String email) throws ParseException {
		Form<Mensaje> mensajeForm2 = form(Mensaje.class).bindFromRequest();
		
		if (mensajeForm2.hasErrors()) {
			return badRequest();
		} else {
			Mensaje mensaje = mensajeForm2.get();
			Date fecha2 = new Date();
			mensaje.fecha = fecha2;
			mensaje.save();
			Mensaje.copiaMensaje(
					mensaje.fecha, 
					mensaje.remitente, 
					mensaje.destinatario, 
					mensaje.asunto, 
					mensaje.mensaje, 
					mensaje.leido);
			return ok(views.html.agenda.muestraPerfil.render(
					Usuario.find.byId(session("email")), 
					Usuario.find.byId(email)));
		}
	}
	
	public static Result mensajesNuevos() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(mensajesNuevos.render(
					Usuario.find.byId(session("email")), 
					Mensaje.mensajesNuevosRecibidos(session("email"))));
		}
	}
	
	public static Result eliminaMensajes() {		
		Form<Mensaje> mensajeAEliminar = form(Mensaje.class).bindFromRequest();
		
		if (mensajeAEliminar.hasErrors()) {
			return badRequest();
		} else {
			Mensaje mensaje = mensajeAEliminar.get();
			separa(mensaje.id.toString());
		}
		return ok(mensajesRecibidos.render(
				Usuario.find.byId(session("email")), 
				Mensaje.listaMensajesRecibidos(session("email"))));
	}
	
	public static Result separa(String valor) {
		StringTokenizer st = new StringTokenizer(valor, "00");
		while (st.hasMoreTokens()) {
			String id = st.nextToken();
			Mensaje.eliminaMensaje(Long.parseLong(id));
		}
		return ok();	
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