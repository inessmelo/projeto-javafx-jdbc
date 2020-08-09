package gui;

import java.io.IOException;
import java.net.URL;
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
import modelo.entidade.Departamento;
import modelo.servico.DepartamentoServico;

public class DepartamentoListaControle implements Initializable, OuvindoAlteracaoDados {

	private DepartamentoServico servico;
	@FXML
	private TableView <Departamento> tabelaDepartamento;   //referencia a tabela do departamento
	@FXML
	private TableColumn <Departamento, Integer> colunaId;  //referencia a coluna Id
	@FXML
	private TableColumn <Departamento, String> colunaNome; //referencia a coluna nome
	@FXML
	private TableColumn<Departamento, Departamento> colunaEditar; //coluna para bot�es editar
	@FXML
	private TableColumn<Departamento, Departamento> colunaRemover;//coluna para bot�es remover
	@FXML
	private Button btnNovo;                                //referencia o bot�o Novo
	 
	private ObservableList<Departamento> obsLista;
	
	public void setDepartamentoServico (DepartamentoServico servico) {
		this.servico = servico;
	}
	
	@FXML
	public void onBtnNovoAction (ActionEvent evento) {
		Stage parentStage = Util.palcoAtual(evento);
		Departamento dep = new Departamento();           //come�ando a janela sem dados
		criandoDialogoFormulario(dep, "/gui/DepartamentoForma.fxml", parentStage);
	}
		
	@Override
	public void initialize(URL url, ResourceBundle rb) {	
		inicializandoNodes();
	}

	public void inicializandoNodes() {
		//inicia o comportamento das tabelas
		colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		
		
		//macete para a tabela ficar no mesmo tamanho que a janela
		Stage stage = (Stage) Main.getPrincipalCena().getWindow();             
		tabelaDepartamento.prefHeightProperty().bind(stage.heightProperty());   //TableView acompanhar a tela
	}
	
	//acesso o servi�o, carrega os departamentos e o joga na obsLista
	public void atualizaTableView() {
		if (servico == null) {                                 //s� pq a inje��o de dependencia est� manual
			throw new IllegalStateException("Servi�o estava nulo!");  //pq est� manual a inje��o
		}
		List <Departamento> lista = servico.procuraTudo();     //recupera os departamentos do servi�o 
		obsLista = FXCollections.observableArrayList(lista);   //instancia o obsLista
		tabelaDepartamento.setItems(obsLista);                 //carrega a instancia��o acima e mostra na lista
		botaoEditar();                                         //inseri o bot�o editar na coluna em cada linha
		botaoRemover();                                        //inseri o bot�o remover na coluna em cada linha
	}
	
	private void criandoDialogoFormulario (Departamento dep, String nomeAbsoluto, Stage parentStage) {    
		try {
			FXMLLoader carrega = new FXMLLoader(getClass().getResource(nomeAbsoluto));  //carrega tela
			Pane painel = carrega.load();                                               //
			
			DepartamentoFormaControle controle = carrega.getController();//pegando a tela carregada e enviando para a janela DepForma
			controle.setDepartamento(dep);                      //injetando a tela no Departamento e passando o dep como argumento
			controle.setDepartamentoServico(new DepartamentoServico()); //instancia um novo departamento no cadastro
			controle.assinaOuvintesAlterandoDados(this);        //est� se inscrevendo pra receber o objeto
			controle.atualizaDadosFormulario();                 //carrega os dados acima no formulario
			
			Stage tela = new Stage();                           //instanciando uma nova janela (janela na frente da janela)
			tela.setTitle("Digite os dados do Departamento");   //nome do t�tulo
			tela.setScene(new Scene(painel));                   //chamando a janela instanciada acima
			tela.setResizable(false);                           //pode ou n�o redimencionar a janela
			tela.initOwner(parentStage);                        //passando o Stage pai da janela
			tela.initModality(Modality.WINDOW_MODAL);           //diz se a janela � ou n�o modal
			tela.showAndWait();
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerta.showAlert("IO Exception", "Erro carregando tela", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDadosAlterados() {
		atualizaTableView();
	}
	
	//cria um bot�o de edi��o para cada linha na tabela
	public void botaoEditar() {
		colunaEditar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaEditar.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			 private final Button btn = new Button("editar");
			 
			 @Override
			 protected void updateItem (Departamento dep, boolean vazio) {
				 super.updateItem(dep, vazio);
				 if (dep == null) {
					 setGraphic(null);
					 return;
				 }
				 setGraphic(btn);
				 btn.setOnAction(evento -> criandoDialogoFormulario(
						 dep, "/gui/DepartamentoForma.fxml", Util.palcoAtual(evento))); //abrindo o formul�rio de edi��o
			 }
		});
	}
	
	private void botaoRemover() {
		colunaRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaRemover.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button btn = new Button("remover");
			
			@Override
			protected void updateItem (Departamento dep, boolean vazio) {
				super.updateItem(dep, vazio);
				if (dep == null) {
					setGraphic(null);
					return;
				}
				setGraphic(btn);
				btn.setOnAction(evento -> removeEntidade(dep));
			}
		});
	}

	protected void removeEntidade(Departamento dep) {
		Optional <ButtonType> remove = Alerta.showConfirmation("Confirma��o", "Tem certeza que deseja deletar?");
		
		if (remove.get() == ButtonType.OK) {   //se o botao OK foi clicado
			if (servico == null) {
				throw new IllegalStateException("Servi�o est� vazio!"); //programador esqueceu de injetar
			}
			try {
				servico.remove(dep);           //removendo a linha
				atualizaTableView();           //atualizando a tela
			}
			catch (DbExcecaoIntegridade e) {
				Alerta.showAlert("Erro ao remover o objeto!", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
	}
		
}
