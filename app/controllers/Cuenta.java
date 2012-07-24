package controllers;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.login;
import views.html.agenda.miCuenta;

public class Cuenta extends Controller {
	
public static class ConfiguraCuenta {
        
        public String nombre;
        public String pass;
        public String passNew;
//        public String imagen;
           
    }

	private static ConexionJDBC conexion = ConexionJDBC.getInstancia();
	
	// Configurar cuenta
	public static Result miCuenta() {
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(miCuenta.render(session("nombre"), session("email"), "", ""));
		}
	}
	
	// Actualiza los datos del usuario
	public static Result configuraCuenta() throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql = "";
		
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<ConfiguraCuenta> formCuenta = form(ConfiguraCuenta.class).bindFromRequest();
			
			if(formCuenta.hasErrors()) {
	            return badRequest(login.render(""));
			} else {
				
				MultipartFormData body = request().body().asMultipartFormData();
				FilePart picture = body.getFile("imagen");
				
				/*
				 *  Recibe un archivo para subirlo al sv
				 *  Aun no esta completo
				 *  el archivo se sube a /tmp/
				 */
				if (picture != null) {
				    String fileName = picture.getFilename();
				    String contentType = picture.getContentType(); 
				    File file = picture.getFile();
				    String path = file.getAbsolutePath();
				    //org.apache.commons.io.FileUtils.copyFile(path, /home/chino)
				    return ok(path);
//				    return ok(prueba.render(fileName, contentType));
				} else {
//				    flash("error", "Missing file");
					return ok("NO");
//				    return ok(prueba.render("malo", "maloo"));    
				}
				
				
				
//				ConfiguraCuenta user = formCuenta.get();
//				String nombre = user.nombre;
//				String pass = user.pass;
//				String passNew = user.passNew;
//			
//				try {
//					Connection con = conexion.abre();
//					
//				
//					if(pass == "" && passNew == "") {
//						sql = "UPDATE usuario SET nombre = '"+nombre+"' WHERE correo = '"+session("email")+"'";
//						st = con.prepareStatement(sql);
//						st.executeUpdate();
//						
//						
////						st = con.prepareStatement(sql);
////						st.execute(sql);
//						session("nombre", nombre);
//						return redirect (routes.Cuenta.miCuenta());
//					} else {
// 						
//						sql = "SELECT password FROM usuario WHERE correo = '"+session("email")+"'";
//						st = con.prepareStatement(sql);
//						rs = st.executeQuery();
//						
//						rs.next();
//						if(pass.equals(rs.getString("password"))) {
//							if(passNew != "") {
//								sql = "UPDATE usuario SET nombre = '"+nombre+"', password = '"+passNew+"' WHERE correo = '"+session("email")+"'";
//								st = con.prepareStatement(sql);
//								st.executeUpdate();
//								
////								st = con.prepareStatement(sql);
////								st.execute(sql);
//								session("nombre", nombre);
//								return redirect (routes.Cuenta.miCuenta());
//							} else {
//								return ok(miCuenta.render(session("nombre"), session("email"), "", "Ingrese una nueva Contraseña"));
//							}								
//						} else {
//							return ok(miCuenta.render(session("nombre"), session("email"), "La contraseña es incorrecta", ""));
//						}
//					}
//						
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//				finally{
//					conexion.cierra();
//				}
//				
			}
//			return ok();
//			return ok(prueba.render(sql, "", "", ""));
//			return ok(miCuenta.render(session("nombre"), session("email")));
		}
		
	}

	public static boolean verificaSession() {
		if(session("email") == null) 
			return false;
		else
			return true;
	}
}
