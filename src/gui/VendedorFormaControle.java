package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbExcecao;
import gui.ouvindo.OuvindoAlteracaoDados;
import gui.util.Alerta;
import gui.util.Limitacao;
import gui.util.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import modelo.entidade.Departamento;
import modelo.entidade.Vendedor;
import modelo.excecao.ValidacaoExcecao;
import modelo.servico.DepartamentoServico;
import modelo.servico.VendedorServico;

public class VendedorFormaControle implements Initializable {

	private Vendedor entidade;
	private VendedorServico servico;
	private DepartamentoServico depServ;
	private List<OuvindoAlteracaoDados> dadosAlterados = new ArrayList<>();
	private ObservableList<Departamento> obsLista;
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNome;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpDataNasc;
	@FXML
	private TextField txtSalario;
	@FXML
	private Label lblMsgErroNome;
	@FXML
	private Label lblMsgErroEmail;
	@FXML
	private Label lblMsgErroDataNasc;
	@FXML
	private Label lblMsgErroSalario;
	@FXML
	private ComboBox<Departamento> cmbDepartamento;
	@FXML
	private Button btnSalvar;
	@FXML
	private Button btnCancelar;

	public void setVendedor(Vendedor entidade) {
		this.entidade = entidade;
	}

	public void setServicos(VendedorServico servico, DepartamentoServico depServ) {
		this.servico = servico;
		this.depServ = depServ;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializandoNodes();
	}

	@FXML
	public void onBtnSalvarAction(ActionEvent evento) {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está vazia!");
		}
		if (servico == null) {
			throw new IllegalStateException("aservico está vazio!");
		}
		try {
			entidade = obterDadosFormulario(); // pegando os dados do método abaixo
			servico.salvaOuAtualiza(entidade); // salvando no BD
			noticandoDadosAlterados();
			Util.palcoAtual(evento).close();   // fechando a janela
		} 
		catch (ValidacaoExcecao e) {
			definirMsgErroNome(e.getErros());
		} 
		catch (DbExcecao e) {
			Alerta.showAlert("Erro ao salvar os dados", null, e.getMessage(), AlertType.ERROR);
		}
	}

	// emite msg de erro no campo Nome
	private void definirMsgErroNome(Map<String, String> erros) {
		Set<String> arquivos = erros.keySet();
		if (arquivos.contains("nome")) {
			lblMsgErroNome.setText(erros.get("nome"));
		}
		else {
			lblMsgErroNome.setText("");
		}
		//lblMsgErroNome.setText((arquivos.contains("nome") ? erros.get("nome") : "")); //condição ternaria
		if (arquivos.contains("email")) {
			lblMsgErroEmail.setText(erros.get("email"));
		}
		else {
			lblMsgErroEmail.setText("");
		}
		//lblMsgErroEmail.setText( (arquivos.contains("email") ? erros.get("email") : "") );  //condição ternaria
		if (arquivos.contains("salario")) {
			lblMsgErroSalario.setText(erros.get("salario"));
		}
		else {
			lblMsgErroSalario.setText("");
		}
		//lblMsgErroSalario.setText((arquivos.contains("salario") ? erros.get("email") : ""));  //condição ternaria
		if (arquivos.contains("dataNasc")) {
			lblMsgErroDataNasc.setText(erros.get("dataNasc"));
		}
		else {
			lblMsgErroDataNasc.setText("");
		}
		//lblMsgErroDataNasc.setText((arquivos.contains("dataNasc") ? erros.get("dataNasc") : ""));  //condição ternaria
	}

	private void noticandoDadosAlterados() {                 // o metodo é executado na interface Ouvindo...
		for (OuvindoAlteracaoDados lista : dadosAlterados) { // pra cada obj alterado na lista
			lista.onDadosAlterados();                        // irá notificar
		}
	}

	private Vendedor obterDadosFormulario() {            //pega os dados preenchido no formulario e carrega no objeto vend
		Vendedor vend = new Vendedor();                  // instanciando um novo vendedor
		ValidacaoExcecao validar = new ValidacaoExcecao("Erro de validação!");

		vend.setId(Util.tryParseToInt(txtId.getText()));                // pega o numero da caixa de texto e converte para inteiro

		//validando o campo da caixa de texto Nome do vendedor
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) { // se a caixa de texto estiver vazio ou tiver espaço em branco no inicio e fim
			validar.addErros("nome", "O campo não pode ficar vazio");   //informa na tela msg de erro
		}
		vend.setNome(txtNome.getText());                                //convertendo os dados informados no TextField em texto
		
		//validando o campo da vcaixa de texto E-mail do vendedor
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			validar.addErros("email", "O campo não pode ficar vazio");
		}
		vend.setEmail(txtEmail.getText());                              //convertendo os dados informados no TextField em texto
		
		//validando o campo DatePicker Data de nascimento do vendedor
		if (dpDataNasc.getValue() == null) {
			validar.addErros("dataNasc", "O campo não pode ficar vazio");
		}
		else {
			Instant instante = Instant.from(dpDataNasc.getValue().atStartOfDay(ZoneId.systemDefault()));  //converte a data do programa para a data da PC do usuario
			vend.setDataNasc(Date.from(instante));   
		}
		
		//validando o campo da caixa e texto do salario do vendedor
		if (txtSalario.getText() == null || txtSalario.getText().equals("")) {
			validar.addErros("salario", "O campo não pode ficar vazio");
		}
		vend.setSalario(Util.tryParseToDouble(txtSalario.getText()));    //convertendo os dados informados no TextField em texto

		vend.setDepartamento(cmbDepartamento.getValue());                //inserindo o departamento no vendedor
		
		if (validar.getErros().size() > 0) {   //verificando se essa validação for >0
			throw validar;
		}
		return vend;
	}

	@FXML
	public void onBtnCancelarAction(ActionEvent evento) {
		Util.palcoAtual(evento).close();                   // fechando a janela
	}

	public void inicializandoNodes() {
		Limitacao.setTextoCampoInteiro(txtId);             // caixa de texto só recebe inteiro
		Limitacao.setTextoCampoDouble(txtSalario);         // caixa de texto só recebe double
		Limitacao.setTextoCampoTamMaximo(txtNome, 50);     // caixa de texto só recebe String com 50 caracteres
		Limitacao.setTextoCampoTamMaximo(txtEmail, 30);    // caixa de texto só recebe String com 30 caracteres
		Util.formatoDatePicker(dpDataNasc, "dd/MM/yyyy");
		
		inicialandoCmbDepartamento();
	}

	public void assinaOuvintesAlterandoDados(OuvindoAlteracaoDados dados) {
		dadosAlterados.add(dados);
	}

	public void atualizaDadosFormulario() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está vazia!");
		}
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
		txtEmail.setText(entidade.getEmail());
		txtSalario.setText(String.format("%.2f", entidade.getSalario()));
		// pegando o fusoHorario da pessoa que estiver usando o sistema
		if (entidade.getDataNasc() != null) {
			dpDataNasc.setValue(LocalDate.ofInstant(entidade.getDataNasc().toInstant(), ZoneId.systemDefault()));
		}
		if (entidade.getDepartamento() == null) {
			cmbDepartamento.getSelectionModel().selectFirst();
		}
		else {
			cmbDepartamento.setValue(entidade.getDepartamento());           //o departamento cadastrado no BD vai pro ComboBox
		}
	}

	public void carregandoObjetosAssociados() {
		if (depServ == null) {
			throw new IllegalStateException("O Serviço de Departamento está vazio!");
		}
		List<Departamento> lista = depServ.procuraTudo(); // carrega os departamento do BD
		obsLista = FXCollections.observableArrayList(lista); // jogando os departamentos dentro da obsLista
		cmbDepartamento.setItems(obsLista); // setando a lista que está associada ao ComboBox
	}
	
	private void inicialandoCmbDepartamento() {
		Callback<ListView<Departamento>, ListCell<Departamento>> fabrica = lv -> new ListCell <Departamento>() {
			
			@Override
			protected void updateItem (Departamento item, boolean vazio) {
				super.updateItem(item, vazio);
				setText(vazio ? "" : item.getNome());
			}
		};
		cmbDepartamento.setCellFactory(fabrica);
		cmbDepartamento.setButtonCell(fabrica.call(null));
	}

}
