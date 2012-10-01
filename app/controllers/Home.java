package controllers;

import java.util.Calendar;

import models.Contacto;
import models.Mensaje;
import models.Tarea;
import models.Usuario;
import models.Grupo;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.agenda.*;

public class Home extends Controller {	

	/**
	 * Muestra la pagina de inicio de la applicacion.
	 * 
	 * @return un objeto Usuario, una lista con las tareas del usuario
	 * un objeto tarea vacio y los grupos que pertenece.
	 */
	public static Result index() {
		Tarea t = new Tarea();
		
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(home.render(
					Usuario.find.byId(session("email")),
					Tarea.find.where().eq("usuario_correo", session("email")).findList(),
					t,
					Grupo.getGrupos(session("email"))
					));
		}
	}
	
	/**
	 * Crea una nueva tarea y la guarda en la BD.
	 * 
	 * @return redirecciona al home.
	 */
	public static Result guardaTarea() {		
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Tarea> tareaForm = form(Tarea.class).bindFromRequest();

			if(tareaForm.hasErrors()) {
				return redirect(routes.Home.index());
			} else {
				tareaForm.get().save();

				// Si el checkbox de repetir esta seleccionado, repite la tarea.
				if (tareaForm.get().repetir != null) {
					if (tareaForm.get().repetir == 1) {
						
						// Si elije repetir cada dia.
						if (tareaForm.get().op_repetir == 1) {

							Integer fin = tareaForm.get().fecha_termino.getDate();
							Calendar nuevaFechaInicio = Calendar.getInstance();
							Calendar nuevaFechaFin = Calendar.getInstance();
							
							// Pasa Date to Calendar. la fecha de inicio y fin.
							// para despues sumar dias.
							nuevaFechaInicio.setTime(tareaForm.get().fecha_inicio);
							nuevaFechaFin.setTime(tareaForm.get().fecha_inicio);
							
							// Itera la cantidad de dias a repetir, depende de la fecha de fin de la tarea y de termino.
							for (Integer dia = tareaForm.get().fecha_fin.getDate(); dia < fin; dia++) {
								// suma un dia a la fecha de inicio y fin
								nuevaFechaInicio.add(Calendar.DATE, 1);
								nuevaFechaFin.add(Calendar.DATE, 1);

								// Pasa los datos mas la nueva fecha al metodo setTare, para que la guarde.
								Tarea.setTarea(tareaForm.get().nombre, tareaForm.get().descripcion, tareaForm.get().prioridad, 
										tareaForm.get().usuario, tareaForm.get().hora_inicio, tareaForm.get().hora_fin,
										nuevaFechaInicio.getTime(), nuevaFechaFin.getTime());		
							}
						}
						// Si elije repetir cada semana.
						else {

							Integer fin = tareaForm.get().fecha_termino.getDate();
							Calendar nuevaFechaInicio = Calendar.getInstance();
							Calendar nuevaFechaFin = Calendar.getInstance();
							
							// Pasa Date to Calendar. la fecha de inicio y fin.
							// para despues sumar dias.
							nuevaFechaInicio.setTime(tareaForm.get().fecha_inicio);
							nuevaFechaFin.setTime(tareaForm.get().fecha_inicio);
							
							// Itera la cantidad de dias a repetir, depende de la fecha de fin de la tarea y de termino.
							for (Integer dia = tareaForm.get().fecha_fin.getDate(); dia < fin; dia = dia + 7) {
								// suma un dia a la fecha de inicio y fin
								nuevaFechaInicio.add(Calendar.DATE, 7);
								nuevaFechaFin.add(Calendar.DATE, 7);

								// Pasa los datos mas la nueva fecha al metodo setTare, para que la guarde.
								Tarea.setTarea(tareaForm.get().nombre, tareaForm.get().descripcion, tareaForm.get().prioridad, 
										tareaForm.get().usuario, tareaForm.get().hora_inicio, tareaForm.get().hora_fin,
										nuevaFechaInicio.getTime(), nuevaFechaFin.getTime());		
							}
						}
					}
					
				}					
				return redirect(routes.Home.index());
			}
		}
	}

	/**
	 * Edita una tarea seleccionada en la vista home.
	 * 
	 * @return redirecciona al home.
	 */
	public static Result editaTarea() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Tarea> editaForm = form(Tarea.class).bindFromRequest();

			if(editaForm.hasErrors()) {
				return redirect(routes.Home.index());
			} else {				
				editaForm.get().update();
				return redirect(routes.Home.index());
			}
		}
	}
	
	/**
	 * Elimina una tarea seleccionada en la vista home.
	 * 
	 * @return redirecciona al home.
	 */
	public static Result eliminaTarea() {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Tarea> eliminaForm = form(Tarea.class).bindFromRequest();

			if (eliminaForm.hasErrors()) {
				return redirect(routes.Home.index());
			} else {
				// llama al metodo ubicado en el modelo de tarea para eliminar
				Tarea.eliminaTarea(eliminaForm.get().id);
				return redirect(routes.Home.index());
			}
		}
	}

	/**
	 * Obtiene los datos de una determinada tarea, 
	 * para mostrarlos en la vista y poder editarla o eliminarla
	 * 
	 * @return un objeto Usuario, una lista con las tareas del usuario
	 * un objeto tarea buscada por id y los grupos que pertenece.
	 */
	public static Result getTarea() {
		Form<Tarea> mostrarForm = form(Tarea.class).bindFromRequest();
		if (mostrarForm.hasErrors()) {
			return ok("Error edita");
		} else {
			return ok(home.render(
					Usuario.find.byId(session("email")), 
					Tarea.find.where().eq("usuario_correo", session("email")).findList(),
					Tarea.find.byId(mostrarForm.get().id),
					Grupo.getGrupos(session("email"))
					));
		}
	}
	
	/**
	 * Notifica si alguien envia una solicitud de contacto
	 * se muestra en rojo al lado de Contactos.
	 * 
	 * @return el numero de solicitudes encontradas.
	 */
	public static Integer notificacionAmigos(){
		return Contacto.find.where()
				.eq("usuario2_correo", session("email"))
				.eq("amigos", "no")
				.findRowCount();
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
	 * Obtiene el numero de notificaciones de mensajes nuevos
	 */
	public static Integer notificacionesMensajes() {
		return Mensaje.find.where()
				.eq("destinatario", session("email"))
				.eq("leido", "no")
				.eq("estado", "recibido")
				.findRowCount();
	}
}