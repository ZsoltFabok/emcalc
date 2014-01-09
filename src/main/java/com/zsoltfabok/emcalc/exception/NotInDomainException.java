package com.zsoltfabok.emcalc.exception;


public class NotInDomainException extends Exception
{

    public NotInDomainException()
    {
    }

    public NotInDomainException(String message)
    {
        super(message);
    }

    public NotInDomainException(double x)
    {
        value = x;
    }

    private double value;
}
