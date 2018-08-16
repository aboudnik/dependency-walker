package org.boudnik.walker;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexandre_Boudnik
 * @since 08/14/2018
 */
public class Walker {
    private final Connection connection;
    public Map<String, List<String>> map;

    private Walker() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;database=test;integratedSecurity=true");
    }

    static Walker getInstance() throws Exception {
        return new Walker();
    }

    public void build(String table) throws SQLException {
    }

    public void build() throws SQLException {
        map = new LinkedHashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "select " +
                        "object_schema_name(referenced_object_id), object_name(referenced_object_id), " +
                        "object_schema_name(parent_object_id),  object_name(parent_object_id) " +
                        "from sys.foreign_keys")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String r = resultSet.getObject(1) + "." + resultSet.getObject(2);
                String p = resultSet.getObject(3) + "." + resultSet.getObject(4);
                List<String> d = map.computeIfAbsent(r, k -> new ArrayList<>());
                d.add(p);
            }
        }
    }

    String copy() {
        return null;
    }

    String delete() {
        return null;
    }

    String move() {
        return null;
    }

    public void close() throws SQLException {
        connection.close();
    }
}
