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
@Table(name="tarea")
public class Tarea extends Model {

	@Id
	public Long id;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date fecha_inicio;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	public Time hora_inicio;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date fecha_fin;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	public Time hora_fin;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(length=60, nullable=false)
	public String nombre;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(length=500, nullable=true)
	public String descripcion;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(nullable=false)
	public Integer prioridad;
	
	@ManyToOne
	public Usuario usuario;
	
	// Consultas
	
	public static Model.Finder<Long,Tarea> find = new Model.Finder(Long.class, Tarea.class);
}
