package com.eitc.utils;

import com.eitc.checker.CreditCardInfoChecker;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertiesReader {
    
    private static final Logger LOGGER = Logger.getLogger(CreditCardInfoChecker.class.getName());

    private final Properties configProp = new Properties();

    private PropertiesReader() {
        //Private constructor to restrict new instances
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("api.properties");
        try {
            configProp.load(in);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "讀取設定檔錯誤", e);
        }
    }

    //Bill Pugh Solution for singleton pattern
    private static class LazyHolder {

        private static final PropertiesReader INSTANCE = new PropertiesReader();
    }

    public static PropertiesReader getInstance() {
        return LazyHolder.INSTANCE;
    }

    public String getProperty(String key) {
        return configProp.getProperty(key);
    }

    public Set<String> getAllPropertyNames() {
        return configProp.stringPropertyNames();
    }

    public boolean containsKey(String key) {
        return configProp.containsKey(key);
    }
}
