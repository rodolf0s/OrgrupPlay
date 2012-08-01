package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name="contacto")
public class Contacto extends Model {
	
	@Id
	public Long id;
	
	@ManyToOne
	public Usuario usuario_correo1;
	
	@ManyToOne
	public Usuario usuario_correo2;
	
	// Consultas
	
	public static Model.Finder<Long,Contacto> find = new Model.Finder(Long.class, Contacto.class);	
	
}
