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

public class Cuenta extends Controller {
	
	public static class CambioPassword {        
        public String passOld;
        public String passNew;
        public String passNew2;           
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
	
	public static String getPicture() {
        String imagen = "/images/usuarios/rodolfo.santander06@gmail.com.png";
        String imagen2 = "@routes.Assets.at('images/usuarios/rodolfo.santander06@gmail.com.png')";
        String imagen3 = "rodolfo.santander06@gmail.com.png";
        return imagen;
//        renderBinary(imagen);
//        response.setContentTypeIfNotSet(picture.image.type());
//        renderBinary(picture.image.get());
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
	            return badRequest(miCuenta.render(session("nombre"), session("email"), "", ""));
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
					    	return ok(miCuenta.render(session("nombre"), session("email"), "La imagen supera el limite", ""));
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
						    
						    String path = "./public/images/usuarios/";
						    fileName = path + session("email") + extension;
						    org.apache.commons.io.FileUtils.copyFile(file, new File(fileName));
						    
						    sql = "UPDATE usuario SET nombre = '"+nombre+"', imagen = '"+fileName+"' WHERE correo = '"+session("email")+"'";
							st = con.prepareStatement(sql);
							st.executeUpdate();
							session("nombre", nombre);
							return redirect (routes.Cuenta.miCuenta());
					    }
					    
					} else {
						sql = "UPDATE usuario SET nombre = '"+nombre+"' WHERE correo = '"+session("email")+"'";
						st = con.prepareStatement(sql);
						st.executeUpdate();
						session("nombre", nombre);
						return redirect (routes.Cuenta.miCuenta());
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
	            return badRequest(miCuenta.render(session("nombre"), session("email"), "", ""));
			} else {
				
				CambioPassword claves = formPassword.get();
				
				try {
					Connection con = conexion.abre();
					
					sql = "SELECT password FROM usuario WHERE correo = '"+session("email")+"'";
					st = con.prepareStatement(sql);
					rs = st.executeQuery();
					
					rs.next();
					if(claves.passOld.equals(rs.getString("password"))) {
						
						sql = "UPDATE usuario SET password = '"+claves.passNew+"' WHERE correo = '"+session("email")+"'";
						st = con.prepareStatement(sql);
						st.executeUpdate();
						return redirect (routes.Cuenta.miCuenta());							
					} else {
						return ok(miCuenta.render(session("nombre"), session("email"), "La contraseña es incorrecta", ""));
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
	
	// Actualiza los datos del usuario
//	public static Result configuraCuenta() throws SQLException, IOException {
//		PreparedStatement st = null;
//		ResultSet rs = null;
//		String sql = "";
//		String nombre = "";
//		String pass = "";
//		String passNew = "";
//		String extension = "";
//		Boolean imagen = false;
//		String filename = "";
//		
//		if(!verificaSession()) {
//			return redirect(routes.Application.index());
//		} else {
//			Form<ConfiguraCuenta> formCuenta = form(ConfiguraCuenta.class).bindFromRequest();
//			
//			if(formCuenta.hasErrors()) {
//	            return badRequest(login.render(""));
//			} else {
//				
//				ConfiguraCuenta user = formCuenta.get();
//				nombre = user.nombre;
//				pass = user.pass;
//				passNew = user.passNew;				
//				
//				MultipartFormData body = request().body().asMultipartFormData();
//				FilePart picture = body.getFile("imagen");
//				
//				// Verifica si el usuario cambia la imagen de su cuenta
//				if (picture != null) {					
//				    String contentType = picture.getContentType(); 
//				    File file = picture.getFile();
//				    
//				    // verifica el tipo de imagen de la imagen a subir.
//				    if(contentType.equals("image/png")) {
//				    	extension = ".png";
//				    }
//				    else if(contentType.equals("image/jpeg")) {
//				    	extension = ".jpg";
//				    }
//				    else if(contentType.equals("image/gif")) {
//				    	extension = ".gif";
//				    }
//				    
//				    imagen = true;				    
//				    String path = "./public/images/usuarios/";
//				    filename = path + session("email") + extension;
//				    org.apache.commons.io.FileUtils.moveFile(file, new File(filename));
//				} else {
//					imagen = false;
//				}
//				
//				try {
//					Connection con = conexion.abre();
//									
//					if(pass.equals("") && passNew.equals("") && imagen == false) {
//						sql = "UPDATE usuario SET nombre = '"+nombre+"' WHERE correo = '"+session("email")+"'";
//						st = con.prepareStatement(sql);
//						st.executeUpdate();
//						session("nombre", nombre);
//						return redirect (routes.Cuenta.miCuenta());
//					} 
//					else if(pass.equals("") && passNew.equals("") && imagen == true) {
//						sql = "UPDATE usuario SET nombre = '"+nombre+"', imagen = '"+filename+"' WHERE correo = '"+session("email")+"'";
//						st = con.prepareStatement(sql);
//						st.executeUpdate();
//						session("nombre", nombre);
//						return redirect (routes.Cuenta.miCuenta());
//					}
//					else if(imagen == false) {
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
//								session("nombre", nombre);
//								return redirect (routes.Cuenta.miCuenta());
//							} else {
//								return ok(miCuenta.render(session("nombre"), session("email"), "", "Ingrese una nueva Contraseña"));
//							}								
//						} else {
//							return ok(miCuenta.render(session("nombre"), session("email"), "La contraseña es incorrecta", ""));
//						}
//					}
//					else {
// 						
//						sql = "SELECT password FROM usuario WHERE correo = '"+session("email")+"'";
//						st = con.prepareStatement(sql);
//						rs = st.executeQuery();
//						
//						rs.next();
//						if(pass.equals(rs.getString("password"))) {
//							if(passNew != "") {
//								sql = "UPDATE usuario SET nombre = '"+nombre+"', password = '"+passNew+"', imagen = '"+filename+"' WHERE correo = '"+session("email")+"'";
//								st = con.prepareStatement(sql);
//								st.executeUpdate();
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
//			}
//			return ok();
//			
////			return ok(nombre);
////			return ok(miCuenta.render(session("nombre"), session("email")));
//		}
//		
//	}

	public static boolean verificaSession() {
		if(session("email") == null) 
			return false;
		else
			return true;
	}
}