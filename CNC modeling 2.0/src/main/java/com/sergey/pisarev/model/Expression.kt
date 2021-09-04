package com.sergey.pisarev.model

import java.lang.StringBuilder
import java.util.*

object Expression {
    //Метод возвращает true, если проверяемый символ - разделитель ("пробел" или "равно")
    private fun isDelimeter(c: Char): Boolean {
        return " =".indexOf(c) != -1
    }

    //Метод возвращает true, если проверяемый символ - оператор
    private fun isOperator(с: Char): Boolean {
        return "+-/*^()".indexOf(с) != -1
    }

    //Метод возвращает приоритет оператора
    private fun getPriority(s: Char): Byte {
        return when (s) {
            '(' -> 0
            ')' -> 1
            '+' -> 2
            '-' -> 3
            '*', '/' -> 4
            '^' -> 5
            else -> 6
        }
    }

    //"Входной" метод класса
    fun calculate(input: String): Float {
        var input = input
        input = insertZero(input)
        val output = getExpression(input) //Преобразовываем выражение в постфиксную запись
        return counting(output) //Возвращаем результат
    }

    private fun insertZero(input: String): String {
        var sb = StringBuilder(input)
        if (input.contains("-") && sb[0] == '-') {
            sb = sb.replace(0, 0, "0")
        }
        if (input.contains("-") && sb.indexOf("-") != 0) {
            for (i in 0 until sb.length) {
                if (sb[i] == '-') {
                    if (sb[i - 1] == '(' || sb[i - 1] == '+' || sb[i - 1] == '-') {
                        sb = sb.replace(i, i, "0")
                    }
                }
            }
        }
        if (input.contains("+") && sb.indexOf("+") != 0) {
            for (i in 0 until sb.length) {
                if (sb[i] == '+') {
                    if (sb[i - 1] == '(' || sb[i - 1] == '+' || sb[i - 1] == '-') {
                        sb = sb.replace(i, i, "0")
                    }
                }
            }
        }
        return sb.toString()
    }

    private fun getExpression(input: String): String {
        val output = StringBuilder() //Строка для хранения выражения
        val operStack = Stack<Char>() //Стек для хранения операторо
        val inputArr = input.toCharArray()
        var i = 0
        while (i < inputArr.size) {

            //Разделители пропускаем
            if (isDelimeter(inputArr[i])) {
                i++
                continue  //Переходим к следующему символу
            }

            //Если символ - цифра, то считываем все число
            if (Character.isDigit(inputArr[i])) //Если цифра
            {
                //Читаем до разделителя или оператора, что бы получить число
                while (!isDelimeter(inputArr[i]) && !isOperator(inputArr[i])) {
                    output.append(inputArr[i]) //Добавляем каждую цифру числа к нашей строке
                    i++ //Переходим к следующему символу
                    if (i == inputArr.size) break //Если символ - последний, то выходим из цикла
                }
                output.append(" ") //Дописываем после числа пробел в строку с выражением
                i-- //Возвращаемся на один символ назад, к символу перед разделителем
            }

            //Если символ - оператор
            if (isOperator(inputArr[i])) //Если оператор
            {
                if (inputArr[i] == '(') //Если символ - открывающая скобка
                    operStack.push(inputArr[i]) //Записываем её в стек
                else if (inputArr[i] == ')') //Если символ - закрывающая скобка
                {
                    //Выписываем все операторы до открывающей скобки в строку
                    var s = operStack.pop()
                    while (s != '(') {
                        output.append(s).append(" ")
                        s = operStack.pop()
                    }
                } else  //Если любой другой оператор
                {
                    if (operStack.size > 0) //Если в стеке есть элементы
                        if (getPriority(inputArr[i]) <= getPriority(operStack.peek())) //И если приоритет нашего оператора меньше и
                        //ли равен приоритету оператора на вершине стека
                            output.append(operStack.pop()).append(" ") //То добавляем последний оператор из стека в строку с выражением
                    operStack.push(inputArr[i]) //Если стек пуст, или же приоритет оператора выше - добавляем операторов на вершину стека
                }
            }
            i++
        }
        //Когда прошли по всем символам, выкидываем из стека все оставшиеся там операторы в строку
        while (operStack.size > 0) output.append(operStack.pop()).append(" ")
        return output.toString() //Возвращаем выражение в постфиксной записи
    }

    private fun counting(input: String): Float {
        val inputArr = input.toCharArray()
        var result = 0f //Результат
        val temp = Stack<Float>() // стек для решения
        var i = 0
        while (i < input.length) {

            //Если символ - цифра, то читаем все число и записываем на вершину стека
            if (Character.isDigit(inputArr[i])) {
                val a = StringBuilder()
                while (!isDelimeter(inputArr[i]) && !isOperator(inputArr[i])) //Пока не разделитель
                {
                    a.append(inputArr[i]) //Добавляем
                    i++
                    if (i == input.length) break
                }
                temp.push(a.toString().toFloat()) //Записываем в стек
                i--
            } else if (isOperator(inputArr[i])) //Если символ - оператор
            {
                //Берем два последних значения из стека
                val a = temp.pop()
                val b = temp.pop()
                when (inputArr[i]) {
                    '+' -> result = b + a
                    '-' -> result = b - a
                    '*' -> result = b * a
                    '/' -> result = b / a
                    '^' -> result = Math.pow(b.toDouble(), a.toDouble()).toFloat()
                }
                temp.push(result) //Результат вычисления записываем обратно в стек
            }
            i++
        }
        return temp.peek() //Забираем результат всех вычислений из стека и возвращаем его
    }
}