package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.CheckoutRecord;
import com.wecloud.erp.report.AbstractExcelModel;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.hssf.usermodel.*;

public class CheckoutRecordExcelModel extends AbstractExcelModel
{

    public CheckoutRecordExcelModel(List list)
    {
        objList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        int col = 0;
        HSSFRow excelHeader = sheet.createRow(0);
        excelHeader.createCell(col++).setCellValue("\u65E5\u671F");
        excelHeader.createCell(col++).setCellValue("\u7F16\u53F7");
        excelHeader.createCell(col++).setCellValue("\u7C7B\u578B");
        excelHeader.createCell(col++).setCellValue("\u6570\u91CF");
        excelHeader.createCell(col++).setCellValue("\u5236\u5355\u4EBA");
        excelHeader.createCell(col++).setCellValue("\u5907\u6CE8");
    }

    public void setRows(HSSFSheet sheet)
    {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        int record = 1;
        CheckoutRecord obj;
        int col;
        HSSFRow excelRow;
        for(Iterator iterator = objList.iterator(); iterator.hasNext(); excelRow.createCell(col++).setCellValue(obj.getDescription()))
        {
            obj = (CheckoutRecord)iterator.next();
            col = 0;
            excelRow = sheet.createRow(record++);
            excelRow.createCell(col++).setCellValue(fmt.format(obj.getCreatedOn()));
            excelRow.createCell(col++).setCellValue(obj.getCode());
            excelRow.createCell(col++).setCellValue((String)TYPE_MAP.get(obj.getType()));
            excelRow.createCell(col++).setCellValue(obj.getQty().doubleValue());
            excelRow.createCell(col++).setCellValue(obj.getCreator());
        }

    }

    public String getTitle()
    {
        return "\u51FA\u5E93\u5355\u5217\u8868";
    }

    private static Map TYPE_MAP;
    List objList;

    static 
    {
        TYPE_MAP = new HashMap();
        TYPE_MAP.put("sales", "\u9500\u552E");
        TYPE_MAP.put("produce", "\u751F\u4EA7");
        TYPE_MAP.put("research", "\u7814\u53D1");
    }
}
