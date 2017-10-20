package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.PO;
import com.wecloud.erp.model.PoItem;

public class PODetail
{

    public PODetail()
    {
    }

    public PO getOrderReq()
    {
        return orderReq;
    }

    public void setOrderReq(PO orderReq)
    {
        this.orderReq = orderReq;
    }

    public List<PoItem> getItems()
    {
        return items;
    }

    public void setItems(List<PoItem> items)
    {
        this.items = items;
    }

    private PO orderReq;
    private List<PoItem> items;
}
