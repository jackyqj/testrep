package com.wecloud.erp.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wecloud.erp.model.CustomerViewExample;
import com.wecloud.erp.model.Faulty;
import com.wecloud.erp.model.FaultyExample;
import com.wecloud.erp.model.FaultyItem;
import com.wecloud.erp.model.FaultyItemExample;
import com.wecloud.erp.model.ItemRequestedView;
import com.wecloud.erp.model.ItemRequestedViewExample;
import com.wecloud.erp.model.ItemStorageExample;
import com.wecloud.erp.model.ItemSummary;
import com.wecloud.erp.model.PoSummaryExample;
import com.wecloud.erp.model.SoSummaryExample;
import com.wecloud.erp.model.SplitItemView;
import com.wecloud.erp.model.SplitItemViewExample;
import com.wecloud.erp.persistence.dao.CustomerViewMapper;
import com.wecloud.erp.persistence.dao.FaultyItemMapper;
import com.wecloud.erp.persistence.dao.FaultyMapper;
import com.wecloud.erp.persistence.dao.ItemRequestedViewMapper;
import com.wecloud.erp.persistence.dao.ItemStorageMapper;
import com.wecloud.erp.persistence.dao.ItemSummaryMapper;
import com.wecloud.erp.persistence.dao.PoSummaryMapper;
import com.wecloud.erp.persistence.dao.SoSummaryMapper;
import com.wecloud.erp.persistence.dao.SplitItemViewMapper;
import com.wecloud.erp.utils.ErpUtils;

// Referenced classes of package com.wecloud.erp.service:
//            ViewManager

@Service(value="viewManager")
public class ViewManagerImpl
    implements ViewManager
{

    public ViewManagerImpl()
    {
    }

    public List listCustomerViews(CustomerViewExample example)
    {
        return customerViewDao.selectByExample(example);
    }

    public List listItemStorages(ItemStorageExample example)
    {
        return itemStorageDao.selectByExample(example);
    }

    public int countItemStorage(ItemStorageExample example)
    {
        return itemStorageDao.countByExample(example);
    }

    public void addFaulty(Faulty faulty)
    {
        faultyDao.insert(faulty);
    }

    public void addFaultyItem(FaultyItem faultyItem)
    {
        faultyItemDao.insert(faultyItem);
    }

    public void deleteFaulty(String id)
    {
        faultyDao.deleteByPrimaryKey(id);
    }

    public void deleteFaultyItem(String id)
    {
        faultyItemDao.deleteByPrimaryKey(id);
    }

    public Faulty getFaulty(String id)
    {
        return faultyDao.selectByPrimaryKey(id);
    }

    public FaultyItem getFaultyItem(String id)
    {
        return faultyItemDao.selectByPrimaryKey(id);
    }

    public void updateFaulty(Faulty faulty)
    {
        faultyDao.updateByPrimaryKey(faulty);
    }

    public void updateFaultyItem(FaultyItem faultyItem)
    {
        faultyItemDao.updateByPrimaryKey(faultyItem);
    }

    public List listFaultys(FaultyExample example)
    {
        return faultyDao.selectByExampleWithBLOBs(example);
    }

    public List listFaultyItems(FaultyItemExample example)
    {
        return faultyItemDao.selectByExample(example);
    }

    public int countFaulty(FaultyExample example)
    {
        return faultyDao.countByExample(example);
    }

    public ItemSummary getItemSummary()
    {
        return (ItemSummary)itemSummaryDao.selectByExample(null).get(0);
    }

    public List listPoSummarys(PoSummaryExample example)
    {
        return poSummaryDao.selectByExample(example);
    }

    public List listSoSummarys(SoSummaryExample example)
    {
        return soSummaryDao.selectByExample(example);
    }

    public ItemRequestedView getItemRequestedView(String id)
    {
        ItemRequestedViewExample example = new ItemRequestedViewExample();
        example.createCriteria().andItemIdEqualTo(id);
        List items = itemRequestedViewDao.selectByExample(example);
        return ErpUtils.hasElement(items) ? (ItemRequestedView)items.get(0) : null;
    }

    public List<SplitItemView> listSplitItems(SplitItemViewExample example) {
    	return splitItemViewDao.selectByExampleWithBLOBs(example);
    }
    public int countSplitItems(SplitItemViewExample example) {
    	return splitItemViewDao.countByExample(example);
    }
    @Resource
    private CustomerViewMapper customerViewDao;
    @Resource
    private ItemStorageMapper itemStorageDao;
    @Resource
    private FaultyMapper faultyDao;
    @Resource
    private FaultyItemMapper faultyItemDao;
    @Resource
    private ItemSummaryMapper itemSummaryDao;
    @Resource
    private PoSummaryMapper poSummaryDao;
    @Resource
    private SoSummaryMapper soSummaryDao;
    @Resource
    private ItemRequestedViewMapper itemRequestedViewDao;
    @Resource
    private SplitItemViewMapper splitItemViewDao;
}
