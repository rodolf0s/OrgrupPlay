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
@Table(name="grupo")
public class Grupo extends Model {
	
	@Id
	public Long id;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(length=25, nullable=false)
	public String nombre;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(length=10, nullable=false)
	public String distintivo;
	
	// Consultas
	
	public static Finder<Long,Grupo> find = new Finder<Long,Grupo>(Long.class, Grupo.class);	
}
