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
import com.avaje.ebean.annotation.Where;

import play.data.format.Formats;
import play.data.validation.Constraints;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name="mensaje")
public class Mensaje extends Model{

    @Id
    public Long id;

    @Formats.NonEmpty
    @Column(nullable=false)
    @Formats.DateTime(pattern="dd/MM/yyyy HH:mm:ss")
    public Date fecha;

    @ManyToOne
    public Usuario remitente;

    @Formats.NonEmpty
    @Column(length=50, nullable=false)
    public String destinatario;

    @Column(length=50, nullable=false)
    public String asunto;

    @Column(length=300, nullable=false)
    public String mensaje;

    @Formats.NonEmpty
    @Column(length=2, nullable=false)
    public String leido;

    @Formats.NonEmpty
    @Column(length=8, nullable=false)
    public String estado;

    @Formats.NonEmpty
    @Column(length=2, nullable=false)
    public String notificado;

    public static Finder<Long,Mensaje> find = new Finder<Long,Mensaje>(Long.class, Mensaje.class);

    public static List<Mensaje> listaMensajesRecibidos(String email){
        return find.where()
                .eq("destinatario", email)
                .eq("estado", "recibido")
                .orderBy().desc("fecha")
                .findList();
    }

    public static List<Mensaje> muestraId(Long id) {
        return find.where()
                .eq("id", id)
                .findList();
    }

    public static List<Mensaje> listaMensajesEnviados(Usuario email) {
        return find.where()
                .eq("remitente", email)
                .eq("estado", "enviado")
                .findList();
    }

    public static List<Mensaje> mensajesNuevosRecibidos(String email) {
        return find.where()
                .eq("destinatario", email)
                .eq("leido", "no")
                .eq("estado", "recibido")
                .findList();
    }

    /**
     * Cambia el estado del mensaje(campo leido), cuando lo lee el usuario
     */
    public static void cambiaEstadoLeido(Long id, String email) {
        Ebean.createSqlUpdate(
                "update mensaje set leido = 'si' where " +
                "remitente_correo != '"+email+"' and id = '"+id+"'"
                ).execute();
    }

    /**
     * Elimina mensajes a atraves del id
     */
    public static void eliminaMensaje(Long id) {
        Ebean.createSqlUpdate(
                "delete from mensaje where " +
                "id = "+id+""
                ).execute();
    }

    /**
     * Crea copia al enviar un mensaje con el estado "recibido"
     * @param fecha
     * @param remitente
     * @param destinatario
     * @param asunto
     * @param mensaje
     * @param leido
     */
    public static void copiaMensaje(Date fecha, Usuario remitente, String destinatario, String asunto, String mensaje, String leido) {
        Mensaje mensaje2 = new Mensaje();

        mensaje2.fecha = fecha;
        mensaje2.remitente = remitente;
        mensaje2.destinatario = destinatario;
        mensaje2.asunto = asunto;
        mensaje2.mensaje = mensaje;
        mensaje2.leido = leido;
        mensaje2.estado = "recibido";
        if (Notificaciones.getMensaje(destinatario))
            mensaje2.notificado = "no";
        else
            mensaje2.notificado = "si";
        mensaje2.save();
    }

    /**
     * Busca en la BD 10 campos desde la pagina que le envian.
     * Es para los mensajes recibidos
     *
     * @param page es la pagina actual ej 2
     * @param filter
     * @param email
     * @return
     */
    public static Page<Mensaje> page(int page, String filter, String email) {
        return
                find
                    .where()
                    .eq("destinatario", email)
                    .eq("estado", "recibido")
                    .ilike("asunto", "%" + filter + "%")
                    .orderBy().desc("fecha")
                    .findPagingList(10)
                    .getPage(page);
    }

    /**
     * Busca en la BD 10 campos desde la pagina que le envian.
     * Es para los mensajes enviados
     *
     * @param page es la pagina actual ej 2
     * @param filter
     * @param email
     * @return
     */
    public static Page<Mensaje> page2(int page, String filter, String email) {
        return
                find
                    .where()
                    .eq("remitente_correo", email)
                    .eq("estado", "enviado")
                    .ilike("asunto", "%" + filter + "%")
                    .orderBy().desc("fecha")
                    .findPagingList(10)
                    .getPage(page);
    }

    /**
     * Busca en la BD 10 campos desde la pagina que le envian.
     * Es para los mensajes nuevos recibidos
     * @param page
     * @param filter
     * @param email
     * @return
     */
    public static Page<Mensaje> page3(int page, String filter, String email) {
        return
                find
                    .where()
                    .eq("destinatario", email)
                    .eq("estado", "recibido")
                    .eq("leido", "no")
                    .ilike("asunto", "%" + filter + "%")
                    .orderBy().desc("fecha")
                    .findPagingList(10)
                    .getPage(page);
    }
}
