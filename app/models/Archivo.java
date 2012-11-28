package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
@Table(name="archivo")
public class Archivo extends Model {

    @Id
    public Long id;

    @Formats.NonEmpty
    @Column(length=300, nullable=false)
    public String nombre;

    @Formats.NonEmpty
    @Column(nullable=false)
    @Formats.DateTime(pattern="dd/MM/yyyy")
    public Date fecha;

    @Formats.NonEmpty
    @Column(nullable=false)
    @Formats.DateTime(pattern="HH:mm:ss")
    public Date hora;

    @ManyToOne
    public Usuario usuario;

    @ManyToOne
    public Reunion reunion;

    // Consultas

    public static Finder<Long,Archivo> find = new Finder<Long,Archivo>(Long.class, Archivo.class);

    /**
     * Elimina todo los archivos.
     *
     * @param id es el id de la reunion.
     */
    public static void eliminaTodo(Long id) {
        String sql = "DELETE FROM archivo WHERE reunion_id = " + id;
        Ebean.createSqlUpdate(sql).execute();
    }
}
