package com.wecloud.erp.controller;

import com.wecloud.erp.business.CalculateModel;
import com.wecloud.erp.entity.SODetail;
import com.wecloud.erp.entity.Search;
import com.wecloud.erp.entity.SessionUser;
import com.wecloud.erp.model.Customer;
import com.wecloud.erp.model.CustomerExample;
import com.wecloud.erp.model.SalesOrder;
import com.wecloud.erp.model.SalesOrderExample;
import com.wecloud.erp.model.Sequence;
import com.wecloud.erp.model.SoItem;
import com.wecloud.erp.model.SoItemExample;
import com.wecloud.erp.report.impl.SoExcelModel;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.OrderManager;
import com.wecloud.erp.utils.ErpUtils;
import com.wecloud.erp.utils.LOG;
import com.wecloud.erp.utils.UUID;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

@Controller
public class OrderAction {
    @Resource
    private ItemManager itemManager;
    @Resource
    private OrderManager orderManager;
    private static final Logger LOGGER = Logger.getLogger((Class)OrderAction.class);

    @RequestMapping(value={"/saveSalesOrder.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object saveSalesOrder(@RequestBody SODetail soDetail, HttpServletRequest request) {
        CalculateModel.calculate((SODetail)soDetail);
        SalesOrder order = soDetail.getObj();
        String orderId = order.getId();
        if (orderId == null) {
            order.setId(UUID.get());
            Sequence seq = this.orderManager.getSequence("SO");
            String orderNo = ErpUtils.generateRefNo((Sequence)seq);
            order.setCode(orderNo);
            order.setCreatedOn(new Date());
            SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
            order.setCreatedBy(sUser.getId());
            order.setCreator(sUser.getName());
            this.orderManager.addSalesOrder(order);
        } else {
            this.orderManager.updateSalesOrder(order);
        }
        if (orderId != null) {
            SoItemExample query = new SoItemExample();
            query.createCriteria().andOrderIdEqualTo(orderId);
            this.orderManager.deleteSoItem(query);
            LOG.debug((Logger)LOGGER, (Object)"remove so items.");
        }
        List<SoItem> items = soDetail.getItems();
        items.forEach(soItem -> {
            if (soItem.getId() == null) {
                soItem.setId(UUID.get());
                soItem.setOrderId(order.getId());
            }
            LOG.debug((Logger)LOGGER, (Object)("add so item." + soItem.getId()));
            this.orderManager.addSoItem(soItem);
        }
        );
        return soDetail;
    }

    @RequestMapping(value={"/dApproveSalesOrder.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object dApproveSalesOrder(@RequestBody SalesOrder order, HttpServletRequest request) {
        SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        order.setdApprovalStatus("approved");
        order.setApprovalStatus(order.getmApprovalStatus());
        order.setdApproverId(sUser.getId());
        order.setdApprover(sUser.getName());
        this.setPmcStatus(order);
        this.orderManager.updateSalesOrder(order);
        return order;
    }

    @RequestMapping(value={"/mApproveSalesOrder.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object mApproveSalesOrder(@RequestBody SalesOrder order, HttpServletRequest request) {
        SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        order.setmApprovalStatus("approved");
        order.setApprovalStatus(order.getdApprovalStatus());
        order.setmApproverId(sUser.getId());
        order.setmApprover(sUser.getName());
        this.setPmcStatus(order);
        this.orderManager.updateSalesOrder(order);
        return order;
    }

    @RequestMapping(value={"/approveSo.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Map<String, Object> approveSo(@RequestParam String requestId, @RequestParam String approveType, HttpServletRequest request) {
        List<String> idList = ErpUtils.getList((String)requestId);
        SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        for (String reqId : idList) {
            SalesOrder so = this.orderManager.getSalesOrder(reqId);
            if ("department".equals(approveType)) {
                so.setdApprovalStatus("approved");
                so.setApprovalStatus(so.getmApprovalStatus());
                so.setdApproverId(sUser.getId());
                so.setdApprover(sUser.getName());
            } else {
                so.setmApprovalStatus("approved");
                so.setApprovalStatus(so.getdApprovalStatus());
                so.setmApproverId(sUser.getId());
                so.setmApprover(sUser.getName());
            }
            this.setPmcStatus(so);
            this.orderManager.updateSalesOrder(so);
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("so", idList);
        return result;
    }

    private void setPmcStatus(SalesOrder order) {
        if ("approved".equals(order.getApprovalStatus())) {
            order.setPmcStatus("approved");
        }
    }

    @Transactional
    private void updateSoWithRecalculation(SalesOrder order) {
        List<SoItem> soItems = this.getSoItems(order.getId());
        if (ErpUtils.hasElement(soItems)) {
            double totalAmount = 0.0;
            double totalQty = 0.0;
            for (SoItem item : soItems) {
                totalAmount += item.getTotalAmount() == null ? 0.0 : item.getTotalAmount();
                totalQty += item.getQty() == null ? 0.0 : item.getQty();
            }
            order.setTotalAmount(Double.valueOf(totalAmount));
            order.setQty(Double.valueOf(totalQty));
            if (order.getTaxRate() != null && order.getTaxRate() > 0.0) {
                order.setNetAmount(Double.valueOf(totalAmount + totalAmount * order.getTaxRate() / 100.0));
            }
            this.orderManager.updateSalesOrder(order);
        }
    }

    @RequestMapping(value={"/loadSalesOrder.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadSalesOrder(@RequestParam String orderId) {
        SalesOrder order = this.orderManager.getSalesOrder(orderId);
        LOG.info((Logger)LOGGER, (Object)order);
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (order != null) {
            result.put("order", (Object)order);
            result.put("orderItems", this.getSoItems(orderId));
        }
        return result;
    }

    private List<SoItem> getSoItems(String soId) {
        SoItemExample query = new SoItemExample();
        query.createCriteria().andOrderIdEqualTo(soId);
        return this.orderManager.listSoItems(query);
    }

    @RequestMapping(value={"/deleteSalesOrder.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Map<String, Object> deleteSalesOrder(@RequestParam String orderId) {
        SoItemExample query = new SoItemExample();
        query.createCriteria().andOrderIdEqualTo(orderId);
        this.orderManager.deleteSoItem(query);
        this.orderManager.deleteSalesOrder(orderId);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("orderId", orderId);
        return result;
    }

    @RequestMapping(value={"/listSalesOrders.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listSalesOrders(@RequestBody Search search) {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String buyerId = search.getBuyerId();
        String status = search.getStatus();
        SalesOrderExample query = new SalesOrderExample();
        SalesOrderExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (ErpUtils.isNotEmpty((String)buyerId)) {
            criteria.andBuyerIdEqualTo(buyerId);
        }
        if (deliveryDateFrom != null) {
            criteria.andDeliveryDateGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andDeliveryDateLessThanOrEqualTo(deliveryDateTo);
        }
        if (ErpUtils.isNotEmpty((String)status)) {
            if (status.startsWith("approval-")) {
                String approvalStatus = status.split("-")[1];
                criteria.andApprovalStatusEqualTo(approvalStatus);
            } else if (status.startsWith("pmc-")) {
                String pmcStatus = status.split("-")[1];
                List statusList = ErpUtils.getList((String)pmcStatus);
                criteria.andPmcStatusIn(statusList);
            } else if (status.equals("draft")) {
                criteria.andStatusEqualTo(status);
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
        List orders = this.orderManager.listSalesOrders(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.orderManager.countSalesOrder(query);
        result.put("totalCount", count);
        result.put("orders", orders);
        return result;
    }

    @RequestMapping(value={"/exportSoList.do"}, method={RequestMethod.GET})
    public ModelAndView exportSoList(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String status, @RequestParam String buyerId) {
        Date deliveryDateFrom = ErpUtils.isNotEmpty((String)dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty((String)dateTo) ? new Date(dateTo) : null;
        SalesOrderExample query = new SalesOrderExample();
        SalesOrderExample.Criteria criteria = query.createCriteria();
        if (ErpUtils.isNotEmpty((String)orderNo)) {
            criteria.andCodeLike("%" + orderNo + "%");
        }
        if (ErpUtils.isNotEmpty((String)buyerId)) {
            criteria.andBuyerIdEqualTo(buyerId);
        }
        if (deliveryDateFrom != null) {
            criteria.andDeliveryDateGreaterThanOrEqualTo(deliveryDateFrom);
        }
        if (deliveryDateTo != null) {
            criteria.andDeliveryDateLessThanOrEqualTo(deliveryDateTo);
        }
        if (ErpUtils.isNotEmpty((String)status)) {
            if (status.startsWith("approval-")) {
                String approvalStatus = status.split("-")[1];
                criteria.andApprovalStatusEqualTo(approvalStatus);
            } else if (status.startsWith("pmc-")) {
                String pmcStatus = status.split("-")[1];
                criteria.andPmcStatusEqualTo(pmcStatus);
            } else if (status.equals("draft")) {
                criteria.andStatusEqualTo(status);
            }
        }
        query.setOrderByClause("CODE desc");
        query.setOrderByClause("created_on desc, code desc");
        List<SalesOrder> soList = this.orderManager.listSalesOrders(query);
        List<Customer> customers = this.itemManager.listCustomers(null);
        HashMap custMap = new HashMap();
        customers.forEach(cust -> {
            custMap.put(cust.getId(), cust.getName());
        }
        );
        if (soList != null) {
            soList.forEach(so -> {
                so.setBuyerId((String)custMap.get(so.getBuyerId()));
            }
            );
        }
        SoExcelModel viewModel = new SoExcelModel(soList);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }
}
