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

@Entity
@Table(name="archivo")
public class Archivo extends Model{
	
	@Id
	@Constraints.Required
	@Formats.NonEmpty
	@Column(length=350, nullable=false)
	public String ruta;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(length=255, nullable=false)
	public String nombre;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	public Date fecha;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	public Time hora;
	
	@ManyToOne
	public Usuario usuario_correo;
	
	@ManyToOne
	public Reunion reunion_fecha;
	
	@ManyToOne
	public Reunion reunion_hora;
	
	@ManyToOne
	public Reunion reunion_grupo_id;
	
	public static Model.Finder<String,Archivo> find = new Model.Finder(String.class, Archivo.class);
	
}
