package com.wecloud.erp.controller;

import java.util.ArrayList;
import java.util.Collection;
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

import com.wecloud.erp.business.BEConstants;
import com.wecloud.erp.entity.CheckoutRecordDetail;
import com.wecloud.erp.entity.CheckoutRequestDetail;
import com.wecloud.erp.entity.ProduceRequestDetail;
import com.wecloud.erp.entity.Search;
import com.wecloud.erp.entity.SessionUser;
import com.wecloud.erp.model.CheckinRecordItem;
import com.wecloud.erp.model.CheckoutRecord;
import com.wecloud.erp.model.CheckoutRecordExample;
import com.wecloud.erp.model.CheckoutRecordItem;
import com.wecloud.erp.model.CheckoutRecordItemExample;
import com.wecloud.erp.model.CheckoutReqItem;
import com.wecloud.erp.model.CheckoutReqItemExample;
import com.wecloud.erp.model.CheckoutRequest;
import com.wecloud.erp.model.CheckoutRequestExample;
import com.wecloud.erp.model.DeliveryRequest;
import com.wecloud.erp.model.Item;
import com.wecloud.erp.model.ItemExample;
import com.wecloud.erp.model.ItemRequestedView;
import com.wecloud.erp.model.ItemSupplierMapping;
import com.wecloud.erp.model.OrderRequestItem;
import com.wecloud.erp.model.ProduceRequest;
import com.wecloud.erp.model.ProduceRequestItem;
import com.wecloud.erp.model.ProduceRequestItemExample;
import com.wecloud.erp.model.ProduceRequestViewExample;
import com.wecloud.erp.model.Product;
import com.wecloud.erp.model.ProductExample;
import com.wecloud.erp.model.ProductItem;
import com.wecloud.erp.model.ProductItemExample;
import com.wecloud.erp.model.SalesOrder;
import com.wecloud.erp.model.Sequence;
import com.wecloud.erp.model.SoItem;
import com.wecloud.erp.model.SoItemExample;
import com.wecloud.erp.model.Storage;
import com.wecloud.erp.model.StorageExample;
import com.wecloud.erp.report.impl.CheckoutRecordExcelModel;
import com.wecloud.erp.report.impl.CheckoutRequestExcelModel;
import com.wecloud.erp.report.impl.PrdReqExcelModel;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.OrderManager;
import com.wecloud.erp.service.PoManager;
import com.wecloud.erp.service.ViewManager;
import com.wecloud.erp.utils.ErpUtils;
import com.wecloud.erp.utils.LOG;
import com.wecloud.erp.utils.UUID;
import com.wecloud.erp.web.exception.ActionException;

@Controller
public class PmcController {
    @Resource
    private OrderManager orderManager;
    @Resource
    private ItemManager itemManager;
    @Resource
    private PoManager poManager;
    @Resource
    private ViewManager viewManager;
    private static final Logger LOGGER = Logger.getLogger((Class)OrderAction.class);

    @RequestMapping(value={"/saveProduceRequest.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object saveProduceRequest(@RequestBody ProduceRequestDetail prdreq, HttpServletRequest request) {
        if (prdreq.getId() == null) {
            ProduceRequest req = new ProduceRequest();
            req.setId(UUID.get());
            Sequence seq = this.orderManager.getSequence("PRDP");
            req.setCode(ErpUtils.generateRefNo((Sequence)seq));
            req.setNoticeId(prdreq.getNoticeId());
            req.setOrderId(prdreq.getOrderId());
            SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
            req.setCreatedBy(sUser.getId());
            req.setCreator(sUser.getName());
            req.setCreatedOn(new Date());
            req.setDeliveryDate(prdreq.getDeliveryDate());
            req.setPmcStatus("processing");
            req.setReferenceNo(prdreq.getReferenceNo());
            req.setQty(Double.valueOf(prdreq.getQty()));
            req.setDescription(prdreq.getDescription());
            this.orderManager.addProduceRequest(req);
            for (ProduceRequestItem item : prdreq.getReqItems()) {
                item.setId(UUID.get());
                item.setRequestId(req.getId());
                item.setPmcStatus("processing");
                item.setCompletedQty(Double.valueOf(0.0));
                this.orderManager.addProduceRequestItem(item);
            }
        } else {
            throw new ActionException();
        }
        return prdreq;
    }

    @RequestMapping(value={"/loadProduceRequest.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadProduceRequest(@RequestParam String requestId) {
        ProduceRequest prdRequest = this.orderManager.getProduceRequest(requestId);
        SalesOrder so = this.orderManager.getSalesOrder(prdRequest.getOrderId());
        List<SoItem> soItems = this.getSoItems(so.getId());
        ProduceRequestItemExample query = new ProduceRequestItemExample();
        query.createCriteria().andRequestIdEqualTo(requestId);
        List<ProduceRequestItem> reqItems = this.orderManager.listProduceRequestItems(query);
        HashMap itemMap = new HashMap();
        reqItems.forEach(reqItem -> {
            HashMap<String, ProduceRequestItem> subMap = new HashMap<String, ProduceRequestItem>();
            subMap.put("reqItem", reqItem);
            itemMap.put(reqItem.getItemId(), subMap);
        }
        );
        soItems.forEach(soItem -> {
            ((Map)itemMap.get(soItem.getItemId())).put("soItem", soItem);
        }
        );
        LOG.info((Logger)LOGGER, itemMap);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemMap", itemMap);
        result.put("prdRequest", (Object)prdRequest);
        result.put("salesOrder", (Object)so);
        return result;
    }

    private List<SoItem> getSoItems(String soId) {
        SoItemExample query = new SoItemExample();
        query.createCriteria().andOrderIdEqualTo(soId);
        return (List<SoItem>) orderManager.listSoItems(query);
    }

    @RequestMapping(value={"/listProduceRequests.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listProduceRequests(@RequestBody Search search) {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String status = search.getStatus();
        ProduceRequestViewExample query = new ProduceRequestViewExample();
        ProduceRequestViewExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (deliveryDateFrom != null) {
            criteria.andDeliveryDateGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andDeliveryDateLessThanOrEqualTo(deliveryDateTo);
        }
        if (ErpUtils.isNotEmpty((String)status) && status.startsWith("pmc-")) {
            String pmcStatus = status.split("-")[1];
            List statusList = ErpUtils.getList((String)pmcStatus);
            criteria.andPmcStatusIn(statusList);
        }
        query.setOrderByClause("created_on desc, req_code desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List<ProduceRequest> requests = this.orderManager.listProduceRequests(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.orderManager.countProduceRequestView(query);
        result.put("totalCount", count);
        result.put("produceRequests", requests);
        return result;
    }

    @Transactional
    @RequestMapping(value={"/saveCheckoutReq.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Object saveCheckoutReq(@RequestBody CheckoutRequestDetail obj, HttpServletRequest request) {
        if (obj.getId() == null) {
            obj.setId(UUID.get());
            Sequence seq = this.orderManager.getSequence("CKTREQ");
            obj.setCode(ErpUtils.generateRefNo((Sequence)seq));
            SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
            obj.setCreatedBy(sUser.getId());
            obj.setCreator(sUser.getName());
            obj.setCreatedOn(new Date());
            obj.setPmcStatus("pending");
            this.orderManager.addCheckoutRequest((CheckoutRequest)obj);
            for (CheckoutReqItem item : obj.getObjItems()) {
                item.setId(UUID.get());
                item.setRequestId(obj.getId());
                this.orderManager.addCheckoutReqItem(item);
            }
        } else {
            throw new ActionException();
        }
        return obj.getId();
    }

    @RequestMapping(value={"/saveCheckoutRec.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object saveCheckoutRec(@RequestBody CheckoutRecordDetail obj, HttpServletRequest request) {
        if (obj.getId() == null) {
            String reqId;
            obj.setId(UUID.get());
            Sequence seq = this.orderManager.getSequence("CKTREC");
            obj.setCode(ErpUtils.generateRefNo((Sequence)seq));
            SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
            obj.setCreatedBy(sUser.getId());
            obj.setCreator(sUser.getName());
            obj.setPmcStatus("pending");
            obj.setCreatedOn(new Date());
            this.orderManager.addCheckoutRecord((CheckoutRecord)obj);
            String soId = obj.getSoId();
            for (CheckoutRecordItem item : obj.getObjItems()) {
                item.setId(UUID.get());
                item.setRecordId(obj.getId());
                Storage storage = this.itemManager.getStorage(item.getItemId(), item.getCode(), item.getStorageType());
                if (storage == null) {
                    storage = new Storage();
                    storage.setId(UUID.get());
                    storage.setCode(item.getCode());
                    storage.setItemId(item.getItemId());
                    storage.setType(item.getStorageType());
                    storage.setUom(item.getUom());
                    storage.setQty(Double.valueOf(0.0));
                    this.itemManager.addStorage(storage);
                }
                if (!storage.getUom().equals(item.getUom())) {
                    LOG.warn((Logger)LOGGER, (Object)"UOM is different!");
                    throw new ActionException();
                }
                storage.setQty(Double.valueOf(storage.getQty() - item.getQty()));
                LOG.debug((Logger)LOGGER, (Object)("update storage: " + (Object)storage + ": checkout Qty: " + item.getQty()));
                this.itemManager.updateStorage(storage);
                this.orderManager.addCheckoutRecordItem(item);
                if (!ErpUtils.isNotEmpty((String)soId)) continue;
                SoItemExample sie = new SoItemExample();
                sie.createCriteria().andOrderIdEqualTo(soId).andItemIdEqualTo(item.getItemId());
                List<SoItem> soItems = this.orderManager.listSoItems(sie);
                if (!ErpUtils.hasElement((Collection)soItems)) continue;
                SoItem soItem = (SoItem)soItems.get(0);
                soItem.setShippedQty(Double.valueOf(soItem.getShippedQty() == null ? item.getQty() : soItem.getShippedQty() + item.getQty()));
                this.orderManager.updateSoItem(soItem);
            }
            if (ErpUtils.isNotEmpty((String)soId)) {
                SalesOrder so = this.orderManager.getSalesOrder(soId);
                double shippedQty = obj.getQty();
                if (so.getShippedQty() != null) {
                    shippedQty += so.getShippedQty().doubleValue();
                }
                so.setShippedQty(Double.valueOf(shippedQty));
                so.setPmcStatus(so.getShippedQty().doubleValue() == so.getQty().doubleValue() ? "completed_shipped" : "partially_shipped");
                this.orderManager.updateSalesOrder(so);
            }
            if ((reqId = obj.getRequestId()) != null) {
                if (BEConstants.CHECKOUT_TYPE_SALES.equals(obj.getType())) {
                    DeliveryRequest dlvrReq = this.poManager.getDeliveryRequest(reqId);
                    if (dlvrReq != null) {
                        dlvrReq.setPmcStatus("completed");
                        this.poManager.updateDeliveryRequest(dlvrReq);
                    }
                } else {
                    CheckoutRequest chkReq = this.orderManager.getCheckoutRequest(reqId);
                    if (chkReq != null) {
                        chkReq.setPmcStatus("completed");
                        this.orderManager.updateCheckoutRequest(chkReq);
                    }
                }
            }
        } else {
            throw new ActionException();
        }
        return obj.getId();
    }

    @RequestMapping(value={"/listCheckoutRec.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listCheckoutRec(@RequestBody Search search) {
        String status;
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String type = search.getItemType();
        CheckoutRecordExample query = new CheckoutRecordExample();
        CheckoutRecordExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (ErpUtils.isNotEmpty((String)(status = search.getStatus()))) {
            List statusList = ErpUtils.getList((String)status);
            criteria.andPmcStatusIn(statusList);
        }
        if (ErpUtils.isNotEmpty((String)type)) {
            List typeList = ErpUtils.getList((String)type);
            criteria.andTypeIn(typeList);
        }
        if (deliveryDateFrom != null) {
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        }
        query.setOrderByClause("CODE desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List records = this.orderManager.listCheckoutRecords(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.orderManager.countCheckoutRecord(query);
        result.put("totalCount", count);
        result.put("objList", records);
        return result;
    }

    @RequestMapping(value={"/loadCheckoutRec.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadCheckoutRec(@RequestParam String requestId) {
        SalesOrder so;
        CheckoutRecord checkoutReq = this.orderManager.getCheckoutRecord(requestId);
        CheckoutRecordItemExample example = new CheckoutRecordItemExample();
        example.createCriteria().andRecordIdEqualTo(requestId);
        List<CheckoutRecordItem> reqItems = this.orderManager.listCheckoutRecordItems(example);
        Map<String, Map<String, Object>> itemMap = new HashMap<>();
        ArrayList<String> itemIds = new ArrayList<>();
        reqItems.forEach(reqItem -> {
            HashMap<String, Object> subMap = new HashMap<>();
            subMap.put("objItem", reqItem);
            itemMap.put(reqItem.getId(), subMap);
            itemIds.add(reqItem.getItemId());
        }
        );
        ItemExample iQuery = new ItemExample();
        iQuery.createCriteria().andIdIn(itemIds);
        List<Item> items = this.itemManager.listItems(iQuery);
        Map<String, Item> tmpIMap = new HashMap<>();
        items.forEach(item -> {
        	tmpIMap.put(item.getId(), item);
        }
        );
        itemMap.entrySet().forEach(subEntry -> {
        	Map<String, Object> subMap = subEntry.getValue();
        	CheckoutRecordItem recItem = (CheckoutRecordItem) subMap.get("objItem");
        	subMap.put("item", tmpIMap.get(recItem.getItemId()));
        });
        LOG.info((Logger)LOGGER, itemMap);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemMap", itemMap);
        result.put("obj", (Object)checkoutReq);
        if (checkoutReq.getSoId() != null && (so = this.orderManager.getSalesOrder(checkoutReq.getSoId())) != null) {
            result.put("buyerId", so.getBuyerId());
        }
        return result;
    }

    @RequestMapping(value={"/loadCheckoutReq.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadCheckoutReq(@RequestParam String requestId) {
        CheckoutRequest checkoutReq = this.orderManager.getCheckoutRequest(requestId);
        CheckoutReqItemExample example = new CheckoutReqItemExample();
        example.createCriteria().andRequestIdEqualTo(requestId);
        List<CheckoutReqItem> reqItems = this.orderManager.listCheckoutReqItems(example);
        Map<String, Map<String, Object>> itemMap = new HashMap<>();
        List<String> itemIds = new ArrayList<>();
        reqItems.forEach(reqItem -> {
            Map<String, Object> subMap = new HashMap<>();
            subMap.put("objItem", reqItem);
            itemMap.put(reqItem.getId(), subMap);
            itemIds.add(reqItem.getItemId());
        }
        );
        ItemExample iQuery = new ItemExample();
        iQuery.createCriteria().andIdIn(itemIds);
        List<Item> items = itemManager.listItems(iQuery);
        Map<String, Item> tmpIMap = new HashMap<>();
        items.forEach(item -> {
        	tmpIMap.put(item.getId(), item);
        }
        );
        itemMap.entrySet().forEach(subEntry -> {
        	Map<String, Object> subMap = subEntry.getValue();
        	CheckoutReqItem reqItem = (CheckoutReqItem) subMap.get("objItem");
        	subMap.put("item", tmpIMap.get(reqItem.getItemId()));
        });
        LOG.info((Logger)LOGGER, itemMap);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemMap", itemMap);
        result.put("obj", (Object)checkoutReq);
        return result;
    }

    @RequestMapping(value={"/listCheckoutReq.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listCheckoutReq(@RequestBody Search search) {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String status = search.getStatus();
        CheckoutRequestExample query = new CheckoutRequestExample();
        CheckoutRequestExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (deliveryDateFrom != null) {
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        }
        if (ErpUtils.isNotEmpty((String)status)) {
            criteria.andPmcStatusEqualTo(status);
        }
        query.setOrderByClause("CODE desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List<CheckoutRequest> requests = this.orderManager.listCheckoutRequests(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.orderManager.countCheckoutRequest(query);
        result.put("totalCount", count);
        result.put("objList", requests);
        return result;
    }

    @RequestMapping(value={"/listItemFromPrdreq.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listItemFromPrdreq(@RequestParam String prdreqId) {
        ProduceRequest prdRequest = this.orderManager.getProduceRequest(prdreqId);
        ProduceRequestItemExample query = new ProduceRequestItemExample();
        query.createCriteria().andRequestIdEqualTo(prdreqId);
        List<ProduceRequestItem> reqItems = this.orderManager.listProduceRequestItems(query);
        ArrayList itemList = new ArrayList();
        HashMap itemMap = new HashMap();
        reqItems.forEach(reqItem -> {
            ProductExample pQuery = new ProductExample();
            pQuery.createCriteria().andItemIdEqualTo(reqItem.getItemId());
            List<Product> prds = this.itemManager.listProducts(pQuery);
            ArrayList prdIds = new ArrayList();
            prds.forEach(prd -> {
                prdIds.add(prd.getId());
            }
            );
            ProductItemExample piQuery = new ProductItemExample();
            piQuery.createCriteria().andProductIdIn(prdIds);
            List<ProductItem> pItems = this.itemManager.listProductItems(piQuery);
            pItems.forEach(pItem -> {
                StorageExample exp = new StorageExample();
                exp.createCriteria().andItemIdEqualTo(pItem.getItemId());
                List<Storage> sts = this.itemManager.listStorages(exp);
                Double balanceQty = 0.0;
                for (Storage st : sts) {
                    balanceQty = balanceQty + st.getQty();
                }
                HashMap<String, Object> subMap = (HashMap<String, Object>)itemMap.get(pItem.getItemId());
                if (subMap == null) {
                    subMap = new HashMap<String, Object>();
                    itemList.add(pItem.getItemId());
                    itemMap.put(pItem.getItemId(), subMap);
                }
                subMap.put("balanceQty", balanceQty);
                ItemRequestedView requestedQty = this.viewManager.getItemRequestedView(pItem.getItemId());
                if (requestedQty != null) {
                    subMap.put("pendingQty", requestedQty.getQty() - requestedQty.getInstockQty());
                }
                CheckoutReqItem crItem = (CheckoutReqItem)subMap.get("checkoutReqItem");
                if (crItem  == null) {
                    crItem = new CheckoutReqItem();
                    crItem.setQty(Double.valueOf(0.0));
                    crItem.setItemId(pItem.getItemId());
                    crItem.setUom(pItem.getUom());
                    subMap.put("checkoutReqItem", crItem);
                }
                crItem.setQty(Double.valueOf(crItem.getQty() + (double)pItem.getQty().intValue() * reqItem.getQty()));
            }
            );
        }
        );
        ItemExample iQuery = new ItemExample();
        iQuery.createCriteria().andIdIn(itemList);
        List<Item> items = this.itemManager.listItems(iQuery);
        CheckoutRequestExample cre = new CheckoutRequestExample();
        cre.createCriteria().andProduceReqIdEqualTo(prdreqId);
        List<CheckoutRequest> reqList = this.orderManager.listCheckoutRequests(cre);
        ArrayList reqIds = new ArrayList();
        if (ErpUtils.hasElement((Collection)reqList)) {
            reqList.forEach(req -> {
                reqIds.add(req.getId());
            }
            );
        }
        items.forEach(item -> {
            double checkedOutQty = 0.0;
            if (reqIds.size() > 0) {
                CheckoutReqItemExample crie = new CheckoutReqItemExample();
                crie.createCriteria().andRequestIdIn(reqIds).andItemIdEqualTo(item.getId());
                List<CheckoutReqItem> chkoutItems = this.orderManager.listCheckoutReqItems(crie);
                if (ErpUtils.hasElement((Collection)chkoutItems)) {
                    for (CheckoutReqItem tItem : chkoutItems) {
                        checkedOutQty += tItem.getQty().doubleValue();
                    }
                }
            }
            ((Map)itemMap.get(item.getId())).put("checkedOutQty", checkedOutQty);
            //setup the class code for subid
            List<ItemSupplierMapping> maps = itemManager.listItemSupplierMappingsByItemId(item.getId());
            List<String> subCodes = new ArrayList<>();
            maps.forEach(map -> {
            	subCodes.add(item.getCode() + "/" + map.getClassCode());
            });
            if (subCodes.size() == 0) {
            	subCodes.add(item.getCode());
            }
            ((Map)itemMap.get(item.getId())).put("subCodes", subCodes);

            ((Map)itemMap.get(item.getId())).put("item", item);
        }
        );
        LOG.info((Logger)LOGGER, itemMap);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemMap", itemMap);
        result.put("prdRequest", (Object)prdRequest);
        return result;
    }

    @RequestMapping(value={"/listItemFromSo.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listItemFromSo(@RequestParam String soId) {
        SalesOrder so = this.orderManager.getSalesOrder(soId);
        SoItemExample query = new SoItemExample();
        query.createCriteria().andOrderIdEqualTo(soId);
        List<SoItem> soItems = this.orderManager.listSoItems(query);
        ArrayList<String> itemList = new ArrayList();
        HashMap itemMap = new HashMap();
        soItems.forEach(reqItem -> {
            ProductExample pQuery = new ProductExample();
            pQuery.createCriteria().andItemIdEqualTo(reqItem.getItemId());
            List<Product> prds = this.itemManager.listProducts(pQuery);
            ArrayList prdIds = new ArrayList();
            prds.forEach(prd -> {
                prdIds.add(prd.getId());
            }
            );
            ProductItemExample piQuery = new ProductItemExample();
            piQuery.createCriteria().andProductIdIn(prdIds);
            List<ProductItem> pItems = this.itemManager.listProductItems(piQuery);
            pItems.forEach(pItem -> {
                StorageExample exp = new StorageExample();
                exp.createCriteria().andItemIdEqualTo(pItem.getItemId());
                List<Storage> sts = this.itemManager.listStorages(exp);
                Double balanceQty = 0.0;
                for (Storage st : sts) {
                    balanceQty = balanceQty + st.getQty();
                }
                HashMap<String, Object> subMap = (HashMap<String, Object>)itemMap.get(pItem.getItemId());
                if (subMap == null) {
                    subMap = new HashMap<String, Object>();
                    itemList.add(pItem.getItemId());
                    itemMap.put(pItem.getItemId(), subMap);
                }
                subMap.put("balanceQty", balanceQty);
                ItemRequestedView requestedQty = this.viewManager.getItemRequestedView(pItem.getItemId());
                if (requestedQty != null) {
                    subMap.put("pendingQty", requestedQty.getQty() - requestedQty.getInstockQty());
                }
                OrderRequestItem orItem = (OrderRequestItem)subMap.get("orItem");
                if (orItem == null) {
                    orItem = new OrderRequestItem();
                    orItem.setQty(Double.valueOf(0.0));
                    orItem.setItemId(pItem.getItemId());
                    orItem.setUom(pItem.getUom());
                    subMap.put("orItem", orItem);
                }
                orItem.setQty(Double.valueOf(orItem.getQty() + (double)pItem.getQty().intValue() * reqItem.getQty()));
            }
            );
        }
        );
        ItemExample iQuery = new ItemExample();
        iQuery.createCriteria().andIdIn(itemList);
        List<Item> items = this.itemManager.listItems(iQuery);
        items.forEach(item -> {
            ((Map)itemMap.get(item.getId())).put("item", item);
        }
        );
        LOG.info((Logger)LOGGER, itemMap);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemMap", itemMap);
        result.put("so", (Object)so);
        return result;
    }

    @RequestMapping(value={"/exportCheckoutReq.do"}, method={RequestMethod.GET})
    public ModelAndView exportCheckoutReq(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo) {
        Date deliveryDateFrom = ErpUtils.isNotEmpty((String)dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty((String)dateTo) ? new Date(dateTo) : null;
        CheckoutRequestExample query = new CheckoutRequestExample();
        CheckoutRequestExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (deliveryDateFrom != null) {
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        }
        query.setOrderByClause("CODE desc");
        List requests = this.orderManager.listCheckoutRequests(query);
        CheckoutRequestExcelModel viewModel = new CheckoutRequestExcelModel(requests);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }

    @RequestMapping(value={"/exportCheckoutRec.do"}, method={RequestMethod.GET})
    public ModelAndView exportCheckoutRec(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String type) {
        Date deliveryDateFrom = ErpUtils.isNotEmpty((String)dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty((String)dateTo) ? new Date(dateTo) : null;
        CheckoutRecordExample query = new CheckoutRecordExample();
        CheckoutRecordExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (ErpUtils.isNotEmpty((String)type)) {
            List typeList = ErpUtils.getList((String)type);
            criteria.andTypeIn(typeList);
        }
        if (deliveryDateFrom != null) {
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        }
        query.setOrderByClause("CODE desc");
        List records = this.orderManager.listCheckoutRecords(query);
        CheckoutRecordExcelModel viewModel = new CheckoutRecordExcelModel(records);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }

    @RequestMapping(value={"/exportProduceRequests.do"}, method={RequestMethod.GET})
    public ModelAndView exportProduceRequests(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String status) {
        Date deliveryDateFrom = ErpUtils.isNotEmpty((String)dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty((String)dateTo) ? new Date(dateTo) : null;
        ProduceRequestViewExample query = new ProduceRequestViewExample();
        ProduceRequestViewExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (deliveryDateFrom != null) {
            criteria.andDeliveryDateGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andDeliveryDateLessThanOrEqualTo(deliveryDateTo);
        }
        if (ErpUtils.isNotEmpty((String)status) && status.startsWith("pmc-")) {
            String pmcStatus = status.split("-")[1];
            criteria.andPmcStatusEqualTo(pmcStatus);
        }
        query.setOrderByClause("created_on desc, req_code desc");
        List requests = this.orderManager.listProduceRequests(query);
        PrdReqExcelModel viewModel = new PrdReqExcelModel(requests);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }
}
