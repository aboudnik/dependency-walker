package org.boudnik.walker;

import java.sql.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Alexandre_Boudnik
 * @since 08/14/2018
 */
public class Walker {

    private Map<Table, List<Constraint>> byPrimary = new HashMap<>();
    private Map<Table, List<Constraint>> byForeign = new HashMap<>();
    private Map<Table, List<String>> columns = new HashMap<>();

    final static String PRINT(Table table, Object level) {
        System.out.println(new String(new char[(int) level]).replace("\0", " ") + table);
        return null;
    }

    static class Table {
        String db;
        String schema;
        String name;

        public Table(String db, String schema, String name) {
            this.db = db;
            this.schema = schema;
            this.name = name;
        }

        @Override
        public String toString() {
            return db + '.' + schema + '.' + name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Table table = (Table) o;
            return Objects.equals(db, table.db) &&
                    Objects.equals(schema, table.schema) &&
                    Objects.equals(this.name, table.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(db, schema, name);
        }
    }

    static class Constraint {
        Table primary;
        List<String> pk;
        Table foreign;
        List<String> fk;
        String name;

        public Constraint(Table primary, String pk, Table foreign, String fk, String name) {
            this.primary = primary;
            this.pk = new ArrayList<>(Arrays.asList(pk));
            this.foreign = foreign;
            this.fk = new ArrayList<>(Arrays.asList(fk));
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Constraint that = (Constraint) o;
            return Objects.equals(primary, that.primary) &&
                    Objects.equals(foreign, that.foreign) &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {

            return Objects.hash(primary, foreign, name);
        }

        @Override
        public String toString() {
            return primary.toString() + '.' + pk + " -* " + foreign.toString() + '.' + fk + '(' + name + ')';
        }
    }

    private final Connection connection;

    private Walker() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;database=test;integratedSecurity=true");
    }

    static Walker getInstance() throws Exception {
        return new Walker();
    }

    public void build(String table) throws SQLException {
    }

    public void build() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select object_id, OBJECT_SCHEMA_NAME(object_id), name from sys.tables")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    columns.put(new Table("test", resultSet.getString(2), resultSet.getString(3)), null);
                }
            }
        }

// todo: PK: PKTABLE_QUALIFIER, PKTABLE_OWNER, PKTABLE_NAME, PKCOLUMN_NAME
// todo: FK: FKTABLE_QUALIFIER, FKTABLE_OWNER, FKTABLE_NAME, FKCOLUMN_NAME
// todo: KEY_SEQ UPDATE_RULE DELETE_RULE FK_NAME PK_NAME DEFERRABILITY
        List<Constraint> constraints = new ArrayList<>();
        for (Table table : columns.keySet()) {
            try (CallableStatement statement = connection.prepareCall("exec sp_fkeys ?, ?, ?")) {
                statement.setString(1, table.name);
                statement.setString(2, table.schema);
                statement.setString(3, table.db);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        constraints.add(
                                new Constraint(
                                        new Table(
                                                resultSet.getString("PKTABLE_QUALIFIER"),
                                                resultSet.getString("PKTABLE_OWNER"),
                                                resultSet.getString("PKTABLE_NAME")),
                                        resultSet.getString("PKCOLUMN_NAME"),
                                        new Table(
                                                resultSet.getString("FKTABLE_QUALIFIER"),
                                                resultSet.getString("FKTABLE_OWNER"),
                                                resultSet.getString("FKTABLE_NAME")),
                                        resultSet.getString("FKCOLUMN_NAME"),
                                        resultSet.getString("FK_NAME")));
                    }
                }
            }
        }

        Map<Constraint, Constraint> uniques = new HashMap<>();
        Map<Constraint, Constraint> duplicates = new HashMap<>();
        for (Constraint item : constraints)
            if (uniques.containsKey(item))
                duplicates.put(item, item);
            else
                uniques.put(item, item);
        for (Map.Entry<Constraint, Constraint> entry : duplicates.entrySet()) {
            Constraint constraint = uniques.get(entry.getKey());
            constraint.pk.addAll(entry.getValue().pk);
            constraint.fk.addAll(entry.getValue().fk);
        }
        for (Constraint constraint : uniques.values()) {
            List<Constraint> pc = byPrimary.computeIfAbsent(constraint.primary, k -> new ArrayList<>());
            pc.add(constraint);
            List<Constraint> fc = byForeign.computeIfAbsent(constraint.foreign, k -> new ArrayList<>());
            fc.add(constraint);
        }
    }


    private String walk(Map<Table, List<Constraint>> tree, Table root, Function<Constraint, Table> direction, BiFunction<Table, Object, String> action, int level) {
        String apply = action.apply(root, level);
        List<Constraint> constraints = tree.get(root);
        if (constraints == null) {
            return apply;
        }
        for (Constraint constraint : constraints) {
            walk(tree, direction.apply(constraint), direction, action, level + 2);
        }
        return apply;
    }

    String walkDown(Table start, BiFunction<Table, Object, String> action) {
        return walk(byPrimary, start, constraint -> constraint.foreign, action, 0);
    }

    String walkUp(Table start, BiFunction<Table, Object, String> action) {
        return walk(byForeign, start, constraint -> constraint.primary, action, 0);
    }

    void forEach(Consumer<Table> consumer) {
        for (Map.Entry<Table, List<String>> entry : columns.entrySet()) {
            System.out.println("for " + entry.getKey() + ':');
            consumer.accept(entry.getKey());
            System.out.println();
        }
    }

    Consumer<Table> delete = table -> walkDown(table, (t, level) -> {
        String x = new String(new char[(int) level]).replace("\0", " ") + "delete from " + t;
        System.out.println(x);
        return x;
    });

    public void close() throws SQLException {
        connection.close();
    }
}
