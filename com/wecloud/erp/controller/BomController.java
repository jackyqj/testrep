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
import com.wecloud.erp.entity.ProductDetail;
import com.wecloud.erp.entity.Search;
import com.wecloud.erp.entity.SessionUser;
import com.wecloud.erp.model.Item;
import com.wecloud.erp.model.ItemExample;
import com.wecloud.erp.model.Product;
import com.wecloud.erp.model.ProductExample;
import com.wecloud.erp.model.ProductItem;
import com.wecloud.erp.model.ProductItemExample;
import com.wecloud.erp.model.Sequence;
import com.wecloud.erp.model.Uom;
import com.wecloud.erp.report.impl.ProductExcelModel;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.OrderManager;
import com.wecloud.erp.utils.ErpUtils;
import com.wecloud.erp.utils.LOG;
import com.wecloud.erp.utils.UUID;

@Controller
public class BomController {
    @Resource
    private OrderManager orderManager;
    @Resource
    private ItemManager itemManager;
    private static final Logger LOGGER = Logger.getLogger(BomController.class);

    @RequestMapping(value={"/saveProduct.do"}, method={RequestMethod.POST})
    @Transactional
    @ResponseBody
    public Object saveProduct(@RequestBody ProductDetail product, HttpServletRequest request) {
        if (product.getId() == null) {
            Item item = product.getItem();
            product.setId(UUID.get());
            if (StringUtils.isEmpty((CharSequence)product.getCode())) {
                Sequence seq = this.orderManager.getSequence("BOM");
                product.setCode(ErpUtils.genBomCode((Item)item, (Sequence)seq));
            }
            SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
            product.setCreatedBy(sUser.getId());
            product.setCreator(sUser.getName());
            product.setCreatedOn(new Date());
            product.setUpdatedOn(new Date());
            product.setStatus(BEConstants.STATUS_DRAFT);
            item.setId(UUID.get());
            item.setIsBaseitem(Integer.valueOf(0));
            item.setCode(product.getCode());
            item.setName(product.getName());
            item.setUom(product.getUom());
            item.setStatus(BEConstants.STATUS_DRAFT);
            item.setCreatedOn(new Date());
            item.setUpdatedOn(new Date());
            item.setStyle(product.getStyle());
            product.setItemId(item.getId());
            this.itemManager.addItem(item);
            this.itemManager.addProduct((Product)product);
            for (ProductItem pItem : product.getObjItems()) {
                pItem.setId(UUID.get());
                pItem.setProductId(product.getId());
                this.itemManager.addProductItem(pItem);
            }
        } else {
            Item item = product.getItem();
            item.setCode(product.getCode());
            item.setName(product.getName());
            item.setUom(product.getUom());
            item.setCreatedOn(new Date());
            item.setUpdatedOn(new Date());
            item.setStyle(product.getStyle());
            product.setUpdatedOn(new Date());
            this.itemManager.updateItem(item);
            this.itemManager.updateProduct((Product)product);
            if (product.getStatus() == BEConstants.STATUS_FINAL && ErpUtils.isNotEmpty((String)product.getParentId())) {
                Product originalPrd = this.itemManager.getProduct(product.getParentId());
                originalPrd.setStatus(BEConstants.STATUS_INVALID);
                originalPrd.setUpdatedOn(new Date());
                this.itemManager.updateProduct(originalPrd);
            }
            this.itemManager.deleteProductItemsByPrdId(product.getId());
            for (ProductItem pItem : product.getObjItems()) {
                pItem.setId(UUID.get());
                pItem.setProductId(product.getId());
                this.itemManager.addProductItem(pItem);
            }
        }
        return product;
    }

    @RequestMapping(value={"/loadProduct.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> loadProduct(@RequestParam String productId) {
        Product product = this.itemManager.getProduct(productId);
        Item item = this.itemManager.getItem(product.getItemId());
        ProductItemExample query = new ProductItemExample();
        query.createCriteria().andProductIdEqualTo(productId);
        List<ProductItem> itemList = this.itemManager.listProductItems(query);
        ArrayList<String> itemIds = new ArrayList();
        HashMap itemMap = new HashMap();
        itemList.forEach(pItem -> {
            HashMap<String, ProductItem> subMap = new HashMap<String, ProductItem>();
            subMap.put("objItem", pItem);
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
        LOG.info((Logger)LOGGER, itemMap);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("itemMap", itemMap);
        result.put("item", (Object)item);
        result.put("obj", (Object)product);
        return result;
    }

    @RequestMapping(value={"/listProduct.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listProduct(@RequestBody Search search) {
        String keyword = search.getKeyword();
        String status = search.getStatus();
        ProductExample query = new ProductExample();
        if (ErpUtils.isNotEmpty((String)keyword)) {
            keyword = "%" + keyword + "%";
            if (ErpUtils.isNotEmpty((String)status)) {
                query.or().andCodeLike(keyword).andStatusEqualTo(new Integer(status));
                query.or().andNameLike(keyword).andStatusEqualTo(new Integer(status));
                query.or().andStyleLike(keyword).andStatusEqualTo(new Integer(status));
            } else {
                query.or().andCodeLike(keyword);
                query.or().andNameLike(keyword);
                query.or().andStyleLike(keyword);
            }
        } else if (ErpUtils.isNotEmpty((String)status)) {
            query.createCriteria().andStatusEqualTo(new Integer(status));
        }
        query.setOrderByClause("created_on desc, code desc");
        String limitStart = (String)StringUtils.defaultIfEmpty((CharSequence)search.getLimitStart(), (CharSequence)"0");
        String limitEnd = search.getLimitEnd();
        if (limitEnd == null) {
            limitEnd = String.valueOf(Integer.parseInt(limitStart) + 30);
        }
        query.setLimitStart(limitStart);
        query.setLimitEnd(limitEnd);
        List productList = this.itemManager.listProducts(query);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int count = this.itemManager.countProduct(query);
        result.put("totalCount", count);
        result.put("objList", productList);
        return result;
    }

    @RequestMapping(value={"/exportProduct.do"}, method={RequestMethod.GET})
    public ModelAndView exportProduct(@RequestParam String keyword, @RequestParam String status) {
        ProductExample query = new ProductExample();
        if (ErpUtils.isNotEmpty((String)keyword)) {
            keyword = "%" + keyword + "%";
            if (ErpUtils.isNotEmpty((String)status)) {
                query.or().andCodeLike(keyword).andStatusEqualTo(new Integer(status));
                query.or().andNameLike(keyword).andStatusEqualTo(new Integer(status));
                query.or().andStyleLike(keyword).andStatusEqualTo(new Integer(status));
            } else {
                query.or().andCodeLike(keyword);
                query.or().andNameLike(keyword);
                query.or().andStyleLike(keyword);
            }
        } else if (ErpUtils.isNotEmpty((String)status)) {
            query.createCriteria().andStatusEqualTo(new Integer(status));
        }
        query.setOrderByClause("created_on desc, code desc");
        List<Product> productList = this.itemManager.listProducts(query);
        List<Uom> uoms = this.itemManager.listUoms(null);
        HashMap uomMap = new HashMap();
        uoms.forEach(uom -> {
            uomMap.put(uom.getId(), uom.getName());
        }
        );
        if (ErpUtils.hasElement((Collection)productList)) {
            productList.forEach(item -> {
                item.setUom((String)uomMap.get(item.getUom()));
            }
            );
        }
        ProductExcelModel viewModel = new ProductExcelModel(productList);
        return new ModelAndView("excelReportView", "excelModel", (Object)viewModel);
    }
}
