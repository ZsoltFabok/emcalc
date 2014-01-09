package com.zsoltfabok.emcalc.html;


public class NamedData
{

    public NamedData(String name, String data)
    {
        this.name = name;
        this.data = data;
    }

    public String getData()
    {
        return data;
    }

    public String getName()
    {
        return name;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private String name;
    private String data;
}
