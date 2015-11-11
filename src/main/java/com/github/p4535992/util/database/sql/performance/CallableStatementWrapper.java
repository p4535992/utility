package com.github.p4535992.util.database.sql.performance;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.sql.*;
import java.util.Map;
import java.math.BigDecimal;
import java.util.Calendar;

@SuppressWarnings( "deprecation" )
public class CallableStatementWrapper extends PreparedStatementWrapper implements CallableStatement
{
  CallableStatement realCallableStatement;
  public CallableStatementWrapper(CallableStatement statement, ConnectionWrapper parent, String sql)
  {
    super(statement, parent, sql);
    realCallableStatement = statement;
  }

  public Array getArray(int i) throws SQLException {
    return new SQLArrayWrapper(realCallableStatement.getArray(i), this, sql);
  }

  public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
    return realCallableStatement.getBigDecimal(parameterIndex);
  }

  public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
    return realCallableStatement.getBigDecimal(parameterIndex, scale);
  }

  public Blob getBlob(int i) throws SQLException {
    return realCallableStatement.getBlob(i);
  }

  public boolean getBoolean(int parameterIndex) throws SQLException {
    return realCallableStatement.getBoolean(parameterIndex);
  }

  public byte getByte(int parameterIndex) throws SQLException {
    return realCallableStatement.getByte(parameterIndex);
  }

  public byte[] getBytes(int parameterIndex) throws SQLException {
    return realCallableStatement.getBytes(parameterIndex);
  }

  public Clob getClob(int i) throws SQLException {
    return realCallableStatement.getClob(i);
  }

  public Date getDate(int parameterIndex) throws SQLException {
    return realCallableStatement.getDate(parameterIndex);
  }

  public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
    return realCallableStatement.getDate(parameterIndex, cal);
  }

  public double getDouble(int parameterIndex) throws SQLException {
    return realCallableStatement.getDouble(parameterIndex);
  }

  public float getFloat(int parameterIndex) throws SQLException {
    return realCallableStatement.getFloat(parameterIndex);
  }

  public int getInt(int parameterIndex) throws SQLException {
    return realCallableStatement.getInt(parameterIndex);
  }

  public long getLong(int parameterIndex) throws SQLException {
    return realCallableStatement.getLong(parameterIndex);
  }

  public Object getObject(int parameterIndex) throws SQLException {
    return realCallableStatement.getObject(parameterIndex);
  }

  public Object getObject(int i, Map<String,Class<?>> map) throws SQLException {
    return realCallableStatement.getObject(i, map);
  }

  public Ref getRef(int i) throws SQLException {
    return realCallableStatement.getRef(i);
  }

  public short getShort(int parameterIndex) throws SQLException {
    return realCallableStatement.getShort(parameterIndex);
  }

  public String getString(int parameterIndex) throws SQLException {
    return realCallableStatement.getString(parameterIndex);
  }

  public Time getTime(int parameterIndex) throws SQLException {
    return realCallableStatement.getTime(parameterIndex);
  }

  public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
    return realCallableStatement.getTime(parameterIndex, cal);
  }

  public Timestamp getTimestamp(int parameterIndex) throws SQLException {
    return realCallableStatement.getTimestamp(parameterIndex);
  }

  public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
    return realCallableStatement.getTimestamp(parameterIndex, cal);
  }

  public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
    realCallableStatement.registerOutParameter(parameterIndex, sqlType);
  }

  public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
    realCallableStatement.registerOutParameter(parameterIndex, sqlType, scale);
  }

  public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
    realCallableStatement.registerOutParameter(paramIndex, sqlType, typeName);
  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType) throws SQLException {

  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {

  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {

  }

  @Override
  public URL getURL(int parameterIndex) throws SQLException {
    return null;
  }

  @Override
  public void setURL(String parameterName, URL val) throws SQLException {

  }

  @Override
  public void setNull(String parameterName, int sqlType) throws SQLException {

  }

  @Override
  public void setBoolean(String parameterName, boolean x) throws SQLException {

  }

  @Override
  public void setByte(String parameterName, byte x) throws SQLException {

  }

  @Override
  public void setShort(String parameterName, short x) throws SQLException {

  }

  @Override
  public void setInt(String parameterName, int x) throws SQLException {

  }

  @Override
  public void setLong(String parameterName, long x) throws SQLException {

  }

  @Override
  public void setFloat(String parameterName, float x) throws SQLException {

  }

  @Override
  public void setDouble(String parameterName, double x) throws SQLException {

  }

  @Override
  public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {

  }

  @Override
  public void setString(String parameterName, String x) throws SQLException {

  }

  @Override
  public void setBytes(String parameterName, byte[] x) throws SQLException {

  }

  @Override
  public void setDate(String parameterName, Date x) throws SQLException {

  }

  @Override
  public void setTime(String parameterName, Time x) throws SQLException {

  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x) throws SQLException {

  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {

  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {

  }

  @Override
  public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {

  }

  @Override
  public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {

  }

  @Override
  public void setObject(String parameterName, Object x) throws SQLException {

  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {

  }

  @Override
  public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {

  }

  @Override
  public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {

  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {

  }

  @Override
  public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {

  }

  @Override
  public String getString(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public boolean getBoolean(String parameterName) throws SQLException {
    return false;
  }

  @Override
  public byte getByte(String parameterName) throws SQLException {
    return 0;
  }

  @Override
  public short getShort(String parameterName) throws SQLException {
    return 0;
  }

  @Override
  public int getInt(String parameterName) throws SQLException {
    return 0;
  }

  @Override
  public long getLong(String parameterName) throws SQLException {
    return 0;
  }

  @Override
  public float getFloat(String parameterName) throws SQLException {
    return 0;
  }

  @Override
  public double getDouble(String parameterName) throws SQLException {
    return 0;
  }

  @Override
  public byte[] getBytes(String parameterName) throws SQLException {
    return new byte[0];
  }

  @Override
  public Date getDate(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public Time getTime(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public Timestamp getTimestamp(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public Object getObject(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public BigDecimal getBigDecimal(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
    return null;
  }

  @Override
  public Ref getRef(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public Blob getBlob(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public Clob getClob(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public Array getArray(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public Date getDate(String parameterName, Calendar cal) throws SQLException {
    return null;
  }

  @Override
  public Time getTime(String parameterName, Calendar cal) throws SQLException {
    return null;
  }

  @Override
  public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
    return null;
  }

  @Override
  public URL getURL(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public RowId getRowId(int parameterIndex) throws SQLException {
    return null;
  }

  @Override
  public RowId getRowId(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public void setRowId(String parameterName, RowId x) throws SQLException {

  }

  @Override
  public void setNString(String parameterName, String value) throws SQLException {

  }

  @Override
  public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {

  }

  @Override
  public void setNClob(String parameterName, NClob value) throws SQLException {

  }

  @Override
  public void setClob(String parameterName, Reader reader, long length) throws SQLException {

  }

  @Override
  public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {

  }

  @Override
  public void setNClob(String parameterName, Reader reader, long length) throws SQLException {

  }

  @Override
  public NClob getNClob(int parameterIndex) throws SQLException {
    return null;
  }

  @Override
  public NClob getNClob(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {

  }

  @Override
  public SQLXML getSQLXML(int parameterIndex) throws SQLException {
    return null;
  }

  @Override
  public SQLXML getSQLXML(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public String getNString(int parameterIndex) throws SQLException {
    return null;
  }

  @Override
  public String getNString(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public Reader getNCharacterStream(int parameterIndex) throws SQLException {
    return null;
  }

  @Override
  public Reader getNCharacterStream(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public Reader getCharacterStream(int parameterIndex) throws SQLException {
    return null;
  }

  @Override
  public Reader getCharacterStream(String parameterName) throws SQLException {
    return null;
  }

  @Override
  public void setBlob(String parameterName, Blob x) throws SQLException {

  }

  @Override
  public void setClob(String parameterName, Clob x) throws SQLException {

  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {

  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {

  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {

  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x) throws SQLException {

  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x) throws SQLException {

  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader) throws SQLException {

  }

  @Override
  public void setNCharacterStream(String parameterName, Reader value) throws SQLException {

  }

  @Override
  public void setClob(String parameterName, Reader reader) throws SQLException {

  }

  @Override
  public void setBlob(String parameterName, InputStream inputStream) throws SQLException {

  }

  @Override
  public void setNClob(String parameterName, Reader reader) throws SQLException {

  }

  @Override
  public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
    return null;
  }

  @Override
  public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
    return null;
  }

  public boolean wasNull() throws SQLException {
    return realCallableStatement.wasNull();
  }

  @Override
  public void setURL(int parameterIndex, URL x) throws SQLException {

  }

  @Override
  public ParameterMetaData getParameterMetaData() throws SQLException {
    return null;
  }

  @Override
  public void setRowId(int parameterIndex, RowId x) throws SQLException {

  }

  @Override
  public void setNString(int parameterIndex, String value) throws SQLException {

  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {

  }

  @Override
  public void setNClob(int parameterIndex, NClob value) throws SQLException {

  }

  @Override
  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {

  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {

  }

  @Override
  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {

  }

  @Override
  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {

  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {

  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {

  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {

  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {

  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {

  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {

  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {

  }

  @Override
  public void setClob(int parameterIndex, Reader reader) throws SQLException {

  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {

  }

  @Override
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {

  }

  @Override
  public boolean getMoreResults(int current) throws SQLException {
    return false;
  }

  @Override
  public ResultSet getGeneratedKeys() throws SQLException {
    return null;
  }

  @Override
  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    return 0;
  }

  @Override
  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
    return 0;
  }

  @Override
  public int executeUpdate(String sql, String[] columnNames) throws SQLException {
    return 0;
  }

  @Override
  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
    return false;
  }

  @Override
  public boolean execute(String sql, int[] columnIndexes) throws SQLException {
    return false;
  }

  @Override
  public boolean execute(String sql, String[] columnNames) throws SQLException {
    return false;
  }

  @Override
  public int getResultSetHoldability() throws SQLException {
    return 0;
  }

  @Override
  public boolean isClosed() throws SQLException {
    return false;
  }

  @Override
  public void setPoolable(boolean poolable) throws SQLException {

  }

  @Override
  public boolean isPoolable() throws SQLException {
    return false;
  }

  @Override
  public void closeOnCompletion() throws SQLException {

  }

  @Override
  public boolean isCloseOnCompletion() throws SQLException {
    return false;
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return null;
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }
}
