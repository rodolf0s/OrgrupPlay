package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import play.data.format.Formats;
import play.data.validation.Constraints;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name="correo")
public class Correo extends Model {
	
	@Id
	public Long id;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(length=60, nullable=false)
	public String nombre;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(length=50, nullable=false)
	public String correo;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(length=500, nullable=false)
	public String mensaje;
	
	// Consultas 
	
	public static Finder<Long,Correo> find = new Finder<Long,Correo>(Long.class, Correo.class);	
}
