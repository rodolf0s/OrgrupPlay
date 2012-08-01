package models;

import java.sql.Date;

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
	public Usuario usuario_correo;
	
	@ManyToOne
	public Grupo grupo_id;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(nullable=false)
	public Integer tipo;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date fecha_ngreso;
	
	// Consultas
	
	public static Model.Finder<Long,Integrante> find = new Model.Finder(Long.class, Integrante.class);
}
