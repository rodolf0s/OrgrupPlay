package models;

import java.sql.Date;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name="archivo")
public class Archivo extends Model {
	
	@Id
	@Column(length=350, nullable=false)
	public String ruta;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(length=255, nullable=false)
	public String nombre;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date fecha;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	public Time hora;
	
	@ManyToOne
	public Usuario usuario;
	
	@ManyToOne
	public Reunion reunion;
	
	// Consultas
	
	public static Finder<String,Archivo> find = new Finder<String,Archivo>(String.class, Archivo.class);	
}
