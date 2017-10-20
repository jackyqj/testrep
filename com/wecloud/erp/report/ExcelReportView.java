package com.wecloud.erp.report;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

// Referenced classes of package com.wecloud.erp.report:
//            AbstractExcelModel

public class ExcelReportView extends AbstractExcelView
{

    public ExcelReportView()
    {
    }

    protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        AbstractExcelModel excelModel = (AbstractExcelModel)model.get("excelModel");
        org.apache.poi.hssf.usermodel.HSSFSheet excelSheet = workbook.createSheet(excelModel.getTitle());
        excelModel.setHeader(excelSheet);
        excelModel.setRows(excelSheet);
    }
}
