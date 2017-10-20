package com.wecloud.erp.entity;

import java.util.List;

import com.wecloud.erp.model.Item;
import com.wecloud.erp.model.Product;
import com.wecloud.erp.model.ProductItem;

public class ProductDetail extends Product
{

    public ProductDetail()
    {
    }

    public Item getItem()
    {
        return item;
    }

    public void setItem(Item item)
    {
        this.item = item;
    }

    public List<ProductItem> getObjItems()
    {
        return objItems;
    }

    public void setObjItems(List<ProductItem> objItems)
    {
        this.objItems = objItems;
    }

    private List<ProductItem> objItems;
    private Item item;
}
