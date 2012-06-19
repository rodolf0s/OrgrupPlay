package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Grupo extends Model {
	 
	@Id
	private Integer id;
	private String nombre;
	private String distintivo;
	private Reunion reunion;
	
	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}
	
	public String getNombre(){		
		return nombre;
	}
	
	public void setNombre(String nombre){
		this.nombre = nombre;
	}
	
	public String getDistintivo(){
		return distintivo;
	}
	
	public void setDistintivo(String distintivo){
		this.distintivo = distintivo;
	}
	
	public Reunion ListarReunion(){
		return reunion;
	}
	
}
