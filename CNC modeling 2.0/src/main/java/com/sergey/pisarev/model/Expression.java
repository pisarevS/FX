package com.sergey.pisarev.model;

import java.util.Stack;

public class Expression {
    //Метод возвращает true, если проверяемый символ - разделитель ("пробел" или "равно")
    private static boolean isDelimeter(char c) {
        return (" =".indexOf(c) != -1);
    }

    //Метод возвращает true, если проверяемый символ - оператор
    private static boolean isOperator(char с) {
        return ("+-/*^()".indexOf(с) != -1);
    }

    //Метод возвращает приоритет оператора
    private static byte getPriority(char s) {
        switch (s) {
            case '(':
                return 0;
            case ')':
                return 1;
            case '+':
                return 2;
            case '-':
                return 3;
            case '*':
            case '/':
                return 4;
            case '^':
                return 5;
            default:
                return 6;
        }
    }

    //"Входной" метод класса
    public float calculate(String input) {
        input = insertZero(input);
        String output = getExpression(input); //Преобразовываем выражение в постфиксную запись
        return counting(output); //Возвращаем результат
    }

    private String insertZero(String input) {
        StringBuilder sb = new StringBuilder(input);
        if (input.contains("-") && sb.charAt(0) == '-') {
            sb = sb.replace(0, 0, "0");
        } else if (input.contains("-") && sb.indexOf("-") != 0) {
            for (int i = 0; i < sb.length(); i++) {
                if (sb.charAt(i) == '-') {
                    if (sb.charAt(i - 1) == '(' || sb.charAt(i - 1) == '+' || sb.charAt(i - 1) == '-') {
                        sb = sb.replace(i, i, "0");
                    }
                }
            }
        } else if (input.contains("+") && sb.indexOf("+") != 0) {
            for (int i = 0; i < sb.length(); i++) {
                if (sb.charAt(i) == '+') {
                    if (sb.charAt(i - 1) == '(' || sb.charAt(i - 1) == '+' || sb.charAt(i - 1) == '-') {
                        sb = sb.replace(i, i, "0");
                    }
                }
            }
        }
        return sb.toString();
    }

    private static String getExpression(String input) {
        StringBuilder output = new StringBuilder(); //Строка для хранения выражения
        Stack<Character> operStack = new Stack<>(); //Стек для хранения операторо
        char[] inputArr = input.toCharArray();

        for (int i = 0; i < inputArr.length; i++) //Для каждого символа в входной строке
        {
            //Разделители пропускаем
            if (isDelimeter(inputArr[i]))
                continue; //Переходим к следующему символу

            //Если символ - цифра, то считываем все число
            if (Character.isDigit(inputArr[i])) //Если цифра
            {
                //Читаем до разделителя или оператора, что бы получить число
                while (!isDelimeter(inputArr[i]) && !isOperator(inputArr[i])) {
                    output.append(inputArr[i]); //Добавляем каждую цифру числа к нашей строке
                    i++; //Переходим к следующему символу

                    if (i == inputArr.length) break; //Если символ - последний, то выходим из цикла
                }

                output.append(" "); //Дописываем после числа пробел в строку с выражением
                i--; //Возвращаемся на один символ назад, к символу перед разделителем
            }

            //Если символ - оператор
            if (isOperator(inputArr[i])) //Если оператор
            {
                if (inputArr[i] == '(') //Если символ - открывающая скобка
                    operStack.push(inputArr[i]); //Записываем её в стек
                else if (inputArr[i] == ')') //Если символ - закрывающая скобка
                {
                    //Выписываем все операторы до открывающей скобки в строку
                    char s = operStack.pop();

                    while (s != '(') {
                        output.append(s).append(" ");
                        s = operStack.pop();
                    }
                } else //Если любой другой оператор
                {
                    if (operStack.size() > 0) //Если в стеке есть элементы
                        if (getPriority(inputArr[i]) <= getPriority(operStack.peek())) //И если приоритет нашего оператора меньше и
                            //ли равен приоритету оператора на вершине стека
                            output.append(operStack.pop()).append(" "); //То добавляем последний оператор из стека в строку с выражением

                    operStack.push(inputArr[i]); //Если стек пуст, или же приоритет оператора выше - добавляем операторов на вершину стека
                }
            }
        }
        //Когда прошли по всем символам, выкидываем из стека все оставшиеся там операторы в строку
        while (operStack.size() > 0)
            output.append(operStack.pop()).append(" ");
        return output.toString(); //Возвращаем выражение в постфиксной записи
    }

    private static float counting(String input) {
        char[] inputArr = input.toCharArray();
        float result = 0; //Результат
        Stack<Float> temp = new Stack<>(); //Dhtvtyysq стек для решения
        for (int i = 0; i < input.length(); i++) //Для каждого символа в строке
        {
            //Если символ - цифра, то читаем все число и записываем на вершину стека
            if (Character.isDigit(inputArr[i])) {
                StringBuilder a = new StringBuilder();
                while (!isDelimeter(inputArr[i]) && !isOperator(inputArr[i])) //Пока не разделитель
                {
                    a.append(inputArr[i]); //Добавляем
                    i++;
                    if (i == input.length()) break;
                }
                temp.push(Float.parseFloat(a.toString())); //Записываем в стек
                i--;
            } else if (isOperator(inputArr[i])) //Если символ - оператор
            {
                //Берем два последних значения из стека
                float a = temp.pop();
                float b = temp.pop();
                switch (inputArr[i]) //И производим над ними действие, согласно оператору
                {
                    case '+':
                        result = b + a;
                        break;
                    case '-':
                        result = b - a;
                        break;
                    case '*':
                        result = b * a;
                        break;
                    case '/':
                        result = b / a;
                        break;
                    case '^':
                        result = (float) Math.pow(b, a);
                        break;
                }
                temp.push(result); //Результат вычисления записываем обратно в стек
            }
        }
        return temp.peek(); //Забираем результат всех вычислений из стека и возвращаем его
    }
}
