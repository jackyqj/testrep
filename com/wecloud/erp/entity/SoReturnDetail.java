package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.SoReturn;
import com.wecloud.erp.model.SoReturnItem;

public class SoReturnDetail extends SoReturn
{

    public SoReturnDetail()
    {
    }

    public List<SoReturnItem> getObjItems()
    {
        return objItems;
    }

    public void setObjItems(List<SoReturnItem> objItems)
    {
        this.objItems = objItems;
    }

    private List<SoReturnItem> objItems;
}
