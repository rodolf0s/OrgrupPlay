package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Usuario;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.agenda.contactos;

public class Contactos extends Controller {

	public static ConexionJDBC conexion = ConexionJDBC.getInstancia();
	
	public static Result contactos(){
		
		List<String> hola = new ArrayList<String>();
		
		return ok(contactos.render(session("nombre"), hola));
	}
	
	public static Result buscaContactos() throws SQLException {
		String usuario1;
		String usuarioBusqueda;
		ResultSet rs = null;
		String sql = "";
		Connection con = null;

		//Creo un array
		List<String> lista = new ArrayList<String>();
		
		Form<Usuario> formBuscaContactos = form(Usuario.class).bindFromRequest();
		
		if(formBuscaContactos.hasErrors()){
//			return badRequest(contactos.render(session("nombre"), null));
			return ok("2");
			
		} else{
			
			Usuario amigos = formBuscaContactos.get();
			usuarioBusqueda = amigos.nombre;
			
			try{
				con = conexion.abre();
				
				sql = "SELECT * FROM usuario WHERE nombre = '"+usuarioBusqueda+"' OR correo = '"+usuarioBusqueda+"' AND nombre = '"+session("email")+"'";
				PreparedStatement st = con.prepareStatement(sql);
				rs = st.executeQuery();
				
				while(rs.next()){
					 lista.add(rs.getString("correo"));
					 lista.add(rs.getString("nombre"));					 
				}
				return ok(contactos.render(session("nombre"), lista));
				
//				return ok(lista.get(0).toString());
				
			}catch(Exception e){ 
				e.printStackTrace();
			}
			return ok("111");
		}
	}
}
