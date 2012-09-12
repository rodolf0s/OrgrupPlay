package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
@Table(name="reunion")
public class Reunion extends Model {
	
	@Id
	public Long id;
	
	@Formats.NonEmpty
	@Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date fecha_inico;
	
	@Formats.NonEmpty
	@Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date fecha_fin;
	
	@Formats.NonEmpty
	@Column(nullable=false)
	@Formats.DateTime(pattern="HH:mm:ss")
	public Date hora_inicio;
	
	@Formats.NonEmpty
	@Column(nullable=false)
	@Formats.DateTime(pattern="HH:mm:ss")
	public Date hora_fin;
	
    @Formats.NonEmpty
    @Column(length=50, nullable=false)
	public String nombre;
	
    @Formats.NonEmpty
    @Column(length=500, nullable=true)
	public String descripcion;
    
    @Formats.NonEmpty
    @Column(length=20, nullable=false)
	public String estado;

	@ManyToOne
	public Grupo grupo;
	
	// Consultas
	
	public static Finder<Long,Reunion> find = new Finder<Long,Reunion>(Long.class, Reunion.class);
}