package com.black.search.util;

import com.black.search.commands.Cd;
import com.black.search.commands.Connect;
import com.black.search.commands.Exit;
import com.black.search.commands.Ls;
import com.black.search.commands.Pwd;

public class CommandSelector {

	public CommandSelector(String command) {
		Box.optimizeArguments(command);
	}

	public CommandSelector exit() {
		new Exit().exitSoftware();
		return this;
	}
	
	public CommandSelector cd() {
		new Cd().changeDirectory();
		return this;
	}
	
	public CommandSelector pwd() {
		new Pwd().printCurrentWorkingDirectory();
		return this;
	}
	
	public CommandSelector ls() {
		new Ls().analyzeArguments();
		return this;
	}
}
