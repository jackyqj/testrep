package com.wecloud.erp.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wecloud.erp.model.CheckinRecord;
import com.wecloud.erp.model.CheckinRecordItem;
import com.wecloud.erp.model.CheckinRecordItemExample;
import com.wecloud.erp.model.CheckoutRecord;
import com.wecloud.erp.model.CheckoutRecordItem;
import com.wecloud.erp.model.CheckoutRecordItemExample;
import com.wecloud.erp.model.CheckoutReqItem;
import com.wecloud.erp.model.CheckoutReqItemExample;
import com.wecloud.erp.model.CheckoutRequest;
import com.wecloud.erp.model.Customer;
import com.wecloud.erp.model.DeliveryRequest;
import com.wecloud.erp.model.DeliveryRequestItem;
import com.wecloud.erp.model.DeliveryRequestItemExample;
import com.wecloud.erp.model.Faulty;
import com.wecloud.erp.model.FaultyItem;
import com.wecloud.erp.model.FaultyItemExample;
import com.wecloud.erp.model.Item;
import com.wecloud.erp.model.ItemExample;
import com.wecloud.erp.model.OrderRequest;
import com.wecloud.erp.model.OrderRequestItem;
import com.wecloud.erp.model.OrderRequestItemExample;
import com.wecloud.erp.model.PO;
import com.wecloud.erp.model.PoItem;
import com.wecloud.erp.model.PoItemExample;
import com.wecloud.erp.model.PoReturn;
import com.wecloud.erp.model.PoReturnItem;
import com.wecloud.erp.model.PoReturnItemExample;
import com.wecloud.erp.model.ProduceNotice;
import com.wecloud.erp.model.ProduceRequest;
import com.wecloud.erp.model.ProduceRequestItem;
import com.wecloud.erp.model.ProduceRequestItemExample;
import com.wecloud.erp.model.ProduceRequestViewExample;
import com.wecloud.erp.model.Product;
import com.wecloud.erp.model.ProductItem;
import com.wecloud.erp.model.ProductItemExample;
import com.wecloud.erp.model.QcRecord;
import com.wecloud.erp.model.QcRecordItem;
import com.wecloud.erp.model.QcRecordItemExample;
import com.wecloud.erp.model.QcRequest;
import com.wecloud.erp.model.QcRequestItem;
import com.wecloud.erp.model.QcRequestItemExample;
import com.wecloud.erp.model.SalesOrder;
import com.wecloud.erp.model.SoItem;
import com.wecloud.erp.model.SoItemExample;
import com.wecloud.erp.model.SoReturn;
import com.wecloud.erp.model.SoReturnItem;
import com.wecloud.erp.model.SoReturnItemExample;
import com.wecloud.erp.model.Uom;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.OrderManager;
import com.wecloud.erp.service.PoManager;
import com.wecloud.erp.service.ViewManager;
import com.wecloud.erp.utils.ErpUtils;
import com.wecloud.erp.web.exception.ActionException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JsonDataSource;

@Controller
public class PrintController {
    private static final Logger LOGGER = Logger.getLogger((Class)PrintController.class);
    private static Map<String, String> stMap = new HashMap<String, String>();
    @Resource
    private PoManager poManager;
    @Resource
    private OrderManager orderManager;
    @Resource
    private ItemManager itemManager;
    @Resource
    private ViewManager viewManager;

    static {
        stMap.put("electronic", "\u7535\u5b50\u4ed3");
        stMap.put("product", "\u6210\u54c1\u4ed3");
    }

    @RequestMapping(value={"/printOrderReqReport.do"}, method={RequestMethod.GET})
    public String printOrderReqReport(Model model, HttpServletRequest request) {
        String requestId = request.getParameter("requestId");
        OrderRequest orderReq = this.poManager.getOrderRequest(requestId);
        OrderRequestItemExample example = new OrderRequestItemExample();
        example.createCriteria().andRequestIdEqualTo(orderReq.getId());
        List<OrderRequestItem> items = this.poManager.listOrderRequestItems(example);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        if (ErpUtils.hasElement(items)) {
            items.forEach(item -> {
                item.setUom((String)uomMap.get(item.getUom()));
            }
            );
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("orderReq", orderReq);
        result.put("items", items);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/orderreq.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    @RequestMapping(value={"/printPoReport.do"}, method={RequestMethod.GET})
    public String printPoReport(Model model, HttpServletRequest request) {
        String poId = request.getParameter("poId");
        PO po = this.poManager.getPO(poId);
        PoItemExample example = new PoItemExample();
        example.createCriteria().andOrderIdEqualTo(po.getId());
        List<PoItem> items = this.poManager.listPoItems(example);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        if (ErpUtils.hasElement(items)) {
            items.forEach(item -> {
                item.setUom((String)uomMap.get(item.getUom()));
            }
            );
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("po", po);
        result.put("items", items);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/po.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    @RequestMapping(value={"/printSoReport"}, method={RequestMethod.GET})
    public String printSoReport(Model model, HttpServletRequest request) {
        String soId = request.getParameter("soId");
        SalesOrder so = this.orderManager.getSalesOrder(soId);
        SoItemExample example = new SoItemExample();
        example.createCriteria().andOrderIdEqualTo(so.getId());
        List<SoItem> items = this.orderManager.listSoItems(example);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        if (ErpUtils.hasElement(items)) {
            items.forEach(item -> {
                item.setUom((String)uomMap.get(item.getUom()));
            }
            );
        }
        Customer cust = this.itemManager.getCustomer(so.getBuyerId());
        so.setBuyerId(cust.getName());
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("po", so);
        result.put("items", items);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/so.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    private List<SoItem> getSoItems(String soId) {
        SoItemExample query = new SoItemExample();
        query.createCriteria().andOrderIdEqualTo(soId);
        return this.orderManager.listSoItems(query);
    }

    @RequestMapping(value={"/printPrdreqReport.do"}, method={RequestMethod.GET})
    public String printPrdreqReport(Model model, HttpServletRequest request) {
        String requestId = request.getParameter("reqId");
        ProduceRequest prdRequest = this.orderManager.getProduceRequest(requestId);
        SalesOrder so = this.orderManager.getSalesOrder(prdRequest.getOrderId());
        List<SoItem> soItems = this.getSoItems(so.getId());
        ProduceRequestItemExample query = new ProduceRequestItemExample();
        query.createCriteria().andRequestIdEqualTo(requestId);
        List<ProduceRequestItem> reqItems = this.orderManager.listProduceRequestItems(query);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        HashMap itemMap = new HashMap();
        reqItems.forEach(reqItem -> {
            HashMap<String, ProduceRequestItem> subMap = new HashMap<String, ProduceRequestItem>();
            subMap.put("reqItem", reqItem);
            reqItem.setUom((String)uomMap.get(reqItem.getUom()));
            itemMap.put(reqItem.getItemId(), subMap);
        }
        );
        soItems.forEach(soItem -> {
            ((Map)itemMap.get(soItem.getItemId())).put("soItem", soItem);
        }
        );
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemMap", itemMap.values());
        result.put("prdRequest", prdRequest);
        result.put("salesOrder", so);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/prdreq.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    @RequestMapping(value={"/printPrdnoticeReport.do"}, method={RequestMethod.GET})
    public String printPrdnoticeReport(Model model, HttpServletRequest request) {
        String noticeId = request.getParameter("noticeId");
        ProduceNotice prdNotice = this.orderManager.getProduceNotice(noticeId);
        SalesOrder so = this.orderManager.getSalesOrder(prdNotice.getOrderId());
        ProduceRequestViewExample pre = new ProduceRequestViewExample();
        pre.createCriteria().andSoIdEqualTo(prdNotice.getOrderId());
        List<ProduceRequest> prdRequests = this.orderManager.listProduceRequests(pre);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemMap", prdRequests);
        result.put("prdNotice", prdNotice);
        result.put("salesOrder", so);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/prdnotice.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    @RequestMapping(value={"/printQcreqReport.do"}, method={RequestMethod.GET})
    public String printQcreqReport(Model model, HttpServletRequest request) {
        String objectId = request.getParameter("objectId");
        QcRequest qcReq = this.poManager.getQcRequest(objectId);
        QcRequestItemExample example = new QcRequestItemExample();
        example.createCriteria().andRequestIdEqualTo(objectId);
        List<QcRequestItem> reqItems = this.poManager.listQcRequestItems(example);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        reqItems.forEach(item -> {
            item.setUom((String)uomMap.get(item.getUom()));
        }
        );
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("obj", qcReq);
        result.put("items", reqItems);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/qcreq.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    @RequestMapping(value={"/printQcrecReport.do"}, method={RequestMethod.GET})
    public String printQcrecReport(Model model, HttpServletRequest request) {
        String objectId = request.getParameter("objectId");
        QcRecord qcRec = this.poManager.getQcRecord(objectId);
        QcRecordItemExample example = new QcRecordItemExample();
        example.createCriteria().andRecordIdEqualTo(objectId);
        List<QcRecordItem> recItems = this.poManager.listQcRecordItems(example);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        recItems.forEach(item -> {
            item.setUom((String)uomMap.get(item.getUom()));
        }
        );
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("obj", qcRec);
        result.put("items", recItems);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/qcrec.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    @RequestMapping(value={"/printDeliveryReqReport.do"}, method={RequestMethod.GET})
    public String printDeliveryReqReport(Model model, HttpServletRequest request) {
        String requestId = request.getParameter("reqId");
        DeliveryRequest checkoutReq = this.poManager.getDeliveryRequest(requestId);
        DeliveryRequestItemExample example = new DeliveryRequestItemExample();
        example.createCriteria().andRequestIdEqualTo(requestId);
        List<DeliveryRequestItem> reqItems = this.poManager.listDeliveryRequestItems(example);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        HashMap itemMap = new HashMap();
        ArrayList itemIds = new ArrayList();
        reqItems.forEach(reqItem -> {
            HashMap<String, DeliveryRequestItem> subMap = new HashMap<String, DeliveryRequestItem>();
            reqItem.setUom((String)uomMap.get(reqItem.getUom()));
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
        HashMap<String, Object> result = new HashMap<>();
        result.put("itemMap", itemMap.values());
        result.put("obj", checkoutReq);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/deliveryreq.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    @RequestMapping(value={"/printPoReturnReport.do"}, method={RequestMethod.GET})
    public String printPoReturnReport(Model model, HttpServletRequest request) {
        String returnId = request.getParameter("returnId");
        PoReturn checkoutReq = this.orderManager.getPoReturn(returnId);
        PoReturnItemExample example = new PoReturnItemExample();
        example.createCriteria().andReturnIdEqualTo(returnId);
        List<PoReturnItem> returnItems = this.orderManager.listPoReturnItems(example);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        HashMap itemMap = new HashMap();
        ArrayList itemIds = new ArrayList();
        returnItems.forEach(returnItem -> {
            HashMap<String, PoReturnItem> subMap = new HashMap<String, PoReturnItem>();
            returnItem.setUom((String)uomMap.get(returnItem.getUom()));
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
        HashMap<String, Object> result = new HashMap<>();
        result.put("itemMap", itemMap.values());
        result.put("obj", checkoutReq);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/poreturn.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    @RequestMapping(value={"/printSoReturnReport.do"}, method={RequestMethod.GET})
    public String printSoReturnReport(Model model, HttpServletRequest request) {
        String returnId = request.getParameter("returnId");
        SoReturn checkoutReq = this.orderManager.getSoReturn(returnId);
        SoReturnItemExample example = new SoReturnItemExample();
        example.createCriteria().andReturnIdEqualTo(returnId);
        List<SoReturnItem> returnItems = this.orderManager.listSoReturnItems(example);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        HashMap itemMap = new HashMap();
        ArrayList itemIds = new ArrayList();
        returnItems.forEach(returnItem -> {
            HashMap<String, SoReturnItem> subMap = new HashMap<String, SoReturnItem>();
            returnItem.setUom((String)uomMap.get(returnItem.getUom()));
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
        HashMap<String, Object> result = new HashMap<>();
        result.put("itemMap", itemMap.values());
        result.put("obj", checkoutReq);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/soreturn.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    @RequestMapping(value={"/printCheckinRecReport.do"}, method={RequestMethod.GET})
    public String printCheckinRecReport(Model model, HttpServletRequest request) {
        String objectId = request.getParameter("objectId");
        CheckinRecord qcRec = this.poManager.getCheckinRecord(objectId);
        CheckinRecordItemExample example = new CheckinRecordItemExample();
        example.createCriteria().andRecordIdEqualTo(objectId);
        List<CheckinRecordItem> recItems = this.poManager.listCheckinRecordItems(example);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        HashMap itemMap = new HashMap();
        ArrayList itemIds = new ArrayList();
        recItems.forEach(recItem -> {
            HashMap<String, CheckinRecordItem> subMap = new HashMap<String, CheckinRecordItem>();
            subMap.put("objItem", recItem);
            recItem.setUom((String)uomMap.get(recItem.getUom()));
            recItem.setStorageType(stMap.get(recItem.getStorageType()));
            itemMap.put(recItem.getItemId(), subMap);
            itemIds.add(recItem.getItemId());
        }
        );
        ItemExample iQuery = new ItemExample();
        iQuery.createCriteria().andIdIn(itemIds);
        List<Item> items = this.itemManager.listItems(iQuery);
        items.forEach(item -> {
            ((Map)itemMap.get(item.getId())).put("item", item);
        }
        );
        HashMap<String, Object> result = new HashMap<>();
        result.put("itemMap", itemMap.values());
        result.put("obj", qcRec);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/checkinrec.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    @RequestMapping(value={"/printCheckoutRecReport.do"}, method={RequestMethod.GET})
    public String printCheckoutRecReport(Model model, HttpServletRequest request) {
        String objectId = request.getParameter("objectId");
        CheckoutRecord checkoutRec = this.orderManager.getCheckoutRecord(objectId);
        CheckoutRecordItemExample example = new CheckoutRecordItemExample();
        example.createCriteria().andRecordIdEqualTo(objectId);
        List<CheckoutRecordItem> recItems = this.orderManager.listCheckoutRecordItems(example);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        HashMap itemMap = new HashMap();
        ArrayList itemIds = new ArrayList();
        recItems.forEach(recItem -> {
            HashMap<String, CheckoutRecordItem> subMap = new HashMap<String, CheckoutRecordItem>();
            subMap.put("objItem", recItem);
            recItem.setUom((String)uomMap.get(recItem.getUom()));
            itemMap.put(recItem.getItemId(), subMap);
            itemIds.add(recItem.getItemId());
        }
        );
        ItemExample iQuery = new ItemExample();
        iQuery.createCriteria().andIdIn(itemIds);
        List<Item> items = this.itemManager.listItems(iQuery);
        items.forEach(item -> {
            ((Map)itemMap.get(item.getId())).put("item", item);
        }
        );
        HashMap<String, Object> result = new HashMap<>();
        result.put("itemMap", itemMap.values());
        result.put("obj", checkoutRec);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/checkoutrec.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    @RequestMapping(value={"/printCheckoutReqReport.do"}, method={RequestMethod.GET})
    public String printCheckoutReqReport(Model model, HttpServletRequest request) {
        String objectId = request.getParameter("objectId");
        CheckoutRequest checkoutRec = this.orderManager.getCheckoutRequest(objectId);
        CheckoutReqItemExample example = new CheckoutReqItemExample();
        example.createCriteria().andRequestIdEqualTo(objectId);
        List<CheckoutReqItem> recItems = this.orderManager.listCheckoutReqItems(example);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        HashMap itemMap = new HashMap();
        ArrayList itemIds = new ArrayList();
        recItems.forEach(reqItem -> {
            HashMap<String, CheckoutReqItem> subMap = new HashMap<String, CheckoutReqItem>();
            subMap.put("objItem", reqItem);
            reqItem.setUom((String)uomMap.get(reqItem.getUom()));
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
        result.put("itemMap", itemMap.values());
        result.put("obj", checkoutRec);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/checkoutreq.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    @RequestMapping(value={"/printFaultyReport.do"}, method={RequestMethod.GET})
    public String printFaultyReport(Model model, HttpServletRequest request) {
        String objectId = request.getParameter("objectId");
        Faulty faulty = this.viewManager.getFaulty(objectId);
        Customer vendor = this.itemManager.getCustomer(faulty.getSupplierId());
        if (vendor != null) {
            faulty.setSupplierId(vendor.getName());
        }
        FaultyItemExample example = new FaultyItemExample();
        example.createCriteria().andFaultyIdEqualTo(objectId);
        List<FaultyItem> reqItems = this.viewManager.listFaultyItems(example);
        HashMap itemMap = new HashMap();
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        ArrayList itemIds = new ArrayList();
        reqItems.forEach(faultyItem -> {
            HashMap<String, FaultyItem> subMap = new HashMap<String, FaultyItem>();
            faultyItem.setUom((String)uomMap.get(faultyItem.getUom()));
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
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemMap", itemMap.values());
        result.put("obj", faulty);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/faulty.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }

    @RequestMapping(value={"/printProductReport.do"}, method={RequestMethod.GET})
    public String printProductReport(Model model, HttpServletRequest request) {
        String objectId = request.getParameter("objectId");
        Product product = this.itemManager.getProduct(objectId);
        Item item = this.itemManager.getItem(product.getItemId());
        ProductItemExample query = new ProductItemExample();
        query.createCriteria().andProductIdEqualTo(objectId);
        List<ProductItem> itemList = this.itemManager.listProductItems(query);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        item.setUom((String)uomMap.get(item.getUom()));
        ArrayList itemIds = new ArrayList();
        HashMap itemMap = new HashMap();
        itemList.forEach(pItem -> {
            HashMap<String, ProductItem> subMap = new HashMap<String, ProductItem>();
            subMap.put("objItem", pItem);
            pItem.setUom((String)uomMap.get(pItem.getUom()));
            itemMap.put(pItem.getItemId(), subMap);
            itemIds.add(pItem.getItemId());
        }
        );
        if (ErpUtils.hasElement(itemIds)) {
            ItemExample iQuery = new ItemExample();
            iQuery.createCriteria().andIdIn(itemIds);
            List<Item> items = this.itemManager.listItems(iQuery);
            items.forEach(sItem -> {
                ((Map)itemMap.get(sItem.getId())).put("item", sItem);
            }
            );
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("itemMap", itemMap.values());
        result.put("item", item);
        result.put("obj", product);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        JsonGenerator generator = null;
        try {
            generator = mapper.getFactory().createJsonGenerator((Writer)out);
            generator.writeObject(result);
            JsonDataSource jrDataSource = new JsonDataSource((InputStream)new ByteArrayInputStream(out.toString().getBytes("UTF-8")));
            model.addAttribute("url", "/WEB-INF/jasper/product.jasper");
            model.addAttribute("format", "pdf");
            model.addAttribute("jrMainDataSource", jrDataSource);
        }
        catch (IOException | JRException e) {
            throw new ActionException();
        }
        return "reportView";
    }
}
