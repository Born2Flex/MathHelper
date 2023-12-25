package org.task;

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

    public String toPostfix(String expr) {
        Matcher matcher = NUM.matcher(expr);
        List<String> nums = new ArrayList<>();
        String modifiedExpr = matcher.replaceAll(num -> {
            nums.add(matcher.group());
            return "a";
        });
        modifiedExpr = modifiedExpr.replaceAll("\\s+","");

        boolean prevNum = false;
        boolean unaryMinus = false;
        List<Character> res = new ArrayList<>();
        Deque<Character> stack = new ArrayDeque<>();

        for (char curr : modifiedExpr.toCharArray()) {
            if (curr == 'a' || curr == 'x') {
                if (!prevNum) {
                    res.add(curr);
                    prevNum = true;
                    unaryMinus = false;
                } else {
                    throw new EquationException("In equation can't be 2 numbers in a row!");
                }
            } else if (!prevNum && !unaryMinus && curr == '-') {
                processOperator('!', stack, res);
                unaryMinus = true;
            } else if (isOperation(curr)) {
                if (prevNum) {
                    processOperator(curr, stack, res);
                    prevNum = false;
                } else {
                    throw new EquationException("In equation can't be 2 operators in a row!");
                }
            } else if (curr == '(') {
                stack.push(curr);
            } else if (curr == ')') {
                processBrackets(stack, res);
            } else {
                throw new EquationException("Incorrect equation!");
            }
        }
        while (!stack.isEmpty()) {
            res.add(stack.pop());
        }

        return substituteNumbers(res, nums);
    }

    private String substituteNumbers(List<Character> res, List<String> nums) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < res.size(); i++) {
            char ch = res.get(i);
            if (ch == 'a' && !nums.isEmpty()) {
                result.append(nums.remove(0));
            } else {
                result.append(ch);
            }
            if (i < res.size() - 1) {
                result.append(" ");
            }
        }
        return result.toString();
    }

    private void processBrackets(Deque<Character> stack, List<Character> res) {
        while (!stack.isEmpty() && stack.peek() != '(') {
            res.add(stack.pop());
        }
        stack.pop();
    }

    private boolean isOperation(char op) {
        return op == '-' || op == '+' || op == '*' || op == '/' ;
    }

    private void processOperator(char curr, Deque<Character> stack, List<Character> res) {
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
        System.out.println(parentheses);
        return stack.isEmpty();
    }


    public static void main(String[] args) {
        Parser parser = new Parser();
//        parser.toPostfix("2*x+5=17");
//        parser.toPostfix("-1.3*5/x=1.2");
//        parser.toPostfix("2*x*x=10");
//        parser.toPostfix("2*(x+5+х)+5=10");
//        parser.toPostfix("17=2*x+5");

        System.out.println(parser.validateParentheses("2*(x+5+х)+5=10"));
        System.out.println(parser.validateParentheses("(3 * (2 + 5)) - (4 / (1 + 2)) + ((8 - 2) * 4)"));

        System.out.println(parser.toPostfix("(3 * (2 + 5)) - (4 / (1 + 2)) + ((8 - 2) * 4)"));
        System.out.println(parser.toPostfix("5+(-3+1)"));
        //-1.3*5/x=1.2
        System.out.println(parser.toPostfix(" -1.3 * 5 / x "));
        System.out.println(parser.toPostfix("1.2"));
        System.out.println(parser.toPostfix("1.2------5"));
    }
}
