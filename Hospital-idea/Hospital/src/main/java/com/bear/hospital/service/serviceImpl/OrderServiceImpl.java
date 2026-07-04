package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.BillingMapper;
import com.bear.hospital.mapper.DoctorMapper;
import com.bear.hospital.mapper.InvoiceRecordMapper;
import com.bear.hospital.mapper.OrderMapper;
import com.bear.hospital.mapper.PatientMapper;
import com.bear.hospital.pojo.BillingRecord;
import com.bear.hospital.pojo.Doctor;
import com.bear.hospital.pojo.InvoiceRecord;
import com.bear.hospital.pojo.Orders;
import com.bear.hospital.pojo.Patient;
import com.bear.hospital.service.OrderService;
import com.bear.hospital.utils.RandomUtil;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service("OrderService")
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private BillingMapper billingMapper;
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private PatientMapper patientMapper;
    @Resource
    private InvoiceRecordMapper invoiceRecordMapper;
    @Resource
    private com.bear.hospital.mapper.AuditLogMapper auditLogMapper;
    @Autowired
    private JedisPool jedisPool;//redis连接池
    /**
     * 分页模糊查询所有挂号信息
     */
    @Override
    public HashMap<String, Object> findAllOrders(int pageNumber, int size, String query) {
        Page<Orders> page = new Page<>(pageNumber, size);
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.like("p_id", query);
        IPage<Orders> iPage = this.orderMapper.selectPage(page, wrapper);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("total", iPage.getTotal());       //总条数
        hashMap.put("pages", iPage.getPages());       //总页数
        hashMap.put("pageNumber", iPage.getCurrent());//当前页
        hashMap.put("records", iPage.getRecords()); //查询到的记录
        return hashMap;
    }

    /**
     * 删除挂号信息
     */
    @Override
    public Boolean deleteOrder(int oId) {
        this.orderMapper.deleteById(oId);
        return true;
    }
    /**
     * 增加挂号信息
     * Feature 5: Auto-calc registration fee based on doctor title
     * Feature 6: Check doctor max daily patients
     */
    @Override
    public Boolean addOrder(Orders order, String arId){
        // Feature 6: Check doctor daily max
        String today = TodayUtil.getTodayYmd();
        Doctor doctor = this.doctorMapper.selectById(order.getdId());
        if (doctor != null && doctor.getdMaxDaily() != null && doctor.getdMaxDaily() > 0) {
            int todayCount = this.orderMapper.orderPeopleByDid(today, order.getdId());
            if (todayCount >= doctor.getdMaxDaily()) {
                return false; // exceeded daily limit
            }
        }

        // Feature 5: Auto-calc registration fee based on dPost
        if (order.getORegistrationFee() == null || order.getORegistrationFee() == 0) {
            if (doctor != null && doctor.getdPost() != null) {
                switch (doctor.getdPost()) {
                    case "主任医师":
                        order.setORegistrationFee(50.00);
                        break;
                    case "副主任医师":
                        order.setORegistrationFee(30.00);
                        break;
                    case "主治医师":
                        order.setORegistrationFee(20.00);
                        break;
                    case "医师":
                        order.setORegistrationFee(10.00);
                        break;
                    default:
                        order.setORegistrationFee(doctor.getdPrice() != null ? doctor.getdPrice() : 10.00);
                }
            } else {
                order.setORegistrationFee(10.00);
            }
        }

        //redis开始 — 使用Lua脚本保证跨实例原子性
        Jedis jedis = jedisPool.getResource();
        String oStart = order.getOStart();
        String time = (oStart != null && oStart.length() >= 22) ? oStart.substring(11, 22) : "";
        String timeField = null;
        java.util.Map<String, String> slotMap = new java.util.LinkedHashMap<>();
        slotMap.put("08:30-09:30", "eTOn");
        slotMap.put("09:30-10:30", "nTOt");
        slotMap.put("10:30-11:30", "tTOe");
        slotMap.put("14:30-15:30", "fTOf");
        slotMap.put("15:30-16:30", "fTOs");
        slotMap.put("16:30-17:30", "sTOs");
        for (java.util.Map.Entry<String, String> entry : slotMap.entrySet()) {
            if (entry.getKey().equals(time)) {
                timeField = entry.getValue();
                break;
            }
        }
        if (timeField != null) {
            try {
                // Lua: atomic check-and-decrement. Returns 1 on success, 0 if already 0
                String lua = "if redis.call('hget', KEYS[1], ARGV[1]) == false or tonumber(redis.call('hget', KEYS[1], ARGV[1])) <= 0 then return 0 else redis.call('hincrby', KEYS[1], ARGV[1], -1) return 1 end";
                Object result = jedis.eval(lua, java.util.Collections.singletonList(arId), java.util.Collections.singletonList(timeField));
                if ("0".equals(String.valueOf(result))) {
                    jedis.close();
                    return false;
                }
            } catch (Exception e) {
                System.err.println("Redis不可用，跳过分诊限额校验: " + e.getMessage());
            }
        }
        jedis.close();
        //redis结束
        order.setOId(RandomUtil.randomOid(order.getPId()));
        order.setOState(0);
        order.setOPriceState(0);
        order.setOStart(oStart != null && oStart.length() >= 22 ? oStart.substring(0,22) : oStart);
        this.orderMapper.insert(order);
        return true;
    }
    /**
     * 根据pId查询挂号
     */
    public List<Orders> findOrderByPid(int pId){

        return this.orderMapper.findOrderByPid(pId);
    }
    /**
     * 查看当天挂号列表
     */
    @Override
    public List<Orders> findOrderByNull(String dId, String oStart){
        return this.orderMapper.findOrderByNull(dId, oStart);
    }
    /**
     * 根据id更新挂号信息
     */
    @Override
    public Boolean updateOrder(Orders orders) {
        // 不强制修改 o_state，保留现有状态
        orders.setOEnd(TodayUtil.getToday());
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("o_id", orders.getOId());
        this.orderMapper.update(orders, wrapper);
        return true;
    }
    /**
     * 根据id设置缴费状态
     */
    @Override
    public Boolean updatePrice(int oId){
        UpdateWrapper<Orders> wrapper = new UpdateWrapper<>();
        wrapper.eq("o_id", oId).set("o_price_state", 1).set("o_total_price", 0.00);
        int i = this.orderMapper.update(null, wrapper);
        System.out.println("影响行数"+i);
        return true;
    }
    /**
     * 处理收费
     * Feature 8: Auto invoice generation if invoiceNo is empty
     */
    @Override
    public Boolean processPayment(int oId, String paymentMethod, String invoiceNo, Double insuranceCovered, Double selfPay, String operator) {
        return processPayment(oId, null, paymentMethod, invoiceNo, insuranceCovered, selfPay, operator);
    }

    /** 处理收费（按正确业务流程：传入病历ID emrId，药费/检查费关联到病历） */
    public Boolean processPayment(int oId, Integer emrId, String paymentMethod, String invoiceNo, Double insuranceCovered, Double selfPay, String operator) {
        if (invoiceNo == null || invoiceNo.isEmpty()) {
            invoiceNo = generateInvoiceNo();
        }
        String finalInvoiceNo = invoiceNo;
        Orders order = this.orderMapper.selectById(oId);
        // 挂号费关联到订单o_id（挂号时就产生了）
        if (order != null && order.getORegistrationFee() != null && order.getORegistrationFee() > 0) {
            BillingRecord record = new BillingRecord(oId, null, "挂号费", order.getORegistrationFee(), paymentMethod, finalInvoiceNo, TodayUtil.getToday(), operator);
            billingMapper.insert(record);
        }
        // 药费+检查费关联到病历emr_id
        if (order != null && order.getOTotalPrice() != null && order.getOTotalPrice() > 0) {
            BillingRecord record = new BillingRecord(oId, emrId, "药费+检查费", order.getOTotalPrice(), paymentMethod, finalInvoiceNo, TodayUtil.getToday(), operator);
            billingMapper.insert(record);
        } else if (order != null) {
            BillingRecord record = new BillingRecord(oId, emrId, "药费+检查费", 0.00, paymentMethod, finalInvoiceNo, TodayUtil.getToday(), operator);
            billingMapper.insert(record);
        }
        // 再更新订单状态
        UpdateWrapper<Orders> wrapper = new UpdateWrapper<>();
        wrapper.eq("o_id", oId)
            .set("o_payment_method", paymentMethod)
            .set("o_invoice_no", finalInvoiceNo)
            .set("o_insurance_covered", insuranceCovered != null ? insuranceCovered : 0.00)
            .set("o_self_pay", selfPay != null ? selfPay : 0.00)
            .set("o_price_state", 1)
            .set("o_total_price", 0.00);
        this.orderMapper.update(null, wrapper);
        // Feature 8: Write to invoice_record table (关联缴费记录)
        double totalAmount = (order != null && order.getORegistrationFee() != null ? order.getORegistrationFee() : 0.00)
                + (order != null && order.getOTotalPrice() != null ? order.getOTotalPrice() : 0.00);
        InvoiceRecord invRecord = new InvoiceRecord(oId, finalInvoiceNo, totalAmount, TodayUtil.getToday(), operator);
        // 关联最新的缴费记录 br_id
        QueryWrapper<BillingRecord> brWrapper = new QueryWrapper<>();
        brWrapper.eq("o_id", oId).orderByDesc("br_id").last("limit 1");
        BillingRecord latestBr = billingMapper.selectOne(brWrapper);
        if (latestBr != null) {
            invRecord.setBrId(latestBr.getBrId());
        }
        this.invoiceRecordMapper.insert(invRecord);
        return true;
    }

    /**
     * Generate invoice number: INV-YYYYMMDD-XXXX (with retry for uniqueness)
     */
    private String generateInvoiceNo() {
        String datePart = TodayUtil.getTodayYmd().replace("-", "");
        // Use timestamp millis to avoid concurrency collision
        String seq = String.format("%04d", (int)(Math.random() * 10000));
        return "INV-" + datePart + "-" + seq;
    }
    /**
     * 查找医生已完成的挂号单
     */
    @Override
    public HashMap<String, Object> findOrderFinish(int pageNumber, int size, String query, String dId){
        Page<Orders> page = new Page<>(pageNumber, size);
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.like("p_id", query).eq("d_id", dId).orderByDesc("o_start").eq("o_state", 1);
        IPage<Orders> iPage = this.orderMapper.selectPage(page, wrapper);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("total", iPage.getTotal());       //总条数
        hashMap.put("pages", iPage.getPages());       //总页数
        hashMap.put("pageNumber", iPage.getCurrent());//当前页
        hashMap.put("records", iPage.getRecords()); //查询到的记录

        return hashMap;
    }
    /**
     * 根据dId查询挂号
     */
    public HashMap<String, Object> findOrderByDid(int pageNumber, int size, String query, String dId){
        Page<Orders> page = new Page<>(pageNumber, size);
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.like("p_id", query).eq("d_id", dId).orderByDesc("o_start");
        IPage<Orders> iPage = this.orderMapper.selectPage(page, wrapper);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("total", iPage.getTotal());       //总条数
        hashMap.put("pages", iPage.getPages());       //总页数
        hashMap.put("pageNumber", iPage.getCurrent());//当前页
        hashMap.put("records", iPage.getRecords()); //查询到的记录
        return hashMap;
    }
    /**
     * 统计今天挂号人数
     */
    @Override
    public int orderPeople(String oStart){
        return this.orderMapper.orderPeople(oStart);
    }
    /**
     * 统计今天某个医生挂号人数
     */
    @Override
    public int orderPeopleByDid(String oStart, String dId){
        return this.orderMapper.orderPeopleByDid(oStart, dId);
    }
    /**
     * 统计挂号男女人数
     */
    public List<String> orderGender(){
        return this.orderMapper.orderGender();
    }
    /**
     * 增加诊断及医生意见
     */
    public Boolean updateOrderByAdd(Orders order){

        if (this.orderMapper.updateOrderByAdd(order) == 0){
            return false;
        }

        return true;
    }
    /**
     * 判断诊断之后再次购买药物是否已缴费
     */
    public Boolean findTotalPrice(int oId){
        Orders order = this.orderMapper.selectById(oId);
        if (order.getOTotalPrice() != 0.00){
            order.setOPriceState(0);
            this.orderMapper.updateById(order);
            return true;
        }
        return false;
    }
    /**
     * 请求挂号时间段
     */
    @Override
    public HashMap<String, String> findOrderTime(String arId){
        Jedis jedis = jedisPool.getResource();
        HashMap<String, String> map = (HashMap<String, String>) jedis.hgetAll(arId);

        if(map == null) {
            map = new HashMap<>();
            map.put("tTOe", "40");
            map.put("nTOt", "40");
            map.put("sTOs", "40");
            map.put("eTOn", "40");
            map.put("fTOf", "40");
            map.put("fTOs", "40");
        }

        map.putIfAbsent("tTOe", "40");
        map.putIfAbsent("nTOt", "40");
        map.putIfAbsent("sTOs", "40");
        map.putIfAbsent("eTOn", "40");
        map.putIfAbsent("fTOf", "40");
        map.putIfAbsent("fTOs", "40");

        jedis.hmset(arId, map);
        jedis.expire(arId, 604800);

        return map;
    }
    /**
     * 统计过去20天挂号科室人数
     */
    @Override
    public List<String> orderSection(){
        String startTime = TodayUtil.getPastDate(20);
        String endTime = TodayUtil.getTodayYmd();
        return this.orderMapper.orderSection(startTime, endTime);
    }
    /**
     * 根据日期范围查询订单
     */
    @Override
    public List<Orders> findOrdersByDate(String start, String end) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.ge("o_start", start).le("o_start", end);
        return this.orderMapper.selectList(wrapper);
    }

    /**
     * 统计待缴费订单数（已就诊但未缴费）
     */
    @Override
    public int pendingPaymentCount() {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("o_state", 1).eq("o_price_state", 0);
        return this.orderMapper.selectCount(wrapper);
    }

    /**
     * 更新订单状态（带状态机验证）
     */
    @Override
    public Boolean updateOrderState(int oId, int newState) {
        Orders order = this.orderMapper.selectById(oId);
        if (order == null) return false;
        int currentState = order.getOState() != null ? order.getOState() : 0;

        // Validate state transition: only allow forward progression
        if (newState <= currentState) {
            return false;
        }
        // Before payment (state < 5), limit skip to at most 2 steps
        // After payment, allow any forward jump (e.g., 5->7, 6->7)
        if (currentState < 5 && newState > currentState + 2) {
            return false;
        }

        UpdateWrapper<Orders> wrapper = new UpdateWrapper<>();
        wrapper.eq("o_id", oId).set("o_state", newState);
        // Set oEnd when completing
        if (newState >= Orders.STATE_COMPLETED) {
            wrapper.set("o_end", TodayUtil.getToday());
        }
        boolean success = this.orderMapper.update(null, wrapper) > 0;
        if (success) {
            // Write audit log for state transition
            com.bear.hospital.pojo.AuditLog auditLog = new com.bear.hospital.pojo.AuditLog();
            auditLog.setAlUserId(String.valueOf(order.getPId()));
            auditLog.setAlUserRole("patient");
            auditLog.setAlAction("ORDER_STATE_CHANGE");
            auditLog.setAlTarget("o_id=" + oId);
            auditLog.setAlDetail("State: " + currentState + " -> " + newState);
            auditLog.setAlCreateTime(TodayUtil.getToday());
            auditLogMapper.insert(auditLog);
        }
        return success;
    }

    // ========== Feature 1: Cancel Appointment ==========
    /**
     * 医生完成全部接诊后统一推进订单状态到 STATE_ORDERED
     */
    @Override
    public Boolean finalizeConsultation(int oId) {
        return updateOrderState(oId, Orders.STATE_ORDERED);
    }

    @Override
    public Boolean cancelOrder(int oId, String reason) {
        UpdateWrapper<Orders> wrapper = new UpdateWrapper<>();
        wrapper.eq("o_id", oId)
                .set("o_state", -1)
                .set("o_cancel_reason", reason);
        return this.orderMapper.update(null, wrapper) > 0;
    }

    // ========== Feature 2: Re-registration ==========
    @Override
    public Boolean reRegister(int oId) {
        Orders original = this.orderMapper.selectById(oId);
        if (original == null) return false;
        Orders newOrder = new Orders();
        newOrder.setPId(original.getPId());
        newOrder.setdId(original.getdId());
        newOrder.setORegType(original.getORegType());
        newOrder.setORegistrationFee(original.getORegistrationFee());
        // Default new order start time to now
        String today = TodayUtil.getTodayYmd();
        newOrder.setOStart(today + " " + TodayUtil.getToday().substring(11, 16) + ":00");
        newOrder.setOId(RandomUtil.randomOid(original.getPId()));
        newOrder.setOState(0);
        newOrder.setOPriceState(0);
        return this.orderMapper.insert(newOrder) > 0;
    }

    // ========== Feature 3: Missed appointment & blacklist ==========
    @Override
    public Boolean markMissed(int oId) {
        UpdateWrapper<Orders> wrapper = new UpdateWrapper<>();
        wrapper.eq("o_id", oId).set("o_missed", 1).set("o_state", -1);
        return this.orderMapper.update(null, wrapper) > 0;
    }

    @Override
    public int countMissed(int pId) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("p_id", pId).eq("o_missed", 1);
        return this.orderMapper.selectCount(wrapper);
    }

    // ========== Feature 4: Doctor substitution ==========
    @Override
    public Boolean substituteDoctor(String oldDid, String newDid, String date) {
        UpdateWrapper<Orders> wrapper = new UpdateWrapper<>();
        wrapper.eq("d_id", oldDid)
                .like("o_start", date)
                .set("d_id", newDid);
        return this.orderMapper.update(null, wrapper) >= 0;
    }

    // ========== Feature 10: Patient billing detail ==========
    @Override
    public HashMap<String, Object> patientBillingDetail(int pId) {
        HashMap<String, Object> result = new HashMap<>();
        // Find all orders for this patient
        List<Orders> orders = this.orderMapper.findOrderByPid(pId);
        java.util.ArrayList<HashMap<String, Object>> orderBills = new java.util.ArrayList<>();
        double totalAmount = 0;
        for (Orders order : orders) {
            HashMap<String, Object> orderBill = new HashMap<>();
            orderBill.put("oId", order.getOId());
            orderBill.put("oStart", order.getOStart());
            orderBill.put("oEnd", order.getOEnd());
            orderBill.put("oState", order.getOState());
            orderBill.put("dId", order.getdId());
            orderBill.put("dName", order.getdName());
            orderBill.put("registrationFee", order.getORegistrationFee());
            orderBill.put("totalPrice", order.getOTotalPrice());
            orderBill.put("paymentMethod", order.getOPaymentMethod());
            orderBill.put("invoiceNo", order.getOInvoiceNo());
            orderBill.put("insuranceCovered", order.getOInsuranceCovered());
            orderBill.put("selfPay", order.getOSelfPay());
            orderBill.put("oRegType", order.getORegType());
            // Get billing records for this order
            QueryWrapper<BillingRecord> bw = new QueryWrapper<>();
            bw.eq("o_id", order.getOId());
            List<BillingRecord> billingRecords = this.billingMapper.selectList(bw);
            orderBill.put("billingRecords", billingRecords);
            double orderTotal = 0;
            for (BillingRecord br : billingRecords) {
                orderTotal += br.getBrAmount() != null ? br.getBrAmount() : 0;
            }
            orderBill.put("orderPaid", orderTotal);
            totalAmount += orderTotal;
            orderBills.add(orderBill);
        }
        result.put("orders", orderBills);
        result.put("totalAmount", totalAmount);
        result.put("pId", pId);
        return result;
    }
    // ========== Feature 5: Follow-up reminder (复诊提醒) ==========
    @Override
    public List<Orders> findOrdersNeedingFollowUp() {
        List<Orders> result = new java.util.ArrayList<>();
        int[] intervals = {7, 14, 30};
        for (int interval : intervals) {
            String pastDate = TodayUtil.getPastDate(interval);
            QueryWrapper<Orders> wrapper = new QueryWrapper<>();
            wrapper.like("o_start", pastDate)
                .like("o_advice", "复诊")
                .orderByDesc("o_id");
            result.addAll(this.orderMapper.selectList(wrapper));
        }
        return result;
    }
}