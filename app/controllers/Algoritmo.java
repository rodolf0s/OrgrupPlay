package controllers;

import models.Integrante;
import models.Reunion;
import models.Tarea;
import models.Usuario;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class Algoritmo extends Controller {

    public static Result index() {
        return ok(views.html.grupo.reunion.render());
    }

    public static Result calcula() {
        Form<Reunion> formReunion = form(Reunion.class).bindFromRequest();
        Reunion reunion = formReunion.get();

        return ok(views.html.grupo.muestra.render(
            reunion.nombre,
            reunion.descripcion,
            reunion.fecha_inicio.toString(),
            reunion.fecha_fin.toString(),
            reunion.hora_inicio.toString(),
            reunion.hora_fin.toString(),
            reunion.duracion.toString()));
    }
}