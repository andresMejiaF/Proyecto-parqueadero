package parqueadero.model;

import java.io.Serializable;

public class Servicio implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Empleado emp;
	private Tipo tipo;
	
	//CONSTRUCTOR
	public Servicio(Tipo tipo, Empleado emp) {
		this.tipo=tipo;
		this.emp=emp;
	}
	
	public Servicio() {
		
	}

	//GETTERS AND SETTERS
	public Empleado getEmp() {
		return emp;
	}

	public void setEmp(Empleado emp) {
		this.emp = emp;
	}

	public Tipo getTipo() {
		return tipo;
	}
	
	public void Tipo(Tipo tipo) {
		this.tipo = tipo;
	}
	
	public String getTipoString() {
		return tipo.toString();
	}
	@Override
	public String toString() {
		return "[" + tipo + " - " + emp + " ]";
	}
	
	
}