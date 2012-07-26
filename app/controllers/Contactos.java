package controllers;

import java.sql.ResultSet;

import models.Contacto;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.agenda.contactos;

public class Contactos extends Controller {

	public static ConexionJDBC conexion = ConexionJDBC.getInstancia();
	
	public static Result contactos(){
		return ok(contactos.render(session("nombre")));
	}
	
	public static Result buscaContactos(){
		String usuario1;
		String usuario2;
		ResultSet rs = null;
		String sql;
		
		Form<Contacto> formBuscaContactos = form(Contacto.class).bindFromRequest();
		
		if(formBuscaContactos.hasErrors()){
			return badRequest(contactos.render(session("nombre")));
		} else{
			Contacto amigos = formBuscaContactos.get();
			usuario2 = amigos.usuario_correo2;
			return ok();
		}
	}
}
