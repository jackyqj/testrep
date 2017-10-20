package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.Item;
import com.wecloud.erp.report.AbstractExcelModel;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.*;

public class ItemExcelModel extends AbstractExcelModel
{

    public ItemExcelModel(List list)
    {
        itemList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        HSSFRow excelHeader = sheet.createRow(0);
        excelHeader.createCell(0).setCellValue("\u7269\u6599\u7C7B\u578B");
        excelHeader.createCell(1).setCellValue("\u7269\u6599\u7F16\u7801");
        excelHeader.createCell(2).setCellValue("\u540D\u79F0");
        excelHeader.createCell(3).setCellValue("\u578B\u53F7\u89C4\u683C");
        excelHeader.createCell(4).setCellValue("\u5C01\u88C5");
        excelHeader.createCell(5).setCellValue("\u5355\u4F4D");
        excelHeader.createCell(6).setCellValue("\u4EF7\u683C");
        excelHeader.createCell(7).setCellValue("\u5907\u6CE8");
    }

    public void setRows(HSSFSheet sheet)
    {
        int record = 1;
        Item item;
        HSSFRow excelRow;
        for(Iterator iterator = itemList.iterator(); iterator.hasNext(); excelRow.createCell(7).setCellValue(item.getRemark()))
        {
            item = (Item)iterator.next();
            excelRow = sheet.createRow(record++);
            excelRow.createCell(0).setCellValue(item.getType());
            excelRow.createCell(1).setCellValue(item.getCode());
            excelRow.createCell(2).setCellValue(item.getName());
            excelRow.createCell(3).setCellValue(item.getStyle());
            excelRow.createCell(4).setCellValue(item.getPackType());
            excelRow.createCell(5).setCellValue(item.getUom());
            excelRow.createCell(6).setCellValue(item.getOrderPrice().doubleValue());
        }

    }

    public String getTitle()
    {
        return "\u7269\u6599\u5217\u8868";
    }

    List itemList;
}
