package parqueadero.model;

import java.io.Serializable;

public class Carro extends Vehiculo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Carro(String placa, String modelo, Propietario propiet, Servicio serv, String tipoVeh, int velMax) {
		super(placa, modelo, propiet, serv, tipoVeh, velMax);
	}
	
	public Carro() {
		
	}
	@Override
	public String toString() {
		return "Carro [ "+ getPlaca() + " ]";
	}
	
}
