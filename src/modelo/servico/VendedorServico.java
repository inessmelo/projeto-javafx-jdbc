package modelo.servico;

import java.util.List;

import modelo.dao.FabricaDao;
import modelo.dao.VendedorDao;
import modelo.entidade.Vendedor;

public class VendedorServico {

	private VendedorDao vendDao = FabricaDao.criandoVendedorDao();   //chamada do Vendedor
	
	public List <Vendedor> procuraTudo() {
		return vendDao.procurarTudo();                     //vai ao BD e busca os vendedores
	}
	
	public void salvaOuAtualiza (Vendedor vend) {
		if (vend.getId() == null) {
			vendDao.inserir(vend);                          //inserindo um vendedor no BD caso não exista
		}
		else {
			vendDao.atualizar(vend);                        //atualizando um vendedor no BD caso exista
		}
	}
	
	public void remove (Vendedor vend) {
		vendDao.deletarId(vend.getId());                    //removendo um vendedor do BD
	}
	
}
