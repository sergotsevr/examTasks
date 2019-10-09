package com.tsystems.javaschool.tasks.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.regex.Pattern;

public class Calculator {
    private static HashMap<String, Integer> priorityMap = new HashMap() {{
        put("(", 0);
        put("(", 0);
        put("+", 1);
        put("-", 1);
        put("*", 2);
        put("/", 2);
        put("<", 3);
    }};
    public String evaluate(String statement) {
        try {
            statement = addSpaces(statement);
            String result = calculate(statement);
            result = result.replace(".0000","");
            return result;
        }
        catch (Exception e){
            return null;
        }

    }
    public static String calculate(String expression) {
        if (expression == null) return null;
        BigDecimal result;
        ArrayList<String> postFixExpression;
        try {
            postFixExpression = (ArrayList<String>) toPostfix(expression);
            if (postFixExpression == null) return null;
        } catch (Exception e) {
            return null;
        }
        result = calculatePostFixExpression(postFixExpression);

        if (result != null) {
            result = result.setScale(4, BigDecimal.ROUND_HALF_UP);
            Double d = result.doubleValue();
            String str = d.toString();
            str=insignificantZeros(str);
            return str;
        } else return null;
    }

    private static String insignificantZeros(String str){
        if (str.substring(str.length()-2).equals(".0")){
            return str.substring(0,str.length()-2);
        }
        else return str;
    }
    private static List<String> toPostfix(String expression) throws Exception {
        expression = replaceSingleMinus(expression);

        Stack<String> stack = new Stack();
        ArrayList<String> elements = (ArrayList<String>) splitToParts(expression);
        ArrayList<String> postFixElements = new ArrayList<>();

        for (String element : elements) {
            if (!Pattern.matches("\\(|\\)|\\+|-|\\*|/|<", element)) {
                postFixElements.add(element);
            } else {
                if (element.equals("("))
                    stack.push(element);
                else if (element.equals(")")) {
                    while (true) {
                        String elementFromStack;
                        try {
                            elementFromStack = stack.pop();
                        } catch (EmptyStackException e) {
                            return null;
                        }
                        if (elementFromStack.equals("("))
                            break;
                        else
                            postFixElements.add(elementFromStack);
                    }
                } else {
                    if (stack.isEmpty())
                        stack.push(element);
                    else {
                        while (true) {
                            if (stack.isEmpty()) {
                                stack.push(element);
                                break;
                            } else {
                                try {
                                    if (isHigherPriority(element, stack.peek())) {
                                        stack.push(element);
                                        break;
                                    } else {
                                        postFixElements.add(stack.pop());
                                    }
                                } catch (EmptyStackException e) {
                                    return null;
                                }
                            }
                        }
                    }
                }
            }
        }
        while (!stack.isEmpty()) {
            postFixElements.add(stack.pop());
        }
        return postFixElements;
    }


    private static BigDecimal calculatePostFixExpression(List<String> expression) {
        BigDecimal result;
        Stack<BigDecimal> decimals = new Stack<>();

        for (String element : expression) {
            //перебираем эллементы выражения
            if (Pattern.matches("\\(|\\)|\\+|-|\\*|/", element)) {
                BigDecimal decimal1 = null, decimal2 = null;
                try {
                    decimal1 = decimals.pop();
                    decimal2 = decimals.pop();
                } catch (EmptyStackException e) {
                    return null;
                }

                if (element.equals("+")) {
                    decimals.push(decimal2.add(decimal1));
                } else if (element.equals("-")) {
                    decimals.push(decimal2.subtract(decimal1));
                } else if (element.equals("*")) {
                    decimals.push(decimal2.multiply(decimal1));
                } else if (element.equals("/")) {
                    try {
                        decimals.push(decimal2.divide(decimal1, MathContext.DECIMAL32));
                    } catch (ArithmeticException e) {
                        return null;
                    }

                }
            } else if (element.equals("<")) {
                BigDecimal decimal = null;
                try {
                    decimal = decimals.pop();
                } catch (EmptyStackException e) {
                    return null;
                }
                decimals.push(decimal.multiply(new BigDecimal("-1")));
            } else {
                BigDecimal decimal;
                try {
                    decimal = new BigDecimal(element);
                } catch (NumberFormatException e) {
                    return null;
                }
                decimals.push(decimal);
            }
        }

        result = decimals.pop();
        if (decimals.isEmpty())
            return result;
        else
            return null;
    }

    private static boolean isHigherPriority(String element1, String element2) {
        return priorityMap.get(element1) > priorityMap.get(element2);
    }

    private static List<String> splitToParts(String expression) throws Exception {
        ArrayList<String> partsOfExpression = new ArrayList<>();

        expression = expression.replaceAll(" ", "");
        String[] chars = expression.split("");

        String bufferForNumber = "";
        for (int i = 0; i < chars.length; i++) {
            if (!"".equals(chars[i]) && Pattern.matches("\\(|\\)|\\+|-|\\*|/|<", chars[i])) {

                if (!bufferForNumber.equals("")) {
                    partsOfExpression.add(bufferForNumber);
                    bufferForNumber = "";
                }
                partsOfExpression.add(chars[i]);
            } else if (Pattern.matches("[\\d.]", String.valueOf(chars[i]))) {
                bufferForNumber += chars[i];
            } else {
                if (!"".equals(chars[i]))
                    throw new Exception();
            }
        }

        if (!bufferForNumber.equals("")) partsOfExpression.add(bufferForNumber);

        return partsOfExpression;
    }

    private static String replaceSingleMinus(String expression) {
        String res = expression.replaceAll("^-d*", "<");
        res = res.replaceAll("\\(-", "(<");
        return res;
    }

    public static String addSpaces(String statement) throws Exception {
        List<Character> charsList = new ArrayList<>();


        for (char ch : statement.toCharArray()) {

            charsList.add(ch);
        }
        for (int i = 0; i < charsList.size(); i++) {
            char ch = charsList.get(i);
            if (ch >= 48 && ch <= 57 || ch == 46) {
                continue;
            } else if(ch==40 || ch==41 || ch==43|| ch==45  || ch==47|| ch==42){
                charsList.add(i, ' ');
                i++;
                charsList.add(i + 1, ' ');
                i++;
            }
            else {
                throw new Exception();
            }
        }
        statement = charsList.toString();
        statement = statement.replaceAll(",", "");
        statement = statement.replace("[", "");
        statement = statement.replace("]", "");
        return statement;
    }

}
