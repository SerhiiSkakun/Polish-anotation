package main;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


public class Calculator {
    public static void main(String[] args) {
        String fileSource = "src/source.txt";
        String fileResult = "src/result.txt";

        List<String> listSource = readFile(fileSource);
        List<String> listResult = new ArrayList<>();
        ExpressionParser expressionParser = new ExpressionParser();
        for (String expression : listSource) { //по всем строкам считанного файла
            if(!expression.isEmpty()) {
                List<String> conversionExpressionParsed = expressionParser.parse(expression); //преобразовываем
                if (expressionParser.isAllOk) {
//                    for (String conversionExpressionElement : conversionExpressionParsed) {
//                        System.out.print(conversionExpressionElement + " ");
//                    }
//                    System.out.println();
//                    System.out.println(calculate(conversionExpressionParsed));
                    listResult.add(calculate(conversionExpressionParsed)); //считаем
                } else {
//                    System.out.println(expressionParser.errorMessage);
                    listResult.add(expressionParser.errorMessage);
                }
            }
        }
        writeFile(fileResult, listResult);
    }

    public static String calculate(List<String> conversionExpressionParsed) {
        Deque<Double> stack = new ArrayDeque<>();
        for (String conversionExpressionElement : conversionExpressionParsed) {
            if (conversionExpressionElement.equals("+")) stack.push(stack.pop() + stack.pop());
            else if (conversionExpressionElement.equals("-")) {
                Double b = stack.pop(), a = stack.pop();
                stack.push(a - b);
            }
            else if (conversionExpressionElement.equals("*")) stack.push(stack.pop() * stack.pop());
            else if (conversionExpressionElement.equals("/")) {
                Double b = stack.pop(), a = stack.pop();
                if(b != 0) {
                    stack.push(a / b);
                } else{
                    return "Деление на ноль";
                }
            }
            else if (conversionExpressionElement.equals("u-")) stack.push(-stack.pop());
            else stack.push(Double.valueOf(conversionExpressionElement));
        }
        return stack.pop().toString();
    }

    public static List<String> readFile (String file){
        List<String> list = new ArrayList<>();
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))){
                while(reader.ready()){
                    list.add(reader.readLine());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private static void writeFile(String fileResult, List<String> list) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileResult))){
            writer.write(String.join(System.getProperty("line.separator"), list));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}