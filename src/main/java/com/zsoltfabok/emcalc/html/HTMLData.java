package com.zsoltfabok.emcalc.html;

import java.io.*;
import java.text.*;
import java.util.*;

// Referenced classes of package html:
//            NamedData

public class HTMLData
{

    public HTMLData()
    {
        buffer = new StringBuffer();
    }

    public void writeToHTMLFile(String filename, Vector vectorGasContent, Vector vectorBoilers, Vector vectorOthers, Double consuption[][])
        throws IOException
    {
        NumberFormat numberFormatter = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        buffer = new StringBuffer();
        add("<html>");
        add("<head><title>EmCalc v1.0 Beta1 HTML Kimenet</title></head>");
        add("<body>");
        add("<center><table border=\"0\" cellspacing=\"5\" cellpadding=\"5\" width=\"75%\">");
        add("<tr><td align=\"center\" valign=\"top\" width=\"50%\" rowspan=\"2\">");
        add("<table border=\"1\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\">");
        add("<caption><b>G\341z\366sszet\351tel (tf%)</b></caption>");
        for(int i = 0; i < vectorGasContent.size(); i++)
        {
            String gas = ((NamedData)vectorGasContent.get(i)).getName();
            gas = convertComponent(gas);
            buffer.append("<tr><td><b>" + gas + "</b></td><td align=\"right\" width=\"20%\">" + numberFormatter.format(new Double(((NamedData)vectorGasContent.get(i)).getData())) + "</td></tr>");
        }

        add("</table></td><td align=\"center\" valign=\"top\" width=\"50%\">");
        add("<table border=\"1\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\">");
        add("<caption><b>Kaz\341n darabsz\341mok</b></caption>");
        for(int i = 0; i < vectorBoilers.size(); i++)
            add("<tr><td><b>" + ((NamedData)vectorBoilers.get(i)).getName() + "</b></td><td align=\"right\" width=\"20%\">" + ((NamedData)vectorBoilers.get(i)).getData() + "</td></tr>");

        add("</table></td></tr><tr><td align=\"center\" valign=\"bottom\" width=\"50%\">");
        add("<table border=\"1\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\">");
        add("<caption><b>Tov\341bbi param\351terek</b></caption>");
        for(int i = 0; i < vectorOthers.size(); i++)
            add("<tr><td><b>" + ((NamedData)vectorOthers.get(i)).getName() + "</b></td><td align=\"right\" width=\"20%\">" + ((NamedData)vectorOthers.get(i)).getData() + "</td></tr>");

        add("</table></td></tr><tr><td colspan=\"2\">");
        add("<table border=\"1\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\">");
        add("<caption><b>G\341z kibocs\341jt\341s</b></caption>");
        add("<tr><td align=\"center\" width=\"20%\"><u>T\355pus</u></td><td align=\"center\"><b>CO<sub>2</sub></b>&nbsp;(m<sup>3</sup>)</td><td align=\"center\"><b>H<sub>2</sub>O</b>&nbsp;(m<sup>3</sup>)</td><td align=\"center\"><b>O<sub>2</sub></b>&nbsp;(m<sup>3</sup>)</td><td align=\"center\"><b>N<sub>2</sub></b>&nbsp;(m<sup>3</sup>)</td></tr>");
        for(int i = 0; i < vectorBoilers.size(); i++)
            add("<tr><td><b>" + ((NamedData)vectorBoilers.get(i)).getName() + "</b></td><td align=\"right\">" + numberFormatter.format(consuption[i][0]) + "</td><td align=\"right\">" + numberFormatter.format(consuption[i][1]) + "</td><td align=\"right\">" + numberFormatter.format(consuption[i][2]) + "</td><td align=\"right\">" + numberFormatter.format(consuption[i][3]) + "</td></tr>");

        add("<tr bgcolor=\"lightgray\"><td><b>\326sszesen</b></td><td align=\"right\">" + numberFormatter.format(consuption[5][0]) + "</td><td align=\"right\">" + numberFormatter.format(consuption[5][1]) + "</td><td align=\"right\">" + numberFormatter.format(consuption[5][2]) + "</td><td align=\"right\">" + numberFormatter.format(consuption[5][3]) + "</td></tr>");
        Date now = new Date();
        Format formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm", Locale.ENGLISH);
        add("</table></td></tr><tr><td align=\"right\" colspan=\"2\"><i><font size=\"-1\">Generated with EmCalc version 1.0 Beta1 on " + formatter.format(now) + "</i></font></td></tr></table>");
        add("</center>\n</body>\n</html>");
        stringToFile(buffer.toString(), filename);
    }

    private void add(String row)
    {
        buffer.append(row + '\n');
    }

    private void stringToFile(String str, String filename)
        throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(filename));
        out.write(str);
        out.close();
    }

    private String convertComponent(String component)
    {
        StringBuffer newComponent = new StringBuffer();
        int numberIndex = 0;
        for(int i = 0; i < component.length(); i++)
        {
            String charValue = component.substring(i, i + 1);
            if(charValue.matches("[0-9]"))
            {
                if(numberIndex == 0)
                    numberIndex = i;
            } else
            if(!charValue.matches("[0-9]"))
            {
                if(numberIndex != 0)
                {
                    newComponent.append("<sub>" + component.substring(numberIndex, i) + "</sub>");
                    numberIndex = 0;
                }
                newComponent.append(charValue);
            }
        }

        if(numberIndex != 0)
            newComponent.append("<sub>" + component.substring(numberIndex) + "</sub>");
        return newComponent.toString();
    }

    private StringBuffer buffer;
}
