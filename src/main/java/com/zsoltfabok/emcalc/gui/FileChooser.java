package com.zsoltfabok.emcalc.gui;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class FileChooser
{
    private static class MySimpleFilter extends FileFilter
    {

        public void setExtension(String extension)
        {
            this.extension = extension;
            description = "*." + extension;
        }

        public boolean accept(File file)
        {
            if(file.isDirectory())
                return true;
            String name = file.getName();
            int dotIndex = name.lastIndexOf('.');
            if(dotIndex == -1 || dotIndex == name.length() - 1)
            {
                return false;
            } else
            {
                String extension = name.substring(dotIndex + 1);
                return extension.equalsIgnoreCase(this.extension);
            }
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public String getExtension()
        {
            return extension;
        }

        private String extension;
        private String description;

        MySimpleFilter()
        {
            extension = "";
            description = "";
        }
    }


    public FileChooser()
    {
    }

    public static String getFileName(String initialDirectory, String extension, String title, int mode)
    {
        JFileChooser chooser = new JFileChooser(initialDirectory);
        chooser.setDialogTitle(title);
        MySimpleFilter filter = new MySimpleFilter();
        filter.setExtension(extension);
        chooser.setFileFilter(filter);
        String path = null;
        int result = mode != 0 ? chooser.showSaveDialog(null) : chooser.showOpenDialog(null);
        if(result == 0)
        {
            path = chooser.getSelectedFile().getAbsolutePath();
            if(mode == 1)
            {
                if(!path.endsWith("." + extension))
                    path = path + "." + extension;
                if((new File(path)).exists())
                {
                    Object options[] = {
                        "Igen", "M\351gsem"
                    };
                    int result2 = JOptionPane.showOptionDialog(null, "A(z) \"" + path + "\" f\341jl m\341r l\351tezik. Fel\374l\355rja?", "Figyelmeztet\351s!", 0, 2, null, options, options[0]);
                    if(result2 != 0)
                        path = null;
                }
            }
        }
        return path;
    }
}
