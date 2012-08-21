package controllers;

import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.Ebean;

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
		List<Tarea> tareaVacia = new ArrayList<Tarea>();
		Tarea t = new Tarea();
		
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			return ok(home.render(
					Usuario.find.byId(session("email")),
					Tarea.find.where().eq("usuario_correo", session("email")).findList(),
//					tareaVacia
					t
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
				return redirect(routes.Home.index());
			}

		}
	}

	public static Result editaTarea() {
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Tarea> editaForm = form(Tarea.class).bindFromRequest();

			if(editaForm.hasErrors()) {
				return ok("Error edita");
			} else {
				
				editaForm.get().update();
				return redirect(routes.Home.index());
			}
		}
	}
	
	public static Result eliminaTarea() {
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} else {
			Form<Tarea> eliminaForm = form(Tarea.class).bindFromRequest();

			if(eliminaForm.hasErrors()) {
				return ok("Error elimina");
			} else {
				Tarea.eliminaTarea(eliminaForm.get().id);
				
//				eliminaForm.get().delete();
				return redirect(routes.Home.index());
			}
		}
	}

	public static Result prueba() {
		Form<Tarea> mostrarForm = form(Tarea.class).bindFromRequest();
		if(mostrarForm.hasErrors()) {
			return ok("Error edita");
		} else {
			return ok(home.render(
					Usuario.find.byId(session("email")), 
					Tarea.find.where().eq("usuario_correo", session("email")).findList(),
	//				Tarea.find.where().eq("id", mostrarForm.get().id).findList()
					Tarea.find.byId(mostrarForm.get().id)
					));
		}
	}
	
	public static Integer notificacionAmigos(){
		return Contacto.find.where().eq("usuario2_correo", session("email")).findRowCount();
	}

	
	
	public static boolean verificaSession() {
		if(session("email") == null) 
			return false;
		else
			return true;
	}
	
	

}