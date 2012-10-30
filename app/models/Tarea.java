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
@Table(name="tarea")
public class Tarea extends Model {

    @Id
    public Long id;

    @Column(nullable=false)
    @Formats.DateTime(pattern="dd/MM/yyyy")
    public Date fecha_inicio;

    @Column(nullable=false)
    @Formats.DateTime(pattern="HH:mm:ss")
    public Date hora_inicio;

    @Column(nullable=false)
    @Formats.DateTime(pattern="dd/MM/yyyy")
    public Date fecha_fin;

    @Column(nullable=false)
    @Formats.DateTime(pattern="HH:mm:ss")
    public Date hora_fin;

    @Constraints.Required
    @Column(length=60, nullable=false)
    public String nombre;

    @Column(length=500, nullable=true)
    public String descripcion;

    @Column(nullable=false)
    public Integer prioridad;

    @ManyToOne
    public Usuario usuario;

    @Column(nullable=true)
    @Formats.DateTime(pattern="dd/MM/yyyy")
    public Date fecha_termino;

    @Column(length=2, nullable=false)
    public String notificado;

    // Consultas

    public static Finder<Long,Tarea> find = new Finder<Long,Tarea>(Long.class, Tarea.class);

    /**
     * Obtiene la descripcion de una tarea.
     *
     * @param id
     * @return la descripcion.
     */
    public static String getDescripcion(Long id) {
        Tarea tarea = Ebean.find(Tarea.class)
                .select("descripcion")
                .where().eq("id", id)
                .findUnique();
        return tarea.descripcion.toString();
    }

    /**
     * Elimina una tarea por id.
     *
     * @param id
     */
     public static void eliminaTarea(Long id) {
         Ebean.createSqlUpdate(
                    "delete from tarea where id = :id"
                ).setParameter("id", id).execute();
     }

     /**
      * Recibe los datos de una tarea para guardarla en la BD.
      * @param nombre
      * @param descripcion
      * @param prioridad
      * @param usuario
      * @param hora_inicio
      * @param hora_fin
      * @param fecha_inicio
      * @param fecha_fin
      */
     public static void setTarea(String nombre, String descripcion, Integer prioridad, Usuario usuario, Date hora_inicio, Date hora_fin, Date fecha_inicio, Date fecha_fin) {
         Tarea tarea = new Tarea();

         tarea.fecha_inicio = fecha_inicio;
         tarea.hora_inicio = hora_inicio;
         tarea.fecha_fin = fecha_fin;
         tarea.hora_fin = hora_fin;
         tarea.nombre = nombre;
         tarea.descripcion = descripcion;
         tarea.prioridad = prioridad;
         tarea.usuario = usuario;
         if (Notificaciones.getTarea(usuario.correo))
            tarea.notificado = "no";
         else
            tarea.notificado = "si";
         tarea.save();
     }

     /**
      * Busca un bloque determminado
      * @param fecha
      * @param hora
      * @param correo
      * @return
      */
     public static Integer buscarTarea(Date fecha, Date hora, String correo) {
         return find.where()
                 .eq("fecha_inicio", fecha)
                 .eq("hora_inicio", hora)
                 .eq("usuario_correo", correo)
                 .findRowCount();
     }
     
     /**
      * Busca la hora de termino de una tarea
      */
     public static Date buscaHoraTermino(Date fecha, Date hora, String correo) {
    	 Tarea tarea = Ebean.find(Tarea.class)
                 .where()
                 .eq("fecha_inicio", fecha)
                 .eq("hora_inicio", hora)
                 .eq("usuario_correo", correo)
                 .findUnique();
         return tarea.hora_fin;
         
     }
     
     /**
      * Busca el valor de una tarea
      * @param fecha
      * @param hora
      * @param correo
      * @return
      */
     public static Integer valorTarea(Date fecha, Date hora, String correo) {
    	 Tarea tarea = Ebean.find(Tarea.class)
                 .where()
                 .eq("fecha_inicio", fecha)
                 .eq("hora_inicio", hora)
                 .eq("usuario_correo", correo)
                 .findUnique();
         return tarea.prioridad;
     }
}
