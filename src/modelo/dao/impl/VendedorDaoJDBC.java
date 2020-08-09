package modelo.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbExcecao;
import modelo.dao.VendedorDao;
import modelo.entidade.Departamento;
import modelo.entidade.Vendedor;

public class VendedorDaoJDBC implements VendedorDao {

	private Connection conn;
	
	public VendedorDaoJDBC(Connection conn) {
		this.conn = conn;    //construtor com argumento
	}
	@Override
	public void inserir(Vendedor obj) {
		PreparedStatement ps = null;    //abrindo a conecção Statement
		
		try {
			ps = conn.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);   //retorna um novo Id na tabela
			ps.setString(1, obj.getNome());
			ps.setString(2, obj.getEmail());
			ps.setDate(3, new java.sql.Date(obj.getDataNasc().getTime()));
			ps.setDouble(4, obj.getSalario());
			ps.setInt(5, obj.getDepartamento().getId());
			
			int linhasAfetadas = ps.executeUpdate();     //executa os códigos do SQL para inserir
			if (linhasAfetadas > 0) {
				ResultSet rs = ps.getGeneratedKeys();    //retorna o resultado em inteiro
				if (rs.next()) {            //se o RS retorna true
					int id = rs.getInt(1);  //irá colocar o ID gerado na coluna 1
					obj.setId(id);          //irá inserir no obj
				}
				DB.fecharResultSet(rs);
			}
			else {                         //se linhasAlteradas = 0
				throw new DbExcecao("Erro inexperado! Nenhuma linha foi alterada!");
			}
		}
		catch (SQLException e) {
			throw new DbExcecao(e.getMessage());
		}
		finally {
			DB.fecharStatement(ps);
		}
	}

	@Override
	public void atualizar(Vendedor obj) {
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(
					"UPDATE seller "  
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " 
					+ "WHERE Id = ?");
			ps.setString(1, obj.getNome());
			ps.setString(2, obj.getEmail());
			ps.setDate(3, new java.sql.Date(obj.getDataNasc().getTime()));
			ps.setDouble(4, obj.getSalario());
			ps.setInt(5, obj.getDepartamento().getId());
			ps.setInt(6, obj.getId());
			ps.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbExcecao(e.getMessage());
		}
		finally {
			DB.fecharStatement(ps);
		}	
	}

	@Override
	public void deletarId(Integer id) {
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbExcecao(e.getMessage());
		}
		finally {
			DB.fecharStatement(ps);
		}
	}

	@Override
	public Vendedor produrarId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " 
					+ "FROM seller INNER JOIN department " 
					+ "ON seller.DepartmentId = department.Id " 
					+ "WHERE seller.Id = ?" );
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				Departamento dep = instanciarDepartamento(rs);
				Vendedor vend = instanciarVendedor(rs, dep);
				return vend;
			}
			return null;       //se não tiver nenhum resultado a tabela retorna nula
			
		}
		catch (SQLException e) {
			throw new DbExcecao(e.getMessage());
		}
		finally {  //fechando as conecções
			DB.fecharResultSet(rs);
			DB.fecharStatement(ps);
		}
	}
	
	private Vendedor instanciarVendedor (ResultSet rs, Departamento dep) throws SQLException {
		Vendedor vend = new Vendedor();
		vend.setId(rs.getInt("Id"));
		vend.setNome(rs.getString("Name"));
		vend.setEmail(rs.getString("Email"));
		vend.setDataNasc(new java.util.Date(rs.getTimestamp("BirthDate").getTime()));
		vend.setSalario(rs.getDouble("BaseSalary"));
		vend.setDepartamento(dep);
		return vend;
	}
	
	private Departamento instanciarDepartamento (ResultSet rs) throws SQLException {
		Departamento dep = new Departamento();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setNome(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Vendedor> procurarTudo() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "  
					+ "FROM seller INNER JOIN department " 
					+ "ON seller.DepartmentId = department.Id " 
					+ "ORDER BY Name" );
			rs = ps.executeQuery();
			
			List <Vendedor> lista = new ArrayList<>();                    //criando uma lista de vendedores
			Map <Integer, Departamento> mapa = new HashMap<>();           //controla a não repetição do departamento
			
			while (rs.next()) {
				Departamento dep = mapa.get(rs.getInt("DepartmentId"));   //verificando se existe o departamento
				
				if (dep == null) {
					dep = instanciarDepartamento(rs);                     //criando um novo departamento
					mapa.put(rs.getInt("DepartmentId"), dep);            //salvando esse departamento para não ser repetido
				}
				Vendedor vend = instanciarVendedor(rs, dep);              //cadastrando um novo vendedor
				lista.add(vend);                                          //adicionando esse vendedor na lista
			}
			return lista;
		}
		catch (SQLException e) {
			throw new DbExcecao(e.getMessage());
		}
		finally {
			DB.fecharResultSet(rs);
			DB.fecharStatement(ps);
		}
	}

	@Override
	public List<Vendedor> produrarDepartamento(Departamento departamento) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "  
					+ "FROM seller INNER JOIN department " 
					+ "ON seller.DepartmentId = department.Id "  
					+ "WHERE DepartmentId = ? "  
					+ "ORDER BY Name" );
			ps.setInt(1, departamento.getId());
			rs = ps.executeQuery();
			
			List <Vendedor> lista = new ArrayList<>();
			Map<Integer, Departamento> mapa = new HashMap<>();
			
			while (rs.next()) {
				Departamento dep = mapa.get(rs.getInt("DepartmentId"));  //verificando se o departamento existe
				
				if (dep == null) {                                       //se o DEP retornar nulo
					dep = instanciarDepartamento(rs);                    //irá cadastrar um novo departamento
					mapa.put(rs.getInt("DepartmenteId"), dep);           //e não permitirá que duplique
				}
				Vendedor vend = instanciarVendedor(rs, dep);             //cadastrando o vendedor desse departamento
				lista.add(vend);                                         //incluindo o vendedor nessa lista
			}
			return lista;
		}
		catch (SQLException e) {
			throw new DbExcecao(e.getMessage());
		}
		finally {
			DB.fecharResultSet(rs);
			DB.fecharStatement(ps);
		}
	}

}
