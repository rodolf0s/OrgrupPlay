package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;

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
	
	/**
	 * Busca si es usuario ya esta agregado, para mostrar o no el boton agregar al buscar usuarios
	 */
	public static String compruebaUsuarioExistente(String usuario1, String usuario2) {
		Contacto contacto = Ebean.find(Contacto.class)
				.select("usuario2_correo")
				.where()
				.eq("usuario1_correo", usuario1)
				.eq("usuario2_correo", usuario2)
				.findUnique();
		try {
	    	if(contacto.usuario2.correo.toString().isEmpty()) {
	    		return "";
	    	} else {
	    		return contacto.usuario2.correo.toString();
	    	}
    	} catch(Exception e) {}
		return "";
    }
	
	
//	public static void cambiaEstado(Usuario usuario1, Usuario usuario2) {
//		Contacto contacto = find.ref(i)
//		contacto.estado = 
//	}
	
	/**
	 * Busca una lista con todos los contactos que tiene el usuario
	 * pero que no esten en el grupo.
	 * 
	 * Importante: aun no funciona, la lista la retorna vacia
	 * @param correo
	 * @return empty. esta mal el INNER JOIN
	 */
	public static List<SqlRow> getContactos(String correo) {
		String sql = "SELECT c.id, c.usuario2_correo FROM contacto c INNER JOIN integrante i on c.usuario2_correo = i.usuario_correo WHERE c.usuario1_correo = :correo AND c.usuario2_correo != i.usuario_correo";
		
		return Ebean.createSqlQuery(sql).setParameter("correo", correo).findList();
	}
	
}
