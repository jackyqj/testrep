package com.wecloud.erp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

import com.wecloud.erp.entity.FaultyDetail;
import com.wecloud.erp.entity.Search;
import com.wecloud.erp.entity.SessionUser;
import com.wecloud.erp.model.Faulty;
import com.wecloud.erp.model.FaultyExample;
import com.wecloud.erp.model.FaultyItem;
import com.wecloud.erp.model.FaultyItemExample;
import com.wecloud.erp.model.Inventory;
import com.wecloud.erp.model.Item;
import com.wecloud.erp.model.ItemExample;
import com.wecloud.erp.model.ItemStorage;
import com.wecloud.erp.model.ItemStorageExample;
import com.wecloud.erp.model.Sequence;
import com.wecloud.erp.model.SplitItemView;
import com.wecloud.erp.model.SplitItemViewExample;
import com.wecloud.erp.model.Storage;
import com.wecloud.erp.model.Uom;
import com.wecloud.erp.report.impl.FaultyExcelModel;
import com.wecloud.erp.report.impl.ItemStorageExcelModel;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.OrderManager;
import com.wecloud.erp.service.ViewManager;
import com.wecloud.erp.utils.ErpUtils;
import com.wecloud.erp.utils.LOG;
import com.wecloud.erp.utils.UUID;
import com.wecloud.erp.web.exception.ActionException;

@Controller
public class ViewController {
    @Resource
    private ViewManager viewManager;
    @Resource
    private ItemManager itemManager;
    @Resource
    private OrderManager orderManager;
    private Logger LOGGER = Logger.getLogger((Class)ViewController.class);

    @RequestMapping(value={"/listItemStorage.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listItemStorage(@RequestBody Search search) {
        String itemType = search.getItemType();
        String storageType = search.getStorageType();
        String keyword = search.getKeyword();
        ItemStorageExample query = new ItemStorageExample();
        ItemStorageExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)itemType)) {
            criteria.andTypeEqualTo(itemType);
        }
        if (ErpUtils.isNotEmpty((String)storageType)) {
            criteria.andStorageTypeEqualTo(storageType);
        }
        if (ErpUtils.isNotEmpty((String)keyword)) {
            criteria.andCodeLike("%" + keyword + "%");
        }
        query.setOrderByClause("code desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List requests = this.viewManager.listItemStorages(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.viewManager.countItemStorage(query);
        result.put("totalCount", count);
        result.put("objList", requests);
        return result;
    }

    @RequestMapping(value={"/listItemForSelection.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listItemForSelection(@RequestBody Search search) {
        SplitItemViewExample query = new SplitItemViewExample();
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
        LOG.debug((Logger)LOGGER, (Object)("Get search information: " + (Object)search));
        query.setOrderByClause("CODE desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List<SplitItemView> items = viewManager.listSplitItems(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("items", items);
        int count = viewManager.countSplitItems(query);
        result.put("totalCount", count);
        LOG.info((Logger)LOGGER, result);
        return result;
    }

    @RequestMapping(value={"/saveItemInventory.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Map<String, Object> saveItemInventory(@RequestBody ItemStorage item, HttpServletRequest request) {
        if (item.getInventoryId() != null) {
            this.itemManager.deleteInventory(item.getInventoryId());
        }
        SessionUser user = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        Inventory inv = new Inventory();
        inv.setId(UUID.get());
        inv.setStorageId(item.getId());
        inv.setBalance(Double.valueOf(item.getInventoryQty() - item.getQty()));
        inv.setCreatedBy(user.getId());
        inv.setCreatedOn(new Date());
        inv.setSystemQty(item.getQty());
        inv.setQty(item.getInventoryQty());
        inv.setStatus(Integer.valueOf(1));
        this.itemManager.addInventory(inv);
        item.setBalance(inv.getBalance());
        item.setInventoryId(inv.getId());
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("obj", (Object)item);
        return result;
    }

    @RequestMapping(value={"/confirmItemInventory.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Map<String, Object> confirmItemInventory(@RequestBody ItemStorage item, HttpServletRequest request) {
        if (item.getInventoryId() == null) {
            throw new ActionException();
        }
        Storage storage = this.itemManager.getStorage(item.getId());
        storage.setQty(item.getInventoryQty());
        this.itemManager.updateStorage(storage);
        Inventory oInv = this.itemManager.getInventory(item.getInventoryId());
        oInv.setStatus(Integer.valueOf(2));
        oInv.setUpdatedOn(new Date());
        oInv.setBalance(item.getBalance());
        oInv.setSystemQty(item.getQty());
        oInv.setQty(item.getInventoryQty());
        this.itemManager.updateInventory(oInv);
        item.setBalance(null);
        item.setQty(item.getInventoryQty());
        item.setInventoryQty(null);
        item.setInventoryId(null);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("obj", (Object)item);
        return result;
    }

    @RequestMapping(value={"/saveFaulty.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object saveFaulty(@RequestBody FaultyDetail object, HttpServletRequest request) {
        Faulty obj = object.getObj();
        SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        if (obj.getId() == null) {
            obj.setId(UUID.get());
            Sequence seq = this.orderManager.getSequence("FT");
            obj.setCode(ErpUtils.generateRefNo((Sequence)seq));
            obj.setCreatedBy(sUser.getId());
            obj.setCreator(sUser.getName());
            obj.setCreatedOn(new Date());
            this.viewManager.addFaulty(obj);
            for (FaultyItem item : object.getItems()) {
                item.setId(UUID.get());
                item.setFaultyId(obj.getId());
                this.viewManager.addFaultyItem(item);
            }
        } else {
            throw new ActionException();
        }
        return object;
    }

    @RequestMapping(value={"/listFaulty.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listFaulty(@RequestBody Search search) {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        FaultyExample query = new FaultyExample();
        FaultyExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (deliveryDateFrom != null) {
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        }
        query.setOrderByClause("created_on desc, code desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List records = this.viewManager.listFaultys(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.viewManager.countFaulty(query);
        result.put("totalCount", count);
        result.put("objList", records);
        return result;
    }

    @RequestMapping(value={"/loadFaulty.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadFaulty(@RequestParam String objectId) {
        Faulty faulty = this.viewManager.getFaulty(objectId);
        FaultyItemExample example = new FaultyItemExample();
        example.createCriteria().andFaultyIdEqualTo(objectId);
        List<FaultyItem> reqItems = this.viewManager.listFaultyItems(example);
        HashMap<String, HashMap<String, FaultyItem>> itemMap = new HashMap();
        ArrayList itemIds = new ArrayList();
        reqItems.forEach(faultyItem -> {
            HashMap<String, FaultyItem> subMap = new HashMap<String, FaultyItem>();
            subMap.put("faultyItem", faultyItem);
            itemMap.put(faultyItem.getItemId(), subMap);
            itemIds.add(faultyItem.getItemId());
        }
        );
        ItemExample iQuery = new ItemExample();
        iQuery.createCriteria().andIdIn(itemIds);
        List<Item> itemList = this.itemManager.listItems(iQuery);
        itemList.forEach(item -> {
            ((Map)itemMap.get(item.getId())).put("item", item);
        }
        );
        LOG.info((Logger)this.LOGGER, itemMap);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemMap", itemMap);
        result.put("obj", (Object)faulty);
        return result;
    }

    @RequestMapping(value={"/exportItemStorage.do"}, method={RequestMethod.GET})
    public ModelAndView exportItemStorage(@RequestParam String itemType, @RequestParam String storageType, @RequestParam String keyword) {
        ItemStorageExample query = new ItemStorageExample();
        ItemStorageExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)itemType)) {
            criteria.andTypeEqualTo(itemType);
        }
        if (ErpUtils.isNotEmpty((String)storageType)) {
            criteria.andStorageTypeEqualTo(storageType);
        }
        if (ErpUtils.isNotEmpty((String)keyword)) {
            criteria.andCodeLike("%" + keyword + "%");
        }
        query.setOrderByClause("code desc");
        List<ItemStorage> requests = this.viewManager.listItemStorages(query);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        if (ErpUtils.hasElement(requests)) {
            requests.forEach(item -> {
                item.setUom((String)uomMap.get(item.getUom()));
            }
            );
        }
        ItemStorageExcelModel viewModel = new ItemStorageExcelModel(requests);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }

    @RequestMapping(value={"/exportFaulty.do"}, method={RequestMethod.GET})
    public ModelAndView exportFaulty(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo) {
        Date deliveryDateFrom = ErpUtils.isNotEmpty((String)dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty((String)dateTo) ? new Date(dateTo) : null;
        FaultyExample query = new FaultyExample();
        FaultyExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (deliveryDateFrom != null) {
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        }
        List<Faulty> records = this.viewManager.listFaultys(query);
        HashMap<String, String> nameMap = new HashMap<String, String>();
        for (Faulty rec : records) {
            String id = rec.getSupplierId();
            if (!nameMap.containsKey(id)) {
                nameMap.put(id, this.itemManager.getCustomer(id).getName());
            }
            rec.setSupplierId((String)nameMap.get(rec.getSupplierId()));
        }
        FaultyExcelModel viewModel = new FaultyExcelModel(records);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }
}
