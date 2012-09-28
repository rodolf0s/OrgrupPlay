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

import views.html.agenda.*;



public class Contactos extends Controller {

	public static ConexionJDBC conexion = ConexionJDBC.getInstancia();
	
	public static Result contactos(){
		
		List<Usuario> usuarioVacio = new ArrayList<Usuario>();
		try{
			return ok(contactos.render(Usuario.find.byId(session("email")), usuarioVacio, ""));	
		}catch(Exception e){
			
		}
		return ok();
	}
	
	public static Result buscaContactos() throws SQLException {
		
		List<Usuario> usuarioVacio = new ArrayList<Usuario>();
		
		Form<Usuario> formBuscaContactos = form(Usuario.class).bindFromRequest();
		
		if(formBuscaContactos.hasErrors()){
			return badRequest(contactos.render(Usuario.find.byId(session("email")), usuarioVacio, ""));
		} else{
			Usuario amigos = formBuscaContactos.get();
			return ok(contactos.render(Usuario.find.byId(session("email")), Usuario.listaUsuarios(amigos.nombre), amigos.nombre));
		}
	}
	
	public static Result agregaContacto(){
		
		List<Usuario> usuarioVacio = new ArrayList<Usuario>();
		
		Form<Contacto> formAgregaContacto = form(Contacto.class).bindFromRequest();
		
		if(formAgregaContacto.hasErrors()){
			return badRequest(contactos.render(Usuario.find.byId(session("email")), usuarioVacio, ""));
		}else{
			Contacto amigoEncontrado = formAgregaContacto.get();
//			return ok(amigoEncontrado.usuario1.toString());
			String a = amigoEncontrado.amigos;
			amigoEncontrado.amigos = "no";
			amigoEncontrado.save();
			try{
				return ok(contactos.render(Usuario.find.byId(session("email")), usuarioVacio, a));
			}catch(Exception e){
			
			}			
		}
		return ok("2222");
	}
	
	public static Result muestraAgregarContactos(){
		return ok(agregaContactos.render(Usuario.find.byId(session("email")), Contacto.listaContactosPendientes(session("email"))));
	}
	
	public static Result agregaContactoBd() {
		Form<Contacto> formAgregaContactoBd = form(Contacto.class).bindFromRequest();
		
		if(formAgregaContactoBd.hasErrors()){
			return badRequest(agregaContactos.render(Usuario.find.byId(session("email")), Contacto.listaContactosPendientes(session("email"))));
		}else{
			Contacto amigoGuardar = formAgregaContactoBd.get();
//			cambiaEstado(amigoGuardar.usuario1, amigoGuardar.usuario2);
			amigoGuardar.save();
			Contacto.cambiaEstado(amigoGuardar.usuario2.correo, amigoGuardar.usuario1.correo);
			try{
				return ok(agregaContactos.render(Usuario.find.byId(session("email")), Contacto.listaContactosPendientes(session("email"))));
			}catch(Exception e){
			
			}
		}
		return ok();
	}
	
	public static Result eliminaSolicitud() {
		Form<Contacto> formEliminaSolicitud = form(Contacto.class).bindFromRequest();
		
		if(formEliminaSolicitud.hasErrors()){
			return badRequest(agregaContactos.render(Usuario.find.byId(session("email")), Contacto.listaContactosPendientes(session("email"))));
		}else {
			Contacto solicitudEliminar = formEliminaSolicitud.get();
			Contacto.eliminaSolicitudAmistad(Contacto.obtieneId(solicitudEliminar.usuario1.correo, solicitudEliminar.usuario2.correo));
			try {
				return ok(agregaContactos.render(Usuario.find.byId(session("email")), Contacto.listaContactosPendientes(session("email"))));
			}catch(Exception e){
				
			}
		}
		return ok();
	}
	
	public static Result gestionaContactos() {
		return ok(gestionarContactos.render(Usuario.find.byId(session("email")), Contacto.listaAmigos(session("email"))));
	}
	
	public static Result eliminaContacto() {
		Form<Contacto> formEliminaContacto = form(Contacto.class).bindFromRequest();
		
		if(formEliminaContacto.hasErrors()){
			return ok(gestionarContactos.render(Usuario.find.byId(session("email")), Contacto.listaAmigos(session("email"))));
		}else {
			Contacto eliminaContacto = formEliminaContacto.get();
			Contacto.eliminaContacto(Contacto.obtieneId(eliminaContacto.usuario1.correo, eliminaContacto.usuario2.correo));
			Contacto.eliminaContacto(Contacto.obtieneId2(eliminaContacto.usuario1.correo, eliminaContacto.usuario2.correo));
			try{
				return ok(gestionarContactos.render(Usuario.find.byId(session("email")), Contacto.listaAmigos(session("email"))));
			}catch(Exception e){
				
			}
		}
		return ok();
	}
	
	public static Result muestraElPerfil(String email) {
		return ok(muestraPerfil.render(Usuario.find.byId(session("email")), Usuario.find.byId(email)));
	}
}