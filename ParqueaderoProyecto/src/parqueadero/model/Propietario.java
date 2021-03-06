package parqueadero.model;

import java.io.Serializable;

public class Propietario implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre;
	private String id;
	private String correo;
	private String telefono;
	
	//CONSTRUCTOR
    public Propietario(String nombre, String id, String correo, String telefono){
	    this.nombre=nombre;
	    this.id=id;
	    this.correo=correo;
	    this.telefono=telefono;
    }
    
    public Propietario() {
    	
    }
    
    //GETTERS AND SETTERS
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre=nombre;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id=id;
	}
	
	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	//TO STRING
	public String toString(){
		return nombre + "  "+id;
	}
	
}
