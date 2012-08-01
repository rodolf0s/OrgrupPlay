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
@Table(name="reunion")
public class Reunion extends Model {
	
	@Id
	public Long id;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date fecha;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	public Time hora;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(length=50, nullable=false)
	public String nombre;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(length=500, nullable=true)
	public String descripcion;

	@ManyToOne
	public Grupo grupo;
	
	// Consultas
	
	public static Model.Finder<Long,Reunion> find = new Model.Finder(Long.class, Reunion.class);
}
