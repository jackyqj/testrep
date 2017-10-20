package com.wecloud.erp.entity;

import java.util.Date;

public class Search
{

    public Search()
    {
    }

    public String getCustomerCategory()
    {
        return customerCategory;
    }

    public void setCustomerCategory(String customerCategory)
    {
        this.customerCategory = customerCategory;
    }

    public String getLimitStart()
    {
        return limitStart;
    }

    public void setLimitStart(String limitStart)
    {
        this.limitStart = limitStart;
    }

    public String getLimitEnd()
    {
        return limitEnd;
    }

    public void setLimitEnd(String limitEnd)
    {
        this.limitEnd = limitEnd;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public Date getDateFrom()
    {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom)
    {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo()
    {
        return dateTo;
    }

    public void setDateTo(Date dateTo)
    {
        this.dateTo = dateTo;
    }

    public String getCustomerType()
    {
        return customerType;
    }

    public void setCustomerType(String customerType)
    {
        this.customerType = customerType;
    }

    public String getItemType()
    {
        return itemType;
    }

    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }

    public String getStorageType()
    {
        return storageType;
    }

    public void setStorageType(String storageType)
    {
        this.storageType = storageType;
    }

    public String getSupplierId()
    {
        return supplierId;
    }

    public void setSupplierId(String supplierId)
    {
        this.supplierId = supplierId;
    }

    public String getBuyerId()
    {
        return buyerId;
    }

    public void setBuyerId(String buyerId)
    {
        this.buyerId = buyerId;
    }

    public String getItemId()
    {
        return itemId;
    }

    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    private String keyword;
    private Date dateFrom;
    private Date dateTo;
    private String customerType;
    private String customerCategory;
    private String itemType;
    private String storageType;
    private String supplierId;
    private String buyerId;
    private String itemId;
    private String status;
    private String orderNo;
    private String limitStart;
    private String limitEnd;
}
