package com.black.search.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sdk.ExternalTools;
import org.sdk.data.structures.lists.StringList;
import org.sdk.directory.DirectoryUtils;
import org.sdk.file.FileUtils;

import com.black.search.util.Box;
import com.google.common.base.Strings;

public class Ls {

	public void analyzeArguments() {
		try {
			List<String> paths = new ArrayList<>();

			String listType = "";
			boolean hidden = false;

			if (Box.args.isEmpty()) {
				paths.add(Box.vars.get("path"));
			} else {
				for (String arg : Box.args) {
					if (arg.startsWith("-")) {
						
						for (char c : arg.substring(1).toCharArray()) {
							switch (c) {
							case 'h' -> hidden = true;
							case 'f' -> listType += "f";
							case 'd' -> listType += "d";
							default -> {
								Box.print("\tThe argument is invalid.");
								return;
							}
							}
						}
					} else {
						String currentPath = Box.vars.get("path"), path = null;
						path = ExternalTools.getDirectoryPath(currentPath, path, false);

						if (Strings.isNullOrEmpty(path)) {
							Box.print("\tNo directory found.");
						} else {
							paths.add(path);
						}
					}
				}
			}
			
			list(hidden, listType, paths);
		} catch (Exception e) {
			Box.printException(e);
		}
	}

	public void list(boolean hidden, String listType, List<String> paths) {
	    try {
            StringList users = new StringList(true), createDates = new StringList(true),
                    sizes = new StringList(true), names = new StringList(true);

            int space = 4;

            DirectoryUtils ds = new DirectoryUtils(Box.vars.get("path"));
            File[] files = ds.getDirectoryContent().toArray(new File[0]);

            if (!Strings.isNullOrEmpty(listType)) {

                if (listType.equals("f")) {
                    files = ds.getFiles().toArray(new File[0]);
                } else if (listType.equals("d")) {
                    files = ds.getDirectories().toArray(new File[0]);
                } else if (listType.equals("fd") || listType.equals("df")) {
                	files = ds.getDirectoryContent().toArray(new File[0]);
                }
            }

            for (File file : files) {
                FileUtils fp = new FileUtils(file.getAbsolutePath());

                if (hidden) {
                    users.add(fp.getOwner());
                    createDates.add(fp.getCreateDate());
                    sizes.add(fp.getReadableSize());
                    names.add(fp.getName());
                } else {
                    if (!fp.getFile().isHidden()) {
                        users.add(fp.getOwner());
                        createDates.add(fp.getCreateDate());
                        sizes.add(fp.getReadableSize());
                        names.add(fp.getName());
                    }
                }
            }

            if (!names.isEmpty()) {
                String biggestUser = users.getBiggest(), biggestCreateDate = createDates.getBiggest(),
                        biggestSize = sizes.getBiggest(), biggestName = names.getBiggest();


                Box.print("\t", false);
                int x = ((biggestUser.length() - "User".length()) / 2);

                Box.console.printCharacters(' ', x, false);
                Box.print("User", false);
                Box.console.printCharacters(' ',
                        biggestUser.length() - "User".length() + space - x, false);

                Box.print("Create date", false);
                Box.console.printCharacters(' ',
                        biggestCreateDate.length() - "Create date".length() + space, false);

                x = ((biggestSize.length() - "Size".length()) / 2);
                Box.console.printCharacters(' ', x, false);

                Box.print("Size", false);
                Box.console.printCharacters(' ',
                        biggestSize.length() - "Size".length() + space - x, false);

                x = ((biggestName.length() - "Name".length()) / 2);
                Box.console.printCharacters(' ', x, false);

                Box.print("Name", false);
                Box.console.printCharacters(' ',
                        biggestName.length() - "Name".length() + space - x, true);

                Box.print("\t", false);
                Box.console.printCharacters('-', biggestUser.length(), false);

                Box.console.printCharacters(' ', space, false);
                Box.console.printCharacters('-', biggestCreateDate.length(), false);

                Box.console.printCharacters(' ', space, false);
                Box.console.printCharacters('-', biggestSize.length(), false);

                Box.console.printCharacters(' ', space, false);
                Box.console.printCharacters('-', biggestName.length(), true);

                for (int i = 0; i < users.size(); i++) {
                    Box.print("\t", false);

                    Box.print(users.get(i), false);
                    if (users.get(i).equals(biggestUser)) {
                        Box.print("    ", false);
                    } else {
                    	Box.console.printCharacters(' ', biggestUser.length() - users.get(i).length() + space, false);
                    }

                    Box.print(createDates.get(i), false);
                    if (createDates.get(i).equals(biggestCreateDate)) {
                        Box.print("    ", false);
                    } else {
                    	Box.console.printCharacters(' ', biggestCreateDate.length() - createDates.get(i).length() + space, false);
                    }

                    Box.print(sizes.get(i), false);
                    if (sizes.get(i).equals(biggestSize)) {
                        Box.print("    ", false);
                    } else {
                    	Box.console.printCharacters(' ', biggestSize.length() - sizes.get(i).length() + space, false);
                    }

                    Box.print(names.get(i), true);
                }
            }
        } catch (Exception e) {
        	Box.printException(e);
        }
	}
}
