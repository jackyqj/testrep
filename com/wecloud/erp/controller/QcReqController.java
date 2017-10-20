package com.wecloud.erp.controller;

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
import com.wecloud.erp.entity.QcRecDetail;
import com.wecloud.erp.entity.QcReqDetail;
import com.wecloud.erp.entity.Search;
import com.wecloud.erp.entity.SessionUser;
import com.wecloud.erp.model.OrderRequest;
import com.wecloud.erp.model.PO;
import com.wecloud.erp.model.QcRecord;
import com.wecloud.erp.model.QcRecordExample;
import com.wecloud.erp.model.QcRecordItem;
import com.wecloud.erp.model.QcRecordItemExample;
import com.wecloud.erp.model.QcRequest;
import com.wecloud.erp.model.QcRequestExample;
import com.wecloud.erp.model.QcRequestItem;
import com.wecloud.erp.model.QcRequestItemExample;
import com.wecloud.erp.model.Sequence;
import com.wecloud.erp.report.impl.QcRecordExcelModel;
import com.wecloud.erp.report.impl.QcRequestExcelModel;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.OrderManager;
import com.wecloud.erp.service.PoManager;
import com.wecloud.erp.utils.ErpUtils;
import com.wecloud.erp.utils.UUID;
import com.wecloud.erp.web.exception.ActionException;

@Controller
public class QcReqController {
    @Resource
    private PoManager poManager;
    @Resource
    private OrderManager orderManager;
    @Resource
    private ItemManager itemManager;
    private static final Logger LOGGER = Logger.getLogger(QcReqController.class);

    @RequestMapping(value={"/saveQcReq.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object saveQcReq(@RequestBody QcReqDetail object, HttpServletRequest request) {
        QcRequest obj = object.getObj();
        SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        if (obj.getId() == null) {
            obj.setId(UUID.get());
            Sequence seq = this.orderManager.getSequence("QCREQ");
            obj.setCode(ErpUtils.generateRefNo((Sequence)seq));
            obj.setCreatedBy(sUser.getId());
            if (obj.getSupplierId() != null && obj.getSupplierName() == null) {
                obj.setSupplierName(this.itemManager.getCustomer(obj.getSupplierId()).getName());
            }
            obj.setCreator(sUser.getName());
            obj.setCreatedOn(new Date());
            CalculateModel.calculateQc((QcReqDetail)object);
            this.poManager.addQcRequest(obj);
            PO order = this.poManager.getPO(obj.getOrderId());
            if (order != null) {
                order.setPmcStatus("processing");
                this.poManager.updatePO(order);
                OrderRequest ordReq = this.poManager.getOrderRequest(order.getRequestId());
                if (ordReq != null) {
                    ordReq.setPmcStatus("processing");
                    this.poManager.updateOrderRequest(ordReq);
                }
            }
            for (QcRequestItem item : object.getItems()) {
                item.setId(UUID.get());
                item.setRequestId(obj.getId());
                this.poManager.addQcRequestItem(item);
            }
        } else {
            throw new ActionException();
        }
        return object;
    }

    @RequestMapping(value={"/listQcReq.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listQcReq(@RequestBody Search search) {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String status = search.getStatus();
        QcRequestExample query = new QcRequestExample();
        QcRequestExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (ErpUtils.isNotEmpty((String)status)) {
            criteria.andQcStatusEqualTo(status);
        }
        if (deliveryDateFrom != null) {
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        }
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setOrderByClause("CODE desc");
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List records = this.poManager.listQcRequests(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.poManager.countQcRequest(query);
        result.put("totalCount", count);
        result.put("objList", records);
        return result;
    }

    @RequestMapping(value={"/loadQcReq.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadQcReq(@RequestParam String objectId) {
        QcRequest qcReq = this.poManager.getQcRequest(objectId);
        QcRequestItemExample example = new QcRequestItemExample();
        example.createCriteria().andRequestIdEqualTo(objectId);
        List reqItems = this.poManager.listQcRequestItems(example);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("obj", (Object)qcReq);
        result.put("items", reqItems);
        return result;
    }

    @RequestMapping(value={"/saveQcRec.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object saveQcRec(@RequestBody QcRecDetail object, HttpServletRequest request) {
        QcRecord obj = object.getObj();
        SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        if (obj.getId() == null) {
            obj.setId(UUID.get());
            Sequence seq = this.orderManager.getSequence("QCREC");
            obj.setCode(ErpUtils.generateRefNo((Sequence)seq));
            if (obj.getSupplierId() != null && obj.getSupplierName() == null) {
                obj.setSupplierName(this.itemManager.getCustomer(obj.getSupplierId()).getName());
            }
            obj.setCreatedBy(sUser.getId());
            obj.setCreator(sUser.getName());
            obj.setCreatedOn(new Date());
            this.poManager.addQcRecord(obj);
            QcRequest req = this.poManager.getQcRequest(obj.getRequestId());
            req.setQcStatus(obj.getQcStatus());
            req.setPassedQty(obj.getPassedQty());
            req.setFailedQty(obj.getFailedQty());
            this.poManager.updateQcRequest(req);
            PO order = this.poManager.getPO(req.getOrderId());
            if (order != null) {
                order.setPmcStatus("processing");
                this.poManager.updatePO(order);
                OrderRequest ordReq = this.poManager.getOrderRequest(order.getRequestId());
                if (ordReq != null) {
                    ordReq.setPmcStatus("processing");
                    this.poManager.updateOrderRequest(ordReq);
                }
            }
            QcRequestItemExample example = new QcRequestItemExample();
            example.createCriteria().andRequestIdEqualTo(req.getId());
            List<QcRequestItem> qcreqItems = this.poManager.listQcRequestItems(example);
            HashMap qcreqItemMap = new HashMap();
            if (qcreqItems != null) {
                qcreqItems.forEach(qcreqItem -> {
                    qcreqItemMap.put(qcreqItem.getItemId(), qcreqItem);
                }
                );
            }
            for (QcRecordItem item : object.getItems()) {
                item.setId(UUID.get());
                item.setRecordId(obj.getId());
                QcRequestItem tItem = (QcRequestItem)qcreqItemMap.get(item.getItemId());
                if (tItem != null) {
                    tItem.setPassedQty(item.getPassedQty());
                    tItem.setFailedQty(item.getFailedQty());
                    this.poManager.updateQcRequestItem(tItem);
                }
                this.poManager.addQcRecordItem(item);
            }
        } else {
            throw new ActionException();
        }
        return object;
    }

    @RequestMapping(value={"/listQcRec.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listQcRec(@RequestBody Search search) {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String status = search.getStatus();
        QcRecordExample query = new QcRecordExample();
        QcRecordExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (ErpUtils.isNotEmpty((String)status)) {
            List statusList = ErpUtils.getList((String)status);
            criteria.andQcStatusIn(statusList);
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
        List records = this.poManager.listQcRecords(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.poManager.countQcRecord(query);
        result.put("totalCount", count);
        result.put("objList", records);
        return result;
    }

    @RequestMapping(value={"/loadQcRec.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadQcRec(@RequestParam String objectId) {
        QcRecord qcRec = this.poManager.getQcRecord(objectId);
        QcRecordItemExample example = new QcRecordItemExample();
        example.createCriteria().andRecordIdEqualTo(objectId);
        List recItems = this.poManager.listQcRecordItems(example);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("obj", (Object)qcRec);
        result.put("items", recItems);
        return result;
    }

    @RequestMapping(value={"/exportQcReq.do"}, method={RequestMethod.GET})
    public ModelAndView exportQcReq(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String status) {
        Date deliveryDateFrom = ErpUtils.isNotEmpty((String)dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty((String)dateTo) ? new Date(dateTo) : null;
        QcRequestExample query = new QcRequestExample();
        QcRequestExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (ErpUtils.isNotEmpty((String)status)) {
            criteria.andQcStatusEqualTo(status);
        }
        if (deliveryDateFrom != null) {
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        }
        query.setOrderByClause("CODE desc");
        List records = this.poManager.listQcRequests(query);
        QcRequestExcelModel viewModel = new QcRequestExcelModel(records);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }

    @RequestMapping(value={"/exportQcRec.do"}, method={RequestMethod.GET})
    public ModelAndView exportQcRec(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String status) {
        Date deliveryDateFrom = ErpUtils.isNotEmpty((String)dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty((String)dateTo) ? new Date(dateTo) : null;
        QcRecordExample query = new QcRecordExample();
        QcRecordExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (ErpUtils.isNotEmpty((String)status)) {
            List statusList = ErpUtils.getList((String)status);
            criteria.andQcStatusIn(statusList);
        }
        if (deliveryDateFrom != null) {
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        }
        query.setOrderByClause("CODE desc");
        List records = this.poManager.listQcRecords(query);
        QcRecordExcelModel viewModel = new QcRecordExcelModel(records);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }
}
