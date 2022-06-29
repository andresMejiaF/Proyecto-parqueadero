package parqueadero.model;

import java.io.Serializable;

public class Empleado implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre;
	private String cedula;
	private String numTelefono;
	
	//CONSTRUCTOR 
	public Empleado(String nombre, String cedula, String numTelefono) {
		this.nombre = nombre;
		this.cedula = cedula;
		this.numTelefono = numTelefono;
	}
	
	public Empleado() {
		// TODO Auto-generated constructor stub
	}

	//GETTERS AND SETTERS
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getNumTelefono() {
		return numTelefono;
	}

	public void setNumTelefono(String numTelefono) {
		this.numTelefono = numTelefono;
	}

	//TOSTRING
	@Override
	public String toString() {
		return "[" + nombre + " - " + cedula + "]";
	}
	
}
