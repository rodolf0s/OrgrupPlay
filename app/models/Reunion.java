package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Reunion extends Model {
 
	@Id
	public String fecha;
	public String nombre;
	public String descripcion;
	public String comentario;
	public Documento documento;
	
	public String getFecha(){
		return fecha;
	}

	public void setFecha(String fecha){
		this.fecha = fecha;
	}
	
	public String getNombre(){		
		return nombre;
	}
	
	public void setNombre(String nombre){
		this.nombre = nombre;
	}
	
	public String getDescripcion(){
		return descripcion;
	}
	
	public void setDescripcion(String descripcion){
		this.descripcion = descripcion;
	}
	
	public String getComentario(){
		return descripcion;
	}
	
	public void setComentario(String descripcion){
		this.descripcion = descripcion;
	}
	
	public void setDocumento(Documento documento) {
		this.documento = documento;
	}
	public Documento listarDocumento() {
		return documento;
	}
		
}
