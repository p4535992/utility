package p4535992.util.sql;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 4535992 on 14/05/2015.
 */
public class SQLHelper {

    private static Connection conn;

    public static Map<Integer, String> getAllJdbcTypeNames() throws IllegalArgumentException, IllegalAccessException {
        Map<Integer, String> result = new HashMap<>();
        for (Field field : Types.class.getFields()) {
            result.put((Integer)field.get(null), field.getName());
        }
        return result;
    }

    /**
     * @href: http://www.java2s.com/Code/Java/Database-SQL-JDBC/convertingajavasqlTypesintegervalueintoaprintablename.htm
     * @param jdbcType
     */
    public static void getJdbcTypeName(int jdbcType) {
        Map map = new HashMap();
        // Get all field in java.sql.Types
        Field[] fields = java.sql.Types.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                String name = fields[i].getName();
                Integer value = (Integer) fields[i].get(null);
                map.put(value, name);
            } catch (IllegalAccessException e) {
            }
        }
        System.out.println(map);
    }

    public static Class convertSQLTypes2JavaClass(int type) {
        Class<?> result = Object.class;
        switch (type) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                result = String.class;
                break;
            case Types.NUMERIC:
            case Types.DECIMAL:
                result = java.math.BigDecimal.class;
                break;
            case Types.BIT:
                result = Boolean.class;
                break;
            case Types.TINYINT:
                result = Byte.class;
                break;
            case Types.SMALLINT:
                result = Short.class;
                break;
            case Types.INTEGER:
                result = Integer.class;
                break;
            case Types.BIGINT:
                result = Long.class;
                break;
            case Types.REAL:
            case Types.FLOAT:
                result = Float.class;
                break;
            case Types.DOUBLE:
                result = Double.class;
                break;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                result = Byte[].class;
                break;
            case Types.DATE:
                result = java.sql.Date.class;
                break;
            case Types.TIME:
                result = java.sql.Time.class;
                break;

            case Types.TIMESTAMP:
                result = java.sql.Timestamp.class;
                break;
            case Types.NULL:
                result = new Object().getClass().getSuperclass();
        }
        return result;
    }

    public static int convertClass2SQLTypes(Class<?> aClass) {
        int result;
        if(aClass.getName().equals(String.class.getName()))result = Types.VARCHAR;
        else if(aClass.getName().equals(java.math.BigDecimal.class.getName()))result = Types.NUMERIC;
        else if(aClass.getName().equals(Boolean.class.getName()))result = Types.BIT;
        else if(aClass.getName().equals(Byte.class.getName()))result = Types.TINYINT;
        else if(aClass.getName().equals(Short.class.getName()))result = Types.SMALLINT;
        else if(aClass.getName().equals(Integer.class.getName()))result = Types.INTEGER;
        else if(aClass.getName().equals(Long.class.getName())) result = Types.BIGINT;
        else if(aClass.getName().equals(Float.class.getName()))result = Types.FLOAT;
        else if(aClass.getName().equals(Double.class.getName()))result = Types.DOUBLE;
        else if(aClass.getName().equals(Byte[].class.getName()))result = Types.VARBINARY;
        else if(aClass.getName().equals(java.sql.Date.class.getName())) result = Types.DATE;
        else if(aClass.getName().equals(java.sql.Time.class.getName()))result = Types.TIME;
        else if(aClass.getName().equals(java.sql.Timestamp.class.getName()))result = Types.TIMESTAMP;
        else if(aClass.getName().equals(java.net.URL.class.getName()))result = Types.VARCHAR;
        else result = Types.NULL;
        return result;
    }




    public static Connection getHSQLConnection(String database,String username,String password) throws Exception {
        Class.forName("org.hsqldb.jdbcDriver");
        String url = "jdbc:hsqldb:data/"+database;
        return  conn = DriverManager.getConnection(url, username, password);
    }

    public static Connection getMySqlConnection(String database,String username,String password) throws Exception {
        Class.forName("org.gjt.mm.mysql.Driver");
        String url = "jdbc:mysql://localhost/"+database;
        return conn = DriverManager.getConnection(url, username, password);
    }

    public static Connection getOracleConnection(String database,String username,String password) throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        String url = "jdbc:oracle:thin:@localhost:1521:"+database;// load Oracle driver
        return conn = DriverManager.getConnection(url, username, password);
    }
}
