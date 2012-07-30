package controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import models.Usuario;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.agenda.miCuenta;
import views.html.agenda.*;

public class Cuenta extends Controller {
	
	public static class CambioPassword {        
        public String passOld;
        public String passNew;
        public String passNew2;           
    }

	private static ConexionJDBC conexion = ConexionJDBC.getInstancia();
	
	public static Result perfil() {
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(perfil.render(session("nombre"), session("email"), ""));
		}
	}
	
	public static Result password() {
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(password.render(session("nombre"), session("email"), "", "", "", "", ""));
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
	
	// Actualiza los datos de perfil del usuario, nombre e imagen.
	public static Result actualizaPerfil() throws SQLException, IOException {
		PreparedStatement st = null;
		String sql = "";
		String nombre = "";
		String fileName = "";
		String extension = "";

		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Usuario> formPerfil = form(Usuario.class).bindFromRequest();

			if(formPerfil.hasErrors()) {
	            return badRequest(perfil.render(session("nombre"), session("email"), ""));
			} else {
				Usuario user = formPerfil.get();
				nombre = user.nombre;
								
				MultipartFormData body = request().body().asMultipartFormData();
				FilePart picture = body.getFile("imagen");				
				try {
					Connection con = conexion.abre();
					
					if(picture != null) {
						String contentType = picture.getContentType(); 
					    File file = picture.getFile();
					    Long size = file.length();
					    
					    // comprueba que el tamaño de la imagen no supere 1 MB
					    if(size > 1000000) {
					    	return ok(perfil.render(session("nombre"), session("email"), "La imagen supera el limite"));
					    } else {
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
						    
						    
						    fileName = session("email") + extension;
						    String path = "./public/images/usuarios/" + session("email") + extension;
						    org.apache.commons.io.FileUtils.copyFile(file, new File(path));
						    
						    sql = "UPDATE usuario SET nombre = '"+nombre+"', imagen = '"+fileName+"' WHERE correo = '"+session("email")+"'";
							st = con.prepareStatement(sql);
							st.executeUpdate();
							session("nombre", nombre);
							return redirect (routes.Cuenta.perfil());
					    }
					    
					} else {
						sql = "UPDATE usuario SET nombre = '"+nombre+"' WHERE correo = '"+session("email")+"'";
						st = con.prepareStatement(sql);
						st.executeUpdate();
						session("nombre", nombre);
						return redirect (routes.Cuenta.perfil());
					}
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					conexion.cierra();
				}
				
			}
		}
		return ok();
	}
	
	// Actualiza la contraseña del usuario
	public static Result actualizaPassword() throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql = "";
		
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<CambioPassword> formPassword = form(CambioPassword.class).bindFromRequest();

			if(formPassword.hasErrors()) {
	            return badRequest(password.render(session("nombre"), session("email"), "", "", "", "", ""));
			} else {				
				CambioPassword claves = formPassword.get();
				
				try {
					Connection con = conexion.abre();
				
					sql = "SELECT password FROM usuario WHERE correo = '"+session("email")+"'";
					
					st = con.prepareStatement(sql);
					rs = st.executeQuery();
					
					rs.next();
					
					if(claves.passOld.equals(rs.getString("password"))) {
						
						if(claves.passNew.equals(claves.passNew2)) {
							sql = "UPDATE usuario SET password = '"+claves.passNew+"' WHERE correo = '"+session("email")+"'";
							st = con.prepareStatement(sql);
							st.executeUpdate();
							return redirect (routes.Cuenta.password());
						} else {
							return ok(password.render(session("nombre"), session("email"), "", "Las Contraseñas no coinciden", claves.passOld, claves.passNew, claves.passNew2));
						}													
					} else {
						return ok(password.render(session("nombre"), session("email"), "La contraseña es incorrecta", "", claves.passOld, claves.passNew, claves.passNew2));
					}
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					conexion.cierra();
				}				
			}
		}
		return ok();
	}

	public static boolean verificaSession() {
		if(session("email") == null) 
			return false;
		else
			return true;
	}
}