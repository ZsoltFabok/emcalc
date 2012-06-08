package emcalc.calculation;

import java.util.Hashtable;

import emcalc.exception.NotInDomainException;

// Referenced classes of package calculation:
//            Interval, Function, Expressions, Calculate

public class Consuption
{

    public Consuption()
    {
        hashExpressions = new Hashtable();
        hashExpressions.put("E1", "(2*CH4+3.5*C3H8+5*C3H8+6.5*C4H10+8*C5H12+9.5*C6H14+11*C7H16+12.5*C8H18)/100");
        hashExpressions.put("E2", "(100/21)*E1");
        hashExpressions.put("E3", "n * E2");
        hashExpressions.put("E4", "(CH4+2*C2H6+3*C3H8+4*C4H10+5*C5H12+6*C6H14+7*C7H16+8*C8H18)/100+CO2/100");
        hashExpressions.put("E5", "(2*CH4+3*C2H6+4*C3H8+5*C4H10+6*C5H12+7*C6H14+8*C7H16+9*C8H18)/100");
        hashExpressions.put("E6", "E1*(n-1)");
        hashExpressions.put("E7", "N2/100+E1*n*3.76");
        hashExpressions.put("E8", "E4 + E5 + E6 + E7");
        hashExpressions.put("E9", "E8 - E5");
        hashExpressions.put("E10", "(CH4/100)*35795+(C2H6/100)*64315+(C3H6/100)*93575+(C4H10/100)*123550+(C5H12/100)*144076+(C6H14/100)*171739+(C7H16/100)*198938+(C8H18/100)*224023");
        hashBoilerConsuption = new Hashtable();
        Interval interval1 = new Interval(-15D, -5D);
        interval1.setRightOpened();
        Interval interval2 = new Interval(-5D, 0.0D);
        interval2.setRightOpened();
        Interval interval3 = new Interval(0.0D, 15D);
        Function functionFegC18 = new Function();
        functionFegC18.addInterval(interval1, "-7.5644*x^2 - 226.99*x - 270.91");
        functionFegC18.addInterval(interval2, "0.0329*x^5-0.0655*x^4-7.9692*x^3+16.96*x^2+78.234*x+215");
        functionFegC18.addInterval(interval3, "-0.0577*x^3+2.249*x^2-34.752*x+211.35");
        Function functionFegC21 = new Function();
        functionFegC21.addInterval(interval1, "-15.66*x^2 - 427.7*x - 1187");
        functionFegC21.addInterval(interval2, "-5.0216*x^5 - 66.227*x^4 - 305.72*x^3 - 555.35*x^2 - 352.83*x + 220");
        functionFegC21.addInterval(interval3, "-0.0283*x^3 + 1.0365*x^2 - 22.692*x + 200.2");
        Function functionFegC24 = new Function();
        functionFegC24.addInterval(interval1, "-10.008*x^2 - 332.22*x - 822.46");
        functionFegC24.addInterval(interval2, "-7.2767*x^5 - 98.377*x^4 - 460.74*x^3 - 838.97*x^2 - 506.33*x + 246");
        functionFegC24.addInterval(interval3, "-0.0324*x^3 + 0.7998*x^2 - 19.746*x + 222.93");
        Function functionFegC30 = new Function();
        functionFegC30.addInterval(interval1, "-7.5644*x^2 - 226.99*x - 270.91");
        functionFegC30.addInterval(interval2, "0.0329*x^5-0.0655*x^4-7.9692*x^3+16.96*x^2+78.234*x+215");
        functionFegC30.addInterval(interval3, "-0.0577*x^3+2.249*x^2-34.752*x+211.35");
        Function functionTermoteka25 = new Function();
        functionTermoteka25.addInterval(interval1, "-7.5644*x^2 - 226.99*x - 270.91");
        functionTermoteka25.addInterval(interval2, "0.0329*x^5-0.0655*x^4-7.9692*x^3+16.96*x^2+78.234*x+215");
        functionTermoteka25.addInterval(interval3, "-0.0577*x^3+2.249*x^2-34.752*x+211.35");
        hashBoilerConsuption.put("Feg C18", functionFegC18);
        hashBoilerConsuption.put("Feg C21", functionFegC21);
        hashBoilerConsuption.put("Feg C24", functionFegC24);
        hashBoilerConsuption.put("Feg C30", functionFegC30);
        hashBoilerConsuption.put("Termoteka 25", functionTermoteka25);
    }

    public Double[][] getConsuption(Hashtable gasContent, Hashtable boilers, double temperature, double airFactor)
    {
        Expressions expressions = new Expressions(hashExpressions, gasContent, airFactor);
        String columnExpressions[] = {
            "E4", "E5", "E6", "E7"
        };
        String boilerTypes[] = {
            "Feg C18", "Feg C21", "Feg C24", "Feg C30", "Termoteka 25"
        };
        Double consuptions[][] = new Double[5][4];
        for(int i = 0; i < 5; i++)
            try
            {
                double consuption = ((Function)hashBoilerConsuption.get(boilerTypes[i])).getValue(temperature);
                double localConstant = (double)Integer.parseInt((String)boilers.get(boilerTypes[i])) * consuption;
                for(int j = 0; j < columnExpressions.length; j++)
                {
                    double value = Calculate.round(localConstant * expressions.getResult(columnExpressions[j]));
                    consuptions[i][j] = new Double(value);
                }

            }
            catch(NotInDomainException e)
            {
                e.printStackTrace();
            }

        return consuptions;
    }

    private Hashtable hashExpressions;
    private Hashtable hashBoilerConsuption;
}
