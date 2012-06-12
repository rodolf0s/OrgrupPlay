package models;

public class Agenda {
	
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
