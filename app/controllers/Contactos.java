package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Contacto;
import models.Usuario;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.agenda.contactos;

public class Contactos extends Controller {

	public static ConexionJDBC conexion = ConexionJDBC.getInstancia();
	
	public static Result contactos(){
		
		List<Usuario> usuarioVacio = new ArrayList<Usuario>();
		try{
			return ok(contactos.render(Usuario.find.byId(session("email")), usuarioVacio));	
		}catch(Exception e){
			
		}
		return ok();
	}
	
	public static Result buscaContactos() throws SQLException {
		
		List<Usuario> usuarioVacio = new ArrayList<Usuario>();
		
		Form<Usuario> formBuscaContactos = form(Usuario.class).bindFromRequest();
		
		if(formBuscaContactos.hasErrors()){
			return badRequest(contactos.render(Usuario.find.byId(session("email")), usuarioVacio));
		} else{
			Usuario amigos = formBuscaContactos.get();
			return ok(contactos.render(Usuario.find.byId(session("email")), Usuario.listaUsuarios(amigos.nombre)));
		}
	}
	
	public static Result agregaContacto(){
		
		List<Usuario> usuarioVacio = new ArrayList<Usuario>();
		
		Form<Contacto> formAgregaContacto = form(Contacto.class).bindFromRequest();
		
		if(formAgregaContacto.hasErrors()){
			return badRequest(contactos.render(Usuario.find.byId(session("email")), usuarioVacio));
		}else{
			Contacto amigoEncontrado = formAgregaContacto.get();
//			return ok(amigoEncontrado.usuario1.toString());
			amigoEncontrado.save();
			try{
				return ok(contactos.render(Usuario.find.byId(session("email")), usuarioVacio));
			}catch(Exception e){
			
			}			
		}
		return ok("2222");
	}
}