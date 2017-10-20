package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.SalesOrder;
import com.wecloud.erp.report.AbstractExcelModel;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.hssf.usermodel.*;

public class SoExcelModel extends AbstractExcelModel
{

    public SoExcelModel(List list)
    {
        soList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        HSSFRow excelHeader = sheet.createRow(0);
        excelHeader.createCell(0).setCellValue("\u8BA2\u5355\u65E5\u671F");
        excelHeader.createCell(1).setCellValue("\u8BA2\u5355\u7F16\u53F7");
        excelHeader.createCell(2).setCellValue("\u5BA2\u6237");
        excelHeader.createCell(3).setCellValue("\u91C7\u8D2D\u91D1\u989D");
        excelHeader.createCell(4).setCellValue("\u6570\u91CF");
        excelHeader.createCell(5).setCellValue("\u72B6\u6001");
        excelHeader.createCell(6).setCellValue("\u4EA4\u8D27\u65F6\u95F4");
        excelHeader.createCell(7).setCellValue("\u5236\u5355\u4EBA");
        excelHeader.createCell(8).setCellValue("\u5907\u6CE8");
    }

    public void setRows(HSSFSheet sheet)
    {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        int record = 1;
        SalesOrder obj;
        HSSFRow excelRow;
        for(Iterator iterator = soList.iterator(); iterator.hasNext(); excelRow.createCell(8).setCellValue(obj.getDescription()))
        {
            obj = (SalesOrder)iterator.next();
            excelRow = sheet.createRow(record++);
            excelRow.createCell(0).setCellValue(fmt.format(obj.getCreatedOn()));
            excelRow.createCell(1).setCellValue(obj.getCode());
            excelRow.createCell(2).setCellValue(obj.getBuyerId());
            excelRow.createCell(3).setCellValue(obj.getTotalAmount().doubleValue());
            excelRow.createCell(4).setCellValue(obj.getQty().doubleValue());
            excelRow.createCell(5).setCellValue((String)STATUS_MAP.get(obj.getPmcStatus()));
            if(obj.getDeliveryDate() != null)
                excelRow.createCell(6).setCellValue(fmt.format(obj.getDeliveryDate()));
            excelRow.createCell(7).setCellValue(obj.getCreator());
        }

    }

    public String getTitle()
    {
        return "\u9500\u552E\u8BA2\u5355\u5217\u8868";
    }

    List soList;
    private static Map STATUS_MAP;

    static 
    {
        STATUS_MAP = new HashMap();
        STATUS_MAP.put("pending", "\u672A\u5BA1\u6838");
        STATUS_MAP.put("approved", "\u5DF2\u5BA1\u6838");
        STATUS_MAP.put("processing", "\u672A\u51FA\u5E93");
        STATUS_MAP.put("partially", "\u90E8\u5206\u51FA\u5E93");
        STATUS_MAP.put("completed", "\u5168\u90E8\u51FA\u5E93");
    }
}
