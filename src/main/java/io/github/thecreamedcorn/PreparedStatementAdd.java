package io.github.thecreamedcorn;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementAdd {
    void add(PreparedStatement ps, int index) throws SQLException;
}