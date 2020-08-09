package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbExcecaoIntegridade;
import gui.ouvindo.OuvindoAlteracaoDados;
import gui.util.Alerta;
import gui.util.Util;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.entidade.Vendedor;
import modelo.servico.DepartamentoServico;
import modelo.servico.VendedorServico;

public class VendedorListaControle implements Initializable, OuvindoAlteracaoDados {

	private VendedorServico servico;
	private ObservableList<Vendedor> obsLista;
	@FXML
	private TableView<Vendedor> tabelaVendedor;
	@FXML
	private TableColumn<Vendedor, Integer> colunaId;
	@FXML
	private TableColumn<Vendedor, String> colunaNome;
	@FXML
	private TableColumn<Vendedor, String> colunaEmail;
	@FXML
	private TableColumn<Vendedor, Date> colunaDataNasc;
	@FXML
	private TableColumn<Vendedor, Double> colunaSalario;
	@FXML
	private TableColumn<Vendedor, Vendedor> colunaEditar; //coluna para botões editar
	@FXML
	private TableColumn<Vendedor, Vendedor> colunaRemover;//coluna para botões remover
	@FXML
	private Button btnNovo;                                //referencia o botão Novo
	
	public void setVendedorServico(VendedorServico servico) {
		this.servico = servico;
	}

	@FXML
	public void onBtnNovoAction (ActionEvent evento) {
		Stage parentStage = Util.palcoAtual(evento);
		Vendedor vend = new Vendedor();
		criandoDialogoFormulario(vend, "/gui/VendedorForma.fxml", parentStage);
	}
	
	private void criandoDialogoFormulario(Vendedor vend, String nomeAbsoluto, Stage paretStage) {
		try {
			FXMLLoader carrega = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			Pane painel = carrega.load();
			
			VendedorFormaControle controle = carrega.getController();  //pegando a tela carregada e enviando para a janela DepForma
			controle.setVendedor(vend);                                //injetando a tela no Departamento e passando o dep como argumento
			controle.setServicos(new VendedorServico(), new DepartamentoServico());        //instancia um novo vendedor no cadastro e o departamento no scroolBox
			controle.carregandoObjetosAssociados();
			controle.assinaOuvintesAlterandoDados(this);               //está se inscrevendo pra receber o objeto
			controle.atualizaDadosFormulario();                        //carrega os dados acima no formulario
			
			Stage tela = new Stage();                                  //instanciando uma nova janela (janela na frente da janela)
			tela.setTitle("Digite os dados do Vendedor");              //nome do título
			tela.setScene(new Scene(painel));                          //chamando a janela instanciada acima
			tela.setResizable(false);                                  //pode ou não redimencionar a janela
			tela.initOwner(paretStage);                                //passando o Stage pai da janela
			tela.initModality(Modality.WINDOW_MODAL);                  //diz se a janela é ou não modal
			tela.showAndWait();			
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerta.showAlert("IO Exception", "Erro carreganto a tela", e.getMessage(), AlertType.ERROR);
		}
	}
		
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializandoNodes();
	}

	private void inicializandoNodes() {
		// inicia o comportamento das tabelas
		colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colunaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colunaDataNasc.setCellValueFactory(new PropertyValueFactory<>("dataNasc"));
		Util.formatarColunaData(colunaDataNasc, "dd/MM/yyyy");    //pegando o método do formato de data
		colunaSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));
		Util.formatarColunaDouble(colunaSalario, 2);              //pegando o método do formato de double
		
		//macete para a tabela ficar no mesmo tamanho que a janela
		Stage stage = (Stage) Main.getPrincipalCena().getWindow();
		tabelaVendedor.prefHeightProperty().bind(stage.heightProperty());  //TableView acompanha a tela
	}
	
	//ACESSA O SERVIÇO, CARREGA OS VENDEDORES E OS JOGA NA OBLISTA
	public void atualizaTableView() {
		if (servico == null) {                                        //dependencia manual
			throw new IllegalStateException("Serviço está vazio!");
		}
		List <Vendedor> lista = servico.procuraTudo();                //recupera os vendedores
		obsLista = FXCollections.observableArrayList(lista);          //instancia o obsLista
		tabelaVendedor.setItems(obsLista);                            //carrega a lista acima
		botaoEditar();                                                //atualiza a tela com o botão editar
		botaoRemover();                                               //atualiza a tela com o botão remover
	}
	
	@Override
	public void onDadosAlterados() {
		atualizaTableView();
	}
		
	//cria um botal de edição em cada linha da tabela
	private void botaoEditar() {
		colunaEditar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaEditar.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button btn = new Button("editar");
			
			@Override
			protected void updateItem (Vendedor vend, boolean vazio) {
				super.updateItem(vend, vazio);
				if (vend == null) {
					setGraphic(null);
					return;
				}
				setGraphic(btn);
				btn.setOnAction(evento -> criandoDialogoFormulario(
						vend, "/gui/VendedorForma.fxml", Util.palcoAtual(evento)));    //abrindo o formulario de edição
			}			
		});		
	}
	
	//cria um botão de remover em cada linha da tabela
	private void botaoRemover() {
		colunaRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaRemover.setCellFactory(param -> new TableCell<Vendedor, Vendedor>(){
			private final Button btn = new Button("remover");
			
			@Override
			protected void updateItem (Vendedor vend, boolean vazio) {
				super.updateItem(vend, vazio);
				if (vend == null) {
					setGraphic(null);
					return;
				}
				setGraphic(btn);
				btn.setOnAction(evento -> removeEntidade(vend));
			}
		});			
	}
	
	private void removeEntidade(Vendedor vend) {
		Optional<ButtonType> remove = Alerta.showConfirmation("Confirmação", "Tem certeza que deseja deletar!");
		
		if (remove.get() == ButtonType.OK) {  //se o botão OK foi clicado
			if (servico == null) {
				throw new IllegalStateException("Serviço está vazio!");
			}
			try {
				servico.remove(vend);         //removendo a linha na tabela
				atualizaTableView();          //atualizando a tabela
			}
			catch (DbExcecaoIntegridade e) {
				Alerta.showAlert("Erro ao remover o objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
