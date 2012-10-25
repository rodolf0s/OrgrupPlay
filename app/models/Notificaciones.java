package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

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

    /**
     * Verifica si hay que notificar cuando tiene una tarea que
     * vence el dia de hoy.
     *
     * @param email
     * @return true si hay que notificar
     */
    public static boolean getTarea(String email) {
        return find.where()
                .eq("tarea", "si")
                .eq("usuario_correo", email)
                .findRowCount() == 1;
    }

    /**
     * Verifica si hay que notificar al usuario cuando
     * le llege un nuevo mensaje.
     *
     * @param email
     * @return true si hay que notificar
     */
    public static boolean getMensaje(String email) {
         return find.where()
                 .eq("mensaje", "si")
                 .eq("usuario_correo", email)
                 .findRowCount() == 1;
    }

    /**
     * Verifica si hay que notificar al usuario cuando
     * alguien le envia una solicitud de contacto.
     *
     * @param email
     * @return true si hay que notificar
     */
    public static boolean getContacto(String email) {
        return find.where()
                 .eq("contacto", "si")
                 .eq("usuario_correo", email)
                 .findRowCount() == 1;
    }

    /**
     * Verifica si hay que notificar al usuario cuando
     * lo agregan a un grupo.
     *
     * @param email
     * @return true si hay que notificar
     */
    public static boolean getGrupoAgregan(String email) {
        return find.where()
                 .eq("grupo_agregan", "si")
                 .eq("usuario_correo", email)
                 .findRowCount() == 1;
    }

    /**
     * Verifica si hay que notificar al usuario cuando
     * lo nombren admin en un grupo.
     *
     * @param email
     * @return true si hay que notificar
     */
    public static boolean getGrupoAdmin(String email) {
        return find.where()
                 .eq("grupo_admin", "si")
                 .eq("usuario_correo", email)
                 .findRowCount() == 1;
    }

    /**
     * Verifica si hay que notificar al usuario cuando
     * lo eliminen de un grupo.
     *
     * @param email
     * @return true si hay que notificar
     */
    public static boolean getGrupoEliminan(String email) {
        return find.where()
                 .eq("grupo_eliminan", "si")
                 .eq("usuario_correo", email)
                 .findRowCount() == 1;
    }
}
