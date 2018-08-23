package org.boudnik.walker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Alexandre_Boudnik
 * @since 08/14/2018
 */
public class Walker {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    private Map<Table, List<Constraint>> byPrimary = new HashMap<>();
    private Map<Table, List<Constraint>> byForeign = new HashMap<>();
    private Map<Table, List<String>> columns = new HashMap<>();
    private String condition;
    private final String database;

    static class Table {
        String db;
        String schema;
        String name;

        public Table(String db, String schema, String name) {
            this.db = db/*.toLowerCase()*/;
            this.schema = schema/*.toLowerCase()*/;
            this.name = name/*.toLowerCase()*/;
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
            this.pk = new ArrayList<>(Collections.singletonList(pk));
            this.foreign = foreign;
            this.fk = new ArrayList<>(Collections.singletonList(fk));
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
            return '{' + name.toLowerCase() + ", " + primary.toString().toLowerCase() + '.' + pk.toString().toLowerCase() + " -* " + foreign.toString().toLowerCase() + '.' + fk.toString().toLowerCase() + '}';
        }
    }

    private final Connection connection;

    public Walker(final String address, final int port, String database) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlserver://" + address + ":" + port + ";database=" + (this.database = database), "larissa", "7p;'UsG/#Glg!Ms");
    }

    public void build(String table) throws SQLException {
    }

    public void build() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select object_id, OBJECT_SCHEMA_NAME(object_id), name from sys.tables")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    columns.put(new Table(database, resultSet.getString(2), resultSet.getString(3)), null);
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


    private void walk(Map<Table, List<Constraint>> tree, Table root, Function<Constraint, Table> direction, BiConsumer<Table, List<Constraint>> action, List<Constraint> path) {
        List<Constraint> constraints = tree.get(root);
        if (constraints != null) {
            for (Constraint constraint : constraints) {
                List<Constraint> p = new ArrayList<>(path);
                p.add(0, constraint);
                walk(tree, direction.apply(constraint), direction, action, p);
            }
        }
        action.accept(root, path);
    }


    void walkDown(Table start, BiConsumer<Table, List<Constraint>> action) {
        walk(byPrimary, start, constraint -> constraint.foreign, action, new ArrayList<>());
    }

    void walkUp(Table start, BiConsumer<Table, List<Constraint>> action) {
        walk(byForeign, start, constraint -> constraint.primary, action, new ArrayList<>());
    }

    void delete(Table start, String condition) {
        this.condition = condition;
        walk(byPrimary, start, constraint -> constraint.foreign, this::delete, new ArrayList<>());
    }

    static final BiConsumer<Table, List<Constraint>> PRINT = (table, constraints) -> System.out.println(new String(new char[constraints.size()]).replace("\0", " ") + table + " " + constraints);


    void delete(Table table, List<Constraint> constraints) {
        System.out.println("-- level " + constraints.size() + " " + table + " constraints " + constraints);
        StringBuilder sb = new StringBuilder("delete from " + table + "\n");
        if (!constraints.isEmpty())
            sb.append("from ").append(table).append("\n");
        for (Constraint constraint : constraints) {
            sb.append("inner join ").append(constraint.primary).append(" on ").append(composeOn(constraint)).append("\n");
        }
        sb.append("where " + condition);
        System.out.println(sb.toString().toLowerCase());
    }

    private static String composeOn(Constraint constraint) {
        StringBuilder sb = new StringBuilder();
        String and = "";
        List<String> pk = constraint.pk;
        for (int i = 0; i < pk.size(); i++) {
            sb.append(and).append(constraint.foreign).append(".").append(constraint.fk.get(i)).append("=").append(constraint.primary).append(".").append(constraint.pk.get(i));
            and = " and ";
        }
        return sb.toString();
    }

    public void close() throws SQLException {
        connection.close();
    }

    public static void main(String[] args) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }
        if (args.length == 5) {
            Walker walker = new Walker(args[0], Integer.parseInt(args[1]), args[2]);
            System.out.println("Walker.main OK!");
        }
    }
}
