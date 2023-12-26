package org.task.db;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.task.properties.PropertiesLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBInitializer {
    private static final Logger log = LoggerFactory.getLogger(DBInitializer.class);
    private static final String DDL_FILENAME = "ddl.sql";
    private final Connection connection;

    public DBInitializer(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
            log.debug("Connection to DB created successfully");
        } catch (SQLException e) {
            log.warn("Can't create connection to DB");
            throw new RuntimeException("Failed to create connection", e);
        }
    }

    public void initialize() {
        log.debug("DBInitializer started");
        ClassLoader classLoader = DBInitializer.class.getClassLoader();
        InputStream input = classLoader.getResourceAsStream(DDL_FILENAME);
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.setLogWriter(null);
        scriptRunner.setSendFullScript(true);
        scriptRunner.runScript(new BufferedReader(new InputStreamReader(input)));
    }
}
