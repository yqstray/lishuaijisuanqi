package com.study.calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class BaseCalculator {
    private final char[] operSet = {'+', '-', '*', '/', '(', ')', '#'};

    private final Map<Character, Integer> operMap = new HashMap<Character, Integer>() {{
        put('+', 0);
        put('-', 1);
        put('*', 2);
        put('/', 3);
        put('(', 4);
        put(')', 5);
        put('#', 6);
    }};

    private final char[][] operPrior = {
            /* (o1,o2)  +    -    *    /    (    )    # */
            /*  +  */ {'>', '>', '<', '<', '<', '>', '>'},
            /*  -  */ {'>', '>', '<', '<', '<', '>', '>'},
            /*  *  */ {'>', '>', '>', '>', '<', '>', '>'},
            /*  /  */ {'>', '>', '>', '>', '<', '>', '>'},
            /*  (  */ {'<', '<', '<', '<', '<', '=', ' '},
            /*  )  */ {'>', '>', '>', '>', ' ', '>', '>'},
            /*  #  */ {'<', '<', '<', '<', '<', ' ', '='},
    };

    //返回2个运算符优先级比较的结果'<','=','>'
    private char getPrior(char oper1, char oper2) {
        return operPrior[operMap.get(oper1)][operMap.get(oper2)]; //Map.get方法获取运算符的下标
    }

    //简单四则运算
    private double operate(double a, char oper, double b) {
        switch (oper) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    return Double.MAX_VALUE; //处理异常
                }
                return a / b;
            default:
                return 0;
        }
    }


    private double calSubmath(String math) {
        try {
            if (math.length() == 0) {
                return Double.MAX_VALUE;
            } else {

                if (!hasOper(math.substring(1, math.length())) || math.contains("E-")) {

                    return Double.parseDouble(math);
                }


                int flag = 0;
                if (math.charAt(0) == '-') {
                    flag = 1;
                    math = math.substring(1, math.length());
                }

                Stack<Character> operStack = new Stack<>();
                Stack<Double> numStack = new Stack<>();

                operStack.push('#');
                math += "#";

                String tempNum = "";


                for (int i = 0; i < math.length(); i++) {

                    char charOfMath = math.charAt(i);


                    if (!isOper(charOfMath)
                            || charOfMath == '-' && math.charAt(i - 1) == '(') {
                        tempNum += charOfMath;


                        i++;
                        charOfMath = math.charAt(i);

                        //1.2 判断下一个char是不是oper,如果是oper，就将num压入numStack
                        if (isOper(charOfMath)) {   //此条件成功时，下次for循环就直接跳到else语句了
                            double num = Double.parseDouble(tempNum);
                            if (flag == 1) {        //恢复math首位的负数
                                num = -num;
                                flag = 0;
                            }
                            numStack.push(num); //push num
                            tempNum = ""; //重置tempNum
                        }

                        //1.3 //回退，以免下次循环for语句自身的i++使得跳过了这个char
                        i--;
                    }


                    //(2)oper进栈
                    else {

                        switch (getPrior(operStack.peek(), charOfMath)) {//peek()函数返回栈顶的元素，但不弹出该栈顶元素。

                            //2.1 栈顶oper优先级低，新oper入栈
                            case '<':
                                operStack.push(charOfMath);
                                break;

                            //2.2 说明当前的charOfMath为')'，而栈顶oper为'('，去掉'('，其实也是math去括号的过程
                            case '=':
                                operStack.pop();//pop()函数返回栈顶的元素，并且将该栈顶元素出栈。
                                break;

                            //2.3 栈顶oper优先级高，oper出栈，并将num运算结果push进numStack
                            case '>':
                                char oper = operStack.pop();
                                double b = numStack.pop();
                                double a = numStack.pop();
                                if (operate(a, oper, b) == Double.MAX_VALUE)
                                    return Double.MAX_VALUE;
                                numStack.push(operate(a, oper, b));
                                i--; //继续比较该oper与栈顶oper的关系
                                break;
                        }
                    }
                }
                return numStack.peek(); //最后的math变成一个num了
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return Double.MAX_VALUE;
        }
    }

    //计算math，添加了一些特殊math的处理
    double cal(String math) {
        try {
            if (math.length() == 0) { //处理异常
                return Double.MAX_VALUE;
            } else {
                //运算式只是数字的特征：从第2个char开始math中没有oper
                //式子里只有数字的情况
                if (!hasOper(math.substring(1, math.length())) || math.contains("E-")) {
                    return Double.parseDouble(math);
                }
                //普通运算
                else {
                    return calSubmath(math);
                }
            }
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }

    //判断String中是否有运算符
    private boolean hasOper(String s) {
        return s.contains("+") || s.contains("-") || s.contains("*") || s.contains("/");
    }

    //判断字符是否为运算符
    boolean isOper(char c) {
        int i;
        for (i = 0; i < operSet.length; i++) {
            if (c == operSet[i]) {
                break;
            }
        }
        //break出来，说明是oper，i != operSize
        return i != operSet.length;
    }


}
