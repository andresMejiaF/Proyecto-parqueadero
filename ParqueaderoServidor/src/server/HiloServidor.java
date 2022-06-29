package server;
import java.io.*;
import java.net.*;
import java.util.*;

//import co.edu.uniquindio.banco.model.Banco;
//import co.edu.uniquindio.servidor.persistencia.Persistencia;
import parqueadero.model.*;
import parqueadero.persistencia.*;

public class HiloServidor extends Thread {
     String idAplicacion;
     Servidor miServidor;
     
     DataInputStream flujoEntradaComunicacion=null; 
     DataOutputStream flujoSalidadComunicacion=null;
     ObjectOutputStream flujoSalidaObjeto=null;
     ObjectInputStream flujoEntradaObjeto=null;  
     DataOutputStream dos;
     byte[] byteArray;
     int in;
     File localFileServer;
     int opcion=0;
     
     Parqueadero parqueadero;
     
     public HiloServidor(){
    	 
     }
     
     public void inicializarConexion(DataInputStream flujoEntradaComunicacion,DataOutputStream flujoSalidadComunicacion, ObjectOutputStream flujoSalidaObjeto,
		 ObjectInputStream flujoEntradaObjeto, Servidor servidor, String idAplicacion) {
		
    	 this.miServidor=servidor;
    	 this.idAplicacion = idAplicacion;
    	 this.flujoEntradaComunicacion = flujoEntradaComunicacion;
    	 this.flujoSalidadComunicacion = flujoSalidadComunicacion;
    	 this.flujoSalidaObjeto = flujoSalidaObjeto;
    	 this.flujoEntradaObjeto = flujoEntradaObjeto;
	}
     
     public void run(){
    	 try{
    		 opcion=flujoEntradaComunicacion.readInt();
    		 System.out.println(opcion);
    		 switch(opcion) {
    		 case 1://Solicitud de enviar el objeto  de persistencia
    			 try {
    				 enviarInformacionPersistencia(); 
    			 } catch (Exception e) {
    				 // TODO Auto-generated catch block
    				 e.printStackTrace();
    			 }

    			 break;
    		 case 2:////Solicitud de guardar el objeto  de persistencia
    			 guardarInformacionPersistenciaXml();
    			 break;

    		 case 3: 
    			 guardarInformacionPersistenciaBinariol();
    			 break;
    		
    		 case 4:
    			 guardarEmpleados();
    			 break;
    		 case 5:
    			 	guardarServicios();
    			 break;
    		 }
    		 
    	 }catch (Exception e) {
    		 // TODO: handle exception
    	 }

     }
     
     private void guardarServicios() throws ClassNotFoundException, IOException {
    	 parqueadero =  (Parqueadero) flujoEntradaObjeto.readObject(); 
    	 System.out.println("Se guardo el servicio ");

    	 Persistencia.guardarServicios(parqueadero);
	}


	private void guardarEmpleados() throws ClassNotFoundException, IOException {
    	 parqueadero =  (Parqueadero) flujoEntradaObjeto.readObject(); 
    	 System.out.println("Se guardo el empleado ");

    	 Persistencia.guardarEmpleado(parqueadero);
	}


	private void enviarInformacionPersistencia() throws IOException,Exception {
    	 parqueadero= Persistencia.cargarModelo();
    	 if(parqueadero == null){
    		 parqueadero= new Parqueadero();
    	 }
    	 System.out.println("Se envio la información del parqueadero");
    	 flujoSalidaObjeto.writeObject(parqueadero);
	}
     
     private void guardarInformacionPersistenciaXml() throws IOException,Exception {
    	 parqueadero =  (Parqueadero) flujoEntradaObjeto.readObject(); 
    	 Persistencia.serializarBinario(Persistencia.RUTA_ARCHIVO_PARQUEADERO, parqueadero);
    	 System.out.println("Se guardo la información del parqueadero");
     }

    
     private void guardarInformacionPersistenciaBinariol() throws IOException,Exception {
    	 parqueadero =  (Parqueadero) flujoEntradaObjeto.readObject(); 
    	 Persistencia.serializarBinario(Persistencia.RUTA_ARCHIVO_PARQUEADERO, parqueadero);
    	 System.out.println("Se guardo la información del parqueadero");
     }
	
     public String getIdAplicacion() {
       return idAplicacion;
     }
     
     public void setidAplicacion(String idAplicacion) {
       this.idAplicacion=idAplicacion;
     }
}