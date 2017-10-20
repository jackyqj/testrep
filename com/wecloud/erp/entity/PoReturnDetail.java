package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.PoReturn;
import com.wecloud.erp.model.PoReturnItem;

public class PoReturnDetail extends PoReturn
{

    public PoReturnDetail()
    {
    }

    public List<PoReturnItem> getObjItems()
    {
        return objItems;
    }

    public void setObjItems(List<PoReturnItem> objItems)
    {
        this.objItems = objItems;
    }

    private List<PoReturnItem> objItems;
}
