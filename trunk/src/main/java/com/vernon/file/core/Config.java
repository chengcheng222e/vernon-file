package com.vernon.file.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    // ----------------------------------- field names --------------------------

    private Properties prop = new Properties();
    public static final Config INSTANCE = new Config();

    // ---------------------------- constructor method --------------------------
    private Config() {
        InputStream inputStream = null;
        try {
            inputStream = Config.class.getResourceAsStream("/main.properties");
            prop.load(inputStream);
        } catch (Exception e) {
            System.err.println("Cannot load config file");
            System.exit(0);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ----------------------- setter / getter methods -------------------------

    public String get(String name) {
        return prop.getProperty(name);
    }

    public int getInt(String name, int defaultValue) {
        return Integer.parseInt(get(name, defaultValue + ""));
    }

    public String get(String name, String defaultValue) {
        String value = prop.getProperty(name);
        if (value == null) {
            return defaultValue;
        }
        return value.trim();
    }
}