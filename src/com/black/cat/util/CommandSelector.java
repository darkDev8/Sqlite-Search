package com.black.cat.util;

import com.black.cat.commands.Cd;
import com.black.cat.commands.Connect;
import com.black.cat.commands.Exit;
import com.black.cat.commands.Ls;
import com.black.cat.commands.Pwd;

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
