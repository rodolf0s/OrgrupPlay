package controllers;

import models.Integrante;
import models.Reunion;
import models.Tarea;
import models.Usuario;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Algoritmo extends Controller {

    public static Result index() {
        return ok(views.html.grupo.reunion.render());
    }

    public static Result calcula() throws ParseException {
        Form<Reunion> formReunion = form(Reunion.class).bindFromRequest();
        Reunion reunion = formReunion.get();
        List<Integrante> listaMiembros = Integrante.buscaMiembros(reunion.grupo.id);
        Integer duracion = reunion.duracion;
        Integer [] horasDisponibles = new Integer[duracion];
        List<String> listPosibles = new ArrayList<String>();
        Integer contadorDuracion = 0;
        Integer x = 0;
        Integer r = 0;
        Integer z = 0;
        String ii = new String();
        String jj = new String();

        // return ok(""+horasDisponibles.length);
        for (int i = reunion.fecha_inicio.getDate(); i <= reunion.fecha_fin.getDate(); i++ ) {
            for (int j = reunion.hora_inicio.getHours(); j < reunion.hora_fin.getHours(); j++) {
                Integer mes = reunion.fecha_inicio.getMonth() + 1;
                Date nuevaFechaI = new SimpleDateFormat("yyyy-MM-dd").parse("2012-" + mes + "-" + i);
                Date nuevaHoraI = new SimpleDateFormat("HH:mm:ss").parse(j + ":00:00");
                // Tarea tarea = Tarea.find
                //         .where()
                //         .eq("fecha_inicio", nuevaFechaI)
                //         .eq("hora_inicio", nuevaHoraI)
                //         .findUnique();

                String tarea = Tarea.getTarea(nuevaFechaI, nuevaHoraI);
                
                if (listPosibles.size() < 3) {
                    if (tarea.equals("")) {
                        // horasDisponibles[contadorDuracion] = j;
                    	listPosibles.add(";dia "+ i + " hora " + j);
                        contadorDuracion++;
                    } else {
                        // horasDisponibles = null;
                        x++;
                    }
                    ii += ""+i;
                    jj += ""+j;
                }
                /*else if (horasDisponibles.length == duracion) {
                    for (int x = duracion; i < duracion; i++) {
                        listPosibles.add("" + horasDisponibles[i].toString() );
                    }
                }*/
                z++;

            }
        }

        String a = ""+contadorDuracion;
        String b = ""+x;
        String c = ""+z;
        String l = ""+listPosibles.size();
        return ok(a + " " + b + " " + c + " i=" + ii + " j=" + jj + " l=" + l + " datos: " + listPosibles.get(0) +
        		listPosibles.get(1) + listPosibles.get(2));

        // return ok(views.html.grupo.muestra.render(
        //     reunion.nombre,
        //     reunion.descripcion,
        //     reunion.fecha_inicio.toString(),
        //     reunion.fecha_fin.toString(),
        //     reunion.hora_inicio.toString(),
        //     reunion.hora_fin.toString(),
        //     reunion.duracion.toString(),
        //     listaMiembros,
        //     horasDisponibles,
        //     r));
    }
}