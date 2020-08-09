package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Scene principalCena;
	
	@Override
	public void start(Stage primeiroPalco) {
		try {
			//instanciando passando o caminho da Vie(tela)
			FXMLLoader carrega = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			
			ScrollPane scrollPane = carrega.load();  //Carrega o ScrollPane da classe MainView
			scrollPane.setFitToHeight(true);         //altura da barra de menu no tamanho da tela
			scrollPane.setFitToWidth(true);          //largura da barra de menu no tamanho da tela
			
			principalCena = new Scene(scrollPane);
			primeiroPalco.setScene(principalCena);
			primeiroPalco.setTitle("Tela de Cadadstro");  //definindo o título pra cena
			primeiroPalco.show();                         //mostrando o palco
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Scene getPrincipalCena() {
		return principalCena;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
