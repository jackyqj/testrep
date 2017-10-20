package com.wecloud.erp.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.wecloud.erp.model.Customer;
import com.wecloud.erp.model.Item;
import com.wecloud.erp.model.Sequence;

public class ErpUtils
{

    public ErpUtils()
    {
    }

    public static List getList(String input)
    {
        List result = new ArrayList();
        String as[];
        int j = (as = input.split(",")).length;
        for(int i = 0; i < j; i++)
        {
            String ele = as[i];
            if(ele != null && ele.length() > 0)
                result.add(ele);
        }

        return result;
    }

    public static boolean isNotEmpty(String input)
    {
        return input != null && input.length() > 0;
    }

    public static boolean hasElement(Collection col)
    {
        return col != null && col.size() > 0;
    }

    public static String generateRefNo(Sequence seq)
    {
        String pattern = seq.getPattern();
        String refNo = pattern.replace("{DATE}", DFMT.format(new Date())).replace("{SEQ}", (new StringBuilder()).append(seq.getSeq()).toString());
        return refNo;
    }

    public static String genBomCode(Item item, Sequence seq)
    {
        return (new StringBuilder("3")).append(item.getType()).append("-").append(NFMT5.format(seq.getSeq())).append("-").append(item.getSourceCode()).append("A").toString();
    }

    public static String genItemCode(Item item, Sequence seq)
    {
        return (new StringBuilder(String.valueOf(item.getType()))).append(item.getCodelist()).append("-").append(NFMT5.format(seq.getSeq())).append("-").append(item.getSourceCode()).append("A").toString();
    }

    public static String genCustomerCode(Customer cust, Sequence seq)
    {
        if("buyer".equals(cust.getCategory())) {
            return (new StringBuilder("C")).append(cust.getCountryCode()).append(cust.getDomainCode()).append(YFMT.format(cust.getConnectedDate())).append("-").append(NFMT4.format(seq.getSeq())).toString();
        }
        else {
        	String idc = "S";
        	if (BooleanUtils.toBoolean(cust.getIsTemp())) {
        		idc = "L";
        	}
            return (new StringBuilder(idc)).append(cust.getCountryCode()).append(YFMT.format(cust.getConnectedDate())).append("-").append(NFMT4.format(seq.getSeq())).toString();
        }
    }

    public static double num(Double numObj)
    {
        double result = 0.0D;
        if(numObj != null)
            result = numObj.doubleValue();
        return result;
    }

    public static void main(String args[])
    {
        System.out.println(NFMT4.format(0x1e240L));
    }

    private static final SimpleDateFormat DFMT = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat YFMT = new SimpleDateFormat("yyyy");
    private static final DecimalFormat NFMT4 = new DecimalFormat("0000");
    private static final DecimalFormat NFMT5 = new DecimalFormat("00000");

}
