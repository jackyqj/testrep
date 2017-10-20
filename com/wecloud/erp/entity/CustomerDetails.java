package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.Contact;
import com.wecloud.erp.model.Customer;

public class CustomerDetails
{

    public CustomerDetails()
    {
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public List<Contact> getContacts()
    {
        return contacts;
    }

    public void setContacts(List<Contact> contacts)
    {
        this.contacts = contacts;
    }

    private Customer customer;
    private List<Contact> contacts;
}
