package com.wecloud.erp.service;

import java.util.List;

import com.wecloud.erp.model.ErpRole;
import com.wecloud.erp.model.ErpUser;
import com.wecloud.erp.model.ErpUserExample;

public interface UserManager
{

    public abstract void addErpUser(ErpUser erpuser);

    public abstract void addErpRole(ErpRole erprole);

    public abstract void deleteErpUser(String s);

    public abstract void deleteErpRoleByUserId(String s);

    public abstract ErpUser getErpUser(String s);

    public abstract ErpUser getErpUserById(String s);

    public abstract ErpRole getErpRole(String s);

    public abstract void updateErpUser(ErpUser erpuser);

    public abstract void updateErpRole(ErpRole erprole);

    public abstract List<ErpUser> listErpUsers(ErpUserExample erpuserexample);

    public abstract List<ErpRole> listErpUserRoles(ErpUser erpuser);

    public abstract List<ErpUser> listErpUsersByRole(List<String> list);

    public abstract int countUsers(ErpUserExample erpuserexample);
}
