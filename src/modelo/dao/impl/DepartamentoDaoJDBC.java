package modelo.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbExcecao;
import db.DbExcecaoIntegridade;
import modelo.dao.DepartamentoDao;
import modelo.entidade.Departamento;

public class DepartamentoDaoJDBC implements DepartamentoDao {

	private Connection conn;
	public DepartamentoDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void atualizar(Departamento obj) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE department SET Name = ? WHERE Id = ?");
			ps.setString(1, obj.getNome());
			ps.setInt(2, obj.getId());
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
	public void inserir(Departamento obj) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO department (Name) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);   //cria o Id automatico
			ps.setString(1, obj.getNome());
			
			int linhaAfetada = ps.executeUpdate();
						
			if (linhaAfetada > 0) {
				ResultSet rs = ps.getGeneratedKeys();       //retorna um valor inteiro
				if (rs.next()){
					int id = rs.getInt(1);
					obj.setId(id);
				}		
				DB.fecharResultSet(rs);
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
	public void deletarId(Integer id) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbExcecaoIntegridade(e.getMessage());
		}
		finally {
			DB.fecharStatement(ps);
		}
	}
	
	private Departamento instanciarDepartamento (ResultSet rs) throws SQLException {
		Departamento dep = new Departamento();
		dep.setId(rs.getInt("Id"));
		dep.setNome(rs.getString("Name"));
		//System.out.println(dep.getNome());
		//System.out.println(dep.getId());
		return dep;
	}

	@Override
	public List<Departamento> procuraTudo() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM department ORDER BY Name");
			rs = ps.executeQuery();
			List <Departamento> lista = new ArrayList<>();
			
			while (rs.next()) {
				Departamento dep = instanciarDepartamento(rs);
				lista.add(dep);
			}
			//System.out.println("TAM LISTA: " + lista.size());
			return lista;
		}
		catch (SQLException e) {
			throw new DbExcecao(e.getMessage());
		}
		finally {
			DB.fecharStatement(ps);
		}
	}

	@Override
	public Departamento procuraId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM department WHERE Id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				Departamento dep = instanciarDepartamento(rs);
				return dep;
			}
			return null;
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
