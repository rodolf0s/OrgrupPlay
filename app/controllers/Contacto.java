package controllers;

import java.sql.Connection;
import java.sql.ResultSet;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.*;
import views.html.agenda.*;

import models.Contacto;

public class Contacto extends Controller {

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
			Contacto nuevoContacto = formBuscaContactos.get();
			usuario2 = nuevoContacto.usuario_correo2;
			return ok();
		}
	}
}
