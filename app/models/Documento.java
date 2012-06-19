package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Documento extends Model {

	@Id
	private String fecha;
	@Required
	private String nombre;
	@Required
	private String ruta;

	public String getFecha() {		
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
	
	public String getRuta() {		
		return ruta;
	}
	
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
}
