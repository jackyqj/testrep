
package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.QcRecord;
import com.wecloud.erp.model.QcRecordItem;

public class QcRecDetail
{

    public QcRecDetail()
    {
    }

    public List<QcRecordItem> getItems()
    {
        return items;
    }

    public void setItems(List<QcRecordItem> itmes)
    {
        items = itmes;
    }

    public QcRecord getObj()
    {
        return obj;
    }

    public void setObj(QcRecord obj)
    {
        this.obj = obj;
    }

    private List<QcRecordItem> items;
    private QcRecord obj;
}
