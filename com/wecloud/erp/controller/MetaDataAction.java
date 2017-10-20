package com.wecloud.erp.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wecloud.erp.entity.CustomerDetails;
import com.wecloud.erp.entity.Search;
import com.wecloud.erp.entity.SessionUser;
import com.wecloud.erp.model.Contact;
import com.wecloud.erp.model.ContactExample;
import com.wecloud.erp.model.Currency;
import com.wecloud.erp.model.Customer;
import com.wecloud.erp.model.CustomerType;
import com.wecloud.erp.model.CustomerTypeExample;
import com.wecloud.erp.model.CustomerViewExample;
import com.wecloud.erp.model.ErpUser;
import com.wecloud.erp.model.ItemExample;
import com.wecloud.erp.model.ItemType;
import com.wecloud.erp.model.SalesOrderExample;
import com.wecloud.erp.model.Sequence;
import com.wecloud.erp.model.Uom;
import com.wecloud.erp.model.UomFormula;
import com.wecloud.erp.model.UomFormulaExample;
import com.wecloud.erp.service.ItemManager;
import com.wecloud.erp.service.OrderManager;
import com.wecloud.erp.service.UserManager;
import com.wecloud.erp.service.ViewManager;
import com.wecloud.erp.utils.ErpUtils;
import com.wecloud.erp.utils.LOG;
import com.wecloud.erp.utils.UUID;
import com.wecloud.erp.web.exception.ActionException;

@Controller
public class MetaDataAction {
    @Resource
    private ItemManager itemManager;
    @Resource
    private ViewManager viewManager;
    @Resource
    private OrderManager orderManager;
    @Resource
    private UserManager userManager;
    private static Logger L = Logger.getLogger(MetaDataAction.class);
    private static final String DEPARTMENT = "department";
    private static final String MANAGEMENT = "management";
    private static final List<String> D_APPROVAL_ROLES = new ArrayList<String>();
    private static final List<String> M_APPROVAL_ROLES;

    static {
        D_APPROVAL_ROLES.add("SALES_ADMIN");
        D_APPROVAL_ROLES.add("PURCHASE_ADMIN");
        D_APPROVAL_ROLES.add("QC_ADMIN");
        M_APPROVAL_ROLES = new ArrayList<String>();
        M_APPROVAL_ROLES.add("SYSADMIN");
    }

    @RequestMapping(value={"/listCustomer.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listCustomer(@RequestBody Search search) {
        CustomerViewExample query = new CustomerViewExample();
        CustomerViewExample.Criteria criteria = query.createCriteria().andCategoryEqualTo(search.getCustomerCategory());
        LOG.debug((Logger)L, (Object)("Get search information: " + (Object)search));
        if (ErpUtils.isNotEmpty((String)search.getCustomerType())) {
            criteria.andTypeEqualTo(search.getCustomerType());
        }
        if (ErpUtils.isNotEmpty((String)search.getKeyword())) {
            criteria.andNameLike("%" + search.getKeyword() + "%");
        }
        List<Customer> customers = this.viewManager.listCustomerViews(query);
        CustomerTypeExample tQuery = new CustomerTypeExample();
        tQuery.createCriteria().andCategoryEqualTo(search.getCustomerCategory());
        List<CustomerType> customerTypes = this.itemManager.listCustomerTypes(tQuery);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("customers", customers);
        result.put("customerTypes", customerTypes);
        LOG.info((Logger)L, result);
        return result;
    }

    @RequestMapping(value={"/loadCustomer.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> listCustomer(@RequestParam String customerId) {
        Customer customer = this.itemManager.getCustomer(customerId);
        ContactExample query = new ContactExample();
        query.createCriteria().andCustomerIdEqualTo(customerId);
        List<Contact> contacts = this.itemManager.listContacts(query);
        if (contacts == null) {
            contacts = new ArrayList();
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("customer", (Object)customer);
        result.put("contacts", contacts);
        LOG.info((Logger)L, result);
        return result;
    }

    @RequestMapping(value={"/changePassword.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> changePassword(@RequestParam String oPassword, @RequestParam String nPassword, HttpServletRequest request) {
        Md5PasswordEncoder enc;
        SessionUser user = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        ErpUser eUser = this.userManager.getErpUser(user.getLoginId());
        boolean isOk = false;
        if (user != null && (enc = new Md5PasswordEncoder()).isPasswordValid(eUser.getPassword(), oPassword, (Object)user.getLoginId())) {
            eUser.setPassword(enc.encodePassword(nPassword, (Object)user.getLoginId()));
            this.userManager.updateErpUser(eUser);
            isOk = true;
            LOG.info((Logger)L, (Object)("user password was reset." + user.getId()));
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("isOk", isOk);
        return result;
    }

    @Transactional
    @RequestMapping(value={"/saveCustomer.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> saveCustomer(@RequestBody CustomerDetails customerDetails) {
        Customer customer = customerDetails.getCustomer();
        if (customer.getId() == null) {
            customer.setId(UUID.get());
            String seqKey = String.valueOf(customer.getCategory()) + customer.getConnectedDate().getYear();
            Sequence seq = this.orderManager.getSequence(seqKey);
            customer.setCode(ErpUtils.genCustomerCode((Customer)customer, (Sequence)seq));
            this.itemManager.addCustomer(customer);
        } else {
            this.itemManager.updateCustomer(customer);
            ContactExample query = new ContactExample();
            query.createCriteria().andCustomerIdEqualTo(customer.getId());
            this.itemManager.deleteContactByExample(query);
        }
        List<Contact> contacts = customerDetails.getContacts();
        LOG.info(L, contacts);
        if (contacts != null && !contacts.isEmpty()) {
            contacts.forEach(contact -> {
                if (contact.getId() == null) {
                    contact.setId(UUID.get());
                    contact.setCustomerId(customer.getId());
                }
                this.itemManager.addContact(contact);
            }
            );
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("customer", (Object)customer);
        result.put("contacts", contacts);
        LOG.info((Logger)L, result);
        return result;
    }

    @RequestMapping(value={"/deleteCustomer.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> deleteCustomer(@RequestParam String customerId, @RequestParam String category) {
        boolean validation = true;
        if ("buyer".equals(category)) {
            SalesOrderExample query = new SalesOrderExample();
            query.createCriteria().andBuyerIdEqualTo(customerId);
            if (this.orderManager.countSalesOrder(query) > 0) {
                validation = false;
                LOG.warn((Logger)L, (Object)"Order exist for the buyer.");
            }
        } else if (ErpUtils.hasElement((Collection)this.itemManager.listItemSupplierMappingsBySupplierId(customerId))) {
            validation = false;
            LOG.warn((Logger)L, (Object)"Item exist for the buyer.");
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (!validation) {
            throw new ActionException();
        }
        ContactExample query = new ContactExample();
        query.createCriteria().andCustomerIdEqualTo(customerId);
        this.itemManager.deleteContactByExample(query);
        this.itemManager.deleteCustomer(customerId);
        result.put("customerId", customerId);
        LOG.info((Logger)L, result);
        return result;
    }

    @RequestMapping(value={"/getMetaData.do"})
    @ResponseBody
    public Object getMetaData(HttpServletRequest request) {
        HashMap<String, Object> metaData = new HashMap<String, Object>();
        List currencies = this.itemManager.listCurrencys(null);
        List customers = this.itemManager.listCustomers(null);
        List itemTypes = this.itemManager.listItemTypes(null);
        List<ErpUser> dApprovers = this.getApprovers("department");
        List<ErpUser> mApprovers = this.getApprovers("management");
        List paymentTypes = this.itemManager.listPaymentTypes(null);
        metaData.put("currencies", currencies);
        metaData.put("customers", customers);
        metaData.put("dApprovers", dApprovers);
        metaData.put("mApprovers", mApprovers);
        metaData.put("paymentTypes", paymentTypes);
        metaData.put("itemTypes", itemTypes);
        metaData.put("uoms", this.itemManager.listUoms(null));
        metaData.put("pageSize", 30);
        metaData.put("uomFormulas", this.itemManager.listUomFormulas(null));
        LOG.info((Logger)L, (Object)"before get sesision user");
        SessionUser sUser = (SessionUser)request.getSession().getAttribute("SESSION-USER");
        LOG.info((Logger)L, (Object)("after get sesision user: " + (Object)sUser));
        metaData.put("userRole", this.userManager.listErpUserRoles(this.userManager.getErpUser(sUser.getLoginId())));
        metaData.put("sessionUserId", sUser.getId());
        metaData.put("sessionUserName", sUser.getName());
        LOG.info((Logger)L, (Object)("after get sesision user: " + metaData));
        return metaData;
    }

    private List<ErpUser> getApprovers(String role) {
        return this.userManager.listErpUsersByRole("department".equals(role) ? D_APPROVAL_ROLES : M_APPROVAL_ROLES);
    }

    @RequestMapping(value={"/listCustomerTypes.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Object listCustomerTypes(@RequestParam String category) {
        CustomerTypeExample query = new CustomerTypeExample();
        query.createCriteria().andCategoryEqualTo(category);
        return this.itemManager.listCustomerTypes(query);
    }

    @RequestMapping(value={"/saveCustomerType.do"}, method={RequestMethod.POST}, headers={"Content-Type=application/json"})
    @ResponseBody
    public Object saveCustomerType(@RequestBody CustomerType customerType) {
        LOG.info((Logger)L, (Object)("get: " + (Object)customerType));
        if (ErpUtils.isNotEmpty((String)customerType.getId())) {
            this.itemManager.updateCustomerType(customerType);
        } else {
            customerType.setId(UUID.get());
            this.itemManager.addCustomerType(customerType);
        }
        return customerType;
    }

    @RequestMapping(value={"/deleteCustomerType.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Object deleteCustomerType(@RequestParam String customerTypeId) {
        this.itemManager.deleteCustomerType(customerTypeId);
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("customerTypeId", customerTypeId);
        return result;
    }

    @RequestMapping(value={"/saveItemType.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Object saveItemType(@RequestBody ItemType itemType) {
        if (itemType.getId() == null) {
            itemType.setId(UUID.get());
            this.itemManager.addItemType(itemType);
        } else {
            this.itemManager.updateItemType(itemType);
        }
        return itemType;
    }

    @RequestMapping(value={"/deleteItemType.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Object deleteItemType(@RequestParam String itemTypeId) {
        this.itemManager.deleteItemType(itemTypeId);
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("itemTypeId", itemTypeId);
        return result;
    }

    @RequestMapping(value={"/saveUom.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Object saveUom(@RequestBody Uom uom) {
        if (uom.getId() == null) {
            uom.setId(UUID.get());
            this.itemManager.addUom(uom);
        } else {
            this.itemManager.updateUom(uom);
        }
        return uom;
    }

    @RequestMapping(value={"/deleteUom.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Object deleteUom(@RequestParam String uomId) {
        boolean validation = true;
        ItemExample query = new ItemExample();
        query.createCriteria().andUomEqualTo(uomId);
        if (this.itemManager.countItem(query) > 0) {
            validation = false;
            LOG.warn((Logger)L, (Object)"Item exist for the buyer.");
        } else {
            UomFormulaExample uQuery = new UomFormulaExample();
            uQuery.createCriteria().andBaseUomEqualTo(uomId);
            uQuery.or(uQuery.createCriteria().andFromUomEqualTo(uomId));
            validation = this.itemManager.countUomFormula(uQuery) == 0;
        }
        HashMap<String, String> result = new HashMap<String, String>();
        if (!validation) {
            throw new ActionException();
        }
        this.itemManager.deleteUom(uomId);
        result.put("UomId", uomId);
        return result;
    }

    @RequestMapping(value={"/saveUomFormula.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Object saveUomFormula(@RequestBody UomFormula uomFormula) {
        if (uomFormula.getId() == null) {
            uomFormula.setId(UUID.get());
            this.itemManager.addUomFormula(uomFormula);
        } else {
            this.itemManager.updateUomFormula(uomFormula);
        }
        return uomFormula;
    }

    @RequestMapping(value={"/deleteUomFormula.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Object deleteUomFormula(@RequestParam String uomFormulaId) {
        ItemExample query = new ItemExample();
        query.createCriteria().andUomFormularIdEqualTo(uomFormulaId);
        if (this.itemManager.countItem(query) > 0) {
            LOG.warn((Logger)L, (Object)"Item exist for the buyer.");
            throw new ActionException();
        }
        this.itemManager.deleteUomFormula(uomFormulaId);
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("UomFormulaId", uomFormulaId);
        return result;
    }

    @RequestMapping(value={"/saveCurrency.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Object saveCurrency(@RequestBody Currency currency) {
        if (currency.getId() == null) {
            currency.setId(UUID.get());
            this.itemManager.addCurrency(currency);
        } else {
            this.itemManager.updateCurrency(currency);
        }
        return currency;
    }

    @RequestMapping(value={"/deleteCurrency.do"}, method={RequestMethod.POST})
    @ResponseBody
    public Object deleteCurrency(@RequestParam String currencyId) {
        this.itemManager.deleteCurrency(currencyId);
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("currencyId", currencyId);
        return result;
    }
}
