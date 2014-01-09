package com.zsoltfabok.emcalc.calculation;


public class Interval
{

    public Interval(double boundLeft, double boundRight)
    {
        this.boundLeft = boundLeft;
        this.boundRight = boundRight;
        isOpenedLeft = false;
        isOpenedRight = false;
    }

    public double getBoundLeft()
    {
        return boundLeft;
    }

    public double getBoundRight()
    {
        return boundRight;
    }

    public void setBoundLeft(double borderLeft)
    {
        boundLeft = borderLeft;
    }

    public void setBoundRight(double borderRight)
    {
        boundRight = borderRight;
    }

    public void setLeftOpened()
    {
        isOpenedLeft = true;
    }

    public void setLeftClosed()
    {
        isOpenedLeft = false;
    }

    public void setRightOpened()
    {
        isOpenedRight = true;
    }

    public void setRightClosed()
    {
        isOpenedRight = false;
    }

    public boolean inInterval(double value)
    {
        boolean returnValue = true;
        if(boundLeft <= value && value <= boundRight)
        {
            if(isOpenedLeft && value == boundLeft)
                returnValue = false;
            if(isOpenedRight && value == boundRight)
                returnValue = false;
        } else
        {
            returnValue = false;
        }
        return returnValue;
    }

    private double boundLeft;
    private double boundRight;
    private boolean isOpenedLeft;
    private boolean isOpenedRight;
}
