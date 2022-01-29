package com.black.search.commands;

import com.black.search.util.Box;

public class Exit {

	public void exitSoftware() {
		try {
			if (Box.args.isEmpty()) {
				if (Box.console.ask("\tAre you sure you want to exit", true) == 1) {
					System.exit(0);
				}
			} else if (Box.args.size() == 1) {
				if (Box.args.get(0).equals("-y")) {
					System.exit(0);
				} else {
					Box.print("\tThe argument is not valid.");
				}
			} else {
				Box.print("\tThis command takes only one argument.");
			}
		} catch (Exception e) {
			Box.printException(e);
		}
	}
}
