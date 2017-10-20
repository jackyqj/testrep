
package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.CheckoutReqItem;
import com.wecloud.erp.model.CheckoutRequest;

public class CheckoutRequestDetail extends CheckoutRequest
{

    public CheckoutRequestDetail()
    {
    }

    public List<CheckoutReqItem> getObjItems()
    {
        return objItems;
    }

    public void setObjItems(List<CheckoutReqItem> objItems)
    {
        this.objItems = objItems;
    }

    private List<CheckoutReqItem> objItems;
}
