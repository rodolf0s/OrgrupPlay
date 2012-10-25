package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name="administrador")
public class Administrador extends Model {

    @Id
    @Column(length=20, nullable=false)
    public String usuario;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(length=20, nullable=false)
    public String password;

    // Consultas

    public static Finder<String,Administrador> find = new Finder<String,Administrador>(String.class, Administrador.class);

    /**
     *  Auntentificar Administrador
     */

    public static Administrador authenticate(String usuario, String password) {
        return find.where()
            .eq("usuario", usuario)
            .eq("password", password)
            .findUnique();
    }

    /**
     * Actualiza la contraseña del administrador
     */

    public void setPassword(String usuario, String password) {
        Administrador admin = find.ref(usuario);
        admin.password = password;
        admin.update();
    }

    /**
     * Obtiene la password de la BD del administrador
     */

    public static String getPassword(String usuario) {
        Administrador admin = Ebean.find(Administrador.class)
                .select("password")
                .where().eq("usuario", usuario)
                .findUnique();
        try {
            if(admin.password.isEmpty()) {
                return "";
            } else {
                return admin.password;
            }
        } catch(Exception e) {}
        return "";
    }
}
