package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bear.hospital.mapper.BillingMapper;
import com.bear.hospital.pojo.BillingRecord;
import com.bear.hospital.service.BillingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service("BillingService")
public class BillingServiceImpl implements BillingService {

    @Resource
    private BillingMapper billingMapper;

    @Override
    public Boolean addBillingRecord(BillingRecord record) {
        return this.billingMapper.insert(record) > 0;
    }

    @Override
    public List<BillingRecord> findByOrderId(Integer oId) {
        QueryWrapper<BillingRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("o_id", oId).orderByAsc("br_id");
        return this.billingMapper.selectList(wrapper);
    }

    /**
     * Feature 9: 收费员日结统计
     * Returns totalIncome, regIncome, drugIncome, checkIncome,
     * count by payment method (cash, wechat, alipay, bank, insurance),
     * orderCount, refundCount
     */
    @Override
    public HashMap<String, Object> dailySummary(String date) {
        HashMap<String, Object> result = new HashMap<>();
        // Query all billing records for the given date
        QueryWrapper<BillingRecord> wrapper = new QueryWrapper<>();
        wrapper.like("br_pay_time", date);
        List<BillingRecord> records = this.billingMapper.selectList(wrapper);

        double totalIncome = 0;
        double regIncome = 0;
        double drugIncome = 0;
        double checkIncome = 0;

        int cashCount = 0;
        int wechatCount = 0;
        int alipayCount = 0;
        int bankCount = 0;
        int insuranceCount = 0;

        for (BillingRecord r : records) {
            double amount = r.getBrAmount() != null ? r.getBrAmount() : 0;
            totalIncome += amount;

            String type = r.getBrType();
            if (type != null) {
                if (type.contains("挂号")) {
                    regIncome += amount;
                } else if (type.contains("药")) {
                    drugIncome += amount;
                } else if (type.contains("检查")) {
                    checkIncome += amount;
                }
            }

            String pm = r.getBrPaymentMethod();
            if (pm != null) {
                switch (pm) {
                    case "现金":
                    case "cash":
                        cashCount++;
                        break;
                    case "微信":
                    case "wechat":
                        wechatCount++;
                        break;
                    case "支付宝":
                    case "alipay":
                        alipayCount++;
                        break;
                    case "银行卡":
                    case "bank":
                        bankCount++;
                        break;
                    case "医保":
                    case "insurance":
                        insuranceCount++;
                        break;
                }
            }
        }

        // Count distinct orders
        long orderCount = records.stream().map(BillingRecord::getOId).distinct().count();

        result.put("totalIncome", totalIncome);
        result.put("regIncome", regIncome);
        result.put("drugIncome", drugIncome);
        result.put("checkIncome", checkIncome);
        result.put("cashCount", cashCount);
        result.put("wechatCount", wechatCount);
        result.put("alipayCount", alipayCount);
        result.put("bankCount", bankCount);
        result.put("insuranceCount", insuranceCount);
        result.put("orderCount", (int) orderCount);
        result.put("refundCount", 0); // Refund count could come from refund_request table
        result.put("date", date);

        return result;
    }
}
