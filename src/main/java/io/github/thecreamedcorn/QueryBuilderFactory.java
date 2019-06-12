package io.github.thecreamedcorn;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;

public class QueryBuilderFactory {
	private Connection conn;
	private HashMap<String, String> sqlCalls = new HashMap<>();

	public QueryBuilderFactory(Connection conn) {
		this.conn = conn;
	}

	private Connection getConnection() {
		return conn;
	}

	private void setConnection(Connection conn) {
		this.conn = conn;
	}

	public void loadModule(InputStream in) {
		ModuleScanner ms = new ModuleScanner(in);
		while (ms.hasNext()) {
			String name, statement;
			if ((name = ms.getName()) != null && (statement = ms.getStatement()) != null) {
				sqlCalls.put(name, statement);
			}
		}
	}
	
	public QueryBuilder create(String queryName) {
		if (!sqlCalls.containsKey(queryName)) {
			throw new AssertionError("could not find query by that name");
		}
		return new QueryBuilder(conn, sqlCalls.get(queryName));
	}
}
