package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.PoSummary;
import com.wecloud.erp.report.AbstractExcelModel;
import java.util.Collection;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.*;

public class PoSummaryExcelModel extends AbstractExcelModel
{

    public PoSummaryExcelModel(Collection list)
    {
        objList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        HSSFRow excelHeader = sheet.createRow(0);
        int row = 0;
        excelHeader.createCell(row++).setCellValue("\u7269\u6599\u7F16\u7801");
        excelHeader.createCell(row++).setCellValue("\u7269\u6599\u540D\u79F0");
        excelHeader.createCell(row++).setCellValue("\u578B\u53F7\u89C4\u683C");
        excelHeader.createCell(row++).setCellValue("\u5355\u4EF7");
        excelHeader.createCell(row++).setCellValue("\u5355\u4F4D");
        excelHeader.createCell(row++).setCellValue("\u6570\u91CF");
        excelHeader.createCell(row++).setCellValue("\u91C7\u8D2D\u91D1\u989D");
    }

    public void setRows(HSSFSheet sheet)
    {
        int record = 1;
        double totalQty = 0.0D;
        double totalAmount = 0.0D;
        for(Iterator iterator = objList.iterator(); iterator.hasNext();)
        {
            PoSummary obj = (PoSummary)iterator.next();
            int row = 0;
            HSSFRow excelRow = sheet.createRow(record++);
            excelRow.createCell(row++).setCellValue(obj.getCode());
            excelRow.createCell(row++).setCellValue(obj.getName());
            excelRow.createCell(row++).setCellValue(obj.getStyle());
            excelRow.createCell(row++).setCellValue(obj.getUnitPrice().doubleValue());
            excelRow.createCell(row++).setCellValue(obj.getUom());
            excelRow.createCell(row++).setCellValue(obj.getQty().doubleValue());
            excelRow.createCell(row++).setCellValue(obj.getAmount().doubleValue());
            totalQty += obj.getQty().doubleValue();
            totalAmount += obj.getAmount().doubleValue();
        }

        HSSFRow excelRow = sheet.createRow(record++);
        excelRow.createCell(4).setCellValue("\u5408\u8BA1");
        excelRow.createCell(5).setCellValue(totalQty);
        excelRow.createCell(6).setCellValue(totalAmount);
    }

    public String getTitle()
    {
        return "\u91C7\u8D2D\u7EDF\u8BA1";
    }

    Collection objList;
}
