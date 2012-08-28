package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Expr;

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
    

    

}
