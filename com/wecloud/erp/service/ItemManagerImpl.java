package com.wecloud.erp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wecloud.erp.model.Contact;
import com.wecloud.erp.model.ContactExample;
import com.wecloud.erp.model.Currency;
import com.wecloud.erp.model.CurrencyExample;
import com.wecloud.erp.model.Customer;
import com.wecloud.erp.model.CustomerExample;
import com.wecloud.erp.model.CustomerType;
import com.wecloud.erp.model.CustomerTypeExample;
import com.wecloud.erp.model.Inventory;
import com.wecloud.erp.model.InventoryExample;
import com.wecloud.erp.model.Item;
import com.wecloud.erp.model.ItemExample;
import com.wecloud.erp.model.ItemSupplierMapping;
import com.wecloud.erp.model.ItemSupplierMappingExample;
import com.wecloud.erp.model.ItemType;
import com.wecloud.erp.model.ItemTypeExample;
import com.wecloud.erp.model.PaymentType;
import com.wecloud.erp.model.PaymentTypeExample;
import com.wecloud.erp.model.Product;
import com.wecloud.erp.model.ProductExample;
import com.wecloud.erp.model.ProductItem;
import com.wecloud.erp.model.ProductItemExample;
import com.wecloud.erp.model.ProductionItemExample;
import com.wecloud.erp.model.Storage;
import com.wecloud.erp.model.StorageExample;
import com.wecloud.erp.model.Uom;
import com.wecloud.erp.model.UomExample;
import com.wecloud.erp.model.UomFormula;
import com.wecloud.erp.model.UomFormulaExample;
import com.wecloud.erp.persistence.dao.ContactMapper;
import com.wecloud.erp.persistence.dao.CurrencyMapper;
import com.wecloud.erp.persistence.dao.CustomerMapper;
import com.wecloud.erp.persistence.dao.CustomerTypeMapper;
import com.wecloud.erp.persistence.dao.InventoryMapper;
import com.wecloud.erp.persistence.dao.ItemMapper;
import com.wecloud.erp.persistence.dao.ItemSupplierMappingMapper;
import com.wecloud.erp.persistence.dao.ItemTypeMapper;
import com.wecloud.erp.persistence.dao.PaymentTypeMapper;
import com.wecloud.erp.persistence.dao.ProductItemMapper;
import com.wecloud.erp.persistence.dao.ProductMapper;
import com.wecloud.erp.persistence.dao.ProductionItemMapper;
import com.wecloud.erp.persistence.dao.StorageMapper;
import com.wecloud.erp.persistence.dao.UomFormulaMapper;
import com.wecloud.erp.persistence.dao.UomMapper;
import com.wecloud.erp.utils.ErpUtils;

@Service(value="itemManager")
public class ItemManagerImpl
    implements ItemManager
{

    public ItemManagerImpl()
    {
    }

    public void addContact(Contact contact)
    {
        contactDao.insert(contact);
    }

    public void addCustomer(Customer customer)
    {
        customerDao.insert(customer);
    }

    public void addItem(Item item)
    {
        itemDao.insert(item);
    }

    public void addProduct(Product product)
    {
        productDao.insert(product);
    }

    public void addProductItem(ProductItem productItem)
    {
        productItemDao.insert(productItem);
    }

    public void addUom(Uom uom)
    {
        uomDao.insert(uom);
    }

    public void addUomFormula(UomFormula uomFormula)
    {
        uomFormulaDao.insert(uomFormula);
    }

    public void addStorage(Storage storage)
    {
        storageDao.insert(storage);
    }

    public void addInventory(Inventory inventory)
    {
        inventoryDao.insert(inventory);
    }

    public void addItemType(ItemType itemType)
    {
        itemTypeDao.insert(itemType);
    }

    public void addCurrency(Currency currency)
    {
        currencyDao.insert(currency);
    }

    public void addCustomerType(CustomerType customerType)
    {
        customerTypeDao.insert(customerType);
    }

    public void deleteContact(String id)
    {
        contactDao.deleteByPrimaryKey(id);
    }

    @Transactional
    public void deleteContactByExample(ContactExample example)
    {
        contactDao.deleteByExample(example);
    }

    public void deleteCustomer(String id)
    {
        customerDao.deleteByPrimaryKey(id);
    }

    public void deleteItem(String id)
    {
        itemDao.deleteByPrimaryKey(id);
    }

    public void deleteProduct(String id)
    {
        productDao.deleteByPrimaryKey(id);
    }

    public void deleteProductItem(String id)
    {
        productItemDao.deleteByPrimaryKey(id);
    }

    public void deleteProductItemsByPrdId(String id)
    {
        ProductItemExample exp = new ProductItemExample();
        exp.createCriteria().andProductIdEqualTo(id);
        productItemDao.deleteByExample(exp);
    }

    public void deleteUom(String id)
    {
        uomDao.deleteByPrimaryKey(id);
    }

    public void deleteUomFormula(String id)
    {
        uomFormulaDao.deleteByPrimaryKey(id);
    }

    public void deleteStorage(String id)
    {
        storageDao.deleteByPrimaryKey(id);
    }

    public void deleteStorage(StorageExample example)
    {
        storageDao.deleteByExample(example);
    }

    public void deleteInventory(String id)
    {
        inventoryDao.deleteByPrimaryKey(id);
    }

    public void deleteItemType(String id)
    {
        itemTypeDao.deleteByPrimaryKey(id);
    }

    public void deleteCurrency(String id)
    {
        currencyDao.deleteByPrimaryKey(id);
    }

    public void deleteCustomerType(String id)
    {
        customerTypeDao.deleteByPrimaryKey(id);
    }

    public Contact getContact(String id)
    {
        return contactDao.selectByPrimaryKey(id);
    }

    public Customer getCustomer(String id)
    {
        return customerDao.selectByPrimaryKey(id);
    }

    public Item getItem(String id)
    {
        return itemDao.selectByPrimaryKey(id);
    }

    public Product getProduct(String id)
    {
        return productDao.selectByPrimaryKey(id);
    }

    public ProductItem getProductItem(String id)
    {
        return productItemDao.selectByPrimaryKey(id);
    }

    public Uom getUom(String id)
    {
        return uomDao.selectByPrimaryKey(id);
    }

    public UomFormula getUomFormula(String id)
    {
        return uomFormulaDao.selectByPrimaryKey(id);
    }

    public Storage getStorage(String itemId, String code, String storageType)
    {
        StorageExample query = new StorageExample();
        query.createCriteria().andItemIdEqualTo(itemId).andTypeEqualTo(storageType).andCodeEqualTo(code);
        List list = storageDao.selectByExample(query);
        Storage result = null;
        if(ErpUtils.hasElement(list))
            result = (Storage)list.get(0);
        return result;
    }

    public Storage getStorage(String storageId)
    {
        return storageDao.selectByPrimaryKey(storageId);
    }

    public Inventory getInventory(String id)
    {
        return inventoryDao.selectByPrimaryKey(id);
    }

    public ItemType getItemType(String id)
    {
        return itemTypeDao.selectByPrimaryKey(id);
    }

    public Currency getCurrency(String id)
    {
        return currencyDao.selectByPrimaryKey(id);
    }

    public CustomerType getCustomerType(String id)
    {
        return customerTypeDao.selectByPrimaryKey(id);
    }

    public PaymentType getPaymentType(String id)
    {
        return paymentTypeDao.selectByPrimaryKey(id);
    }

    public int countItem(ItemExample example)
    {
        return itemDao.countByExample(example);
    }

    public int countUomFormula(UomFormulaExample example)
    {
        return uomFormulaDao.countByExample(example);
    }

    public int countProductItem(ProductItemExample example)
    {
        return productItemDao.countByExample(example);
    }

    public int countProduct(ProductExample example)
    {
        return productDao.countByExample(example);
    }

    public int countUom(UomExample example)
    {
        return uomDao.countByExample(example);
    }

    public int countProductionItem(ProductionItemExample example)
    {
        return productionItemDao.countByExample(example);
    }

    public void updateContact(Contact contact)
    {
        contactDao.updateByPrimaryKey(contact);
    }

    public void updateCustomer(Customer customer)
    {
        customerDao.updateByPrimaryKey(customer);
    }

    public void updateItem(Item item)
    {
        itemDao.updateByPrimaryKeyWithBLOBs(item);
    }

    public void updateProduct(Product product)
    {
        productDao.updateByPrimaryKey(product);
    }

    public void updateProductItem(ProductItem productItem)
    {
        productItemDao.updateByPrimaryKey(productItem);
    }

    public void updateUom(Uom uom)
    {
        uomDao.updateByPrimaryKey(uom);
    }

    public void updateUomFormula(UomFormula uomFormula)
    {
        uomFormulaDao.updateByPrimaryKey(uomFormula);
    }

    public void updateStorage(Storage storage)
    {
        storageDao.updateByPrimaryKey(storage);
    }

    public void updateInventory(Inventory inventory)
    {
        inventoryDao.updateByPrimaryKey(inventory);
    }

    public void updateItemType(ItemType itemType)
    {
        itemTypeDao.updateByPrimaryKey(itemType);
    }

    public void updateCurrency(Currency currency)
    {
        currencyDao.updateByPrimaryKey(currency);
    }

    public void updateCustomerType(CustomerType customerType)
    {
        customerTypeDao.updateByPrimaryKey(customerType);
    }

    public List listContacts(ContactExample example)
    {
        return contactDao.selectByExample(example);
    }

    public List listCustomers(CustomerExample example)
    {
        return customerDao.selectByExample(example);
    }

    public List listItems(ItemExample example)
    {
        return itemDao.selectByExampleWithBLOBs(example);
    }

    public List listProductionItems(ProductionItemExample example)
    {
        return productionItemDao.selectByExampleWithBLOBs(example);
    }

    public List listProducts(ProductExample example)
    {
        return productDao.selectByExampleWithBLOBs(example);
    }

    public List listProductItems(ProductItemExample example)
    {
        return productItemDao.selectByExample(example);
    }

    public List listUoms(UomExample example)
    {
        return uomDao.selectByExample(example);
    }

    public List listUomFormulas(UomFormulaExample example)
    {
        return uomFormulaDao.selectByExample(example);
    }

    public List listStorages(StorageExample example)
    {
        return storageDao.selectByExample(example);
    }

    public List listInventorys(InventoryExample example)
    {
        return inventoryDao.selectByExample(example);
    }

    public List listItemTypes(ItemTypeExample example)
    {
        return itemTypeDao.selectByExample(example);
    }

    public List listCurrencys(CurrencyExample example)
    {
        return currencyDao.selectByExample(example);
    }

    public List listCustomerTypes(CustomerTypeExample example)
    {
        return customerTypeDao.selectByExample(example);
    }

    public List listPaymentTypes(PaymentTypeExample example)
    {
        return paymentTypeDao.selectByExample(example);
    }

    public void addItemSupplierMapping(ItemSupplierMapping itemSupplierMapping)
    {
        itemSupplierMappingDao.insert(itemSupplierMapping);
    }

    public void deleteItemSupplierMappingByItemId(String id)
    {
        ItemSupplierMappingExample query = new ItemSupplierMappingExample();
        query.createCriteria().andItemIdEqualTo(id);
        itemSupplierMappingDao.deleteByExample(query);
    }

    public List listItemSupplierMappings(ItemSupplierMappingExample example)
    {
        return itemSupplierMappingDao.selectByExample(example);
    }

    public List listItemSupplierMappingsByItemId(String id)
    {
        ItemSupplierMappingExample query = new ItemSupplierMappingExample();
        query.createCriteria().andItemIdEqualTo(id);
        return itemSupplierMappingDao.selectByExample(query);
    }

    public List listItemSupplierMappingsBySupplierId(String id)
    {
        ItemSupplierMappingExample query = new ItemSupplierMappingExample();
        query.createCriteria().andSupplierIdEqualTo(id);
        return itemSupplierMappingDao.selectByExample(query);
    }

    public Map getMetaData()
    {
        Map metaData = new HashMap();
        List currencies = listCurrencys(null);
        List customers = listCustomers(null);
        List itemTypes = listItemTypes(null);
        List paymentTypes = listPaymentTypes(null);
        metaData.put("currencies", currencies);
        metaData.put("customers", customers);
        metaData.put("paymentTypes", paymentTypes);
        metaData.put("itemTypes", itemTypes);
        metaData.put("uoms", listUoms(null));
        metaData.put("uomFormulas", listUomFormulas(null));
        return metaData;
    }

    @Resource
    private UomMapper uomDao;
    @Resource
    private ContactMapper contactDao;
    @Resource
    private CurrencyMapper currencyDao;
    @Resource
    private CustomerMapper customerDao;
    @Resource
    private CustomerTypeMapper customerTypeDao;
    @Resource
    private InventoryMapper inventoryDao;
    @Resource
    private ItemMapper itemDao;
    @Resource
    private ProductionItemMapper productionItemDao;
    @Resource
    private ItemTypeMapper itemTypeDao;
    @Resource
    private ProductItemMapper productItemDao;
    @Resource
    private ProductMapper productDao;
    @Resource
    private StorageMapper storageDao;
    @Resource
    private UomFormulaMapper uomFormulaDao;
    @Resource
    private PaymentTypeMapper paymentTypeDao;
    @Resource
    private ItemSupplierMappingMapper itemSupplierMappingDao;
}
