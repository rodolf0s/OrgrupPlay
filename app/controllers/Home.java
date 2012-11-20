package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.Contacto;
import models.Integrante;
import models.Mensaje;
import models.Tarea;
import models.Usuario;
import models.Grupo;
import models.Notificaciones;

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
		String [] error = new String[7];

		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(home.render(
					Usuario.find.byId(session("email")),
					Tarea.find.where().eq("usuario_correo", session("email")).findList(),
					t,
					Grupo.getGrupos(session("email")),
					"",
					error
					));
		}
	}

	/**
	 * Crea una nueva tarea y la guarda en la BD.
	 *
	 * @return redirecciona al home.
	 * @throws ParseException
	 */
	public static Result guardaTarea(Integer op) throws ParseException {
		if (!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Tarea> tareaForm = form(Tarea.class).bindFromRequest();
			Tarea t = new Tarea();

			if(tareaForm.hasErrors()) {
				return redirect(routes.Home.index());
			} else {
				Tarea tarea = tareaForm.get();
				List<Tarea> tareas = Tarea.find.where().eq("usuario_correo", session("email")).findList();
				String [] error = new String[7];

				// Comprueba que no ingrese una tarea a la misma hora y fecha que otra
				for (int i = 0; i < tareas.size(); i++) {
					if (tareas.get(i).hora_inicio.compareTo(tarea.hora_inicio) == 0 && tareas.get(i).fecha_inicio.compareTo(tarea.fecha_inicio) == 0) {
						java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
						String fecha = sdf.format(tarea.fecha_inicio);
						error[0] = "No puede crear la tarea, porque ya tiene una a esta hora";
						error[1] = fecha;
						error[2] = tarea.nombre.trim();
						error[3] = tarea.descripcion.trim();
						error[4] = tarea.prioridad.toString();
						error[5] = "1";

						return ok(home.render(
							Usuario.find.byId(session("email")),
							Tarea.find.where().eq("usuario_correo", session("email")).findList(),
							t,
							Grupo.getGrupos(session("email")),
							"",
							error
							));
					}
				}

				if (Notificaciones.getTarea(session("email")))
					tarea.notificado = "no";
				else
					tarea.notificado = "si";
				tarea.save();

				// Si el checkbox de repetir esta seleccionado, repite la tarea.
				if (op > 0) {
					// Si elije repetir cada dia.
					if (op == 1) {
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
			Tarea t = new Tarea();

			if(editaForm.hasErrors()) {
				return redirect(routes.Home.index());
			} else {
				Tarea tarea = Tarea.find.byId(editaForm.get().id);

				// busca si hay una tarea que empieze el mismo dia a la misma hora.
				Tarea tareaa = Tarea.find.where()
									.eq("usuario_correo", session("email"))
									.eq("fecha_inicio", editaForm.get().fecha_inicio)
									.eq("hora_inicio", editaForm.get().hora_inicio)
									.findUnique();
				String [] error = new String[7];

				if (tareaa != null) {
					// Si la tarea buscada es distinto de nulo pero los id son iguales
					// quiere decir que la tarea encontrada es la misma que se va a editar
					// por lo tanto deja actualizar la tarea.
					if (tareaa.id.equals(editaForm.get().id)) {
						tarea.nombre = editaForm.get().nombre;
						tarea.fecha_inicio = editaForm.get().fecha_inicio;
						tarea.fecha_fin = editaForm.get().fecha_fin;
						tarea.hora_inicio = editaForm.get().hora_inicio;
						tarea.hora_fin = editaForm.get().hora_fin;
						tarea.descripcion = editaForm.get().descripcion;
						tarea.prioridad = editaForm.get().prioridad;
						tarea.update();
						return redirect(routes.Home.index());

					// pero si los id de ambas tareas son distintos,
					// quiere decir que hay otra tarea que comienza a esa misma hora
					// por lo tanto no actualiza la tarea
					} else {
						java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
						String fecha = sdf.format(editaForm.get().fecha_inicio);
						error[0] = "No puede editar la tarea, porque ya tiene una a esta hora";
						error[1] = fecha;
						error[2] = editaForm.get().nombre.trim();
						error[3] = editaForm.get().descripcion.trim();
						error[4] = editaForm.get().prioridad.toString();
						error[5] = "2";
						error[6] = editaForm.get().id.toString();
						return ok(home.render(
							Usuario.find.byId(session("email")),
							Tarea.find.where().eq("usuario_correo", session("email")).findList(),
							t,
							Grupo.getGrupos(session("email")),
							"",
							error
						));
					}

				// Si la tarea buscada viene nula, es por que no hay ninguna tarea
				// que empieze el mismo dia a la misma hora, por lo tanto se actualiza.
				} else {
						tarea.nombre = editaForm.get().nombre;
						tarea.fecha_inicio = editaForm.get().fecha_inicio;
						tarea.fecha_fin = editaForm.get().fecha_fin;
						tarea.hora_inicio = editaForm.get().hora_inicio;
						tarea.hora_fin = editaForm.get().hora_fin;
						tarea.descripcion = editaForm.get().descripcion;
						tarea.prioridad = editaForm.get().prioridad;
						tarea.update();
						return redirect(routes.Home.index());
				}
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
		String [] error = new String[7];
		if (mostrarForm.hasErrors()) {
			return ok("Error edita");
		} else {
			return ok(home.render(
					Usuario.find.byId(session("email")),
					Tarea.find.where().eq("usuario_correo", session("email")).findList(),
					Tarea.find.byId(mostrarForm.get().id),
					Grupo.getGrupos(session("email")),
					"",
					error
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
				.eq("amigo", "no")
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

	public static Integer notificacionesGrupos() {
		return Integrante.find.where()
				.eq("usuario_correo", session("email"))
				.eq("estado", "inactivo")
				.findRowCount();
	}
}