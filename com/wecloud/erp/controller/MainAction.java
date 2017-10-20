package com.wecloud.erp.controller;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.DispatcherServlet;

public class MainAction extends DispatcherServlet
{

    public MainAction()
    {
    }

    public void init(ServletConfig config)
        throws ServletException
    {
        super.init(config);
        info((new StringBuilder("init path: ")).append(config.getServletContext().getContextPath()).toString());
    }

    protected void doService(HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        if(request.getSession().getAttribute("SESSION-USER") == null && request.getRequestURI().indexOf("login") < 0)
        {
            Logger.getLogger(getClass()).error((new StringBuilder("User is not login!")).append(request.getContextPath()).toString());
            request.setAttribute("relogin", Boolean.valueOf(true));
            response.setStatus(401);
        } else
        {
            super.doService(request, response);
        }
    }

    protected void log(Object msg)
    {
        Logger.getLogger(getClass()).debug(msg);
    }

    protected void info(Object msg)
    {
        Logger.getLogger(getClass()).info(msg);
    }

    private static final long serialVersionUID = 0xd2313b0d885e8e13L;
}
