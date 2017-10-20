package com.wecloud.erp.entity;

import java.util.Date;
import java.util.List;

import com.wecloud.erp.model.ProduceRequestItem;

public class ProduceRequestDetail
{

    public ProduceRequestDetail()
    {
    }

    public String getNoticeId()
    {
        return noticeId;
    }

    public void setNoticeId(String noticeId)
    {
        this.noticeId = noticeId;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getReferenceNo()
    {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo)
    {
        this.referenceNo = referenceNo;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public double getQty()
    {
        return qty;
    }

    public void setQty(double qty)
    {
        this.qty = qty;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getDeliveryDate()
    {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate)
    {
        this.deliveryDate = deliveryDate;
    }

    public List<ProduceRequestItem> getReqItems()
    {
        return reqItems;
    }

    public void setReqItems(List<ProduceRequestItem> reqItems)
    {
        this.reqItems = reqItems;
    }

    private String id;
    private String orderId;
    private String noticeId;
    private String status;
    private double qty;
    private String description;
    private String referenceNo;
    private Date deliveryDate;
    private List<ProduceRequestItem> reqItems;
}
