package com.wecloud.erp.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ErpHandlerInterceptor extends HandlerInterceptorAdapter
{

    public ErpHandlerInterceptor()
    {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception
    {
        boolean result = super.preHandle(request, response, handler);
        return result;
    }
}
