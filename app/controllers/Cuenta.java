package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.avaje.ebean.SqlRow;

import models.Grupo;
import models.Usuario;
import models.Integrante;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

import views.html.cuenta.*;

public class Cuenta extends Controller {
	
	/**
	 * Clase para cambiar la password del usuario.
	 *
	 */
	public static class CambioPassword {        
        public String passOld;
        public String passNew;
        public String passNew2;           
    }
	
	/**
	 * Muestra la pagina de edicion del perfil.
	 * 
	 * @return la pagina perfil.scala.html con el usuario logeado.
	 */
	public static Result perfil() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(cuenta_perfil.render(
					Usuario.find.byId(session("email")), 
					""));
		}
	}
	
	/**
	 * Muestra la pagina de edicion de la password.
	 * 
	 * @return la pagina password.scala.html.
	 */
	public static Result password() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(cuenta_password.render(
					Usuario.find.byId(session("email")), 
					"", 
					"", 
					"", 
					"", 
					"", 
					""));
		}
	}
	
	/**
	 * Muestra la pagina de edicion de colores.
	 * 
	 * @return
	 */
	public static Result colores() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(cuenta_agenda.render(
					Usuario.find.byId(session("email")), 
					""));
		}
	}
	
	/**
	 * Muestra la pagina de desctivar cuenta.
	 * 
	 * @return
	 */
	public static Result cuenta() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(cuenta_desactivar.render(
					Usuario.find.byId(session("email")), 
					""));
		}
	}

	/**
	 * Actualiza el nombre del usuario y la imagen de la cuenta.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Result actualizaPerfil() throws IOException {
		String fileName = "";
		String extension = "";

		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Usuario> formPerfil = form(Usuario.class).bindFromRequest();

			if (formPerfil.hasErrors()) {
				return badRequest();
			} else {
				Usuario user = formPerfil.get();
						
				// Obtiene la imagen de la vista perfil.
				MultipartFormData body = request().body().asMultipartFormData();
				FilePart picture = body.getFile("imagen");
				
				// Revisa si la imagen viene nula o no, si es distinto de null
				// es porque el usuario actualizara su imagen.
				if (picture != null) {
					String contentType = picture.getContentType();
					File file = picture.getFile();
					
					// Si el tamaño de la imagen supera 1 MB, redirecciona a perfil
					// notificando el error.
					if (file.length() > 1000000) {
						return badRequest(cuenta_perfil.render(
								Usuario.find.byId(session("email")), 
								"La imagen supera el limite"));
					} else {
						
						// Revisa que extension tiene la imagen subida por
						// el usuario para agregarle la extension
						// y si sube una imagen y no un archivo.
					    if (contentType.equals("image/png"))
					    	extension = ".png";
					    else if (contentType.equals("image/jpeg"))
					    	extension = ".jpg";
					    else if (contentType.equals("image/gif"))
					    	extension = ".gif";
					    else
					    	return badRequest(cuenta_perfil.render(
					    			Usuario.find.byId(session("email")), 
					    			"Debe seleccionar una imagen"));
					    
					    // Crea el nombre del archivo con el correo del usuario mas la extension
					    // y luego sube la imagen y la guarda en el disco.
					    fileName = session("email") + extension;
					    String path = "./public/images/usuarios/" + session("email") + extension;
					    org.apache.commons.io.FileUtils.copyFile(file, new File(path));
					    
					    // finalmente actualiza la imagen y redirecciona a perfil.
					    user.setImagen(session("email"), user.nombre, fileName);
					    return redirect (routes.Cuenta.perfil());
					}					
				} else {
					user.setNombre(user.nombre, session("email"));
					user.setTelefono(session("email"), user.telefono);
					user.setLeyenda(session("email"), user.leyenda);
				    return redirect (routes.Cuenta.perfil());
				}			
			}
		}
	}
	
	/**
	 * Actualiza la password del usuario.
	 * 
	 * @return
	 */
	public static Result actualizaPassword() {		
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<CambioPassword> formPassword = form(CambioPassword.class).bindFromRequest();

			if (formPassword.hasErrors()) {
				return badRequest();
			} else {				
				CambioPassword claves = formPassword.get();
				
				// Verifica que la contraseña anterior sea la misma de la BD.
				if (Usuario.getPassword(session("email")).equals(claves.passOld)) {
					
					// Comprueba que las nuevas contraseñas sean iguales.
					if (claves.passNew.equals(claves.passNew2)) {
						Usuario usuario = new Usuario();
						usuario.setPassword(session("email"), claves.passNew);
						return ok(cuenta_password.render(Usuario.find.byId(session("email")), 
								"", 
								"",  
								"", 
								"", 
								"", 
								"Tu contraseña a sido cambiada"));
					} else {
						return ok(cuenta_password.render(Usuario.find.byId(session("email")), 
								"", 
								"Las Contraseñas no coinciden", 
								claves.passOld, 
								claves.passNew, 
								claves.passNew2, 
								""));
					}					
				} else {
					return ok(cuenta_password.render(Usuario.find.byId(session("email")), 
							"La contraseña es incorrecta", 
							"", 
							claves.passOld, 
							claves.passNew, 
							claves.passNew2, 
							""));
				}
			}
		}
	}
	
	/**
	 * Actualiza los colores de las tareas.
	 * 
	 * @return
	 */
	public static Result actualizaColores() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Usuario> formColores = form(Usuario.class).bindFromRequest();

			if (formColores.hasErrors()) {
				return badRequest();
			} else {	
				Usuario nuevoColores = formColores.get();
				nuevoColores.setColores(
						session("email"), 
						nuevoColores.colorTareaAlta, 
						nuevoColores.colorTareaMedia, 
						nuevoColores.colorTareaBaja);
				return redirect(routes.Cuenta.colores());
			}
		}
	}
	
	/**
	 * Desactiva la cuenta del usuario.
	 * 
	 * @return
	 */
	public static Result desactivarCuenta() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Usuario> formCuenta = form(Usuario.class).bindFromRequest();
			if (formCuenta.hasErrors()) {
				return badRequest();
			} else {
				List<SqlRow> grupos = Grupo.getGrupos(session("email"));
				
				// Comprueba que el usuario no sea admin de ningun grupo.
				for (int i = 0; i < grupos.size(); i++) {
					if (Integrante.esAdmin(Long.valueOf(grupos.get(i).getString("id")), session("email")))
						return badRequest(cuenta_desactivar.render(
								Usuario.find.byId(session("email")),
								"No puede desactivar su cuenta, porque es Administrador en un grupo"));
				}
				
				Usuario.desactivarCuenta(session("email"));
				Integrante.eliminaIntegrante(session("email"));
				session().clear();
				return ok(views.html.home.informaciones.render(
						"Su cuenta a sido desactivada satisfactoriamente.", 
						"Cuenta desactivada"));
			}
		}
	}

	/**
	 * El usuario activa su cuenta, cambiando el estado de Inactivo a Activada
	 * @return
	 */
	public static Result activarCuenta() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Usuario.activarCuenta(session("email"));
			session().clear();
			return ok(views.html.home.informaciones.render(
					"Su cuenta a sido activada satisfactoriamente.\nPor favor vuelva a iniciar sesion.", 
					"Cuenta desactivada"));
		}
	}

	/**
	 * Comprueba la variable de session del usuario.
	 * 
	 * @return true si es distinta de null, y false si no a
	 * iniciado session. 
	 */
	public static boolean verificaSession() {
		if (session("email") == null) 
			return false;
		else
			return true;
	}
}