package com.black.cat.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sdk.Console;
import org.sdk.ExternalTools;
import org.sdk.OSTools;

public class Box {
	public final static Map<String, String> vars;
	public static List<String> args;
	
	public final static Console console;
	public final static OSTools os;
	
	static {
		vars = new HashMap<>();
		args = new ArrayList<>();
		
		console = new Console(2, false)
				.setLine(40);
		
		os = new OSTools();
	}
	
	public static void printException(Exception e) {
		console.print(e.getMessage(), true);
	}
	
    public static void optimizeArguments(String command) {
        args = ExternalTools.optimizeParametersAsList(command.split("\\s+"));
        Box.args.remove(0);
    }

    public static void print(String text) {
        console.print(text, true);
    }

    public static void print(String text, boolean nextLine) {
        console.print(text, nextLine);
    }
}
