package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbExcecao;
import gui.ouvindo.OuvindoAlteracaoDados;
import gui.util.Alerta;
import gui.util.Limitacao;
import gui.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.entidade.Departamento;
import modelo.excecao.ValidacaoExcecao;
import modelo.servico.DepartamentoServico;

public class DepartamentoFormaControle implements Initializable {

	private Departamento entidade;
	private DepartamentoServico servico;
	private List <OuvindoAlteracaoDados> dadosAlterados = new ArrayList<>();
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNome;
	@FXML
	private Label lblMsgErro;
	@FXML
	private Button btnSalvar;
	@FXML
	private Button btnCancelar;
	
	public void setDepartamento (Departamento entidade) {
		this.entidade = entidade;
	}
	
	public void setDepartamentoServico (DepartamentoServico servico) {
		this.servico = servico;
	}
	
	@FXML
	public void onTxtIdAction () {
		
	}
	
	@FXML
	public void onTxtNomeAction () {
		
	}
	
	@FXML
	public void onLblMsgErroAction () {
		
	}
	
	@FXML
	public void onBtnSalvarAction (ActionEvent evento) {
		if (entidade == null) {                        //supondo que o programador esquece de injetar a entidade
			throw new IllegalStateException("Entidade está vazia.");  //informa o usuario que a caixa esta vazia
		}
		if (servico == null) {                         //supondo que o programador esquece de injetar o serviço
			throw new IllegalStateException("Serviço está vazio.");   //informa o usuario que a caixa esta vazia
		}
		try {
			entidade = obterDadosFormulario();             //pegando os dados do meodo abaixo
			servico.salvaOuAtualiza(entidade);             //salvando no BD
			notificandoDadosAlterados();
			Util.palcoAtual(evento).close();               //fechando a janela
		}
		catch (ValidacaoExcecao e) {
			definirMsgErro(e.getErros());
		}
		catch (DbExcecao e) {
			Alerta.showAlert("Erro salvando os dados", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notificandoDadosAlterados() {            //executa o metodo onDadosAlterados na inteface OuvindoDadosAlterados
		for (OuvindoAlteracaoDados lista: dadosAlterados) {  //pra cada objeto alterado na lista
			lista.onDadosAlterados();                     //irá notificar
		}
	}

	private Departamento obterDadosFormulario() {
		Departamento dep = new Departamento();            //instancia um novo departamento
		ValidacaoExcecao validar = new ValidacaoExcecao("Erro de validação!");
		dep.setId(Util.tryParseToInt(txtId.getText()));   //pega o numero da caixa de texto e converte para inteiro
			
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) { //se a caixa de texto estiver vazio ou tiver espaço em branco no inicio e fim
			validar.addErros("nome", "O campo não pode ser vazio!");
		}
		
		dep.setNome(txtNome.getText());                   //pega o texto digitado na caixa de texto
		
		if (validar.getErros().size() > 0) {              //se o tamanho da lista de validação de erro for maios que 0
			throw validar;
		}
		return dep;
	}
	
	@FXML
	public void onBtnCancelarAction (ActionEvent evento) {
		Util.palcoAtual(evento).close();                  //fechando a janela
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializacaoNodes();                           //recebendo o método de limitações
	}

	public void inicializacaoNodes() {
		Limitacao.setTextoCampoInteiro(txtId);          //caixa de texto do ID só vai receber numero inteiro
		Limitacao.setTextoCampoTamMaximo(txtNome, 50);  //caixa de texto do Nome só pode receber até 50 caracteres
	}
	
	public void assinaOuvintesAlterandoDados(OuvindoAlteracaoDados dados) {
		dadosAlterados.add(dados);
	}
	
	public void atualizaDadosFormulario () {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está vazia.");
		}
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
	}
	
	public void definirMsgErro(Map<String, String> erros) {
		Set <String> arquivos = erros.keySet();
		if (arquivos.contains("nome")) {
			lblMsgErro.setText(erros.get("nome"));
		}
	}
}
