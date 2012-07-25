package controllers;

import java.io.File;
import java.io.IOException;
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
	public static Result configuraCuenta() throws SQLException, IOException {
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql = "";
		String nombre = "";
		String pass = "";
		String passNew = "";
		String extension = "";
		Boolean imagen = false;
		String filename = "";
		
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<ConfiguraCuenta> formCuenta = form(ConfiguraCuenta.class).bindFromRequest();
			
			if(formCuenta.hasErrors()) {
	            return badRequest(login.render(""));
			} else {
				
				ConfiguraCuenta user = formCuenta.get();
				nombre = user.nombre;
				pass = user.pass;
				passNew = user.passNew;				
				
				MultipartFormData body = request().body().asMultipartFormData();
				FilePart picture = body.getFile("imagen");
				
				// Verifica si el usuario cambia la imagen de su cuenta
				if (picture != null) {					
				    String contentType = picture.getContentType(); 
				    File file = picture.getFile();
				    
				    // verifica el tipo de imagen de la imagen a subir.
				    if(contentType.equals("image/png")) {
				    	extension = ".png";
				    }
				    else if(contentType.equals("image/jpeg")) {
				    	extension = ".jpg";
				    }
				    else if(contentType.equals("image/gif")) {
				    	extension = ".gif";
				    }
				    
				    imagen = true;				    
				    String path = "./public/images/usuarios/";
				    filename = path + session("email") + extension;
				    org.apache.commons.io.FileUtils.moveFile(file, new File(filename));
				} else {
					imagen = false;
				}
				
				try {
					Connection con = conexion.abre();
									
					if(pass.equals("") && passNew.equals("") && imagen == false) {
						sql = "UPDATE usuario SET nombre = '"+nombre+"' WHERE correo = '"+session("email")+"'";
						st = con.prepareStatement(sql);
						st.executeUpdate();
						session("nombre", nombre);
						return redirect (routes.Cuenta.miCuenta());
					} 
					else if(pass.equals("") && passNew.equals("") && imagen == true) {
						sql = "UPDATE usuario SET nombre = '"+nombre+"', imagen = '"+filename+"' WHERE correo = '"+session("email")+"'";
						st = con.prepareStatement(sql);
						st.executeUpdate();
						session("nombre", nombre);
						return redirect (routes.Cuenta.miCuenta());
					}
					else if(imagen == false) {
						sql = "SELECT password FROM usuario WHERE correo = '"+session("email")+"'";
						st = con.prepareStatement(sql);
						rs = st.executeQuery();
						
						rs.next();
						if(pass.equals(rs.getString("password"))) {
							if(passNew != "") {
								sql = "UPDATE usuario SET nombre = '"+nombre+"', password = '"+passNew+"' WHERE correo = '"+session("email")+"'";
								st = con.prepareStatement(sql);
								st.executeUpdate();
								session("nombre", nombre);
								return redirect (routes.Cuenta.miCuenta());
							} else {
								return ok(miCuenta.render(session("nombre"), session("email"), "", "Ingrese una nueva Contraseña"));
							}								
						} else {
							return ok(miCuenta.render(session("nombre"), session("email"), "La contraseña es incorrecta", ""));
						}
					}
					else {
 						
						sql = "SELECT password FROM usuario WHERE correo = '"+session("email")+"'";
						st = con.prepareStatement(sql);
						rs = st.executeQuery();
						
						rs.next();
						if(pass.equals(rs.getString("password"))) {
							if(passNew != "") {
								sql = "UPDATE usuario SET nombre = '"+nombre+"', password = '"+passNew+"', imagen = '"+filename+"' WHERE correo = '"+session("email")+"'";
								st = con.prepareStatement(sql);
								st.executeUpdate();
								session("nombre", nombre);
								return redirect (routes.Cuenta.miCuenta());
							} else {
								return ok(miCuenta.render(session("nombre"), session("email"), "", "Ingrese una nueva Contraseña"));
							}								
						} else {
							return ok(miCuenta.render(session("nombre"), session("email"), "La contraseña es incorrecta", ""));
						}
					}
						
				}catch(Exception e){
					e.printStackTrace();
				}
				finally{
					conexion.cierra();
				}
				
			}
			return ok();
			
//			return ok(nombre);
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