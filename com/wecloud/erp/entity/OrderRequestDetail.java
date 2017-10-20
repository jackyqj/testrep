package com.wecloud.erp.entity;

import java.util.Iterator;
import java.util.List;

import com.wecloud.erp.model.OrderRequest;
import com.wecloud.erp.model.OrderRequestItem;

public class OrderRequestDetail
{

    public OrderRequestDetail()
    {
    }

    public OrderRequest getOrderReq()
    {
        return orderReq;
    }

    public void setOrderReq(OrderRequest orderReq)
    {
        this.orderReq = orderReq;
    }

    public List<OrderRequestItem> getItems()
    {
        return items;
    }

    public void setItems(List<OrderRequestItem> items)
    {
        this.items = items;
    }

    public double getTotalQty()
    {
        double result = 0.0D;
        if(items != null)
        {
            for(Iterator iterator = items.iterator(); iterator.hasNext();)
            {
                OrderRequestItem item = (OrderRequestItem)iterator.next();
                result += item.getQty().doubleValue();
            }

        }
        return result;
    }

    private OrderRequest orderReq;
    private List<OrderRequestItem> items;
}
