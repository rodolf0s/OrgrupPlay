package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import views.html.*;
import views.html.agenda.*;

public class Contacto extends Controller {

	public static Result contacto(){
		return ok(contactos.render(session("nombre")));
	}
}
