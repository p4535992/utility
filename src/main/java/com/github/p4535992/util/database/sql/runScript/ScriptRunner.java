package com.github.p4535992.util.database.sql.runScript;

import java.util.List;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Debopam Pal, Software Developer, National Informatics Center (NIC), India
 * @version  July 29, 2014
 *
 * http://www.codeproject.com/Articles/802383/Run-SQL-Script-sql-containing-DDL-DML-SELECT-state
 *
 * Warnings:
 *  1. Remember, ROLLBACK cannot be possible for DDL Statements (CREATE, UPDATE, DELETE).
 *  So, be careful during writing SQL Queries.
 *  Rules to Obey:
 *  1. You must end every SQL Statement with ; (Semicolon)
 *  2. You must end every statement within PL/SQL Block with + (Plus)
 *  3. You must end every PL/SQL Block with # (Hash)
 */
@SuppressWarnings("unused")
public class ScriptRunner {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ScriptRunner.class);

    public static final String DEFAULT_DELIMITER = ";";
    public static final String PL_SQL_BLOCK_SPLIT_DELIMITER = "+";
    public static final String PL_SQL_BLOCK_END_DELIMITER = "#";

    private final boolean autoCommit, stopOnError;
    private final Connection connection;
    //private String delimiter = ScriptRunner.DEFAULT_DELIMITER;
    //private final PrintWriter out, err;

    /* To Store any 'SELECT' queries output */
    private List<Table> tableList;

    /* To Store any SQL Queries output except 'SELECT' SQL */
    private List<String> sqlOutput;

    public ScriptRunner(final Connection connection, final boolean autoCommit, final boolean stopOnError) {
        if (connection == null) {
            throw new RuntimeException("ScriptRunner requires an SQL Connection");
        }

        this.connection = connection;
        this.autoCommit = autoCommit;
        this.stopOnError = stopOnError;
        //this.out = new PrintWriter(System.out);
        //this.err = new PrintWriter(System.err);

        tableList = new ArrayList<>();
        sqlOutput = new ArrayList<>();
    }

    public void runScript(final Reader reader) throws SQLException, IOException {
        final boolean originalAutoCommit = this.connection.getAutoCommit();
        try {
            if (originalAutoCommit != this.autoCommit) {
                this.connection.setAutoCommit(this.autoCommit);
            }
            this.runScript(this.connection, reader);
        } finally {
            this.connection.setAutoCommit(originalAutoCommit);
        }
    }

    private void runScript(final Connection conn, final Reader reader) throws SQLException, IOException {
        StringBuffer command = null;

        Table table = null;
        try {
            final LineNumberReader lineReader = new LineNumberReader(reader);
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (command == null) {
                    command = new StringBuffer();
                }

                if (table == null) {
                    table = new Table();
                }

                String trimmedLine = line.trim();

                // Interpret SQL Comment & Some statement that are not executable
                if(trimmedLine.startsWith("--")
                        || trimmedLine.startsWith("//")
                        || trimmedLine.startsWith("#")
                        || trimmedLine.toLowerCase().startsWith("rem inserting into")
                        || trimmedLine.toLowerCase().startsWith("set define off")) {

                    // do nothing...
                } else if (trimmedLine.endsWith(DEFAULT_DELIMITER) || trimmedLine.endsWith(PL_SQL_BLOCK_END_DELIMITER)) {
                    // Line is end of statement

                    // Append
                    if (trimmedLine.endsWith(DEFAULT_DELIMITER)) {
                        command.append(line.substring(0, line.lastIndexOf(DEFAULT_DELIMITER)));
                        command.append(" ");
                    } else if (trimmedLine.endsWith(PL_SQL_BLOCK_END_DELIMITER)) {
                        command.append(line.substring(0, line.lastIndexOf(PL_SQL_BLOCK_END_DELIMITER)));
                        command.append(" ");
                    }

                    Statement stmt = null;
                    ResultSet rs = null;
                    try {
                        stmt = conn.createStatement();
                        boolean hasResults = false;
                        if (this.stopOnError) {
                            hasResults = stmt.execute(command.toString());
                        } else {
                            try {
                                stmt.execute(command.toString());
                                logger.info("Execute SQL:"+command.toString());
                            } catch (final SQLException e) {
                                logger.error("Error executing SQL Command: \"" + command + "\"",e);
                            }
                        }

                        rs = stmt.getResultSet();
                        if (hasResults && rs != null) {

                            List<String> headerRow = new ArrayList<>();
                            List<List<String>> toupleList = new ArrayList<>();

                            // Print & Store result column names
                            final ResultSetMetaData md = rs.getMetaData();
                            final int cols = md.getColumnCount();
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < cols; i++) {
                                final String name = md.getColumnLabel(i + 1);
                                sb.append(name).append("\t");

                                headerRow.add(name);
                            }
                            table.setHeaderRow(headerRow);
                            logger.info(sb.toString());

                            sb = new StringBuilder();
                            sb.append(StringUtils.repeat("---------", md.getColumnCount()));
                            logger.info(sb.toString());
                            // Print & Store result rows
                            sb = new StringBuilder();
                            while (rs.next()) {
                                List<String> touple = new ArrayList<>();
                                for (int i = 1; i <= cols; i++) {
                                    final String value = rs.getString(i);
                                    sb.append(value).append("\t");

                                    touple.add(value);
                                }
                                toupleList.add(touple);
                            }
                            table.setToupleList(toupleList);
                            logger.info(sb.toString());
                            this.tableList.add(table);
                            table = null;
                        } else {
                            sqlOutput.add(stmt.getUpdateCount() + " row(s) affected.");
                            //logger.info(stmt.getUpdateCount() + " row(s) affected.");
                        }
                        command = null;
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (final Exception e) {
                                logger.error("Failed to close result: " + e.getMessage(),e);
                            }
                        }
                        if (stmt != null) {
                            try {
                                stmt.close();
                            } catch (final Exception e) {
                                logger.error("Failed to close statement: " + e.getMessage(),e);
                            }
                        }
                    }
                } else if (trimmedLine.endsWith(PL_SQL_BLOCK_SPLIT_DELIMITER)) {
                    command.append(line.substring(0, line.lastIndexOf(PL_SQL_BLOCK_SPLIT_DELIMITER)));
                    command.append(" ");
                } else { // Line is middle of a statement

                    // Append
                    command.append(line);
                    command.append(" ");
                }
            }
            if (!this.autoCommit) {
                conn.commit();
            }
        } catch (final SQLException e) {
            conn.rollback();
            logger.error("Error executing SQL Command: \"" + command + "\"",e);
        } catch (final IOException e) {
            logger.error("Error reading SQL Script.",e);
        }
    }

    /**
     * @return the tableList
     */
    public List<Table> getTableList() {
        return tableList;
    }

    /**
     * @param tableList the tableList to set
     */
    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }

    /**
     * @return the sqlOutput
     */
    public List<String> getSqlOutput() {
        return sqlOutput;
    }

    /**
     * @param sqlOutput the sqlOutput to set
     */
    public void setSqlOutput(List<String> sqlOutput) {
        this.sqlOutput = sqlOutput;
    }
}

