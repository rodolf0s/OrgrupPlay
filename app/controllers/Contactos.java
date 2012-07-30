package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

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
		String sql = "";
		Connection con = null;

		//Creo un array
		ArrayList String = new ArrayList<String>();
		
		Form<Contacto> formBuscaContactos = form(Contacto.class).bindFromRequest();
		
		if(formBuscaContactos.hasErrors()){
			return badRequest(contactos.render(session("nombre")));
			
		} else{
			
			Contacto amigos = formBuscaContactos.get();
			usuario2 = amigos.usuario_correo2;
			
			try{
				con = conexion.abre();
				
				sql = "SELECT * FROM usuario WHERE usuario_correo2 = '"+usuario2+"'";
				PreparedStatement st = con.prepareStatement(sql);
				rs = st.executeQuery();
				
				while(rs.next()){
					String l = rs.getString("correo");
					l +=rs.getString("nombre");
				}
				
				return ok(contactos.render("l"));
				
//				if(rs.getRow() >= 1){
//					rs.getString("nombre");
//					return ok(contactos.render(l));
//				}
				
			}catch(Exception e){ 
				e.printStackTrace();
			}
			return ok();
		}
	}
}
