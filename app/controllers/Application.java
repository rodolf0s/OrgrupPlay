package controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import models.Correo;
import models.Usuario;

import play.*;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Request;

import views.html.*;

import org.apache.commons.mail.*;

public class Application extends Controller {
	
	// Instancia la conexion
	private static ConexionJDBC conexion = ConexionJDBC.getInstancia();
  
	public static Result index() {
		return ok(index.render());
	}
	
	public static Result login() {
		return ok(login.render(""));
	}
	
	public static Result registro() {
		return ok(registro.render(""));
	}
	
	public static Result comprobarLogin() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		String correo;
		String pass;
		String sql;
		ResultSet rs = null;
		
		Form<Usuario> formLogin = form(Usuario.class).bindFromRequest();
		
		if(formLogin.hasErrors()) {
            return badRequest(login.render(""));
		} else {			
			Usuario user = formLogin.get();
			correo = user.correo;
			pass = user.password;
			
			try {				
				Connection con = conexion.abre();
				
				sql = "SELECT nombre, correo, estado FROM usuario WHERE correo = '"+correo+"' AND password = '"+pass+"' ";
				PreparedStatement st = con.prepareStatement(sql); 
				rs = st.executeQuery();
				
				rs.next();
				if(rs.getRow() == 1) {
					
					if(rs.getString("estado").equals("activada")) {
						session("email", correo);
						session("nombre", rs.getString("nombre"));
						return redirect(routes.Home.index());
					} else {
						return ok(login.render("Esta cuenta no esta activada"));
					}					
				} else {
					
					return ok(login.render("Correo o contraseña incorrecta"));
				}
				
			} catch (Exception e){
				e.printStackTrace();
			}finally{
				if(rs != null)
					rs.close();
				}
			
		}
		return ok();
	}	
	
	public static Result comprobarRegistro() throws SQLException, IOException {
		String nombre;
		String correo;
		String pass;
		String sql = "";
		Connection con = null;
		Integer id;
		Boolean estaId = true;
		
		Form<Usuario> formRegistro = form(Usuario.class).bindFromRequest();
		
		if(formRegistro.hasErrors()) {
            return badRequest(registro.render(""));
		} else {
			Usuario user = formRegistro.get();
			pass = user.password;
			nombre = user.nombre;
			correo = user.correo;
			
			
			try {
				con = conexion.abre();				
				
				// Verifica si existe el correo en la BD
				sql = "SELECT correo FROM usuario WHERE correo = '"+correo+"'";
				PreparedStatement st = con.prepareStatement(sql);
				ResultSet rs = st.executeQuery();
				
				rs.next();
				if(rs.getRow() >= 1) {
							
					return ok(registro.render("El correo ya existe"));
				} else {
					try {
						
						/*
						 * Genera un numero aleatorio de 9 digitos
						 * y comprueba que no exista en la BD
						 */
						do {
							id = (int)(Math.random()*1000000000);
							
							// Verifica si existe el id en la BD
							sql = "SELECT id_verificador FROM usuario WHERE id_verificador = '"+id+"'";
							st = con.prepareStatement(sql);
							rs = st.executeQuery();
							
							rs.next();
							
							if(rs.getRow() >= 1) {
								estaId = true;
							} else {
								estaId = false;
								
								String path = "./public/images/usuarios/" + correo + ".gif";
								org.apache.commons.io.FileUtils.copyFile(new File("./public/images/usuarios/user.gif"), new File(path));
								
								sql = "INSERT INTO usuario(correo, nombre, password, imagen, id_verificador, estado) VALUES('"+correo+"', '"+nombre+"', '"+pass+"', '"+correo+".gif', '"+id+"', 'desactivada')";
								st = con.prepareStatement(sql);
								st.executeUpdate();
							}
							
						} while(estaId == true);						
						
						
						// Envia un correo al usuario registrado
						Email email = new SimpleEmail();
					    email.setSmtpPort(587);
					    email.setAuthenticator(new DefaultAuthenticator("orgrup.service@gmail.com", "orgrup2012"));
					    email.setDebug(false);
					    email.setHostName("smtp.gmail.com");
					    email.setFrom("orgrup.service@gmail.com");
					    email.setSubject("Confirmar Cuenta Orgrup");
					    email.setMsg("Hola " +nombre +"\nPara completar el registro haga click aqui para activar la cuenta http://localhost:9000/VerificaCuenta?id="+id+"\n\nUn saludo cordial\nEl equipo de Orgrup.");
					    email.addTo(correo);
					    email.setTLS(true);
					    email.send();
						
						return ok(registrado.render());
//						return ok(test.render(sql, "", "", ""));
					} catch (Exception e) {
						e.printStackTrace();
						
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}finally{
				conexion.cierra();
			}			
		}
		return ok();
	}
	
	public static Result logout() {
		session().clear();
        return redirect(routes.Application.index());
	}
	
	public static Result about() {
        return ok(about.render());
	}
	
	public static Result guardaMensaje() {
		String nombre_remitente;
		String correo;
		String mensaje;
		ResultSet rs = null;
		String sql;
		
		Form<Correo> formContacto = form(Correo.class).bindFromRequest();
		
		if(formContacto.hasErrors()){
			return badRequest(contacto.render());
		} else{
			Correo mensaje_contacto = formContacto.get();
			nombre_remitente = mensaje_contacto.nombre;
			correo = mensaje_contacto.correo;
			mensaje = mensaje_contacto.mensaje;
			
			try {
				Connection con = conexion.abre();
				
				sql = "INSERT INTO correo(id, remitente, correo, mensaje) VALUES(nextval('id_mensaje'), '"+nombre_remitente+"', '"+correo+"', '"+mensaje+"')";
				PreparedStatement st = con.prepareStatement(sql);
				st.executeUpdate();
				return redirect(routes.Application.index());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ok();
	}
	
	public static Result contacto() {
		return ok(contacto.render());
	}
}
  