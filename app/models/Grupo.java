package models;

import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.SqlRow;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
@Table(name="grupo")
public class Grupo extends Model {

    @Id
    public Long id;

    @Formats.NonEmpty
    @Column(length=25, nullable=false)
    public String nombre;

    @Formats.NonEmpty
    @Column(length=150, nullable=false)
    public String descripcion;

    @Formats.NonEmpty
    @Column(length=150, nullable=false)
    public String imagen;

    // Consultas

    public static Finder<Long,Grupo> find = new Finder<Long,Grupo>(Long.class, Grupo.class);

    /**
     * Obtiene todos los grupos que está el usuario, para mostrarlos en el home "/App"
     *
     * @param correo
     * @return una lista con todos los grupos encontrados.
     */
    public static List<SqlRow> getGrupos(String correo) {
        String sql = "SELECT g.id, g.nombre, g.descripcion, g.imagen FROM grupo g " +
                "INNER JOIN integrante i ON g.id = i.grupo_id " +
                "WHERE i.usuario_correo = :correo " +
                "AND i.estado = 'activo' " +
                "ORDER BY g.nombre ASC";
        return Ebean.createSqlQuery(sql).setParameter("correo", correo).findList();
    }

    /**
     * Obtiene un grupo determinado, en el que pertenece el usuario,
     * para mostrarlo en la vista "/grupo"
     *
     * @param correo
     * @param id identificador del grupo seleccionado en la vista home (/App)
     * @return un objeto con el grupo encontrado.
     */
    public static SqlRow getGrupo(String correo, Long id) {
        String sql = "SELECT i.usuario_correo, i.tipo, g.id, g.nombre, g.descripcion, g.imagen " +
                "FROM grupo g INNER JOIN integrante i ON g.id = i.grupo_id " +
                "WHERE i.usuario_correo = :correo AND g.id = :id";
        return Ebean.createSqlQuery(sql).setParameter("correo", correo).setParameter("id", id).findUnique();
    }

    public static Page<Grupo> pageGrupos(int page) {
    		return find
    				.where()
                    .findPagingList(10)
                    .getPage(page);
    }
    
}
