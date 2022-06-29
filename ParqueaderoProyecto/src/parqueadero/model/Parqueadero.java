package parqueadero.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javafx.scene.control.Alert;
import parqueadero.ModelFactoryController.ModelFactoryController;
import parqueadero.services.IParqueadero;

public class Parqueadero implements IParqueadero, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList <Registro> listaRegistros;
//	private ArrayList <Puesto> listaPuestos;
	private Puesto listaPuestos[];
	private ArrayList <Servicio> listaServicios;
	private ArrayList <Empleado> listaEmpleados;
	private int puestosDisponibles = 25; 
	private int vehiculosParqueadero = 0;
	
	//CONSTRUCTOR
	public Parqueadero() {
		listaRegistros = new ArrayList<>();
		listaPuestos = new Puesto[24];
		listaServicios = new ArrayList<>();
		listaEmpleados = new ArrayList<>();
		llenarPuestos();
	}
	

	//GETTERS AND SETTERS
	public ArrayList<Registro> getListaRegistros() {
		return listaRegistros;
	}

	public void setListaRegistros(ArrayList<Registro> listaRegistros) {
		this.listaRegistros = listaRegistros;
	}

	public Puesto[] getListaPuestos() {
		return listaPuestos;
	}

	public void setListaPuestos(Puesto[] listaPuestos) {
		this.listaPuestos = listaPuestos;
	}

	public ArrayList<Servicio> getListaServicios() {
		return listaServicios;
	}

	public void setListaServicios(ArrayList<Servicio> listaServicios) {
		this.listaServicios = listaServicios;
	}
	
	public ArrayList<Empleado> getListaEmpleados() {
		return listaEmpleados;
	}

	public void setListaEmpleados(ArrayList<Empleado> listaEmpleados) {
		this.listaEmpleados = listaEmpleados;
	}

	//METODOS
	public boolean agregarServicio(Tipo tipo, Empleado emp) {
		Servicio serv;
		serv = new Servicio(tipo, emp);
		listaServicios.add(serv);
		return true;
	}
	
	public boolean crearEmpleado(Empleado empl) {
		listaEmpleados.add(empl);
		return true;
	}

	public String listarRegistros(){
        String lis="";
        String fyh="";
        Calendar horayfecha = new GregorianCalendar();
        
        for(int i=0; i<listaRegistros.size(); i++){
        	horayfecha = listaRegistros.get(i).getFecha();
        	fyh = horayfecha.get(Calendar.DAY_OF_MONTH) + "/" + horayfecha.get(Calendar.MONTH) + "/" + horayfecha.get(Calendar.YEAR)
        						+ "		" + horayfecha.get(Calendar.HOUR_OF_DAY) + ":" + horayfecha.get(Calendar.MINUTE);
        	
            lis += "\n\n" + (i+1) + "° Registro		\nFecha y Hora: " + fyh
            		+ "\nVehiculo de placa: " + listaPuestos[i].getVehi().getPlaca()
                    + "\nUbicado en el puesto [: " + listaPuestos[i].getPos() + "] del parqueadero"
                    + "\nHa solicitado el servicio de: " + listaPuestos[i].getVehi().getServ().getTipo()
                    + "\nTeniendo a " + listaPuestos[i].getVehi().getServ().getEmp();
        }
        return lis;
    }
	
	public String listarCedulasEmpleados(){
        String lis="";
        
        for(int i=0; i<listaEmpleados.size(); i++){
            lis += "N° " + (i+1) + "	[" + listaEmpleados.get(i).getCedula() + "]\n";
        }
        
        return lis;
    }
	
	@Override
	public void verificarVehiculoEstaEnParqueadero(String placa) {
		boolean encontrado=false;
		for(int i=0; i<vehiculosParqueadero && encontrado==false;i++) {
			System.out.println("NUEMEORAOEWRAKJ"+i);
			if(placa.equals(listaPuestos[i].getVehi().getPlaca()) && listaPuestos[i].getVehi().getPlaca()!=null) {
				Puesto pues = listaPuestos[i];
				Alert mensaje;
	            mensaje = new Alert (Alert.AlertType.INFORMATION);
	            mensaje.setContentText("¡El vehiculo [" + pues.getVehi().getPlaca() + "] se encuentra en el parqueadero en la plaza (" + (pues.getPos()+1)+")");
	            mensaje.showAndWait();
	            encontrado=true;
			}
		}
		if(encontrado==false){
			Alert mensaje;
            mensaje = new Alert (Alert.AlertType.INFORMATION);
            mensaje.setContentText("¡El vehiculo NO se encuentra en el parqueadero!\nSi desea ingresar un vehiculo, llene el formulario de arriba.");
            mensaje.showAndWait();
		}
	}

	@Override
	public void verificarDisponibilidadPuesto() {	
		if(puestosDisponibles>0) {
			Alert mensaje;
            mensaje = new Alert (Alert.AlertType.INFORMATION);
            mensaje.setContentText("Hay un total de [" + puestosDisponibles + "] plazas disponibles en el parqueadero.");
            mensaje.showAndWait();
		}else {
			Alert mensaje;
            mensaje = new Alert (Alert.AlertType.WARNING);
            mensaje.setContentText("¡El parqueadero esta lleno!");
            mensaje.showAndWait();
		}
	}

	public boolean crearRegistro(Registro reg) {
		listaRegistros.add(reg);
		return true;
	}
	
	public void ocuparPuesto(Puesto pues, int lugar) {
		listaPuestos[lugar] = pues;
		puestosDisponibles--;
		vehiculosParqueadero++;
	}
	
	public void liberarPuesto() {
		puestosDisponibles++;
	}
	
	public boolean crearServicio(Servicio serv) {
		listaServicios.add(serv);
		return true;
	}
	
	public void llenarPuestos() {
		Propietario pro = new Propietario("PUESTO_LIBRE", "PUESTO_LIBRE", "PUESTO_LIBRE", "PUESTO_LIBRE");
		Empleado em = new Empleado("PUESTO_LIBRE", "PUESTO_LIBRE", "PUESTO_LIBRE");
		Servicio ser = new Servicio(Tipo.PARQUEO, em);
		Vehiculo veh = new Vehiculo("PUESTO_LIBRE", "PUESTO_LIBRE", pro, ser, "PUESTO_LIBRE", 0);
		for(int i=0; i<24; i++) {
			Puesto pu = new Puesto(i, veh, true);
			listaPuestos[i] = pu;
		}
	}
	
	
}
