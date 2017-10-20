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

import com.wecloud.erp.entity.DeliveryRequestDetail;
import com.wecloud.erp.entity.Search;
import com.wecloud.erp.entity.SessionUser;
import com.wecloud.erp.entity.SoReturnDetail;
import com.wecloud.erp.model.DeliveryRequest;
import com.wecloud.erp.model.DeliveryRequestExample;
import com.wecloud.erp.model.DeliveryRequestItem;
import com.wecloud.erp.model.DeliveryRequestItemExample;
import com.wecloud.erp.model.Item;
import com.wecloud.erp.model.ItemExample;
import com.wecloud.erp.model.Sequence;
import com.wecloud.erp.model.SoReturn;
import com.wecloud.erp.model.SoReturnExample;
import com.wecloud.erp.model.SoReturnItem;
import com.wecloud.erp.model.SoReturnItemExample;
import com.wecloud.erp.report.impl.DeliveryReqExcelModel;
import com.wecloud.erp.report.impl.SoReturnExcelModel;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.OrderManager;
import com.wecloud.erp.service.PoManager;
import com.wecloud.erp.utils.ErpUtils;
import com.wecloud.erp.utils.LOG;
import com.wecloud.erp.utils.UUID;
import com.wecloud.erp.web.exception.ActionException;

@Controller
public class SalesController {
    @Resource
    private OrderManager orderManager;
    @Resource
    private PoManager poManager;
    @Resource
    private ItemManager itemManager;
    private static final Logger LOGGER = Logger.getLogger((Class)OrderAction.class);

    @RequestMapping(value={"/saveDeliveryRequest.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object saveDeliveryRequest(@RequestBody DeliveryRequestDetail obj, HttpServletRequest request) {
        if (obj.getId() == null) {
            obj.setId(UUID.get());
            Sequence seq = this.orderManager.getSequence("DREQ");
            obj.setCode(ErpUtils.generateRefNo((Sequence)seq));
            SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
            obj.setCreatedBy(sUser.getId());
            obj.setCreator(sUser.getName());
            obj.setCreatedOn(new Date());
            obj.setApprovalStatus("pending");
            obj.setStatus("final");
            obj.setPmcStatus("pending");
            this.poManager.addDeliveryRequest((DeliveryRequest)obj);
            for (DeliveryRequestItem item : obj.getObjItems()) {
                item.setId(UUID.get());
                item.setRequestId(obj.getId());
                this.poManager.addDeliveryRequestItem(item);
            }
        } else {
            throw new ActionException();
        }
        return obj.getId();
    }

    @RequestMapping(value={"/loadDeliveryRequest.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadDeliveryRequest(@RequestParam String requestId) {
        DeliveryRequest checkoutReq = this.poManager.getDeliveryRequest(requestId);
        DeliveryRequestItemExample example = new DeliveryRequestItemExample();
        example.createCriteria().andRequestIdEqualTo(requestId);
        List<DeliveryRequestItem> reqItems = this.poManager.listDeliveryRequestItems(example);
        HashMap itemMap = new HashMap();
        ArrayList itemIds = new ArrayList();
        reqItems.forEach(reqItem -> {
            HashMap<String, DeliveryRequestItem> subMap = new HashMap<String, DeliveryRequestItem>();
            subMap.put("objItem", reqItem);
            itemMap.put(reqItem.getItemId(), subMap);
            itemIds.add(reqItem.getItemId());
        }
        );
        ItemExample iQuery = new ItemExample();
        iQuery.createCriteria().andIdIn(itemIds);
        List<Item> items = this.itemManager.listItems(iQuery);
        items.forEach(item -> {
            ((Map)itemMap.get(item.getId())).put("item", item);
        }
        );
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemMap", itemMap);
        result.put("obj", (Object)checkoutReq);
        return result;
    }

    @RequestMapping(value={"/listDeliveryRequest.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listDeliveryRequest(@RequestBody Search search) {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String status = search.getStatus();
        DeliveryRequestExample query = new DeliveryRequestExample();
        DeliveryRequestExample.Criteria criteria = query.createCriteria();
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
            if (status.startsWith("approval-")) {
                String approvalStatus = status.split("-")[1];
                criteria.andApprovalStatusEqualTo(approvalStatus);
            } else if (status.startsWith("pmc-")) {
                String pmcStatus = status.split("-")[1];
                criteria.andPmcStatusEqualTo(pmcStatus);
            }
        }
        query.setOrderByClause("CODE desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List<DeliveryRequest> requests = this.poManager.listDeliveryRequests(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.poManager.countDeliveryRequest(query);
        result.put("totalCount", count);
        result.put("objList", requests);
        return result;
    }

    @RequestMapping(value={"/approveDeliveryRequest.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object approveDeliveryRequest(@RequestParam String requestId, @RequestParam String approveType, HttpServletRequest request) {
        List<String> ids = ErpUtils.getList((String)requestId);
        for (String reqId : ids) {
            DeliveryRequest req = this.poManager.getDeliveryRequest(reqId);
            req.setdApprovalStatus("approved");
            req.setApprovalStatus("approved");
            req.setPmcStatus("approved");
            this.poManager.updateDeliveryRequest(req);
        }
        return requestId;
    }

    @RequestMapping(value={"/saveSoReturn.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object saveSoReturn(@RequestBody SoReturnDetail obj, HttpServletRequest request) {
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
            this.orderManager.addSoReturn((SoReturn)obj);
            for (SoReturnItem item : obj.getObjItems()) {
                item.setId(UUID.get());
                item.setReturnId(obj.getId());
                this.orderManager.addSoReturnItem(item);
            }
        } else {
            throw new ActionException();
        }
        return obj.getId();
    }

    @RequestMapping(value={"/loadSoReturn.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadSoReturn(@RequestParam String returnId) {
        SoReturn checkoutReq = this.orderManager.getSoReturn(returnId);
        SoReturnItemExample example = new SoReturnItemExample();
        example.createCriteria().andReturnIdEqualTo(returnId);
        List<SoReturnItem> returnItems = this.orderManager.listSoReturnItems(example);
        HashMap itemMap = new HashMap();
        ArrayList itemIds = new ArrayList();
        returnItems.forEach(returnItem -> {
            HashMap<String, SoReturnItem> subMap = new HashMap<String, SoReturnItem>();
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

    @RequestMapping(value={"/listSoReturn.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listSoReturn(@RequestBody Search search) {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String status = search.getStatus();
        SoReturnExample query = new SoReturnExample();
        SoReturnExample.Criteria criteria = query.createCriteria();
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
            if (status.startsWith("approval-")) {
                String approvalStatus = status.split("-")[1];
                criteria.andApprovalStatusEqualTo(approvalStatus);
            } else if (status.startsWith("pmc-")) {
                String pmcStatus = status.split("-")[1];
                criteria.andPmcStatusEqualTo(pmcStatus);
            }
        }
        query.setOrderByClause("created_on desc, code desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List requests = this.orderManager.listSoReturns(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.orderManager.countSoReturn(query);
        result.put("totalCount", count);
        result.put("objList", requests);
        return result;
    }

    @RequestMapping(value={"/approveSoReturn.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object approveSoReturn(@RequestParam String returnId, @RequestParam String approveType, HttpServletRequest request) {
        List<String> ids = ErpUtils.getList((String)returnId);
        for (String reqId : ids) {
            SoReturn req = this.orderManager.getSoReturn(reqId);
            req.setdApprovalStatus("approved");
            req.setApprovalStatus("approved");
            req.setPmcStatus("approved");
            this.orderManager.updateSoReturn(req);
        }
        return returnId;
    }

    @RequestMapping(value={"/exportDeliveryRequest.do"}, method={RequestMethod.GET})
    public ModelAndView exportDeliveryRequest(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String status) {
        Date deliveryDateFrom = ErpUtils.isNotEmpty((String)dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty((String)dateTo) ? new Date(dateTo) : null;
        DeliveryRequestExample query = new DeliveryRequestExample();
        DeliveryRequestExample.Criteria criteria = query.createCriteria();
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
            if (status.startsWith("approval-")) {
                String approvalStatus = status.split("-")[1];
                criteria.andApprovalStatusEqualTo(approvalStatus);
            } else if (status.startsWith("pmc-")) {
                String pmcStatus = status.split("-")[1];
                criteria.andPmcStatusEqualTo(pmcStatus);
            }
        }
        query.setOrderByClause("CODE desc");
        List requests = this.poManager.listDeliveryRequests(query);
        HashMap result = new HashMap();
        DeliveryReqExcelModel viewModel = new DeliveryReqExcelModel(requests);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }

    @RequestMapping(value={"/exportSoReturn.do"}, method={RequestMethod.GET})
    public ModelAndView exportSoReturn(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String status) {
        Date deliveryDateFrom = ErpUtils.isNotEmpty((String)dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty((String)dateTo) ? new Date(dateTo) : null;
        SoReturnExample query = new SoReturnExample();
        SoReturnExample.Criteria criteria = query.createCriteria();
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
            if (status.startsWith("approval-")) {
                String approvalStatus = status.split("-")[1];
                criteria.andApprovalStatusEqualTo(approvalStatus);
            } else if (status.startsWith("pmc-")) {
                String pmcStatus = status.split("-")[1];
                criteria.andPmcStatusEqualTo(pmcStatus);
            }
        }
        query.setOrderByClause("created_on desc, code desc");
        List requests = this.orderManager.listSoReturns(query);
        SoReturnExcelModel viewModel = new SoReturnExcelModel(requests);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }
}
