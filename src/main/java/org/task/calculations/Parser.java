package org.task.calculations;

import org.task.exceptions.EquationException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static final Pattern NUM = Pattern.compile("(\\d+(\\.\\d+)?)");
    private static final Pattern NON_BRACKETS = Pattern.compile("[^()]+");

    public String parseExpr(String expr) {
        List<String> nums = getNumbers(expr);
        expr = formatExpr(expr);
        List<Character> res = toPostfix(expr);
        return substituteNumbers(res, nums);
    }

    private List<String> getNumbers(String expr) {
        Matcher matcher = NUM.matcher(expr);
        List<String> nums = new ArrayList<>();
        while (matcher.find()) {
            nums.add(matcher.group());
        }
        return nums;
    }

    private String formatExpr(String expr) {
        return NUM.matcher(expr).replaceAll("a")
                .replaceAll("\\s+", "");
    }

    private String substituteNumbers(List<Character> expr, List<String> nums) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < expr.size(); i++) {
            char ch = expr.get(i);
            if (ch == 'a' && !nums.isEmpty()) {
                result.append(nums.remove(0));
            } else {
                result.append(ch);
            }
            if (i < expr.size() - 1) {
                result.append(" ");
            }
        }
        return result.toString();
    }

    private List<Character> toPostfix(String expr) {
        boolean prevNum = false;
        boolean unaryMinus = false;
        List<Character> res = new ArrayList<>();
        Deque<Character> stack = new ArrayDeque<>();
        for (char curr : expr.toCharArray()) {
            if (isNumber(curr)) {
                processNumber(curr, prevNum, res);
                prevNum = true;
                unaryMinus = false;
            } else if (isUnaryMinus(curr, prevNum, unaryMinus)) {
                addOperatorToStack('!', stack, res);
                unaryMinus = true;
            } else if (isOperation(curr)) {
                processOperator(curr, prevNum, stack, res);
                prevNum = false;
            } else if (curr == '(') {
                stack.push(curr);
            } else if (curr == ')') {
                processBrackets(stack, res);
            } else {
                throw new EquationException("Incorrect equation!");
            }
        }
        if (!prevNum) {
            throw new EquationException("Incorrect equation!");
        }
        while (!stack.isEmpty()) {
            res.add(stack.pop());
        }
        return res;
    }

    private boolean isUnaryMinus(char curr, boolean prevNum, boolean unaryMinus) {
        return !prevNum && !unaryMinus && curr == '-';
    }

    private boolean isNumber(char curr) {
        return curr == 'a' || curr == 'x';
    }

    private boolean isOperation(char op) {
        return op == '-' || op == '+' || op == '*' || op == '/' ;
    }

    private void processNumber(char curr, boolean prevNum, List<Character> res) {
        if (!prevNum) {
            res.add(curr);
        } else {
            throw new EquationException("In equation can't be 2 numbers in a row!");
        }
    }

    private void processOperator(char curr, boolean prevNum, Deque<Character> stack, List<Character> res) {
        if (prevNum) {
            addOperatorToStack(curr, stack, res);
        } else {
            throw new EquationException("In equation can't be 2 operators in a row!");
        }
    }

    private void addOperatorToStack(char curr, Deque<Character> stack, List<Character> res) {
        int priority = getPriority(curr);
        while (!stack.isEmpty() && priority <= getPriority(stack.peek())) {
            res.add(stack.pop());
        }
        stack.push(curr);
    }

    private int getPriority(char operator) {
        return switch (operator) {
            case '!' -> 3;
            case '*', '/' -> 2;
            case '+', '-' -> 1;
            default -> 0;
        };
    }

    private void processBrackets(Deque<Character> stack, List<Character> res) {
        while (!stack.isEmpty() && stack.peek() != '(') {
            res.add(stack.pop());
        }
        stack.pop();
    }

    public boolean validateParentheses(String expr) {
        String parentheses = NON_BRACKETS.matcher(expr).replaceAll("");
        Deque<Character> stack = new ArrayDeque<>();
        for (char curr : parentheses.toCharArray()) {
            if (curr == '(') {
                stack.push(curr);
            } else if (stack.isEmpty() || stack.pop() != '(') {
                return false;
            }
        }
        return stack.isEmpty();
    }
}
