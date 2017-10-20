package com.wecloud.erp.service;

import java.util.List;

import com.wecloud.erp.model.Customer;
import com.wecloud.erp.model.CustomerViewExample;
import com.wecloud.erp.model.Faulty;
import com.wecloud.erp.model.FaultyExample;
import com.wecloud.erp.model.FaultyItem;
import com.wecloud.erp.model.FaultyItemExample;
import com.wecloud.erp.model.ItemRequestedView;
import com.wecloud.erp.model.ItemStorage;
import com.wecloud.erp.model.ItemStorageExample;
import com.wecloud.erp.model.ItemSummary;
import com.wecloud.erp.model.PoSummary;
import com.wecloud.erp.model.PoSummaryExample;
import com.wecloud.erp.model.SoSummary;
import com.wecloud.erp.model.SoSummaryExample;
import com.wecloud.erp.model.SplitItemView;
import com.wecloud.erp.model.SplitItemViewExample;

public interface ViewManager
{

    public abstract List<Customer> listCustomerViews(CustomerViewExample customerviewexample);

    public abstract List<ItemStorage> listItemStorages(ItemStorageExample itemstorageexample);

    public abstract int countItemStorage(ItemStorageExample itemstorageexample);

    public abstract void addFaulty(Faulty faulty);

    public abstract void addFaultyItem(FaultyItem faultyitem);

    public abstract void deleteFaulty(String s);

    public abstract void deleteFaultyItem(String s);

    public abstract Faulty getFaulty(String s);

    public abstract FaultyItem getFaultyItem(String s);

    public abstract void updateFaulty(Faulty faulty);

    public abstract void updateFaultyItem(FaultyItem faultyitem);

    public abstract List<Faulty> listFaultys(FaultyExample faultyexample);

    public abstract List<FaultyItem> listFaultyItems(FaultyItemExample faultyitemexample);

    public abstract int countFaulty(FaultyExample faultyexample);

    public abstract ItemSummary getItemSummary();

    public abstract List<PoSummary> listPoSummarys(PoSummaryExample posummaryexample);

    public abstract List<SoSummary> listSoSummarys(SoSummaryExample sosummaryexample);

    public abstract ItemRequestedView getItemRequestedView(String s);
    
    public List<SplitItemView> listSplitItems(SplitItemViewExample example);
    public int countSplitItems(SplitItemViewExample example);
}
