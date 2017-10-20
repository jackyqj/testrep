package com.wecloud.erp.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wecloud.erp.entity.Search;
import com.wecloud.erp.entity.UserDetail;
import com.wecloud.erp.model.ErpRole;
import com.wecloud.erp.model.ErpUser;
import com.wecloud.erp.model.ErpUserExample;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.UserManager;
import com.wecloud.erp.utils.ErpUtils;
import com.wecloud.erp.utils.UUID;
import com.wecloud.erp.web.exception.ActionException;

@Controller
public class UserController
{

    public UserController()
    {
    }

    @RequestMapping(value={"/saveUser.do"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public UserDetail saveUser(@RequestBody UserDetail userDetail)
    {
        ErpUser obj = userDetail.getUser();
        List roles = userDetail.getRoles();
        ErpUser user = userManager.getErpUser(obj.getLoginId());
        if(user == null)
        {
            user = new ErpUser();
            user.setId(UUID.get());
            user.setName(obj.getName());
            user.setLoginId(obj.getLoginId());
            Md5PasswordEncoder enc = new Md5PasswordEncoder();
            user.setPassword(enc.encodePassword(obj.getPassword(), user.getLoginId()));
            userManager.addErpUser(user);
        } else
        if(user.getId().equals(obj.getId()))
        {
            user.setName(obj.getName());
            if(ErpUtils.isNotEmpty(obj.getPassword()))
            {
                Md5PasswordEncoder enc = new Md5PasswordEncoder();
                user.setPassword(enc.encodePassword(obj.getPassword(), user.getLoginId()));
            }
            userManager.updateErpUser(user);
            userManager.deleteErpRoleByUserId(obj.getId());
        } else
        {
            throw new ActionException();
        }
        ErpRole role;
        for(Iterator iterator = roles.iterator(); iterator.hasNext(); userManager.addErpRole(role))
        {
            role = (ErpRole)iterator.next();
            role.setId(UUID.get());
            role.setUserId(user.getId());
        }

        user.setPassword(null);
        userDetail.setUser(user);
        return userDetail;
    }

    @RequestMapping(value={"/loadUser.do"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public UserDetail loadUser(@RequestParam String userId)
    {
        ErpUser user = userManager.getErpUserById(userId);
        if(user == null)
        {
            throw new ActionException();
        } else
        {
            List roles = userManager.listErpUserRoles(user);
            UserDetail obj = new UserDetail();
            user.setPassword(null);
            obj.setUser(user);
            obj.setRoles(roles);
            user.setPassword(null);
            return obj;
        }
    }

    @RequestMapping(value={"/removeUser.do"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Object removeUser(@RequestParam String userId)
    {
        userManager.deleteErpRoleByUserId(userId);
        userManager.deleteErpUser(userId);
        return "ok";
    }

    @RequestMapping(value={"/listUsers.do"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Map listUsers(@RequestBody Search search)
    {
        String keyword = search.getKeyword();
        ErpUserExample query = new ErpUserExample();
        if(ErpUtils.isNotEmpty(keyword))
        {
            query.createCriteria().andLoginIdLike((new StringBuilder("%")).append(keyword).append("%").toString());
            query.or().andNameLike((new StringBuilder("%")).append(keyword).append("%").toString());
        }
        String limitStart = (String)StringUtils.defaultIfEmpty(search.getLimitStart(), "0");
        String limitEnd = search.getLimitEnd();
        if(limitEnd == null)
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List users = userManager.listErpUsers(query);
        Map result = new HashMap();
        int count = userManager.countUsers(query);
        result.put("totalCount", Integer.valueOf(count));
        result.put("objList", users);
        return result;
    }

    private static final long serialVersionUID = 0xee5381c5d0ac8e8cL;
    @Resource
    private ItemManager itemManager;
    @Resource
    private UserManager userManager;
}
