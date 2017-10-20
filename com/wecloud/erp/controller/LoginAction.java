package com.wecloud.erp.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wecloud.erp.entity.SessionUser;
import com.wecloud.erp.model.ErpUser;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.UserManager;

@Controller
public class LoginAction
{

    public LoginAction()
    {
    }

    @RequestMapping(value={"/sales/test.do"})
    public String test(HttpServletRequest request)
    {
        return "login";
    }

    @RequestMapping(value={"/login.do"})
    public String login(@RequestParam String userName, @RequestParam String password, HttpServletRequest request)
    {
        ErpUser user = userManager.getErpUser(userName);
        if(user == null) {
        	request.setAttribute("message", "notfound");
        	return "login-failed";
        }
        Md5PasswordEncoder enc = new Md5PasswordEncoder();
        if(!enc.isPasswordValid(user.getPassword(), password, userName)) {
        	request.setAttribute("message", "password");
        	return "login-failed";
        }
        SessionUser sessionUser = new SessionUser(user);
        request.getSession().setAttribute("SESSION-USER", sessionUser);
        return "homepage";
    }

    @RequestMapping(value={"/logout.do"})
    public String validataUser(HttpServletRequest request, HttpServletResponse response)
    {
        request.getSession().removeAttribute("SESSION-USER");
        return "logout";
    }

    public void setItemManager(ItemManager im)
    {
        itemManager = im;
    }

    public ItemManager getItemManager()
    {
        return itemManager;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPwd()
    {
        return pwd;
    }

    public void setPwd(String pwd)
    {
        this.pwd = pwd;
    }

    private static final long serialVersionUID = 0xee5381c5d0ac8e8cL;
    @Resource
    private ItemManager itemManager;
    @Resource
    private UserManager userManager;
    private String name;
    private String pwd;
}
