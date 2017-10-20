package com.wecloud.erp.service;

import java.util.List;
import java.util.Map;

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
import com.wecloud.erp.model.ProductionItem;
import com.wecloud.erp.model.ProductionItemExample;
import com.wecloud.erp.model.Storage;
import com.wecloud.erp.model.StorageExample;
import com.wecloud.erp.model.Uom;
import com.wecloud.erp.model.UomExample;
import com.wecloud.erp.model.UomFormula;
import com.wecloud.erp.model.UomFormulaExample;

public interface ItemManager
{

    public abstract void addContact(Contact contact);

    public abstract void addCustomer(Customer customer);

    public abstract void addItem(Item item);

    public abstract void addProduct(Product product);

    public abstract void addProductItem(ProductItem productitem);

    public abstract void addUom(Uom uom);

    public abstract void addUomFormula(UomFormula uomformula);

    public abstract void addStorage(Storage storage);

    public abstract void addInventory(Inventory inventory);

    public abstract void addItemType(ItemType itemtype);

    public abstract void addCurrency(Currency currency);

    public abstract void addCustomerType(CustomerType customertype);

    public abstract void updateContact(Contact contact);

    public abstract void updateCustomer(Customer customer);

    public abstract void updateItem(Item item);

    public abstract void updateProduct(Product product);

    public abstract void updateProductItem(ProductItem productitem);

    public abstract void updateUom(Uom uom);

    public abstract void updateUomFormula(UomFormula uomformula);

    public abstract void updateStorage(Storage storage);

    public abstract void updateInventory(Inventory inventory);

    public abstract void updateItemType(ItemType itemtype);

    public abstract void updateCurrency(Currency currency);

    public abstract void updateCustomerType(CustomerType customertype);

    public abstract void deleteContact(String s);

    public abstract void deleteContactByExample(ContactExample contactexample);

    public abstract int countItem(ItemExample itemexample);

    public abstract int countUomFormula(UomFormulaExample uomformulaexample);

    public abstract int countProductItem(ProductItemExample productitemexample);

    public abstract int countProduct(ProductExample productexample);

    public abstract int countUom(UomExample uomexample);

    public abstract int countProductionItem(ProductionItemExample productionitemexample);

    public abstract void deleteCustomer(String s);

    public abstract void deleteItem(String s);

    public abstract void deleteProduct(String s);

    public abstract void deleteProductItem(String s);

    public abstract void deleteProductItemsByPrdId(String s);

    public abstract void deleteUom(String s);

    public abstract void deleteUomFormula(String s);

    public abstract void deleteStorage(String s);

    public abstract void deleteStorage(StorageExample storageexample);

    public abstract void deleteInventory(String s);

    public abstract void deleteItemType(String s);

    public abstract void deleteCurrency(String s);

    public abstract void deleteCustomerType(String s);

    public abstract Contact getContact(String s);

    public abstract Customer getCustomer(String s);

    public abstract Item getItem(String s);

    public abstract Product getProduct(String s);

    public abstract ProductItem getProductItem(String s);

    public abstract Uom getUom(String s);

    public abstract UomFormula getUomFormula(String s);

    public abstract Storage getStorage(String s, String s1, String s2);

    public abstract Storage getStorage(String s);

    public abstract Inventory getInventory(String s);

    public abstract ItemType getItemType(String s);

    public abstract Currency getCurrency(String s);

    public abstract CustomerType getCustomerType(String s);

    public abstract PaymentType getPaymentType(String s);

    public abstract List<PaymentType> listPaymentTypes(PaymentTypeExample paymenttypeexample);

    public abstract List<Contact> listContacts(ContactExample contactexample);

    public abstract List<Customer> listCustomers(CustomerExample customerexample);

    public abstract List<Item> listItems(ItemExample itemexample);

    public abstract List<ProductionItem> listProductionItems(ProductionItemExample productionitemexample);

    public abstract List<Product> listProducts(ProductExample productexample);

    public abstract List<ProductItem> listProductItems(ProductItemExample productitemexample);

    public abstract List<Uom> listUoms(UomExample uomexample);

    public abstract List<UomFormula> listUomFormulas(UomFormulaExample uomformulaexample);

    public abstract List<Storage> listStorages(StorageExample storageexample);

    public abstract List<Inventory> listInventorys(InventoryExample inventoryexample);

    public abstract List<ItemType> listItemTypes(ItemTypeExample itemtypeexample);

    public abstract List<Currency> listCurrencys(CurrencyExample currencyexample);

    public abstract List<CustomerType> listCustomerTypes(CustomerTypeExample customertypeexample);

    public abstract void addItemSupplierMapping(ItemSupplierMapping itemsuppliermapping);

    public abstract void deleteItemSupplierMappingByItemId(String s);

    public abstract List<ItemSupplierMapping> listItemSupplierMappings(ItemSupplierMappingExample itemsuppliermappingexample);

    public abstract List<ItemSupplierMapping> listItemSupplierMappingsByItemId(String s);

    public abstract List<ItemSupplierMapping> listItemSupplierMappingsBySupplierId(String s);

    public abstract Map getMetaData();
}
