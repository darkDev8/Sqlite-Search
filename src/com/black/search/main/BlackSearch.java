package com.black.search.main;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.sdk.ExternalTools;
import org.sdk.data.types.arrays.StringArrays;

import com.black.search.commands.Connect;
import com.black.search.util.Boot;
import com.black.search.util.Box;
import com.black.search.util.CommandSelector;
import com.google.common.base.Strings;

public class BlackSearch {
	private static String command;

	public static void main(String[] args) {
		title();
		startBoot();

		if (StringArrays.isNullOrEmpty(args)) {
			Box.print("Warning: No arguments detected, switching to prompt mode.");

			while (true) {
				prompt();

				command = Box.console.read(null, true);
				CommandSelector cs = new CommandSelector(command);

				switch (command.split("\\s+")[0]) {
				case "exit" -> cs.exit();
				case "connect" -> {
					/*
					 * Remove connect from command
					 */
					Box.optimizeArguments(command);
					startAnalyzeArguments(Box.args.toArray(String[]::new));
				}

				case "cd" -> cs.cd();
				case "pwd" -> cs.pwd();
				case "ls" -> cs.ls();

				default -> Box.print("\tThe command is invalid.");
				}
			}
		} else {
			startAnalyzeArguments(args);
		}
	}

	private static void title() {
		Box.print("\tBlackSearch");
		Box.print("\tCreated by darkDev8 (Build 217)");
	}

	private static void prompt() {
		String path = Box.vars.get("path").replace(Box.os.getHomeUser(), "~");
		Box.print("[" + Box.os.getUsername() + " $ " + path + "] ", false);
	}

	private static void startBoot() {
		Boot boot = new Boot();
		Box.print("");
		
		Box.print("\tLoading build number...");
		if (!boot.loadBuildNumber()) {
			Box.print("\tFailed to load build number.");
			System.exit(-1);
		}

		Box.print("\tLoading system path...");
		if (!boot.loadPath()) {
			Box.print("\tFailed to load system path.");
			System.exit(-1);
		}

		Box.print("\t", false);
		Box.console.printCharacters('=', 40, true);
		Box.print("\n", false);
	}

	private static void startAnalyzeArguments(String[] arguments) {
		try {
			List<String> databasePaths = new ArrayList<>(), wildCards = new ArrayList<>();
			/*
			 * Remove duplicate arguments
			 */
			ArrayList<String> args = new ArrayList<>(new LinkedHashSet<>(ExternalTools.toArrayList(arguments)));

			String key = null, filePath = null, logFilePath = null;
			boolean countRecords = false, caseSensitive = false;

			if (!args.contains("-p") && !args.contains("-P") && !args.contains("-w") && !args.contains("-W")) {
				Box.print("\tYou must specify at least one database path.");
				return;
			}

			if (!args.contains("-k") && !args.contains("-K") && !args.contains("-f") && !args.contains("-F")) {
				Box.print("\tYou must specify at least one key or file to serch.");
				return;
			}

			if (args.contains("-k") && args.contains("-K") && args.contains("-f") && args.contains("-F")) {
				Box.print("\tThe key can be only text or file in the same time.");
				return;
			}

			loop: for (int i = 0; i < args.size(); i++) {

				switch (args.get(i).trim().toLowerCase()) {
				case "-r" -> countRecords = true;
				case "-c" -> caseSensitive = true;
				case "-k" -> {
					if (i + 1 < args.size()) {
						key = args.get(i + 1).trim();

						if (Strings.isNullOrEmpty(key)) {
							Box.print("\tYou must specify key to search in the database.");
							return;
						}
					} else {
						Box.print("\tYou must specify key to search in the database.");
						return;
					}
				}

				case "-f" -> {
					if (i + 1 < args.size()) {
						filePath = args.get(i + 1).trim();
						String currentPath = Box.vars.get("path");

						if (Strings.isNullOrEmpty(filePath)) {
							Box.print("\tYou must specify file path to read the file.");
							return;
						} else {
							filePath = ExternalTools.getFilePath(currentPath, filePath, false);
						}
					} else {
						Box.print("\tYou must specify file path to read the file.");
						return;
					}
				}

				case "-p" -> {
					if (i + 1 < args.size()) {
						String path = ExternalTools.getFilePath(Box.vars.get("path"), args.get(i + 1), false);

						if (Strings.isNullOrEmpty(path)) {
							Box.print("\tNo database file detected.");
							return;
						} else {
							databasePaths.add(path);
						}
					} else {
						Box.print("\tNo database file detected.");
						return;
					}
				}

				case "-w" -> {
					if (i + 1 < args.size()) {
						String wildCard = args.get(i + 1).trim();

						if (!wildCard.startsWith("[") && !wildCard.endsWith("]")) {
							Box.print("\tThe wildcard format is invalid.([])");
							return;
						}

						/*
						 * Fetch wildcard from [] and remove spaces
						 */
						wildCard = wildCard.substring(1, wildCard.length() - 1);
						wildCard.replaceAll("\\s+", "");

						if (wildCard.contains(",")) {
							for (String wc : wildCard.split(",")) {
								if (!wildCards.contains(wc)) {
									wildCards.add(wc);
								}
							}
						} else {
							if (!Strings.isNullOrEmpty(wildCard)) {
								wildCards.add(wildCard);
							}
						}

						if (wildCards.isEmpty()) {
							Box.print("\tThe wildcard is empty.");
							return;
						} else {
							String path = Box.vars.get("path");
							File dir = new File(path);

							for (String wc : wildCards) {
								/*
								 * Fetch files from wildcard
								 */
								FileFilter fileFilter = new WildcardFileFilter(wc);
								File[] files = dir.listFiles(fileFilter);

								for (File file : files) {
									if (!databasePaths.contains(file.getAbsolutePath())) {
										databasePaths.add(file.getAbsolutePath());
									}
								}
							}
						}
					} else {
						Box.print("\tThe wildcard is empty.");
						return;
					}
				}

				case "-l" -> {
					if (i + 1 < args.size()) {
						logFilePath = args.get(i + 1);
					} else {
						Box.print("\tYou must specify a log file name.");
					}
				}

				default -> {
					if (args.get(i).startsWith("-")) {
						Box.print("\tInvalid argument detected.");
						break loop;
					}
				}
				}
			}

			new Connect(countRecords, caseSensitive, databasePaths, key, filePath, logFilePath).connectToDatabase();
		} catch (Exception e) {
			Box.printException(e);
		}
	}
}
