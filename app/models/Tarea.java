package models;

import java.util.Date;

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
	
	@Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date fecha_inicio;
	
	@Column(nullable=false)
	@Formats.DateTime(pattern="HH:mm:ss")
	public Date hora_inicio;
	
	@Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date fecha_fin;
	
	@Column(nullable=false)
	@Formats.DateTime(pattern="HH:mm:ss")
	public Date hora_fin;
	
	@Constraints.Required
    @Column(length=60, nullable=false)
	public String nombre;
	
    @Column(length=500, nullable=true)
	public String descripcion;
	
    @Column(nullable=false)
	public Integer prioridad;
	
	@ManyToOne
	public Usuario usuario;
	
	// Consultas
	
	public static Finder<Long,Tarea> find = new Finder<Long,Tarea>(Long.class, Tarea.class);
}
