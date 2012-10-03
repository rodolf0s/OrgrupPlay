package controllers;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.avaje.ebean.Ebean;

import views.html.agenda.home;
import views.html.grupo.*;

import models.Archivo;
import models.Contacto;
import models.Grupo;
import models.Integrante;
import models.Reunion;
import models.Tarea;
import models.Usuario;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

public class Grupos extends Controller {

	/**
	 * clase para crear un nuevo grupo en la vista "/grupo/?"
	 *
	 */
	public static class Group {
		public String nombre;
		public String descripcion;
		public String imagen;
		public Long grupoId;
	}

	/**
	 * Muestra la pagina principal de un grupo.
	 * 
	 * @param id
	 * @return
	 */
	public static Result index(Long id) {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			// verifica que el usuario pertenesca al grupo. haciendo un INNER JOIN grupo con integrante
			// si no pertenece al grupo lo redirecciona al home "/App"
			if (Grupo.getGrupo(session("email"), id) != null) {
				return ok(views.html.grupo.grupo.render(
						Usuario.find.byId(session("email")),
						Grupo.getGrupo(session("email"), id),
						Grupo.getGrupos(session("email")),
						Integrante.find.where().eq("grupo_id", id).findList(),
						Contacto.listaAmigos(session("email")),
						""
						));
			} else {
				return redirect(routes.Home.index());
			}
		}			
	}
	
	/**
	 * Muestra las reuniones del grupo.
	 * 
	 * @param id
	 * @return
	 */
	public static Result muestraReuniones(Long id) {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			// verifica que el usuario pertenesca al grupo. haciendo un INNER JOIN grupo con integrante
			// si no pertenece al grupo lo redirecciona al home "/App"
			if (Grupo.getGrupo(session("email"), id) != null) {
				return ok(views.html.grupo.grupo_reuniones.render(
						Usuario.find.byId(session("email")),
						Grupo.getGrupo(session("email"), id),
						Grupo.getGrupos(session("email")),
						Integrante.find.where().eq("grupo_id", id).findList(),
						Contacto.listaAmigos(session("email")),
						Reunion.find.where().eq("grupo_id", id).findList(),
						""
						));
			} else {
				return redirect(routes.Home.index());
			}
		}	
	}
	
	/**
	 * Muestra los miembros del grupo.
	 * 
	 * @param id
	 * @return
	 */
	public static Result muestraMiembros(Long id) {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			// verifica que el usuario pertenesca al grupo. haciendo un INNER JOIN grupo con integrante
			// si no pertenece al grupo lo redirecciona al home "/App"
			if (Grupo.getGrupo(session("email"), id) != null) {
				return ok(views.html.grupo.grupo_miembros.render(
						Usuario.find.byId(session("email")),
						Grupo.getGrupo(session("email"), id),
						Grupo.getGrupos(session("email")),
						Integrante.find.where().eq("grupo_id", id).findList(),
						Contacto.listaAmigos(session("email")),
						""
						));
			} else {
				return redirect(routes.Home.index());
			}
		}	
	}
	
	/**
	 * Muestra las preferencias del grupo.
	 * 
	 * @param id
	 * @return
	 */
	public static Result muestraPreferencias(Long id) {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			// verifica que el usuario pertenesca al grupo. haciendo un INNER JOIN grupo con integrante
			// si no pertenece al grupo lo redirecciona al home "/App"
			if (Grupo.getGrupo(session("email"), id) != null) {
				return ok(views.html.grupo.grupo_preferencias.render(
						Usuario.find.byId(session("email")),
						Grupo.getGrupo(session("email"), id),
						Grupo.getGrupos(session("email")),
						Integrante.find.where().eq("grupo_id", id).findList(),
						Contacto.listaAmigos(session("email")),
						""
						));
			} else {
				return redirect(routes.Home.index());
			}
		}	
	}
	
	/**
	 * Muestra una reunion y sus documentos asociados.
	 * 
	 * @param idReunion
	 * @param idGrupo
	 * @return
	 */
	public static Result verReunion(Long idReunion, Long idGrupo) {
		return ok(grupo_reunion.render(
				Usuario.find.byId(session("email")),
				Grupo.getGrupo(session("email"), idGrupo),
				Grupo.getGrupos(session("email")),
				Reunion.find.byId(idReunion),
				Archivo.find.where().eq("reunion_id", idReunion).findList(),
				""
				));
	}
	
	/**
	 * Muestra los grupos a los que pertenece el usuario.
	 * 
	 * @return
	 */
	public static Result muestraGrupos() {
		return ok(muestra_grupos.render(Usuario.find.byId(session("email")), Grupo.getGrupos(session("email")), ""));
	}

	/**
	 * Crea un grupo nuevo.
	 * 
	 * @return redirecciona al grupo donde esta.
	 * @throws IOException 
	 */
	public static Result crearGrupo(Integer pag, Long id, Long idReunion) throws IOException {
		Tarea t = new Tarea();
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Group> creaGrupo = form(Group.class).bindFromRequest();

			if (creaGrupo.hasErrors()) {
				return badRequest();
			} else {
				String fileName = "";
				String extension = "";

				Grupo nuevoGrupo = new Grupo();

				// Obtiene la imagen de la vista perfil.
				MultipartFormData body = request().body().asMultipartFormData();
				FilePart picture = body.getFile("imagen");

				// Revisa si la imagen viene nula o no, si es distinto de null
				if (picture != null) {
					String contentType = picture.getContentType();
					File file = picture.getFile();

					// Si el tamaño de la imagen supera 1 MB, redirecciona a perfil
					// notificando el error.
					if (file.length() > 1000000) {
						// comprueba desde que pagina se esta creando el grupo
						// para enviar el mensaje de error.
						if (pag == 1)
				    		return badRequest(home.render(
				    				Usuario.find.byId(session("email")),
				    				Tarea.find.where().eq("usuario_correo", session("email")).findList(),
				    				t,
				    				Grupo.getGrupos(session("email")),
				    				"La imagen supera el limite"
				    				));
				    	else if (pag == 2)
				    		return badRequest(muestra_grupos.render(
									Usuario.find.byId(session("email")), 
									Grupo.getGrupos(session("email")), 
									"La imagen supera el limite"
									));
				    	else if (pag == 3)
				    		return badRequest(views.html.grupo.grupo.render(
				    				Usuario.find.byId(session("email")),
				    				Grupo.getGrupo(session("email"), id),
				    				Grupo.getGrupos(session("email")),
				    				Integrante.find.where().eq("grupo_id", id).findList(),
				    				Contacto.listaAmigos(session("email")),
				    				"La imagen supera el limite"
				    				));
				    	else if (pag == 4)
				    		return badRequest(views.html.grupo.grupo_miembros.render(
				    				Usuario.find.byId(session("email")),
				    				Grupo.getGrupo(session("email"), id),
				    				Grupo.getGrupos(session("email")),
				    				Integrante.find.where().eq("grupo_id", id).findList(),
				    				Contacto.listaAmigos(session("email")),
				    				"Debe seleccionar una imagen"
				    				));
				    	else if (pag == 5)
				    		return badRequest(views.html.grupo.grupo_preferencias.render(
				    				Usuario.find.byId(session("email")),
				    				Grupo.getGrupo(session("email"), id),
				    				Grupo.getGrupos(session("email")),
				    				Integrante.find.where().eq("grupo_id", id).findList(),
				    				Contacto.listaAmigos(session("email")),
				    				"La imagen supera el limite"
				    				));
				    	else if (pag == 6)
				    		return badRequest(grupo_reunion.render(
				    				Usuario.find.byId(session("email")),
				    				Grupo.getGrupo(session("email"), id),
				    				Grupo.getGrupos(session("email")),
				    				Reunion.find.byId(idReunion),
				    				Archivo.find.where().eq("reunion_id", idReunion).findList(),
				    				"La imagen supera el limite"
				    				));
				    	else if (pag == 7)
				    		return badRequest(views.html.grupo.grupo_reuniones.render(
									Usuario.find.byId(session("email")),
									Grupo.getGrupo(session("email"), id),
									Grupo.getGrupos(session("email")),
									Integrante.find.where().eq("grupo_id", id).findList(),
									Contacto.listaAmigos(session("email")),
									Reunion.find.where().eq("grupo_id", id).findList(),
									"La imagen supera el limite"
									));
					} else {
						// Revisa que extension tiene la imagen subida por
						// el usuario para agregarle la extension.
					    if (contentType.equals("image/png"))
					    	extension = ".png";
					    else if (contentType.equals("image/jpeg"))
					    	extension = ".jpg";
					    else if (contentType.equals("image/gif"))
					    	extension = ".gif";
					    else
					    	// comprueba desde que pagina se esta creando el grupo
							// para enviar el mensaje de error.
					    	if (pag == 1)
					    		return badRequest(home.render(
					    				Usuario.find.byId(session("email")),
					    				Tarea.find.where().eq("usuario_correo", session("email")).findList(),
					    				t,
					    				Grupo.getGrupos(session("email")),
					    				"Debe seleccionar una imagen"
					    				));
					    	else if (pag == 2)
					    		return badRequest(muestra_grupos.render(
										Usuario.find.byId(session("email")), 
										Grupo.getGrupos(session("email")), 
										"Debe seleccionar una imagen"
										));
					    	else if (pag == 3)
					    		return badRequest(views.html.grupo.grupo.render(
					    				Usuario.find.byId(session("email")),
					    				Grupo.getGrupo(session("email"), id),
					    				Grupo.getGrupos(session("email")),
					    				Integrante.find.where().eq("grupo_id", id).findList(),
					    				Contacto.listaAmigos(session("email")),
					    				"Debe seleccionar una imagen"
					    				));
					    	else if (pag == 4)
					    		return badRequest(views.html.grupo.grupo_miembros.render(
					    				Usuario.find.byId(session("email")),
					    				Grupo.getGrupo(session("email"), id),
					    				Grupo.getGrupos(session("email")),
					    				Integrante.find.where().eq("grupo_id", id).findList(),
					    				Contacto.listaAmigos(session("email")),
					    				"Debe seleccionar una imagen"
					    				));
					    	else if (pag == 5)
					    		return badRequest(views.html.grupo.grupo_preferencias.render(
					    				Usuario.find.byId(session("email")),
					    				Grupo.getGrupo(session("email"), id),
					    				Grupo.getGrupos(session("email")),
					    				Integrante.find.where().eq("grupo_id", id).findList(),
					    				Contacto.listaAmigos(session("email")),
					    				"Debe seleccionar una imagen"
					    				));
					    	else if (pag == 6)
					    		return badRequest(grupo_reunion.render(
					    				Usuario.find.byId(session("email")),
					    				Grupo.getGrupo(session("email"), id),
					    				Grupo.getGrupos(session("email")),
					    				Reunion.find.byId(idReunion),
					    				Archivo.find.where().eq("reunion_id", idReunion).findList(),
					    				"Debe seleccionar una imagen"
					    				));
					    	else if (pag == 7)
					    		return badRequest(views.html.grupo.grupo_reuniones.render(
										Usuario.find.byId(session("email")),
										Grupo.getGrupo(session("email"), id),
										Grupo.getGrupos(session("email")),
										Integrante.find.where().eq("grupo_id", id).findList(),
										Contacto.listaAmigos(session("email")),
										Reunion.find.where().eq("grupo_id", id).findList(),
										"Debe seleccionar una imagen"
										));
					    
					    nuevoGrupo.nombre = creaGrupo.get().nombre;
						nuevoGrupo.descripcion = creaGrupo.get().descripcion;
					    nuevoGrupo.imagen = "group.png";
					    nuevoGrupo.save();

					    // crea el nombre de la imagen + la extension.
					    fileName = nuevoGrupo.id.toString() + extension;

					    String path = "./public/grupos/" + fileName;
					    org.apache.commons.io.FileUtils.copyFile(file, new File(path));

					    nuevoGrupo.imagen = fileName;
					    nuevoGrupo.update();
					}
				} else { 
					nuevoGrupo.nombre = creaGrupo.get().nombre;
					nuevoGrupo.descripcion = creaGrupo.get().descripcion;
				    nuevoGrupo.imagen = "group.png";
				    nuevoGrupo.save();
				}	
				// Crea un directorio al grupo para los documentos.
			    File directorio = new File("./public/grupos/" + nuevoGrupo.id.toString());
			    directorio.mkdir();
			   
				// Crea objetos para agregar posteriormente al usuario
				// que creo el grupo a la tabla integrante
				Usuario user = new Usuario();
				Integrante nuevoIntegrante = new Integrante();
				Date fecha = new Date();

				user.correo = session("email");				

				// crea el nuevo integrante pasando los datos correspondientes
				nuevoIntegrante.grupo = nuevoGrupo;
				nuevoIntegrante.usuario = user;
				nuevoIntegrante.tipo = 1;
				nuevoIntegrante.fecha_ingreso = fecha;
				nuevoIntegrante.save();

				return redirect(routes.Grupos.index(nuevoGrupo.id));
			}
		}
	}

	/**
	 * Agrega un nuevo integrante al grupo.
	 * 
	 * @return al la pagina grupo donde esta (/grupo/?)
	 * donde ? es el id del grupo.
	 */
	public static Result agregaIntegrante(Integer pag) {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Integrante> agregaIntegrante = form(Integrante.class).bindFromRequest();
			if (agregaIntegrante.hasErrors()) {
				return badRequest();
			} else {
				Integrante integrante = new Integrante();
				Date fecha = new Date();
				Long id = agregaIntegrante.get().grupo.id;
				Grupo grupo = new Grupo();
				grupo.id = id;
				
				integrante.fecha_ingreso = fecha;
				integrante.tipo = 2;
				integrante.usuario = agregaIntegrante.get().usuario;
				integrante.grupo = grupo;
				integrante.save();
				
				if (pag == 1)
					return redirect(routes.Grupos.index(id));
				else
					return redirect(routes.Grupos.muestraMiembros(id));
			}
		}
	}
	
	/**
	 * Elimina un integrante del grupo.
	 * 
	 * @return
	 */
	public static Result eliminaIntegrante(String email, Long idGrupo) {
		Integrante integrante = Integrante.find.where().eq("usuario_correo", email).eq("grupo_id", idGrupo).findUnique();
		integrante.delete();
		return redirect(routes.Grupos.muestraMiembros(idGrupo));
	}
	
	/**
	 * Edita un grupo.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Result editaGrupo() throws IOException {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Grupo> editaGrupo = form(Grupo.class).bindFromRequest();
			if (editaGrupo.hasErrors()) {
				return badRequest();
			} else {
				Grupo grupo = Grupo.find.ref(editaGrupo.get().id);
				String extension = "";
				String fileName = "";
				
				// Obtiene la imagen de la vista perfil.
				MultipartFormData body = request().body().asMultipartFormData();
				FilePart picture = body.getFile("imagen");
				
				if (picture != null) {
					String contentType = picture.getContentType();
					File file = picture.getFile();

					// Si el tamaño de la imagen supera 1 MB, redirecciona a perfil
					// notificando el error.
					if (file.length() > 1000000) {
						return redirect(routes.Home.index());
					} else {

						// Revisa que extension tiene la imagen subida por
						// el usuario para agregarle la extension.
					    if (contentType.equals("image/png")) {
					    	extension = ".png";
					    }
					    else if (contentType.equals("image/jpeg")) {
					    	extension = ".jpg";
					    }
					    else if (contentType.equals("image/gif")) {
					    	extension = ".gif";
					    }

					    // crea el nombre de la imagen + la extension.
					    fileName = editaGrupo.get().id.toString() + extension;

					    String path = "./public/grupos/" + fileName;
					    org.apache.commons.io.FileUtils.copyFile(file, new File(path));

					    grupo.nombre = editaGrupo.get().nombre;
					    grupo.descripcion = editaGrupo.get().descripcion;
					    grupo.imagen = fileName;
					    grupo.update();
					}
				} else {
					grupo.nombre = editaGrupo.get().nombre;
				    grupo.descripcion = editaGrupo.get().descripcion;
				    grupo.update();
				}
				return redirect(routes.Grupos.muestraPreferencias(editaGrupo.get().id));
			}			
		}
	}
	
	/**
	 * Elimina un grupo.
	 * 
	 * @return
	 */
	public static Result eliminaGrupo(Long id) {
		Integrante.eliminaTodos(id);
		Grupo.find.ref(id).delete();				
		return redirect(routes.Home.index());
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
	
	/**
	 * Sube archivos a una determinada reunion.
	 * 
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static Result subeArchivo(Long idReunion, Long idGrupo) throws IOException, ParseException {
		Form<Archivo> upload = form(Archivo.class).bindFromRequest();
		if (upload.hasErrors()) {
			return badRequest();
		} else {
			Archivo archivo = upload.get();
			DateFormat formatter; 
			
			// Se recibe el archivo de la vista.
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart file = body.getFile("nombre");
			
			// Si no es nulo.
			if (file != null) {
				// Genera un random de 9 digitos.
				Integer id = (int)(Math.random()*1000000000);
				// Agrega los 9 digitos mas el nombre del archivo.
				String fileName = id.toString() + "_" + file.getFilename();
				File documento = file.getFile();				
				String path = "./public/grupos/" + idGrupo.toString() + "/" + fileName;
				
				Date fecha = new Date();
				String hora = new Date().getHours() + ":" + new Date().getMinutes() + ":" + new Date().getSeconds();				 
				formatter = new SimpleDateFormat("HH:mm:ss");
				Date hour = (Date)formatter.parse(hora);
				
				Usuario user = new Usuario();
				user.correo = session("email");
				
				archivo.nombre = fileName;
				archivo.fecha = fecha;
				archivo.hora = hour;
				archivo.usuario = user;
				archivo.reunion.id = idReunion;
				archivo.save();
				
				// Sube el archivo y lo guarda en la ruta especificada.
				org.apache.commons.io.FileUtils.copyFile(documento, new File(path));
				return redirect(routes.Grupos.verReunion(idReunion, idGrupo));
			}
		}
		return ok();
	}
	
	/**
	 * Descarga un archivo.
	 * 
	 * @param id
	 * @return
	 */
	public static Result descargarArchivo(Long id, Long grupoId) {
		Archivo archivo = Archivo.find.byId(id);
		File file = new File("./public/grupos/" + grupoId.toString() + "/" + archivo.nombre);
        response().setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
		return ok(file);
	}
	
	/**
	 * Elimina un archivo.
	 * 
	 * @param idArchivo
	 * @param idReunion
	 * @param idGrupo
	 * @return
	 */
	public static Result eliminarArchivo(Long idArchivo, Long idReunion, Long idGrupo) {
		Archivo archivo = Archivo.find.byId(idArchivo);
		File file = new File("./public/grupos/" + idGrupo.toString() + "/" + archivo.nombre);
		file.delete();
		archivo.delete();
		return redirect(routes.Grupos.verReunion(idReunion, idGrupo));
	}
	
	/**
	 * Cambia de administrador en un grupo.
	 * 
	 * @return
	 */
	public static Result cambiarAdmin() {
		Form<Integrante> cambiaAdmin = form(Integrante.class).bindFromRequest();
		if (cambiaAdmin.hasErrors()) {
			return badRequest();
		} else {
			Integrante.quitarAdmin(session("email"), cambiaAdmin.get().grupo.id);
			Integrante.agregarAdmin(cambiaAdmin.get().usuario.correo, cambiaAdmin.get().grupo.id);
		}
		return redirect(routes.Grupos.index(cambiaAdmin.get().grupo.id));
	}
	
	/**
	 * Deja un grupo determinado un integrante.
	 * 
	 * @param id
	 * @return
	 */
	public static Result dejarGrupo(Long id) {
		Integrante integrante = Integrante.find.where()
				.eq("usuario_correo", session("email"))
				.eq("grupo_id", id).findUnique();
		integrante.delete();
		return redirect(routes.Home.index());
	}
}