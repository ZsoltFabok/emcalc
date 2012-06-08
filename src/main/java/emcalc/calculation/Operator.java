package emcalc.calculation;


public class Operator
{

    public Operator(char operator)
    {
        switch(operator)
        {
        case 41: // ')'
            stackPrecedence = -1;
            inputPrecedence = 0;
            break;

        case 40: // '('
            stackPrecedence = 0;
            inputPrecedence = 7;
            break;

        case 43: // '+'
            stackPrecedence = 2;
            inputPrecedence = 1;
            break;

        case 45: // '-'
            stackPrecedence = 2;
            inputPrecedence = 1;
            break;

        case 42: // '*'
            stackPrecedence = 4;
            inputPrecedence = 3;
            break;

        case 47: // '/'
            stackPrecedence = 4;
            inputPrecedence = 3;
            break;

        case 94: // '^'
            stackPrecedence = 6;
            inputPrecedence = 5;
            break;
        }
        this.operator = operator;
    }

    public int getStackPrecedence()
    {
        return stackPrecedence;
    }

    public int getInputPrecedence()
    {
        return inputPrecedence;
    }

    public char getOperator()
    {
        return operator;
    }

    public Double calculate(Double operand1, Double operand2)
    {
        switch(operator)
        {
        case 43: // '+'
            return new Double(operand1.doubleValue() + operand2.doubleValue());

        case 45: // '-'
            return new Double(operand1.doubleValue() - operand2.doubleValue());

        case 42: // '*'
            return new Double(operand1.doubleValue() * operand2.doubleValue());

        case 47: // '/'
            return new Double(operand1.doubleValue() / operand2.doubleValue());

        case 94: // '^'
            return new Double(Math.pow(operand1.doubleValue(), operand2.doubleValue()));
        }
        return null;
    }

    public static boolean isOperator(char ch)
    {
        String operators = "()+-/*^";
        for(int j = 0; j < operators.length(); j++)
            if(ch == operators.charAt(j))
                return true;

        return false;
    }

    private char operator;
    private int stackPrecedence;
    private int inputPrecedence;
}
