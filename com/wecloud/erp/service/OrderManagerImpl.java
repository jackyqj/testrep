package com.wecloud.erp.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.wecloud.erp.persistence.dao.CheckoutRecordItemMapper;
import com.wecloud.erp.persistence.dao.CheckoutRecordMapper;
import com.wecloud.erp.persistence.dao.CheckoutReqItemMapper;
import com.wecloud.erp.persistence.dao.CheckoutRequestMapper;
import com.wecloud.erp.persistence.dao.CustomerViewMapper;
import com.wecloud.erp.persistence.dao.PoReturnItemMapper;
import com.wecloud.erp.persistence.dao.PoReturnMapper;
import com.wecloud.erp.persistence.dao.ProduceNoticeMapper;
import com.wecloud.erp.persistence.dao.ProduceRequestItemMapper;
import com.wecloud.erp.persistence.dao.ProduceRequestMapper;
import com.wecloud.erp.persistence.dao.ProduceRequestViewMapper;
import com.wecloud.erp.persistence.dao.SalesOrderMapper;
import com.wecloud.erp.persistence.dao.SequenceMapper;
import com.wecloud.erp.persistence.dao.SoItemMapper;
import com.wecloud.erp.persistence.dao.SoReturnItemMapper;
import com.wecloud.erp.persistence.dao.SoReturnMapper;
import com.wecloud.erp.utils.UUID;

// Referenced classes of package com.wecloud.erp.service:
//            OrderManager

@Service(value="orderManager")
public class OrderManagerImpl
    implements OrderManager
{

    public OrderManagerImpl()
    {
    }

    @Transactional
    public void addSalesOrder(SalesOrder salesOrder)
    {
        salesOrderDao.insert(salesOrder);
    }

    @Transactional
    public void addSoItem(SoItem soItem)
    {
        soItemDao.insert(soItem);
    }

    @Transactional
    public void addProduceRequest(ProduceRequest produceRequest)
    {
        produceRequestDao.insert(produceRequest);
    }

    @Transactional
    public void addProduceRequestItem(ProduceRequestItem produceRequestItem)
    {
        produceRequestItemDao.insert(produceRequestItem);
    }

    @Transactional
    public void deleteSalesOrder(String id)
    {
        salesOrderDao.deleteByPrimaryKey(id);
    }

    @Transactional
    public void deleteSoItem(String id)
    {
        soItemDao.deleteByPrimaryKey(id);
    }

    @Transactional
    public void deleteSoItem(SoItemExample example)
    {
        soItemDao.deleteByExample(example);
    }

    @Transactional
    public void deleteProduceRequest(String id)
    {
        produceRequestDao.deleteByPrimaryKey(id);
    }

    @Transactional
    public void deleteProduceRequestItem(String id)
    {
        produceRequestItemDao.deleteByPrimaryKey(id);
    }

    public SalesOrder getSalesOrder(String id)
    {
        return salesOrderDao.selectByPrimaryKey(id);
    }

    public SoItem getSoItem(String id)
    {
        return soItemDao.selectByPrimaryKey(id);
    }

    public ProduceRequest getProduceRequest(String id)
    {
        return produceRequestDao.selectByPrimaryKey(id);
    }

    public ProduceRequestItem getProduceRequestItem(String id)
    {
        return produceRequestItemDao.selectByPrimaryKey(id);
    }

    @Transactional
    public void updateSalesOrder(SalesOrder salesOrder)
    {
        salesOrderDao.updateByPrimaryKeyWithBLOBs(salesOrder);
    }

    @Transactional
    public void updateSoItem(SoItem soItem)
    {
        soItemDao.updateByPrimaryKey(soItem);
    }

    @Transactional
    public void updateProduceRequest(ProduceRequest produceRequest)
    {
        produceRequestDao.updateByPrimaryKeyWithBLOBs(produceRequest);
    }

    @Transactional
    public void updateProduceRequestItem(ProduceRequestItem produceRequestItem)
    {
        produceRequestItemDao.updateByPrimaryKey(produceRequestItem);
    }

    public List listSalesOrders(SalesOrderExample example)
    {
        return salesOrderDao.selectByExampleWithBLOBs(example);
    }

    public void addCheckoutRequest(CheckoutRequest checkoutRequest)
    {
        checkoutRequestDao.insert(checkoutRequest);
    }

    public void addCheckoutReqItem(CheckoutReqItem checkoutReqItem)
    {
        checkoutReqItemDao.insert(checkoutReqItem);
    }

    public void addCheckoutRecord(CheckoutRecord checkoutRecord)
    {
        checkoutRecordDao.insert(checkoutRecord);
    }

    public void addCheckoutRecordItem(CheckoutRecordItem checkoutRecordItem)
    {
        checkoutRecordItemDao.insert(checkoutRecordItem);
    }

    public void deleteCheckoutRequest(String id)
    {
        checkoutRequestDao.deleteByPrimaryKey(id);
    }

    public void deleteCheckoutReqItem(String id)
    {
        checkoutReqItemDao.deleteByPrimaryKey(id);
    }

    public void deleteCheckoutRecord(String id)
    {
        checkoutRecordDao.deleteByPrimaryKey(id);
    }

    public void deleteCheckoutRecordItem(String id)
    {
        checkoutRecordItemDao.deleteByPrimaryKey(id);
    }

    public CheckoutRequest getCheckoutRequest(String id)
    {
        return checkoutRequestDao.selectByPrimaryKey(id);
    }

    public CheckoutReqItem getCheckoutReqItem(String id)
    {
        return checkoutReqItemDao.selectByPrimaryKey(id);
    }

    public CheckoutRecord getCheckoutRecord(String id)
    {
        return checkoutRecordDao.selectByPrimaryKey(id);
    }

    public CheckoutRecordItem getCheckoutRecordItem(String id)
    {
        return checkoutRecordItemDao.selectByPrimaryKey(id);
    }

    public void updateCheckoutRequest(CheckoutRequest checkoutRequest)
    {
        checkoutRequestDao.updateByPrimaryKeyWithBLOBs(checkoutRequest);
    }

    public void updateCheckoutReqItem(CheckoutReqItem checkoutReqItem)
    {
        checkoutReqItemDao.updateByPrimaryKey(checkoutReqItem);
    }

    public void updateCheckoutRecord(CheckoutRecord checkoutRecord)
    {
        checkoutRecordDao.updateByPrimaryKeyWithBLOBs(checkoutRecord);
    }

    public void updateCheckoutRecordItem(CheckoutRecordItem checkoutRecordItem)
    {
        checkoutRecordItemDao.updateByPrimaryKey(checkoutRecordItem);
    }

    public List listCheckoutRequests(CheckoutRequestExample example)
    {
        return checkoutRequestDao.selectByExampleWithBLOBs(example);
    }

    public List listCheckoutReqItems(CheckoutReqItemExample example)
    {
        return checkoutReqItemDao.selectByExample(example);
    }

    public List listCheckoutRecords(CheckoutRecordExample example)
    {
        return checkoutRecordDao.selectByExampleWithBLOBs(example);
    }

    public List listCheckoutRecordItems(CheckoutRecordItemExample example)
    {
        return checkoutRecordItemDao.selectByExample(example);
    }

    public int countSalesOrder(SalesOrderExample example)
    {
        return salesOrderDao.countByExample(example);
    }

    public List listSoItems(SoItemExample example)
    {
        return soItemDao.selectByExampleWithBLOBs(example);
    }

    public int countSoItem(SoItemExample example)
    {
        return soItemDao.countByExample(example);
    }

    public List listProduceRequests(ProduceRequestViewExample example)
    {
        return produceRequestViewDao.selectByExampleWithBLOBs(example);
    }

    public List listProduceRequestItems(ProduceRequestItemExample example)
    {
        return produceRequestItemDao.selectByExample(example);
    }

    public int countProduceRequestItem(ProduceRequestItemExample example)
    {
        return produceRequestItemDao.countByExample(example);
    }

    @Transactional
    public Sequence getSequence(String type)
    {
        SequenceExample query = new SequenceExample();
        query.createCriteria().andTypeEqualTo(type);
        query.setOrderByClause(" SEQ DESC");
        List seqs = sequenceDao.selectByExample(query);
        Sequence result = new Sequence();
        result.setSeq(Integer.valueOf(0x3b9ac9ff));
        if(seqs != null && seqs.size() > 0)
        {
            Sequence seq = (Sequence)seqs.get(0);
            result.setSeq(seq.getSeq());
            result.setPattern(seq.getPattern());
            if(seq.getSeq().intValue() > 0x3b9ac9fe)
                seq.setSeq(Integer.valueOf(1));
            else
                seq.setSeq(Integer.valueOf(seq.getSeq().intValue() + 1));
            sequenceDao.updateByPrimaryKey(seq);
        } else
        {
            result.setSeq(Integer.valueOf(2));
            result.setId(UUID.get());
            result.setType(type);
            result.setPattern((new StringBuilder(String.valueOf(type))).append("-{DATE}-{SEQ}").toString());
            sequenceDao.insert(result);
            result.setSeq(Integer.valueOf(1));
        }
        return result;
    }

    public int countProduceRequest(ProduceRequestExample example)
    {
        return produceRequestDao.countByExample(example);
    }

    public int countProduceRequestView(ProduceRequestViewExample example)
    {
        return produceRequestViewDao.countByExample(example);
    }

    public int countCustomerView(CustomerViewExample example)
    {
        return customerViewDao.countByExample(example);
    }

    public int countSequence(SequenceExample example)
    {
        return sequenceDao.countByExample(example);
    }

    public int countCheckoutRecord(CheckoutRecordExample example)
    {
        return checkoutRecordDao.countByExample(example);
    }

    public int countCheckoutRecordItem(CheckoutRecordItemExample example)
    {
        return checkoutRecordItemDao.countByExample(example);
    }

    public int countCheckoutRequest(CheckoutRequestExample example)
    {
        return checkoutRequestDao.countByExample(example);
    }

    public int countCheckoutReqItem(CheckoutReqItemExample example)
    {
        return checkoutReqItemDao.countByExample(example);
    }

    public void addSoReturn(SoReturn soReturn)
    {
        soReturnDao.insert(soReturn);
    }

    public void addSoReturnItem(SoReturnItem soReturnItem)
    {
        soReturnItemDao.insert(soReturnItem);
    }

    public void addPoReturn(PoReturn poReturn)
    {
        poReturnDao.insert(poReturn);
    }

    public void addPoReturnItem(PoReturnItem poReturnItem)
    {
        poReturnItemDao.insert(poReturnItem);
    }

    public void deleteSoReturn(String id)
    {
        soReturnDao.deleteByPrimaryKey(id);
    }

    public void deleteSoReturnItem(String id)
    {
        soReturnItemDao.deleteByPrimaryKey(id);
    }

    public void deletePoReturn(String id)
    {
        poReturnDao.deleteByPrimaryKey(id);
    }

    public void deletePoReturnItem(String id)
    {
        poReturnItemDao.deleteByPrimaryKey(id);
    }

    public SoReturn getSoReturn(String id)
    {
        return soReturnDao.selectByPrimaryKey(id);
    }

    public SoReturnItem getSoReturnItem(String id)
    {
        return soReturnItemDao.selectByPrimaryKey(id);
    }

    public PoReturn getPoReturn(String id)
    {
        return poReturnDao.selectByPrimaryKey(id);
    }

    public PoReturnItem getPoReturnItem(String id)
    {
        return poReturnItemDao.selectByPrimaryKey(id);
    }

    public void updateSoReturn(SoReturn soReturn)
    {
        soReturnDao.updateByPrimaryKeyWithBLOBs(soReturn);
    }

    public void updateSoReturnItem(SoReturnItem soReturnItem)
    {
        soReturnItemDao.updateByPrimaryKey(soReturnItem);
    }

    public void updatePoReturn(PoReturn poReturn)
    {
        poReturnDao.updateByPrimaryKeyWithBLOBs(poReturn);
    }

    public void updatePoReturnItem(PoReturnItem poReturnItem)
    {
        poReturnItemDao.updateByPrimaryKey(poReturnItem);
    }

    public List listSoReturns(SoReturnExample example)
    {
        return soReturnDao.selectByExampleWithBLOBs(example);
    }

    public List listSoReturnItems(SoReturnItemExample example)
    {
        return soReturnItemDao.selectByExample(example);
    }

    public List listPoReturns(PoReturnExample example)
    {
        return poReturnDao.selectByExampleWithBLOBs(example);
    }

    public List listPoReturnItems(PoReturnItemExample example)
    {
        return poReturnItemDao.selectByExample(example);
    }

    public int countPoReturn(PoReturnExample example)
    {
        return poReturnDao.countByExample(example);
    }

    public int countSoReturn(SoReturnExample example)
    {
        return soReturnDao.countByExample(example);
    }

    public void addProduceNotice(ProduceNotice produceNotice)
    {
        produceNoticeDao.insert(produceNotice);
    }

    public void deleteProduceNotice(String id)
    {
        produceNoticeDao.deleteByPrimaryKey(id);
    }

    public ProduceNotice getProduceNotice(String id)
    {
        return produceNoticeDao.selectByPrimaryKey(id);
    }

    public void updateProduceNotice(ProduceNotice produceNotice)
    {
        produceNoticeDao.updateByPrimaryKeyWithBLOBs(produceNotice);
    }

    public List listProduceNotices(ProduceNoticeExample example)
    {
        return produceNoticeDao.selectByExampleWithBLOBs(example);
    }

    public int countProduceNotice(ProduceNoticeExample example)
    {
        return produceNoticeDao.countByExample(example);
    }

    @Resource
    private SalesOrderMapper salesOrderDao;
    @Resource
    private SoItemMapper soItemDao;
    @Resource
    private ProduceRequestMapper produceRequestDao;
    @Resource
    private ProduceRequestViewMapper produceRequestViewDao;
    @Resource
    private ProduceRequestItemMapper produceRequestItemDao;
    @Resource
    private CustomerViewMapper customerViewDao;
    @Resource
    private SequenceMapper sequenceDao;
    @Resource
    private CheckoutRecordMapper checkoutRecordDao;
    @Resource
    private CheckoutRecordItemMapper checkoutRecordItemDao;
    @Resource
    private CheckoutRequestMapper checkoutRequestDao;
    @Resource
    private CheckoutReqItemMapper checkoutReqItemDao;
    @Resource
    private SoReturnMapper soReturnDao;
    @Resource
    private SoReturnItemMapper soReturnItemDao;
    @Resource
    private PoReturnMapper poReturnDao;
    @Resource
    private PoReturnItemMapper poReturnItemDao;
    @Resource
    private ProduceNoticeMapper produceNoticeDao;
}
