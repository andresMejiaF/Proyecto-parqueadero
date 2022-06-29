package parqueadero.model;

import java.io.Serializable;
import java.util.Calendar;

public class Registro implements Serializable{

	private static final long serialVersionUID = 1L;
	public Calendar fechayhora;
	public Vehiculo vehic;
	
	//CONSTRUCTOR
	public Registro(Calendar fechayhora, Vehiculo vehic) {
		super();
		this.fechayhora = fechayhora;
		this.vehic = vehic;
	}
	
	public Registro() {
		
	}
	
	//GETTERS AND SETTERS 
	public Calendar getFecha() {
		return fechayhora;
	}

	public void setFecha(Calendar fechayhora) {
		this.fechayhora = fechayhora;
	}

	public Vehiculo getVehic() {
		return vehic;
	}

	public void setVehic(Vehiculo vehic) {
		this.vehic = vehic;
	}

}
