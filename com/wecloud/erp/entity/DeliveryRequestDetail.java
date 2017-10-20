package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.DeliveryRequest;
import com.wecloud.erp.model.DeliveryRequestItem;

public class DeliveryRequestDetail extends DeliveryRequest
{

    public DeliveryRequestDetail()
    {
    }

    public List<DeliveryRequestItem> getObjItems()
    {
        return objItems;
    }

    public void setObjItems(List<DeliveryRequestItem> objItems)
    {
        this.objItems = objItems;
    }

    private List<DeliveryRequestItem> objItems;
}
