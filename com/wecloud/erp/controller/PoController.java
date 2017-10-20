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

import com.wecloud.erp.business.CalculateModel;
import com.wecloud.erp.entity.OrderRequestDetail;
import com.wecloud.erp.entity.PODetail;
import com.wecloud.erp.entity.PoReturnDetail;
import com.wecloud.erp.entity.Search;
import com.wecloud.erp.entity.SessionUser;
import com.wecloud.erp.model.Item;
import com.wecloud.erp.model.ItemExample;
import com.wecloud.erp.model.ItemRequestedView;
import com.wecloud.erp.model.ItemSupplierMapping;
import com.wecloud.erp.model.ItemSupplierMappingExample;
import com.wecloud.erp.model.OrderRequest;
import com.wecloud.erp.model.OrderRequestExample;
import com.wecloud.erp.model.OrderRequestItem;
import com.wecloud.erp.model.OrderRequestItemExample;
import com.wecloud.erp.model.PO;
import com.wecloud.erp.model.POExample;
import com.wecloud.erp.model.PoItem;
import com.wecloud.erp.model.PoItemExample;
import com.wecloud.erp.model.PoReturn;
import com.wecloud.erp.model.PoReturnExample;
import com.wecloud.erp.model.PoReturnItem;
import com.wecloud.erp.model.PoReturnItemExample;
import com.wecloud.erp.model.Product;
import com.wecloud.erp.model.ProductExample;
import com.wecloud.erp.model.ProductItem;
import com.wecloud.erp.model.ProductItemExample;
import com.wecloud.erp.model.Sequence;
import com.wecloud.erp.model.SoItem;
import com.wecloud.erp.model.SoItemExample;
import com.wecloud.erp.model.Storage;
import com.wecloud.erp.model.StorageExample;
import com.wecloud.erp.report.impl.OrderReqExcelModel;
import com.wecloud.erp.report.impl.PoExcelModel;
import com.wecloud.erp.report.impl.PoReturnExcelModel;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.OrderManager;
import com.wecloud.erp.service.PoManager;
import com.wecloud.erp.service.ViewManager;
import com.wecloud.erp.utils.ErpUtils;
import com.wecloud.erp.utils.LOG;
import com.wecloud.erp.utils.UUID;
import com.wecloud.erp.web.exception.ActionException;

@Controller
public class PoController {
    @Resource
    private PoManager poManager;
    @Resource
    private OrderManager orderManager;
    @Resource
    private ItemManager itemManager;
    @Resource
    private ViewManager viewManager;
    private static final Logger LOGGER = Logger.getLogger((Class)PoController.class);

    @RequestMapping(value={"/saveOrderReq.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object saveOrderRequest(@RequestBody OrderRequestDetail orderReqDtl, HttpServletRequest request) {
        OrderRequest orderReq = orderReqDtl.getOrderReq();
        if (orderReq.getId() == null) {
            orderReq.setId(UUID.get());
            Sequence seq = this.orderManager.getSequence("ODREQ");
            orderReq.setCode(ErpUtils.generateRefNo((Sequence)seq));
            SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
            orderReq.setCreatedBy(sUser.getId());
            orderReq.setCreatorName(sUser.getName());
            orderReq.setStatus("draft");
            orderReq.setCreatedOn(new Date());
            orderReq.setQty(Double.valueOf(orderReqDtl.getTotalQty()));
            this.poManager.addOrderRequest(orderReq);
            for (OrderRequestItem item : orderReqDtl.getItems()) {
                item.setId(UUID.get());
                item.setRequestId(orderReq.getId());
                item.setInstockQty(Double.valueOf(0.0));
                this.poManager.addOrderRequestItem(item);
            }
        } else {
            orderReq.setQty(Double.valueOf(orderReqDtl.getTotalQty()));
            if ("final".equals(orderReq.getStatus())) {
                orderReq.setdApprovalStatus("pending");
                orderReq.setmApprovalStatus("pending");
                orderReq.setPmcStatus("pending");
            }
            this.poManager.updateOrderRequest(orderReq);
            OrderRequestItemExample example = new OrderRequestItemExample();
            example.createCriteria().andRequestIdEqualTo(orderReq.getId());
            this.poManager.deleteOrderRequestItemByExample(example);
            for (OrderRequestItem item : orderReqDtl.getItems()) {
                item.setId(UUID.get());
                item.setRequestId(orderReq.getId());
                item.setInstockQty(Double.valueOf(0.0));
                this.poManager.addOrderRequestItem(item);
            }
        }
        return orderReqDtl;
    }

    @RequestMapping(value={"/loadOrderReq.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadOrderRequest(@RequestParam String requestId) {
        OrderRequest orderReq = this.poManager.getOrderRequest(requestId);
        OrderRequestItemExample example = new OrderRequestItemExample();
        example.createCriteria().andRequestIdEqualTo(orderReq.getId());
        List<OrderRequestItem> items = this.poManager.listOrderRequestItems(example);
        ArrayList reqItemIdList = new ArrayList();
        items.forEach(item -> {
            reqItemIdList.add(item.getItemId());
        }
        );
        String soId = orderReq.getProduceRequestId();
        HashMap itemMap = new HashMap();
        if (ErpUtils.isNotEmpty((String)soId)) {
            SoItemExample query = new SoItemExample();
            query.createCriteria().andOrderIdEqualTo(soId);
            List<SoItem> reqItems = this.orderManager.listSoItems(query);
            ArrayList itemList = new ArrayList();
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
                    if (reqItemIdList.contains(pItem.getItemId())) {
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
                        Double requestQty = (Double)subMap.get("requestQty");
                        if (requestQty == null) {
                            subMap.put("requestQty", (double)pItem.getQty().intValue() * reqItem.getQty());
                        } else {
                            subMap.put("requestQty", requestQty + (double)pItem.getQty().intValue() * reqItem.getQty());
                        }
                        ItemRequestedView requestedQty = this.viewManager.getItemRequestedView(pItem.getItemId());
                        if (requestedQty != null) {
                            subMap.put("pendingQty", requestedQty.getQty() - requestedQty.getInstockQty());
                        }
                    }
                }
                );
            }
            );
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("orderReq", (Object)orderReq);
        result.put("items", items);
        result.put("qtys", itemMap);
        return result;
    }

    @RequestMapping(value={"/loadOrderReqForPo.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadOrderRequestForPo(@RequestParam String requestId, @RequestParam String supplierId) {
        OrderRequest orderReq = this.poManager.getOrderRequest(requestId);
        OrderRequestItemExample example = new OrderRequestItemExample();
        example.createCriteria().andRequestIdEqualTo(orderReq.getId());
        List<OrderRequestItem> reqItems = this.poManager.listOrderRequestItems(example);
        Map<String, Object> itemMap = new HashMap<>();
        ArrayList<String> itemIds = new ArrayList<>();
        reqItems.forEach(recItem -> {
            HashMap<String, OrderRequestItem> subMap = new HashMap<String, OrderRequestItem>();
            subMap.put("objItem", recItem);
            itemMap.put(recItem.getItemId(), subMap);
            itemIds.add(recItem.getItemId());
        }
        );
        ItemSupplierMappingExample imQuery = new ItemSupplierMappingExample();
        imQuery.createCriteria().andSupplierIdEqualTo(supplierId).andItemIdIn(itemIds);
        List<ItemSupplierMapping> mappings = this.itemManager.listItemSupplierMappings(imQuery);
        ArrayList validIds = new ArrayList();
        Map<String, String> codeMap = new HashMap<>();
        if (mappings != null) {
            mappings.forEach(map -> {
                validIds.add(map.getItemId());
                codeMap.put(map.getItemId(), map.getClassCode());
            }
            );
        }
        if (ErpUtils.hasElement(validIds)) {
            ItemExample iQuery = new ItemExample();
            iQuery.createCriteria().andIdIn(validIds);
            List<Item> items = this.itemManager.listItems(iQuery);
            items.forEach(item -> {
            	Map<String, Object> subMap = (Map<String, Object>) itemMap.get(item.getId());
            	OrderRequestItem recItem = (OrderRequestItem) subMap.get("objItem");
            	recItem.setCode(item.getCode() + "/" + codeMap.get(item.getId()));
                subMap.put("item", item);
            }
            );
        }
        itemIds.forEach(key -> {
            if (!validIds.contains(key)) {
                itemMap.remove(key);
            }
        }
        );
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemMap", itemMap.values());
        result.put("orderReq", (Object)orderReq);
        return result;
    }

    @RequestMapping(value={"/dApproveOrderReq.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> dApproveOrderReq(@RequestParam String requestId, HttpServletRequest request) {
        List<String> idList = ErpUtils.getList((String)requestId);
        OrderRequest orderReq = null;
        SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        for (String reqId : idList) {
            orderReq = this.poManager.getOrderRequest(reqId);
            orderReq.setdApprovalStatus("approved");
            orderReq.setPmcStatus(orderReq.getmApprovalStatus());
            orderReq.setdApproverId(sUser.getId());
            orderReq.setdApprover(sUser.getName());
            this.poManager.updateOrderRequest(orderReq);
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("orderReq", orderReq);
        return result;
    }

    @RequestMapping(value={"/mApproveOrderReq.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> mApproveOrderReq(@RequestParam String requestId, HttpServletRequest request) {
        List<String> idList = ErpUtils.getList((String)requestId);
        OrderRequest orderReq = null;
        SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        for (String reqId : idList) {
            orderReq = this.poManager.getOrderRequest(reqId);
            orderReq.setmApprovalStatus("approved");
            orderReq.setPmcStatus(orderReq.getdApprovalStatus());
            orderReq.setmApproverId(sUser.getId());
            orderReq.setmApprover(sUser.getName());
            this.poManager.updateOrderRequest(orderReq);
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("orderReq", orderReq);
        return result;
    }

    @RequestMapping(value={"/listOrderReq.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listOrderRequests(@RequestBody Search search) {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String status = search.getStatus();
        OrderRequestExample query = new OrderRequestExample();
        OrderRequestExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (deliveryDateFrom != null) {
            criteria.andDeliveryDateGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andDeliveryDateLessThanOrEqualTo(deliveryDateTo);
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
        List requests = this.poManager.listOrderRequests(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.poManager.countOrderRequest(query);
        result.put("totalCount", count);
        result.put("objList", requests);
        return result;
    }

    @RequestMapping(value={"/deleteOrderReq.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object deleteOrderReq(@RequestParam String orderId, HttpServletRequest request) {
        OrderRequestItemExample example = new OrderRequestItemExample();
        example.createCriteria().andRequestIdEqualTo(orderId);
        this.poManager.deleteOrderRequestItemByExample(example);
        this.poManager.deleteOrderRequest(orderId);
        return orderId;
    }

    @Transactional
    @RequestMapping(value={"/savePo.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Object savePO(@RequestBody PODetail poDtl, HttpServletRequest request) {
        PO po = poDtl.getOrderReq();
        if (po.getId() == null) {
            po.setId(UUID.get());
            Sequence seq = this.orderManager.getSequence("PO");
            po.setCode(ErpUtils.generateRefNo((Sequence)seq));
            SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
            po.setCreatedBy(sUser.getId());
            po.setCreatorName(sUser.getName());
            po.setStatus("draft");
            po.setCreatedOn(new Date());
            CalculateModel.calculate((PODetail)poDtl);
            this.poManager.addPO(po);
            for (PoItem item : poDtl.getItems()) {
                item.setId(UUID.get());
                item.setOrderId(po.getId());
                item.setInstockQty(Double.valueOf(0.0));
                this.poManager.addPoItem(item);
            }
        } else {
            CalculateModel.calculate((PODetail)poDtl);
            if ("final".equals(po.getStatus())) {
                po.setdApprovalStatus("pending");
                po.setmApprovalStatus("pending");
                po.setPmcStatus("pending");
            }
            this.poManager.updatePO(po);
            PoItemExample example = new PoItemExample();
            example.createCriteria().andOrderIdEqualTo(po.getId());
            this.poManager.deletePoItemByExample(example);
            for (PoItem item : poDtl.getItems()) {
                item.setId(UUID.get());
                item.setOrderId(po.getId());
                item.setInstockQty(Double.valueOf(0.0));
                this.poManager.addPoItem(item);
            }
        }
        return poDtl;
    }

    @RequestMapping(value={"/loadPo.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadPO(@RequestParam String requestId) {
        PO po = this.poManager.getPO(requestId);
        PoItemExample example = new PoItemExample();
        example.createCriteria().andOrderIdEqualTo(po.getId());
        List items = this.poManager.listPoItems(example);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("po", (Object)po);
        result.put("items", items);
        return result;
    }

    @RequestMapping(value={"/dApprovePo.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> dApprovePO(@RequestParam String requestId, HttpServletRequest request) {
        List<String> idList = ErpUtils.getList((String)requestId);
        PO po = null;
        SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        for (String reqId : idList) {
            po = this.poManager.getPO(reqId);
            po.setdApprovalStatus("approved");
            po.setApprovalStatus(po.getmApprovalStatus());
            po.setPmcStatus(po.getApprovalStatus());
            po.setdApproverId(sUser.getId());
            po.setdApprover(sUser.getName());
            this.poManager.updatePO(po);
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("po", po);
        return result;
    }

    @RequestMapping(value={"/mApprovePo.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> mApprovePO(@RequestParam String requestId, HttpServletRequest request) {
        List<String> idList = ErpUtils.getList((String)requestId);
        PO po = null;
        SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        for (String reqId : idList) {
            po = this.poManager.getPO(reqId);
            po.setmApprovalStatus("approved");
            po.setApprovalStatus(po.getdApprovalStatus());
            po.setPmcStatus(po.getApprovalStatus());
            po.setdApproverId(sUser.getId());
            po.setdApprover(sUser.getName());
            this.poManager.updatePO(po);
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("po", po);
        return result;
    }

    @RequestMapping(value={"/listPo.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listPos(@RequestBody Search search) {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String status = search.getStatus();
        String supplierId = search.getSupplierId();
        POExample query = new POExample();
        POExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (ErpUtils.isNotEmpty((String)supplierId)) {
            criteria.andSupplierIdEqualTo(supplierId);
        }
        if (deliveryDateFrom != null) {
            criteria.andDeliveryDateGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andDeliveryDateLessThanOrEqualTo(deliveryDateTo);
        }
        if (ErpUtils.isNotEmpty((String)status)) {
            criteria.andPmcStatusEqualTo(status);
        }
        query.setOrderByClause("created_on desc, code desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List requests = this.poManager.listPOs(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.poManager.countPO(query);
        result.put("totalCount", count);
        result.put("objList", requests);
        return result;
    }

    @RequestMapping(value={"/deletePo.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object deletePO(@RequestParam String orderId, HttpServletRequest request) {
        PoItemExample example = new PoItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        this.poManager.deletePoItemByExample(example);
        this.poManager.deletePO(orderId);
        return orderId;
    }

    @RequestMapping(value={"/savePoReturn.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object savePoReturn(@RequestBody PoReturnDetail obj, HttpServletRequest request) {
        if (obj.getId() == null) {
            obj.setId(UUID.get());
            Sequence seq = this.orderManager.getSequence("SOR");
            obj.setCode(ErpUtils.generateRefNo((Sequence)seq));
            SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
            obj.setCreatedBy(sUser.getId());
            obj.setCreator(sUser.getName());
            obj.setCreatedOn(new Date());
            obj.setApprovalStatus("pending");
            obj.setStatus("draft");
            obj.setPmcStatus("pending");
            this.orderManager.addPoReturn((PoReturn)obj);
            for (PoReturnItem item : obj.getObjItems()) {
                item.setId(UUID.get());
                item.setReturnId(obj.getId());
                this.orderManager.addPoReturnItem(item);
            }
        } else {
            throw new ActionException();
        }
        return obj.getId();
    }

    @RequestMapping(value={"/loadPoReturn.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadPoReturn(@RequestParam String returnId) {
        PoReturn checkoutReq = this.orderManager.getPoReturn(returnId);
        PoReturnItemExample example = new PoReturnItemExample();
        example.createCriteria().andReturnIdEqualTo(returnId);
        List<PoReturnItem> returnItems = this.orderManager.listPoReturnItems(example);
        HashMap itemMap = new HashMap();
        ArrayList itemIds = new ArrayList();
        returnItems.forEach(returnItem -> {
            HashMap<String, PoReturnItem> subMap = new HashMap<String, PoReturnItem>();
            subMap.put("objItem", returnItem);
            itemMap.put(returnItem.getItemId(), subMap);
            itemIds.add(returnItem.getItemId());
        }
        );
        ItemExample iQuery = new ItemExample();
        iQuery.createCriteria().andIdIn(itemIds);
        List<Item> items = this.itemManager.listItems(iQuery);
        items.forEach(item -> {
            ((Map)itemMap.get(item.getId())).put("item", item);
        }
        );
        LOG.info((Logger)LOGGER, itemMap);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemMap", itemMap);
        result.put("obj", (Object)checkoutReq);
        return result;
    }

    @RequestMapping(value={"/listPoReturn.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listPoReturn(@RequestBody Search search) {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String status = search.getStatus();
        PoReturnExample query = new PoReturnExample();
        PoReturnExample.Criteria criteria = query.createCriteria();
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
        query.setOrderByClause("created_on desc, code desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List requests = this.orderManager.listPoReturns(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.orderManager.countPoReturn(query);
        result.put("totalCount", count);
        result.put("objList", requests);
        return result;
    }

    @RequestMapping(value={"/approvePoReturn.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object approvePoReturn(@RequestParam String returnId, @RequestParam String approveType, HttpServletRequest request) {
        List<String> ids = ErpUtils.getList((String)returnId);
        for (String reqId : ids) {
            PoReturn req = this.orderManager.getPoReturn(reqId);
            req.setdApprovalStatus("approved");
            req.setApprovalStatus("approved");
            req.setPmcStatus("approved");
            this.orderManager.updatePoReturn(req);
        }
        return returnId;
    }

    @RequestMapping(value={"/exportPoList.do"}, method={RequestMethod.GET})
    public ModelAndView exportPoList(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String status) {
        Date deliveryDateFrom = ErpUtils.isNotEmpty((String)dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty((String)dateTo) ? new Date(dateTo) : null;
        POExample query = new POExample();
        POExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (deliveryDateFrom != null) {
            criteria.andDeliveryDateGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andDeliveryDateLessThanOrEqualTo(deliveryDateTo);
        }
        if (ErpUtils.isNotEmpty((String)status)) {
            criteria.andPmcStatusEqualTo(status);
        }
        query.setOrderByClause("created_on desc, code desc");
        List poList = this.poManager.listPOs(query);
        PoExcelModel viewModel = new PoExcelModel(poList);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }

    @RequestMapping(value={"/exportPoReturn.do"}, method={RequestMethod.GET})
    public ModelAndView exportPoReturn(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String status) {
        Date deliveryDateFrom = ErpUtils.isNotEmpty((String)dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty((String)dateTo) ? new Date(dateTo) : null;
        PoReturnExample query = new PoReturnExample();
        PoReturnExample.Criteria criteria = query.createCriteria();
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
        query.setOrderByClause("created_on desc, code desc");
        List requests = this.orderManager.listPoReturns(query);
        PoReturnExcelModel viewModel = new PoReturnExcelModel(requests);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }

    @RequestMapping(value={"/exportOrderReq.do"}, method={RequestMethod.GET})
    public ModelAndView exportOrderReq(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String status) {
        Date deliveryDateFrom = ErpUtils.isNotEmpty((String)dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty((String)dateTo) ? new Date(dateTo) : null;
        OrderRequestExample query = new OrderRequestExample();
        OrderRequestExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (deliveryDateFrom != null) {
            criteria.andDeliveryDateGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andDeliveryDateLessThanOrEqualTo(deliveryDateTo);
        }
        if (ErpUtils.isNotEmpty((String)status)) {
            criteria.andPmcStatusEqualTo(status);
        }
        query.setOrderByClause("CODE desc");
        List requests = this.poManager.listOrderRequests(query);
        OrderReqExcelModel viewModel = new OrderReqExcelModel(requests);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }
}
