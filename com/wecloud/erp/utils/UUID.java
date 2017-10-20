package com.wecloud.erp.utils;


public class UUID
{

    public UUID()
    {
    }

    public static String get()
    {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
