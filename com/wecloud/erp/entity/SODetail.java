package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.SalesOrder;
import com.wecloud.erp.model.SoItem;

public class SODetail
{

    public SODetail()
    {
    }

    public SalesOrder getObj()
    {
        return obj;
    }

    public void setObj(SalesOrder obj)
    {
        this.obj = obj;
    }

    public List<SoItem> getItems()
    {
        return items;
    }

    public void setItems(List<SoItem> items)
    {
        this.items = items;
    }

    private SalesOrder obj;
    private List<SoItem> items;
}
