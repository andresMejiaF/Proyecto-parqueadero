package parqueadero.model;

import java.io.Serializable;

public class Puesto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pos;
	private Vehiculo vehi;
	private boolean disponible;

	//CONSTRUCTOR
	public Puesto(int pos, Vehiculo vehi, boolean disponible) {
		super();
		this.pos = pos;
		this.vehi = vehi;
		this.disponible = disponible;
	}
	
	public boolean isDisponible() {
		return disponible;
	}

	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	public Puesto() {
		
	}
	
	//GETTERS AND SETTERS
	public int getPos() {
		return pos;
	}
	
	public void setPos(int pos) {
		this.pos = pos;
	}
	
	public Vehiculo getVehi() {
		return vehi;
	}
	
	public void setVehi(Vehiculo vehi) {
		this.vehi = vehi;
	}
	
	
}
