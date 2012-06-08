package emcalc.calculation;

import java.util.Enumeration;
import java.util.Hashtable;

import emcalc.exception.NotInDomainException;

// Referenced classes of package calculation:
//            Interval, Calculate

public class Function
{

    public Function()
    {
        intervals = new Hashtable();
    }

    public void addInterval(Interval interval, String function)
    {
        intervals.put(interval, function);
    }

    public double getValue(double x)
        throws NotInDomainException
    {
        Enumeration keys = intervals.keys();
        boolean found = false;
        Interval actualInterval = null;
        while(keys.hasMoreElements() && !found)
        {
            actualInterval = (Interval)keys.nextElement();
            if(actualInterval.inInterval(x))
                found = true;
        }
        double value;
        if(found)
        {
            String function = (String)intervals.get(actualInterval);
            function = function.replaceAll("x", "(" + (new Double(x)).toString() + ")");
            value = Calculate.evaluate(function);
        } else
        {
            throw new NotInDomainException(x);
        }
        return value;
    }

    private Hashtable intervals;
}
