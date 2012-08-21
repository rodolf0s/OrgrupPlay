package models;

import java.util.List;

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
@Table(name="contacto")
public class Contacto extends Model {
	
	@Id
	public Long id;
	
	@ManyToOne
	public Usuario usuario1;
	
	@ManyToOne
	public Usuario usuario2;
	
	@Formats.NonEmpty
	@Column(nullable=false)
	public String amigos;
	
	// Consultas
	
	public static Finder<Long,Contacto> find = new Finder<Long,Contacto>(Long.class, Contacto.class);	
	
	
	public static List<Contacto> listaContactosPendientes(String email){
		return Contacto.find.where().eq("usuario2_correo", email).eq("amigos", "no").findList();
	}
	
	public static void estado(Long id){
		Contacto contacto = find.ref(id);
		contacto.amigos = "si";
		contacto.update();
	}
	
	/**
	 * 
	 * Listo contactos(destinario) para enviar mensaje
	 */
	
	public static List<Contacto> listaAmigos(Usuario email) {
		return find.where()
				.eq("usuario1", email)
				.findList();
	}
}
