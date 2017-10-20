package com.wecloud.erp.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.wecloud.erp.business.BEConstants;
import com.wecloud.erp.entity.ItemDetail;
import com.wecloud.erp.entity.Search;
import com.wecloud.erp.model.Customer;
import com.wecloud.erp.model.Item;
import com.wecloud.erp.model.ItemExample;
import com.wecloud.erp.model.ItemSupplierMapping;
import com.wecloud.erp.model.ProduceRequestItemExample;
import com.wecloud.erp.model.ProductExample;
import com.wecloud.erp.model.ProductItemExample;
import com.wecloud.erp.model.ProductionItem;
import com.wecloud.erp.model.ProductionItemExample;
import com.wecloud.erp.model.Sequence;
import com.wecloud.erp.model.SoItemExample;
import com.wecloud.erp.model.Storage;
import com.wecloud.erp.model.StorageExample;
import com.wecloud.erp.model.Uom;
import com.wecloud.erp.report.impl.ItemExcelModel;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.OrderManager;
import com.wecloud.erp.utils.ErpUtils;
import com.wecloud.erp.utils.LOG;
import com.wecloud.erp.utils.UUID;
import com.wecloud.erp.web.exception.ActionException;

@Controller
public class BaseItemAction {
    @Resource
    private ItemManager itemManager;
    @Resource
    private OrderManager orderManager;
    private static Logger L = Logger.getLogger((Class)BaseItemAction.class);

    @RequestMapping(value={"/listItem.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listItem(@RequestBody Search search) {
        ItemExample query = new ItemExample();
        String keyword = search.getKeyword();
        String itemType = search.getItemType();
        if (ErpUtils.isNotEmpty((String)keyword)) {
            keyword = "%" + keyword + "%";
            if (ErpUtils.isNotEmpty((String)itemType)) {
                query.or().andCodeLike(keyword).andIsBaseitemEqualTo(Integer.valueOf(1)).andTypeEqualTo(itemType);
                query.or().andNameLike(keyword).andIsBaseitemEqualTo(Integer.valueOf(1)).andTypeEqualTo(itemType);
                query.or().andStyleLike(keyword).andIsBaseitemEqualTo(Integer.valueOf(1)).andTypeEqualTo(itemType);
            } else {
                query.or().andCodeLike(keyword).andIsBaseitemEqualTo(Integer.valueOf(1));
                query.or().andNameLike(keyword).andIsBaseitemEqualTo(Integer.valueOf(1));
                query.or().andStyleLike(keyword).andIsBaseitemEqualTo(Integer.valueOf(1));
            }
        } else if (ErpUtils.isNotEmpty((String)itemType)) {
            query.createCriteria().andIsBaseitemEqualTo(Integer.valueOf(1)).andTypeEqualTo(itemType);
        } else {
            query.createCriteria().andIsBaseitemEqualTo(Integer.valueOf(1));
        }
        LOG.debug((Logger)L, (Object)("Get search information: " + (Object)search));
        query.setOrderByClause("CODE desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List items = this.itemManager.listItems(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("items", items);
        int count = this.itemManager.countItem(query);
        result.put("totalCount", count);
        LOG.info((Logger)L, result);
        return result;
    }

    @RequestMapping(value={"/exportItemList.do"}, method={RequestMethod.GET})
    public ModelAndView exportItemList(@RequestParam String itemType, @RequestParam String keyword) {
        ItemExample query = new ItemExample();
        if (ErpUtils.isNotEmpty((String)keyword)) {
            keyword = "%" + keyword + "%";
            if (ErpUtils.isNotEmpty((String)itemType)) {
                query.or().andCodeLike(keyword).andIsBaseitemEqualTo(Integer.valueOf(1)).andTypeEqualTo(itemType);
                query.or().andNameLike(keyword).andIsBaseitemEqualTo(Integer.valueOf(1)).andTypeEqualTo(itemType);
                query.or().andStyleLike(keyword).andIsBaseitemEqualTo(Integer.valueOf(1)).andTypeEqualTo(itemType);
            } else {
                query.or().andCodeLike(keyword).andIsBaseitemEqualTo(Integer.valueOf(1));
                query.or().andNameLike(keyword).andIsBaseitemEqualTo(Integer.valueOf(1));
                query.or().andStyleLike(keyword).andIsBaseitemEqualTo(Integer.valueOf(1));
            }
        } else if (ErpUtils.isNotEmpty((String)itemType)) {
            query.createCriteria().andIsBaseitemEqualTo(Integer.valueOf(1)).andTypeEqualTo(itemType);
        } else {
            query.createCriteria().andIsBaseitemEqualTo(Integer.valueOf(1));
        }
        query.setOrderByClause("CODE desc");
        List<Item> items = this.itemManager.listItems(query);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap<String, String> uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        if (ErpUtils.hasElement((Collection)items)) {
            items.forEach(item -> {
                item.setUom((String)uomMap.get(item.getUom()));
            }
            );
        }
        ItemExcelModel viewModel = new ItemExcelModel(items);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }

    @RequestMapping(value={"/listProductionItem.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listProductionItem(@RequestBody Search search) {
        ProductionItemExample query = new ProductionItemExample();
        if (search != null && (ErpUtils.isNotEmpty((String)search.getItemType()) || ErpUtils.isNotEmpty((String)search.getKeyword()))) {
            ProductionItemExample.Criteria criteria = query.createCriteria();
            if (ErpUtils.isNotEmpty((String)search.getItemType())) {
                criteria.andTypeEqualTo(search.getItemType());
                if (ErpUtils.isNotEmpty((String)search.getKeyword())) {
                    criteria.andNameLike("%" + search.getKeyword() + "%");
                    query.or(query.createCriteria().andTypeEqualTo(search.getItemType()).andStyleLike("%" + search.getKeyword() + "%"));
                }
            } else if (ErpUtils.isNotEmpty((String)search.getKeyword())) {
                criteria.andNameLike("%" + search.getKeyword() + "%");
                query.or(query.createCriteria().andStyleLike("%" + search.getKeyword() + "%"));
            }
        }
        LOG.debug((Logger)L, (Object)("Get search information: " + (Object)search));
        query.setOrderByClause("CODE desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List<ProductionItem> items = this.itemManager.listProductionItems(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("items", items);
        int count = this.itemManager.countProductionItem(query);
        result.put("totalCount", count);
        LOG.info((Logger)L, result);
        return result;
    }

    @RequestMapping(value={"/loadItem.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadItem(@RequestParam String itemId) {
        Item item = this.itemManager.getItem(itemId);
        List<ItemSupplierMapping> maps = this.itemManager.listItemSupplierMappingsByItemId(itemId);
        ArrayList<Customer> suppliers = new ArrayList();
        if (ErpUtils.hasElement((Collection)maps)) {
            maps.forEach(map -> {
                Customer sup = new Customer();
                sup.setId(map.getSupplierId());
                sup.setName(map.getSupplierName());
                sup.setCategory(map.getClassCode());
                suppliers.add(sup);
            }
            );
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("item", (Object)item);
        result.put("suppliers", suppliers);
        LOG.info((Logger)L, result);
        return result;
    }

    @RequestMapping(value={"/saveItem.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Map<String, Object> saveItem(@RequestBody ItemDetail item) {
        if (item.getId() == null) {
            item.setId(UUID.get());
            Sequence seq = this.orderManager.getSequence("ITM");
            item.setCode(ErpUtils.genItemCode((Item)item, (Sequence)seq));
            item.setIsBaseitem(Integer.valueOf(1));
            item.setStatus(BEConstants.STATUS_FINAL);
            this.itemManager.addItem((Item)item);
        } else {
            this.itemManager.updateItem((Item)item);
            this.itemManager.deleteItemSupplierMappingByItemId(item.getId());
        }
        for (Customer supplier : item.getSuppliers()) {
            ItemSupplierMapping map = new ItemSupplierMapping();
            map.setId(UUID.get());
            map.setItemId(item.getId());
            map.setSupplierId(supplier.getId());
            map.setSupplierName(supplier.getName());
            map.setClassCode(supplier.getCategory());
            this.itemManager.addItemSupplierMapping(map);
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("item", (Object)item);
        LOG.info((Logger)L, result);
        return result;
    }

    @RequestMapping(value={"/deleteItem.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> deleteItem(@RequestParam String itemId) {
        StorageExample sQuery = new StorageExample();
        sQuery.createCriteria().andItemIdEqualTo(itemId);
        List<Storage> storages = this.itemManager.listStorages(sQuery);
        boolean hasZeroStorage = false;
        if (ErpUtils.hasElement((Collection)storages)) {
            hasZeroStorage = true;
            for (Storage storage : storages) {
                if (storage.getQty() <= 0.0) continue;
                throw new ActionException();
            }
        }
        ProductItemExample piQuery = new ProductItemExample();
        piQuery.createCriteria().andItemIdEqualTo(itemId);
        if (this.itemManager.countProductItem(piQuery) > 0) {
            throw new ActionException();
        }
        ProductExample pQuery = new ProductExample();
        pQuery.createCriteria().andItemIdEqualTo(itemId);
        if (this.itemManager.countProduct(pQuery) > 0) {
            throw new ActionException();
        }
        SoItemExample siQuery = new SoItemExample();
        siQuery.createCriteria().andItemIdEqualTo(itemId);
        if (this.orderManager.countSoItem(siQuery) > 0) {
            throw new ActionException();
        }
        ProduceRequestItemExample prQuery = new ProduceRequestItemExample();
        prQuery.createCriteria().andItemIdEqualTo(itemId);
        if (this.orderManager.countProduceRequestItem(prQuery) > 0) {
            throw new ActionException();
        }
        if (hasZeroStorage) {
            LOG.debug((Logger)L, (Object)"Delete the storage with 0 qty..");
            this.itemManager.deleteStorage(sQuery);
        }
        LOG.debug((Logger)L, (Object)("Delete item.." + itemId));
        this.itemManager.deleteItemSupplierMappingByItemId(itemId);
        this.itemManager.deleteItem(itemId);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemId", itemId);
        LOG.info((Logger)L, result);
        return result;
    }
}
