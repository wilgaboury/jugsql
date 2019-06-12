package io.github.thecreamedcorn;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    public static <T> List<T> mapResultSetToPojo(Class<T> clazz, ResultSet resSet) throws SQLException {
        ArrayList<T> result = new ArrayList<>();
        HashMap<String, Method> setMethods = new HashMap<>();

        for (Method m : clazz.getMethods()) {
            if (m.getParameterCount() == 1) {
                if (m.isAnnotationPresent(ColumnName.class)) {
                    ColumnName cn = m.getAnnotation(ColumnName.class);
                    setMethods.put(cn.name(), m);
                } else if (m.getName().length() > 3 && m.getName().substring(0, 3).equals("set")) {
                    //converts camel case name into lower case snake case name
                    StringBuilder sb = new StringBuilder(m.getName().substring(3).toLowerCase());
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

        return result;
    }
}
