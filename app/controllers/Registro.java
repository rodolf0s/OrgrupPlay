package controllers;

import models.Usuario;
import play.*;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Registro extends Controller {

	public static Form<Usuario> registroForm = form(Usuario.class);
	
	public static Result registro() {
		return ok(registro.render(registroForm));
	}
}
