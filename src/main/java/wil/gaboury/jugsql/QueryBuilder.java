package wil.gaboury.jugsql;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        ArrayList<T> result = new ArrayList<>();
        HashMap<String, Method> setMethods = new HashMap<>();

        for (Method m : clazz.getMethods()) {
            if (m.getParameterCount() == 1) {
                if (m.getName().length() > 3 && m.getName().substring(0, 3).equals("set")) {
                    StringBuilder sb = new StringBuilder(m.getName().substring(3, m.getName().length()));
                    for (int i = 0; i < sb.length(); i++) {
                        if (Character.isUpperCase(sb.charAt(i))) {
                            char c = sb.charAt(i);
                            sb.deleteCharAt(i);
                            sb.insert(i, Character.toLowerCase(c));
                            sb.insert(i, '_');
                            i++;
                        }
                    }
                    setMethods.put(sb.toString(), m);

                } else if (m.isAnnotationPresent(ColumnName.class)) {
                    ColumnName cn = m.getAnnotation(ColumnName.class);
                    setMethods.put(cn.name(), m);
                }
            }
        }

        List<Map.Entry<String, Method>> entries = setMethods.entrySet().stream().collect(Collectors.toList());

        for (int i = 0; i < entries.size(); i++) {
            String name = entries.get(i).getKey();
            Method m = entries.get(i).getValue();
            Class<?> type = m.getParameterTypes()[0];

            try {
                ResultSet.class.getDeclaredMethod("get" + type.getName());
            } catch (NoSuchMethodException e1) {
                entries.remove(i);
                i--;
            } catch (SecurityException e1) {
                System.err.println(e1.getMessage());
                System.exit(0);
            }
        }

        try (ResultSet resSet = execute()) {
            while (resSet.next()) {
                T obj = null;
                for (Map.Entry<String, Method> e : entries) {
                    try {
                        obj = clazz.getConstructor().newInstance();
                        Method m = e.getValue();
                        m.invoke(obj, ResultSet.class.getDeclaredMethod("get" + e.getValue().getName()).invoke(resSet));
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
                        throw new AssertionError("Class '" + clazz.getName() + "' is not POJO or has a constructor with arguments", e1);
                    }
                }
                result.add(obj);
            }
        }

        return result;
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
