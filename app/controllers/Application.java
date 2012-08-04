package controllers;

import java.io.File;
import java.io.IOException;

import models.Correo;
import models.Usuario;

import play.data.Form;
import play.mvc.*;

import views.html.*;

import org.apache.commons.mail.*;

public class Application extends Controller {
	
	// -- Autentificacion.
    
    public static class Login {
        
        public String correo;
        public String password;
        
        public String validate() {
            if(Usuario.authenticate(correo, password) == null) {
                return "Correo o contraseña invalido";
            }
            return null;
        }        
    }
  
	public static Result index() {
		return ok(index.render());
	}
	
	public static Result login() {
		return ok(login.render(form(Login.class), ""));
	}
	
	public static Result registro() {
		return ok(registro.render(form(Usuario.class), ""));
	}
	
	public static Result recuperar() {		
		return ok(olvidoPassword.render(""));
	}	
	
	public static Result contacto() {
		return ok(contacto.render());
	}
	
	public static Result about() {
        return ok(about.render());
	}
	
	public static Result logout() {
		session().clear();
        return redirect(routes.Application.index());
	}
	
	
	/*
	 * Comprueba el Login y si la cuenta esta activada o no.
	 */	
	public static Result comprobarLogin() {
		
	      Form<Login> loginForm = form(Login.class).bindFromRequest();
	      
	      if(loginForm.hasErrors()) {
	          return badRequest(login.render(loginForm, ""));
	      } else {
	    	  Login user = loginForm.get();

	    	  if(Usuario.cuentaActivada(user.correo)) {
	    		  session("email", loginForm.get().correo);
		          return redirect(routes.Home.index());
	    	  } else {
	    		  return ok(login.render(form(Login.class), "Esta cuenta no esta activada"));
	    	  }	          
	      }
	  }
	
	
	/*
	 * comprueba el registro del usuario, enviandole a la cuenta un enlace para que
	 * posteriormente active la cuenta.
	 */
	public static Result comprobarRegistro() throws IOException, EmailException {
		
		Integer id;
		Form<Usuario> formRegistro = form(Usuario.class).bindFromRequest();
		
		if(formRegistro.hasErrors()) {
            return badRequest(registro.render(form(Usuario.class), ""));
		} else {
			Usuario user = formRegistro.get();
			
			if(Usuario.esMiembro(user.correo)) {
				
				return ok(registro.render(form(Usuario.class), "El correo ya existe"));
				
			} else {
				
				do {
					/*
					 * genera un numero de 9 digitos para usarlo posteriormente
					 * en el enlace que se le envia al correo y validar la cuenta.
					 */
					id = (int)(Math.random()*1000000000);
					
				} while(Usuario.estaIdVerificador(id) == true);
				
				/*
				 * Crea una imagen para el usuario por defecto, guardandola en /images/usuarios,
				 * con el nombre del correo + las extension (.gif).
				 */
				String path = "./public/images/usuarios/" + user.correo + ".gif";
				org.apache.commons.io.FileUtils.copyFile(new File("./public/images/usuarios/user.gif"), new File(path));
				
				
				/*
				 * Agrega los datos faltantes al modelo usuario. para guardarlos posteriormente a la BD.
				 */
				user.id_verificador = id;
				user.imagen = user.correo + ".gif";
				user.estado = "desactivada";
				
				/*
				 * Guarda el nuevo usuario a la BD
				 */
				user.save();
				
				/*
				 * Envia un correo al usuario que se acaba de registrar 
				 * con un enlace para verificar la cuenta.
				 */
				Email email = new SimpleEmail();
			    email.setSmtpPort(587);
			    email.setAuthenticator(new DefaultAuthenticator("orgrup.service@gmail.com", "orgrup2012"));
			    email.setDebug(false);
			    email.setHostName("smtp.gmail.com");
			    email.setFrom("orgrup.service@gmail.com");
			    email.setSubject("Confirmar Cuenta Orgrup");
			    email.setMsg("Hola " +user.nombre +"\nPara completar el registro haga click en el siguiente enlace para activar la cuenta http://localhost:9000/VerificaCuenta?correo="+user.correo+"&id="+id+"\n\nUn saludo cordial\nEl equipo de Orgrup.");
			    email.addTo(user.correo);
			    email.setTLS(true);
			    email.send();
			    
			    return ok(informaciones.render("Bienvenidos a la red de Orgrup, red de agendas que te permitirá " +
			    		"llevar un registro de todas tus actividades, podrás buscar amigos y colegas para gestionar " +
			    		"reuniones de forma automática, subir apuntes, documentos y más!. " +
			    		"Confirme su cuenta a través del enlace enviado a su correo.", "Registro"));
//			    return ok(registrado.render());
			}		
		}
	}
	
	
	/*
	 * Metodo que envia la contraseña al usuario por correo electronico.
	 */
	public static Result recuperarPassword() throws EmailException {
		Form<Usuario> formLogin = form(Usuario.class).bindFromRequest();
		
		if(formLogin.hasErrors()) {
          return badRequest(olvidoPassword.render(""));
		} else {
			String correo = formLogin.get().correo;
			String password = Usuario.recuperarPassword(correo);
			
			// si la password no esta vacia envia el correo con dicha password
			if (!password.isEmpty()) {
				
				// Envia un correo al usuario registrado para recuperar contraseña
				Email email = new SimpleEmail();
			    email.setSmtpPort(587);
			    email.setAuthenticator(new DefaultAuthenticator("orgrup.service@gmail.com", "orgrup2012"));
			    email.setDebug(false);
			    email.setHostName("smtp.gmail.com");
			    email.setFrom("orgrup.service@gmail.com");
			    email.setSubject("Recuperar Contraseña");
			    email.setMsg("Ud a solicitado su clave de ingreso, su clave es '"+password+"'");
			    email.addTo(correo);
			    email.setTLS(true);
			    email.send();
			    
			    return ok(informaciones.render("Su contraseña a sido enviada a su correo electronico.", "Recuperar Contraseña"));
			} else {
				return ok(olvidoPassword.render("Correo incorrecto"));
			}
		}
	}
	
	/*
	 * guarda el mensaje del usuario en la BD
	 */
	public static Result guardaMensaje() {
	
		Form<Correo> formContacto = form(Correo.class).bindFromRequest();
		
		if(formContacto.hasErrors()){
			return badRequest(contacto.render());
		} else{
			Correo mensaje = formContacto.get();

			mensaje.save();
			return ok(informaciones.render("Su mensaje a sido enviado exitosamente.", "Contacto"));
		}
	}
	
	
	/*
	 * Metodo para validar al usuario anteriormente registrado
	 * comparando el id llegado por la url al que esta en la BD
	 */
	public static Result verificaCuenta(String correo, Integer id) {
		
		if(Usuario.verificaCuenta(correo, id)) {
			Usuario.actualizaEstado(correo, id);
			return ok(verificaCuenta.render());
		}
		return ok();
	}
}  