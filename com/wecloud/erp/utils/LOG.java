package com.wecloud.erp.utils;

import org.apache.log4j.Logger;

public class LOG
{

    public LOG()
    {
    }

    public static void debug(Logger l, Object msg)
    {
        if(l.isDebugEnabled())
            l.debug(msg);
    }

    public static void info(Logger l, Object msg)
    {
        if(l.isInfoEnabled())
            l.info(msg);
    }

    public static void warn(Logger l, Object msg)
    {
        l.warn(msg);
    }

    public static void warn(Logger l, Object msg, Throwable t)
    {
        l.warn(msg, t);
    }
}
