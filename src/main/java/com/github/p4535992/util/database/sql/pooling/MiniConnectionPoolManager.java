package com.github.p4535992.util.database.sql.pooling;

import java.sql.Statement;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.Stack;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;

/**
 * A simple standalone JDBC connection pool manager.
 * <p>
 * The public methods of this class are thread-safe.
 * <p>
 * Author: Christian d'Heureuse (<a href="http://www.source-code.biz">www.source-code.biz</a>)<br>
 * License: <a href="http://www.gnu.org/licenses/lgpl.html">LGPL</a>
 * <p>
 * 2007-06-21: Constructor with a timeout parameter added.
 */
@SuppressWarnings("unused")
public class MiniConnectionPoolManager {

    private ConnectionPoolDataSource  dataSource;

    private static PrintWriter  logWriter;
    private Semaphore  semaphore;
    private Stack<PooledConnection> recycledConnections;

    private PoolConnectionEventListener    poolConnectionEventListener;

    private static boolean isDisposed;
    private static int itimeout = 60;                   // milliseconds before launcht eh timeout exception
    private static int activeConnections = 1;          //number of active connections
    private static int imaxConnections   = 8;           // number of connections
    private static final int inoOfThreads      = 50;    // number of worker threads
    private static final int iProcessingTime = 30;    // total processing time of the test program in seconds
    private static final int iThreadPauseTime1 = 100;   // max. thread pause time in microseconds, without a connection
    private static final int iThreadPauseTime2 = 100;   // max. thread pause time in microseconds, with a connection

    //private static MiniConnectionPoolManager poolMgr;
    private static WorkerThread[] threads;
    private static boolean shutdownFlag;
    private static final Object  shutdownObj = new Object();
    private static Random random = new Random();

    /**
     * Thrown in {@link #getConnection()} when no free connection becomes available within <code>timeout</code> seconds.
     */
    public static class TimeoutException extends RuntimeException {
        private static final long serialVersionUID = 1;
        public TimeoutException () {
            super ("Timeout while waiting for a free database connection."); }}

    /**
     * Constructs a MiniConnectionPoolManager object with a timeout of 60 seconds.
     * @param dataSource      the data source for the connections.
     * @param maxConnections  the maximum number of connections.
     */
    public MiniConnectionPoolManager (ConnectionPoolDataSource dataSource, int maxConnections) {
        this (dataSource, maxConnections, itimeout); }

    /**
     * Constructs a MiniConnectionPoolManager object.
     * @param dataSource      the data source for the connections.
     * @param maxConnections  the maximum number of connections.
     * @param timeout         the maximum time in seconds to wait for a free connection.
     */
    public MiniConnectionPoolManager (ConnectionPoolDataSource dataSource, int maxConnections, int timeout) {
        this.dataSource = dataSource;
        imaxConnections = maxConnections;
        itimeout = timeout;
        try {
            logWriter = dataSource.getLogWriter(); }
        catch (SQLException ignored) {}
        if (maxConnections < 1) throw new IllegalArgumentException("Invalid maxConnections value.");
        semaphore = new Semaphore(maxConnections,true);
        recycledConnections = new Stack<>();
        poolConnectionEventListener = new PoolConnectionEventListener(); }

    /**
     * Closes all unused pooled connections.
     * @throws java.sql.SQLException if any SQl error occurred.
     */
    public synchronized void dispose() throws SQLException {
        if (isDisposed) return;
        isDisposed = true;
        SQLException e = null;
        while (!recycledConnections.isEmpty()) {
            PooledConnection pconn = recycledConnections.pop();
            try {
                pconn.close(); }
            catch (SQLException e2) {
                if (e == null) e = e2; }}
        if (e != null) throw e; }

    /**
     * Retrieves a connection from the connection pool.
     * If <code>maxConnections</code> connections are already in use, the method
     * waits until a connection becomes available or <code>timeout</code> seconds elapsed.
     * When the application is finished using the connection, it must close it
     * in order to return it to the pool.
     * @return a new Connection object.
     * @throws java.sql.SQLException if any error SQl is occurred.
     * @throws TimeoutException when no connection becomes available within <code>timeout</code> seconds.
     */
    public Connection getConnection() throws SQLException {
        // This routine is unsynchronized, because semaphore.tryAcquire() may block.
        synchronized (this) {
            if (isDisposed) throw new IllegalStateException("Connection pool has been disposed."); }
        try {
            if (!semaphore.tryAcquire(itimeout,TimeUnit.SECONDS))
                throw new TimeoutException(); }
        catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while waiting for a database connection.",e); }
        boolean ok = false;
        try {
            Connection conn = getConnection2();
            ok = true;
            return conn; }
        finally {
            if (!ok) semaphore.release(); }}

    private synchronized Connection getConnection2() throws SQLException {
        if (isDisposed) throw new IllegalStateException("Connection pool has been disposed.");   // test again with lock
        PooledConnection pconn;
        if (!recycledConnections.empty()) {
            pconn = recycledConnections.pop(); }
        else {
            pconn = dataSource.getPooledConnection(); }
        Connection conn = pconn.getConnection();
        activeConnections++;
        pconn.addConnectionEventListener (poolConnectionEventListener);
        assertInnerState();
        return conn; }

    private synchronized void recycleConnection (PooledConnection pconn) {
        if (isDisposed) { disposeConnection (pconn); return; }
        if (activeConnections <= 0) throw new AssertionError();
        activeConnections--;
        semaphore.release();
        recycledConnections.push (pconn);
        assertInnerState(); }

    private synchronized void disposeConnection (PooledConnection pconn) {
        if (activeConnections <= 0) throw new AssertionError();
        activeConnections--;
        semaphore.release();
        closeConnectionNoEx (pconn);
        assertInnerState(); }

    private void closeConnectionNoEx (PooledConnection pconn) {
        try {
            pconn.close(); }
        catch (SQLException e) {
            log ("Error while closing database connection: "+e.toString()); }}

    private void log (String msg) {
        String s = "MiniConnectionPoolManager: "+msg;
        try {
            if (logWriter == null)
                System.err.println (s);
            else
                logWriter.println (s); }
        catch (Exception ignored) {}}

    private void assertInnerState() {
        if (activeConnections < 0) throw new AssertionError();
        if (activeConnections+recycledConnections.size() > imaxConnections) throw new AssertionError();
        if (activeConnections+semaphore.availablePermits() > imaxConnections) throw new AssertionError(); }

    private class PoolConnectionEventListener implements ConnectionEventListener {
        @Override
        public void connectionClosed (ConnectionEvent event) {
            PooledConnection pconn = (PooledConnection)event.getSource();
            pconn.removeConnectionEventListener (this);
            recycleConnection (pconn); }
        @Override
        public void connectionErrorOccurred (ConnectionEvent event) {
            PooledConnection pconn = (PooledConnection)event.getSource();
            pconn.removeConnectionEventListener (this);
            disposeConnection (pconn); }}

    /**
     * Returns the number of active (open) connections of this pool.
     * This is the number of <code>Connection</code> objects that have been
     * issued by {@link #getConnection()} for which <code>Connection.close()</code>
     * has not yet been called.
     * @return the number of active connections.
     **/
    public synchronized int getActiveConnections() {
        return activeConnections; }





    public class WorkerThread extends Thread {
        public int threadNo;
        @Override
        public void run() {threadMain (threadNo); }
    }

    public ConnectionPoolDataSource createDataSource() throws Exception {

        // Version for H2:
        //org.h2.jdbcx.JdbcDataSource dataSource = new org.h2.jdbcx.JdbcDataSource();
        //dataSource.setURL ("jdbc:h2:file:c:/temp/temp_TestMiniConnectionPoolManagerDB;DB_CLOSE_DELAY=-1");

        // Version for Apache Derby:
       /*
          org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource dataSource = new org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource();
          dataSource.setDatabaseName ("c:/temp/temp_TestMiniConnectionPoolManagerDB");
          dataSource.setCreateDatabase ("create");
          dataSource.setLogWriter (new PrintWriter(System.out));
       */

        // Versioo for JTDS:
       /*
          net.sourceforge.jtds.jdbcx.JtdsDataSource dataSource = new net.sourceforge.jtds.jdbcx.JtdsDataSource();
          dataSource.setAppName ("TestMiniConnectionPoolManager");
          dataSource.setDatabaseName ("Northwind");
          dataSource.setServerName ("localhost");
          dataSource.setUser ("sa");
          dataSource.setPassword (System.getProperty("saPassword"));
       */

        // Version for the Microsoft SQL Server driver (sqljdbc.jar):
       /*
          // The sqljdbc 1.1 documentation, chapter "Using Connection Pooling", recommends to use
          // SQLServerXADataSource instead of SQLServerConnectionPoolDataSource, even when no
          // distributed transactions are used.
          com.microsoft.sqlserver.jdbc.SQLServerXADataSource dataSource = new com.microsoft.sqlserver.jdbc.SQLServerXADataSource();
          dataSource.setApplicationName ("TestMiniConnectionPoolManager");
          dataSource.setDatabaseName ("Northwind");
          dataSource.setServerName ("localhost");
          dataSource.setUser ("sa");
          dataSource.setPassword (System.getProperty("saPassword"));
          dataSource.setLogWriter (new PrintWriter(System.out));
       */


        return dataSource;
    }
    
    public void startWorkerThreads() {
        threads = new WorkerThread[inoOfThreads];
        for (int threadNo=0; threadNo<inoOfThreads; threadNo++) {
            WorkerThread thread = new WorkerThread();
            threads[threadNo] = thread;
            thread.threadNo = threadNo;
            thread.start(); }}

    public void stopWorkerThreads() throws Exception {
        setShutdownFlag();
        for (int threadNo=0; threadNo<inoOfThreads; threadNo++) {
            threads[threadNo].join(); }}

    public void setShutdownFlag() {
        synchronized (shutdownObj) {
            shutdownFlag = true;
            shutdownObj.notifyAll(); }}

    public void threadMain (int threadNo) {
        try {
            threadMain2 (threadNo); }
        catch (Throwable e) {
            System.out.println ("\nException in thread "+threadNo+": "+e);
            e.printStackTrace (System.out);
            setShutdownFlag(); }}

    public void threadMain2 (int threadNo) throws Exception {
        // System.out.println ("Thread "+threadNo+" started.");
        while (true) {
            if (!pauseRandom(iThreadPauseTime1)) return;
            threadTask (threadNo); }}

    public void threadTask (int threadNo) throws Exception {
        try (Connection conn = getConnection()) {
            if (shutdownFlag) return;
            System.out.print (threadNo+" ");
            incrementThreadCounter (conn,threadNo);
            pauseRandom (iThreadPauseTime2); }
    }

    public boolean pauseRandom (int maxPauseTime) throws Exception {
        return pause (random.nextInt(maxPauseTime)); }

    public boolean pause (int pauseTime) throws Exception {
        synchronized (shutdownObj) {
            if (shutdownFlag) return false;
            if (pauseTime <= 0) return true;
            int ms = pauseTime / 1000;
            int ns = (pauseTime % 1000) * 1000;
            shutdownObj.wait (ms,ns); }
        return true; }

    public void initDb() throws SQLException {
        try (Connection conn = getConnection()) {
            System.out.println ("initDb connected");
            initDb2 (conn); }
        System.out.println ("initDb done"); }

    public void initDb2 (Connection conn) throws SQLException {
        execSqlNoErr (conn,"drop table temp");
        execSql (conn,"create table temp (threadNo integer, ctr integer)");
        for (int i=0; i<inoOfThreads; i++)
            execSql (conn,"insert into temp values("+i+",0)"); }

    public void incrementThreadCounter (Connection conn, int threadNo) throws SQLException {
        execSql (conn,"update temp set ctr = ctr + 1 where threadNo="+threadNo); }

    public void execSqlNoErr (Connection conn, String sql) {
        try {
            execSql (conn,sql); }
        catch (SQLException ignored) {}}

    public void execSql (Connection conn, String sql) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        }
    }

   /* public static void main (String[] args) throws Exception {
        System.out.println ("Program started.");
        ConnectionPoolDataSource dataSource = createDataSource();
        poolMgr = new MiniConnectionPoolManager(dataSource,8);
        initDb();
        startWorkerThreads();
        pause (processingTime*1000000);
        System.out.println ("\nStopping threads.");
        stopWorkerThreads();
        System.out.println ("\nAll threads stopped.");
        poolMgr.dispose();
        System.out.println ("Program completed.");
    }*/

} // end class MiniConnectionPoolManager