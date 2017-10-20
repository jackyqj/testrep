package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.QcRequest;
import com.wecloud.erp.report.AbstractExcelModel;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.hssf.usermodel.*;

public class QcRequestExcelModel extends AbstractExcelModel
{

    public QcRequestExcelModel(List list)
    {
        poList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        HSSFRow excelHeader = sheet.createRow(0);
        excelHeader.createCell(0).setCellValue("\u65E5\u671F");
        excelHeader.createCell(1).setCellValue("\u7F16\u53F7");
        excelHeader.createCell(2).setCellValue("\u4F9B\u5E94\u5546");
        excelHeader.createCell(3).setCellValue("\u6570\u91CF");
        excelHeader.createCell(4).setCellValue("\u72B6\u6001");
        excelHeader.createCell(5).setCellValue("\u6536\u8D27\u65F6\u95F4");
        excelHeader.createCell(6).setCellValue("\u5236\u5355\u4EBA");
        excelHeader.createCell(7).setCellValue("\u5907\u6CE8");
    }

    public void setRows(HSSFSheet sheet)
    {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        int record = 1;
        QcRequest obj;
        HSSFRow excelRow;
        for(Iterator iterator = poList.iterator(); iterator.hasNext(); excelRow.createCell(7).setCellValue(obj.getDescription()))
        {
            obj = (QcRequest)iterator.next();
            excelRow = sheet.createRow(record++);
            excelRow.createCell(0).setCellValue(fmt.format(obj.getCreatedOn()));
            excelRow.createCell(1).setCellValue(obj.getCode());
            excelRow.createCell(2).setCellValue(obj.getSupplierName());
            if(obj.getQty() != null)
                excelRow.createCell(3).setCellValue(obj.getQty().doubleValue());
            excelRow.createCell(4).setCellValue((String)STATUS_MAP.get(obj.getQcStatus()));
            if(obj.getDeliveryDate() != null)
                excelRow.createCell(5).setCellValue(fmt.format(obj.getDeliveryDate()));
            excelRow.createCell(6).setCellValue(obj.getCreator());
        }

    }

    public String getTitle()
    {
        return "\u6536\u8D27\u901A\u77E5\u5355\u5217\u8868";
    }

    List poList;
    private static Map STATUS_MAP;

    static 
    {
        STATUS_MAP = new HashMap();
        STATUS_MAP.put("pending", "\u672A\u68C0\u9A8C");
        STATUS_MAP.put("passed", "\u68C0\u9A8C\u901A\u8FC7");
        STATUS_MAP.put("partially", "\u90E8\u5206\u901A\u8FC7");
        STATUS_MAP.put("failed", "\u672A\u901A\u8FC7");
    }
}
