package com.black.search.util;

import org.sdk.data.types.Strings;
import org.sdk.file.TextFile;

public class Boot {
	
    public boolean loadBuildNumber() {
        try {
        	TextFile file = new TextFile("build.txt");

            if (file.exists()) {
                String firstLine = file.readFirstLine(true);

                if (Strings.isNumber(firstLine)) {
                    int number = Integer.parseInt(firstLine) + 1;
                    Box.vars.put("build", String.valueOf(number));

                    file.write(String.valueOf(number));
                } else {
                    Box.vars.put("build", "1");
                    file.write("1");
                }
            } else {
                file.create();
                file.write("1");
                Box.vars.put("build", "1");
            }

            return true;
        } catch (Exception e) {
            Box.printException(e);
            return false;
        }
    }

    public boolean loadPath() {
        try {
            if (Box.vars.containsKey("path")) {
                Box.vars.replace("path", Box.vars.get("path"), Box.os.getExecutePath());
            } else {
                Box.vars.put("path", Box.os.getExecutePath());
            }

            return true;
        } catch (Exception e) {
            Box.printException(e);
            return false;
        }
    }
}
