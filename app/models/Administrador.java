package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import play.data.format.Formats;
import play.data.validation.Constraints;

import play.db.ebean.Model;

@Entity
@Table(name="administrador")
public class Administrador extends Model{
	
	@Id
	@Constraints.Required
	@Formats.NonEmpty
	@Column(length=20, nullable=false)
	public String usuario;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(length=20, nullable=false)
	public String password;

	public static Model.Finder<String,Administrador> find = new Model.Finder(String.class, Administrador.class);
}
