package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.PO;
import com.wecloud.erp.model.PoItem;
import com.wecloud.erp.model.ProduceNotice;

public class ProduceNoticeDetail
{

    public ProduceNoticeDetail()
    {
    }

    public ProduceNotice getNotice()
    {
        return notice;
    }

    public void setNotice(ProduceNotice notice)
    {
        this.notice = notice;
    }

    public PO getOrder()
    {
        return order;
    }

    public void setOrder(PO order)
    {
        this.order = order;
    }

    public List<PoItem> getOrderItems()
    {
        return orderItems;
    }

    public void setOrderItems(List<PoItem> orderItems)
    {
        this.orderItems = orderItems;
    }

    private ProduceNotice notice;
    private PO order;
    private List<PoItem> orderItems;
}
