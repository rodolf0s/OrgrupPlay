package controllers;

import models.Usuario;
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
			
			return ok(home.render(Usuario.find.byId(session("email"))));
			
		}
	}
	
	
	public static boolean verificaSession() {
		if(session("email") == null) 
			return false;
		else
			return true;
	}
}