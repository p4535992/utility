package com.github.p4535992.util.database.sql;
import java.sql.*;
import java.util.*;

/**
 * Converts database types to Java class types.
 * @author 4535992.
 * @version 2015-07-07.
 */
@SuppressWarnings("unused")
public class  SQLKit {
    private static Connection connection;
    private static Statement stmt;
    private static String query;

    @SuppressWarnings("unchecked")
    public SQLKit() {}

    public static Map<String,Integer> getColumns(String database,String table,String column) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet result = metaData.getColumns( null, database, table, column );
        Map<String,Integer> map = new HashMap<>();
        while(result.next()){
            String columnName = result.getString(4);
            Integer columnType = result.getInt(5);
            map.put(columnName,columnType);
        }
        return map;
    }

    public static void openConnection(String url,String user,String pass) throws SQLException, ClassNotFoundException{
        //Class.forName("org.h2.Driver"); //Loading driver connection
        connection = DriverManager.getConnection(url, user, pass);
    }

    public static void closeConnection() throws SQLException{ connection.close();}

    public static Connection setConnection(String classDriverName,String dialectDB,
                  String host,String port,String database,String user,String pass) throws ClassNotFoundException, SQLException {
       //"org.hsqldb.jdbcDriver","jdbc:hsqldb:data/tutorial"
        Class.forName(classDriverName); //load driver//"com.sql.jdbc.Driver"
        String url = ("" + dialectDB + "://" + host + ":" + port + "/" + database); //"jdbc:sql://localhost:3306/jdbctest"
        Connection conn = DriverManager.getConnection(url, user, pass);
        System.out.println("Got Connection.");
        connection = conn;
        return conn;
    }

    public static Connection getConnection(){
        return connection;
    }

    public static void executeSQLCommand(String sql) throws Exception {
        stmt.executeUpdate(sql);
    }

    public static void checkData(String sql) throws Exception {
        java.sql.ResultSet rs = stmt.executeQuery(sql);
        java.sql.ResultSetMetaData metadata = rs.getMetaData();

        for (int i = 0; i < metadata.getColumnCount(); i++) {
            System.out.print("\t"+ metadata.getColumnLabel(i + 1));
        }
        System.out.println("\n----------------------------------");

        while (rs.next()) {
            for (int i = 0; i < metadata.getColumnCount(); i++) {
                Object value = rs.getObject(i + 1);
                if (value == null) {
                    System.out.print("\t       ");
                } else {
                    System.out.print("\t"+value.toString().trim());
                }
            }
            System.out.println("");
        }
    }
}

