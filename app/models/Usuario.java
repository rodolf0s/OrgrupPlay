package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Usuario extends Model{

	@Id
	@Email
	public String correo;
	@Required
	public String nombre;
	@Required
	public String pass;
	
	public List<Usuario> contacto;
	
	public String getCorreo(){
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}
	
	public String getNombre(){		
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getPass(){
		return pass;
	}
	
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public Usuario(String correo, String nombre) {
		this.correo = correo;
		this.nombre = nombre;
	}
}
