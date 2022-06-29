package application;
	
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
//import parqueadero.ModelFactoryController.ModelFactoryController;
import parqueadero.model.Parqueadero;
import parqueadero.persistencia.Persistencia;
//import parqueadero.views.CRUDParqueaderoController;
import server.Servidor;

public class Main  {    
	
	static Servidor miServidor;
	
	
	public static void main(String args[])throws IOException{
		miServidor=new Servidor();
		//ejecuta la aplicacion
		try {
			miServidor.runServer();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace(); 
			
		}
	}
}
