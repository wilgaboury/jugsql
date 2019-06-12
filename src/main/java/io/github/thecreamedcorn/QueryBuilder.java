package io.github.thecreamedcorn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class QueryBuilder {
    private Connection conn;
    private String sql;
    private SqlStatementData statementData;
    private HashMap<String, PreparedStatementAdd> nameValueMap = new HashMap<>();


    public QueryBuilder(Connection conn, String sql) {
        this.conn = conn;
        this.sql = sql;
        statementData = new SqlStatementData(sql);
    }

    public ResultSet execute() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < statementData.getParameters().size(); i++) {
                PreparedStatementAdd psa = nameValueMap.get(statementData.getParameters().get(i));
                if (psa == null) {
                    throw new AssertionError("parameter '" + statementData.getParameters().get(i) + "' was not set");
                }
                psa.add(ps, i);
            }
            return ps.executeQuery();
        }
    }

    public <T> List<T> executeAndMap(Class<T> clazz) throws SQLException {
        return Utils.mapResultSetToPojo(clazz, execute());
    }

    public Connection getConnection() {
        return conn;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public QueryBuilder setVariable(String name, String value) {
        sql = sql.replaceAll("\\{\\w*" + name + "\\w*\\}", value);
        return this;
    }

    public QueryBuilder setBytes(String name, byte[] value) {
        nameValueMap.put(name, (ps, i) -> ps.setBytes(i, value));
        return this;
    }
    public QueryBuilder setInt(String name, int value) {
        nameValueMap.put(name, (ps, i) -> ps.setInt(i, value));
        return this;
    }
    public QueryBuilder setLong(String name, long value) {
        nameValueMap.put(name, (ps, i) -> ps.setLong(i, value));
        return this;
    }
    public QueryBuilder setString(String name, String value) {
        nameValueMap.put(name, (ps, i) -> ps.setString(i, value));
        return this;
    }
}
