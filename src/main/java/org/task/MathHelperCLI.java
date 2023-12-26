package org.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.task.calculations.Evaluator;
import org.task.calculations.Parser;
import org.task.db.DBInitializer;
import org.task.dto.EquationDto;
import org.task.exceptions.EquationException;
import org.task.exceptions.InvalidPropertiesException;
import org.task.properties.PropertiesLoader;

import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class MathHelperCLI {
    private static MathService mathService;
    private static final Logger log = LoggerFactory.getLogger(MathHelperCLI.class);
    public static void main(String[] args) {
        try {
            mathService = initialize();
        } catch (InvalidPropertiesException e) {
            log.info("Please enter needed data in application.properties");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        boolean active = true;
        while (active) {
            log.info("Choose an option:");
            log.info("1. Save Equation");
            log.info("2. Add Root");
            log.info("3. Find Equations by Root");
            log.info("4. Find Equations with Single Root");
            log.info("5. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    saveEquation(scanner);
                    break;
                case 2:
                    addRoot(scanner);
                    break;
                case 3:
                    findEquationsByRoot(scanner);
                    break;
                case 4:
                    findEquationsWithSingleRoot();
                    break;
                case 5:
                    log.info("Exiting the program. Goodbye!");
                    active = false;
                    break;
                default:
                    log.info("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static MathService initialize() {
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        Properties properties = propertiesLoader.loadProperties();
        validateProperties(properties);
        DBInitializer initializer = new DBInitializer(properties.getProperty("url"),
                                                    properties.getProperty("username"),
                                                    properties.getProperty("password"));
        initializer.initialize();
        return new MathService(new Parser(),
                new Evaluator(),
                new MathRepository(properties.getProperty("url"),
                        properties.getProperty("username"),
                        properties.getProperty("password")));
    }

    private static void validateProperties(Properties properties) {
        if (properties.getProperty("url").isEmpty()
                || properties.getProperty("username").isEmpty()
                || properties.getProperty("password").isEmpty()) {
            throw new InvalidPropertiesException("No needed properties!");
        }
    }

    private static void saveEquation(Scanner scanner) {
        log.info("Enter the equation:");
        String equation = scanner.nextLine();
        try {
            mathService.saveEquation(equation);
            log.info("Equation saved successfully.");
        } catch (EquationException e) {
            log.warn("Error: {}", e.getMessage());
        }
    }

    private static void addRoot(Scanner scanner) {
        List<EquationDto> equations = mathService.findAllEquations();
        if (equations.isEmpty()) {
            log.info("No equations available. Returning to the main menu.");
            return;
        }
        log.info("Select an equation by entering its number (or type 'back' to return to the main menu):");
        for (int i = 0; i < equations.size(); i++) {
            log.info("{}. {}", (i + 1), equations.get(i).getEquation());
        }
        log.info("Enter the equation number to add a root:");
        int equationNum = scanner.nextInt();
        scanner.nextLine();
        log.info("Enter the root value:");
        double root = scanner.nextDouble();
        scanner.nextLine();
        boolean result = mathService.saveRoot(equations.get(equationNum - 1).getEquationId(), root);
        if (result) {
            log.info("Root added successfully.");
        } else {
            log.warn("Root is incorrect or already exists");
        }
    }

    private static void findEquationsByRoot(Scanner scanner) {
        log.info("Enter the root value to find equations:");
        double root = scanner.nextDouble();
        scanner.nextLine();
        List<String> equations = mathService.findEquationsByRoot(root);
        if (equations.isEmpty()) {
            log.info("No equations found with the specified root.");
        } else {
            log.info("Equations with the specified root:");
            for (String equation : equations) {
                log.info(equation);
            }
        }
    }

    private static void findEquationsWithSingleRoot() {
        List<String> equations = mathService.findEquationsWithSingleRoot();
        if (equations.isEmpty()) {
            log.info("No equations found with a single root.");
        } else {
            log.info("Equations with a single root:");
            for (String equation : equations) {
                log.info(equation);
            }
        }
    }
}
