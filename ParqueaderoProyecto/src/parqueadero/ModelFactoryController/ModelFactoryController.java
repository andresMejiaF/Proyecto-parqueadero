package parqueadero.ModelFactoryController;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import application.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import parqueadero.model.Empleado;
import parqueadero.model.Parqueadero;
import parqueadero.model.Puesto;
import parqueadero.model.Registro;
import parqueadero.model.Servicio;
import parqueadero.persistencia.ArchivoUtil;
import parqueadero.persistencia.Persistencia;
import parqueadero.views.CRUDParqueaderoController;

public class ModelFactoryController implements Runnable{
	Parqueadero parqueadero;
	Thread threadPersistenciaXML;
	Thread threadPersistenciaBinario;
	Thread threadPersistenciaEmpleado;
	Thread threadPersistenciaServicio;
	Thread threadGenerarInformeAdministrativo;
	Thread threadGenerarInformeVehiculos;
	int hiloActual=0;
	
	private Socket socketComunicacion = null;//para la comunicacion (entrada y salida)
	private Socket socketTransferenciaObjeto = null;//permite recibir el archivo
	private DataOutputStream flujoSalidaComunicacion = null;
	private DataInputStream flujoEntradaComunicacion=null;

	private String idInstanciaCliente = "";
	byte[] receivedData;
	int in;
	BufferedInputStream flujoEntradaArchivo;
	BufferedOutputStream flujoSalidadLocalArchivo;

	String filename = "";
	String rutaArchivoLocal = "src/resourcesServidor/";
	ObjectInputStream flujoEntradaObjeto;
	ObjectOutputStream flujoSalidaObjeto;
	
	//------------------------------  Singleton ------------------------------------------------
	// Clase estatica oculta. Tan solo se instanciara el singleton una vez
	private static class SingletonHolder {
		// El constructor de Singleton puede ser llamado desde aquí al ser protected
	private final static ModelFactoryController eINSTANCE = new ModelFactoryController();
	}

	// Método para obtener la instancia de nuestra clase
	public static ModelFactoryController getInstance() {
		return SingletonHolder.eINSTANCE;
	}

	public ModelFactoryController() {
		try {
			inicializarDatos();
		} catch (Exception e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		Persistencia.escribirEnLog("Se ingreso al sistema", 1, "INGRESO_SISTEMA");
	}

	public void inicializarDatos() throws Exception {
		parqueadero = new Parqueadero(); 
		
		Persistencia.serializarBinario(Persistencia.RUTA_ARCHIVO_PARQUEADERO, parqueadero);
	}
	
	private void leerObjetoPersistenciaTransferido() throws IOException, ClassNotFoundException {

//		flujoEntradaObjeto = new ObjectInputStream(socketTransferenciaObjeto.getInputStream());
//		flujoSalidaObjeto = new ObjectOutputStream(socketTransferenciaObjeto.getOutputStream());
		parqueadero =  (Parqueadero) flujoEntradaObjeto.readObject();
		System.out.println("objeto recibido");
		flujoEntradaObjeto.close();
	}
	private void solicitarInformacionPersistencia() throws IOException {
		flujoSalidaComunicacion.writeInt(1);
		flujoSalidaComunicacion.close();
	}

	public void crearConexion() throws IOException,Exception {
		try {
			socketComunicacion = new Socket("localhost", 8081); //socket para enviar datos primitivos
			socketTransferenciaObjeto = new Socket("localhost", 8082);//sokcet para enviar y recibir objeto

			flujoEntradaComunicacion = new DataInputStream(socketComunicacion.getInputStream());
			flujoSalidaComunicacion = new DataOutputStream(socketComunicacion.getOutputStream());

			flujoEntradaObjeto = new ObjectInputStream(socketTransferenciaObjeto.getInputStream());
			flujoSalidaObjeto = new ObjectOutputStream(socketTransferenciaObjeto.getOutputStream());

		} catch (IOException e) {
			throw new Exception("\tError en el servidor");

		}
	}

	public Parqueadero getParqueadero() {
		return parqueadero;
	}

	public void setParqueadero(Parqueadero parqueadero) {
		this.parqueadero = parqueadero;
	}

	public boolean crearRegistro(Registro reg) {
		return 	parqueadero.crearRegistro(reg);
	}

	public void ocuparPuesto(Puesto pues, int lugar) {
		parqueadero.ocuparPuesto(pues, lugar);
	}

	public void abrirVentanaRegistro() {
		parqueadero.listarRegistros();
	}

	public boolean crearEmpleado(Empleado empleado) {
		return parqueadero.crearEmpleado(empleado);
	}
	
	public boolean crearServicio(Servicio serv) {
		return parqueadero.crearServicio(serv);
	}

	@Override
	public void run() {		
		switch (hiloActual) {
		//Guardar el parqueadero en XML
		case 1:
			try {
				crearConexion();
				solicitarGuardarPersistencia();
				enviarObjetoPersistenciaTransferido();
				
			} catch (IOException e1) {
				// TODO Bloque catch generado automáticamente
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Bloque catch generado automáticamente
				e1.printStackTrace();
			}
			hiloActual = 0;
			break;
			
		//Guardar el parqueadero en Binario
		case 2:
			try {
				crearConexion();
				solicitarGuardarPersistenciaBinario();
				enviarObjetoPersistenciaTransferido();
				
			} catch (IOException e1) {
				// TODO Bloque catch generado automáticamente
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Bloque catch generado automáticamente
				e1.printStackTrace();
			}
			hiloActual = 0;

			break;
			
		//Guardar empleados en archivo de texto plano.
		case 3:
			try {
				Persistencia.guardarEmpleado(parqueadero);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				crearConexion();
				solicitarGuardarEmpleado();
				enviarObjetoPersistenciaTransferido();
				
			} catch (IOException e1) {
				// TODO Bloque catch generado automáticamente
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Bloque catch generado automáticamente
				e1.printStackTrace();
			}
			hiloActual = 0;

			break;
		
		//Guardar servicios en archivo de texto plano.
		case 4:
			try {
				crearConexion();
				solicitarGuardarServico();
				enviarObjetoPersistenciaTransferido();
				
			} catch (IOException e1) {
				// TODO Bloque catch generado automáticamente
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Bloque catch generado automáticamente
				e1.printStackTrace();
			}
			hiloActual = 0;

			break;
		}
	}
	
	private void solicitarGuardarServico() throws IOException {

		flujoSalidaComunicacion.writeInt(5);
		flujoSalidaComunicacion.close();
	}
	
	private void solicitarGuardarEmpleado() throws IOException {

		flujoSalidaComunicacion.writeInt(4);
		flujoSalidaComunicacion.close();
	}

	public void guardarProyectoXML() {
		hiloActual = 1;
		threadPersistenciaXML =  new Thread(this);

		threadPersistenciaXML.start();
		try {
			threadPersistenciaXML.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void solicitarGuardarPersistencia() throws IOException {

		flujoSalidaComunicacion.writeInt(2);
		flujoSalidaComunicacion.close();
	}
	
	private void solicitarGuardarPersistenciaBinario() throws IOException {

		flujoSalidaComunicacion.writeInt(3);
		flujoSalidaComunicacion.close();
	}
	private void enviarObjetoPersistenciaTransferido() throws IOException, ClassNotFoundException {

		flujoSalidaObjeto.writeObject(parqueadero);
		System.out.println("objeto enviado");
		flujoSalidaObjeto.close();
	}
	
	public void guardarProyectoBinario() {
		hiloActual = 2;
		threadPersistenciaBinario =  new Thread(this);

		threadPersistenciaBinario.start();
		try {
			threadPersistenciaBinario.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void guardarProyectoEmpleados() {
		hiloActual = 3;
		threadPersistenciaEmpleado =  new Thread(this);

		threadPersistenciaEmpleado.start();
		try {
			threadPersistenciaEmpleado.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void guardarProyectoServicio() {
		hiloActual = 4;
		threadPersistenciaServicio =  new Thread(this);

		threadPersistenciaServicio.start();
		try {
			threadPersistenciaServicio.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
