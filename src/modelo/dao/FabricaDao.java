package modelo.dao;

import db.DB;
import modelo.dao.impl.DepartamentoDaoJDBC;
import modelo.dao.impl.VendedorDaoJDBC;

public class FabricaDao {

	public static VendedorDao criandoVendedorDao() {
		return new VendedorDaoJDBC(DB.getConeccao());
	}
	
	public static DepartamentoDao criandoDepartamentoDao() {
		return new DepartamentoDaoJDBC(DB.getConeccao());
	}
}
