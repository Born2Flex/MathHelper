package org.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.task.dto.EquationDto;
import org.task.exceptions.InvalidPropertiesException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MathRepository {
    private static final Logger log = LoggerFactory.getLogger(MathRepository.class);
    private final Connection connection;
    public MathRepository(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
            log.debug("Connection to DB created successfully");
        } catch (SQLException e) {
            log.warn("Can't create connection to DB");
            throw new InvalidPropertiesException("Failed to create connection", e);
        }
    }

    public EquationDto findEquationById(int id) {
        EquationDto equationDto = null;
        try (PreparedStatement statement = connection
                .prepareStatement("""
                                    SELECT id, left_part, right_part, equation
                                    FROM equations WHERE id = (?)
                                    """)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                equationDto = new EquationDto();
                equationDto.setEquationId(resultSet.getInt("id"));
                equationDto.setLeftPart(resultSet.getString("left_part"));
                equationDto.setRightPart(resultSet.getString("right_part"));
                equationDto.setEquation(resultSet.getString("equation"));
            }
        } catch (SQLException e) {
            log.warn("Exception occurred while getting equation by id", e);
        }
        if (equationDto == null) {
            log.warn("Can't find equation by id");
        }
        return equationDto;
    }

    public void saveEquation(String leftPart, String rightPart, String equation) {
        try (PreparedStatement statement = connection
                .prepareStatement("""
                                    INSERT INTO equations (left_part, right_part, equation)
                                    VALUES (?, ?, ?)
                                    """)) {
            statement.setString(1, leftPart);
            statement.setString(2, rightPart);
            statement.setString(3, equation);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.warn("Can't save equation in DB", e);
        }
    }

    public void saveRoot(int equationId, double root) {
        try (PreparedStatement statement = connection
                .prepareStatement("""
                                    INSERT INTO roots (equation_id, root_value)
                                    VALUES (?, ?)
                                    """)) {
            statement.setDouble(1, equationId);
            statement.setDouble(2, root);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.warn("Can't save root in DB");
        }
    }

    public List<String> findEquationsByRoot(double root) {
        List<String> equations = new ArrayList<>();
        try (PreparedStatement statement = connection
                .prepareStatement("""
                                    SELECT equation
                                    FROM equations e
                                    JOIN roots r ON e.id = r.equation_id
                                    WHERE root_value = (?)
                                    """)) {
            statement.setDouble(1, root);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                equations.add(resultSet.getString("equation"));
            }
        } catch (SQLException e) {
            log.warn("Can't find equations by root");
        }
        return equations;
    }

    public List<String> findEquationsWithSingleRoot() {
        List<String> equations = new ArrayList<>();
        try (PreparedStatement statement = connection
                .prepareStatement("""
                                    SELECT equation
                                    FROM equations e
                                    JOIN roots r ON e.id = r.equation_id
                                    GROUP BY e.equation
                                    HAVING COUNT(r.root_value) = 1;
                                    """)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                equations.add(resultSet.getString("equation"));
            }
        } catch (SQLException e) {
            log.warn("Can't find equations by root");
        }
        return equations;
    }

    public List<EquationDto> findAllEquations() {
        List<EquationDto> equations = new ArrayList<>();
        try (PreparedStatement statement = connection
                .prepareStatement("""
                                    SELECT *
                                    FROM equations e;
                                    """)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                EquationDto equation = new EquationDto();
                equation.setEquationId(resultSet.getInt(1));
                equation.setLeftPart(resultSet.getString(2));
                equation.setRightPart(resultSet.getString(3));
                equation.setEquation(resultSet.getString(4));
                equations.add(equation);
            }
        } catch (SQLException e) {
            log.warn("Can't find all equations");
        }
        return equations;
    }

    public boolean equationAlreadyExists(String equation) {
        try (PreparedStatement statement = connection
                .prepareStatement("""
                                    SELECT COUNT(*)
                                    FROM equations
                                    WHERE equation = (?)
                                    """)) {
            statement.setString(1, equation);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            log.warn("Error checking if equation exists in DB", e);
        }
        return false;
    }

    public boolean rootAlreadyExists(int equationId, double root) {
        try (PreparedStatement statement = connection
                .prepareStatement("""
                                    SELECT COUNT(*)
                                    FROM roots
                                    WHERE equation_id = (?) AND root_value = (?)
                                    """)) {
            statement.setInt(1, equationId);
            statement.setDouble(2, root);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            log.warn("Error checking if root exists in DB", e);
        }
        return false;
    }
}
