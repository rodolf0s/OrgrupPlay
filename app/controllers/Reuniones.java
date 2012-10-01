package controllers;

import models.Reunion;
import models.Usuario;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.grupo.*;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Reuniones extends Controller {

	public static Result generarReunion() {
		
		Form<Reunion> formReunion = form(Reunion.class).bindFromRequest();
		
		if(formReunion.hasErrors()){
			return ok("Error");		
		}else {
			Reunion objetoReunion = formReunion.get();
			objetoReunion.save();
			return ok(mensajeReunion.render(Usuario.find.byId(session("email")), objetoReunion));
	}
		
	}
}
