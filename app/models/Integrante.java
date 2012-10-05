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
import play.mvc.Result;

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
	 * @param id Es el id del integrante dentro del grupo.
	 */
	public static void eliminaIntegrante(Long id) {
		Integrante integrante = find.ref(id);
		integrante.delete();
	}

	/**
	 * Elimina un integrante del grupo por correo.
	 * 
	 * @param correo es el correo del integrante a eliminar.
	 */
	public static void eliminaIntegrante(String correo) {
		String sql = "DELETE FROM integrante WHERE usuario_correo = '"+correo+"'";
		Ebean.createSqlUpdate(sql).execute();
	}
	
	/**
	 * Elimina todos los integrantes de un grupo.
	 * 
	 * @param id es el id del grupo.
	 */
	public static void eliminaTodos(Long id) {
		String sql = "DELETE FROM integrante WHERE grupo_id = " + id;
		Ebean.createSqlUpdate(sql).execute();
	}
	
	/**
	 * Verifica si el usuario es Admin del grupo.
	 * 
	 * @param id del grupo
	 * @param correo del usuario
	 * @return si es admin devuelve true.
	 */
	public static boolean esAdmin(Long id, String correo) {
		Integrante integrante = Ebean.find(Integrante.class)  
    	        .select("tipo")  
    	        .where()
    	        .eq("grupo_id", id)
    	        .eq("usuario_correo", correo)
    	        .findUnique();
		if (integrante.tipo == 1)
			return true;
		else
			return false;
	}
	
	/**
	 * Se asigna admin a otro integrante.
	 * 
	 * @param email es el correo del nuevo admin.
	 * @param idGrupo
	 */
	public static void agregarAdmin(String email, Long idGrupo) {
		Integrante integrante = find.where().eq("grupo_id", idGrupo).eq("usuario_correo", email).findUnique();
		integrante.tipo = 1;
		integrante.update();
	}
	
	/**
	 * Se le quita el admin al actual.
	 * 
	 * @param email es el correo del admin a quitar.
	 * @param idGrupo
	 */
	public static void quitarAdmin(String email, Long idGrupo) {
		Integrante integrante = find.where().eq("grupo_id", idGrupo).eq("usuario_correo", email).findUnique();
		integrante.tipo = 2;
		integrante.update();
	}
	
	/**
	 * 
	 * Obtiene el id del usuario en un grupo
	 * @return
	 */
	public static Long ObtieneId(Long id, String correo){
		Integrante integrante = Ebean.find(Integrante.class)
				.select("id")
				.where()
				.eq("grupo_id", id)
				.eq("usuario_correo", correo)
				.findUnique();
		return integrante.id;
		
	}
	
	/**
	 * Verifica si un contacto del usuario ya pertenece a un grupo determinado
	 * para no mostrarlo en agregar integrante.
	 * 
	 * @param email
	 * @param idGrupo
	 * @return
	 */
	public static boolean esIntegrante(String email, Long idGrupo) {
		Integrante integrante = find.where().eq("grupo_id", idGrupo).eq("usuario_correo", email).findUnique();
		try {
			if (integrante.usuario.correo.isEmpty())
				return true;
			else
				return false;
			} catch(Exception e) {}
		return true;
	}
	
	public static Integer contarMiembros(Long id) {
		return find.where()
		.eq("grupo_id", id)
		.findRowCount();
	}
}