package gui.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class Util {

	public static Stage palcoAtual(ActionEvent evento) {
		return (Stage) ((Node) evento.getSource()).getScene().getWindow(); // abrindo a tela do evento
	}

	//so aceita numero inteiro
	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);
		} 
		catch (NumberFormatException e) {
			return null;
		}
	}
	
	//só aceita numero com virgula
	public static Double tryParseToDouble(String str) {
		try {
			return Double.parseDouble(str);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	// formatando a data
	public static <T> void formatarColunaData(TableColumn<T, Date> tabelaColuna, String formato) {
		tabelaColuna.setCellFactory(coluna -> {
			TableCell<T, Date> celula = new TableCell<T, Date>() {
				private SimpleDateFormat sdf = new SimpleDateFormat(formato);

				@Override
				protected void updateItem(Date item, boolean vazio) {
					super.updateItem(item, vazio);
					if (vazio) {
						setText(null);
					} else {
						setText(sdf.format(item));
					}
				}
			};
			return celula;
		});
	}

	// formatando numero com ponto flutuante
	public static <T> void formatarColunaDouble(TableColumn<T, Double> tabelaColuna, int casaDecinal) {
		tabelaColuna.setCellFactory(coluna -> {
			TableCell<T, Double> celula = new TableCell<T, Double>() {

				@Override
				protected void updateItem(Double item, boolean vazio) {
					super.updateItem(item, vazio);
					if (vazio) {
						setText(null);
					} else {
						// Locale.setDefault(Locale.US);
						setText(String.format("%." + casaDecinal + "f", item));
					}
				}
			};
			return celula;
		});
	}

	// formatar a data para aparecer no formato que quiser (DatePicker)
	public static void formatoDatePicker (DatePicker dataPicker, String formato) {
		dataPicker.setConverter(new StringConverter<LocalDate>() {
			DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern(formato);
			{
				dataPicker.setPromptText(formato.toLowerCase());
			}
			
			@Override
			public String toString (LocalDate data) {
				if (data != null) {
					return dataFormatter.format(data);
				}
				else {
					return "";
				}
			}
			
			public LocalDate fromString (String str) {
				if (str != null && !str.isEmpty()) {
					return LocalDate.parse(str, dataFormatter);
				}
				else {
					return null;
				}				
			}				
		});
	}
}
