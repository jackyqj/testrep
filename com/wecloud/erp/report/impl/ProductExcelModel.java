package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.Product;
import com.wecloud.erp.report.AbstractExcelModel;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.*;

public class ProductExcelModel extends AbstractExcelModel
{

    public ProductExcelModel(List list)
    {
        objList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        HSSFRow excelHeader = sheet.createRow(0);
        int row = 0;
        excelHeader.createCell(row++).setCellValue("\u5546\u54C1\u7F16\u7801");
        excelHeader.createCell(row++).setCellValue("\u5546\u54C1\u540D\u79F0");
        excelHeader.createCell(row++).setCellValue("\u578B\u53F7\u89C4\u683C");
        excelHeader.createCell(row++).setCellValue("\u5355\u4F4D");
        excelHeader.createCell(row++).setCellValue("\u5907\u6CE8");
    }

    public void setRows(HSSFSheet sheet)
    {
        int record = 1;
        Product obj;
        int row;
        HSSFRow excelRow;
        for(Iterator iterator = objList.iterator(); iterator.hasNext(); excelRow.createCell(row++).setCellValue(obj.getRemark()))
        {
            obj = (Product)iterator.next();
            row = 0;
            excelRow = sheet.createRow(record++);
            excelRow.createCell(row++).setCellValue(obj.getCode());
            excelRow.createCell(row++).setCellValue(obj.getName());
            excelRow.createCell(row++).setCellValue(obj.getStyle());
            excelRow.createCell(row++).setCellValue(obj.getUom());
        }

    }

    public String getTitle()
    {
        return "\u5546\u54C1\u5217\u8868";
    }

    List objList;
}
