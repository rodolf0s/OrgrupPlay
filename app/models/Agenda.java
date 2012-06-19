package models;


import play.db.ebean.Model;


public class Agenda extends Model{
	
	private Tarea tarea;
	private Usuario usuario;
	private Grupo grupo;

	
	public Tarea listarTarea(){
		return tarea;
	}
	
	public Usuario listarContacto(){
		return usuario;
	}

	public Grupo listarGrupo(){
		return grupo;
	}
 
}
