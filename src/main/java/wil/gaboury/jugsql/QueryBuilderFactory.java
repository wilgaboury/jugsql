package wil.gaboury.jugsql;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

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
		//TODO make checks for end of file and make a looping construct
		ModuleScanner ms = new ModuleScanner(in);
		sqlCalls.put(ms.getName(), ms.getStatement());
	}
	
	public QueryBuilder create(String queryName) {
		//TODO add check for query name that is not in the hashmap
		return new QueryBuilder(conn, sqlCalls.get(queryName));
	}
}
