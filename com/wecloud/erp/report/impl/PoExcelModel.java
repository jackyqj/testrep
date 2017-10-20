package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.PO;
import com.wecloud.erp.report.AbstractExcelModel;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.*;

public class PoExcelModel extends AbstractExcelModel
{

    public PoExcelModel(List list)
    {
        poList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        HSSFRow excelHeader = sheet.createRow(0);
        excelHeader.createCell(0).setCellValue("\u8BA2\u5355\u65E5\u671F");
        excelHeader.createCell(1).setCellValue("\u8BA2\u5355\u7F16\u53F7");
        excelHeader.createCell(2).setCellValue("\u4F9B\u5E94\u5546");
        excelHeader.createCell(3).setCellValue("\u91C7\u8D2D\u91D1\u989D");
        excelHeader.createCell(4).setCellValue("\u6570\u91CF");
        excelHeader.createCell(5).setCellValue("\u72B6\u6001");
        excelHeader.createCell(6).setCellValue("\u4EA4\u8D27\u65F6\u95F4");
        excelHeader.createCell(7).setCellValue("\u5236\u5355\u4EBA");
        excelHeader.createCell(8).setCellValue("\u5907\u6CE8");
    }

    public void setRows(HSSFSheet sheet)
    {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        int record = 1;
        PO po;
        HSSFRow excelRow;
        for(Iterator iterator = poList.iterator(); iterator.hasNext(); excelRow.createCell(8).setCellValue(po.getDescription()))
        {
            po = (PO)iterator.next();
            excelRow = sheet.createRow(record++);
            excelRow.createCell(0).setCellValue(fmt.format(po.getCreatedOn()));
            excelRow.createCell(1).setCellValue(po.getCode());
            excelRow.createCell(2).setCellValue(po.getSupplierName());
            excelRow.createCell(3).setCellValue(po.getTotalAmount().doubleValue());
            excelRow.createCell(4).setCellValue(po.getQty().doubleValue());
            excelRow.createCell(5).setCellValue(getPoStatus(po.getPmcStatus()));
            excelRow.createCell(6).setCellValue(fmt.format(po.getDeliveryDate()));
            excelRow.createCell(7).setCellValue(po.getCreatorName());
        }

    }

    public String getTitle()
    {
        return "\u8BA2\u5355\u5217\u8868";
    }

    List poList;
}
