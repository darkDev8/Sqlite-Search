package com.black.search.commands;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sdk.ExternalTools;
import org.sdk.SqliteConnection;
import org.sdk.file.FileUtils;
import org.sdk.file.TextFile;

import com.black.search.database.DatabaseUtil;
import com.black.search.util.Box;
import com.google.common.base.Strings;

public class Connect {
	private boolean countRecords;
	private boolean caseSensitive;

	private List<String> databasePaths;
	private String key;

	private String filePath;
	private String logFilePath;

	private StringBuilder log;

	public Connect(boolean countRecords, boolean caseSensitive, List<String> databasePaths, String key, String filePath,
			String logFilePath) {
		super();

		this.countRecords = countRecords;
		this.caseSensitive = caseSensitive;

		this.databasePaths = databasePaths;
		this.key = key;

		this.filePath = filePath;
		this.logFilePath = logFilePath;

		log = new StringBuilder();
	}

	public void connectToDatabase() {
		try {
			for (String path : databasePaths) {
				SqliteConnection sqliteConnection = new SqliteConnection(path);
				FileUtils fUtils = new FileUtils(path);

				boolean found = false;
				ResultSet rs = null;

				/*
				 * Check database file from system.
				 */
				if (fUtils.exists()) {
					List<String> tables = new ArrayList<>();
					String sql = null;

					if (sqliteConnection.connect(false)) {
						Connection con = sqliteConnection.getConnection();

						addToLog("[*] Database \"" + fUtils.getName() + "\" connected successfully.");

						addToLog("==================================================");
						addToLog("[*] Fetching tables...");
						
						tables = DatabaseUtil.fetchTables(con);
						if (tables.isEmpty()) {
							addToLog("The database is empty.");
						} else {
							addToLog("[*] " + tables.size() + " tables detected.\n");
							
							for (String table : tables) {
								addToLog("[*] "+ table, false);
								
								if (countRecords) {
									addToLog("(" + DatabaseUtil.countRecords(con, table) + ") records");
								} else {
									addToLog("");
								}
							}
							
							addToLog("");
							if (Strings.isNullOrEmpty(key)) {
								addToLog("[*] Searching for file...");
							} else if (Strings.isNullOrEmpty(filePath)) {
								addToLog("[*] Searching for key...");
							}

							for (String table : tables) {
								List<String> columns = DatabaseUtil.fetchColumns(con, table);

								for (String column : columns) {
									sql = "SELECT " + column + " FROM " + table;
									PreparedStatement pst = con.prepareStatement(sql);
									rs = pst.executeQuery();

									while (rs.next()) {
										if (Strings.isNullOrEmpty(key)) {
											if (new FileUtils(filePath).exists()) {
												/*
												 * Read from file and check bytes of data
												 */
												if (Arrays.equals(ExternalTools.readFileToBytes(filePath),
														rs.getBytes(column))) {
													addToLog("[*] File \"" + new FileUtils(filePath).getName()
															+ "\" found in table \"" + table + "\" in column \""
															+ column + "\"");
													
								
													found = true;
												}
											} else {
												addToLog("[*] No file exists to read data.");
												return;
											}
										} else if (Strings.isNullOrEmpty(filePath)) {
											if (caseSensitive) {
												if (key.equals(rs.getString(column))) {
													addToLog("[*] Key \"" + key + "\" found in table \"" + table
															+ "\" in column \"" + column + "\"");

								
													found = true;
												}
											} else {
												if (key.equalsIgnoreCase(rs.getString(column))) {
													addToLog("[*] Key \"" + key + "\" found in table \"" + table
															+ "\" in column \"" + column + "\"");
													
								
													found = true;
												}
											}
										}
									}
								}
							}

							if (!found) {
								addToLog("[*] No match found in the database.");
							}
						}

						addToLog("------------------------------" + System.lineSeparator());
						if (!log.isEmpty() && !Strings.isNullOrEmpty(logFilePath)) {
							TextFile logFile = new TextFile(logFilePath);
							logFile.clear();

							if (logFile.append(log.toString() + "\t" + Box.os.getTimeDate())) {
								Box.print("\t[*] Log file created successfully.");
							} else {
								Box.print("\t[*] Failed to create new log file.");
							}
						}
					} else {
						addToLog("[*] Failed to connect to \"" + fUtils.getName() + "\" database.");
					}
				} else {
					addToLog("[*] No database file detected.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addToLog(String log, boolean nextLine) {
		this.log.append(log).append(System.lineSeparator());
		Box.print("\t" + log, nextLine);
	}

	private void addToLog(String log) {
		addToLog(log, true);
	}
}
