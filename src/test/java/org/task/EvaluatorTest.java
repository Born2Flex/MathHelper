package org.task;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.task.exceptions.EquationException;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {
    private static Evaluator evaluator;

    @BeforeAll
    static void setup() {
        evaluator = new Evaluator();
    }

    @ParameterizedTest(name = "left part = {0}, right part = {1}, root = {2}, expected result = {3}")
    @CsvFileSource(resources = "/expressions_roots.csv")
    void validateRootForEquationTest(String leftPart, String rightPart, double root, boolean expectedResult) {
        assertEquals(expectedResult, evaluator.validateRootForEquation(leftPart, rightPart, root));
    }

    @ParameterizedTest(name = "left part = {0}, right part = {1}, root = {2}, expected result = {3}")
    @CsvFileSource(resources = "/expressions_div0.csv")
    void validateRootForEquationTest_DivisionByZero_ShouldThrow(String leftPart, String rightPart, double root) {
        Exception e = assertThrows(EquationException.class, () -> {
            evaluator.validateRootForEquation(leftPart, rightPart, root);
        });
        assertEquals("Cannot divide by zero!",e.getMessage());
    }
}