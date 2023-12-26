package org.task;

import org.task.exceptions.EquationException;

import java.util.ArrayDeque;
import java.util.Deque;

public class Evaluator {
    private static final double EPS = 1e-9;
    public boolean validateRootForEquation(String leftPart, String rightPart, double root) {
        leftPart = substituteRoot(leftPart, root);
        rightPart = substituteRoot(rightPart, root);
        return Math.abs(evaluate(leftPart) - evaluate(rightPart)) < EPS;
    }
    private String substituteRoot(String expr, double root) {
        return expr.replaceAll("x", String.valueOf(root));
    }
    public double evaluate(String postfixExpr) {
        String[] tokens = postfixExpr.split("\\s+");
        Deque<Double> stack = new ArrayDeque<>();
        for (String token : tokens) {
            if (isNumeric(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                double secondOperand = stack.pop();
                double firstOperand = 0;
                if (!token.equals("!")){
                    firstOperand = stack.pop();
                }
                switch (token) {
                    case "!":
                        stack.push(-secondOperand);
                        break;
                    case "+":
                        stack.push(firstOperand + secondOperand);
                        break;
                    case "-":
                        stack.push(firstOperand - secondOperand);
                        break;
                    case "*":
                        stack.push(firstOperand * secondOperand);
                        break;
                    case "/":
                        if (secondOperand != 0.0) {
                            stack.push(firstOperand / secondOperand);
                        } else {
                            throw new EquationException("Cannot divide by zero!");
                        }
                }
            }
        }
        return stack.pop();
    }

    private boolean isNumeric(String num) {
        try {
            Double.parseDouble(num);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
