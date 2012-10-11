package controllers;

import models.Reunion;
import models.Tarea;
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

	public static Result generarReunion(String correo) {
		Integer numero= 0;
		Integer [][] bloques = {};
		Date [] dias = {};
		Date [] horas = {};
		
 		Form<Reunion> formReunion = form(Reunion.class).bindFromRequest();
		
		if(formReunion.hasErrors()){
			return ok("Error");		
		}else {
			//Creamos el objeto reunion y obtenemos los valores desde el form
			Reunion objetoReunion = formReunion.get();
			Date fechaInicio = objetoReunion.fecha_inicio;
			Date fechaFin = objetoReunion.fecha_fin;
			Date horaInicio = objetoReunion.hora_inicio;
			Date horaFin = objetoReunion.hora_fin;
			
			//Transformamos los tipo Date en tipo Calendar
			Calendar fechaInicioCalendar = new GregorianCalendar(); 
			Calendar fechaFinCalendar = new GregorianCalendar(); 
			Calendar horaInicioCalendar = new GregorianCalendar(); 
			Calendar horaFinCalendar = new GregorianCalendar(); 
			fechaInicioCalendar.setTime(fechaInicio);
			fechaFinCalendar.setTime(fechaFin);
			
			//aumentamos en 1 la fecha de fin para la busqueda porque before no cuenta el igual
			fechaFinCalendar.add(fechaFinCalendar.DAY_OF_MONTH, +1);
			
			Integer a = 0;
			
			//Revisamos dias con bloques libres del administrador
			while(fechaInicioCalendar.before(fechaFinCalendar)) {
				
				Integer b = 0;				
				horaInicioCalendar.setTime(horaInicio);
				horaFinCalendar.setTime(horaFin);
				
				//aumentamos en 1 la hora de fin para la busqueda porque before no cuenta el igual
				horaFinCalendar.add(horaFinCalendar.HOUR, +1);
				
				while(horaInicioCalendar.before(horaFinCalendar)) {
					Integer estado = null;
					Date fechaInicio1 = fechaInicioCalendar.getTime();
					Date horaInicio1 = horaInicioCalendar.getTime();
					
					estado = Tarea.buscarTarea(fechaInicio1, horaInicio1, correo);
					//Indica si el bloque esta libre o no
					if (estado > 0){
						numero = numero + 1;
					}else{

					}

					horaInicioCalendar.add(horaInicioCalendar.HOUR, +1);
					b = b+1;
				}
				fechaInicioCalendar.add(fechaInicioCalendar.DAY_OF_MONTH, +1);
				a = a+1;
			}
	}
		return ok(mensajeReunion.render(session("email"), numero, correo));
	}
}
