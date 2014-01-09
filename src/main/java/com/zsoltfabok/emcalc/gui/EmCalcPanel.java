package com.zsoltfabok.emcalc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.zsoltfabok.emcalc.calculation.Check;
import com.zsoltfabok.emcalc.calculation.Consuption;
import com.zsoltfabok.emcalc.exception.InvalidConfigFormatException;
import com.zsoltfabok.emcalc.exception.InvalidDataException;
import com.zsoltfabok.emcalc.html.HTMLData;
import com.zsoltfabok.emcalc.html.NamedData;
import com.zsoltfabok.emcalc.xml.XMLData;

// Referenced classes of package gui:
//            FileChooser

public class EmCalcPanel extends JPanel
{
    class MyDocumentListener
        implements DocumentListener
    {

        public void changedUpdate(DocumentEvent documentevent)
        {
        }

        public void insertUpdate(DocumentEvent event)
        {
            if(updated)
            {
                buttonCalculate.setEnabled(true);
                buttonHTML.setEnabled(false);
                MyTableModel tableModel = new MyTableModel();
                tableResult.setModel(tableModel);
                updated = false;
            }
        }

        public void removeUpdate(DocumentEvent documentevent)
        {
        }

        MyDocumentListener()
        {
            super();
        }
    }

    class MyTableModel extends AbstractTableModel
    {

        public Class getColumnClass(int c)
        {
            return getValueAt(0, c).getClass();
        }

        public int getColumnCount()
        {
            return columnNames.length;
        }

        public String getColumnName(int col)
        {
            return columnNames[col];
        }

        public int getRowCount()
        {
            return data.length;
        }

        public Object getValueAt(int row, int col)
        {
            return data[row][col];
        }

        public boolean isCellEditable(int row, int col)
        {
            return false;
        }

        public void setValueAt(Object value, int row, int col)
        {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }

        private String columnNames[] = {
            "T\355pus", "CO2 (m3)", "H20 (m3)", "O2 (m3)", "N2 (m3)"
        };
        private Object data[][] = {
            {
                "F\351g C18", new Double(0.0D), new Double(0.0D), new Double(0.0D), new Double(0.0D)
            }, {
                "F\351g C21", new Double(0.0D), new Double(0.0D), new Double(0.0D), new Double(0.0D)
            }, {
                "F\351g C24", new Double(0.0D), new Double(0.0D), new Double(0.0D), new Double(0.0D)
            }, {
                "F\351g C30", new Double(0.0D), new Double(0.0D), new Double(0.0D), new Double(0.0D)
            }, {
                "Termot\351ka 25", new Double(0.0D), new Double(0.0D), new Double(0.0D), new Double(0.0D)
            }, {
                "\326sszesen", new Double(0.0D), new Double(0.0D), new Double(0.0D), new Double(0.0D)
            }
        };

        MyTableModel()
        {
            super();
        }
    }

    class NumberRenderer extends DefaultTableCellRenderer
    {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            if(row == 5)
                setBackground(Color.GRAY);
            else
                setBackground(Color.WHITE);
            if(value instanceof Double)
            {
                setText(formatter.format(value));
                setHorizontalAlignment(4);
            } else
            {
                super.setValue(value);
            }
            return this;
        }

        public void setValue(Object value)
        {
            if(value instanceof Double)
            {
                setText(formatter.format(value));
                setHorizontalAlignment(4);
            }
            super.setValue(value);
        }

        NumberFormat formatter;

        NumberRenderer()
        {
            super();
            formatter = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        }
    }


    public EmCalcPanel()
    {
        super(new GridLayout(1, 1));
        config = null;
        updated = false;
        initGUI();
    }

    private void calculate()
    {
        if(checkFields())
        {
            Consuption con = new Consuption();
            Double values[][] = con.getConsuption(getGasContent(), getBoilers(), Double.parseDouble(inputTemperature.getText()), Double.parseDouble(inputAirFactor.getText()));
            double sum[] = new double[4];
            for(int i = 0; i < 5; i++)
            {
                for(int j = 0; j < 4; j++)
                {
                    tableResult.setValueAt(values[i][j], i, j + 1);
                    sum[j] += values[i][j].doubleValue();
                }

            }

            for(int i = 0; i < 4; i++)
                tableResult.setValueAt(new Double(sum[i]), 5, i + 1);

            buttonHTML.setEnabled(true);
            updated = true;
        } else
        {
            buttonHTML.setEnabled(false);
        }
    }

    public boolean checkFields()
    {
        try
        {
            Component components[] = panelGasContent.getComponents();
            for(int i = 0; i < components.length; i++)
                if(components[i] instanceof JTextField)
                {
                    String value = ((JTextField)components[i]).getText();
                    if(!Check.isValidGasValue(value))
                    {
                        ((JTextField)components[i]).selectAll();
                        components[i].requestFocus();
                        throw new Exception("Hib\341s G\341z\366ssztev\u0151 \351rt\351k.");
                    }
                }

            if(!Check.checkGasContentPercentage(getGasContent()))
                throw new Exception("A g\341z\366sszetev\u0151k nem megfelel\u0151ek (\366sszeg\374k nem 100%).");
            components = panelBoiler.getComponents();
            for(int i = 0; i < components.length; i++)
                if(components[i] instanceof JTextField)
                {
                    String value = ((JTextField)components[i]).getText();
                    if(!Check.isValidBoilerNumber(value))
                    {
                        ((JTextField)components[i]).selectAll();
                        components[i].requestFocus();
                        throw new Exception("Hib\341s kaz\341n darabsz\341m.");
                    }
                }

            if(!Check.checkBoilerCount(getBoilers()))
                throw new Exception("Legal\341bb egy kaz\341nnak lennie kell.");
            if(!Check.isValidTemperature(inputTemperature.getText()))
            {
                inputTemperature.selectAll();
                inputTemperature.requestFocus();
                throw new Exception("Hib\341s h\u0151m\351rs\351klet \351rt\351k.");
            }
            if(!Check.isValidAirFactor(inputAirFactor.getText()))
            {
                inputAirFactor.selectAll();
                inputAirFactor.requestFocus();
                throw new Exception("Hib\341s leveg\u0151t\351nyez\u0151 \351rt\351k.");
            } else
            {
                return true;
            }
        }
        catch(Exception e)
        {
            messageWarning(e.getMessage());
        }
        return false;
    }

    public void fillFieldsWithValue(String filename)
    {
        try
        {
            config = new XMLData(filename);
            setGasContent(config.getGasContent());
            setBoilers(config.getBoilers());
            inputTemperature.setText(config.getTemperature());
            inputAirFactor.setText(config.getAirFactor());
        }
        catch(InvalidConfigFormatException e)
        {
            messageWarning(e.getMessage());
        }
        catch(InvalidDataException e)
        {
            messageWarning("Hib\341s konfigur\341ci\363s f\341jl:\n" + e.getMessage());
        }
        catch(SAXParseException e)
        {
            messageWarning("Hib\341s konfigur\341ci\363s f\341jl.");
        }
        catch(SAXException e)
        {
            messageWarning("Hib\341s konfigur\341ci\363s f\341jl.");
        }
        catch(ParserConfigurationException e)
        {
            messageWarning("Hib\341s konfigur\341ci\363s f\341jl.");
        }
        catch(FileNotFoundException e)
        {
            messageError("Hiba t\366rt\351nt a konfigur\341ci\363s f\341jl olvas\341sakor.");
        }
        catch(IOException e)
        {
            messageError("Hiba t\366rt\351nt a konfigur\341ci\363s f\341jl olvas\341sakor.");
        }
        catch(Exception e)
        {
            messageError("Nem v\341rt hiba.");
            e.printStackTrace();
        }
        buttonCalculate.setEnabled(true);
    }

    private void generateHTMLOutput()
    {
        String filename = FileChooser.getFileName(".", "html", "HTML kimenet", 1);
        if(filename != null)
        {
            HTMLData htmlOutput = new HTMLData();
            Vector gasContent = new Vector(1, 1);
            Component components[] = panelGasContent.getComponents();
            for(int i = 0; i < components.length; i++)
                if(components[i] instanceof JTextField)
                {
                    String value = ((JTextField)components[i]).getText();
                    String name = components[i].getName().substring(5);
                    gasContent.add(new NamedData(name, value));
                }

            Vector boilers = new Vector(1, 1);
            components = panelBoiler.getComponents();
            for(int i = 0; i < components.length; i++)
                if(components[i] instanceof JTextField)
                {
                    String value = ((JTextField)components[i]).getText();
                    String name = ((JLabel)components[i - 1]).getText();
                    boilers.add(new NamedData(name, value));
                }

            Vector other = new Vector(1, 1);
            other.add(new NamedData("Havi k\366z\351ph\u0151m\351rs\351klet (\260C)", inputTemperature.getText()));
            other.add(new NamedData("Leveg\u0151t\351nyez\u0151", inputAirFactor.getText()));
            Double values[][] = new Double[6][4];
            for(int i = 0; i < 6; i++)
            {
                for(int j = 0; j < 4; j++)
                    values[i][j] = (Double)tableResult.getValueAt(i, j + 1);

            }

            try
            {
                htmlOutput.writeToHTMLFile(filename, gasContent, boilers, other, values);
            }
            catch(IOException e)
            {
                messageError("Hiba t\366rt\351nt a HTML f\341jl \355r\341sakor.");
            }
            catch(Exception e)
            {
                messageError("Nem v\341rt hiba.");
                e.printStackTrace();
            }
        }
    }

    private Hashtable getGasContent()
    {
        Hashtable gasContent = new Hashtable();
        Component components[] = panelGasContent.getComponents();
        for(int i = 0; i < components.length; i++)
            if(components[i] instanceof JTextField)
                gasContent.put(components[i].getName().substring(5), ((JTextField)components[i]).getText());

        return gasContent;
    }

    private Hashtable getBoilers()
    {
        Hashtable boilers = new Hashtable();
        Component components[] = panelBoiler.getComponents();
        for(int i = 0; i < components.length; i++)
            if(components[i] instanceof JTextField)
                boilers.put(components[i].getName().substring(5), ((JTextField)components[i]).getText());

        return boilers;
    }

    public void initGUI()
    {
        try
        {
            JScrollPane scrollPaneTable = new JScrollPane();
            tableResult = new JTable();
            JPanel panelTable = new JPanel();
            JPanel panelTemperature = new JPanel();
            inputTemperature = new JFormattedTextField();
            JPanel panelAirFactor = new JPanel();
            inputAirFactor = new JFormattedTextField();
            panelGasContent = new JPanel();
            JLabel labelCH4 = new JLabel();
            JFormattedTextField inputCH4 = new JFormattedTextField();
            JLabel labelC2H6 = new JLabel();
            JFormattedTextField inputC2H6 = new JFormattedTextField();
            JLabel labelC3H8 = new JLabel();
            JFormattedTextField inputC3H8 = new JFormattedTextField();
            JLabel labelC4H10 = new JLabel();
            JFormattedTextField inputC4H10 = new JFormattedTextField();
            JLabel labelC5H12 = new JLabel();
            JFormattedTextField inputC5H12 = new JFormattedTextField();
            JLabel labelC6H14 = new JLabel();
            JFormattedTextField inputC6H14 = new JFormattedTextField();
            JLabel labelC7H16 = new JLabel();
            JFormattedTextField inputC7H16 = new JFormattedTextField();
            JLabel labelC8H18 = new JLabel();
            JFormattedTextField inputC8H18 = new JFormattedTextField();
            JLabel labelN2 = new JLabel();
            JFormattedTextField inputN2 = new JFormattedTextField();
            JLabel labelCO2 = new JLabel();
            JFormattedTextField inputCO2 = new JFormattedTextField();
            panelBoiler = new JPanel();
            JLabel labelFegC18 = new JLabel();
            JFormattedTextField inputFegC18 = new JFormattedTextField();
            JLabel labelFegC21 = new JLabel();
            JFormattedTextField inputFegC21 = new JFormattedTextField();
            JLabel labelFegC24 = new JLabel();
            JFormattedTextField inputFegC24 = new JFormattedTextField();
            JLabel labelFegC30 = new JLabel();
            JFormattedTextField inputFegC30 = new JFormattedTextField();
            JLabel labelTermoteka25 = new JLabel();
            JFormattedTextField inputTermoteka25 = new JFormattedTextField();
            buttonCalculate = new JButton();
            buttonHTML = new JButton();
            setLayout(null);
            setPreferredSize(new Dimension(565, 410));
            scrollPaneTable.setPreferredSize(new Dimension(540, 114));
            scrollPaneTable.setBounds(new Rectangle(10, 252, 545, 114));
            add(scrollPaneTable);
            tableResult.setPreferredSize(new Dimension(535, 96));
            scrollPaneTable.add(tableResult);
            scrollPaneTable.setViewportView(tableResult);
            GridLayout panelTableLayout = new GridLayout(1, 1);
            panelTable.setLayout(panelTableLayout);
            panelTableLayout.setHgap(0);
            panelTableLayout.setVgap(0);
            panelTableLayout.setColumns(1);
            panelTableLayout.setRows(1);
            panelTable.setPreferredSize(new Dimension(540, 114));
            panelTable.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
            panelTable.setBounds(new Rectangle(11, 283, 540, 114));
            add(panelTable);
            GridLayout panelTemperatureLayout = new GridLayout(1, 1);
            panelTemperature.setLayout(panelTemperatureLayout);
            panelTemperatureLayout.setHgap(0);
            panelTemperatureLayout.setVgap(0);
            panelTemperatureLayout.setColumns(1);
            panelTemperatureLayout.setRows(1);
            panelTemperature.setPreferredSize(new Dimension(180, 50));
            panelTemperature.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, false), "Havi k\366z\351ph\u0151m\351rs\351klet (\260C)", 4, 2, new Font("Dialog", 1, 12), new Color(0, 0, 0)));
            panelTemperature.setBounds(new Rectangle(165, 10, 180, 50));
            add(panelTemperature);
            inputTemperature.setHorizontalAlignment(4);
            inputTemperature.setText("0");
            inputTemperature.setPreferredSize(new Dimension(155, 20));
            panelTemperature.add(inputTemperature);
            GridLayout panelAirComponentLayout = new GridLayout(1, 1);
            panelAirFactor.setLayout(panelAirComponentLayout);
            panelAirComponentLayout.setHgap(0);
            panelAirComponentLayout.setVgap(0);
            panelAirComponentLayout.setColumns(1);
            panelAirComponentLayout.setRows(1);
            panelAirFactor.setPreferredSize(new Dimension(180, 50));
            panelAirFactor.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, false), "Leveg\u0151t\351nyez\u0151", 4, 2, new Font("Dialog", 1, 12), new Color(0, 0, 0)));
            panelAirFactor.setBounds(new Rectangle(165, 78, 180, 50));
            add(panelAirFactor);
            inputAirFactor.setHorizontalAlignment(4);
            inputAirFactor.setText("0");
            inputAirFactor.setPreferredSize(new Dimension(132, 21));
            panelAirFactor.add(inputAirFactor);
            GridLayout panelGasContentLayout = new GridLayout(10, 2);
            panelGasContent.setLayout(panelGasContentLayout);
            panelGasContentLayout.setHgap(0);
            panelGasContentLayout.setVgap(0);
            panelGasContentLayout.setColumns(2);
            panelGasContentLayout.setRows(10);
            panelGasContent.setPreferredSize(new Dimension(135, 229));
            panelGasContent.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, false), "G\341z\366sszet\351tel (tf%)", 4, 2, new Font("Dialog", 1, 12), new Color(0, 0, 0)));
            panelGasContent.setBounds(new Rectangle(10, 10, 135, 229));
            add(panelGasContent);
            labelCH4.setText("CH4");
            labelCH4.setPreferredSize(new Dimension(45, 15));
            panelGasContent.add(labelCH4);
            inputCH4.setHorizontalAlignment(4);
            inputCH4.setText("0");
            inputCH4.setPreferredSize(new Dimension(70, 20));
            inputCH4.setName("inputCH4");
            panelGasContent.add(inputCH4);
            labelC2H6.setText("C2H6");
            labelC2H6.setPreferredSize(new Dimension(45, 15));
            panelGasContent.add(labelC2H6);
            inputC2H6.setHorizontalAlignment(4);
            inputC2H6.setText("0");
            inputC2H6.setPreferredSize(new Dimension(65, 20));
            inputC2H6.setRequestFocusEnabled(true);
            inputC2H6.setName("inputC2H6");
            panelGasContent.add(inputC2H6);
            labelC3H8.setText("C3H8");
            labelC3H8.setPreferredSize(new Dimension(45, 15));
            panelGasContent.add(labelC3H8);
            inputC3H8.setHorizontalAlignment(4);
            inputC3H8.setText("0");
            inputC3H8.setPreferredSize(new Dimension(65, 20));
            inputC3H8.setName("inputC3H8");
            panelGasContent.add(inputC3H8);
            labelC4H10.setText("C4H10");
            labelC4H10.setPreferredSize(new Dimension(45, 15));
            panelGasContent.add(labelC4H10);
            inputC4H10.setHorizontalAlignment(4);
            inputC4H10.setText("0");
            inputC4H10.setPreferredSize(new Dimension(70, 15));
            inputC4H10.setName("inputC4H10");
            panelGasContent.add(inputC4H10);
            labelC5H12.setText("C5H12");
            labelC5H12.setPreferredSize(new Dimension(45, 15));
            panelGasContent.add(labelC5H12);
            inputC5H12.setHorizontalAlignment(4);
            inputC5H12.setText("0");
            inputC5H12.setPreferredSize(new Dimension(70, 20));
            inputC5H12.setName("inputC5H12");
            panelGasContent.add(inputC5H12);
            labelC6H14.setText("C6H14");
            labelC6H14.setPreferredSize(new Dimension(46, 15));
            panelGasContent.add(labelC6H14);
            inputC6H14.setHorizontalAlignment(4);
            inputC6H14.setText("0");
            inputC6H14.setPreferredSize(new Dimension(70, 20));
            inputC6H14.setName("inputC6H14");
            panelGasContent.add(inputC6H14);
            labelC7H16.setText("C7H16");
            labelC7H16.setPreferredSize(new Dimension(45, 15));
            panelGasContent.add(labelC7H16);
            inputC7H16.setHorizontalAlignment(4);
            inputC7H16.setText("0");
            inputC7H16.setPreferredSize(new Dimension(70, 20));
            inputC7H16.setName("inputC7H16");
            panelGasContent.add(inputC7H16);
            labelC8H18.setText("C8H18");
            labelC8H18.setPreferredSize(new Dimension(45, 15));
            panelGasContent.add(labelC8H18);
            inputC8H18.setHorizontalAlignment(4);
            inputC8H18.setText("0");
            inputC8H18.setPreferredSize(new Dimension(70, 20));
            inputC8H18.setName("inputC8H18");
            panelGasContent.add(inputC8H18);
            labelN2.setText("N2");
            labelN2.setPreferredSize(new Dimension(45, 15));
            panelGasContent.add(labelN2);
            inputN2.setHorizontalAlignment(4);
            inputN2.setText("0");
            inputN2.setPreferredSize(new Dimension(70, 20));
            inputN2.setName("inputN2");
            panelGasContent.add(inputN2);
            labelCO2.setText("CO2");
            labelCO2.setPreferredSize(new Dimension(45, 15));
            panelGasContent.add(labelCO2);
            inputCO2.setHorizontalAlignment(4);
            inputCO2.setText("0");
            inputCO2.setPreferredSize(new Dimension(70, 20));
            inputCO2.setName("inputCO2");
            panelGasContent.add(inputCO2);
            GridLayout panelBoilerLayout = new GridLayout(5, 2);
            panelBoiler.setLayout(panelBoilerLayout);
            panelBoilerLayout.setHgap(0);
            panelBoilerLayout.setVgap(0);
            panelBoilerLayout.setColumns(2);
            panelBoilerLayout.setRows(5);
            panelBoiler.setPreferredSize(new Dimension(190, 137));
            panelBoiler.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, false), "Kaz\341n darabsz\341m", 4, 2, new Font("Dialog", 1, 12), new Color(0, 0, 0)));
            panelBoiler.setBounds(new Rectangle(365, 10, 190, 137));
            add(panelBoiler);
            labelFegC18.setText("F\351g C18");
            labelFegC18.setPreferredSize(new Dimension(142, 22));
            panelBoiler.add(labelFegC18);
            inputFegC18.setHorizontalAlignment(4);
            inputFegC18.setText("0");
            inputFegC18.setPreferredSize(new Dimension(90, 21));
            inputFegC18.setName("inputFeg C18");
            panelBoiler.add(inputFegC18);
            labelFegC21.setText("F\351g C21");
            labelFegC21.setPreferredSize(new Dimension(82, 41));
            panelBoiler.add(labelFegC21);
            inputFegC21.setHorizontalAlignment(4);
            inputFegC21.setText("0");
            inputFegC21.setPreferredSize(new Dimension(80, 24));
            inputFegC21.setName("inputFeg C21");
            panelBoiler.add(inputFegC21);
            labelFegC24.setText("F\351g C24");
            labelFegC24.setPreferredSize(new Dimension(130, 35));
            panelBoiler.add(labelFegC24);
            inputFegC24.setHorizontalAlignment(4);
            inputFegC24.setText("0");
            inputFegC24.setPreferredSize(new Dimension(90, 21));
            inputFegC24.setName("inputFeg C24");
            panelBoiler.add(inputFegC24);
            labelFegC30.setText("F\351g C30");
            labelFegC30.setPreferredSize(new Dimension(130, 27));
            panelBoiler.add(labelFegC30);
            inputFegC30.setHorizontalAlignment(4);
            inputFegC30.setText("0");
            inputFegC30.setPreferredSize(new Dimension(90, 21));
            inputFegC30.setName("inputFeg C30");
            panelBoiler.add(inputFegC30);
            labelTermoteka25.setText("Termot\351ka 25");
            labelTermoteka25.setPreferredSize(new Dimension(96, 22));
            panelBoiler.add(labelTermoteka25);
            inputTermoteka25.setHorizontalAlignment(4);
            inputTermoteka25.setText("0");
            inputTermoteka25.setPreferredSize(new Dimension(80, 21));
            inputTermoteka25.setName("inputTermoteka 25");
            panelBoiler.add(inputTermoteka25);
            buttonCalculate.setText("Sz\341m\355t\341s");
            buttonCalculate.setMnemonic('S');
            buttonCalculate.setPreferredSize(new Dimension(92, 25));
            buttonCalculate.setBounds(new Rectangle(209, 214, 92, 25));
            add(buttonCalculate);
            buttonCalculate.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt)
                {
                    calculate();
                }
            }
);
            buttonHTML.setText("HTML kimenet...");
            buttonHTML.setMnemonic('H');
            buttonHTML.setPreferredSize(new Dimension(143, 25));
            buttonHTML.setBounds(new Rectangle(388, 214, 143, 25));
            add(buttonHTML);
            buttonHTML.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt)
                {
                    generateHTMLOutput();
                }
            }
);
            Component components[] = panelGasContent.getComponents();
            for(int i = 0; i < components.length; i++)
                if(components[i] instanceof JTextField)
                {
                    ((JTextField)components[i]).getDocument().addDocumentListener(new MyDocumentListener());
                    javax.swing.text.DefaultFormatter formatter = new NumberFormatter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US)));
                    DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(formatter, formatter, formatter);
                    ((JFormattedTextField)components[i]).setFormatterFactory(fmtFactory);
                    ((JFormattedTextField)components[i]).setFocusLostBehavior(0);
                    ((JFormattedTextField)components[i]).setValue(new Double(0.0D));
                }

            components = panelBoiler.getComponents();
            for(int i = 0; i < components.length; i++)
                if(components[i] instanceof JTextField)
                {
                    ((JTextField)components[i]).getDocument().addDocumentListener(new MyDocumentListener());
                    javax.swing.text.DefaultFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
                    DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(formatter, formatter, formatter);
                    ((JFormattedTextField)components[i]).setFormatterFactory(fmtFactory);
                    ((JFormattedTextField)components[i]).setFocusLostBehavior(0);
                    ((JFormattedTextField)components[i]).setValue(new Integer(0));
                }

            javax.swing.text.DefaultFormatter formatter = new NumberFormatter(new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US)));
            DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(formatter, formatter, formatter);
            inputAirFactor.getDocument().addDocumentListener(new MyDocumentListener());
            inputAirFactor.setFormatterFactory(fmtFactory);
            inputAirFactor.setFocusLostBehavior(0);
            inputAirFactor.setValue(new Double(0.0D));
            inputTemperature.getDocument().addDocumentListener(new MyDocumentListener());
            inputTemperature.setFormatterFactory(fmtFactory);
            inputTemperature.setFocusLostBehavior(0);
            inputTemperature.setValue(new Double(0.0D));
            MyTableModel tableModel = new MyTableModel();
            tableResult.setModel(tableModel);
            tableResult.setDefaultRenderer(java.lang.Double.class, new NumberRenderer());
            tableResult.setDefaultRenderer(java.lang.String.class, new NumberRenderer());
            tableResult.setRowSelectionAllowed(false);
            panelGasContent.setToolTipText("Adja meg g\341z sz\341zal\351kos \366sszet\351tel\351t!");
            panelBoiler.setToolTipText("Adja meg a telep\374l\351sen/telep\374l\351sr\351szen lev\u0151 kaz\341nok darabsz\341m\341t!");
            panelTemperature.setToolTipText("Adja meg a vizsg\341lt id\u0151tartam k\366z\351ph\u0151m\351rs\351klet\351t!");
            panelAirFactor.setToolTipText("Adja meg a t\374zel\u0151berendez\351sekre jellemz\u0151 l\351gfeleslegi t\351nyez\u0151t!");
            tableResult.setToolTipText("A g\341z kibocs\341jt\341sok m3-ben.");
            buttonCalculate.setEnabled(true);
            buttonHTML.setEnabled(false);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isLoaded()
    {
        return config != null;
    }

    private void messageError(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Hiba!", 0);
    }

    private void messageWarning(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Figyelmeztet\351s!", 2);
    }

    public void reload()
    {
        setGasContent(config.getGasContent());
        setBoilers(config.getBoilers());
        inputTemperature.setText(config.getTemperature());
        inputAirFactor.setText(config.getAirFactor());
    }

    public void saveConfig(String filename)
    {
        if(checkFields())
            try
            {
                config.saveXMLData(filename, getGasContent(), getBoilers(), inputTemperature.getText(), inputAirFactor.getText());
            }
            catch(Exception e)
            {
                messageError("Hiba t\366rt\351nt a konfigur\341ci\363s f\341jl \355r\341sakor.");
            }
    }

    private void setGasContent(Hashtable gasContent)
    {
        Component components[] = panelGasContent.getComponents();
        for(int i = 0; i < components.length; i++)
            if(components[i] instanceof JTextField)
                ((JTextField)components[i]).setText((String)gasContent.get(components[i].getName().substring(5)));

    }

    private void setBoilers(Hashtable boilers)
    {
        Component components[] = panelBoiler.getComponents();
        for(int i = 0; i < components.length; i++)
            if(components[i] instanceof JTextField)
                ((JTextField)components[i]).setText((String)boilers.get(components[i].getName().substring(5)));

    }

    private JPanel panelBoiler;
    private JPanel panelGasContent;
    private JTable tableResult;
    private JButton buttonCalculate;
    private JButton buttonHTML;
    private JFormattedTextField inputTemperature;
    private JFormattedTextField inputAirFactor;
    private XMLData config;
    private boolean updated;







}
