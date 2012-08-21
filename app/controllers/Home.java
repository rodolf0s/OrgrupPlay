package controllers;

import models.Contacto;
import models.Tarea;
import models.Usuario;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.*;

import views.html.*;
import views.html.agenda.*;

public class Home extends Controller {	
	
	public static Result index() {

		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(home.render(
					Usuario.find.byId(session("email")),
					Tarea.find.where().eq("usuario_correo", session("email")).findList()
					));
			
		}
	}
	
	public static Result guardaTarea() {
		
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Tarea> tareaForm = form(Tarea.class).bindFromRequest();

			if(tareaForm.hasErrors()) {
				return ok("Error");
//				return badRequest(home.render(Usuario.find.byId(session("email")), tareaForm));
			} else {
				tareaForm.get().save();
				return ok(home.render(
						Usuario.find.byId(session("email")), 
						Tarea.find.where().eq("usuario_correo", session("email")).findList()
						));
//				Tarea tarea = tareaForm.get();
				
//				int hora = tarea.hora_inicio.getHours();
//				
//				String hours = hora+"";
//				tarea.save();
//				return ok("nombre: "+ tarea.nombre
//						+ "\n fecha inicio: " + tarea.fecha_inicio.toString()
//						+ "\n hora inicio: " + tarea.hora_inicio.toString()
//						+ "\n fecha fin: " + tarea.fecha_fin.toString()
//						+ "\n hora fin: " + tarea.hora_fin.toString()
//						+ "\n Descripcion: " + tarea.descripcion
//						+ "\n Prioridad: " + tarea.prioridad+"");
			}

		}
	}
	
	public static Integer notificacionAmigos(){
		return Contacto.find.where().eq("usuario2_correo", session("email")).eq("amigos", "no").findRowCount();
	}

	public static boolean verificaSession() {
		if(session("email") == null) 
			return false;
		else
			return true;
	}
	
	

}