package main;

import java.util.*;

class ExpressionParser {
    private static String operators = "+-*/";
    private static String delimiters = "() " + operators;
    public static boolean isAllOk = true;
    public static String errorMessage;

    private static boolean isDelimiter(String token) { //если оператор или скобки
        if (token.length() != 1) return false;
        for (int i = 0; i < delimiters.length(); i++) {
            if (token.charAt(0) == delimiters.charAt(i)) return true;
        }
        return false;
    }

    private static boolean isOperator(String token) { //если оператор или унарный минус (не скобки)
        if(!token.equals("")) {
            if (token.equals("u-")) return true;
            for (int i = 0; i < operators.length(); i++) {
                if (token.charAt(0) == operators.charAt(i)) return true;
            }
        }
        return false;
    }

    private static int priority(String token) {
        if (token.equals("(")) return 1;
        if (token.equals("+") || token.equals("-")) return 2;
        if (token.equals("*") || token.equals("/")) return 3;
        return 4;
    }

    private static void checkErrors (String prev, String curr, Deque<String> stack){
        if (isOperator(curr) && isOperator(prev)){ //если 2 оператора подряд
            if(!"-".equals(curr)) { //2 оператора с последним минусом пропускаем
                errorMessage = "Введены 2 оператора подряд";
                isAllOk = false;
            }
            else{
                if(isStackHasTooOperators(stack)) { //если перед минусом есть 2 оператора
                    errorMessage = "Введены 2 оператора подряд";
                    isAllOk = false;
                }
            }
        }
        if(isOperator(curr) && "".equals(prev) && !curr.equals("-")){ //если выражение начинается с оператора
            errorMessage = "Выражение начинается с оператора";
            isAllOk = false;
        }
        if(!isDelimiter(curr))
        try {
            Double.parseDouble(curr); //если операнд не число
        } catch (NumberFormatException e) {
            errorMessage = "Введено не число";
            isAllOk = false;
        }
    }

    public static List<String> parse(String infix) {
        isAllOk = true;
        List<String> postfix = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        StringTokenizer tokenizer = new StringTokenizer(infix, delimiters, true); //перевели строку в токины (1 токен = 1 символ)
        String prev = "";
        String curr = "";
        while (tokenizer.hasMoreTokens()) {
            curr = tokenizer.nextToken();
            checkErrors(prev, curr, stack);
            if(!isAllOk) return postfix;
            if (!tokenizer.hasMoreTokens() && isOperator(curr)) { //если выражение заканчивается оператором
                errorMessage = "Выражение заканчивается оператором";
                isAllOk = false;
                return postfix;
            }
            if (curr.equals(" ")) continue; //натыкаемся на пробел, пропускаем
            if (isDelimiter(curr)) {
                if (curr.equals("(")) stack.push(curr); // если открывающая скобка, закидываем в стек
                else if (curr.equals(")")) { // если закрывающая скобка, вытаскиваем из стека всё до открывающейся скобки
                    if (!stack.isEmpty()) {
                        while (!stack.peek().equals("(")) {
                            postfix.add(stack.pop());
                            if (stack.isEmpty()) { //если есть закрывающаяся раньше открывающейся и стек не пустой
                                errorMessage = "Скобки не согласованы";
                                isAllOk = false;
                                return postfix;
                            }
                        }
                        stack.pop(); //после того, как всё извлекли, выкидываем открывающуюся скобку
                    } else { //если есть закрывающаяся раньше открывающейся и стек пустой
                        errorMessage = "Скобки не согласованы";
                        isAllOk = false;
                        return postfix;
                    }
                } else {
                    if (curr.equals("-") && (prev.equals("") || (isDelimiter(prev) && !prev.equals(")")))) {
                        // если унарный минус
                        curr = "u-";
                    } else { //если обычный оператор
                        while (!stack.isEmpty() && (priority(curr) <= priority(stack.peek()))) {
                            postfix.add(stack.pop()); //сравниваем приоритет текущего оператора с тем что в стеке
                        }
                    }
                    stack.push(curr);
                }
            } else { //если операнд, просто добавляем
                postfix.add(curr);
            }
            prev = curr;
        }

        while (!stack.isEmpty()) { //если разобрали всё выражение, операторы из стека переносим в коллекцию
            if (isOperator(stack.peek())) postfix.add(stack.pop());
            else { //не нашли отрывающейся скобки
                errorMessage = "Скобки не согласованы";
                isAllOk = false;
                return postfix;
            }
        }
        return postfix;
    }

    private static boolean isStackHasTooOperators(Deque<String> stack) { // Проверяет стек на 2 оператора подряд
        boolean has2Operators = false;
        String tempValue = stack.pop();
        if(!stack.isEmpty()){
            if(isOperator(stack.peek())) has2Operators = true;
        }
        stack.push(tempValue);
        return has2Operators;
    }
}