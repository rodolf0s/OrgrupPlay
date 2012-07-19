package models;

import java.sql.Date;

public class Tarea {

	public Date fecha;
	public String nombre;
	public String descripcion;
	public Integer prioridad;
	public String usuario_correo;
	
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public void setPrioridad(Integer prioridad) {
		this.prioridad = prioridad;
	}
	
	public void setUsuario_correo(String usuario_correo) {
		this.usuario_correo = usuario_correo;
	}
}
