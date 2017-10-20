package com.wecloud.erp.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.wecloud.erp.entity.Search;
import com.wecloud.erp.model.ItemSummary;
import com.wecloud.erp.model.PO;
import com.wecloud.erp.model.POExample;
import com.wecloud.erp.model.PoSummary;
import com.wecloud.erp.model.PoSummaryExample;
import com.wecloud.erp.model.SalesOrder;
import com.wecloud.erp.model.SalesOrderExample;
import com.wecloud.erp.model.SoSummary;
import com.wecloud.erp.model.SoSummaryExample;
import com.wecloud.erp.report.impl.PoSummaryExcelModel;
import com.wecloud.erp.report.impl.SoSummaryExcelModel;
import com.wecloud.erp.service.OrderManager;
import com.wecloud.erp.service.PoManager;
import com.wecloud.erp.service.ViewManager;
import com.wecloud.erp.utils.ErpUtils;

// Referenced classes of package com.wecloud.erp.controller:
//            ViewController

@Controller
public class SummaryController
{

    public SummaryController()
    {
        LOGGER = Logger.getLogger(SummaryController.class);
    }

    @RequestMapping(value={"/listFinancialSummary.do"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Map listFinancialSummary()
    {
        ItemSummary itemSummary = viewManager.getItemSummary();
        Date current = new Date();
        Date dateFrom = new Date(current.getYear(), current.getMonth(), 1);
        POExample poquery = new POExample();
        poquery.createCriteria().andCreatedOnGreaterThanOrEqualTo(dateFrom);
        List pos = poManager.listPOs(poquery);
        double poAmount = 0.0D;
        if(pos != null)
        {
            for(Iterator iterator = pos.iterator(); iterator.hasNext();)
            {
                PO po = (PO)iterator.next();
                poAmount += po.getNetAmount().doubleValue();
            }

        }
        SalesOrderExample soquery = new SalesOrderExample();
        soquery.createCriteria().andCreatedOnGreaterThan(dateFrom);
        List sos = orderManager.listSalesOrders(soquery);
        double soAmount = 0.0D;
        if(sos != null)
        {
            for(Iterator iterator1 = sos.iterator(); iterator1.hasNext();)
            {
                SalesOrder so = (SalesOrder)iterator1.next();
                soAmount += so.getNetAmount().doubleValue();
            }

        }
        Map result = new HashMap();
        result.put("itemSummary", itemSummary);
        result.put("poSummary", Double.valueOf(poAmount));
        result.put("soSummary", Double.valueOf(soAmount));
        return result;
    }

    @RequestMapping(value={"/listPoSummary.do"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Map listPoSummary(@RequestBody Search search)
    {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String supplierId = search.getSupplierId();
        String itemType = search.getItemType();
        PoSummaryExample query = new PoSummaryExample();
        com.wecloud.erp.model.PoSummaryExample.Criteria criteria = query.createCriteria();
        if(ErpUtils.isNotEmpty(orderNo))
            criteria.andCodeLike((new StringBuilder("%")).append(orderNo).append("%").toString());
        if(deliveryDateFrom != null)
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        if(deliveryDateTo != null)
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        if(ErpUtils.isNotEmpty(supplierId))
            criteria.andSupplierIdEqualTo(supplierId);
        if(ErpUtils.isNotEmpty(itemType))
            criteria.andTypeEqualTo(itemType);
        List records = viewManager.listPoSummarys(query);
        Map summaryMap = new HashMap();
        Double totalQty = Double.valueOf(0.0D);
        Double totalAmount = Double.valueOf(0.0D);
        if(records != null)
        {
            for(Iterator iterator = records.iterator(); iterator.hasNext();)
            {
                PoSummary rec = (PoSummary)iterator.next();
                String key = (new StringBuilder(String.valueOf(rec.getCode()))).append(rec.getName()).append(rec.getStyle()).append(rec.getType()).append(rec.getUom()).append(rec.getUnitPrice()).toString();
                if(summaryMap.containsKey(key))
                {
                    PoSummary existObj = (PoSummary)summaryMap.get(key);
                    existObj.setAmount(Double.valueOf(existObj.getAmount().doubleValue() + rec.getAmount().doubleValue()));
                    existObj.setQty(Double.valueOf(existObj.getQty().doubleValue() + rec.getQty().doubleValue()));
                } else
                {
                    summaryMap.put(key, rec);
                }
                totalQty = Double.valueOf(totalQty.doubleValue() + rec.getQty().doubleValue());
                totalAmount = Double.valueOf(totalAmount.doubleValue() + rec.getAmount().doubleValue());
            }

        }
        Map result = new HashMap();
        result.put("objList", summaryMap.values());
        result.put("totalQty", totalQty);
        result.put("totalAmount", totalAmount);
        return result;
    }

    @RequestMapping(value={"/listSoSummary.do"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Map listSoSummary(@RequestBody Search search)
    {
        String orderNo = search.getOrderNo();
        Date deliveryDateFrom = search.getDateFrom();
        Date deliveryDateTo = search.getDateTo();
        String buyerId = search.getBuyerId();
        String itemType = search.getItemType();
        SoSummaryExample query = new SoSummaryExample();
        com.wecloud.erp.model.SoSummaryExample.Criteria criteria = query.createCriteria();
        if(ErpUtils.isNotEmpty(orderNo))
            criteria.andCodeLike((new StringBuilder("%")).append(orderNo).append("%").toString());
        if(deliveryDateFrom != null)
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        if(deliveryDateTo != null)
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        if(ErpUtils.isNotEmpty(buyerId))
            criteria.andBuyerIdEqualTo(buyerId);
        if(ErpUtils.isNotEmpty(itemType))
            criteria.andTypeEqualTo(itemType);
        List records = viewManager.listSoSummarys(query);
        Map summaryMap = new HashMap();
        Double totalQty = Double.valueOf(0.0D);
        Double totalAmount = Double.valueOf(0.0D);
        if(records != null)
        {
            for(Iterator iterator = records.iterator(); iterator.hasNext();)
            {
                SoSummary rec = (SoSummary)iterator.next();
                String key = (new StringBuilder(String.valueOf(rec.getCode()))).append(rec.getName()).append(rec.getStyle()).append(rec.getType()).append(rec.getUom()).append(rec.getUnitPrice()).toString();
                if(summaryMap.containsKey(key))
                {
                    SoSummary existObj = (SoSummary)summaryMap.get(key);
                    existObj.setAmount(Double.valueOf(existObj.getAmount().doubleValue() + rec.getAmount().doubleValue()));
                    existObj.setQty(Double.valueOf(existObj.getQty().doubleValue() + rec.getQty().doubleValue()));
                } else
                {
                    summaryMap.put(key, rec);
                }
                totalQty = Double.valueOf(totalQty.doubleValue() + rec.getQty().doubleValue());
                totalAmount = Double.valueOf(totalAmount.doubleValue() + rec.getAmount().doubleValue());
            }

        }
        Map result = new HashMap();
        result.put("objList", summaryMap.values());
        result.put("totalQty", totalQty);
        result.put("totalAmount", totalAmount);
        return result;
    }

    @RequestMapping(value={"/exportSoSummary.do"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView exportSoSummary(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String buyerId, @RequestParam String itemType)
    {
        Date deliveryDateFrom = ErpUtils.isNotEmpty(dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty(dateTo) ? new Date(dateTo) : null;
        SoSummaryExample query = new SoSummaryExample();
        com.wecloud.erp.model.SoSummaryExample.Criteria criteria = query.createCriteria();
        if(ErpUtils.isNotEmpty(orderNo))
            criteria.andCodeLike((new StringBuilder("%")).append(orderNo).append("%").toString());
        if(deliveryDateFrom != null)
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        if(deliveryDateTo != null)
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        if(ErpUtils.isNotEmpty(buyerId))
            criteria.andBuyerIdEqualTo(buyerId);
        if(ErpUtils.isNotEmpty(itemType))
            criteria.andTypeEqualTo(itemType);
        List records = viewManager.listSoSummarys(query);
        Map summaryMap = new HashMap();
        Double totalQty = Double.valueOf(0.0D);
        Double totalAmount = Double.valueOf(0.0D);
        if(records != null)
        {
            for(Iterator iterator = records.iterator(); iterator.hasNext();)
            {
                SoSummary rec = (SoSummary)iterator.next();
                String key = (new StringBuilder(String.valueOf(rec.getCode()))).append(rec.getName()).append(rec.getStyle()).append(rec.getType()).append(rec.getUom()).append(rec.getUnitPrice()).toString();
                if(summaryMap.containsKey(key))
                {
                    SoSummary existObj = (SoSummary)summaryMap.get(key);
                    existObj.setAmount(Double.valueOf(existObj.getAmount().doubleValue() + rec.getAmount().doubleValue()));
                    existObj.setQty(Double.valueOf(existObj.getQty().doubleValue() + rec.getQty().doubleValue()));
                } else
                {
                    summaryMap.put(key, rec);
                }
                totalQty = Double.valueOf(totalQty.doubleValue() + rec.getQty().doubleValue());
                totalAmount = Double.valueOf(totalAmount.doubleValue() + rec.getAmount().doubleValue());
            }

        }
        SoSummaryExcelModel viewModel = new SoSummaryExcelModel(summaryMap.values());
        return new ModelAndView("excelReportView", "excelModel", viewModel);
    }

    @RequestMapping(value={"/exportPoSummary.do"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView exportPoSummary(@RequestParam String orderNo, @RequestParam String dateFrom, @RequestParam String dateTo, @RequestParam String supplierId, @RequestParam String itemType)
    {
        Date deliveryDateFrom = ErpUtils.isNotEmpty(dateFrom) ? new Date(dateFrom) : null;
        Date deliveryDateTo = ErpUtils.isNotEmpty(dateTo) ? new Date(dateTo) : null;
        PoSummaryExample query = new PoSummaryExample();
        com.wecloud.erp.model.PoSummaryExample.Criteria criteria = query.createCriteria();
        if(ErpUtils.isNotEmpty(orderNo))
            criteria.andCodeLike((new StringBuilder("%")).append(orderNo).append("%").toString());
        if(deliveryDateFrom != null)
            criteria.andCreatedOnGreaterThanOrEqualTo(deliveryDateFrom);
        if(deliveryDateTo != null)
            criteria.andCreatedOnLessThanOrEqualTo(deliveryDateTo);
        if(ErpUtils.isNotEmpty(supplierId))
            criteria.andSupplierIdEqualTo(supplierId);
        if(ErpUtils.isNotEmpty(itemType))
            criteria.andTypeEqualTo(itemType);
        List records = viewManager.listPoSummarys(query);
        Map summaryMap = new HashMap();
        Double totalQty = Double.valueOf(0.0D);
        Double totalAmount = Double.valueOf(0.0D);
        if(records != null)
        {
            for(Iterator iterator = records.iterator(); iterator.hasNext();)
            {
                PoSummary rec = (PoSummary)iterator.next();
                String key = (new StringBuilder(String.valueOf(rec.getCode()))).append(rec.getName()).append(rec.getStyle()).append(rec.getType()).append(rec.getUom()).append(rec.getUnitPrice()).toString();
                if(summaryMap.containsKey(key))
                {
                    PoSummary existObj = (PoSummary)summaryMap.get(key);
                    existObj.setAmount(Double.valueOf(existObj.getAmount().doubleValue() + rec.getAmount().doubleValue()));
                    existObj.setQty(Double.valueOf(existObj.getQty().doubleValue() + rec.getQty().doubleValue()));
                } else
                {
                    summaryMap.put(key, rec);
                }
                totalQty = Double.valueOf(totalQty.doubleValue() + rec.getQty().doubleValue());
                totalAmount = Double.valueOf(totalAmount.doubleValue() + rec.getAmount().doubleValue());
            }

        }
        PoSummaryExcelModel viewModel = new PoSummaryExcelModel(summaryMap.values());
        return new ModelAndView("excelReportView", "excelModel", viewModel);
    }

    @Resource
    private ViewManager viewManager;
    @Resource
    private OrderManager orderManager;
    @Resource
    private PoManager poManager;
    private Logger LOGGER;
}
