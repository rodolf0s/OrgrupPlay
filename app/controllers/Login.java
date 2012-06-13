package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import java.util.*;
import views.html.*;

import models.*;

public class Login extends Controller {
    
    /**
     * Define una forma de envolver la clase de usuario.
     */ 
    final static Form<Usuario> loginForm = form(Usuario.class);
  
    /**
     * Mostrar un formulario en blanco.
     */ 
    public static Result login() {
        return ok(login.render(loginForm));
    }
  
    /**
     * Mostrar un formulario pre-llenado con una cuenta existente.
     */
    public static Result edit() {
    	loginForm.bindFromRequest();
        Usuario datos = loginForm.get();
        datos.getCorreo();
        datos.getPass();
        return ok("Weeeena!");
    } 
  
}