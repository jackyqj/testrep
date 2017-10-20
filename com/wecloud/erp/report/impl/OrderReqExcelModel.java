package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.OrderRequest;
import com.wecloud.erp.report.AbstractExcelModel;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.*;

public class OrderReqExcelModel extends AbstractExcelModel
{

    public OrderReqExcelModel(List list)
    {
        objList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        HSSFRow excelHeader = sheet.createRow(0);
        excelHeader.createCell(0).setCellValue("\u65E5\u671F");
        excelHeader.createCell(1).setCellValue("\u7F16\u53F7");
        excelHeader.createCell(2).setCellValue("\u6570\u91CF");
        excelHeader.createCell(3).setCellValue("\u72B6\u6001");
        excelHeader.createCell(4).setCellValue("\u4EA4\u8D27\u65F6\u95F4");
        excelHeader.createCell(5).setCellValue("\u5236\u5355\u4EBA");
        excelHeader.createCell(6).setCellValue("\u5907\u6CE8");
    }

    public void setRows(HSSFSheet sheet)
    {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        int record = 1;
        OrderRequest obj;
        HSSFRow excelRow;
        for(Iterator iterator = objList.iterator(); iterator.hasNext(); excelRow.createCell(6).setCellValue(obj.getDescription()))
        {
            obj = (OrderRequest)iterator.next();
            excelRow = sheet.createRow(record++);
            excelRow.createCell(0).setCellValue(fmt.format(obj.getCreatedOn()));
            excelRow.createCell(1).setCellValue(obj.getCode());
            excelRow.createCell(2).setCellValue(obj.getQty().doubleValue());
            excelRow.createCell(3).setCellValue(getPoStatus(obj.getPmcStatus()));
            if(obj.getDeliveryDate() != null)
                excelRow.createCell(4).setCellValue(fmt.format(obj.getDeliveryDate()));
            excelRow.createCell(5).setCellValue(obj.getCreatorName());
        }

    }

    public String getTitle()
    {
        return "\u8BF7\u8D2D\u5355\u5217\u8868";
    }

    List objList;
}
