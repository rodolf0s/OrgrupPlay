package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import play.data.format.Formats;
import play.data.validation.Constraints;

import play.db.ebean.Model;

@Entity
@Table(name="correo")
public class Correo extends Model{
	
	@Id
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	public Integer id;
	
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
	
	public static Model.Finder<Integer,Correo> find = new Model.Finder(Integer.class, Correo.class);
	
}
