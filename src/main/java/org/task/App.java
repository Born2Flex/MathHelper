package org.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.task.calculations.Evaluator;
import org.task.calculations.Parser;
import org.task.properties.PropertiesLoader;

import java.util.Properties;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {
        log.info( "Hello World!" );
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        Properties properties = propertiesLoader.loadProperties();
        MathService service = new MathService(new Parser(),
                new Evaluator(),
                new MathRepository(properties.getProperty("url"),
                                    properties.getProperty("username"),
                                    properties.getProperty("password")));
//        service.saveEquation("2*x+5=17");
        service.findEquationById(1);
        log.debug("");
    }
}
