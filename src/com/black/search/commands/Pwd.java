package com.black.search.commands;

import com.black.search.util.Box;

public class Pwd {
	
	public void printCurrentWorkingDirectory() {
		try {
			if (Box.args.isEmpty()) {
				Box.print("\tPath: " + Box.vars.get("path"));
			} else {
				Box.print("\tThis command takes no argument.");
			}
		} catch (Exception e) {
			Box.printException(e);
		}
	}
}
