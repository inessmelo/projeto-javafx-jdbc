package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerta;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import modelo.servico.DepartamentoServico;
import modelo.servico.VendedorServico;

public class MainViewControle implements Initializable {

	@FXML
	private MenuItem menuVendedor;
	@FXML
	private MenuItem menuDepartamento;
	@FXML
	private MenuItem menuAjuda;
	
	@FXML
	//abrindo a tela de Vendedor no menu
	public void onMenuVendedorAction() {
		carregaTela("/gui/VendedorLista.fxml", (VendedorListaControle controle) -> {
			controle.setVendedorServico(new VendedorServico());
			controle.atualizaTableView();
		});
	}
	
	@FXML
	//abrindo a tela de Departamento no menu
	public void onMenuDepartamentoAction() {
		carregaTela("/gui/DepartamentoLista.fxml", (DepartamentoListaControle controle) -> {
			controle.setDepartamentoServico(new DepartamentoServico());
			controle.atualizaTableView();
		}); 
	}
	
	@FXML
	public void onMenuAjudaAction() {
		carregaTela("/gui/Sobre.fxml", x -> {});
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
	}
	
	private synchronized <T> void carregaTela (String nomeCaminho, Consumer<T> inicializacaoAction) {
		try {
			FXMLLoader carrega = new FXMLLoader(getClass().getResource(nomeCaminho));    //para carregar a tela
			VBox vBox = carrega.load();                                                  //objeto que está no Scene
			
			Scene principalCena = Main.getPrincipalCena();                               //
			VBox menuVBox = (VBox) ((ScrollPane) principalCena.getRoot()).getContent();  //
			
			Node principalMenu = menuVBox.getChildren().get(0);
			menuVBox.getChildren().clear();                      //limpando todos os children do ManinVBox
			menuVBox.getChildren().add(principalMenu);           //adicionando os children no menuPrincipal
			menuVBox.getChildren().addAll(vBox.getChildren());   //adicionando os children do vBox
			
			//executando a função passada por argumento
			T controle = carrega.getController();	
			inicializacaoAction.accept(controle);
		}
		catch (IOException e) {
			Alerta.showAlert("Erro: IO Exception", "Erro carregando a página", e.getMessage(), AlertType.ERROR);
		}
	}
}
