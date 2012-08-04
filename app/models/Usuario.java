package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
@Table(name="usuario")
public class Usuario extends Model {
	
	@Id
	@Column(length=50, nullable=false)
	public String correo;
	
    @Formats.NonEmpty
    @Column(length=60, nullable=false)
	public String nombre;
	
    @Formats.NonEmpty
    @Column(length=20, nullable=false)
	public String password;
	
    @Formats.NonEmpty
    @Column(length=60, nullable=false)
	public String ciudad;
	
    @Formats.NonEmpty
    @Column(length=300, nullable=true)
	public String leyenda;
	
    @Formats.NonEmpty
    @Column(length=350, nullable=false)
	public String imagen;
	
    @Formats.NonEmpty
    @Column(nullable=false)
	public Integer id_verificador;
	
	@Formats.NonEmpty
    @Column(length=11, nullable=false)
	public String estado;	
	
	// Consultas
	
	public static Model.Finder<String,Usuario> find = new Model.Finder(String.class, Usuario.class);
	
	/**
	 *	Auntentifica el usuario	
	 */

	public static Usuario authenticate(String correo, String password) {
        return find.where()
            .eq("correo", correo)
            .eq("password", password)
            .findUnique();
    }

    /**
     * Consulta si existe un usuario determinado
     */
	
    public static boolean esMiembro(String correo) {
        return find.where()
            .eq("correo", correo)
            .findRowCount() > 0;
    }
    
    /**
     * Consulta si existe el id_verificador
     */
    
    public static boolean estaIdVerificador(Integer idVerificador) {
    	return find.where()
    		.eq("id_verificador", idVerificador)
    		.findRowCount() > 0;
    }
    
    /**
     * comprueba la cuenta, viendo si el id llegado por url
     * esta en la BD y si es unico
     */

    public static boolean verificaCuenta(String correo, Integer idVerificador) {
    	return find.where()
    		.eq("correo", correo)
    		.eq("id_verificador", idVerificador)
    		.findRowCount() == 1;
    }
    
    /*
     * acutializa el estado del usuario, una vez activada
     */
    
    public static void actualizaEstado(String correo, Integer idVerificador) {
    	Ebean.createSqlUpdate(
    			"update usuario set estado = 'Activada' where " +
    			"correo = '"+correo+"' and id_verificador = '"+idVerificador+"'"
    			).execute();
    }
    
    /*
     * verifica que la cuenta este activada
     */
    
    public static boolean cuentaActivada(String correo) {
    	return find.where()
    			.eq("correo", correo)
    			.eq("estado", "Activada")
    			.findRowCount() == 1;
    }
    
    /*
     * consulta para recuperar la password de un determinado usuario
     */
    
    public static String recuperarPassword(String correo) {
    	Usuario usuario = Ebean.find(Usuario.class)  
    	        .select("password")  
    	        .where().eq("correo", correo)  
    	        .findUnique();
    	try {
	    	if(usuario.password.isEmpty()) {
	    		return "";
	    	} else {
	    		return usuario.password;
	    	}
    	} catch(Exception e) {}
		return "";
    }
    
    /*
     * Actualiza el nombre de usuario
     */
    
    public void setNombre(String nombre, String correo) {
        Usuario usuario = find.ref(correo);
        usuario.nombre = nombre;
        usuario.update();    
    }
    
    /*
     * Actualiza el nombre y la imagen del usuario
     */
    
    public void setImagen(String correo, String nombre, String imagen) {
        Usuario usuario = find.ref(correo);
        usuario.nombre = nombre;
        usuario.imagen = imagen;
        usuario.update();    
    }
    
    /*
     * Actualiza la contraseña del usuario
     */
    
    public void setPassword(String correo, String password) {
    	Usuario usuario = find.ref(correo);
    	usuario.password = password;
    	usuario.update();
    }
}