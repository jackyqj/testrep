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

import com.wecloud.erp.entity.CheckinRecDetail;
import com.wecloud.erp.entity.Search;
import com.wecloud.erp.entity.SessionUser;
import com.wecloud.erp.model.CheckinRecord;
import com.wecloud.erp.model.CheckinRecordExample;
import com.wecloud.erp.model.CheckinRecordItem;
import com.wecloud.erp.model.CheckinRecordItemExample;
import com.wecloud.erp.model.Item;
import com.wecloud.erp.model.ItemExample;
import com.wecloud.erp.model.OrderRequest;
import com.wecloud.erp.model.OrderRequestItem;
import com.wecloud.erp.model.OrderRequestItemExample;
import com.wecloud.erp.model.PO;
import com.wecloud.erp.model.ProduceNotice;
import com.wecloud.erp.model.ProduceRequest;
import com.wecloud.erp.model.ProduceRequestItem;
import com.wecloud.erp.model.ProduceRequestItemExample;
import com.wecloud.erp.model.QcRecord;
import com.wecloud.erp.model.QcRequest;
import com.wecloud.erp.model.SalesOrder;
import com.wecloud.erp.model.Sequence;
import com.wecloud.erp.model.Storage;
import com.wecloud.erp.report.impl.CheckinRecordExcelModel;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.OrderManager;
import com.wecloud.erp.service.PoManager;
import com.wecloud.erp.utils.ErpUtils;
import com.wecloud.erp.utils.LOG;
import com.wecloud.erp.utils.UUID;
import com.wecloud.erp.web.exception.ActionException;

@Controller
public class CheckinController {
    @Resource
    private OrderManager orderManager;
    @Resource
    private PoManager poManager;
    @Resource
    private ItemManager itemManager;
    private static final Logger LOGGER = Logger.getLogger(CheckinController.class);

    @RequestMapping(value={"/saveCheckinRec.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object saveCheckinRec(@RequestBody CheckinRecDetail object, HttpServletRequest request) {
        CheckinRecord obj = object.getObj();
        SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        if (obj.getId() == null) {
            String reqId;
            String qcrecId;
            obj.setId(UUID.get());
            Sequence seq = this.orderManager.getSequence("QCREC");
            obj.setCode(ErpUtils.generateRefNo((Sequence)seq));
            obj.setCreatedBy(sUser.getId());
            obj.setCreator(sUser.getName());
            obj.setCreatedOn(new Date());
            this.poManager.addCheckinRecord(obj);
            HashMap<String, Double> qtyMap = new HashMap<String, Double>();
            HashMap<String, CheckinRecordItem> itemMap = new HashMap<String, CheckinRecordItem>();
            for (CheckinRecordItem item : object.getItems()) {
                item.setId(UUID.get());
                item.setRecordId(obj.getId());
                itemMap.put(item.getItemId(), item);
                qtyMap.put(item.getItemId(), item.getQty());
                Storage storage = this.itemManager.getStorage(item.getItemId(), item.getCode(), item.getStorageType());
                if (storage == null) {
                    storage = new Storage();
                    storage.setId(UUID.get());
                    storage.setItemId(item.getItemId());
                    storage.setCode(item.getCode());
                    storage.setType(item.getStorageType());
                    storage.setUom(item.getUom());
                    storage.setQty(item.getQty());
                    LOG.debug((Logger)LOGGER, (Object)("add storage: " + (Object)storage + ": checkin Qty: " + item.getQty()));
                    this.itemManager.addStorage(storage);
                } else if (storage.getUom().equals(item.getUom())) {
                    storage.setQty(Double.valueOf(storage.getQty() + item.getQty()));
                    LOG.debug((Logger)LOGGER, (Object)("update storage: " + (Object)storage + ": checkin Qty: " + item.getQty()));
                    this.itemManager.updateStorage(storage);
                } else {
                    LOG.warn((Logger)LOGGER, (Object)"UOM is different!");
                    throw new ActionException();
                }
                item.setStorageId(storage.getId());
                this.poManager.addCheckinRecordItem(item);
            }
            String qcreqId = obj.getQcRequestId();
            if (ErpUtils.isNotEmpty((String)qcreqId)) {
                LOG.info((Logger)LOGGER, (Object)("update item status of the Qcrequest: " + qcreqId));
                QcRequest qcReq = this.poManager.getQcRequest(qcreqId);
                PO po = this.poManager.getPO(qcReq.getOrderId());
                OrderRequest orderReq = this.poManager.getOrderRequest(po.getRequestId());
                Double shippedQty = po.getShippedQty() == null ? 0.0 : po.getShippedQty();
                Double ordReqQty = orderReq.getShippedQty() == null ? 0.0 : orderReq.getShippedQty();
                shippedQty = shippedQty + obj.getQty();
                po.setShippedQty(shippedQty);
                ordReqQty = ordReqQty + obj.getQty();
                orderReq.setShippedQty(ordReqQty);
                if (po.getQty() > po.getShippedQty()) {
                    po.setPmcStatus("partially");
                } else {
                    po.setPmcStatus("completed");
                }
                if (orderReq.getQty() > orderReq.getShippedQty()) {
                    orderReq.setPmcStatus("partially");
                } else {
                    orderReq.setPmcStatus("completed");
                }
                qcReq.setQcStatus("completed");
                this.poManager.updateQcRequest(qcReq);
                this.poManager.updateOrderRequest(orderReq);
                this.poManager.updatePO(po);
                OrderRequestItemExample orq = new OrderRequestItemExample();
                orq.createCriteria().andIdEqualTo(po.getRequestId());
                List<OrderRequestItem> oriList = this.poManager.listOrderRequestItems(orq);
                if (ErpUtils.hasElement((Collection)oriList)) {
                    for (OrderRequestItem ori : oriList) {
                        if (!qtyMap.containsKey(ori.getItemId())) continue;
                        ori.setInstockQty(Double.valueOf(ori.getInstockQty() + (Double)qtyMap.get(ori.getItemId())));
                        this.poManager.updateOrderRequestItem(ori);
                    }
                }
            }
            if (ErpUtils.isNotEmpty((String)(qcrecId = obj.getQcRecordId()))) {
                LOG.info((Logger)LOGGER, (Object)("update item status of the Qcrecord: " + qcrecId));
                QcRecord qcRec = this.poManager.getQcRecord(qcrecId);
                qcRec.setQcStatus("completed");
                this.poManager.updateQcRecord(qcRec);
            }
            if (ErpUtils.isNotEmpty((String)(reqId = obj.getProduceRequestId()))) {
                LOG.info((Logger)LOGGER, (Object)("update item status of the producerequest: " + reqId));
                ProduceRequestItemExample example = new ProduceRequestItemExample();
                example.createCriteria().andRequestIdEqualTo(reqId);
                List<ProduceRequestItem> reqItems = this.orderManager.listProduceRequestItems(example);
                boolean allItemsCompleted = true;
                double completedQty = 0.0;
                for (ProduceRequestItem reqItem : reqItems) {
                    boolean pmcCompleted = "completed".equals(reqItem.getPmcStatus());
                    if (itemMap.containsKey(reqItem.getItemId())) {
                        CheckinRecordItem cItem = (CheckinRecordItem)itemMap.get(reqItem.getItemId());
                        reqItem.setCompletedQty(Double.valueOf(reqItem.getCompletedQty() == null ? cItem.getQty() : reqItem.getCompletedQty() + cItem.getQty()));
                        boolean bl = pmcCompleted = reqItem.getQty().doubleValue() == reqItem.getCompletedQty().doubleValue();
                        if (pmcCompleted) {
                            reqItem.setPmcStatus("completed");
                        } else {
                            reqItem.setPmcStatus("partially");
                        }
                        completedQty += reqItem.getCompletedQty().doubleValue();
                        this.orderManager.updateProduceRequestItem(reqItem);
                    }
                    boolean bl = allItemsCompleted = allItemsCompleted && pmcCompleted;
                }
                ProduceRequest req = this.orderManager.getProduceRequest(reqId);
                if (req.getCompletedQty() != null) {
                    req.setCompletedQty(Double.valueOf(completedQty + req.getCompletedQty()));
                } else {
                    req.setCompletedQty(Double.valueOf(completedQty));
                }
                String newStatus = req.getQty().doubleValue() == req.getCompletedQty().doubleValue() ? "completed" : "partially";
                LOG.info((Logger)LOGGER, (Object)("update status of the producerequest: " + reqId));
                req.setPmcStatus(newStatus);
                SalesOrder so = this.orderManager.getSalesOrder(req.getOrderId());
                if (so.getCompletedQty() != null) {
                    so.setCompletedQty(Double.valueOf(completedQty + req.getCompletedQty()));
                } else {
                    so.setCompletedQty(Double.valueOf(completedQty));
                }
                String soStatus = so.getQty().doubleValue() == so.getCompletedQty().doubleValue() ? "completed" : "partially";
                String oriSoStatus = so.getPmcStatus();
                if (!"partially_shipped".equals(oriSoStatus) && !"completed_shipped".equals(oriSoStatus)) {
                    so.setPmcStatus(soStatus);
                }
                ProduceNotice notice = this.orderManager.getProduceNotice(req.getNoticeId());
                notice.setPmcStatus(so.getPmcStatus());
                this.orderManager.updateProduceNotice(notice);
                this.orderManager.updateProduceRequest(req);
                this.orderManager.updateSalesOrder(so);
            }
        } else {
            throw new ActionException();
        }
        return object;
    }

    @RequestMapping(value={"/listCheckinRec.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listCheckinRec(@RequestBody Search search) {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String type = search.getItemType();
        CheckinRecordExample query = new CheckinRecordExample();
        CheckinRecordExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (deliveryDateFrom != null) {
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        }
        if ("po".equals(type)) {
            criteria.andTypeEqualTo(type);
        } else {
            criteria.andTypeNotEqualTo("po");
        }
        query.setOrderByClause("CODE desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List records = this.poManager.listCheckinRecords(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.poManager.countCheckinRecord(query);
        result.put("totalCount", count);
        result.put("objList", records);
        return result;
    }

    @RequestMapping(value={"/loadCheckinRec.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadCheckinRec(@RequestParam String objectId) {
        CheckinRecord qcRec = this.poManager.getCheckinRecord(objectId);
        CheckinRecordItemExample example = new CheckinRecordItemExample();
        example.createCriteria().andRecordIdEqualTo(objectId);
        List<CheckinRecordItem> recItems = this.poManager.listCheckinRecordItems(example);
        Map<String, Map<String, Object>> itemMap = new HashMap<>();
        List<String> itemIds = new ArrayList<>();
        recItems.forEach(recItem -> {
            HashMap<String, Object> subMap = new HashMap<>();
            subMap.put("objItem", recItem);
            itemMap.put(recItem.getId(), subMap);
            itemIds.add(recItem.getItemId());
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
        	CheckinRecordItem recItem = (CheckinRecordItem) subMap.get("objItem");
        	subMap.put("item", tmpIMap.get(recItem.getItemId()));
        });
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("obj", (Object)qcRec);
        result.put("itemMap", itemMap);
        return result;
    }

    @RequestMapping(value={"/listPrdFromPrdreq.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listPrdFromPrdreq(@RequestParam String prdreqId) {
        ProduceRequest prdRequest = this.orderManager.getProduceRequest(prdreqId);
        ProduceRequestItemExample query = new ProduceRequestItemExample();
        query.createCriteria().andRequestIdEqualTo(prdreqId);
        List<ProduceRequestItem> reqItems = this.orderManager.listProduceRequestItems(query);
        ArrayList itemList = new ArrayList();
        HashMap itemMap = new HashMap();
        reqItems.forEach(reqItem -> {
            HashMap<String, ProduceRequestItem> subMap = new HashMap<String, ProduceRequestItem>();
            subMap.put("reqItem", reqItem);
            itemMap.put(reqItem.getItemId(), subMap);
            itemList.add(reqItem.getItemId());
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
        result.put("prdRequest", (Object)prdRequest);
        return result;
    }

    @RequestMapping(value={"/exportCheckinRec.do"}, method={RequestMethod.GET})
    public ModelAndView exportCheckinRec(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String type) {
        Date deliveryDateFrom = ErpUtils.isNotEmpty((String)dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty((String)dateTo) ? new Date(dateTo) : null;
        CheckinRecordExample query = new CheckinRecordExample();
        CheckinRecordExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (deliveryDateFrom != null) {
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        }
        if ("po".equals(type)) {
            criteria.andTypeEqualTo(type);
        } else {
            criteria.andTypeNotEqualTo("po");
        }
        query.setOrderByClause("CODE desc");
        List records = this.poManager.listCheckinRecords(query);
        CheckinRecordExcelModel viewModel = new CheckinRecordExcelModel(records);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }
}
