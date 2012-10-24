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
		Integer [] bloque = null;
		Date [] dias = null;
		Date [] horas = null;
		Integer posiciones = null;
		Integer contarBloques = 0;
		Date [] horasUso = null;
		Date [] diasUso = null;
		Integer duracion = null;
		
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
			duracion = objetoReunion.duracion;
			
			//Transformamos los tipo Date en tipo Calendar
			Calendar fechaInicioCalendar = new GregorianCalendar(); 
			Calendar fechaFinCalendar = new GregorianCalendar(); 
			Calendar horaInicioCalendar = new GregorianCalendar(); 
			Calendar horaFinCalendar = new GregorianCalendar(); 
			fechaInicioCalendar.setTime(fechaInicio);
			fechaFinCalendar.setTime(fechaFin);
			horaInicioCalendar.setTime(horaInicio);
			horaFinCalendar.setTime(horaFin);
			
			//aumentamos en 1 la fecha de fin para la busqueda porque before no cuenta el igual
			fechaFinCalendar.add(fechaFinCalendar.DAY_OF_MONTH, +1);
			
			//Calcular los dias que se revizaran
			Integer diferenciaDias =  fechaFinCalendar.get(Calendar.DAY_OF_YEAR) - fechaInicioCalendar.get(Calendar.DAY_OF_YEAR);
			
			//Calcular las horas que se revizaran
			Integer diferenciaHoras = horaFinCalendar.get(Calendar.HOUR_OF_DAY) - horaInicioCalendar.get(Calendar.HOUR_OF_DAY);
			diferenciaHoras = diferenciaHoras + 1;
			
			//Calcular la catidad de posiciones del vector
			posiciones = diferenciaDias * diferenciaHoras;
			
			//Inicializar los vectores
			bloque = new Integer[posiciones];
			dias = new Date [posiciones];
			horas = new Date [posiciones];
			diasUso = new Date [posiciones];
			horasUso = new Date [posiciones];
			
			//puntero del vector
			Integer a = 0;
			
			//Revisamos dias con bloques libres del administrador
			while(fechaInicioCalendar.before(fechaFinCalendar)) {
				
				//reiniciar el valor de las horas			
				horaInicioCalendar.setTime(horaInicio);
				horaFinCalendar.setTime(horaFin);
				
				//aumentamos en 1 la hora de fin para la busqueda porque before no cuenta el igual
				horaFinCalendar.add(horaFinCalendar.HOUR, +1);
				
				while(horaInicioCalendar.before(horaFinCalendar)) {
					Integer estado = null;
					Date fechaInicio1 = fechaInicioCalendar.getTime();
					Date horaInicio1 = horaInicioCalendar.getTime();
				
					//guardar la fecha del bloque
					dias[a] = fechaInicio1;
					horas[a] = horaInicio1; 
					
					//Indica si el bloque esta libre o no
					estado = Tarea.buscarTarea(fechaInicio1, horaInicio1, correo);
					if (estado > 0){
						bloque [a] = 1;
					}else{
						bloque [a] = 0;
					}

					horaInicioCalendar.add(horaInicioCalendar.HOUR, +1);
					a = a+1;
				}
				fechaInicioCalendar.add(fechaInicioCalendar.DAY_OF_MONTH, +1);
			}
	}
		Date fechaComparar = dias[0];
		Date transicionHora = null;
		Integer contarFecha = 0;
		
		Integer resultados = 0;
		//Comprobar que excistan bloques consecutivos igual a la duracion de la reunion
		for(int z = 0 ; z < posiciones; z++){
			
			//Comprobar que siga siendo el mismo dia			
			if(fechaComparar.equals(dias[z])){
			
				//Comprobar que el bloque este libre y sea consecutivo
				if(bloque[z] == 0){
					contarBloques = contarBloques + 1;
				
					//Comprobar que la fecha que se guarde sea la fecha de inicio de la reunion
					if(contarBloques == 1){
						transicionHora = horas[z];
					}
					
					//Comprobar que la reunion cumple con las horas necesarias
					if(contarBloques == duracion){
						resultados = resultados +1;
						
						//Guardar el dia y el bloque de la reunion
						diasUso[contarFecha] = fechaComparar;
						horasUso[contarFecha] = transicionHora;
						contarFecha = contarFecha +1;
						transicionHora = null;
						
//						//calcular puntaje reunion


						contarBloques = 0;
						z = z - (duracion - 1);

					}
				}else{
					//resetear todos los valores en caso de no alcanzar duracion
					contarBloques = 0;
					transicionHora = null;
				
				}
				
			}else{
				//Cambia la fecha se comienza denuevo el conteo
				fechaComparar = dias[z];
				
				//disminuir el contador para que no pase por alto el bloque que cambia la fecha
				z = z -1;
				contarBloques = 0;
				transicionHora = null;
				
			}
		}
		return ok(mensajeReunion.render(session("email"), numero, correo, bloque, resultados, diasUso, horasUso, contarFecha));
	}
}
