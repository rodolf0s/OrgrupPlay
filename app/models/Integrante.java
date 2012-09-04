package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

import play.data.format.Formats;
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
	
    @Formats.NonEmpty
    @Column(nullable=false)
	public Integer tipo;
	
    @Formats.NonEmpty
    @Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date fecha_ingreso;
	
	// Consultas
	
	public static Finder<Long,Integrante> find = new Finder<Long,Integrante>(Long.class, Integrante.class);
	
	/**
	 * Elimina un integrante del grupo.
	 * 
	 * @param id
	 * 		Es el id del integrante dentro del grupo.
	 */
	public static void eliminaIntegrante(Long id) {
		Integrante integrante = find.ref(id);
		integrante.delete();
	}
}
