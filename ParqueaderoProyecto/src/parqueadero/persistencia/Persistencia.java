package parqueadero.persistencia;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import parqueadero.model.Empleado;
import parqueadero.model.Parqueadero;
import parqueadero.model.Servicio;
import parqueadero.model.Tipo;

public class Persistencia {
	//RUTA ARCHIVOS
	public static final String RUTA_ARCHIVO_EMPLEADOS = "C:/td/persistencia/archivos/archivoEmpleados.txt";
	public static final String RUTA_ARCHIVO_SERVICIO = "C:/td/persistencia/archivos/archivoServicio.txt";
	//RUTA DEL LOG
	public static final String RUTA_ARCHIVO_LOG = "C:/td/persistencia/log/LogParqueadero.txt";
	
	public static final String RUTA_ARCHIVO_PARQUEADERO = "C:/td/persistencia/parqueaderoBINARIO.txt";
	public static final String RUTA_ARCHIVO_XML = "C:/td/persistencia/parqueaderoXML.xml";
	
	public static final String RUTA_ARCHIVO_INFORMES_ADMINISTRATIVOS = "C:/td/persistencia/Informes/informes_gestion_administrativa.txt";
	public static final String RUTA_ARCHIVO_INFORMES_VEHICULOS = "C:/td/persistencia/Informes/informes_gestion_vehiculos.txt";

	public static void cargarEmpleados(Parqueadero parq) throws IOException {
		
		parq.getListaEmpleados().clear();
		ArrayList<String> contenido = ArchivoUtil.cargarArchivo(RUTA_ARCHIVO_EMPLEADOS);
		
		boolean valido=true;
		Empleado empl = new Empleado();
		String nombre = "";
		String cedula = "";
		String telefono= "";
		String[] informacionEmpleado = null;
		for (String linea : contenido) {
			informacionEmpleado = linea.split("@@");
			nombre = informacionEmpleado[0];
			cedula = informacionEmpleado[1];
			telefono = informacionEmpleado[2];
			empl = new Empleado(nombre, cedula, telefono);
			parq.getListaEmpleados().add(empl);
		}
	}
	
	public static void cargarServicios(Parqueadero parq) throws IOException {
			
			parq.getListaServicios().clear();
			ArrayList<String> contenido = ArchivoUtil.cargarArchivo(RUTA_ARCHIVO_SERVICIO);
			
			boolean valido=true;
			Servicio serv = new Servicio();
			Empleado empl = new Empleado();
			Tipo tipoSer=null;
			String nombre = "";
			String cedula = "";
			String telefono ="";
			String tipo ="";
			String[] informacionServicio = null;
			for (String linea : contenido) {
				informacionServicio = linea.split("@@");
				nombre = informacionServicio[0];
				cedula = informacionServicio[1];
				telefono = informacionServicio[2];
				tipo = informacionServicio[3];
				//Se crea el empleado
				empl = new Empleado(nombre, cedula, telefono);
				//Se empareja el servicio
				if(tipo.equals("PARQUEO")) {
					tipoSer= Tipo.PARQUEO;
				}
				if(tipo.equals("LAVADO")) {
					tipoSer= Tipo.LAVADO;
				}
				if(tipo.equals("PINTURA")) {
					tipoSer= Tipo.PINTURA;
				}
				if(tipo.equals("MANTENIMIENTO")) {
					tipoSer= Tipo.MANTENIMIENTO;
				}
				serv = new Servicio(tipoSer, empl);
				parq.getListaServicios().add(serv);
			}
			
		}

	public static void guardarEmpleado(Parqueadero parq) throws IOException{
		String contenido ="";
		for(int i=0; i< parq.getListaEmpleados().size(); i++) {
			if(parq.getListaEmpleados().get(i)!=null && parq.getListaEmpleados().get(i).getCedula()!="EMPLEADO_ELIMINADO") {
				Empleado empleadoGuardar = parq.getListaEmpleados().get(i);
				contenido += empleadoGuardar.getNombre() + "@@" + empleadoGuardar.getCedula() + "@@" + empleadoGuardar.getNumTelefono() + "\n";
			}
		}
		ArchivoUtil.guardarArchivo(RUTA_ARCHIVO_EMPLEADOS, contenido, false);
	}
	
	public static void guardarInformeAdministrativo(String usuario, String reporte) throws IOException {
		String titulo = "		*-Informe de gestion administrativa-*\n";
		Calendar horayfecha = new GregorianCalendar();
		String fech   = "			Fecha: [" + horayfecha.get(Calendar.DAY_OF_MONTH) + "/" + (horayfecha.get(Calendar.MONTH)+1) + "/" + horayfecha.get(Calendar.YEAR) + "]\n";
		String user   = "			Reporte realizado por: [" + usuario + "]\n\n";
		String mensajetotal = titulo + fech + user + reporte;
		ArchivoUtil.guardarArchivo(RUTA_ARCHIVO_INFORMES_ADMINISTRATIVOS, mensajetotal, false);
	}
	
	public static void guardarInformeVehiculo(String usuario, String reporte) throws IOException {
		String titulo = "		*-Informe de gestion de Gestion de Vehiculos-*\n";
		Calendar horayfecha = new GregorianCalendar();
		String fech   = "			Fecha: [" + horayfecha.get(Calendar.DAY_OF_MONTH) + "/" + (horayfecha.get(Calendar.MONTH)+1) + "/" + horayfecha.get(Calendar.YEAR) + "]\n";
		String user   = "			Reporte realizado por: [" + usuario + "]\n\n";
		String mensajetotal = titulo + fech + user + reporte;
		ArchivoUtil.guardarArchivo(RUTA_ARCHIVO_INFORMES_VEHICULOS , mensajetotal, false);
		
	}
	public static void guardarServicios(Parqueadero parq) throws IOException{
		String contenido ="";
		for(int i=0; i< parq.getListaServicios().size(); i++) {
			if(parq.getListaServicios().get(i)!=null && parq.getListaServicios().get(i).getEmp().getNombre()!="SERVICIO_ELIMINADO") {
				Servicio servicioGuardar = parq.getListaServicios().get(i);
				contenido += servicioGuardar.getEmp().getNombre() + "@@" + servicioGuardar.getEmp().getCedula() + "@@" + servicioGuardar.getEmp().getNumTelefono() + "@@" + servicioGuardar.getTipo().toString() + "\n";			}
		}
		ArchivoUtil.guardarArchivo(RUTA_ARCHIVO_SERVICIO, contenido, false);
	}
	
	public static void escribirEnLog(String notificacion, int nivel,  String accion) {
		ArchivoUtil.escribirEnLog(RUTA_ARCHIVO_LOG, notificacion, nivel, accion);
	}
	
	public static Parqueadero cargarModelo() throws IOException {
		Object objeto = ArchivoUtil.cargarArchivo(RUTA_ARCHIVO_PARQUEADERO);
		Parqueadero parqueadero = (Parqueadero) objeto;
		return parqueadero;
	}
	
	public static Object cargarRecursoSerializadoXML(String rutaArchivo) throws IOException {
        XMLDecoder decodificadorXML;
        Object objetoXML;
        decodificadorXML = new XMLDecoder(new FileInputStream(rutaArchivo));
        objetoXML = decodificadorXML.readObject();
        decodificadorXML.close();
        return objetoXML;
    }
   
    public static void salvarRecursoSerializadoXML(String rutaArchivo, Object objeto) throws IOException {
        XMLEncoder codificadorXML;
        codificadorXML = new XMLEncoder(new FileOutputStream(rutaArchivo));
        codificadorXML.writeObject(objeto);
        codificadorXML.close();
    }

    public static void serializarBinario(String ruta, Object objeto) throws IOException {
		FileOutputStream fileOutputS = new FileOutputStream(ruta);
		ObjectOutputStream objectOutputS = new ObjectOutputStream(fileOutputS);
		objectOutputS.writeObject(objeto);
		fileOutputS.close();
		objectOutputS.close();
	}
    
	public static void copiarArchivo(String rutaArchivoCopiar, String tipo) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			File original = new File(rutaArchivoCopiar);
			File copia = new File("C:/td/Persistencia/respaldo/copia" + tipo);
			
			inputStream = new FileInputStream(original);
			outputStream = new FileOutputStream(copia);
			byte[] buffer = new byte[1024];
			int lenght;
			while ((lenght=inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, lenght);
			}
			inputStream.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
