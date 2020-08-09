package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class DB {

	private static Connection conn = null;
	
	public static Connection getConeccao() {                   //abrindo conecção
		if (conn == null) {
			try {
				Properties prop = carregaProperties();          //pegando as propriedades do método abaixo
				String url = prop.getProperty("dburl");         //recebendo o caminho do BD
				conn = DriverManager.getConnection(url, prop);  //conectando com o BD
			}
			catch (SQLException e) {
				throw new DbExcecao(e.getMessage());
			}
		}
		return conn;
	}
	
	public static void fecharConeccao() {   
		if (conn != null) {
			try {
				conn.close();   //fechando a conecção
			}
			catch (SQLException e) {
				throw new DbExcecao(e.getMessage());
			}
		}
	}
	
	public static Properties carregaProperties() {      //carregando os dados do arquivo db.properties
		try (FileInputStream fs = new FileInputStream("db.properties")){
			Properties prop = new Properties();
			prop.load(fs);
			return prop;
		}
		catch (IOException e) {
			throw new DbExcecao(e.getMessage());
		}
	}
	
	public static void fecharStatement (Statement st) {
		if (st != null) {
			try {
				st.close();   //fechando a conecção Statement
			}
			catch (SQLException e) {
				throw new DbExcecao(e.getMessage());
			}
		}
	}
	
	public static void fecharResultSet (ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();   //fechando a conecção ResultSet
			}
			catch (SQLException e) {
				throw new DbExcecao(e.getMessage());
			}
		}
	}
}
