package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
@Table(name="usuario")
public class Usuario extends Model {
	
	@Id
	@Column(length=50, nullable=false)
	public String correo;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(length=60, nullable=false)
	public String nombre;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(length=20, nullable=false)
	public String password;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(length=60, nullable=false)
	public String ciudad;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(length=300, nullable=true)
	public String leyenda;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(length=350, nullable=false)
	public String imagen;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(nullable=false)
	public Integer id_verificador;
	
	@Constraints.Required
    @Formats.NonEmpty
    @Column(length=11, nullable=false)
	public String estado;	
	
	// Consultas
	
	public static Model.Finder<String,Usuario> find = new Model.Finder(String.class, Usuario.class);
	
	public static Usuario authenticate(String email, String password) {
        return find.where()
            .eq("correo", email)
            .eq("password", password)
            .findUnique();
    }

}
