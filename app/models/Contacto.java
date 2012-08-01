package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
@Table(name="contacto")
public class Contacto extends Model{
	
	@Id
	@ManyToOne
	@Constraints.Required
	@Formats.NonEmpty
	@Column(length=50, nullable=false)
	public Usuario usuario_correo1;
	
	@Id
	@ManyToOne
	@Constraints.Required
	@Formats.NonEmpty
	@Column(length=50, nullable=false)
	public Usuario usuario_correo2;
	

	public static Model.Finder<Usuario,Contacto> find = new Model.Finder(Usuario.class, Contacto.class);	
	
}
