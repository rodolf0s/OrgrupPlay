package models;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;

public class Usuario{
 
	@Required
	@Email
	private String correo;
	@Required
	private String nombre;
	@Required
	private String pass;
	private Usuario usuario;
	
	public String getCorreo(){
		return correo;
	}

	public void setCorreo(String correo){
		this.correo = correo;
	}
	
	public String getNombre(){		
		return nombre;
	}
	
	public void setNombre(String nombre){
		this.nombre = nombre;
	}
	
	public String getPass(){
		return pass;
	}
	
	public void setPass(String pass){
		this.pass = pass;
	}
	
		
	public void agregarContacto(){
		
	}
	
	public void eliminarContacto(){
		
	}
	
	public void agregarTarea(){
		
	}
	
	public void eliminarTarea(){
		
	}
	
	public void editarTarea(){
		
	}
	
	public void crearGrupo(){
		
	}
	
	public void abandonarGrupo(){
		
	}

	
}
