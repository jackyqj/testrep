package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.ProduceRequestView;
import com.wecloud.erp.report.AbstractExcelModel;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.hssf.usermodel.*;

public class PrdReqExcelModel extends AbstractExcelModel
{

    public PrdReqExcelModel(List list)
    {
        poList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        HSSFRow excelHeader = sheet.createRow(0);
        excelHeader.createCell(0).setCellValue("\u5236\u4EE4\u5355\u65E5\u671F");
        excelHeader.createCell(1).setCellValue("\u5236\u4EE4\u5355\u7F16\u53F7");
        excelHeader.createCell(2).setCellValue("\u8BA2\u5355\u7F16\u53F7");
        excelHeader.createCell(3).setCellValue("\u6570\u91CF");
        excelHeader.createCell(4).setCellValue("\u751F\u4EA7\u72B6\u6001");
        excelHeader.createCell(5).setCellValue("\u4EA4\u8D27\u65F6\u95F4");
        excelHeader.createCell(6).setCellValue("\u5236\u5355\u4EBA");
        excelHeader.createCell(7).setCellValue("\u5907\u6CE8");
    }

    public void setRows(HSSFSheet sheet)
    {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        int record = 1;
        ProduceRequestView po;
        HSSFRow excelRow;
        for(Iterator iterator = poList.iterator(); iterator.hasNext(); excelRow.createCell(7).setCellValue(po.getDescription()))
        {
            po = (ProduceRequestView)iterator.next();
            excelRow = sheet.createRow(record++);
            excelRow.createCell(0).setCellValue(fmt.format(po.getCreatedOn()));
            excelRow.createCell(1).setCellValue(po.getCode());
            excelRow.createCell(2).setCellValue(po.getReqCode());
            excelRow.createCell(3).setCellValue(po.getQty().doubleValue());
            excelRow.createCell(4).setCellValue((String)STATUS_MAP.get(po.getPmcStatus()));
            if(po.getDeliveryDate() != null)
                excelRow.createCell(5).setCellValue(fmt.format(po.getDeliveryDate()));
            excelRow.createCell(6).setCellValue(po.getCreator());
        }

    }

    public String getTitle()
    {
        return "\u5236\u4EE4\u5355\u5217\u8868";
    }

    List poList;
    private static Map STATUS_MAP;

    static 
    {
        STATUS_MAP = new HashMap();
        STATUS_MAP.put("processing", "\u672A\u5165\u5E93");
        STATUS_MAP.put("partially", "\u90E8\u5206\u5165\u5E93");
        STATUS_MAP.put("completed", "\u5168\u90E8\u5165\u5E93");
    }
}
