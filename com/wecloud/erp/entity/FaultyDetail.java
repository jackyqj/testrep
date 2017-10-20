package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.Faulty;
import com.wecloud.erp.model.FaultyItem;

public class FaultyDetail
{

    public FaultyDetail()
    {
    }

    public List<FaultyItem> getItems()
    {
        return items;
    }

    public void setItems(List<FaultyItem> itmes)
    {
        items = itmes;
    }

    public Faulty getObj()
    {
        return obj;
    }

    public void setObj(Faulty obj)
    {
        this.obj = obj;
    }

    private List<FaultyItem> items;
    private Faulty obj;
}
