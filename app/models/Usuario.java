package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.data.validation.*;

@Entity
public class Usuario extends Model{

	@Id
	@Email
	@MinLength(50)
	public String correo;
	@Required
	@MinLength(50)
	public String nombre;
	@Required
	@MinLength(10) 
	public String pass;
	private Usuario usuario;
	
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
//	
//		
//	public void agregarContacto() {
//		
//	}
//	
//	public void eliminarContacto() {
//		
//	}
//	
//	public void agregarTarea(){
//		
//	}
//	
//	public void eliminarTarea(){
//		
//	}
//	
//	public void editarTarea(){
//		
//	}
//	
//	public void crearGrupo(){
//		
//	}
//	
//	public void abandonarGrupo(){
//		
//	}

	
}
