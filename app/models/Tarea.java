package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Tarea extends Model {

	@Id
	public String fecha;
	@Required
	public String nombre;
	@Required
	public String descripcion;
	@Required
	public Integer prioridad;
	
	public String getFecha(){
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	
	public String getNombre() {		
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public Integer getPrioridad() {
		return prioridad;
	}
	
	public void setPrioridad(Integer prioridad){
		this.prioridad = prioridad;
	}
	
}

