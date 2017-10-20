package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.CheckinRecord;
import com.wecloud.erp.model.CheckinRecordItem;

public class CheckinRecDetail
{

    public CheckinRecDetail()
    {
    }

    public List<CheckinRecordItem> getItems()
    {
        return items;
    }

    public void setItems(List<CheckinRecordItem> itmes)
    {
        items = itmes;
    }

    public CheckinRecord getObj()
    {
        return obj;
    }

    public void setObj(CheckinRecord obj)
    {
        this.obj = obj;
    }

    private List<CheckinRecordItem> items;
    private CheckinRecord obj;
}
