package com.mycompany.app.Config;

import tools.jackson.databind.ObjectMapper;

public class ConfigurationManager {
    private static final String DEFAULT_CONFIG_FILE = "default-config.json";
    private static final ObjectMapper om = new ObjectMapper();

    private Configuration config;
    private String configFile;

    private ConfigurationManager() {
        this.configFile = DEFAULT_CONFIG_FILE;
        loadConfiguration();
    }

    private ConfigurationManager(String configFile) {
        this.configFile = configFile;
        loadConfiguration();
    }

    private static class Holder {
        private static final ConfigurationManager INSTANCE = new ConfigurationManager();
    }

    public static ConfigurationManager getInstance() {
        return Holder.INSTANCE;
    }

    public static ConfigurationManager getInstance(String configFile) {
        ConfigurationManager instance = Holder.INSTANCE;
        if (!instance.configFile.equals(configFile)) {
            instance.setConfigFile(configFile);
            instance.loadConfiguration();
        }
        return instance;
    }

    public Configuration loadConfiguration() {
        try {
            this.config = om.readValue(
                    this.getClass().getClassLoader().getResourceAsStream(configFile),
                    Configuration.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }

    public void saveConfiguration(Configuration config) {
        try {
            om.writeValue(System.out, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public Configuration getConfig() {
        return config;
    }
}
