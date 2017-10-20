package com.wecloud.erp.report.impl;

import com.wecloud.erp.model.CheckinRecord;
import com.wecloud.erp.report.AbstractExcelModel;
import com.wecloud.erp.utils.ErpUtils;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.hssf.usermodel.*;

public class CheckinRecordExcelModel extends AbstractExcelModel
{

    public CheckinRecordExcelModel(List list)
    {
        objList = list;
    }

    public void setHeader(HSSFSheet sheet)
    {
        int col = 0;
        HSSFRow excelHeader = sheet.createRow(0);
        excelHeader.createCell(col++).setCellValue("\u65E5\u671F");
        excelHeader.createCell(col++).setCellValue("\u7F16\u53F7");
        excelHeader.createCell(col++).setCellValue("\u7C7B\u522B");
        if(ErpUtils.hasElement(objList) && "po".equals(((CheckinRecord)objList.get(0)).getType()))
            excelHeader.createCell(col++).setCellValue("\u6536\u8D27\u901A\u77E5");
        else
            excelHeader.createCell(col++).setCellValue("\u751F\u4EA7\u901A\u77E5");
        excelHeader.createCell(col++).setCellValue("\u6570\u91CF");
        excelHeader.createCell(col++).setCellValue("\u5236\u5355\u4EBA");
        excelHeader.createCell(col++).setCellValue("\u5907\u6CE8");
    }

    public void setRows(HSSFSheet sheet)
    {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        int record = 1;
        CheckinRecord obj;
        int col;
        HSSFRow excelRow;
        for(Iterator iterator = objList.iterator(); iterator.hasNext(); excelRow.createCell(col++).setCellValue(obj.getDescription()))
        {
            obj = (CheckinRecord)iterator.next();
            col = 0;
            excelRow = sheet.createRow(record++);
            excelRow.createCell(col++).setCellValue(fmt.format(obj.getCreatedOn()));
            excelRow.createCell(col++).setCellValue(obj.getCode());
            excelRow.createCell(col++).setCellValue((String)TYPE_MAP.get(obj.getType()));
            excelRow.createCell(col++).setCellValue(obj.getName());
            excelRow.createCell(col++).setCellValue(obj.getQty().doubleValue());
            excelRow.createCell(col++).setCellValue(obj.getCreator());
        }

    }

    public String getTitle()
    {
        return "\u6536\u8D27\u901A\u77E5\u5355\u5217\u8868";
    }

    private static Map TYPE_MAP;
    List objList;

    static 
    {
        TYPE_MAP = new HashMap();
        TYPE_MAP.put("po", "\u91C7\u8D2D\u5165\u5E93");
        TYPE_MAP.put("produce", "\u6210\u54C1\u5165\u5E93");
        TYPE_MAP.put("other", "\u5176\u4ED6");
    }
}
