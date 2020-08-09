package db;

public class DbExcecaoIntegridade extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public DbExcecaoIntegridade (String msg) {
		super(msg);
	}
}
