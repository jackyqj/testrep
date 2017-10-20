package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.PoReturn;
import com.wecloud.erp.report.AbstractExcelModel;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.*;

public class PoReturnExcelModel extends AbstractExcelModel
{

    public PoReturnExcelModel(List list)
    {
        objList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        sheet.autoSizeColumn(20);
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
        PoReturn obj;
        HSSFRow excelRow;
        for(Iterator iterator = objList.iterator(); iterator.hasNext(); excelRow.createCell(5).setCellValue(obj.getDescription()))
        {
            obj = (PoReturn)iterator.next();
            excelRow = sheet.createRow(record++);
            excelRow.createCell(0).setCellValue(fmt.format(obj.getCreatedOn()));
            excelRow.createCell(1).setCellValue(obj.getCode());
            excelRow.createCell(2).setCellValue(obj.getReferenceNo());
            excelRow.createCell(3).setCellValue(obj.getQty().doubleValue());
            excelRow.createCell(4).setCellValue(obj.getCreator());
        }

    }

    public String getTitle()
    {
        return "\u91C7\u8D2D\u9000\u8D27\u5355\u5217\u8868";
    }

    List objList;
}
