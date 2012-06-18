package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import java.util.*;
import views.html.*;

import models.*;

public class Login extends Controller{
    
    /**
     * Declaramos un formulario para el login, con el modelo Usuario.
     */ 
    //public static Form<Usuario> loginForm = form(Usuario.class);
  
    /**
     * Mostrar la pagina del formulario.
     */ 
    public static Result login() {
        return ok(login.render());
    }

    public static Result registro() {
        return ok(registro.render());
    }
  
    /**
     * metodo que comprueba el usuario y contraseña.
     */
    public static Result comprobarLogin() {
    	String hola = new String();
        Form<Usuario> form = form(Usuario.class).bindFromRequest();
        // loginForm.bindFromRequest();
        Usuario usuario = form.get();
        
        if(usuario.correo.equals("rodo")) {
        	hola = "RODOLFO";
        }
        return ok(muestra.render(hola, usuario.pass));
    } 

    public static Result comprobarRegistro() {
        // loginForm.bindFromRequest();
        // Usuario usuario = loginForm.get();
        return ok();
    } 
  
}