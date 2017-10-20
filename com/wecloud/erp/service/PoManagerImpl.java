package com.wecloud.erp.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

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
import com.wecloud.erp.persistence.dao.CheckinRecordItemMapper;
import com.wecloud.erp.persistence.dao.CheckinRecordMapper;
import com.wecloud.erp.persistence.dao.DeliveryRequestItemMapper;
import com.wecloud.erp.persistence.dao.DeliveryRequestMapper;
import com.wecloud.erp.persistence.dao.OrderRequestItemMapper;
import com.wecloud.erp.persistence.dao.OrderRequestMapper;
import com.wecloud.erp.persistence.dao.POMapper;
import com.wecloud.erp.persistence.dao.PoItemMapper;
import com.wecloud.erp.persistence.dao.QcRecordItemMapper;
import com.wecloud.erp.persistence.dao.QcRecordMapper;
import com.wecloud.erp.persistence.dao.QcRequestItemMapper;
import com.wecloud.erp.persistence.dao.QcRequestMapper;

// Referenced classes of package com.wecloud.erp.service:
//            PoManager

@Service(value="poManager")
public class PoManagerImpl
    implements PoManager
{

    public PoManagerImpl()
    {
    }

    public void addCheckinRecordItem(CheckinRecordItem checkinRecordItem)
    {
        checkinRecordItemDao.insert(checkinRecordItem);
    }

    public void addCheckinRecord(CheckinRecord checkinRecord)
    {
        checkinRecordDao.insert(checkinRecord);
    }

    public void addQcRecordItem(QcRecordItem qcRecordItem)
    {
        qcRecordItemDao.insert(qcRecordItem);
    }

    public void addQcRecord(QcRecord qcRecord)
    {
        qcRecordDao.insert(qcRecord);
    }

    public void addQcRequestItem(QcRequestItem qcRequestItem)
    {
        qcRequestItemDao.insert(qcRequestItem);
    }

    public void addQcRequest(QcRequest qcRequest)
    {
        qcRequestDao.insert(qcRequest);
    }

    public void addPoItem(PoItem poItem)
    {
        poItemDao.insert(poItem);
    }

    public void addPO(PO pO)
    {
        pODao.insert(pO);
    }

    public void addOrderRequestItem(OrderRequestItem orderRequestItem)
    {
        orderRequestItemDao.insert(orderRequestItem);
    }

    public void addOrderRequest(OrderRequest orderRequest)
    {
        orderRequestDao.insert(orderRequest);
    }

    public void deleteCheckinRecordItem(String id)
    {
        checkinRecordItemDao.deleteByPrimaryKey(id);
    }

    public void deleteCheckinRecord(String id)
    {
        checkinRecordDao.deleteByPrimaryKey(id);
    }

    public void deleteQcRecordItem(String id)
    {
        qcRecordItemDao.deleteByPrimaryKey(id);
    }

    public void deleteQcRecord(String id)
    {
        qcRecordDao.deleteByPrimaryKey(id);
    }

    public void deleteQcRequestItem(String id)
    {
        qcRequestItemDao.deleteByPrimaryKey(id);
    }

    public void deleteQcRequest(String id)
    {
        qcRequestDao.deleteByPrimaryKey(id);
    }

    public void deletePoItem(String id)
    {
        poItemDao.deleteByPrimaryKey(id);
    }

    public void deletePoItemByExample(PoItemExample example)
    {
        poItemDao.deleteByExample(example);
    }

    public void deletePO(String id)
    {
        pODao.deleteByPrimaryKey(id);
    }

    public void deleteOrderRequestItem(String id)
    {
        orderRequestItemDao.deleteByPrimaryKey(id);
    }

    public void deleteOrderRequestItemByExample(OrderRequestItemExample example)
    {
        orderRequestItemDao.deleteByExample(example);
    }

    public void deleteOrderRequest(String id)
    {
        orderRequestDao.deleteByPrimaryKey(id);
    }

    public CheckinRecordItem getCheckinRecordItem(String id)
    {
        return checkinRecordItemDao.selectByPrimaryKey(id);
    }

    public CheckinRecord getCheckinRecord(String id)
    {
        return checkinRecordDao.selectByPrimaryKey(id);
    }

    public QcRecordItem getQcRecordItem(String id)
    {
        return qcRecordItemDao.selectByPrimaryKey(id);
    }

    public QcRecord getQcRecord(String id)
    {
        return qcRecordDao.selectByPrimaryKey(id);
    }

    public QcRequestItem getQcRequestItem(String id)
    {
        return qcRequestItemDao.selectByPrimaryKey(id);
    }

    public QcRequest getQcRequest(String id)
    {
        return qcRequestDao.selectByPrimaryKey(id);
    }

    public PoItem getPoItem(String id)
    {
        return poItemDao.selectByPrimaryKey(id);
    }

    public PO getPO(String id)
    {
        return pODao.selectByPrimaryKey(id);
    }

    public OrderRequestItem getOrderRequestItem(String id)
    {
        return orderRequestItemDao.selectByPrimaryKey(id);
    }

    public OrderRequest getOrderRequest(String id)
    {
        return orderRequestDao.selectByPrimaryKey(id);
    }

    public void updateCheckinRecordItem(CheckinRecordItem checkinRecordItem)
    {
        checkinRecordItemDao.updateByPrimaryKey(checkinRecordItem);
    }

    public void updateCheckinRecord(CheckinRecord checkinRecord)
    {
        checkinRecordDao.updateByPrimaryKeyWithBLOBs(checkinRecord);
    }

    public void updateQcRecordItem(QcRecordItem qcRecordItem)
    {
        qcRecordItemDao.updateByPrimaryKey(qcRecordItem);
    }

    public void updateQcRecord(QcRecord qcRecord)
    {
        qcRecordDao.updateByPrimaryKeyWithBLOBs(qcRecord);
    }

    public void updateQcRequestItem(QcRequestItem qcRequestItem)
    {
        qcRequestItemDao.updateByPrimaryKey(qcRequestItem);
    }

    public void updateQcRequest(QcRequest qcRequest)
    {
        qcRequestDao.updateByPrimaryKeyWithBLOBs(qcRequest);
    }

    public void updatePoItem(PoItem poItem)
    {
        poItemDao.updateByPrimaryKey(poItem);
    }

    public void updatePO(PO pO)
    {
        pODao.updateByPrimaryKeyWithBLOBs(pO);
    }

    public void updateOrderRequestItem(OrderRequestItem orderRequestItem)
    {
        orderRequestItemDao.updateByPrimaryKey(orderRequestItem);
    }

    public void updateOrderRequest(OrderRequest orderRequest)
    {
        orderRequestDao.updateByPrimaryKey(orderRequest);
    }

    public List listCheckinRecordItems(CheckinRecordItemExample example)
    {
        return checkinRecordItemDao.selectByExample(example);
    }

    public List listCheckinRecords(CheckinRecordExample example)
    {
        return checkinRecordDao.selectByExampleWithBLOBs(example);
    }

    public List listQcRecordItems(QcRecordItemExample example)
    {
        return qcRecordItemDao.selectByExample(example);
    }

    public List listQcRecords(QcRecordExample example)
    {
        return qcRecordDao.selectByExampleWithBLOBs(example);
    }

    public List listQcRequestItems(QcRequestItemExample example)
    {
        return qcRequestItemDao.selectByExample(example);
    }

    public List listQcRequests(QcRequestExample example)
    {
        return qcRequestDao.selectByExampleWithBLOBs(example);
    }

    public List listPoItems(PoItemExample example)
    {
        return poItemDao.selectByExample(example);
    }

    public List listPOs(POExample example)
    {
        return pODao.selectByExampleWithBLOBs(example);
    }

    public List listOrderRequestItems(OrderRequestItemExample example)
    {
        return orderRequestItemDao.selectByExample(example);
    }

    public List listOrderRequests(OrderRequestExample example)
    {
        return orderRequestDao.selectByExampleWithBLOBs(example);
    }

    public void addDeliveryRequest(DeliveryRequest deliveryRequest)
    {
        deliveryRequestDao.insert(deliveryRequest);
    }

    public void addDeliveryRequestItem(DeliveryRequestItem deliveryRequestItem)
    {
        deliveryRequestItemDao.insert(deliveryRequestItem);
    }

    public void deleteDeliveryRequest(String id)
    {
        deliveryRequestDao.deleteByPrimaryKey(id);
    }

    public void deleteDeliveryRequestItem(String id)
    {
        deliveryRequestItemDao.deleteByPrimaryKey(id);
    }

    public DeliveryRequest getDeliveryRequest(String id)
    {
        return deliveryRequestDao.selectByPrimaryKey(id);
    }

    public DeliveryRequestItem getDeliveryRequestItem(String id)
    {
        return deliveryRequestItemDao.selectByPrimaryKey(id);
    }

    public void updateDeliveryRequest(DeliveryRequest deliveryRequest)
    {
        deliveryRequestDao.updateByPrimaryKeyWithBLOBs(deliveryRequest);
    }

    public void updateDeliveryRequestItem(DeliveryRequestItem deliveryRequestItem)
    {
        deliveryRequestItemDao.updateByPrimaryKey(deliveryRequestItem);
    }

    public List listDeliveryRequests(DeliveryRequestExample example)
    {
        return deliveryRequestDao.selectByExampleWithBLOBs(example);
    }

    public List listDeliveryRequestItems(DeliveryRequestItemExample example)
    {
        return deliveryRequestItemDao.selectByExample(example);
    }

    public int countCheckinRecordItem(CheckinRecordItemExample example)
    {
        return checkinRecordItemDao.countByExample(example);
    }

    public int countCheckinRecord(CheckinRecordExample example)
    {
        return checkinRecordDao.countByExample(example);
    }

    public int countQcRecordItem(QcRecordItemExample example)
    {
        return qcRecordItemDao.countByExample(example);
    }

    public int countQcRecord(QcRecordExample example)
    {
        return qcRecordDao.countByExample(example);
    }

    public int countQcRequestItem(QcRequestItemExample example)
    {
        return qcRequestItemDao.countByExample(example);
    }

    public int countQcRequest(QcRequestExample example)
    {
        return qcRequestDao.countByExample(example);
    }

    public int countPoItem(PoItemExample example)
    {
        return poItemDao.countByExample(example);
    }

    public int countPO(POExample example)
    {
        return pODao.countByExample(example);
    }

    public int countOrderRequestItem(OrderRequestItemExample example)
    {
        return orderRequestItemDao.countByExample(example);
    }

    public int countOrderRequest(OrderRequestExample example)
    {
        return orderRequestDao.countByExample(example);
    }

    public int countDeliveryRequest(DeliveryRequestExample example)
    {
        return deliveryRequestDao.countByExample(example);
    }

    public int countDeliveryRequestItem(DeliveryRequestItemExample example)
    {
        return deliveryRequestItemDao.countByExample(example);
    }

    @Resource
    private CheckinRecordItemMapper checkinRecordItemDao;
    @Resource
    private CheckinRecordMapper checkinRecordDao;
    @Resource
    private QcRecordItemMapper qcRecordItemDao;
    @Resource
    private QcRecordMapper qcRecordDao;
    @Resource
    private QcRequestItemMapper qcRequestItemDao;
    @Resource
    private QcRequestMapper qcRequestDao;
    @Resource
    private PoItemMapper poItemDao;
    @Resource
    private POMapper pODao;
    @Resource
    private OrderRequestItemMapper orderRequestItemDao;
    @Resource
    private OrderRequestMapper orderRequestDao;
    @Resource
    private DeliveryRequestMapper deliveryRequestDao;
    @Resource
    private DeliveryRequestItemMapper deliveryRequestItemDao;
}
