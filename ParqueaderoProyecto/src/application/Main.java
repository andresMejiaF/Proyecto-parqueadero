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
import parqueadero.ModelFactoryController.ModelFactoryController;
import parqueadero.model.Parqueadero;
import parqueadero.persistencia.Persistencia;
import parqueadero.views.CRUDParqueaderoController;

public class Main extends Application {
	
	@FXML TextField tfcedulaa;
	private Stage primaryStage;
	private BorderPane rootLayout;
	private AnchorPane login;
	ModelFactoryController modelFactoryController;
	Parqueadero parqueadero;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public Main() {
		modelFactoryController = new ModelFactoryController();
		parqueadero = modelFactoryController.getParqueadero();
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Parqueadero de vehiculos.");
		initRootLayout();
		showPersonOverview();
//		modelFactoryController = ModelFactoryController.getInstance();
//		try {
//			modelFactoryController.inicializarDatos();
//		} catch (Exception e) {
//			// TODO Bloque catch generado automáticamente
//			e.printStackTrace();
//		}
	}

	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("/parqueadero/views/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			Scene scene = new Scene(rootLayout,982,540);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showPersonOverview() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("/parqueadero/views/ParqueaderoView.fxml"));
			AnchorPane parqueaderoOV = (AnchorPane) loader.load();
			rootLayout.setCenter(parqueaderoOV);
			CRUDParqueaderoController controller = loader.getController();
			controller.setMain(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void ayuda() {
		String list = modelFactoryController.getParqueadero().listarCedulasEmpleados();
		Alert mensaje;
		mensaje = new Alert (Alert.AlertType.INFORMATION);
        mensaje.setContentText(list);
        mensaje.showAndWait();
	}
	
	public void iniciarSesion() {
		String cedul = tfcedulaa.getText();
		boolean aux = false;
		for(int i=0; i<modelFactoryController.getParqueadero().getListaEmpleados().size() && aux==false; i++) {
			if(cedul.equals(modelFactoryController.getParqueadero().getListaEmpleados().get(i).getCedula())) {
				aux = true;
			} 	
		}
		if(aux==false) {
			String notif = "¡Acceso DENEGADO!";
			Alert mensaje;
			mensaje = new Alert (Alert.AlertType.WARNING);
	        mensaje.setContentText(notif);
	        mensaje.showAndWait();
		}
		
		if(aux==true) {
			String notif = "¡Acceso concedido!";
			Alert mensaje;
			mensaje = new Alert (Alert.AlertType.INFORMATION);
	        mensaje.setContentText(notif);
	        mensaje.showAndWait();
	        initRootLayout();
	        showPersonOverview();
		}
	}
}