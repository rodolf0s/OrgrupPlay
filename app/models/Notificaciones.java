package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name="notificaciones")
public class Notificaciones extends Model {

	@Id
	public Long id;
	
	@Column(length=2, nullable=false)
	public String tarea;
	
	@Column(length=2, nullable=false)
	public String mensaje;
	
	@Column(length=2, nullable=false)
	public String contacto;
	
	@Column(length=2, nullable=false)
	public String grupoAgregan;
	
	@Column(length=2, nullable=false)
	public String grupoEliminan;
	
	@Column(length=2, nullable=false)
	public String grupoAdmin;
	
	@ManyToOne
	public Usuario usuario;
	
	// Consultas
	
	public static Finder<Long,Notificaciones> find = new Finder<Long,Notificaciones>(Long.class, Notificaciones.class);
	
	/**
	 * Actualiza las notificaciones del usuario.
	 * 
	 * @param notificacioness
	 * @param email del usuario a actualizar
	 */
	public static void actualizaNotificaciones(Notificaciones notificacioness, String email) {
		Notificaciones notificaciones = Notificaciones.find.where()
				.eq("usuario_correo", email)
				.findUnique();
		notificaciones.tarea = notificacioness.tarea;
		notificaciones.contacto = notificacioness.contacto;
		notificaciones.mensaje = notificacioness.mensaje;
		notificaciones.grupoAgregan = notificacioness.grupoAgregan;
		notificaciones.grupoEliminan = notificacioness.grupoEliminan;
		notificaciones.grupoAdmin = notificacioness.grupoAdmin;
	
		notificaciones.update();
	}
}
