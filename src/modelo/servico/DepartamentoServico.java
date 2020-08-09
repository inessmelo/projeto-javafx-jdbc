package modelo.servico;

import java.util.List;

import modelo.dao.DepartamentoDao;
import modelo.dao.FabricaDao;
import modelo.entidade.Departamento;

public class DepartamentoServico {
	
	//injetando a dependencia ao BD
	private DepartamentoDao depDao = FabricaDao.criandoDepartamentoDao();  //chamada do DepartamentoDao

	public List<Departamento> procuraTudo(){
		return depDao.procuraTudo();        //vai ao BD e busca os departamentos
	}
	
	public void salvaOuAtualiza (Departamento dep) {
		if (dep.getId() == null) {
			depDao.inserir(dep);
		}
		else {
			depDao.atualizar(dep);
		}
	}
	
	public void remove(Departamento dep) {
		depDao.deletarId(dep.getId());      //remove um departamento do BD
	}
}
