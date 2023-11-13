package com.spin.cgt.tool;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogTool {
    public static final Logger LOGGER = Logger.getLogger("CaseGenTool");

    static {
        LOGGER.setLevel(Level.INFO);
        try {
            FileHandler fileHandler = new FileHandler("test_log/caseGenTool.log");
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
