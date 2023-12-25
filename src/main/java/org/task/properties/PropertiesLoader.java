package org.task.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.task.App;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private static final String CONFIG_FILENAME = "application.properties";
    private static final Logger log = LoggerFactory.getLogger(PropertiesLoader.class);

    public Properties loadProperties() {
        Properties properties = new Properties();
        try {
            // Use the current class's ClassLoader to access resources
            ClassLoader classLoader = App.class.getClassLoader();
            InputStream input = classLoader.getResourceAsStream(CONFIG_FILENAME);

            if (input != null) {
                properties.load(input);
            } else {
                log.error("File not found in classpath: {}", CONFIG_FILENAME);
                System.exit(1);
            }
        } catch (IOException e) {
            log.error("Error occurred while loading properties",e);
        }
        return properties;
    }
}
