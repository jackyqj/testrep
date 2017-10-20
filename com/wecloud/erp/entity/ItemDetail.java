package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.Customer;
import com.wecloud.erp.model.Item;

public class ItemDetail extends Item
{

    public ItemDetail()
    {
    }

    public List<Customer> getSuppliers()
    {
        return suppliers;
    }

    public void setSuppliers(List<Customer> suppliers)
    {
        this.suppliers = suppliers;
    }

    private List<Customer> suppliers;
}
