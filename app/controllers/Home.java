package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	// private static List<String> list = new ArrayList<String>();
	
	public static Result index() throws SQLException {
		
		ResultSet rs = null;


		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {

//			List<String> list = new ArrayList<String>();
//			list.add("Rodolfo");
//			list.add("Andres");
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
	
	public static String getImagen() throws SQLException {
		String imagen = "";
		
        try {
        	Connection con = conexion.abre();
        	
        	String sql = "SELECT imagen FROM usuario WHERE correo = '"+session("email")+"'";
			
        	PreparedStatement st = con.prepareStatement(sql);
        	ResultSet rs = st.executeQuery();
        	rs.next();
        	imagen = rs.getString("imagen");
        	
        } catch(Exception e) {
			e.printStackTrace();
		} finally {
			conexion.cierra();
		}
        return imagen;
    }

    public static String getCorreo() {
    	return session("email");
    }
	
	public static boolean verificaSession() {
		if(session("email") == null) 
			return false;
		else
			return true;
	}
}
