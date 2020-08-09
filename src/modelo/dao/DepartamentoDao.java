package modelo.dao;

import java.util.List;

import modelo.entidade.Departamento;

public interface DepartamentoDao {

	void atualizar (Departamento obj);
	void inserir (Departamento obj);
	void deletarId (Integer id);
	List <Departamento> procuraTudo();
	Departamento procuraId (Integer id);
	
}
