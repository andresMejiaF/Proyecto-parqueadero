package parqueadero.services;

import parqueadero.model.Empleado;
import parqueadero.model.Tipo;

public interface IParqueadero {
	 public void verificarVehiculoEstaEnParqueadero(String placa);
	 public void verificarDisponibilidadPuesto();
	 public String listarRegistros();
     public boolean crearEmpleado(Empleado empl);
 	 public boolean agregarServicio(Tipo tipo, Empleado emp);
}
