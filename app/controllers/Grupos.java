package controllers;

import java.util.Date;

import models.Grupo;
import models.Integrante;
import models.Usuario;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class Grupos extends Controller {
	
	public static Result index(String id) {
		
		// verifica que el usuario pertenesca al grupo. haciendo un INNER JOIN grupo con integrante
		// si no pertenece al grupo lo redirecciona al home "/App"
		if (Grupo.getGrupo(session("email"), Long.valueOf(id).longValue()) != null) {
			return ok(views.html.agenda.grupo.render(
					Usuario.find.byId(session("email")),
					Grupo.getGrupo(session("email"), Long.valueOf(id).longValue())
					));
		} else {
			return redirect(routes.Home.index());
		}	
	}
	
	/**
	 * Crea un grupo nuevo.
	 * 
	 * @return redirecciona al home.
	 */
	public static Result crearGrupo() {
		
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Grupo> creaGrupo = form(Grupo.class).bindFromRequest();

			if (creaGrupo.hasErrors()) {
				return redirect(routes.Home.index());
			} else {
				creaGrupo.get().save();
				
				// Obtiene el id del grupo creado
				Grupo idGrupo = Grupo.find.select("id")
						.where().
						eq("nombre", creaGrupo.get().nombre).
						eq("distintivo", creaGrupo.get().distintivo)
						.findUnique();
				
				/*
				 * Crea objetos para agregar posteriormente al usuario
				 * que creo el grupo a la tabla integrante
				 */
				Usuario user = new Usuario();
				Integrante nuevoIntegrante = new Integrante();
				Date fecha = new Date();
				
				user.correo = session("email");				
				
				// crea el nuevo integrante paasando los datos correspondientes
				nuevoIntegrante.grupo = idGrupo;
				nuevoIntegrante.usuario = user;
				nuevoIntegrante.tipo = 1;
				nuevoIntegrante.fecha_ingreso = fecha;
				nuevoIntegrante.save();

				return redirect(routes.Home.index());
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
