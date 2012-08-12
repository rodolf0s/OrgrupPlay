package controllers;

import java.io.File;
import java.io.IOException;

import models.Usuario;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

import views.html.agenda.*;

public class Cuenta extends Controller {
	
	public static class CambioPassword {        
        public String passOld;
        public String passNew;
        public String passNew2;           
    }
	
	public static Result perfil() {
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(perfil.render(Usuario.find.byId(session("email")), ""));
		}
	}
	
	public static Result password() {
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(password.render(Usuario.find.byId(session("email")), "", "", "", "", "", ""));
		}
	}

	
	/*
	 * Actualiza el nombre del usuario y la imagen de la cuenta.
	 */
	public static Result actualizaPerfil() throws IOException {
		String fileName = "";
		String extension = "";

		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Usuario> formPerfil = form(Usuario.class).bindFromRequest();

			if(formPerfil.hasErrors()) {
				return ok(perfil.render(Usuario.find.byId(session("email")), ""));
			} else {
				Usuario user = formPerfil.get();
						
				/*
				 * obtiene la imagen de la vista perfil.
				 */
				MultipartFormData body = request().body().asMultipartFormData();
				FilePart picture = body.getFile("imagen");
				
				/*
				 * revisa si la imagen viene nula o no, si es distinto de null
				 * es porque el usuario actualizara su imagen.
				 */
				if(picture != null) {
					String contentType = picture.getContentType();
					File file = picture.getFile();
					
					/*
					 * si el tamaño de la imagen supera 1 MB, redirecciona a perfil
					 * notificando el error.
					 */
					if(file.length() > 1000000) {
						return ok(perfil.render(Usuario.find.byId(session("email")), "La imagen supera el limite"));
					} else {
						/*
						 * revisa que extension tiene la imagen subida por
						 * el usuario para agregarle la extension.
						 */
					    if(contentType.equals("image/png")) {
					    	extension = ".png";
					    }
					    else if(contentType.equals("image/jpeg")) {
					    	extension = ".jpg";
					    }
					    else if(contentType.equals("image/gif")) {
					    	extension = ".gif";
					    }
					    
					    /*
					     * crea el nombre del archivo con el correo del usuario mas la extension
					     * y luego sube la imagen y la guarda en el disco.
					     */
					    fileName = session("email") + extension;
					    String path = "./public/images/usuarios/" + session("email") + extension;
					    org.apache.commons.io.FileUtils.copyFile(file, new File(path));
					    
					    /*
					     * finalmente actualiza la imagen y redirecciona a perfil.
					     */
					    user.setImagen(session("email"), user.nombre, fileName);
					    return redirect (routes.Cuenta.perfil());
					}
					
				} else {
					user.setNombre(user.nombre, session("email"));
				    return redirect (routes.Cuenta.perfil());
				}			
			}
		}
	}
	
	// Actualiza la contraseña del usuario
	public static Result actualizaPassword() {
		
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<CambioPassword> formPassword = form(CambioPassword.class).bindFromRequest();

			if(formPassword.hasErrors()) {
				return ok(password.render(Usuario.find.byId(session("email")), "", "", "", "", "", ""));
			} else {				
				CambioPassword claves = formPassword.get();
				
				/*
				 * verifica que la contraseña anterior sea la misma de la BD.
				 */
				if(Usuario.getPassword(session("email")).equals(claves.passOld)) {
					
					/*
					 * comprueba que las nuevas contraseñas sean iguales.
					 */
					if(claves.passNew.equals(claves.passNew2)) {
						Usuario usuario = new Usuario();
						usuario.setPassword(session("email"), claves.passNew);
//						return redirect (routes.Cuenta.password());
						return ok(password.render(Usuario.find.byId(session("email")), "", "",  "", "", "", "Tu contraseña a sido cambiada"));
					} else {
						return ok(password.render(Usuario.find.byId(session("email")), "", "Las Contraseñas no coinciden", claves.passOld, claves.passNew, claves.passNew2, ""));
					}
					
				} else {
					return ok(password.render(Usuario.find.byId(session("email")), "La contraseña es incorrecta", "", claves.passOld, claves.passNew, claves.passNew2, ""));
				}
			}
		}
	}

	public static boolean verificaSession() {
		if(session("email") == null) 
			return false;
		else
			return true;
	}
}