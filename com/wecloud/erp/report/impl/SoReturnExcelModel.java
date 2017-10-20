package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.SoReturn;
import com.wecloud.erp.report.AbstractExcelModel;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.hssf.usermodel.*;

public class SoReturnExcelModel extends AbstractExcelModel
{

    public SoReturnExcelModel(List list)
    {
        poList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        HSSFRow excelHeader = sheet.createRow(0);
        excelHeader.createCell(0).setCellValue("\u521B\u5EFA\u65E5\u671F");
        excelHeader.createCell(1).setCellValue("\u5355\u7F16\u53F7");
        excelHeader.createCell(2).setCellValue("\u9500\u552E\u8BA2\u5355");
        excelHeader.createCell(3).setCellValue("\u6570\u91CF");
        excelHeader.createCell(4).setCellValue("\u5236\u5355\u4EBA");
        excelHeader.createCell(5).setCellValue("\u5907\u6CE8");
    }

    public void setRows(HSSFSheet sheet)
    {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        int record = 1;
        SoReturn po;
        HSSFRow excelRow;
        for(Iterator iterator = poList.iterator(); iterator.hasNext(); excelRow.createCell(5).setCellValue(po.getDescription()))
        {
            po = (SoReturn)iterator.next();
            excelRow = sheet.createRow(record++);
            excelRow.createCell(0).setCellValue(fmt.format(po.getCreatedOn()));
            excelRow.createCell(1).setCellValue(po.getCode());
            excelRow.createCell(2).setCellValue(po.getReferenceNo());
            excelRow.createCell(3).setCellValue(po.getQty().doubleValue());
            excelRow.createCell(4).setCellValue(po.getCreator());
        }

    }

    public String getTitle()
    {
        return "\u9500\u552E\u9000\u8D27\u5355\u5217\u8868";
    }

    List poList;
    private static Map STATUS_MAP;

    static 
    {
        STATUS_MAP = new HashMap();
        STATUS_MAP.put("pending", "\u672A\u5BA1\u6838");
        STATUS_MAP.put("approved", "\u5DF2\u5BA1\u6838");
        STATUS_MAP.put("processing", "\u672A\u51FA\u5E93");
        STATUS_MAP.put("partially", "\u90E8\u5206\u5165\u5E93");
        STATUS_MAP.put("completed", "\u5168\u90E8\u5165\u5E93");
    }
}
