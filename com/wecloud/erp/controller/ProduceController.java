package com.wecloud.erp.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.wecloud.erp.entity.Search;
import com.wecloud.erp.entity.SessionUser;
import com.wecloud.erp.model.ProduceNotice;
import com.wecloud.erp.model.ProduceNoticeExample;
import com.wecloud.erp.model.ProduceRequestViewExample;
import com.wecloud.erp.model.SalesOrder;
import com.wecloud.erp.model.Sequence;
import com.wecloud.erp.report.impl.PrdNoticeExcelModel;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.OrderManager;
import com.wecloud.erp.service.ViewManager;
import com.wecloud.erp.utils.ErpUtils;
import com.wecloud.erp.utils.LOG;
import com.wecloud.erp.utils.UUID;

@Controller
public class ProduceController
{

    public ProduceController()
    {
    }

    @RequestMapping(value={"/saveProduceNotice.do"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object saveProduceNotice(@RequestBody ProduceNotice notice, HttpServletRequest request)
    {
        if(notice.getId() == null)
        {
            notice.setId(UUID.get());
            Sequence seq = orderManager.getSequence("PRDN");
            notice.setCode(ErpUtils.generateRefNo(seq));
            SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
            notice.setCreatedBy(sUser.getId());
            notice.setCreator(sUser.getName());
            notice.setCreatedOn(new Date());
            if(notice.getStatus() == null)
                notice.setStatus("draft");
            LOG.info(LOGGER, (new StringBuilder("Insert new notice.")).append(notice.getId()).toString());
            orderManager.addProduceNotice(notice);
        } else
        {
            LOG.info(LOGGER, (new StringBuilder("Update notice.")).append(notice.getId()).toString());
            orderManager.updateProduceNotice(notice);
        }
        return notice;
    }

    @RequestMapping(value={"/approveProduceNotice.do"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object approveProduceNotice(@RequestParam String noticeId, @RequestParam String approveType, @RequestParam String approvalReason, HttpServletRequest request)
    {
        List ids = ErpUtils.getList(noticeId);
        for(Iterator iterator = ids.iterator(); iterator.hasNext();)
        {
            String tId = (String)iterator.next();
            ProduceNotice notice = orderManager.getProduceNotice(tId);
            if("approved".equals(approveType))
            {
                notice.setStatus("approved");
                notice.setPmcStatus("approved");
                String soId = notice.getOrderId();
                SalesOrder so = orderManager.getSalesOrder(soId);
                so.setPmcStatus("processing");
                orderManager.updateProduceNotice(notice);
                orderManager.updateSalesOrder(so);
            } else
            {
                notice.setStatus("draft");
                notice.setDescription((new StringBuilder(String.valueOf(notice.getDescription()))).append("\u3010\u9A73\u56DE\u3011").append(approvalReason).toString());
                notice.setPmcStatus(null);
                orderManager.updateProduceNotice(notice);
            }
        }

        return noticeId;
    }

    @RequestMapping(value={"/loadProduceNotice.do"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Map loadProduceNotice(@RequestParam String noticeId)
    {
        ProduceNotice prdNotice = orderManager.getProduceNotice(noticeId);
        SalesOrder so = orderManager.getSalesOrder(prdNotice.getOrderId());
        ProduceRequestViewExample pre = new ProduceRequestViewExample();
        pre.createCriteria().andSoIdEqualTo(prdNotice.getOrderId());
        List prdRequests = orderManager.listProduceRequests(pre);
        Map result = new HashMap();
        result.put("notice", prdNotice);
        result.put("order", so);
        result.put("items", prdRequests);
        return result;
    }

    @RequestMapping(value={"/listProduceNotices.do"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Map listProduceNotices(@RequestBody Search search)
    {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String status = search.getStatus();
        ProduceNoticeExample query = new ProduceNoticeExample();
        com.wecloud.erp.model.ProduceNoticeExample.Criteria criteria = query.createCriteria();
        if(ErpUtils.isNotEmpty(orderNo))
            criteria.andCodeLike((new StringBuilder("%")).append(orderNo).append("%").toString());
        if(deliveryDateFrom != null)
            criteria.andDeliveryDateGreaterThanOrEqualTo(deliveryDateFrom);
        if(deliveryDateTo != null)
            criteria.andDeliveryDateLessThanOrEqualTo(deliveryDateTo);
        if(ErpUtils.isNotEmpty(status))
            if(status.startsWith("pmc-"))
            {
                String pmcStatus = status.split("-")[1];
                List statusList = ErpUtils.getList(pmcStatus);
                criteria.andPmcStatusIn(statusList);
            } else
            {
                criteria.andStatusEqualTo(status);
            }
        query.setOrderByClause("created_on desc");
        String limitStart = (String)StringUtils.defaultIfEmpty(search.getLimitStart(), "0");
        String limitEnd = search.getLimitEnd();
        if(limitEnd == null)
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List notices = orderManager.listProduceNotices(query);
        Map result = new HashMap();
        int count = orderManager.countProduceNotice(query);
        result.put("totalCount", Integer.valueOf(count));
        result.put("objectList", notices);
        return result;
    }

    @RequestMapping(value={"/exportProduceNotices.do"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView exportProduceNotices(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String status)
    {
        Date deliveryDateFrom = ErpUtils.isNotEmpty(dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty(dateTo) ? new Date(dateTo) : null;
        ProduceNoticeExample query = new ProduceNoticeExample();
        com.wecloud.erp.model.ProduceNoticeExample.Criteria criteria = query.createCriteria();
        if(ErpUtils.isNotEmpty(orderNo))
            criteria.andCodeLike((new StringBuilder("%")).append(orderNo).append("%").toString());
        if(deliveryDateFrom != null)
            criteria.andDeliveryDateGreaterThanOrEqualTo(deliveryDateFrom);
        if(deliveryDateTo != null)
            criteria.andDeliveryDateLessThanOrEqualTo(deliveryDateTo);
        if(ErpUtils.isNotEmpty(status))
            if(status.startsWith("pmc-"))
            {
                String pmcStatus = status.split("-")[1];
                List statusList = ErpUtils.getList(pmcStatus);
                criteria.andPmcStatusIn(statusList);
            } else
            {
                criteria.andStatusEqualTo(status);
            }
        query.setOrderByClause("created_on desc, req_code desc");
        List notices = orderManager.listProduceNotices(query);
        PrdNoticeExcelModel viewModel = new PrdNoticeExcelModel(notices);
        return new ModelAndView("excelReportView", "excelModel", viewModel);
    }

    @Resource
    private OrderManager orderManager;
    @Resource
    private ItemManager itemManager;
    @Resource
    private ViewManager viewManager;
    private static final Logger LOGGER = Logger.getLogger(ProduceController.class);

}
