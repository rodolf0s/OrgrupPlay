package controllers;

import models.Correo;
import models.Integrante;
import models.Reunion;
import models.Tarea;
import models.Usuario;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.grupo.*;
import views.html.home.*;
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
		Long grupo = null;
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
			grupo = objetoReunion.grupo.id;
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

			//Revisamos dias con bloques donde puede asistir del administrador
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
					estado = Tarea.valorTarea(fechaInicio1, horaInicio1, correo);
					if (estado == 3){

						//Obtenemos hora termino de la tarea
						Date termino = Tarea.buscaHoraTermino(fechaInicio1, horaInicio1, correo);

						//Lo transformamos a calendar
						Calendar terminoCalendar = new GregorianCalendar();
						terminoCalendar.setTime(termino);

						 	//Comprobar si la hora fin tarea es mayor a la hora fin reunion
							if(terminoCalendar.after(horaFinCalendar)){

								//Marcar todos los bloques ocupados de la tarea (en caso de que la tarea dure mas de una hora)
								while(horaInicioCalendar.before(horaFinCalendar)) {

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
		miembros = Integrante.contarMiembros(idGrupo.longValue());

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
								asistentesAux = 0;
								correosMiembrosAsistentesAux = null;
								sumatoriaValoresBloque = 0;
								contarBloques = 0;
								transicionHora = null;
								correosMiembrosAsistentes = null;
								asistentes = 0;
								puntajeBloque = 0;

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

		//Ordenar los vectores de mayor a menor segun puntaje
		for(int contador = 0; contador < contarFecha; contador++){

			for(int contador2 = contador + 1; contador2 < contarFecha; contador2++){

				if(puntajeReunion[contador] < puntajeReunion[contador2]){

					//variables auxiliares para todos los vectores
					Integer auxiliar1 = puntajeReunion[contador];
					Date	auxiliar2 = diasUso[contador];
					Date	auxiliar3 = horasUso[contador];

					//Reemplazo de los vectores
					puntajeReunion[contador] = puntajeReunion[contador2];
					diasUso[contador] = diasUso[contador2];
					horasUso[contador] = horasUso[contador2];

					//Ordenamiento
					puntajeReunion[contador2] = auxiliar1;
					diasUso[contador2] = auxiliar2;
					horasUso[contador2] = auxiliar3;

				}
			}

		}

		//Comprobar que exista por lo menos una solucion
		if (puntajeReunion[0] != null){

			Integer [] hora = new Integer[3];
			Integer [] fin = new Integer[3];
			Integer [] dia = new Integer[3];
			Integer [] mes = new Integer[3];
			Integer [] anio = new Integer[3];
			String fecha1 = null;
			String fecha2 = null;
			String fecha3 = null;
			Calendar [] finalReunionCalendar = new GregorianCalendar[3];
			Date [] finalReunion = new Date[3];
			//Preparar las fechas para mostrarlas por pantalla

			//Convertir en Calendar
			Calendar horasUso1 = new GregorianCalendar();
			horasUso1.setTime(horasUso[0]);

			Calendar diasUso1 = new GregorianCalendar();
			diasUso1.setTime(diasUso[0]);

			//Inicio de reunion
			hora[0] = horasUso1.get(Calendar.HOUR_OF_DAY);

			//Termino de reunion
			fin[0] = hora[0] + duracion;

			//Dia de la reunion
			dia[0] = diasUso1.get(Calendar.DAY_OF_MONTH);

			//Mes de la reunion (los meses van de 0 a 11)
			mes[0] = diasUso1.get(Calendar.MONTH) +1;

			//Anio de la reunion
			anio[0] = diasUso1.get(Calendar.YEAR);

			//Fecha de la reunion
			fecha1 = dia[0] + "/" + mes[0] + "/" + anio[0];

			//Hora en que termina la reunion
			finalReunionCalendar[0] = horasUso1;
			finalReunionCalendar[0].add(finalReunionCalendar[0].HOUR_OF_DAY, +duracion);
			finalReunion[0] = finalReunionCalendar[0].getTime();

			if(puntajeReunion[1] != null){

				Calendar horasUso2 = new GregorianCalendar();
				horasUso2.setTime(horasUso[1]);

				Calendar diasUso2 = new GregorianCalendar();
				diasUso2.setTime(diasUso[1]);

				hora[1] = horasUso2.get(Calendar.HOUR_OF_DAY);

				fin[1] = hora[1] + duracion;

				dia[1] = diasUso2.get(Calendar.DAY_OF_MONTH);

				mes[1] = diasUso2.get(Calendar.MONTH) +1;

				anio[1] = diasUso2.get(Calendar.YEAR);

				fecha2 = dia[1] + "/" + mes[1] + "/" + anio[1];

				//Hora en que termina la reunion
				finalReunionCalendar[1] = horasUso2;
				finalReunionCalendar[1].add(finalReunionCalendar[0].HOUR_OF_DAY, +duracion);
				finalReunion[1] = finalReunionCalendar[1].getTime();

					if(puntajeReunion[2] != null){

						Calendar horasUso3 = new GregorianCalendar();
						horasUso3.setTime(horasUso[2]);

						Calendar diasUso3 = new GregorianCalendar();
						diasUso3.setTime(diasUso[2]);

						hora[2] = horasUso3.get(Calendar.HOUR_OF_DAY);

						fin[2] = hora[2] + duracion;

						dia[2] = diasUso3.get(Calendar.DAY_OF_MONTH);

						mes[2] = diasUso3.get(Calendar.MONTH) +1;

						anio[2] = diasUso3.get(Calendar.YEAR);

						fecha3 = dia[2] + "/" + mes[2] + "/" + anio[2];

						//Hora en que termina la reunion
						finalReunionCalendar[2] = horasUso2;
						finalReunionCalendar[2].add(finalReunionCalendar[0].HOUR_OF_DAY, +duracion);
						finalReunion[2] = finalReunionCalendar[2].getTime();
					}
			}


			return ok(mensajeReunion.render(session("email"), puntajeReunion, hora, fin, fecha1, fecha2, fecha3, grupo, duracion, diasUso, horasUso, asistenciaMinima, finalReunion));

		}else{

			return ok(informaciones.render("no se puede generar una reunion con los datos entregados, porfavor intente nuevamente.", "Error Reunion"));
		}
	}

	public static Result guardaReunion(){

		Form<Reunion> formReunion = form(Reunion.class).bindFromRequest();

		if (formReunion.hasErrors()){
			return badRequest(informaciones.render("Error al guardar reunion", "Reunion"));
		}else {
			Reunion reu = formReunion.get();
			reu.save();
			return redirect(routes.Grupos.muestraReuniones(reu.grupo.id));
		}
	}
}
