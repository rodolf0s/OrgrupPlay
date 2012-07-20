package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import models.Usuario;

import play.api.mvc.Session;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.*;

import views.html.*;
import views.html.agenda.*;

public class Agenda extends Controller {

	
	public static class ConfiguraCuenta {
        
        public String nombre;
        public String pass;
        public String passNew;
           
    }
	
	
	private static ConexionJDBC conexion = ConexionJDBC.getInstancia();

	public static Result index() throws SQLException {
		
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			
			return ok(contactos.render("Bienvenido", session("nombre")));
			
		}
	}
	
	
	public static boolean verificaSession() {
		if(session("email") == null) 
			return false;
		else
			return true;
	}
}
