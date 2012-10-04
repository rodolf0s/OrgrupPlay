package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
@Table(name="reunion")
public class Reunion extends Model {
	
	@Id
	public Long id;
	
	@Formats.NonEmpty
	@Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date fecha_inicio;
	
	@Formats.NonEmpty
	@Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date fecha_fin;
	
	@Formats.NonEmpty
	@Column(nullable=false)
	@Formats.DateTime(pattern="HH")
	public Date hora_inicio;
	
	@Formats.NonEmpty
	@Column(nullable=false)
	@Formats.DateTime(pattern="HH")
	public Date hora_fin;
	
    @Formats.NonEmpty
    @Column(length=50, nullable=false)
	public String nombre;
	
    @Formats.NonEmpty
    @Column(length=500, nullable=true)
	public String descripcion;
    
    @Formats.NonEmpty
    @Column(length=20, nullable=false)
	public String estado;
    
    @Formats.NonEmpty
    @Column(nullable=false)
	public Integer duracion;
    
    @Formats.NonEmpty
    @Column(nullable=false)
    public Integer asistencia;

	@ManyToOne
	public Grupo grupo;
	
	// Consultas
	
	public static Finder<Long,Reunion> find = new Finder<Long,Reunion>(Long.class, Reunion.class);
	
	/**
	 * Elimina todas las reuniones de un grupo.
	 * 
	 * @param id es el id del grupo.
	 */
	public static void eliminaTodo(Long id) {
		String sql = "DELETE FROM reunion WHERE grupo_id = " + id;
		Ebean.createSqlUpdate(sql).execute();
	}
	
	/**
	 * Obtiene todas las reuniones de un grupo determinado.
	 * 
	 * @param id es el id del grupo.
	 * @return una lista con las reuniones.
	 */
	public static List<Reunion> getReuniones(Long id) {
		return find.where().eq("grupo_id", id).findList();
	}
}