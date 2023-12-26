package org.task;

import org.task.calculations.Evaluator;
import org.task.calculations.Parser;
import org.task.dto.EquationDto;
import org.task.exceptions.EquationException;

import java.util.List;

public class MathService {
    private final Parser parser;
    private final Evaluator evaluator;
    private final MathRepository repository;

    public MathService(Parser parser, Evaluator evaluator, MathRepository repository) {
        this.parser = parser;
        this.evaluator = evaluator;
        this.repository = repository;
    }

    public void saveEquation(String equation) {
        if (equation.indexOf('=') != -1) {
            String[] parts = equation.split("=");
            equation = equation.replaceAll("\\s+", "");
            if (repository.equationAlreadyExists(equation)) {
                throw new EquationException("Equation already exists");
            }
            if (parser.validateParentheses(parts[0]) && parser.validateParentheses(parts[1])) {
                repository.saveEquation(parser.parseExpr(parts[0]), parser.parseExpr(parts[1]), equation);
            } else {
                throw new EquationException("Incorrect parentheses!");
            }
        } else {
            throw new EquationException("Incorrect equation!");
        }
    }

    public boolean saveRoot(int equationId, double root) {
        EquationDto equationDto = repository.findEquationById(equationId);
        if (evaluator.validateRootForEquation(equationDto.getLeftPart(), equationDto.getRightPart(), root)
                && !repository.rootAlreadyExists(equationId, root)) {
            repository.saveRoot(equationId, root);
            return true;
        }
        return false;
    }

    public List<String> findEquationsByRoot(double root) {
        return repository.findEquationsByRoot(root);
    }

    public List<String> findEquationsWithSingleRoot() {
        return repository.findEquationsWithSingleRoot();
    }

    public List<EquationDto> findAllEquations() {
        return repository.findAllEquations();
    }
}
