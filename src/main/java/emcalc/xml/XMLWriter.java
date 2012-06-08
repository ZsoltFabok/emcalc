package emcalc.xml;

import java.io.IOException;
import java.io.OutputStream;
import org.w3c.dom.*;

public class XMLWriter
{

    public XMLWriter()
    {
        destination = null;
        separator = "  ";
        encoding = "UTF-8";
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void writeNode(OutputStream destination, Node node)
        throws IOException
    {
        this.destination = destination;
        writeln("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
        processTree(node, "");
    }

    private void processTree(Node nodeRoot, String separator)
        throws IOException
    {
        writeln(separator + node2String(nodeRoot));
        NodeList childElements = nodeRoot.getChildNodes();
        for(int i = 0; i < childElements.getLength(); i++)
        {
            Node childElement = childElements.item(i);
            if(childElement instanceof Text)
                writeln(separator + childElement.getNodeValue());
            else
            if(childElement instanceof Comment)
                writeln(separator + "<!--\n" + childElement.getNodeValue() + "\n-->");
            else
                processTree(childElement, new String(separator + this.separator));
        }

        if(nodeRoot.getFirstChild() != null)
            writeln(separator + "</" + nodeRoot.getNodeName() + ">");
    }

    private String node2String(Node node)
    {
        NamedNodeMap attrs = node.getAttributes();
        StringBuffer attrString = new StringBuffer();
        for(int j = 0; j < attrs.getLength(); j++)
        {
            String attrName = attrs.item(j).getNodeName();
            String attrValue = attrs.item(j).getNodeValue();
            attrString.append(" " + attrName + "=\"" + attrValue + "\"");
        }

        String slash = "";
        if(node.getFirstChild() == null)
            slash = "/";
        return new String("<" + node.getNodeName() + attrString.toString() + slash + ">");
    }

    private void writeln(String text)
        throws IOException
    {
        destination.write(text.getBytes());
        destination.write("\n".getBytes());
    }

    private OutputStream destination;
    private String separator;
    private String encoding;
}
