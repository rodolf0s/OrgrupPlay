package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;

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
	
    @Formats.NonEmpty
    @Column(nullable=false)
    public String estado;
    
    @Formats.NonEmpty
	@Column(length=7, nullable=true)
	public String notificado;
    
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
		if (Notificaciones.getGrupoAdmin(email))
			integrante.notificado = "admin";
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
	
	/**
	 * Cuenta la cantidad de miembros que posee un grupo a traves del id
	 * @param id
	 * @return
	 */
	public static Integer contarMiembros(Long id) {
		return find.where()
		.eq("grupo_id", id)
		.findRowCount();
	}
	
	
	/*
	 * Retorna el id de los grupos en estado inactivo 
	 */
//	public static List<Integrante> cuentaGruposInactivos(String email) {
//		return Integrante.find.where()
//				.eq("usuario_correo", email)
//				.eq("estado", "inactivo")
//				.findList();
//	}
	
	/*
	 * Cambia el estado del integrante en un grupo al aceptar la invitacion para ingresar a un grupo
	 */
	public static void cambiaEstadoIntegrante(String email, Long grupoId) {
		Integrante integrante = find.where().eq("usuario_correo", email).eq("grupo_id", grupoId).findUnique();
				integrante.estado = "activo";
				integrante.update();
	}
	
	public static Page<Integrante> page(int page, String filter, String email) {
		return 
				find	
					.where()
					.eq("usuario_correo", email)
					.eq("estado", "inactivo")
					.orderBy().asc("usuario_correo")
					.findPagingList(10)
					.getPage(page);
	}
	
}