package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.ItemStorage;
import com.wecloud.erp.report.AbstractExcelModel;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.*;

public class ItemStorageExcelModel extends AbstractExcelModel
{

    public ItemStorageExcelModel(List list)
    {
        objList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        int col = 0;
        HSSFRow excelHeader = sheet.createRow(0);
        excelHeader.createCell(col++).setCellValue("\u7269\u6599\u7C7B\u578B");
        excelHeader.createCell(col++).setCellValue("\u7269\u6599\u7F16\u7801");
        excelHeader.createCell(col++).setCellValue("\u540D\u79F0");
        excelHeader.createCell(col++).setCellValue("\u578B\u53F7\u89C4\u683C");
        excelHeader.createCell(col++).setCellValue("\u5C01\u88C5");
        excelHeader.createCell(col++).setCellValue("\u4ED3\u5E93");
        excelHeader.createCell(col++).setCellValue("\u6570\u91CF");
        excelHeader.createCell(col++).setCellValue("\u5355\u4F4D");
        excelHeader.createCell(col++).setCellValue("\u54C1\u724C");
    }

    public void setRows(HSSFSheet sheet)
    {
        int record = 1;
        ItemStorage obj;
        int col;
        HSSFRow excelRow;
        for(Iterator iterator = objList.iterator(); iterator.hasNext(); excelRow.createCell(col++).setCellValue(obj.getBrand()))
        {
            obj = (ItemStorage)iterator.next();
            col = 0;
            excelRow = sheet.createRow(record++);
            excelRow.createCell(col++).setCellValue(obj.getType());
            excelRow.createCell(col++).setCellValue(obj.getCode());
            excelRow.createCell(col++).setCellValue(obj.getName());
            excelRow.createCell(col++).setCellValue(obj.getStyle());
            excelRow.createCell(col++).setCellValue(obj.getPackType());
            excelRow.createCell(col++).setCellValue(getStorage(obj.getStorageType()));
            excelRow.createCell(col++).setCellValue(obj.getQty().doubleValue());
            excelRow.createCell(col++).setCellValue(obj.getUom());
        }

    }

    public String getTitle()
    {
        return "\u5E93\u5B58\u5217\u8868";
    }

    List objList;
}
