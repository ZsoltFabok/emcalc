package com.zsoltfabok.emcalc.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

// Referenced classes of package gui:
//            EmCalcPanel, FileChooser

public class EmCalcGUI extends JFrame
{
    class MyMenuListener
        implements MenuListener
    {

        public void menuSelected(MenuEvent e)
        {
            int tabIndex = paneTab.getSelectedIndex();
            if(tabIndex >= 0 && !((EmCalcPanel)vectorTabs.get(tabIndex)).isLoaded())
            {
                menuitemOpenXML.setEnabled(true);
                menuitemSaveXML.setEnabled(true);
                menuitemReload.setEnabled(false);
            }
            if(tabIndex >= 0 && ((EmCalcPanel)vectorTabs.get(tabIndex)).isLoaded())
                menuitemReload.setEnabled(true);
            else
            if(vectorTabs.size() == 1)
                menuitemCloseAll.setEnabled(false);
        }

        public void menuDeselected(MenuEvent menuevent)
        {
        }

        public void menuCanceled(MenuEvent menuevent)
        {
        }

        MyMenuListener()
        {
            super();
        }
    }

    class MenuActionListener
        implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            if(event.getSource().equals(menuitemNewTab))
            {
                addCalculateTab();
                menuitemClose.setEnabled(true);
                menuitemCloseAll.setEnabled(true);
                menuitemOpenXML.setEnabled(true);
                menuitemSaveXML.setEnabled(true);
            } else
            if(event.getSource().equals(menuitemClose))
            {
                Object options[] = {
                    "Igen", "M\351gsem"
                };
                int result = JOptionPane.showOptionDialog(null, "Val\363ban bez\341jra a \"" + paneTab.getTitleAt(paneTab.getSelectedIndex()) + "\" -t?", "Figyelmeztet\351s!", 0, 2, null, options, options[0]);
                if(result == 0)
                {
                    closeActualTab();
                    if(vectorTabs.size() == 0)
                    {
                        menuitemClose.setEnabled(false);
                        menuitemCloseAll.setEnabled(false);
                        menuitemOpenXML.setEnabled(false);
                        menuitemSaveXML.setEnabled(false);
                        menuitemReload.setEnabled(false);
                    } else
                    if(vectorTabs.size() == 1)
                        menuitemCloseAll.setEnabled(false);
                }
            } else
            if(event.getSource().equals(menuitemCloseAll))
            {
                Object options[] = {
                    "Igen", "M\351gsem"
                };
                int result = JOptionPane.showOptionDialog(null, "Val\363ban bez\341jra az \366sszes sz\341m\355t\341st?", "Figyelmeztet\351s!", 0, 2, null, options, options[0]);
                if(result == 0)
                {
                    closeAll();
                    menuitemClose.setEnabled(false);
                    menuitemCloseAll.setEnabled(false);
                    menuitemOpenXML.setEnabled(false);
                    menuitemSaveXML.setEnabled(false);
                    menuitemReload.setEnabled(false);
                }
            } else
            if(event.getSource().equals(menuitemOpenXML))
            {
                String filename = FileChooser.getFileName(".", "xml", "Konfigur\341ci\363s f\341jl bet\366lt\351se", 0);
                if(filename != null)
                {
                    ((EmCalcPanel)vectorTabs.get(paneTab.getSelectedIndex())).fillFieldsWithValue(filename);
                    menuitemReload.setEnabled(true);
                }
            } else
            if(event.getSource().equals(menuitemSaveXML))
            {
                if(((EmCalcPanel)vectorTabs.get(paneTab.getSelectedIndex())).checkFields())
                {
                    String filename = FileChooser.getFileName(".", "xml", "Konfigur\341ci\363s f\341jl ment\351se", 1);
                    if(filename != null)
                        ((EmCalcPanel)vectorTabs.get(paneTab.getSelectedIndex())).saveConfig(filename);
                }
            } else
            if(event.getSource().equals(menuitemReload))
                ((EmCalcPanel)vectorTabs.get(paneTab.getSelectedIndex())).reload();
            else
            if(event.getSource().equals(menuitemExit))
                System.exit(0);
            else
            if(event.getSource().equals(menuitemAbout))
                JOptionPane.showMessageDialog(null, "Az EmCalc egy f\374stg\341z kibocs\341t\341st\nellen\u0151rz\u0151 \351s prognosztiz\341l\363 szoftver.\n\nK\351sz\355tett\351k:\n * Sajtos Levente (sajtos.levente@freemail.hu)\n * Fab\363k Zsolt (fabok@iit.uni-miskolc.hu)\n\n Verzi\363sz\341m: 1.0 Beta1", "N\351vjegy", 1);
            else
            if(event.getSource().equals(menuitemUsage))
                JOptionPane.showMessageDialog(null, "A program haszn\341lata:\n\nEls\u0151 l\351p\351sben nyissuk meg a f\341jl men\374 \372j sz\341m\355t\341s ablakot,\nmajd adjuk meg a program \341ltal k\351rt adatokat.\n\nA sz\341m\355t\341sok elv\351gz\351s\351hez sz\374ks\351ges megadni a felhaszn\341lt\ng\341z sz\341zal\351kos \366sszet\351tel\351t, illetve \341tlagh\u0151m\351rs\351klet\351t \351s az\n\374zemel\u0151 t\374zel\u0151berendez\351sek sz\341m\341t.\n\nA sz\341m\355t\341s f\374lre kattintva a program megadja az adott \341tlag\nh\u0151m\351rs\351klethez tartoz\363 f\374stg\341zkibocs\341jt\341s m\351rt\351k\351t k\366bm\351terben.\n\nLehet\u0151s\351g van arra, hogy \366sszehasonl\355t\363 sz\341m\355t\341sokat v\351gezz\374nk,\negy \372j sz\341m\355t\341s ind\355t\341s\341val.", "Haszn\341lat", 1);
        }

        MenuActionListener()
        {
            super();
        }
    }


    public static void main(String args[])
    {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        EmCalcGUI frame = new EmCalcGUI();
        frame.setDefaultCloseOperation(3);
        frame.setSize(new Dimension(585, 455));
        frame.setVisible(true);
    }

    public EmCalcGUI()
    {
        super("EmCalc 1.0 Beta1");
        lastTabIndex = 1;
        menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(368, 20));
        JMenu menuFile = new JMenu("F\341jl");
        menuFile.setMnemonic('F');
        menuitemNewTab = new JMenuItem("\332j sz\341m\355t\341s");
        menuitemNewTab.addActionListener(new MenuActionListener());
        menuitemNewTab.setMnemonic('j');
        menuitemClose = new JMenuItem("Aktu\341lis sz\341m\355t\341s bez\341r\341sa");
        menuitemClose.addActionListener(new MenuActionListener());
        menuitemClose.setMnemonic('z');
        menuitemCloseAll = new JMenuItem("Az \366sszes sz\341m\355t\341s bez\341r\341sa");
        menuitemCloseAll.addActionListener(new MenuActionListener());
        menuitemCloseAll.setMnemonic('e');
        menuitemOpenXML = new JMenuItem("Konfigur\341ci\363s f\341jl bet\366lt\351se...");
        menuitemOpenXML.addActionListener(new MenuActionListener());
        menuitemOpenXML.setMnemonic('b');
        menuitemSaveXML = new JMenuItem("Konfigur\341ci\363s f\341jl ment\351se...");
        menuitemSaveXML.addActionListener(new MenuActionListener());
        menuitemSaveXML.setMnemonic('m');
        menuitemReload = new JMenuItem("Konfigur\341ci\363s f\341jl \372jrat\366lt\351se");
        menuitemReload.addActionListener(new MenuActionListener());
        menuitemReload.setMnemonic('j');
        menuitemExit = new JMenuItem("Kil\351p\351s");
        menuitemExit.addActionListener(new MenuActionListener());
        menuitemExit.setMnemonic('K');
        menuFile.add(menuitemNewTab);
        menuFile.add(new JSeparator());
        menuFile.add(menuitemClose);
        menuFile.add(menuitemCloseAll);
        menuFile.add(new JSeparator());
        menuFile.add(menuitemOpenXML);
        menuFile.add(menuitemSaveXML);
        menuFile.add(menuitemReload);
        menuFile.add(new JSeparator());
        menuFile.add(menuitemExit);
        menuFile.addMenuListener(new MyMenuListener());
        menuitemClose.setEnabled(false);
        menuitemCloseAll.setEnabled(false);
        menuitemOpenXML.setEnabled(false);
        menuitemSaveXML.setEnabled(false);
        menuitemReload.setEnabled(false);
        JMenu menuHelp = new JMenu("Seg\355ts\351g");
        menuHelp.setMnemonic('e');
        menuitemAbout = new JMenuItem("N\351vjegy");
        menuitemAbout.addActionListener(new MenuActionListener());
        menuitemAbout.setMnemonic('N');
        menuitemUsage = new JMenuItem("Haszn\341lat");
        menuitemUsage.addActionListener(new MenuActionListener());
        menuitemAbout.setMnemonic('H');
        menuHelp.add(menuitemAbout);
        menuHelp.add(menuitemUsage);
        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        setJMenuBar(menuBar);
        paneTab = new JTabbedPane();
        paneTab.setTabLayoutPolicy(1);
        paneTab.setOpaque(true);
        getContentPane().add(paneTab, "Center");
        vectorTabs = new Vector(1, 1);
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent evt)
            {
                System.exit(0);
            }
        }
);
    }

    private void addCalculateTab()
    {
        vectorTabs.add(new EmCalcPanel());
        paneTab.addTab(lastTabIndex++ + ". Sz\341m\355t\341s", (Component)vectorTabs.lastElement());
    }

    private void closeActualTab()
    {
        int tabIndex = paneTab.getSelectedIndex();
        paneTab.remove((Component)vectorTabs.remove(tabIndex));
    }

    private void closeAll()
    {
        vectorTabs.removeAllElements();
        paneTab.removeAll();
    }

    private JTabbedPane paneTab;
    private JMenuItem menuitemNewTab;
    private JMenuItem menuitemClose;
    private JMenuItem menuitemCloseAll;
    private JMenuItem menuitemOpenXML;
    private JMenuItem menuitemSaveXML;
    private JMenuItem menuitemReload;
    private JMenuItem menuitemExit;
    private JMenuItem menuitemAbout;
    private JMenuItem menuitemUsage;
    private JMenuBar menuBar;
    private Vector vectorTabs;
    private int lastTabIndex;
    public static final String version = "1.0 Beta1";














}
