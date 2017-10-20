package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.Faulty;
import com.wecloud.erp.report.AbstractExcelModel;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.*;

public class FaultyExcelModel extends AbstractExcelModel
{

    public FaultyExcelModel(List list)
    {
        objList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        HSSFRow excelHeader = sheet.createRow(0);
        int col = 0;
        excelHeader.createCell(col++).setCellValue("\u65E5\u671F");
        excelHeader.createCell(col++).setCellValue("\u7F16\u7801");
        excelHeader.createCell(col++).setCellValue("\u4F9B\u5E94\u5546");
        excelHeader.createCell(col++).setCellValue("\u6570\u91CF");
        excelHeader.createCell(col++).setCellValue("\u5236\u5355\u4EBA");
        excelHeader.createCell(col++).setCellValue("\u5907\u6CE8");
    }

    public void setRows(HSSFSheet sheet)
    {
        int record = 1;
        Faulty obj;
        int col;
        HSSFRow excelRow;
        for(Iterator iterator = objList.iterator(); iterator.hasNext(); excelRow.createCell(col++).setCellValue(obj.getDescription()))
        {
            obj = (Faulty)iterator.next();
            col = 0;
            excelRow = sheet.createRow(record++);
            excelRow.createCell(col++).setCellValue(FMT.format(obj.getCreatedOn()));
            excelRow.createCell(col++).setCellValue(obj.getCode());
            excelRow.createCell(col++).setCellValue(obj.getSupplierId());
            excelRow.createCell(col++).setCellValue(obj.getQty().doubleValue());
            excelRow.createCell(col++).setCellValue(obj.getCreator());
        }

    }

    public String getTitle()
    {
        return "\u62A5\u5E9F\u5355\u5217\u8868";
    }

    List objList;
    private static SimpleDateFormat FMT = new SimpleDateFormat("yyyy-MM-dd");

}
