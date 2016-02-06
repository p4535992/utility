package com.github.p4535992.util.repositoryRDF.virtuoso;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import virtuoso.jdbc4.VirtuosoExtendedString;
import virtuoso.jdbc4.VirtuosoRdfBox;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


/**
 * Created by 4535992 on 08/01/2016.
 * href: https://gist.github.com/seralf/9599273 (TinkerPool)
 * href: https://github.com/openlink/virtuoso-opensource/blob/develop/6/binsrc/sesame2/virtuoso_driver/VirtuosoTest.java
 */
public class VirtuosoUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(VirtuosoUtilities.class);

    public static String VIRTUOSO_JDBC_DRIVER = "jdbc:virtuoso://";
    public static String VIRTUOSO_HOST = "localhost";
    public static int VIRTUOSO_PORT = 1111;
    public static String VIRTUOSO_USERNAME = "dba";
    public static String VIRTUOSO_PASSWORD = "dba";

    private static final String VIRTUOSO_DEFAULT_URL_ = "jdbc:virtuoso://localhost:1111/charset=UTF-8";
    /*private static final String VIRTUOSO_DEFAUL_USERNAME= "dba";
    private static final String VIRTUOSO_DEFAUL_PASSWORD= "dba";*/
    private static final String SPARQL_SELECT_ALL ="SELECT * WHERE { GRAPH ?graph { ?s ?p ?o } } limit 100";

    static int PASSED = 0;
    static int FAILED = 0;
    static int testCounter = 0;

    private static VirtuosoUtilities instance;

    public static VirtuosoUtilities getInstance() {
        if(instance == null){
           instance = new VirtuosoUtilities();
        }
        return instance;

    }

    private VirtuosoUtilities() {}

    /**
     * Method to connect to a Virtuoso serve with OpenRDF API.
     * @param host the String of the Host Server Virtuoso.
     * @param port the String of the Int of the Port of the Server Virtuoso.
     * @param username the String username.
     * @param password the String password.
     * @return the Repository OpenRDF.
     */
    public Repository connectToVirtuosoRepository(String host,String port,String username,String password){
        try {
            Repository repository = new virtuoso.sesame2.driver.VirtuosoRepository(
                    "jdbc:virtuoso://" + VIRTUOSO_HOST + ":" + VIRTUOSO_PORT, VIRTUOSO_USERNAME, VIRTUOSO_PASSWORD);
            RepositoryConnection conn = repository.getConnection();
            repository.initialize();
            logger.info("Connected to the repository at the url:" +
                    "jdbc:virtuoso://" + VIRTUOSO_HOST + ":" + VIRTUOSO_PORT);
            return repository;
        } catch (RepositoryException e) {
            logger.warn("Can't connected to the repository at the url:" +
                    "jdbc:virtuoso://" + VIRTUOSO_HOST + ":" + VIRTUOSO_PORT);
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    public Repository connectToVirtuosoRepository(){
        return connectToVirtuosoRepository(VIRTUOSO_HOST ,String.valueOf(VIRTUOSO_PORT), VIRTUOSO_USERNAME, VIRTUOSO_PASSWORD);
    }

    public Repository  connectToVirtuosoRepository(String... sa){
        if(sa.length > 4){
            logger.warn("You put to may arguments for the connection to a Virtuosos server.");
        }
        if(sa[0] != null)VIRTUOSO_HOST = sa[0];
        if(sa[1] != null)VIRTUOSO_PORT  = Integer.parseInt(sa[1]);
        if(sa[2] != null)VIRTUOSO_USERNAME = sa[2] ;
        if(sa[3] != null)VIRTUOSO_PASSWORD = sa[3];
        return connectToVirtuosoRepository(VIRTUOSO_HOST,String.valueOf(VIRTUOSO_PORT),VIRTUOSO_USERNAME,VIRTUOSO_PASSWORD);
    }

    public Connection getSQLConnectionToVirtuoso(){
        try {
            String url = "jdbc:virtuoso://localhost:1111";
            Class.forName("virtuoso.jdbc4.Driver");
            return DriverManager.getConnection(url, "dba", "123456");
        } catch (ClassNotFoundException | SQLException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    public static void test(String args[]) {
        try {
            String url;
            url = "jdbc:virtuoso://localhost:1111";
            Class.forName("virtuoso.jdbc4.Driver");
            Connection connection = DriverManager.getConnection(url, "dba", "123456");
            java.sql.Statement stmt = connection.createStatement();

            stmt.execute("clear graph <gr>");
            java.sql.ResultSet rs = stmt.getResultSet();
            while (rs.next());

            stmt.execute("insert into graph <gr> " + "{ <aa> <bb> \"cc\" . <xx> <yy> <zz> . " + "  <mm> <nn> \"Some long literal with language\"@en . " + "  <oo> <pp> \"12345\"^^<http://www.w3.org/2001/XMLSchema#int> }");
            rs = stmt.getResultSet();
            while (rs.next());

            // output:valmode "LONG" turns RDF box on output
            // boolean more = stmt.execute("define output:valmode \"LONG\" select * from <gr> where { ?x ?y ?z }");
            boolean more = stmt.execute("select * from <gr> where { ?x ?y ?z }");
            ResultSetMetaData data = stmt.getResultSet().getMetaData();
            for (int i = 1; i <= data.getColumnCount(); i++)
                System.out.println(data.getColumnLabel(i) + "\t" + data.getColumnTypeName(i));
            System.out.println("===");
            if (more) {
                rs = stmt.getResultSet();
                while (rs.next()) {
                    for (int i = 1; i <= data.getColumnCount(); i++) {
                        String s = stmt.getResultSet().getString(i);
                        Object o = stmt.getResultSet().getObject(i);
                        // Value casted =
                        System.out.print("Object type is " + o.getClass().getName() + " ");
                        System.out.print(data.getColumnLabel(i) + " = ");
                        if (o instanceof VirtuosoRdfBox) // Typed literal
                        {
                            VirtuosoRdfBox rb = (VirtuosoRdfBox) o;
                            System.out.println(rb.rb_box + " lang=" + rb.getLang() + " type=" + rb.getType() + " ro_id=" + rb.rb_ro_id);
                        }
                        else if (o instanceof VirtuosoExtendedString) // String representing an IRI
                        {
                            VirtuosoExtendedString vs = (VirtuosoExtendedString) o;
                            if (vs.iriType == VirtuosoExtendedString.IRI) System.out.println("<" + vs.str + ">");
                            else if (vs.iriType == VirtuosoExtendedString.BNODE) System.out.println("<" + vs.str + ">");
                            else // not reached atm, literals are String or RdfBox
                                System.out.println("\"" + vs.str + "\"");
                        }
                        else if (stmt.getResultSet().wasNull()) System.out.println("NULL\t");
                        else System.out.println(s + " (No extended type availible)\t");
                    }
                    System.out.println("---");
                }
                more = stmt.getMoreResults();
            }
            stmt.close();

            // Try making new typed literal
            // System.out.println("---");
            // VirtuosoRdfBox rb = new VirtuosoRdfBox (connection, "Some literal with many symbols over 20", null, "cz");
            // System.out.println (rb.rb_box + " lang=" + rb.getLang() + " type=" + rb.getType() + " ro_id=" + rb.rb_ro_id );

            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("eof");
        System.exit(0);
    }








}
