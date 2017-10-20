package com.wecloud.erp.service;

import java.util.List;

import com.wecloud.erp.model.CheckinRecord;
import com.wecloud.erp.model.CheckinRecordExample;
import com.wecloud.erp.model.CheckinRecordItem;
import com.wecloud.erp.model.CheckinRecordItemExample;
import com.wecloud.erp.model.DeliveryRequest;
import com.wecloud.erp.model.DeliveryRequestExample;
import com.wecloud.erp.model.DeliveryRequestItem;
import com.wecloud.erp.model.DeliveryRequestItemExample;
import com.wecloud.erp.model.OrderRequest;
import com.wecloud.erp.model.OrderRequestExample;
import com.wecloud.erp.model.OrderRequestItem;
import com.wecloud.erp.model.OrderRequestItemExample;
import com.wecloud.erp.model.PO;
import com.wecloud.erp.model.POExample;
import com.wecloud.erp.model.PoItem;
import com.wecloud.erp.model.PoItemExample;
import com.wecloud.erp.model.QcRecord;
import com.wecloud.erp.model.QcRecordExample;
import com.wecloud.erp.model.QcRecordItem;
import com.wecloud.erp.model.QcRecordItemExample;
import com.wecloud.erp.model.QcRequest;
import com.wecloud.erp.model.QcRequestExample;
import com.wecloud.erp.model.QcRequestItem;
import com.wecloud.erp.model.QcRequestItemExample;

public interface PoManager
{

    public abstract void addCheckinRecordItem(CheckinRecordItem checkinrecorditem);

    public abstract void addCheckinRecord(CheckinRecord checkinrecord);

    public abstract void addQcRecordItem(QcRecordItem qcrecorditem);

    public abstract void addQcRecord(QcRecord qcrecord);

    public abstract void addQcRequestItem(QcRequestItem qcrequestitem);

    public abstract void addQcRequest(QcRequest qcrequest);

    public abstract void addPoItem(PoItem poitem);

    public abstract void addPO(PO po);

    public abstract void addOrderRequestItem(OrderRequestItem orderrequestitem);

    public abstract void addOrderRequest(OrderRequest orderrequest);

    public abstract void deleteCheckinRecordItem(String s);

    public abstract void deleteCheckinRecord(String s);

    public abstract void deleteQcRecordItem(String s);

    public abstract void deleteQcRecord(String s);

    public abstract void deleteQcRequestItem(String s);

    public abstract void deleteQcRequest(String s);

    public abstract void deletePoItem(String s);

    public abstract void deletePoItemByExample(PoItemExample poitemexample);

    public abstract void deletePO(String s);

    public abstract void deleteOrderRequestItem(String s);

    public abstract void deleteOrderRequestItemByExample(OrderRequestItemExample orderrequestitemexample);

    public abstract void deleteOrderRequest(String s);

    public abstract CheckinRecordItem getCheckinRecordItem(String s);

    public abstract CheckinRecord getCheckinRecord(String s);

    public abstract QcRecordItem getQcRecordItem(String s);

    public abstract QcRecord getQcRecord(String s);

    public abstract QcRequestItem getQcRequestItem(String s);

    public abstract QcRequest getQcRequest(String s);

    public abstract PoItem getPoItem(String s);

    public abstract PO getPO(String s);

    public abstract OrderRequestItem getOrderRequestItem(String s);

    public abstract OrderRequest getOrderRequest(String s);

    public abstract void updateCheckinRecordItem(CheckinRecordItem checkinrecorditem);

    public abstract void updateCheckinRecord(CheckinRecord checkinrecord);

    public abstract void updateQcRecordItem(QcRecordItem qcrecorditem);

    public abstract void updateQcRecord(QcRecord qcrecord);

    public abstract void updateQcRequestItem(QcRequestItem qcrequestitem);

    public abstract void updateQcRequest(QcRequest qcrequest);

    public abstract void updatePoItem(PoItem poitem);

    public abstract void updatePO(PO po);

    public abstract void updateOrderRequestItem(OrderRequestItem orderrequestitem);

    public abstract void updateOrderRequest(OrderRequest orderrequest);

    public abstract List<CheckinRecordItem> listCheckinRecordItems(CheckinRecordItemExample checkinrecorditemexample);

    public abstract List<CheckinRecord> listCheckinRecords(CheckinRecordExample checkinrecordexample);

    public abstract List<QcRecordItem> listQcRecordItems(QcRecordItemExample qcrecorditemexample);

    public abstract List<QcRecord> listQcRecords(QcRecordExample qcrecordexample);

    public abstract List<QcRequestItem> listQcRequestItems(QcRequestItemExample qcrequestitemexample);

    public abstract List<QcRequest> listQcRequests(QcRequestExample qcrequestexample);

    public abstract List<PoItem> listPoItems(PoItemExample poitemexample);

    public abstract List<PO> listPOs(POExample poexample);

    public abstract List<OrderRequestItem> listOrderRequestItems(OrderRequestItemExample orderrequestitemexample);

    public abstract List<OrderRequest> listOrderRequests(OrderRequestExample orderrequestexample);

    public abstract void addDeliveryRequest(DeliveryRequest deliveryrequest);

    public abstract void addDeliveryRequestItem(DeliveryRequestItem deliveryrequestitem);

    public abstract void deleteDeliveryRequest(String s);

    public abstract void deleteDeliveryRequestItem(String s);

    public abstract DeliveryRequest getDeliveryRequest(String s);

    public abstract DeliveryRequestItem getDeliveryRequestItem(String s);

    public abstract void updateDeliveryRequest(DeliveryRequest deliveryrequest);

    public abstract void updateDeliveryRequestItem(DeliveryRequestItem deliveryrequestitem);

    public abstract List<DeliveryRequest> listDeliveryRequests(DeliveryRequestExample deliveryrequestexample);

    public abstract List<DeliveryRequestItem> listDeliveryRequestItems(DeliveryRequestItemExample deliveryrequestitemexample);

    public abstract int countCheckinRecordItem(CheckinRecordItemExample checkinrecorditemexample);

    public abstract int countCheckinRecord(CheckinRecordExample checkinrecordexample);

    public abstract int countQcRecordItem(QcRecordItemExample qcrecorditemexample);

    public abstract int countQcRecord(QcRecordExample qcrecordexample);

    public abstract int countQcRequestItem(QcRequestItemExample qcrequestitemexample);

    public abstract int countQcRequest(QcRequestExample qcrequestexample);

    public abstract int countPoItem(PoItemExample poitemexample);

    public abstract int countPO(POExample poexample);

    public abstract int countOrderRequestItem(OrderRequestItemExample orderrequestitemexample);

    public abstract int countOrderRequest(OrderRequestExample orderrequestexample);

    public abstract int countDeliveryRequest(DeliveryRequestExample deliveryrequestexample);

    public abstract int countDeliveryRequestItem(DeliveryRequestItemExample deliveryrequestitemexample);
}
