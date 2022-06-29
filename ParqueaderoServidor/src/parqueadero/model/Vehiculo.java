package parqueadero.model;

import java.io.Serializable;

public class Vehiculo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String placa;
	private String modelo;
	private Propietario propiet;
	private Servicio serv;
	private String tipoVeh;
	private int velMax;
	
	//CONSTRUCTOR SUPERCLASS
	public Vehiculo(String placa, String modelo, Propietario propiet, Servicio serv, String tipoVeh, int velMax) {
		super();
		this.placa = placa;
		this.modelo = modelo;
		this.propiet = propiet;
		this.serv = serv;
		this.tipoVeh = tipoVeh;
		this.velMax = velMax;
	}
	
	public Vehiculo() {
		
	}
	
	//GETTERS AND SETTERS
	public String getPlaca() {
		return placa;
	}
	
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	
	public String getModelo() {
		return modelo;
	}
	
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	
	public Propietario getPropiet() {
		return propiet;
	}
	
	public void setPropiet(Propietario propiet) {
		this.propiet = propiet;
	}
	
	public Servicio getServ() {
		return serv;
	}
	
	public void setServ(Servicio serv) {
		this.serv = serv;
	}

	public String getTipoVeh() {
		return tipoVeh;
	}

	public void setTipoVeh(String tipoVeh) {
		this.tipoVeh = tipoVeh;
	}

	public int getVelMax() {
		return velMax;
	}

	public void setVelMax(int velMax) {
		this.velMax = velMax;
	}

	@Override
	public String toString() {
		return "Vehiculo [placa=" + placa + ", propiet=" + propiet + "]";
	}
	
	
}
