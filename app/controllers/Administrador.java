package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import models.Admin;
import models.Usuario;

import play.*;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Request;

import views.html.*;
import views.html.administrar.*;
import org.apache.commons.mail.*;

public class Administrador extends Controller {
	
	// Instanciamos la conexion
	private static ConexionJDBC conexion = ConexionJDBC.getInstancia();
	//Iniciar como administrador	
	public static Result comprobarLogin() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		String usuario;
		String pass;
		String sql;
		ResultSet rs = null;
		
		Form<Admin> formLogin = form(Admin.class).bindFromRequest();
		
		if(formLogin.hasErrors()) {
            return badRequest(administrador.render(""));
		} else {			
			Admin user = formLogin.get();
			usuario = user.usuario;
			pass = user.password;
			
			try {				
				Connection con = conexion.abre();
				
				sql = "SELECT usuario FROM administrador WHERE usuario = '"+usuario+"' AND password = '"+pass+"' ";
				PreparedStatement st = con.prepareStatement(sql); 
				rs = st.executeQuery();
				
				rs.next();
				if(rs.getRow() == 1) {
										
					session("usuario", usuario);
										
					return redirect(routes.Administrador.mensaje());
				} else {
					
					return ok(administrador.render("Usuario o contraseña incorrectos"));
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
	
	//Redirecciona a login de administrador
	public static Result iniciar() {
		return ok(administrador.render(""));
	}
	
	//Cerrar sesion administrador
	public static Result cerrarSesion() {
		session().clear();
		return redirect(routes.Application.index());
	}
	
	//Redirecciona a la pagina mensajes
	public static Result mensaje() {
		
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} 
		
		else {
		return ok(mensaje.render());
		}
	}
	
	//Redirecciona a la pagina cuentas
	public static Result cuentas() {
		

		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} 
		
		else {
		return ok(cuentas.render());
		}
	}
	
//Seccion administradores
	
	//Redireccionar a crear administrador
	public static Result crearAdmin() {
		
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} 
		
		else {
		return ok(crearadmin.render(""));
		}
	}
	
	//Guardar nuevo administrador
	public static Result nuevoAdmin() throws SQLException {
		String usuario;
		String password;
		String sql = "";
		Connection con = null;
		
		Form<Admin> formRegistro = form(Admin.class).bindFromRequest();
		
		if(formRegistro.hasErrors()) {
            return badRequest(crearadmin.render(""));
		} else {
			Admin user = formRegistro.get();
			password = user.password;
			usuario = user.usuario;
			
			try {
				con = conexion.abre();				
				
				// Verifica si existe el usuario en la BD
				sql = "SELECT usuario FROM administrador WHERE usuario = '"+usuario+"'";
				PreparedStatement st = con.prepareStatement(sql);
				ResultSet rs = st.executeQuery();
				
				rs.next();
				if(rs.getRow() >= 1) {
							
					return ok(crearadmin.render("El administrador ya existe"));
				} else {
					try {	
							sql = "INSERT INTO administrador(usuario, password) VALUES('"+usuario+"', '"+password+"')";
							st = con.prepareStatement(sql);
							st.executeUpdate();
						}

					catch (Exception e) {
						e.printStackTrace();
						
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}finally{
				conexion.cierra();
			}			
		}
		return ok(crearadmin.render("El nuevo administrador ha sido registrado"));
	}
	
	//Redirecciona a cambiar contraseña
	public static Result cambiarPass() {
		
		if(!verificaSession()) {
			return redirect(routes.Application.index());
		} 
		
		else {
		return ok(cambiarpass.render());
		}
	}
	
	//redirecciona a eliminar administrador
		public static Result eliminaAdmin() {
			
			if(!verificaSession()) {
				return redirect(routes.Application.index());
			} 
			
			else {
			return ok(eliminaradmin.render());
			}
		}
	
//valida la existencia de una sesion
	public static boolean verificaSession() {
		if(session("usuario") == null) 
			return false;
		else
			return true;
	}
}
