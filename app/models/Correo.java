package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;

import play.data.format.Formats;
import play.data.validation.Constraints;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name="correo")
public class Correo extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(length=60, nullable=false)
    public String nombre;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(length=60, nullable=false)
    public String asunto;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(length=50, nullable=false)
    public String correo;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(length=500, nullable=false)
    public String mensaje;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(nullable=false)
    public Integer estado;

    // Consultas

    public static Finder<Long,Correo> find = new Finder<Long,Correo>(Long.class, Correo.class);

      /**
     * Lista todos los mensajes recibidos
     */
    public static List<Correo> listaCorreos(){
        return find.all();
    }

    /**
     * Busca el mensaje que se leera
     */
    public static List<Correo> muestraId(Long id) {
        return find.where()
                .eq("id", id)
                .findList();
    }

    /**
     * Actualizar estado a leido
     */

    public void setEstado(Long id) {
        Correo correo = find.ref(id);
        correo.estado = 2;
        correo.update();
    }

    /**
     * Actualizar estado a respondido
     */

    public void setRespuesta(Long id) {
        Correo correo = find.ref(id);
        correo.estado = 3;
        correo.update();
    }

    /**
     * Elimina correos a atraves del id
     */
    public static void eliminaMensaje(Long id) {
        Ebean.createSqlUpdate(
                "delete from correo where " +
                "id = "+id+""
                ).execute();
    }

    /**
     * Busca en la BD 10 campos desde la pagina que le envian.
     * Es para los correos enviados a los administradores
     *
     * @param page es la pagina actual ej 2
     * @param filter
     * @param email
     * @return
     */
    public static Page<Correo> page(int page, String filter) {
        return
                find
                .where()
                .ilike("asunto", "%" + filter + "%")
                .orderBy().desc("nombre")
                .findPagingList(10)
                .getPage(page);
    }
}
