package controllers;

import java.util.Date;

import models.Contacto;
import models.Grupo;
import models.Integrante;
import models.Usuario;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class Grupos extends Controller {
	
	/**
	 * clase para crear un nuevo grupo en la vista "/grupo/?"
	 *
	 */
	public static class Group {
		public String nombre;
		public String distintivo;
		public Long grupoId;
	}
	
	public static Result index(Long id) {		
		// verifica que el usuario pertenesca al grupo. haciendo un INNER JOIN grupo con integrante
		// si no pertenece al grupo lo redirecciona al home "/App"
		if (Grupo.getGrupo(session("email"), id) != null) {
			return ok(views.html.agenda.grupo.render(
					Usuario.find.byId(session("email")),
					Grupo.getGrupo(session("email"), id),
					Grupo.getGrupos(session("email")),
					Integrante.find.where().eq("grupo_id", id).findList(),
					Contacto.find.where().eq("usuario1_correo", session("email")).findList()
					// Contacto.getContactos(session("email"))
					));
		} else {
			return redirect(routes.Home.index());
		}	
	}
	
	/**
	 * Crea un grupo nuevo.
	 * 
	 * @return redirecciona al grupo donde esta.
	 */
	public static Result crearGrupo() {
		
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Group> creaGrupo = form(Group.class).bindFromRequest();

			if (creaGrupo.hasErrors()) {
				return redirect(routes.Home.index());
			} else {
				Grupo nuevoGrupo = new Grupo();
				
				nuevoGrupo.nombre = creaGrupo.get().nombre;
				nuevoGrupo.distintivo = creaGrupo.get().distintivo;
				nuevoGrupo.save();
				
				/*
				 * Crea objetos para agregar posteriormente al usuario
				 * que creo el grupo a la tabla integrante
				 */
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

				return redirect(routes.Grupos.index(creaGrupo.get().grupoId));
			}
		}
	}
	
	/**
	 * agrega un nuevo integrante al grupo.
	 * 
	 * @return al la pagina grupo donde esta (/grupo/?)
	 * donde ? es el id del grupo.
	 */
	public static Result agregaIntegrante() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Integrante> agregaIntegrante = form(Integrante.class).bindFromRequest();
			if (agregaIntegrante.hasErrors()) {
				return badRequest();
			} else {
				Date fecha = new Date();
				
				// Importante: aqui solo crea un objeto tipo usuario para buscar en la 
				// BD el correo, ya que solo llega el nombre del usuario. 
				// Esto solo funciona si el nombre fuera unico.
				Usuario user = new Usuario();
				user.correo = Usuario.getCorreo(agregaIntegrante.get().usuario.correo);				
				agregaIntegrante.get().usuario.correo = user.correo;
				
				agregaIntegrante.get().tipo = 2;
				agregaIntegrante.get().fecha_ingreso = fecha;
				agregaIntegrante.get().save();
				
				Long id = agregaIntegrante.get().grupo.id;
				return redirect(routes.Grupos.index(id));
			}
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
