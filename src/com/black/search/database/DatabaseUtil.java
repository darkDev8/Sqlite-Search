package com.black.search.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {

	public static List<String> fetchTables(Connection connection) throws SQLException {
		List<String> tables = new ArrayList<>();
		DatabaseMetaData metaData = connection.getMetaData();

		ResultSet rs = metaData.getTables(null, null, "%", new String[] { "TABLE" });
		while (rs.next()) {
			tables.add(rs.getString("TABLE_NAME"));
		}

		return tables;
	}

	public static long countRecords(Connection connection, String tableName) throws SQLException {
		PreparedStatement pst = connection.prepareStatement("SELECT COUNT(*) AS total FROM ?");
		pst.setString(1, tableName);
		
		ResultSet rs = pst.executeQuery();
		return rs.getLong("total");
	}
	
	public static List<String> fetchColumns(Connection connection, String tableName) throws SQLException {
		String sql = "SELECT * FROM " + tableName + " LIMIT 0";

		List<String> columns = new ArrayList<>();
		Statement statement = connection.createStatement();

		ResultSet rs = statement.executeQuery(sql);
		ResultSetMetaData mrs = rs.getMetaData();
		
		for (int i = 1; i <= mrs.getColumnCount(); i++) {
			columns.add(mrs.getColumnLabel(i));
		}
		
		return columns;
	}
}
