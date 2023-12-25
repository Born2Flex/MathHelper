package org.task;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.task.exceptions.EquationException;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    private static Parser parser;

    @BeforeAll
    static void setup() {
        parser = new Parser();
    }

    @ParameterizedTest(name = "expression = {0}, expected result = {1}")
    @CsvFileSource(resources = "/expressions_correct.csv")
    void parseExprTest_CorrectExpressions(String expr, String expectedResult) {
        assertEquals(expectedResult, parser.parseExpr(expr));
    }

    @ParameterizedTest(name = "expression = {0}, expected exception = {1}")
    @CsvFileSource(resources = "/expressions_incorrect.csv")
    void parseExprTest_IncorrectExpressions(String expr, String expectedExceptionMessage) {
        Exception e = assertThrows(EquationException.class, () -> {
           parser.parseExpr(expr);
        });
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @ParameterizedTest(name = "expression = {0}")
    @CsvFileSource(resources = "/parentheses_correct.csv")
    void validateParenthesesTest_CorrectParentheses(String expr) {
        assertTrue(parser.validateParentheses(expr));
    }

    @ParameterizedTest(name = "expression = {0}")
    @CsvFileSource(resources = "/parentheses_incorrect.csv")
    void validateParenthesesTest_IncorrectParentheses(String expr) {
        assertFalse(parser.validateParentheses(expr));
    }
}