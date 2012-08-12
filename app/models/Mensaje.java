package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.format.Formats;
import play.data.validation.Constraints;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name="mensaje")
public class Mensaje extends Model{

	@Id
	public Long id;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	@Formats.DateTime(pattern="dd/MM/yyyy HH:mm:ss")
	public Date fecha;
	
	@ManyToOne
	public Usuario remitente;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	public String destinatario;
	
	@Constraints.Required
	@Formats.NonEmpty
	@Column(nullable=false)
	public String mensaje;
	
	public static Finder<Long,Mensaje> find = new Finder<Long,Mensaje>(Long.class, Mensaje.class);
	
}
