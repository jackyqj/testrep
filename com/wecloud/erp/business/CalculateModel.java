package com.wecloud.erp.business;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import com.wecloud.erp.entity.PODetail;
import com.wecloud.erp.entity.QcReqDetail;
import com.wecloud.erp.entity.SODetail;
import com.wecloud.erp.model.PO;
import com.wecloud.erp.model.PoItem;
import com.wecloud.erp.model.QcRequest;
import com.wecloud.erp.model.QcRequestItem;
import com.wecloud.erp.model.SalesOrder;
import com.wecloud.erp.model.SoItem;
import com.wecloud.erp.utils.ErpUtils;

// Referenced classes of package com.wecloud.erp.business:
//            ErpConfig

public class CalculateModel
{

    public CalculateModel()
    {
    }

    public static void calculate(PODetail poDtl)
    {
        PO po = poDtl.getOrderReq();
        List items = poDtl.getItems();
        BigDecimal totalQty = new BigDecimal(0);
        BigDecimal totalAmt = new BigDecimal(0);
        BigDecimal taxRate = new BigDecimal(ErpUtils.num(po.getTaxRate()));
        if(items != null)
        {
            for(Iterator iterator = items.iterator(); iterator.hasNext();)
            {
                PoItem item = (PoItem)iterator.next();
                BigDecimal qty = new BigDecimal(ErpUtils.num(item.getQty()));
                BigDecimal price = new BigDecimal(ErpUtils.num(item.getUnitPrice()));
                BigDecimal amount = qty.multiply(price);
                item.setTotalAmount(Double.valueOf(doubleVal(amount)));
                totalQty = totalQty.add(qty);
                totalAmt = totalAmt.add(amount);
            }

        }
        po.setTotalAmount(Double.valueOf(doubleVal(totalAmt.add(totalAmt.multiply(taxRate).divide(new BigDecimal(100))))));
        po.setNetAmount(Double.valueOf(doubleVal(totalAmt)));
        po.setQty(Double.valueOf(doubleVal(totalQty)));
    }

    public static void calculate(SODetail soDtl)
    {
        SalesOrder so = soDtl.getObj();
        List items = soDtl.getItems();
        BigDecimal totalQty = new BigDecimal(0);
        BigDecimal totalAmt = new BigDecimal(0);
        BigDecimal taxRate = new BigDecimal(ErpUtils.num(so.getTaxRate()));
        if(items != null)
        {
            for(Iterator iterator = items.iterator(); iterator.hasNext();)
            {
                SoItem item = (SoItem)iterator.next();
                BigDecimal qty = new BigDecimal(ErpUtils.num(item.getQty()));
                BigDecimal price = new BigDecimal(ErpUtils.num(item.getUnitPrice()));
                BigDecimal amount = qty.multiply(price);
                item.setTotalAmount(Double.valueOf(doubleVal(amount)));
                totalQty = totalQty.add(qty);
                totalAmt = totalAmt.add(amount);
            }

        }
        so.setTotalAmount(Double.valueOf(doubleVal(totalAmt.add(totalAmt.multiply(taxRate).divide(new BigDecimal(100))))));
        so.setNetAmount(Double.valueOf(doubleVal(totalAmt)));
        so.setQty(Double.valueOf(doubleVal(totalQty)));
    }

    public static void calculateQc(QcReqDetail qcreqDtl)
    {
        QcRequest po = qcreqDtl.getObj();
        List items = qcreqDtl.getItems();
        BigDecimal totalQty = new BigDecimal(0);
        if(items != null)
        {
            for(Iterator iterator = items.iterator(); iterator.hasNext();)
            {
                QcRequestItem item = (QcRequestItem)iterator.next();
                BigDecimal qty = new BigDecimal(ErpUtils.num(item.getQty()));
                totalQty = totalQty.add(qty);
            }

        }
        po.setQty(Double.valueOf(doubleVal(totalQty)));
    }

    private static double doubleVal(BigDecimal val)
    {
        return val.setScale(ErpConfig.SCALE_SIZE, 4).doubleValue();
    }
}
