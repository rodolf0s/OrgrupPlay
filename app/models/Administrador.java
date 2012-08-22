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
@Table(name="administrador")
public class Administrador extends Model {
	
	@Id
	@Column(length=20, nullable=false)
	public String usuario;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(length=20, nullable=false)
	public String password;

	// Consultas
	
	public static Finder<String,Administrador> find = new Finder<String,Administrador>(String.class, Administrador.class);
	
	/**
	 *	Auntentificar Administrador	
	 */

	public static Administrador authenticate(String usuario, String password) {
        return find.where()
            .eq("usuario", usuario)
            .eq("password", password)
            .findUnique();
    }
}
