package emcalc.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import emcalc.calculation.Check;
import emcalc.exception.InvalidConfigFormatException;
import emcalc.exception.InvalidDataException;

// Referenced classes of package xml:
//            XMLWriter

public class XMLData
{

    public XMLData(String filename)
        throws SAXParseException, SAXException, ParserConfigurationException, FileNotFoundException, IOException, InvalidDataException, InvalidConfigFormatException
    {
        load(filename);
        this.filename = filename;
    }

    public void reload()
        throws SAXParseException, SAXException, ParserConfigurationException, FileNotFoundException, IOException, InvalidDataException, InvalidConfigFormatException
    {
        load(filename);
    }

    private void load(String filename)
        throws SAXParseException, SAXException, ParserConfigurationException, FileNotFoundException, IOException, InvalidDataException, InvalidConfigFormatException
    {
        gasContent = new Hashtable();
        gasContent.put("CH4", "");
        gasContent.put("C2H6", "");
        gasContent.put("C3H8", "");
        gasContent.put("C4H10", "");
        gasContent.put("C5H12", "");
        gasContent.put("C6H14", "");
        gasContent.put("C7H16", "");
        gasContent.put("C8H18", "");
        gasContent.put("N2", "");
        gasContent.put("CO2", "");
        boilers = new Hashtable();
        boilers.put("Feg C18", "");
        boilers.put("Feg C21", "");
        boilers.put("Feg C24", "");
        boilers.put("Feg C30", "");
        boilers.put("Termoteka 25", "");
        temperature = null;
        airFactor = null;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.parse(new FileInputStream(new File(filename)));
        document.getDocumentElement().normalize();
        Element rootNode = document.getDocumentElement();
        Node nodeGasContents = null;
        Node nodeBoilers = null;
        Node nodeTemperature = null;
        Node nodeAirFactor = null;
        NodeList childNodes = rootNode.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); i++)
        {
            Node childNode = childNodes.item(i);
            if(childNode instanceof Element)
            {
                String name = childNode.getNodeName();
                if(name.equals("gasContents"))
                    nodeGasContents = childNode;
                else
                if(name.equals("boilers"))
                    nodeBoilers = childNode;
                else
                if(name.equals("temperature") && !childNode.hasChildNodes())
                    nodeTemperature = childNode;
                else
                if(name.equals("airFactor") && !childNode.hasChildNodes())
                    nodeAirFactor = childNode;
                else
                    throw new InvalidConfigFormatException("Hib\341s a konfigur\341ci\363s f\341jl.");
            } else
            if((childNode instanceof Text) && !childNode.getNodeValue().matches("\\s*"))
                throw new InvalidConfigFormatException("Hib\341s a konfigur\341ci\363s f\341jl.");
        }

        childNodes = nodeGasContents.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); i++)
        {
            Node childNode = childNodes.item(i);
            if((childNode instanceof Element) && childNode.getNodeName().equals("content"))
            {
                if(childNode.hasChildNodes())
                    throw new InvalidConfigFormatException("Hib\341s a konfigur\341ci\363s f\341jl.");
                String key = childNode.getAttributes().getNamedItem("name").getNodeValue();
                String value = childNode.getAttributes().getNamedItem("value").getNodeValue();
                if(!gasContent.containsKey(key) || !((String)gasContent.get(key)).equals(""))
                    throw new InvalidConfigFormatException("Hib\341s a konfigur\341ci\363s f\341jl.");
                if(!Check.isValidGasValue(value))
                    throw new InvalidDataException("Hib\341s G\341z\366ssztev\u0151 \351rt\351k.");
                gasContent.put(key, value);
            } else
            {
                if(childNode instanceof Element)
                    throw new InvalidConfigFormatException("Hib\341s a konfigur\341ci\363s f\341jl.");
                if((childNode instanceof Text) && !childNode.getNodeValue().matches("\\s*"))
                    throw new InvalidConfigFormatException("Hib\341s a konfigur\341ci\363s f\341jl.");
            }
        }

        if(!Check.checkGasContentPercentage(gasContent))
            throw new InvalidDataException("A g\341z\366sszetev\u0151k nem megfelel\u0151ek (\366sszeg\374k nem 100%).");
        childNodes = nodeBoilers.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); i++)
        {
            Node childNode = childNodes.item(i);
            if((childNode instanceof Element) && childNode.getNodeName().equals("boiler"))
            {
                if(childNode.hasChildNodes())
                    throw new InvalidConfigFormatException("Hib\341s a konfigur\341ci\363s f\341jl.");
                String key = childNode.getAttributes().getNamedItem("type").getNodeValue();
                String value = childNode.getAttributes().getNamedItem("count").getNodeValue();
                if(!boilers.containsKey(key) || !((String)boilers.get(key)).equals(""))
                    throw new InvalidConfigFormatException("Hib\341s a konfigur\341ci\363s f\341jl.");
                if(!Check.isValidBoilerNumber(value))
                    throw new InvalidDataException("Hib\341s kaz\341n darabsz\341m.");
                boilers.put(key, value);
            } else
            {
                if(childNode instanceof Element)
                    throw new InvalidConfigFormatException("Hib\341s a konfigur\341ci\363s f\341jl.");
                if((childNode instanceof Text) && !childNode.getNodeValue().matches("\\s*"))
                    throw new InvalidConfigFormatException("Hib\341s a konfigur\341ci\363s f\341jl.");
            }
        }

        if(!Check.checkBoilerCount(boilers))
            throw new InvalidDataException("Legal\341bb egy kaz\341nnak lennie kell.");
        String wrapperString = nodeTemperature.getAttributes().getNamedItem("value").getNodeValue();
        if(!Check.isValidTemperature(wrapperString))
            throw new InvalidDataException("Hib\341s h\u0151m\351rs\351klet \351rt\351k.");
        temperature = new Integer(wrapperString);
        wrapperString = nodeAirFactor.getAttributes().getNamedItem("value").getNodeValue();
        if(!Check.isValidAirFactor(wrapperString))
        {
            throw new InvalidDataException("Hib\341s leveg\u0151t\351nyez\u0151 \351rt\351k.");
        } else
        {
            airFactor = new Double(wrapperString);
            return;
        }
    }

    public void saveXMLData(String filename, Hashtable gasContent, Hashtable boilers, String temperature, String airFactor)
        throws SAXParseException, SAXException, ParserConfigurationException, FileNotFoundException, IOException, InvalidDataException
    {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element elementConfig = document.createElement("config");
        Date now = new Date();
        Format formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
        String commentString = new String("This is an EmCalc configuration XML file which contains\nthe gas content,\nthe numbers of the boilers,\nthe monthly average temperature,\nthe air factor.\n\nCreated with EmCalc version 1.0 Beta1 on " + formatter.format(now));
        org.w3c.dom.Comment elementDescriptionComment = document.createComment(commentString);
        elementConfig.appendChild(elementDescriptionComment);
        Element elementGasContents = document.createElement("gasContents");
        if(!Check.checkGasContentPercentage(gasContent))
            throw new InvalidDataException("A g\341z\366sszetev\u0151k nem megfelel\u0151ek (\366sszeg\374k nem 100%).");
        Element content;
        for(Enumeration keys = gasContent.keys(); keys.hasMoreElements(); elementGasContents.appendChild(content))
        {
            String key = (String)keys.nextElement();
            String value = (String)gasContent.get(key);
            if(!Check.isValidGasValue(value))
                throw new InvalidDataException();
            content = document.createElement("content");
            content.setAttribute("name", key);
            content.setAttribute("value", value);
        }

        elementConfig.appendChild(elementGasContents);
        Element elementBoilers = document.createElement("boilers");
        if(!Check.checkBoilerCount(boilers))
            throw new InvalidDataException();
        Element boiler;
        for(Enumeration keys = boilers.keys(); keys.hasMoreElements(); elementBoilers.appendChild(boiler))
        {
            String key = (String)keys.nextElement();
            String value = (String)boilers.get(key);
            if(!Check.isValidBoilerNumber(value))
                throw new InvalidDataException();
            boiler = document.createElement("boiler");
            boiler.setAttribute("type", key);
            boiler.setAttribute("count", value);
        }

        elementConfig.appendChild(elementBoilers);
        if(!Check.isValidTemperature(temperature.toString()))
            throw new InvalidDataException();
        Element elementTemperature = document.createElement("temperature");
        elementTemperature.setAttribute("value", temperature);
        elementConfig.appendChild(elementTemperature);
        if(!Check.isValidAirFactor(airFactor.toString()))
        {
            throw new InvalidDataException();
        } else
        {
            Element elementAirFactor = document.createElement("airFactor");
            elementAirFactor.setAttribute("value", airFactor);
            elementConfig.appendChild(elementAirFactor);
            XMLWriter output = new XMLWriter();
            output.writeNode(new FileOutputStream(new File(filename)), elementConfig);
            return;
        }
    }

    public Hashtable getGasContent()
    {
        return gasContent;
    }

    public Hashtable getBoilers()
    {
        return boilers;
    }

    public String getTemperature()
    {
        return temperature.toString();
    }

    public String getAirFactor()
    {
        return airFactor.toString();
    }

    private Hashtable gasContent;
    private Hashtable boilers;
    private Integer temperature;
    private Double airFactor;
    private String filename;
}
