package com.wecloud.erp.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.wecloud.erp.model.ErpRole;
import com.wecloud.erp.model.ErpRoleExample;
import com.wecloud.erp.model.ErpUser;
import com.wecloud.erp.model.ErpUserExample;
import com.wecloud.erp.persistence.dao.ErpRoleMapper;
import com.wecloud.erp.persistence.dao.ErpUserMapper;

// Referenced classes of package com.wecloud.erp.service:
//            UserManager

@Service(value="userManager")
public class UserManagerImpl
    implements UserManager
{

    public UserManagerImpl()
    {
    }

    public List listErpUserRoles(ErpUser user)
    {
        ErpRoleExample query = new ErpRoleExample();
        query.createCriteria().andUserIdEqualTo(user.getId());
        return erpRoleDao.selectByExample(query);
    }

    public void addErpUser(ErpUser erpUser)
    {
        erpUserDao.insert(erpUser);
    }

    public void addErpRole(ErpRole erpRole)
    {
        erpRoleDao.insert(erpRole);
    }

    public void deleteErpUser(String id)
    {
        erpUserDao.deleteByPrimaryKey(id);
    }

    public void deleteErpRoleByUserId(String id)
    {
        ErpRoleExample query = new ErpRoleExample();
        query.createCriteria().andUserIdEqualTo(id);
        erpRoleDao.deleteByExample(query);
    }

    public ErpUser getErpUser(String name)
    {
        ErpUserExample query = new ErpUserExample();
        query.createCriteria().andLoginIdEqualTo(name);
        List users = erpUserDao.selectByExample(query);
        if(users.isEmpty() || users.size() == 0)
            return null;
        else
            return (ErpUser)users.get(0);
    }

    public ErpUser getErpUserById(String id)
    {
        return erpUserDao.selectByPrimaryKey(id);
    }

    public ErpRole getErpRole(String id)
    {
        return erpRoleDao.selectByPrimaryKey(id);
    }

    public void updateErpUser(ErpUser erpUser)
    {
        erpUserDao.updateByPrimaryKey(erpUser);
    }

    public void updateErpRole(ErpRole erpRole)
    {
        erpRoleDao.updateByPrimaryKey(erpRole);
    }

    public List listErpUsersByRole(List roleNames)
    {
        ErpRoleExample query = new ErpRoleExample();
        query.createCriteria().andRoleIn(roleNames);
        List roles = erpRoleDao.selectByExample(query);
        if(CollectionUtils.isEmpty(roles))
            return null;
        List userIds = new ArrayList();
        for(Iterator iterator = roles.iterator(); iterator.hasNext();)
        {
            ErpRole role = (ErpRole)iterator.next();
            if(!userIds.contains(role.getUserId()))
                userIds.add(role.getUserId());
        }

        ErpUserExample uQuery = new ErpUserExample();
        uQuery.createCriteria().andIdIn(userIds);
        return erpUserDao.selectByExample(uQuery);
    }

    public List listErpUsers(ErpUserExample example)
    {
        return erpUserDao.selectByExample(example);
    }

    public int countUsers(ErpUserExample example)
    {
        return erpUserDao.countByExample(example);
    }

    @Resource
    private ErpUserMapper erpUserDao;
    @Resource
    private ErpRoleMapper erpRoleDao;
}
