package modelo.excecao;

import java.util.HashMap;
import java.util.Map;

public class ValidacaoExcecao extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Map <String, String> erros = new HashMap<>();
	
	public ValidacaoExcecao (String msg) {
		super(msg);
	}
	
	public Map<String, String> getErros(){
		return erros;
	}
	
	public void addErros(String nomeCampo, String msgErro) {
		erros.put(nomeCampo, msgErro);
	}
}
