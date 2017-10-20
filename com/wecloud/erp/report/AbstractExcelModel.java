package com.wecloud.erp.report;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;

public abstract class AbstractExcelModel
{

    public AbstractExcelModel()
    {
    }

    public abstract String getTitle();

    public abstract void setHeader(HSSFSheet hssfsheet);

    public abstract void setRows(HSSFSheet hssfsheet);

    protected String getStorage(String id)
    {
        return (String)STORAGE_MAP.get(id);
    }

    protected String getPoStatus(String pmcStatus)
    {
        if(STATUS_MAP.containsKey(pmcStatus))
            return (String)STATUS_MAP.get(pmcStatus);
        else
            return DEFAULT_STATUS;
    }

    private static String DEFAULT_STATUS = "\u672A\u5BA1\u6838";
    private static Map STATUS_MAP;
    private static Map STORAGE_MAP;

    static 
    {
        STATUS_MAP = new HashMap();
        STATUS_MAP.put("pending", "\u672A\u5BA1\u6838");
        STATUS_MAP.put("approved", "\u5DF2\u5BA1\u6838");
        STATUS_MAP.put("processing", "\u672A\u5165\u5E93");
        STATUS_MAP.put("partially", "\u90E8\u5206\u5165\u5E93");
        STATUS_MAP.put("completed", "\u5168\u90E8\u5165\u5E93");
        STORAGE_MAP = new HashMap();
        STORAGE_MAP.put("product", "\u6210\u54C1\u5E93");
        STORAGE_MAP.put("electronic", "\u7535\u5B50\u5E93");
    }
}
