package com.github.p4535992.util.database.sql.performance;

import java.util.Map;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Array;

public class SQLArrayWrapper implements Array
{
  Array realArray;
  StatementWrapper parentStatement;
  String sql;

  public SQLArrayWrapper (Array array, StatementWrapper statement, String sql) {
    parentStatement = statement;
    this.sql = sql;
  }

  @Override
  public Object getArray() throws SQLException {
    return realArray.getArray();
  }

  @Override
  public Object getArray(long index, int count) throws SQLException {
    return realArray.getArray(index, count);
  }

  @Override
  public Object getArray(long index, int count, Map<String,Class<?>> map) throws SQLException {
    return realArray.getArray(index, count, map);
  }

  @Override
  public Object getArray(Map<String,Class<?>> map) throws SQLException {
    return realArray.getArray(map);
  }

  @Override
  public int getBaseType() throws SQLException {
    return realArray.getBaseType();
  }

  @Override
  public String getBaseTypeName() throws SQLException {
    return realArray.getBaseTypeName();
  }

  @Override
  public ResultSet getResultSet() throws SQLException {
    return new ResultSetWrapper(realArray.getResultSet(), parentStatement, sql);
  }

  @Override
  public ResultSet getResultSet(long index, int count) throws SQLException {
    return new ResultSetWrapper(realArray.getResultSet(index, count), parentStatement, sql);
  }

  @Override
  public void free() throws SQLException {

  }

  @Override
  public ResultSet getResultSet(long index, int count, Map<String,Class<?>> map) throws SQLException {
    return new ResultSetWrapper(realArray.getResultSet(index, count, map), parentStatement, sql);
  }

  @Override
  public ResultSet getResultSet(Map<String,Class<?>> map) throws SQLException {
    return new ResultSetWrapper(realArray.getResultSet(map), parentStatement, sql);
  }

}

