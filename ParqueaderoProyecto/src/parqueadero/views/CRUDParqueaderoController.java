package parqueadero.views;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import javax.swing.JOptionPane;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import parqueadero.model.Carro;
import parqueadero.model.Empleado;
import parqueadero.model.Moto;
import parqueadero.model.Propietario;
import parqueadero.model.Puesto;
import parqueadero.model.Registro;
import parqueadero.model.Servicio;
import parqueadero.model.Tipo;
import parqueadero.model.Vehiculo;
import parqueadero.persistencia.Persistencia;
import parqueadero.ModelFactoryController.*;
import parqueadero.exceptions.CamposVaciosException;
import parqueadero.exceptions.CedulaRepetidaException;
import parqueadero.exceptions.EmpleadoOcupadoException;
import parqueadero.exceptions.InformeVacioException;
import parqueadero.exceptions.LoginCedulaNoExistente;
import parqueadero.exceptions.LoginCerrarSesionSinSesion;
import parqueadero.exceptions.LoginInicioSesionDoble;
import parqueadero.exceptions.MatriculaRepetidaException;
import parqueadero.exceptions.OperacionesSinSesionIniciada;
import parqueadero.exceptions.VelocidadMaximaNegativaException;
 
public class CRUDParqueaderoController {
	@FXML private ComboBox<String> cbServiVehi = new ComboBox<String>();
	@FXML private ComboBox<String> cbServiVehi2 = new ComboBox<String>();
	@FXML private ComboBox<String> cbTipoVehi;
	@FXML private TextField tfNombreProp;
	@FXML private TextField tfCedulaProp;
	@FXML private TextField tfEmailProp;
	@FXML private TextField tfTelefonoProp;
	@FXML private TextField tfPlacaVehi;
	@FXML private TextField tfModeloVehi;
	@FXML private TextField tfVelMaxVehi;
	@FXML private TextField servicioElegido;
	@FXML private TextField cedulaIngresada; 
	@FXML private ComboBox<String> cbVehiculo;

	private int vehiculosEnParqueadero=0;
	private String usuario;
	private boolean registro=false;
	private boolean sesionIniciada=false;
	private boolean admin = false;
	private Main main;
	private String reporteVehiculo="Informacion del reporte: \n";
	private ObservableList<Propietario> listaPropr = FXCollections.observableArrayList();
	ModelFactoryController modelFactoryController;
	
	public CRUDParqueaderoController() {
		modelFactoryController = new ModelFactoryController();
	}
	
	@FXML
	private void initialize() {
		//CLIENTES
		cbTipoVehi.getItems().addAll("Carro", "Moto");
		cbTipoVehi.getSelectionModel().select("Carro");
		
		//EMPLEADOS
		cbServicio.getItems().addAll("Parqueo", "Lavado", "Pintura", "Mantenimiento");
		cbServicio.getSelectionModel().select("Parqueo");
		cbServicio2.getItems().addAll("Parqueo", "Lavado", "Pintura", "Mantenimiento");
		cbServicio2.getSelectionModel().select("Parqueo");
		//CARGA LOS EMPLEADOS Y LOS SERVICIOS
		try {
			Persistencia.cargarEmpleados(modelFactoryController.getParqueadero());
			Persistencia.cargarServicios(modelFactoryController.getParqueadero());
			crearCopias();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		actualizarComboEmpleados();
		actualizarComboServicios();
		actualizarComboVehiculos();
		actualizarRegistro();
	}

	public void setMain(Main main) {
		this.main = main;
		Persistencia.escribirEnLog("Se ejecuto el main", 1, "EJECUCION_MAIN");
	}
	
	@FXML
	public void agregarVehiculo() throws MatriculaRepetidaException, OperacionesSinSesionIniciada, VelocidadMaximaNegativaException{
		if(registro==true) {
			String nombre= tfNombreProp.getText();
			String cedula= tfCedulaProp.getText();
			String email= tfEmailProp.getText();
			String telefono= tfTelefonoProp.getText();
			String tipoVehiculo = "";
			String modelo = tfModeloVehi.getText();
			int velMax = Integer.parseInt(tfVelMaxVehi.getText());
			String placa = tfPlacaVehi.getText();
			Calendar fechahora = new GregorianCalendar();
			boolean agregado=false, valido=true;
			Propietario propiet= new Propietario(nombre, cedula, email, telefono);
			
			try {
				if(velMax<=0) {
					VelocidadMaximaNegativaException falsa = new VelocidadMaximaNegativaException();
					throw falsa;
				}
			}catch (VelocidadMaximaNegativaException e) {
				Alert mensaje;
	            mensaje = new Alert (Alert.AlertType.ERROR);
	            mensaje.setHeaderText("EXCEPCION ENCONTRADA");
	            mensaje.setContentText("EXCEPCION ENCONTRADA: La velocidad maxima ingresada no es valida.");
	            mensaje.showAndWait();
				valido = false;
	            Persistencia.escribirEnLog("Ha ocurrido una excepcion agregando el empleado: velocidad maxima invalida", 3, "EXCEPCION_ENCONTRADA");
			}
			
			//TIPO combobox
			String tipoVehi = null;
			int bTipo= cbTipoVehi.getSelectionModel().getSelectedIndex();
			if(bTipo==0) {
				tipoVehiculo = "Carro";
			}
			if(bTipo==1) {
				tipoVehiculo = "Moto";
			}
			actualizarComboServicios();
			
			String servicioSeleccionado= cbServiVehi.getValue();
			
			Servicio serv = null;
	
			for(int i=0; i<modelFactoryController.getParqueadero().getListaServicios().size(); i++) {
				if(servicioSeleccionado.equals(modelFactoryController.getParqueadero().getListaServicios().get(i).toString())) {
					serv = modelFactoryController.getParqueadero().getListaServicios().get(i);
				}
			}
			
			Puesto pues;
			int lugar = vehiculosEnParqueadero;
			boolean puestoEncontrado=false;
			
			for(int i=0; i<modelFactoryController.getParqueadero().getListaPuestos().length && puestoEncontrado==false; i++) {
				if(modelFactoryController.getParqueadero().getListaPuestos()[i]!=null && modelFactoryController.getParqueadero().getListaPuestos()[i].isDisponible()==true) {
					lugar=i;
					puestoEncontrado=true;
				}
			}
			
			Registro reg;		
			Vehiculo vehi = null;
				
			//CREAR EL VEHICULO
			try {
				for(int i=0; i<modelFactoryController.getParqueadero().getListaRegistros().size();i++) {
					String comp = modelFactoryController.getParqueadero().getListaRegistros().get(i).getVehic().getPlaca();
					if(placa.equals(comp)) {
						valido=false;
						MatriculaRepetidaException falsa = new MatriculaRepetidaException();
						throw falsa;
					}
				}
			}catch (Exception e) {
				Alert mensaje;
	            mensaje = new Alert (Alert.AlertType.ERROR);
	            mensaje.setContentText("EXCEPCION ENCONTRADA: La matricula ingresada ya se encuentra registrada en otro vehiculo.");
	            mensaje.showAndWait();
	            Persistencia.escribirEnLog("Ha ocurrido una excepcion registrando el vehiculo: Matricula ya existente en la base de datos", 3, "EXCEPCION_ENCONTRADA");
			}
			
			if(valido==true) {
				if(tipoVehiculo=="Carro") {
					vehi = new Carro(placa, modelo, propiet, serv, tipoVehiculo, velMax);
				}else {
					vehi = new Moto(placa, modelo, propiet, serv, tipoVehiculo, velMax);
				}
				pues = new Puesto(lugar, vehi, false);
				reg = new Registro(fechahora, vehi);
				//Dentro del registro queda apuntado el vehiculo con su propietario.
				agregado = modelFactoryController.crearRegistro(reg);
				modelFactoryController.ocuparPuesto(pues, lugar);
				vehiculosEnParqueadero++;
				actualizarComboVehiculos();
				
			}
			
			if(agregado==true) {
				Alert mensaje;
	            mensaje = new Alert (Alert.AlertType.INFORMATION);
	            mensaje.setContentText("¡Vehiculo registrado con exito!\nEstacionado en la "
	            		+ "posicion [" + (lugar+1) + "] del parqueadero.");
	            mensaje.showAndWait();
	            Persistencia.escribirEnLog("Se agrego un vehiculo", 1, "AGREGACION_VEHICULO");
				reporteVehiculo +=  "Se ha registrado un vehiculo de tipo: "+tipoVehiculo +"\n"+" con la placa: "+placa+"\n"+ " su propietario es: "+nombre+"\n"+" con el servicio y encargado: "+serv+ "\n--------------------------------------------------\n\n";

	    		try {
					crearCopias();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			}else {
				Alert mensaje;
	            mensaje = new Alert (Alert.AlertType.WARNING);
	            mensaje.setContentText("¡Vehiculo NO registrado!");
	            mensaje.showAndWait();
	            Persistencia.escribirEnLog("Error al ingresar vehiculo", 3, "ERROR_INGRESAR_VEHICULO");
			}
		}
		
		if(registro==false){
			 mensajeRegistroFalso();
			 exceptionOperacionInvalida();
		}
	
	}
	
	public void retirarVehiculo() {
		System.out.println("BOTONRETIRARVEHICULO");
		String vehiculoSeleccionado = cbVehiculo.getValue();
		boolean vehiculoEliminado=false;
		String notificacion="";
		for(int i=0; i<vehiculosEnParqueadero && vehiculoEliminado==false; i++) {
			if(vehiculoSeleccionado.equals(modelFactoryController.getParqueadero().getListaPuestos()[i].getVehi().getPlaca()) && modelFactoryController.getParqueadero().getListaPuestos()[i].getVehi().getPlaca()!="VEHICULO_RETIRADO") {
				notificacion += "Se esta retirando el siguiente vehiculo."
						+ "\n" + modelFactoryController.getParqueadero().getListaPuestos()[i].getVehi().getTipoVeh() + " de placa: " + modelFactoryController.getParqueadero().getListaPuestos()[i].getVehi().getPlaca();
				modelFactoryController.getParqueadero().getListaPuestos()[i].setDisponible(true);
				modelFactoryController.getParqueadero().getListaPuestos()[i].getVehi().setModelo("VEHICULO_RETIRADO");
				modelFactoryController.getParqueadero().getListaPuestos()[i].getVehi().setPlaca("VEHICULO_RETIRADO");

			}
		}
		
		if(vehiculoEliminado==true) {
			Alert mensaje;
	        mensaje = new Alert (Alert.AlertType.INFORMATION);
	        mensaje.setContentText("Se ha retirado el vehiculo del parqueadero.");
	        mensaje.showAndWait();
	        Persistencia.escribirEnLog(notificacion, 1, "VEHICULO_RETIRADO");
		}
		actualizarComboVehiculos();
		modelFactoryController.getParqueadero().liberarPuesto();
	}
	
	@FXML
	public void limpiarCamposDeTexto() {
		tfNombreProp.setText("");
		tfCedulaProp.setText("");
		tfEmailProp.setText("");
		tfTelefonoProp.setText("");
		tfPlacaVehi.setText("");
		tfModeloVehi.setText("");
		tfVelMaxVehi.setText(""); 
	}
	
	@FXML
	public void solicitarAcceso() throws LoginCedulaNoExistente, LoginInicioSesionDoble{  
		Persistencia.escribirEnLog("Se esta intentando iniciar sesion en el sistema.", 1, "INTENTO_INICIO_SESION");
		for(int i=0; i<modelFactoryController.getParqueadero().getListaEmpleados().size() && registro==false && sesionIniciada==false; i++){
			if(modelFactoryController.getParqueadero().getListaEmpleados().get(i).getCedula().equals(cedulaIngresada.getText()) || cedulaIngresada.getText().equals("admin")) {
				registro=true;
				usuario = cedulaIngresada.getText();
			}
		}
		 
		if(sesionIniciada==true) {
			try {
				LoginInicioSesionDoble falsa = new LoginInicioSesionDoble();
				throw falsa;				
			}catch (LoginInicioSesionDoble e) {
				Alert mensaje;
		        mensaje = new Alert (Alert.AlertType.ERROR);
		        mensaje.setContentText("EXCEPCION ENCONTRADA: Se esta intentando iniciar sesion, con una sesion ya iniciada.");
		        mensaje.showAndWait();
		        Persistencia.escribirEnLog("Ha ocurrido una excepcion intentando iniciar sesion otra vez", 3, "EXCEPCION_ENCONTRADA");
			}
		}
		
		if(registro==true && sesionIniciada==false){
			Alert mensaje;
            mensaje = new Alert (Alert.AlertType.CONFIRMATION);
            mensaje.setContentText("Se inicio sesión con exito, ¡Acceso concedido!");
            mensaje.showAndWait();
			String notificacion = "El usuario [" + usuario + "] ha ingresado al sistema";
			Persistencia.escribirEnLog(notificacion, 1, "SESION_INICIADA");
            cedulaIngresada.setText("*SESION INICIADA*");
			sesionIniciada=true;
			reporteVehiculo+="el "+usuario+" ha iniciado sesion"+"\n";
			if(usuario.equals("admin")) {
				admin=true;
			}
		}
		
		if(registro==false){
	        try {
				LoginCedulaNoExistente falsa = new LoginCedulaNoExistente();
				throw falsa;				
			}catch (LoginCedulaNoExistente e) {
				Alert mensaje;
		        mensaje = new Alert (Alert.AlertType.ERROR);
		        mensaje.setContentText("EXCEPCION ENCONTRADA: La cedula ingresa no se encuentra registrada en la base de datos; Si desea registrarse como empleado solicite al administrador que le registre.");
		        mensaje.showAndWait();
		        Persistencia.escribirEnLog("Ha ocurrido una excepcion intentando iniciar sesion", 3, "EXCEPCION_ENCONTRADA");

			}
	        Persistencia.escribirEnLog("Fallo al iniciar sesion; cedula no existente en la base de datos", 3, "ERROR_INICIO_SESION");
		}
		actualizarRegistro();
	}
	
	@FXML
	public void  cerrarSesion()throws LoginCerrarSesionSinSesion {
		
		if(sesionIniciada==false && registro==false) {
			try {
				LoginCerrarSesionSinSesion falsa = new LoginCerrarSesionSinSesion();
				throw falsa;				
			}catch (LoginCerrarSesionSinSesion e) { 
				Alert mensaje;
		        mensaje = new Alert (Alert.AlertType.ERROR);
		        mensaje.setContentText("EXCEPCION ENCONTRADA: Se esta intentando cerrar sesion sin una sesion iniciada.");
		        mensaje.showAndWait();
		        Persistencia.escribirEnLog("Ha ocurrido una excepcion intentando cerrar sesion", 3, "EXCEPCION_ENCONTRADA");
			}
		}
		if(sesionIniciada==true) {
			String notificacion = "El usuario [" + usuario + "] ha cerrado su sesion";
			Persistencia.escribirEnLog(notificacion, 1, "SESION_CERRADA");
			reporteVehiculo+=notificacion+" \n";
			Alert mensaje;
			mensaje = new Alert (Alert.AlertType.INFORMATION);
			mensaje.setContentText("Se ha cerrado la sesion con exito." );
			mensaje.showAndWait();
			registro=false;
			sesionIniciada=false;
			admin=false;
			actualizarRegistro();
		}
	}
	
	
	
	public void generarInformeVehiculo() throws InformeVacioException{
		boolean valido=true;
		
		try {
			if(reporteVehiculo.equals("Informacion del reporte: \n")) {
				InformeVacioException falsa = new InformeVacioException();
				throw falsa;
			}
		}catch (InformeVacioException e) {
			Alert mensaje;
            mensaje = new Alert (Alert.AlertType.ERROR);
            mensaje.setHeaderText("EXCEPCION ENCONTRADA");
            mensaje.setContentText("EXCEPCION ENCONTRADA: Reporte vacio.");
            mensaje.showAndWait();
			valido = false;
            Persistencia.escribirEnLog("Ha ocurrido una excepcion generando el informe: no se ha realizado ninguna accion.", 3, "EXCEPCION_ENCONTRADA");
		}
		
		if(registro==true && valido==true){
			try {
				Persistencia.guardarInformeVehiculo(usuario, reporteVehiculo);
				Persistencia.escribirEnLog( "Se ha generado un informe del CRUD de Gestion De vehiculos", 1, "GENERAR_INFORME");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(registro==false){
			mensajeRegistroFalso();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////TAB EMPLEADO////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	@FXML private ComboBox<String> cbEmpleados = new ComboBox<String>();
	@FXML private ComboBox<String> cbEmpleados2 = new ComboBox<String>();
	@FXML private ComboBox<String> cbEmpleados3 = new ComboBox<String>();
	@FXML private ComboBox<String> cbServicio;
	@FXML private ComboBox<String> cbServicio2;
	@FXML private TableView<Empleado> tv_listaEmpleado;
	@FXML private TextField empleadoElegido;
	@FXML private TextField tfNombreEmpleado;
	@FXML private TextField tfCedulaEmpleado;
	@FXML private TextField tfTelefonoEmpleado;
	@FXML private TextField tfNombreEmpleadoedit;
	@FXML private TextField tfCedulaEmpleadoedit;
	@FXML private TextField tfTelefonoEmpleadoedit;
	@FXML private TextField tfPlacaBuscar;
	private String reporteAdministrativo="Informacion del reporte: \n";
	boolean modificado=false;
	boolean modificadoServ=false;
	boolean eliminado=false;
	private ObservableList<Empleado> listaObservab = FXCollections.observableArrayList();
	
	@FXML
	public void agregarEmpleado() throws IOException, CedulaRepetidaException, OperacionesSinSesionIniciada, CamposVaciosException{
		if(registro==true && admin==true) {
			String nombre= tfNombreEmpleado.getText();
			String cedula= tfCedulaEmpleado.getText();
			String telefono= tfTelefonoEmpleado.getText();
			
			boolean valido = true, agregado = false;
			try {
				if(tfNombreEmpleado.getText().equals("") || tfCedulaEmpleado.getText().equals("") || tfTelefonoEmpleado.getText().equals("")) {
					CamposVaciosException falsa = new CamposVaciosException();
					throw falsa;
				}
			}catch (CamposVaciosException e) {
				Alert mensaje;
	            mensaje = new Alert (Alert.AlertType.ERROR);
	            mensaje.setContentText("EXCEPCION ENCONTRADA: Se encuentran algunos campos de texto vacios.");
	            mensaje.showAndWait();
				valido = false;
	            Persistencia.escribirEnLog("Ha ocurrido una excepcion agregando el empleado: campos vacios", 3, "EXCEPCION_ENCONTRADA");
			}
			
			
			try {
				for(int i=0; i<modelFactoryController.getParqueadero().getListaEmpleados().size();i++) {
					String comp = modelFactoryController.getParqueadero().getListaEmpleados().get(i).getCedula();
					if(cedula.equals(comp)) {
						valido=false;
						CedulaRepetidaException falsa = new CedulaRepetidaException();
						throw falsa;
					}
				}
			}catch (CedulaRepetidaException e) {
				Alert mensaje;
	            mensaje = new Alert (Alert.AlertType.ERROR);
	            mensaje.setContentText("EXCEPCION ENCONTRADA: La cedula ingresada ya se encuentra registrada en otro empleado.");
	            mensaje.showAndWait();
	            Persistencia.escribirEnLog("Ha ocurrido una excepcion agregando el empleado: Cedula ya existente en la base de datos", 3, "EXCEPCION_ENCONTRADA");
			}
			
			if(valido==true) {
				Empleado empleado= new Empleado(nombre, cedula, telefono);
				agregado = modelFactoryController.crearEmpleado(empleado);
			}
			
			if(agregado==true) {
				Alert mensaje;
	            mensaje = new Alert (Alert.AlertType.INFORMATION);
	            mensaje.setContentText("¡Empleado registrado con exito!");
	            mensaje.showAndWait();
	            modelFactoryController.guardarProyectoEmpleados();
	            Persistencia.escribirEnLog("Se agrego el empleado correctamente", 1, "CREACION_EMPLEADO");
	            Persistencia.copiarArchivo(Persistencia.RUTA_ARCHIVO_EMPLEADOS, "Empleados.txt");
	            Persistencia.escribirEnLog("Se copia el archivo empleado correctamente", 1, "COPIA_ARCHIVO");
	            reporteAdministrativo += "Se agrego un empleado con la siguiente informacion: "
	            				+ "Nombre completo: " + tfNombreEmpleado.getText() + "\n"
	            				+ "Cedula: " + tfCedulaEmpleado.getText() + "\n"
        						+ "Telefono: " + tfTelefonoEmpleado.getText() 
        						+ "\n--------------------------------------------------\n\n";
	            
			}else {
				Alert mensaje;
	            mensaje = new Alert (Alert.AlertType.WARNING);
	            mensaje.setContentText("¡Empleado NO registrado!");
	            mensaje.showAndWait();
	            Persistencia.escribirEnLog("Error al crear el empleado", 3, "ERROR_CREACION_EMPLEADO");
			}
			
			actualizarComboEmpleados();
			crearCopias();
		}
		if(registro==false){
			mensajeRegistroFalso();
			exceptionOperacionInvalida();
		}
		
		if(admin==false) {
			mensajePermisoInvalido();
		}
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getReporteAdministrativo() {
		return reporteAdministrativo;
	}

	public void setReporteAdministrativo(String reporteAdministrativo) {
		this.reporteAdministrativo = reporteAdministrativo;
	}

	@FXML
	public void limpiarCamposDeTexto2() {
		tfNombreEmpleado.setText("");
		tfCedulaEmpleado.setText("");
		tfTelefonoEmpleado.setText("");
	}
	
	//SERVICIO
	
	public void crearServicio() throws IOException, OperacionesSinSesionIniciada {
		if(registro==true && admin==true) {
			boolean agregado=false, valido=true;
			//TIPO
			Tipo servicio = null;
			int bServ= cbServicio.getSelectionModel().getSelectedIndex();
			if(bServ==0) {
				servicio = Tipo.PARQUEO;
			}
			if(bServ==1) {
				servicio = Tipo.LAVADO;
			}
			if(bServ==2) {
				servicio = Tipo.PINTURA;
			}
			if(bServ==3) {
				servicio = Tipo.MANTENIMIENTO;
			}
			//EMPLEADO
			actualizarComboEmpleados();
			String empleadoSeleccionado= cbEmpleados.getValue();
			Empleado epml = new Empleado();
	
			for(int i=0; i<modelFactoryController.getParqueadero().getListaEmpleados().size(); i++) {
				if(empleadoSeleccionado.equals(modelFactoryController.getParqueadero().getListaEmpleados().get(i).toString())) {
					epml = modelFactoryController.getParqueadero().getListaEmpleados().get(i);
				}
			}
			
			try {
				for(int i=0; i<modelFactoryController.getParqueadero().getListaServicios().size();i++) {
					Empleado comp = modelFactoryController.getParqueadero().getListaServicios().get(i).getEmp();
					if(epml.equals(comp)) {
						valido=false;
						EmpleadoOcupadoException falsa = new EmpleadoOcupadoException();
						throw falsa;
					}
				}
			}catch (Exception e) {
				Alert mensaje;
	            mensaje = new Alert (Alert.AlertType.ERROR);
	            mensaje.setContentText("El empleado asignado ya se encuentra asignado a otro servicio.");
	            mensaje.showAndWait();
			}
			
			if(valido==true) {
				Servicio serv = new Servicio(servicio, epml);
				agregado = modelFactoryController.crearServicio(serv);
			}
			
			if(agregado==true) {
				Alert mensaje;
	            mensaje = new Alert (Alert.AlertType.INFORMATION);
	            mensaje.setContentText("¡Servicio creado con exito!");
	            mensaje.showAndWait();
	            modelFactoryController.guardarProyectoServicio();
	            Persistencia.escribirEnLog("Servicio creado correctamente", 1, "CREACION_SERVICIO");
	            Persistencia.copiarArchivo(Persistencia.RUTA_ARCHIVO_SERVICIO, "Servicios.txt");
	            Persistencia.escribirEnLog("Se ha copiado el archivo servicio correctamente", 1, "COPIA_ARCHIVO");
	            reporteAdministrativo += "Se agrego un servicio al parqueadero con la siguiente informacion: "
        				+ "El empleado: " + epml.getNombre() + "\n"
        				+ "Se encargo del servicio de: " + servicio + "\n"
						+ "\n--------------------------------------------------\n\n";
			}else {
				Alert mensaje;
	            mensaje = new Alert (Alert.AlertType.WARNING);
	            mensaje.setContentText("¡Servicio NO registrado!");
	            mensaje.showAndWait();
	            Persistencia.escribirEnLog("Error al crear servicio", 3, "ERROR_CREACION_SERVICIO");
			}
			actualizarComboServicios();
			crearCopias();
		}
		if(registro==false){
			mensajeRegistroFalso();
			exceptionOperacionInvalida();
		}
		
		if(admin==false) {
			mensajePermisoInvalido();
		}
	}
	
	public void listarRegistros() throws IOException, OperacionesSinSesionIniciada{
		if(registro==true) {
	        String lis;
	        lis = modelFactoryController.getParqueadero().listarRegistros();
	        Alert mensaje;
	        mensaje = new Alert (Alert.AlertType.INFORMATION);
	        mensaje.setContentText(lis);
	        mensaje.showAndWait();
	        Persistencia.escribirEnLog("Se listaron los registros", 1, "LISTADO_REGISTROS");
			crearCopias();
		}	
		if(registro==false){
			mensajeRegistroFalso();
			exceptionOperacionInvalida();
		}
    }
	
	public void crearCopias() throws IOException {
		modelFactoryController.guardarProyectoXML();
		Persistencia.copiarArchivo(Persistencia.RUTA_ARCHIVO_XML, "XML.xml");
		
		modelFactoryController.guardarProyectoBinario();
		Persistencia.copiarArchivo(Persistencia.RUTA_ARCHIVO_PARQUEADERO, "Parqueadero.txt");
		
        Persistencia.copiarArchivo(Persistencia.RUTA_ARCHIVO_EMPLEADOS, "Empleados.txt");
        Persistencia.escribirEnLog("Se copia el archivo empleado correctamente", 1, "COPIA_ARCHIVO");
        Persistencia.copiarArchivo(Persistencia.RUTA_ARCHIVO_SERVICIO, "Servicios.txt");
        Persistencia.escribirEnLog("Se copia el archivo servicio correctamente", 1, "COPIA_ARCHIVO");
	}
	
	public void verificarDisponibilidad() throws OperacionesSinSesionIniciada {
		if(registro==true) {
			modelFactoryController.getParqueadero().verificarDisponibilidadPuesto();
		}
		if(registro==false){
			mensajeRegistroFalso();
			exceptionOperacionInvalida();
		}
	}
	
	public void verificarVehiculoEnParqueadero() throws OperacionesSinSesionIniciada {
		if(registro==true) {
			String placa = tfPlacaBuscar.getText();
			modelFactoryController.getParqueadero().verificarVehiculoEstaEnParqueadero(placa);
			reporteVehiculo+="Se verifico que el vehiculo: "+placa+ "se encontrara en el parqueadero "+"\n";
		}
		
		if(registro==false){
			 mensajeRegistroFalso();
			 exceptionOperacionInvalida();
		}
	}
	
	public void actualizarComboEmpleados() {
		ObservableList<String> empleados= FXCollections.observableArrayList();
		for(int i=0; i<modelFactoryController.getParqueadero().getListaEmpleados().size();i++) {
			if(modelFactoryController.getParqueadero().getListaEmpleados().get(i)!=null && modelFactoryController.getParqueadero().getListaEmpleados().get(i).getCedula()!="EMPLEADO_ELIMINADO") {
				empleados.add(modelFactoryController.getParqueadero().getListaEmpleados().get(i).toString());
			}
		}
		cbEmpleados.setItems(empleados);
		cbEmpleados2.setItems(empleados);
		cbEmpleados3.setItems(empleados);
	}
	
	public void actualizarComboServicios() {
		ObservableList<String> servicios= FXCollections.observableArrayList();
		for(int i=0; i<modelFactoryController.getParqueadero().getListaServicios().size();i++) {
			if(modelFactoryController.getParqueadero().getListaServicios().get(i)!=null && modelFactoryController.getParqueadero().getListaServicios().get(i).getEmp().getCedula()!="SERVICIO_ELIMINADO") {
				servicios.add(modelFactoryController.getParqueadero().getListaServicios().get(i).toString());
			}
		}
		cbServiVehi.setItems(servicios);
		cbServiVehi2.setItems(servicios);
	}
	public void actualizarComboVehiculos() {
		ObservableList<String> vehiculos= FXCollections.observableArrayList();
		for(int i=0; i<modelFactoryController.getParqueadero().getListaPuestos().length;i++) {
			if(modelFactoryController.getParqueadero().getListaPuestos()[i]!=null && modelFactoryController.getParqueadero().getListaPuestos()[i].getVehi().getPlaca()!="VEHICULO_RETIRADO" && modelFactoryController.getParqueadero().getListaPuestos()[i].getVehi().getPlaca()!="PUESTO_LIBRE" ) {
				vehiculos.add(modelFactoryController.getParqueadero().getListaPuestos()[i].getVehi().getPlaca());
			}
		}
		cbVehiculo.setItems(vehiculos);
	}
	public void eliminarEmpleado() throws OperacionesSinSesionIniciada {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("CONFIRMAR ACCION");
		alert.setHeaderText("ADVERTENCIA: ELIMINACION EMPLEADO!");
		alert.setContentText("Se eliminara la informacion del empleado, esto ocasionara que se elimine todas las instanscias del mismo, al igual que el servicio al cual se encontraba ligado. ¿Desea continuar?");
		
		Optional<ButtonType> result = alert.showAndWait();
		if(registro==true && result.get().getText().equals("Aceptar") && admin==true) {
			admin =true;
			String empleadoSeleccionado= cbEmpleados2.getValue();
			Empleado emp = new Empleado();
	
			for(int i=0; i<modelFactoryController.getParqueadero().getListaEmpleados().size(); i++) {
				System.out.println(i);
				System.out.println(empleadoSeleccionado);
				System.out.println(modelFactoryController.getParqueadero().getListaEmpleados().get(i).toString());
				if(empleadoSeleccionado.equals(modelFactoryController.getParqueadero().getListaEmpleados().get(i).toString())) {
					String notificacion = "Se ha eliminado la informacion de un empleado"
							+ "\nNombre: " + modelFactoryController.getParqueadero().getListaEmpleados().get(i).getNombre()
							+ "\nCedula: " + modelFactoryController.getParqueadero().getListaEmpleados().get(i).getCedula()
							+ "\nTelefono: " + modelFactoryController.getParqueadero().getListaEmpleados().get(i).getNumTelefono();
					reporteAdministrativo += notificacion + "\n--------------------------------------------------\n\n";
					Persistencia.escribirEnLog(notificacion, 2, "ELIMINACION_EMPLEADO");
					Empleado empBorrar = modelFactoryController.getParqueadero().getListaEmpleados().get(i);
					empBorrar.setCedula("EMPLEADO_ELIMINADO");
					empBorrar.setNombre("EMPLEADO_ELIMINADO");
					empBorrar.setNumTelefono("EMPLEADO_ELIMINADO");
					modelFactoryController.getParqueadero().getListaEmpleados().set(i, empBorrar);
					modificado=true;
				}
			}
			
			//Al eliminar un empleado, para eliminarle de un servicio se cambia su nombre por "" para posteriores validaciones.
			for(int i=0; i<modelFactoryController.getParqueadero().getListaServicios().size();i++) {
				if(empleadoSeleccionado.equals(modelFactoryController.getParqueadero().getListaServicios().get(i).getEmp().toString())) {
					modelFactoryController.getParqueadero().getListaServicios().get(i).getEmp().setNombre("");
					modificado=true;
				}
			}
			actualizarComboEmpleados();
			actualizarComboServicios();
			modelFactoryController.guardarProyectoEmpleados();
			modelFactoryController.guardarProyectoServicio();

			modificado=true;

			tfNombreEmpleadoedit.setText("");
			tfCedulaEmpleadoedit.setText("");
			tfTelefonoEmpleadoedit.setText("");
		}
		
		if(result.get().getText()=="Cancelar") {
			Alert mensaje;
	        mensaje = new Alert (Alert.AlertType.INFORMATION);
	        mensaje.setTitle("ELIMINACION DE EMPLEADO CANELADA");
	        mensaje.setHeaderText("Información: ");
            mensaje.setContentText("Se ha cancelado la eliminación del empleado.");
            mensaje.showAndWait();
		}
		
		if(registro==false){
			mensajeRegistroFalso();
			exceptionOperacionInvalida();
		}
		
		if(admin==false) {
			mensajePermisoInvalido();
		}
	}
	
	//Este metodo, actualiza los campos de texto de la modificacion del empleado con los valores del empleado seleccionado en el combobox
	public void prueba() {
		for(int i=0; i<modelFactoryController.getParqueadero().getListaEmpleados().size() && modificado==false; i++) {
			String emplSelec ="";
			if(modificado==false) {
				emplSelec = cbEmpleados2.getValue();
			}
			
			if(modificado==true) {
				System.out.println("modificadoTrue");
				tfNombreEmpleadoedit.setText("");
				tfCedulaEmpleadoedit.setText("");
				tfTelefonoEmpleadoedit.setText("");
			}
			
			if(emplSelec.equals(modelFactoryController.getParqueadero().getListaEmpleados().get(i).toString()) && modificado==false) {
				tfNombreEmpleadoedit.setText(modelFactoryController.getParqueadero().getListaEmpleados().get(i).getNombre());
				tfCedulaEmpleadoedit.setText(modelFactoryController.getParqueadero().getListaEmpleados().get(i).getCedula());
				tfTelefonoEmpleadoedit.setText(modelFactoryController.getParqueadero().getListaEmpleados().get(i).getNumTelefono());
			}
		}
		if(modificado==true) {
			tfNombreEmpleadoedit.setText("");
			tfCedulaEmpleadoedit.setText("");
			tfTelefonoEmpleadoedit.setText("");
		}
		modificado=false;
	}
	
	public void prueba2() {
		System.out.println("No existira este metodo bro.");
	}
	
	public void modificarEmpleado() throws OperacionesSinSesionIniciada {
	
		boolean mismaCedula = false;
		boolean cedulaValida = true;
		boolean empleadoEncontrado = false;
		int pos=0;
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("CONFIRMAR ACCION");
		alert.setHeaderText("Modificación de empleado.");
		alert.setContentText("Se modificara la informacion del empleado, esto ocasionara que se modifiquen todas las instanscias del mismo. ¿Desea continuar?");
		
		Optional<ButtonType> result = alert.showAndWait();	
		String notificacion="";
		if(registro==true && result.get().getText().equals("Aceptar") && admin==true) {
			Empleado emplEdit = new Empleado();
			String cedula = tfCedulaEmpleadoedit.getText();
			String empleadoSeleccionado= cbEmpleados2.getValue();
			for(int i=0; i<modelFactoryController.getParqueadero().getListaEmpleados().size() && empleadoEncontrado==false; i++) {
				if(empleadoSeleccionado.equals(modelFactoryController.getParqueadero().getListaEmpleados().get(i).toString())) {
					emplEdit = modelFactoryController.getParqueadero().getListaEmpleados().get(i);
					pos = i;
					empleadoEncontrado = true;					
				}
			}
			
			if(cedula.equals(emplEdit.getCedula())) {
				mismaCedula = true;
			}
			
			if(mismaCedula==false) {
				for(int k=0; k<modelFactoryController.getParqueadero().getListaEmpleados().size() && cedulaValida==true; k++) {
					if(cedula.equals(modelFactoryController.getParqueadero().getListaEmpleados().get(k).getCedula())) {
						cedulaValida = false;
					}
				}
				System.out.println("DISTINTA CEDULA");
			}
			
			if(mismaCedula==true) {
				String nuevoNombre = tfNombreEmpleadoedit.getText();
				String nuevaCedula = tfCedulaEmpleadoedit.getText();
				String nuevoTelefono = tfTelefonoEmpleadoedit.getText();
				System.out.println("***********************");
				System.out.println(tfNombreEmpleadoedit.getText());
				System.out.println(tfCedulaEmpleadoedit.getText());
				System.out.println(tfTelefonoEmpleadoedit.getText());
				System.out.println("MISMA CEDULA");
				
				notificacion = "Se ha modificado la informacion de un empleado"
						+ "\nNombre anterior: " + emplEdit.getNombre() + "  Nombre Nuevo: " + tfNombreEmpleadoedit.getText()
						+ "\nCedula anterior: " + emplEdit.getCedula() + " Cedula Nueva: " + tfCedulaEmpleadoedit.getText()
						+ "\nTelefono anterior: " + emplEdit.getNumTelefono() + " Telefono Nuevo: " + tfTelefonoEmpleadoedit.getText();
				reporteAdministrativo += notificacion + "\n--------------------------------------------------\n\n";
				System.out.println(pos);
				modelFactoryController.getParqueadero().getListaEmpleados().get(pos).setCedula(nuevoNombre);
				modelFactoryController.getParqueadero().getListaEmpleados().get(pos).setNombre(nuevaCedula);
				modelFactoryController.getParqueadero().getListaEmpleados().get(pos).setNumTelefono(nuevoTelefono);
				modificado=true;
				Persistencia.escribirEnLog(notificacion, 2, "MODIFICACION_EMPLEADO");
			}
			
			if(cedulaValida==true) {
				notificacion = "Se ha modificado la informacion de un empleado"
						+ "\nNombre anterior: " + emplEdit.getNombre() + "  Nombre Nuevo: " + tfNombreEmpleadoedit.getText()
						+ "\nCedula anterior: " + emplEdit.getCedula() + " Cedula Nueva: " + tfCedulaEmpleadoedit.getText()
						+ "\nTelefono anterior: " + emplEdit.getNumTelefono() + " Telefono Nuevo: " + tfTelefonoEmpleadoedit.getText();
				reporteAdministrativo += notificacion + "\n--------------------------------------------------\n\n";
				modelFactoryController.getParqueadero().getListaEmpleados().get(pos).setCedula(tfCedulaEmpleadoedit.getText());
				modelFactoryController.getParqueadero().getListaEmpleados().get(pos).setNombre(tfNombreEmpleadoedit.getText());
				modelFactoryController.getParqueadero().getListaEmpleados().get(pos).setNumTelefono(tfTelefonoEmpleadoedit.getText());
				modificado=true;
				Persistencia.escribirEnLog(notificacion, 2, "MODIFICACION_EMPLEADO");
			}
			
			if(cedulaValida==false) {
				Alert mensaje;
		        mensaje = new Alert (Alert.AlertType.WARNING);
		        mensaje.setTitle("Accion invalida");
		        mensaje.setHeaderText("EXCEPCION: Cedula repetida.");
	            mensaje.setContentText("La nueva cedula del empleado es igual a una de otro empleado.");
	            mensaje.showAndWait();
			}

			//Sobreescribe el empleado en su determinado servicio ya creado.
			for(int i=0; i<modelFactoryController.getParqueadero().getListaServicios().size();i++) {
				if(empleadoSeleccionado.equals(modelFactoryController.getParqueadero().getListaServicios().get(i).getEmp().toString())) {
					Empleado emplEditao = new Empleado(tfNombreEmpleadoedit.getText(), tfCedulaEmpleadoedit.getText(), tfTelefonoEmpleadoedit.getText());
					modelFactoryController.getParqueadero().getListaServicios().get(i).setEmp(emplEditao);
					Persistencia.escribirEnLog("Se ha actualizado la informacion del empleado en el servicio al que estaba vinculado.", 2, "MODIFICACION_EMPLEADO_EN_SERVICIO");
				}
			}
			actualizarComboServicios(); 
			//Se guardan los cambios realizados en los archivos de texto.
			modelFactoryController.guardarProyectoEmpleados();
			modelFactoryController.guardarProyectoServicio();
			Persistencia.escribirEnLog("Se han guardado los cambios realizados al empleado en el archivo de texto.", 2, "MODIFICACION_EMPLEADO");
			tfNombreEmpleadoedit.setText("");
			tfCedulaEmpleadoedit.setText("");
			tfTelefonoEmpleadoedit.setText("");
		}
		
		if(result.get().getText()=="Cancelar") {
			Alert mensaje;
	        mensaje = new Alert (Alert.AlertType.INFORMATION);
	        mensaje.setTitle("Modificacion de empleado cancelada");
	        mensaje.setHeaderText("Informacion: ");
            mensaje.setContentText("Se ha cancelado la eliminación del empleado.");
            mensaje.showAndWait();
		}
		
		if(registro==false){
			mensajeRegistroFalso();
			exceptionOperacionInvalida();
		}
		
		if(admin==false) {
			mensajePermisoInvalido();
		}
		actualizarComboEmpleados();
	}
	
	public void eliminarServicio() throws OperacionesSinSesionIniciada {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("CONFIRMAR ACCION");
		alert.setHeaderText("ADVERTENCIA: ELIMINACION DE SERVICIO!");
		alert.setContentText("Se eliminara el servicio seleccionado\n ¿Desea continuar?");
		
		Optional<ButtonType> result = alert.showAndWait();
		if(registro==true && result.get().getText().equals("Aceptar") && admin==true) {
			String servicioSeleccionado= cbServiVehi2.getValue();
			Servicio serv = new Servicio();
			Empleado empl = new Empleado();
			for(int i=0; i<modelFactoryController.getParqueadero().getListaServicios().size(); i++) {
				if(servicioSeleccionado.equals(modelFactoryController.getParqueadero().getListaServicios().get(i).toString())) {
					String notificacion = "Se ha eliminado la informacion de un servicio"
							+ "\nServicio de: " + modelFactoryController.getParqueadero().getListaServicios().get(i).getTipo()
							+ "\nA cargo del empleado: " + modelFactoryController.getParqueadero().getListaServicios().get(i).getEmp().getNombre();
					reporteAdministrativo += notificacion + "\n--------------------------------------------------\n\n";
					empl.setCedula("SERVICIO_ELIMINADO");
					empl.setNombre("SERVICIO_ELIMINADO");
					empl.setNumTelefono("SERVICIO_ELIMINADO");
					modelFactoryController.getParqueadero().getListaServicios().get(i).setEmp(empl);
					Persistencia.escribirEnLog(notificacion, 2, "ELIMINACION_SERVICIO");
					Servicio servBorrar = modelFactoryController.getParqueadero().getListaServicios().get(i);
					servBorrar.setEmp(empl);
					modelFactoryController.getParqueadero().getListaServicios().set(i, servBorrar);
				}
			}
			actualizarComboServicios();
			modelFactoryController.guardarProyectoServicio();
		}
		
		if(registro==false){
			mensajeRegistroFalso();
			exceptionOperacionInvalida();
		}
		
		if(admin==false) {
			mensajePermisoInvalido();
		}
	}
	
	public void modificarServicio() throws OperacionesSinSesionIniciada {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("CONFIRMAR ACCION");
		alert.setHeaderText("ADVERTENCIA: MODIFICACION DE SERVICIO!");
		alert.setContentText("Se modificara el servicio seleccionado\n ¿Desea continuar?");
		
		Optional<ButtonType> result = alert.showAndWait();
		if(registro==true && result.get().getText().equals("Aceptar") && admin==true) {
			String servicioSeleccionado= cbServiVehi2.getValue();
			Servicio serv = new Servicio();
			Empleado emplDelServicio = new Empleado();
			String emplSeleccionado=cbEmpleados3.getValue();
			boolean valido = true;
			boolean mismoEmpleado = false;
			boolean empleadoValido = true;
			boolean servicioEncontrado = false;
			int pos=0;
			Tipo servicio = null;
			for(int i=0; i<modelFactoryController.getParqueadero().getListaServicios().size() && servicioEncontrado==false; i++) {
				if(servicioSeleccionado.equals(modelFactoryController.getParqueadero().getListaServicios().get(i).toString())) {
					serv = modelFactoryController.getParqueadero().getListaServicios().get(i);
					emplDelServicio = serv.getEmp();
					pos = i;
					servicioEncontrado=true;
					if(emplDelServicio.toString().equals(emplSeleccionado)) {
						mismoEmpleado = true;
					}
					int bServ2= cbServicio2.getSelectionModel().getSelectedIndex();
					if(bServ2==0) {
						servicio = Tipo.PARQUEO;
					}
					if(bServ2==1) {
						servicio = Tipo.LAVADO;
					}
					if(bServ2==2) {
						servicio = Tipo.PINTURA;
					}
					if(bServ2==3) {
						servicio = Tipo.MANTENIMIENTO;
					}
				}
			}//ENCONTRAR SERVICIO
			
			if(mismoEmpleado==false) {
				for(int k=0; k<modelFactoryController.getParqueadero().getListaServicios().size() && empleadoValido==true;k++) {
					if(emplSeleccionado.equals(modelFactoryController.getParqueadero().getListaServicios().get(k).getEmp().toString())) {
						empleadoValido=false;
					}
				}
			}
			
			if(mismoEmpleado==true) {
				serv.Tipo(servicio);
				serv.setEmp(emplDelServicio);
				modelFactoryController.getParqueadero().getListaServicios().set(pos, serv);
				System.out.println("guardando conmismo empleado");
				modelFactoryController.guardarProyectoServicio();
				actualizarComboServicios();
			}
			Empleado nuevoEmpl = new Empleado();
			if(empleadoValido==true) {
				for(int i=0; i<modelFactoryController.getParqueadero().getListaEmpleados().size(); i++) {
					System.out.println(emplSeleccionado);
					System.out.println(modelFactoryController.getParqueadero().getListaEmpleados().get(i).toString());
					if(emplSeleccionado.equals(modelFactoryController.getParqueadero().getListaEmpleados().get(i).toString())) {
						nuevoEmpl = modelFactoryController.getParqueadero().getListaEmpleados().get(i);
					}
				}
				System.out.println("ANTES DE MODIFICARLO COMO TAL");
				System.out.println(servicio);
				System.out.println(nuevoEmpl.toString());
				serv.Tipo(servicio);
				serv.setEmp(nuevoEmpl);
				System.out.println(serv.toString());
				modelFactoryController.getParqueadero().getListaServicios().set(pos, serv);
				modelFactoryController.guardarProyectoServicio();
				actualizarComboServicios();
			}
			
			if(empleadoValido==false) {
				Alert mensaje;
		        mensaje = new Alert (Alert.AlertType.WARNING);
		        mensaje.setTitle("Accion invalida");
		        mensaje.setHeaderText("EXCEPCION: Empleado repetido.");
	            mensaje.setContentText("El nuevo empleado que se desea asignar, ya se encuentra asignado a otro servicio.");
	            mensaje.showAndWait();
			}
		}
		
		if(registro==false){
			mensajeRegistroFalso();
			exceptionOperacionInvalida();
		}
		
		if(admin==false) {
			mensajePermisoInvalido();
		}
	}
	
	public void generarInformeAdmin() throws InformeVacioException{
		boolean valido=true;
		
		try {
			if(reporteAdministrativo.equals("Informacion del reporte: \n")) {
				InformeVacioException falsa = new InformeVacioException();
				throw falsa;
			}
		}catch (InformeVacioException e) {
			Alert mensaje;
            mensaje = new Alert (Alert.AlertType.ERROR);
            mensaje.setHeaderText("EXCEPCION ENCONTRADA");
            mensaje.setContentText("EXCEPCION ENCONTRADA: Reporte vacio.");
            mensaje.showAndWait();
			valido = false;
            Persistencia.escribirEnLog("Ha ocurrido una excepcion generando el informe: no se ha realizado ninguna accion.", 3, "EXCEPCION_ENCONTRADA");
		}
		
		if(registro==true && admin==true && valido==true){
			try {
				Persistencia.guardarInformeAdministrativo(usuario, reporteAdministrativo);
				Persistencia.escribirEnLog("Se ha generado un informe del CRUD administrativo", 1, "GENERAR_INFORME");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(registro==false){
			mensajeRegistroFalso();

		}
		if(admin==false){
			mensajePermisoInvalido(); 
		}
	}
	
	public void actualizarRegistro() {
		if(registro==false) {
			cedulaIngresada.setText("Ingrese C.C o Palabra clave.");
			tfNombreProp.setText("¡PRIMERO INICIE SESION!");
			tfCedulaProp.setText("¡PRIMERO INICIE SESION!");
			tfEmailProp.setText("¡PRIMERO INICIE SESION!");
			tfTelefonoProp.setText("¡PRIMERO INICIE SESION!");
			tfPlacaVehi.setText("¡PRIMERO INICIE SESION!");
			tfModeloVehi.setText("¡PRIMERO INICIE SESION!");
			tfVelMaxVehi.setText("¡PRIMERO INICIE SESION!");
			tfNombreEmpleado.setText("¡PRIMERO INICIE SESION!");
			tfCedulaEmpleado.setText("¡PRIMERO INICIE SESION!");
			tfTelefonoEmpleado.setText("¡PRIMERO INICIE SESION!");
			tfNombreEmpleadoedit.setText("¡PRIMERO INICIE SESION!");
			tfCedulaEmpleadoedit.setText("¡PRIMERO INICIE SESION!");
			tfTelefonoEmpleadoedit.setText("¡PRIMERO INICIE SESION!");
			tfPlacaBuscar.setText("¡PRIMERO INICIE SESION!");
		}
		if(registro==true) {
			tfNombreProp.setText("Nombre completo del propietario");
			tfCedulaProp.setText("Documento de identidad del propietario");
			tfEmailProp.setText("Correo electronico del propietario");
			tfTelefonoProp.setText("Telefono movil o fijo del propietario");
			tfPlacaVehi.setText("Ingrese la placa del vehiculo");
			tfModeloVehi.setText("1978 (Ingrese solo numeros)");
			tfVelMaxVehi.setText("200 (Ingrese solo numeros)");
			tfNombreEmpleado.setText("Nombre completo del empleado");
			tfCedulaEmpleado.setText("Documento identidad del empleado");
			tfTelefonoEmpleado.setText("Telefono movil o fijo del empleado");
			tfNombreEmpleadoedit.setText("Seleccione un empleado primero");
			tfCedulaEmpleadoedit.setText("Seleccione un empleado primero");
			tfTelefonoEmpleadoedit.setText("Seleccione un empleado primero");
			tfPlacaBuscar.setText("Ingrese la matricula a buscar");
		}
	}
	
	public void mensajeRegistroFalso() {
		Alert mensaje;
        mensaje = new Alert (Alert.AlertType.WARNING);
        mensaje.setTitle("ACCION INVALIDA");
        mensaje.setHeaderText("Advertencia: Permiso denegado.");
        mensaje.setContentText("Se requiere una sesion iniciada para operar la aplicacion.\nPor favor, inicie sesión.");
        mensaje.showAndWait();
	}
	
	public void mensajePermisoInvalido() {
		Alert mensaje;
        mensaje = new Alert (Alert.AlertType.WARNING);
        mensaje.setTitle("ACCION DENEGADA");
        mensaje.setHeaderText("Advertencia: Permiso denegado.");
        mensaje.setContentText("Para realizar esta operacion, se requiere iniciar sesion como admin.");
        mensaje.showAndWait();
	}
	
	public void exceptionOperacionInvalida() throws OperacionesSinSesionIniciada{
		try {
			OperacionesSinSesionIniciada falsa = new OperacionesSinSesionIniciada();
			throw falsa;				
		}catch (OperacionesSinSesionIniciada e) {
			Alert mensaje;
	        mensaje = new Alert (Alert.AlertType.ERROR);
	        mensaje.setTitle("ACCION INVALIDA");
	        mensaje.setHeaderText("Advertencia: Excepcion encontrada.");
	        mensaje.setContentText("Se esta intentando realizar una operacion sin una sesion iniciada.");
	        mensaje.showAndWait();
	        Persistencia.escribirEnLog("Ha ocurrido una excepcion intentando realizar una operacion; No:Sesion", 3, "EXCEPCION_ENCONTRADA");
		}
	}
}