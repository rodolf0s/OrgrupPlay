package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name="contacto")
public class Contacto extends Model {
	
	@Id
	public Long id;
	
	@ManyToOne
	public Usuario usuario1;
	
	@ManyToOne
	public Usuario usuario2;
	
	// Consultas
	
	public static Finder<Long,Contacto> find = new Finder<Long,Contacto>(Long.class, Contacto.class);	
}
