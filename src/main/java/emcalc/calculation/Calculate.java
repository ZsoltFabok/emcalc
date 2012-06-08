package emcalc.calculation;

import java.math.BigDecimal;
import java.util.Stack;
import java.util.Vector;

public class Calculate
{

    public Calculate()
    {
    }

    public static double round(double value)
    {
        return (new BigDecimal(value)).setScale(2, 4).doubleValue();
    }

    public static double evaluate(String expression)
    {
        StringBuffer buffer = new StringBuffer(expression.replaceAll("\\s", ""));
        boolean insert = false;
        for(int i = 0; i < buffer.length(); i++)
            if((i == 0 || buffer.charAt(i - 1) != ')' && Operator.isOperator(buffer.charAt(i - 1))) && buffer.charAt(i) == '-')
            {
                buffer.insert(i, "(0");
                insert = true;
                i += 2;
            } else
            if(insert && Operator.isOperator(buffer.charAt(i)))
            {
                buffer.insert(i, ")");
                insert = false;
            }

        expression = buffer.toString() + ')';
        Stack stack = new Stack();
        stack.push(new Operator('('));
        Vector output = new Vector(1, 1);
        buffer = new StringBuffer();
        for(int k = 0; k < expression.length(); k++)
            if(Operator.isOperator(expression.charAt(k)))
            {
                if(buffer.length() != 0)
                    output.add(new Double(buffer.toString()));
                Operator op = new Operator(expression.charAt(k));
                boolean ready = true;
                while(ready)
                {
                    int stackPrecedence = ((Operator)stack.peek()).getStackPrecedence();
                    int inputPrecedence = op.getInputPrecedence();
                    if(stackPrecedence >= inputPrecedence)
                    {
                        Operator tmp = (Operator)stack.pop();
                        if(tmp.getOperator() != '(')
                            output.add(tmp);
                        else
                            ready = false;
                    } else
                    {
                        if(op.getOperator() != ')')
                            stack.push(op);
                        ready = false;
                    }
                }
                buffer = new StringBuffer();
            } else
            {
                buffer.append(expression.charAt(k));
            }

        for(int i = 0; i < output.size(); i++)
            if(output.get(i) instanceof Double)
            {
                stack.push((Double)output.get(i));
            } else
            {
                Double operand2 = (Double)stack.pop();
                Double operand1 = (Double)stack.pop();
                stack.push(((Operator)output.get(i)).calculate(operand1, operand2));
            }

        return ((Double)stack.pop()).doubleValue();
    }
}
