package com.wecloud.erp.entity;

import java.util.Date;

import com.wecloud.erp.model.ErpUser;

public class SessionUser
{

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public SessionUser(String loginId)
    {
        setLoginId(loginId);
        setLoginOn(new Date());
        setLastActivedOn(new Date());
        setSessionTimeout(DEFAULT_TIMEOUT);
    }

    public SessionUser(ErpUser erpUser)
    {
        this(erpUser.getLoginId());
        setId(erpUser.getId());
        setName(erpUser.getName());
    }

    public long getSessionTimeout()
    {
        return sessionTimeout;
    }

    public void setSessionTimeout(long sessionTimeout)
    {
        this.sessionTimeout = sessionTimeout;
    }

    public String getLoginId()
    {
        return loginId;
    }

    public void setLoginId(String loginId)
    {
        this.loginId = loginId;
    }

    public Date getLoginOn()
    {
        return loginOn;
    }

    public void setLoginOn(Date loginOn)
    {
        this.loginOn = loginOn;
    }

    public Date getLastActivedOn()
    {
        return lastActivedOn;
    }

    public void setLastActivedOn(Date lastActivedOn)
    {
        this.lastActivedOn = lastActivedOn;
    }

    public boolean checkActive()
    {
        Date now = new Date();
        boolean result = now.getTime() - lastActivedOn.getTime() < sessionTimeout;
        if(result)
            setLastActivedOn(now);
        return result;
    }

    private String loginId;
    private String id;
    private String name;
    private Date loginOn;
    private Date lastActivedOn;
    private long sessionTimeout;
    private static long DEFAULT_TIMEOUT = 0x1b7740L;

}
