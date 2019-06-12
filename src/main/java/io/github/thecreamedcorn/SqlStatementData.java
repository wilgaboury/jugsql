package io.github.thecreamedcorn;

import java.util.HashSet;
import java.util.List;

public class SqlStatementData implements Cloneable {
    private String sql;
    private HashSet<String> parameterSearch;
    private List<String> parameters;

    public SqlStatementData(String sql) {
        this.sql = sql;

        int colonLoc = sql.indexOf(':');
        while (colonLoc > 0) {
            int i = colonLoc + 1;
            while (i < sql.length()
                && (Character.isAlphabetic(sql.charAt(i))
                    || Character.isDigit(sql.charAt(i))
                    || sql.charAt(i) == '_')) {
                i++;
            }
            String name = sql.substring(i + 1, colonLoc);
            if (!name.equals("")) {
                if (parameterSearch.contains(name)) {
                    throw new AssertionError("two parameters have the same name in the same statement");
                }
                parameterSearch.add(name);
                parameters.add(name);
            }
            colonLoc = sql.indexOf(colonLoc + 1, ':');
        }
    }

    public String getSql() {
        return sql;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public boolean containsParameter(String paramName) {
        return parameterSearch.contains(paramName);
    }

    public SqlStatementData clone() {
        try {
            return (SqlStatementData) super.clone();
        } catch (CloneNotSupportedException e) {
            System.err.println("unable to clone SqlStatementData object");
            return null;
        }
    }
}
