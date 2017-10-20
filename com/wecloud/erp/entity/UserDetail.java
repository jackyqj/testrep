package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.ErpRole;
import com.wecloud.erp.model.ErpUser;

public class UserDetail
{

    public UserDetail()
    {
    }

    public List<ErpRole> getRoles()
    {
        return roles;
    }

    public void setRoles(List<ErpRole> roles)
    {
        this.roles = roles;
    }

    public ErpUser getUser()
    {
        return user;
    }

    public void setUser(ErpUser user)
    {
        this.user = user;
    }

    private List<ErpRole> roles;
    private ErpUser user;
}
