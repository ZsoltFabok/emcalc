package com.zsoltfabok.emcalc.calculation;

import java.util.Enumeration;
import java.util.Hashtable;

// Referenced classes of package calculation:
//            Calculate, Interval

public class Check
{

    public Check()
    {
    }

    public static boolean isDouble(String number)
    {
        try
        {
            Double.parseDouble(number);
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    public static boolean checkBoilerCount(Hashtable boilers)
    {
        int count = 0;
        for(Enumeration keys = boilers.keys(); keys.hasMoreElements();)
        {
            String key = (String)keys.nextElement();
            count += Integer.parseInt((String)boilers.get(key));
        }

        return count != 0;
    }

    public static boolean checkGasContentPercentage(Hashtable gasContent)
    {
        double percentage = 0.0D;
        for(Enumeration keys = gasContent.keys(); keys.hasMoreElements();)
        {
            String key = (String)keys.nextElement();
            percentage += Double.parseDouble((String)gasContent.get(key));
        }

        percentage = Calculate.round(percentage);
        return percentage == 100D;
    }

    public static boolean isInteger(String number)
    {
        try
        {
            Integer.parseInt(number);
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    public static boolean isValidGasValue(String number)
    {
        Interval interval = new Interval(0.0D, 1.7976931348623157E+308D);
        return isDouble(number) && interval.inInterval(Double.parseDouble(number));
    }

    public static boolean isValidBoilerNumber(String number)
    {
        Interval interval = new Interval(0.0D, 1.7976931348623157E+308D);
        return isInteger(number) && interval.inInterval(Double.parseDouble(number));
    }

    public static boolean isValidTemperature(String number)
    {
        Interval interval = new Interval(-15D, 15D);
        return isDouble(number) && interval.inInterval(Double.parseDouble(number));
    }

    public static boolean isValidAirFactor(String number)
    {
        Interval interval = new Interval(0.0D, 1.7976931348623157E+308D);
        return isDouble(number) && interval.inInterval(Double.parseDouble(number));
    }
}
