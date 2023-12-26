package org.task;

import org.task.dto.EquationDto;

import java.util.List;

public class MathService {
    private Parser parser;
    private Parser evaluator;
    private MathRepository repository;

    public MathService(Parser parser, Parser evaluator, MathRepository repository) {
        this.parser = parser;
        this.evaluator = evaluator;
        this.repository = repository;
    }

    public EquationDto findEquationById(int id) {
        return repository.findEquationById(id);
    }

    public void saveEquation(String equation) {

//        repository.saveEquation();
    }

    public void saveRoot(int equationId, double root) {

//        repository.saveRoot(equationId, root);
    }

    public List<String> findEquationsByRoot(double root) {
        return repository.findEquationsByRoot(root);
    }

    public List<String> findEquationsWithSingleRoot() {
        return repository.findEquationsWithSingleRoot();
    }
}
