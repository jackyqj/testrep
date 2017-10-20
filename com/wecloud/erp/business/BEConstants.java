package com.wecloud.erp.business;


public class BEConstants
{

    public BEConstants()
    {
    }

    public static Integer STATUS_DRAFT = Integer.valueOf(0);
    public static Integer STATUS_PENDING = Integer.valueOf(1);
    public static Integer STATUS_FINAL = Integer.valueOf(2);
    public static Integer STATUS_INVALID = Integer.valueOf(3);
    public static String CHECKIN_TYPE_PO = "po";
    public static String CHECKIN_TYPE_PRODUCE = "produce";
    public static String CHECKOUT_TYPE_SALES = "sales";

}
