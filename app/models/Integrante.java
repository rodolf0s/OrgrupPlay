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
@Table(name="integrante")
public class Integrante extends Model {
	
	@Id
	public Long id;
	
	@ManyToOne
	public Usuario usuario;
	
	@ManyToOne
	public Grupo grupo;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(nullable=false)
	public Integer tipo;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date fecha_ingreso;
	
	// Consultas
	
	public static Finder<Long,Integrante> find = new Finder<Long,Integrante>(Long.class, Integrante.class);
}
