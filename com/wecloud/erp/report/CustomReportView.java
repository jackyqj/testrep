package com.wecloud.erp.report;

import java.util.Map;

import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class CustomReportView extends JasperReportsMultiFormatView
{

    public CustomReportView()
    {
    }

    protected JasperPrint fillReport(Map model)
        throws Exception
    {
        if(model.containsKey("url"))
        {
            setUrl(String.valueOf(model.get("url")));
            report = loadReport();
        }
        model.put(DIR_KEY, getServletContext().getRealPath("/WEB-INF/jasper/"));
        return super.fillReport(model);
    }

    protected JasperReport getReport()
    {
        return report;
    }

    private JasperReport report;
    private static String DIR_KEY = "SUBREPORT_DIR";

}
