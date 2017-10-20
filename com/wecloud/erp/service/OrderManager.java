package com.wecloud.erp.service;

import java.util.List;

import com.wecloud.erp.model.CheckoutRecord;
import com.wecloud.erp.model.CheckoutRecordExample;
import com.wecloud.erp.model.CheckoutRecordItem;
import com.wecloud.erp.model.CheckoutRecordItemExample;
import com.wecloud.erp.model.CheckoutReqItem;
import com.wecloud.erp.model.CheckoutReqItemExample;
import com.wecloud.erp.model.CheckoutRequest;
import com.wecloud.erp.model.CheckoutRequestExample;
import com.wecloud.erp.model.CustomerViewExample;
import com.wecloud.erp.model.PoReturn;
import com.wecloud.erp.model.PoReturnExample;
import com.wecloud.erp.model.PoReturnItem;
import com.wecloud.erp.model.PoReturnItemExample;
import com.wecloud.erp.model.ProduceNotice;
import com.wecloud.erp.model.ProduceNoticeExample;
import com.wecloud.erp.model.ProduceRequest;
import com.wecloud.erp.model.ProduceRequestExample;
import com.wecloud.erp.model.ProduceRequestItem;
import com.wecloud.erp.model.ProduceRequestItemExample;
import com.wecloud.erp.model.ProduceRequestViewExample;
import com.wecloud.erp.model.SalesOrder;
import com.wecloud.erp.model.SalesOrderExample;
import com.wecloud.erp.model.Sequence;
import com.wecloud.erp.model.SequenceExample;
import com.wecloud.erp.model.SoItem;
import com.wecloud.erp.model.SoItemExample;
import com.wecloud.erp.model.SoReturn;
import com.wecloud.erp.model.SoReturnExample;
import com.wecloud.erp.model.SoReturnItem;
import com.wecloud.erp.model.SoReturnItemExample;

public interface OrderManager
{

    public abstract void addSalesOrder(SalesOrder salesorder);

    public abstract void addSoItem(SoItem soitem);

    public abstract void addProduceRequest(ProduceRequest producerequest);

    public abstract void addProduceRequestItem(ProduceRequestItem producerequestitem);

    public abstract void deleteSalesOrder(String s);

    public abstract void deleteSoItem(String s);

    public abstract void deleteSoItem(SoItemExample soitemexample);

    public abstract void deleteProduceRequest(String s);

    public abstract void deleteProduceRequestItem(String s);

    public abstract SalesOrder getSalesOrder(String s);

    public abstract SoItem getSoItem(String s);

    public abstract ProduceRequest getProduceRequest(String s);

    public abstract ProduceRequestItem getProduceRequestItem(String s);

    public abstract int countSalesOrder(SalesOrderExample salesorderexample);

    public abstract void updateSalesOrder(SalesOrder salesorder);

    public abstract void updateSoItem(SoItem soitem);

    public abstract void updateProduceRequest(ProduceRequest producerequest);

    public abstract void updateProduceRequestItem(ProduceRequestItem producerequestitem);

    public abstract List<SalesOrder> listSalesOrders(SalesOrderExample salesorderexample);

    public abstract List<SoItem> listSoItems(SoItemExample soitemexample);

    public abstract List<ProduceRequest> listProduceRequests(ProduceRequestViewExample producerequestviewexample);

    public abstract List<ProduceRequestItem> listProduceRequestItems(ProduceRequestItemExample producerequestitemexample);

    public abstract int countProduceRequestItem(ProduceRequestItemExample producerequestitemexample);

    public abstract int countSoItem(SoItemExample soitemexample);

    public abstract Sequence getSequence(String s);

    public abstract void addCheckoutRequest(CheckoutRequest checkoutrequest);

    public abstract void addCheckoutReqItem(CheckoutReqItem checkoutreqitem);

    public abstract void addCheckoutRecord(CheckoutRecord checkoutrecord);

    public abstract void addCheckoutRecordItem(CheckoutRecordItem checkoutrecorditem);

    public abstract void deleteCheckoutRequest(String s);

    public abstract void deleteCheckoutReqItem(String s);

    public abstract void deleteCheckoutRecord(String s);

    public abstract void deleteCheckoutRecordItem(String s);

    public abstract CheckoutRequest getCheckoutRequest(String s);

    public abstract CheckoutReqItem getCheckoutReqItem(String s);

    public abstract CheckoutRecord getCheckoutRecord(String s);

    public abstract CheckoutRecordItem getCheckoutRecordItem(String s);

    public abstract void updateCheckoutRequest(CheckoutRequest checkoutrequest);

    public abstract void updateCheckoutReqItem(CheckoutReqItem checkoutreqitem);

    public abstract void updateCheckoutRecord(CheckoutRecord checkoutrecord);

    public abstract void updateCheckoutRecordItem(CheckoutRecordItem checkoutrecorditem);

    public abstract List<CheckoutRequest> listCheckoutRequests(CheckoutRequestExample checkoutrequestexample);

    public abstract List<CheckoutReqItem> listCheckoutReqItems(CheckoutReqItemExample checkoutreqitemexample);

    public abstract List<CheckoutRecord> listCheckoutRecords(CheckoutRecordExample checkoutrecordexample);

    public abstract List<CheckoutRecordItem> listCheckoutRecordItems(CheckoutRecordItemExample checkoutrecorditemexample);

    public abstract int countProduceRequest(ProduceRequestExample producerequestexample);

    public abstract int countProduceRequestView(ProduceRequestViewExample producerequestviewexample);

    public abstract int countCustomerView(CustomerViewExample customerviewexample);

    public abstract int countSequence(SequenceExample sequenceexample);

    public abstract int countCheckoutRecord(CheckoutRecordExample checkoutrecordexample);

    public abstract int countCheckoutRecordItem(CheckoutRecordItemExample checkoutrecorditemexample);

    public abstract int countCheckoutRequest(CheckoutRequestExample checkoutrequestexample);

    public abstract int countCheckoutReqItem(CheckoutReqItemExample checkoutreqitemexample);

    public abstract void addSoReturn(SoReturn soreturn);

    public abstract void addSoReturnItem(SoReturnItem soreturnitem);

    public abstract void addPoReturn(PoReturn poreturn);

    public abstract void addPoReturnItem(PoReturnItem poreturnitem);

    public abstract void deleteSoReturn(String s);

    public abstract void deleteSoReturnItem(String s);

    public abstract void deletePoReturn(String s);

    public abstract void deletePoReturnItem(String s);

    public abstract SoReturn getSoReturn(String s);

    public abstract SoReturnItem getSoReturnItem(String s);

    public abstract PoReturn getPoReturn(String s);

    public abstract PoReturnItem getPoReturnItem(String s);

    public abstract void updateSoReturn(SoReturn soreturn);

    public abstract void updateSoReturnItem(SoReturnItem soreturnitem);

    public abstract void updatePoReturn(PoReturn poreturn);

    public abstract void updatePoReturnItem(PoReturnItem poreturnitem);

    public abstract List<SoReturn> listSoReturns(SoReturnExample soreturnexample);

    public abstract List<SoReturnItem> listSoReturnItems(SoReturnItemExample soreturnitemexample);

    public abstract List<PoReturn> listPoReturns(PoReturnExample poreturnexample);

    public abstract List<PoReturnItem> listPoReturnItems(PoReturnItemExample poreturnitemexample);

    public abstract int countPoReturn(PoReturnExample poreturnexample);

    public abstract int countSoReturn(SoReturnExample soreturnexample);

    public abstract void addProduceNotice(ProduceNotice producenotice);

    public abstract void deleteProduceNotice(String s);

    public abstract ProduceNotice getProduceNotice(String s);

    public abstract void updateProduceNotice(ProduceNotice producenotice);

    public abstract List<ProduceNotice> listProduceNotices(ProduceNoticeExample producenoticeexample);

    public abstract int countProduceNotice(ProduceNoticeExample producenoticeexample);
}
