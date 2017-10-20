package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.QcRequest;
import com.wecloud.erp.model.QcRequestItem;

public class QcReqDetail
{

    public QcReqDetail()
    {
    }

    public List<QcRequestItem> getItems()
    {
        return items;
    }

    public void setItems(List<QcRequestItem> itmes)
    {
        items = itmes;
    }

    public QcRequest getObj()
    {
        return obj;
    }

    public void setObj(QcRequest obj)
    {
        this.obj = obj;
    }

    private List<QcRequestItem> items;
    private QcRequest obj;
}
