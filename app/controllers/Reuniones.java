package controllers;

import models.Integrante;
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
import java.util.List;
import java.util.StringTokenizer;

public class Reuniones extends Controller {

	public static Result generarReunion(String correo) {
		
		Integer [] bloque = null;
		Integer puntajeBloque = 0;
		Integer [] puntajeReunion = null;
		Integer sumatoriaValoresBloque = 0;
		Date [] dias = null;
		Date [] horas = null;
		Integer posiciones = null;
		Integer contarBloques = 0;
		Date [] horasUso = null;
		Date [] diasUso = null;
		Integer duracion = null;
		Integer idGrupo = 0;
		Integer miembros = 0;
		String correosMiembrosAsistentes = null;
		String correosMiembrosAsistentesAux = null;
		List<Integrante> listaMiembros = null;
		Integer valor = null;
		Integer asistentes = 0;
		Integer asistentesAux = 0;
		Integer asistenciaMinima = 0;
		
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
			idGrupo = objetoReunion.grupo.id.intValue();
			asistenciaMinima = objetoReunion.asistencia;
			
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
				
					//Indica si el bloque esta libre o no
					estado = Tarea.buscarTarea(fechaInicio1, horaInicio1, correo);
					if (estado > 0){
						
						//Obtenemos hora termino de la tarea
						Date termino = Tarea.buscaHoraTermino(fechaInicio1, horaInicio1, correo);
						
						//Lo transformamos a calendar
						Calendar terminoCalendar = new GregorianCalendar(); 
						terminoCalendar.setTime(termino);
						
						//Marcar todos los bloques ocupados de la tarea (en caso de que la tarea dure mas de una hora)
						while(horaInicioCalendar.before(terminoCalendar)) {
						
						horaInicio1 = horaInicioCalendar.getTime();
						
						//guardar la fecha y valor del bloque
						dias[a] = fechaInicio1;
						horas[a] = horaInicio1; 	
						bloque [a] = 1;
						
						//aumenta contador y hora
						a = a + 1;
						horaInicioCalendar.add(horaInicioCalendar.HOUR, +1);
						}
						
					}else{
						
						//guardar la fecha y valor del bloque
						dias[a] = fechaInicio1;
						horas[a] = horaInicio1; 
						bloque [a] = 0;
						
						//aumenta contador y hora
						a = a+1;
						horaInicioCalendar.add(horaInicioCalendar.HOUR, +1);
					}
				
				}
				fechaInicioCalendar.add(fechaInicioCalendar.DAY_OF_MONTH, +1);
			}
	}
		//variables para comprobacion bloques 
		Date fechaComparar = dias[0];
		Date transicionHora = null;
		Integer contarFecha = 0;
		
		//Buscar miembros del grupo
		miembros = Integrante.contarMiembros(idGrupo.longValue())-1;
		
		//Variables de puntajes
		puntajeReunion = new Integer [posiciones];
		
		//Se reciben todos los miembros del grupo en una lista
		listaMiembros = Integrante.buscaMiembros(idGrupo.longValue());

		Integer resultados = 0;
		//Comprobar que excistan bloques consecutivos igual a la duracion de la reunion y que cumplan con el minimo de asistencia
		for(int z = 0 ; z < posiciones; z++){
			
			//Comprobar que siga siendo el mismo dia			
			if(fechaComparar.equals(dias[z])){
			
				//Comprobar que el bloque este libre y sea consecutivo
				if(bloque[z] == 0){
					
					//Comprobar que sea el primer bloque de la reunion
					if(contarBloques == 0){
						
						//Buscar miembros que pueden asistir (cuando es el primer bloque extrae los correos de los asistentes)
						for(int y = 0 ; y < miembros; y++){
							
							valor = Tarea.valorTarea(fechaComparar, horas[z], listaMiembros.get(y).usuario.correo);
							
							//Guardar los asistentes a la reunion
							if((valor == 0) || (valor == 1) || (valor == 2)){
								
								correosMiembrosAsistentes = correosMiembrosAsistentes + ";" + listaMiembros.get(y).usuario.correo;
								asistentes = asistentes + 1;
								
								//Sumatoria de los puntaje de las tareas
								sumatoriaValoresBloque = sumatoriaValoresBloque + valor;
							}
								
						}
						
						//Comprobar que se cumple asistencia minima
						if(asistenciaMinima <= asistentes){
							
							//Sumatoria puntajes del bloque
							puntajeBloque = puntajeBloque + (asistentes * 3 - sumatoriaValoresBloque);
							
							transicionHora = horas[z];
							contarBloques = contarBloques + 1;
							sumatoriaValoresBloque = 0;
							
						}else{
							//reiniciar todos los valores en caso de que no se cumpla el minimo de asistencia
							asistentesAux = 0;
							correosMiembrosAsistentesAux = null;
							sumatoriaValoresBloque = 0;
							contarBloques = 0;
							transicionHora = null;
							correosMiembrosAsistentes = null;
							asistentes = 0;
							puntajeBloque = 0;
							
						}
						
					}else{
					
					//Comparar la asistencia de los bloques con el primero de la reunion
					for(int y = 0 ; y < miembros; y++){
						
						valor = Tarea.valorTarea(fechaComparar, horas[z], listaMiembros.get(y).usuario.correo);
						
						//Guardar los asistentes a la reunion
						if((valor == 0) || (valor == 1) || (valor == 2)){
							
							correosMiembrosAsistentesAux = correosMiembrosAsistentesAux + ";" + listaMiembros.get(y).usuario.correo;
							
							//Sumatoria de los puntaje de las tareas
							sumatoriaValoresBloque = sumatoriaValoresBloque + valor;
						}
							
					}
									
					StringTokenizer token = new StringTokenizer(correosMiembrosAsistentes, ";");
					
					//Compara los asistentes entre bloques
					while(token.hasMoreTokens()) {
						String miembroConfirmado = token.nextToken();
						
						StringTokenizer token2 = new StringTokenizer(correosMiembrosAsistentesAux, ";");
						
						while(token2.hasMoreTokens()){
							
							String miembroConfirmado2 = token2.nextToken();
							
							if(miembroConfirmado.equals(miembroConfirmado2) ) {
								asistentesAux = asistentesAux +1;
							}
						}	
					}
					
					//Comprobar que el nuevo bloque cumple con el minimo de asistencia
					if(asistenciaMinima <= asistentesAux){
						
						//Sumatoria puntajes del bloque
						puntajeBloque = puntajeBloque + (asistentes * 3 - sumatoriaValoresBloque);
						
						contarBloques = contarBloques + 1;
						asistentesAux = 0;
						correosMiembrosAsistentesAux = null;
						sumatoriaValoresBloque = 0;
						

						
						//Comprobar que la reunion cumple con las horas necesarias
						if(contarBloques == duracion){
							resultados = resultados + 1;
							//Guardar el dia y el bloque de la reunion
							diasUso[contarFecha] = fechaComparar;
							horasUso[contarFecha] = transicionHora;
							
							//almacena puntaje de la reunion
							puntajeReunion [contarFecha] = puntajeBloque; 
							
							//Contador para los resultados (dia, hora y puntaje reunion)
							contarFecha = contarFecha +1;
							

							//Resetear valores
							contarBloques = 0;
							transicionHora = null;
							asistentes = 0;
							correosMiembrosAsistentes = null;
							
							//retroceder el contador para ver las otras combinaciones
							z = z - (duracion - 1);

						}
						
					}else{
						
						//reiniciar todos los valores
						asistentesAux = 0;
						correosMiembrosAsistentesAux = null;
						sumatoriaValoresBloque = 0;
						contarBloques = 0;
						transicionHora = null;
						correosMiembrosAsistentes = null;
						asistentes = 0;
						puntajeBloque = 0;
					}
					
				   }
				}else{
					//resetear todos los valores en caso de no alcanzar duracion
					asistentesAux = 0;
					correosMiembrosAsistentesAux = null;
					sumatoriaValoresBloque = 0;
					contarBloques = 0;
					transicionHora = null;
					correosMiembrosAsistentes = null;
					asistentes = 0;
					puntajeBloque = 0;
				
				}
				
			}else{
				//Cambia la fecha se comienza denuevo el conteo
				fechaComparar = dias[z];
				
				//disminuir el contador para que no pase por alto el bloque que cambia la fecha
				z = z -1;
				
				//resetear valores
				asistentesAux = 0;
				correosMiembrosAsistentesAux = null;
				sumatoriaValoresBloque = 0;
				contarBloques = 0;
				transicionHora = null;
				correosMiembrosAsistentes = null;
				asistentes = 0;
				puntajeBloque = 0;
				
			}
		}
		return ok(mensajeReunion.render(session("email"), correo, bloque, resultados, diasUso, horasUso, contarFecha, idGrupo, miembros, listaMiembros, asistentes));
	}
}
