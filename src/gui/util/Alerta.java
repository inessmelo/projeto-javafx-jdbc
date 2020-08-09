package gui.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Alerta {

	//janela de alerta
	public static void showAlert (String titulo, String cabecalho, String conteudo, AlertType tipo) {
		Alert alerta = new Alert(tipo);
		alerta.setTitle(titulo);
		alerta.setHeaderText(cabecalho);
		alerta.setContentText(conteudo);
		alerta.show();
	}
	
	//janela de alerta de confirmação
	public static Optional<ButtonType> Confirmacao(String titulo, String conteudo){
		Alert alerta = new Alert(AlertType.CONFIRMATION);
		alerta.setTitle(titulo);
		alerta.setHeaderText(null);
		alerta.setContentText(conteudo);
		return alerta.showAndWait();
	} 
	
	public static Optional<ButtonType> showConfirmation(String title, String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		return alert.showAndWait();
		}
}
