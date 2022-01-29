package com.black.search.commands;

import org.sdk.ExternalTools;

import com.black.search.util.Box;
import com.google.common.base.Strings;

public class Cd {

	public void changeDirectory() {
		try {
			if (Box.args.isEmpty()) {
				Box.vars.replace("path", Box.vars.get("path"), Box.os.getHomeUser());
			} else if (Box.args.size() == 1) {
				String path = ExternalTools.getDirectoryPath(Box.vars.get("path"),
						Box.args.get(0).replace("~", Box.os.getHomeUser()), false);
				
				if (Strings.isNullOrEmpty(path)) {
					Box.print("\tThere is no such path.");
				} else {
					Box.vars.replace("path", Box.vars.get("path"), path);
				}
			} else {
				Box.print("\tThis command takes only one path.");
			}
		} catch (Exception e) {
			Box.printException(e);
		}
	}
}
