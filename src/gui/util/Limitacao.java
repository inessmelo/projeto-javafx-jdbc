package gui.util;

import javafx.scene.control.TextField;

public class Limitacao {
	
	public static void setTextoCampoInteiro (TextField texto) {
		texto.textProperty().addListener( (ref, valorVelho, valorNovo) -> {
			if (valorNovo != null && !valorNovo.matches("\\d*")) {
				texto.setText(valorVelho);
			}
		});
	}

	public static void setTextoCampoDouble (TextField texto) {
		texto.textProperty().addListener( (ref, valorVelho, valorNovo) -> {
			if (valorNovo != null && !valorNovo.matches("\\d*([\\.]\\d*)?")) {
				texto.setText(valorVelho);
			}
		});
	}
	
	public static void setTextoCampoTamMaximo (TextField texto, int max) {
		texto.textProperty().addListener( (ref, valorVelho, valorNovo) -> {
			if (valorNovo != null && valorNovo.length() > max) {
				texto.setText(valorVelho);
			}
		});
	}
}
