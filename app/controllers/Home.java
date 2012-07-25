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

public class Home extends Controller {	
	
	private static ConexionJDBC conexion = ConexionJDBC.getInstancia();

	public static Result index() throws SQLException {
		
		ResultSet rs = null;


		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {


			// try {

			// 	Connection con = conexion.abre();
			// 	sql = "SELECT descripcion FROM tarea";
			// 	PreparedStatement st = con.prepareStatement(sql); 
			// 	rs = st.executeQuery();

			// 	rs.next();
				


			// }catch (Exception e){
			// 	e.printStackTrace();
			// }finally{
			// 	if(rs != null)
			// 		rs.close();
			// }
			
			return ok(home.render(session("nombre")));
			
		}
	}
	
	
	public static boolean verificaSession() {
		if(session("email") == null) 
			return false;
		else
			return true;
	}
}
