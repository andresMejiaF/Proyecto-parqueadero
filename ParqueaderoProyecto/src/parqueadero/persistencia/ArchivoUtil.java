package parqueadero.persistencia;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class ArchivoUtil {

	public static ArrayList<String> cargarArchivo(String rutaArchivo) throws IOException{
		ArrayList<String> contenido = new ArrayList<>();
		FileReader fileReader = new FileReader(rutaArchivo);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String linea = "";
		while ((linea = bufferedReader.readLine()) != null) {
			contenido.add(linea);
		}
		bufferedReader.close();
		fileReader.close();
		return contenido;
	}
	
	public static void guardarArchivo(String rutaArchivo, String contenido, boolean flagSobreescribir) throws IOException{
		FileWriter fileWriter = new FileWriter(rutaArchivo, flagSobreescribir);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write(contenido);
		bufferedWriter.close();
		fileWriter.close();
	}

	public static void escribirEnLog(String rutaArchivoLog, String mensaje, int nivel, String accion) {
		String log = "";
		Logger LOGGER = Logger.getLogger(accion);
		FileHandler fileHandler = null;
		String fechaSistema = cargarFechaSistema();
		try{
			fileHandler = new FileHandler (rutaArchivoLog, true);
			fileHandler.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(fileHandler);
			switch (nivel){
			case 1:
				LOGGER.log(Level.INFO, accion + "," + mensaje + "," + fechaSistema);
				break;
			case 2:
				LOGGER.log(Level.WARNING, accion + "," + mensaje + "," + fechaSistema);
				break;
			case 3:
				LOGGER.log(Level.SEVERE, accion + "," + mensaje + "," + fechaSistema);
				break;
			default:
				break;
			}
		}catch (SecurityException e){
				LOGGER.log(Level.SEVERE, e.getMessage());
		}catch (IOException e){
			MessageDialog.openError(new Shell(), "Error", e + "");
		}finally {
			fileHandler.close();
		}
	}
	
	private static String cargarFechaSistema() {
		String diaN = "";
		String mesN = "";
		String anioN = "";
		Calendar cal1 = Calendar.getInstance();
		int dia = cal1.get(Calendar.DATE);
		int mes = cal1.get(Calendar.MONTH)+1;
		int anio = cal1.get(Calendar.YEAR);
		int hora = cal1.get(Calendar.HOUR);
		int minuto = cal1.get(Calendar.MINUTE);
		if (dia<10){
			diaN += "0" + dia;
		}
		else{
			diaN += "" + dia;
		}
		if (mes<10){
			mesN += "0" + mes;
		}
		else{
			mesN += "" + mes;
		}
		return anio + "-" + mesN + "-" + diaN + "-" + hora + "-" + minuto;
	}
	
	//LEER BINARIO
	public static Object cargarRecursoSerializado (String rutaArchivo)throws Exception {
        Object aux = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(rutaArchivo));
            aux = ois.readObject();
        } catch (Exception e2) {
            throw e2;
        } finally {
            if (ois != null)
                ois.close();
        }
        return aux;
    }
	
	//GUARDAR BINARIO
	public static void salvarRecursoSerializado (String rutaArchivo, Object object) throws Exception{
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo));
			oos.writeObject(object);
		} catch (Exception e) {
			throw e;
		}finally {
			if(oos != null) {
				oos.close();
			}
		}
	}
	
	// SERIALIZACIÓN
	public static Object cargarObjeto(String ruta) throws IOException, ClassNotFoundException {
		FileInputStream archivoIS = new FileInputStream(ruta);
		ObjectInputStream ois = new ObjectInputStream(archivoIS);
		Object objeto = ois.readObject();
		archivoIS.close();
		ois.close();
		return objeto;
	}

	public static void guardarObjeto(Object objeto, String ruta) throws IOException {
		FileOutputStream archivoOS = new FileOutputStream(ruta);
		ObjectOutputStream oos = new ObjectOutputStream(archivoOS);
		oos.writeObject(objeto);
		archivoOS.close();
		oos.close();
	}
}
