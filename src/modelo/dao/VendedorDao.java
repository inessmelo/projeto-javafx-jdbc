package modelo.dao;

import java.util.List;

import modelo.entidade.Departamento;
import modelo.entidade.Vendedor;

public interface VendedorDao {

	void inserir (Vendedor obj);
	void atualizar (Vendedor obj);
	void deletarId (Integer id);
	Vendedor produrarId (Integer id);
	List <Vendedor> procurarTudo();
	List <Vendedor> produrarDepartamento (Departamento departamento);

}
