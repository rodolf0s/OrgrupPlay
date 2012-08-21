package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

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
	
	public static Finder<String,Usuario> find = new Finder<String,Usuario>(String.class, Usuario.class);

	
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
    
    /**
     * acutializa el estado del usuario, una vez activada
     */
 
    
    public static void actualizaEstado(String correo, Integer idVerificador) {
    	Ebean.createSqlUpdate(
    			"update usuario set estado = 'Activada' where " +
    			"correo = '"+correo+"' and id_verificador = '"+idVerificador+"'"
    			).execute();
    }
    
    /**
     * verifica que la cuenta este activada
     */
    
    public static boolean cuentaActivada(String correo) {
    	return find.where()
    			.eq("correo", correo)
    			.eq("estado", "Activada")
    			.findRowCount() == 1;
    }
    
    /**
     * Obtiene la password de la BD de un determiando usuario
     */
    
    public static String getPassword(String correo) {
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
    
    /**
     * Actualiza el nombre de usuario
     */
    
    public void setNombre(String nombre, String correo) {
        Usuario usuario = find.ref(correo);
        usuario.nombre = nombre;
        usuario.update();    
    }
    
    /**
     * Actualiza el nombre y la imagen del usuario
     */
    
    public void setImagen(String correo, String nombre, String imagen) {
        Usuario usuario = find.ref(correo);
        usuario.nombre = nombre;
        usuario.imagen = imagen;
        usuario.update();    
    }
    
    /**
     * Actualiza la contraseña del usuario
     */
    
    public void setPassword(String correo, String password) {
    	Usuario usuario = find.ref(correo);
    	usuario.password = password;
    	usuario.update();
    }
    
    /**
     * Obtiene una lista de los usuarios, para agregar a sus contactos
     */
    public static List<Usuario> listaUsuarios(String nombre){
    	return find.where()
    			.or(Expr.like("nombre", "%"+nombre+"%"),  Expr.like("correo", "%"+nombre+"%")).findList();
  	
    }
    
    /**
     * Muestra nombre de usuario atraves del email
     */
    
	public static String muestraNombre(String email){
	    Usuario usuario = Ebean.find(Usuario.class)
	    		.select("nombre")
	    		.where()
	    		.eq("correo", email)
	    		.findUnique();
	    return usuario.nombre;
	}
	
	/**
	 * Obtiene imagen de usuario a traves del correo
	 */
	
	public static String muestraImagen(String email) {
		Usuario usuario = Ebean.find(Usuario.class)
				.select("imagen")
				.where()
				.eq("correo", email)
				.findUnique();
		return usuario.imagen;
	}
}