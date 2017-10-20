package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.CheckoutRecord;
import com.wecloud.erp.model.CheckoutRecordItem;
import com.wecloud.erp.model.Item;

public class CheckoutRecordDetail extends CheckoutRecord
{

    public CheckoutRecordDetail()
    {
    }

    public Item getItem()
    {
        return item;
    }

    public void setItem(Item item)
    {
        this.item = item;
    }

    public List<CheckoutRecordItem> getObjItems()
    {
        return objItems;
    }

    public void setObjItems(List<CheckoutRecordItem> objItems)
    {
        this.objItems = objItems;
    }

    private List<CheckoutRecordItem> objItems;
    private Item item;
}
