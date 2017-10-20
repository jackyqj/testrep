package com.wecloud.erp.service;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// Referenced classes of package com.wecloud.erp.service:
//            UserManager

public class UserDetailServiceImpl
    implements UserDetailsService
{

    public UserDetailServiceImpl()
    {
    }

    public UserDetails loadUserByUsername(String userName)
        throws UsernameNotFoundException, DataAccessException
    {
        return null;
    }

    public void setUserManager(UserManager userManager)
    {
        this.userManager = userManager;
    }

    private UserManager userManager;
}
