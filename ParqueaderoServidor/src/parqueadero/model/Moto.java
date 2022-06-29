package parqueadero.model;

import java.io.Serializable;

public class Moto extends Vehiculo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Moto(String placa, String modelo, Propietario propiet, Servicio serv, String tipoVeh, int velMax) {
		super(placa, modelo, propiet, serv, tipoVeh, velMax);
	}
	
	public Moto() {
		
	}
	
	@Override
	public String toString() {
		return "Moto [ "+ getPlaca() + " ]";
	}
}
