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
import views.html.helper.select;

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
	 * @param id
	 * 		Es el id del integrante dentro del grupo.
	 */
	public static void eliminaIntegrante(Long id) {
		Integrante integrante = find.ref(id);
		integrante.delete();
	}
	
	/**
	 * Elimina todos los integrantes de un grupo.
	 * 
	 * @param id es el id del grupo.
	 */
	public static void eliminaTodos(Long id) {
		String sql = "DELETE FROM integrante WHERE grupo_id = "+id;
		Ebean.createSqlUpdate(sql).execute();
	}
	
	/**
	 * Verifica si el usuario es Admin del grupo.
	 * 
	 * @param id del correo a evaluar
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
		if(integrante.tipo == 1)
			return true;
		else
			return false;
	}
	
}
