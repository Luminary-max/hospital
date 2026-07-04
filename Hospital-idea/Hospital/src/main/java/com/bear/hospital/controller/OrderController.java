package com.bear.hospital.controller;

import com.bear.hospital.pojo.Orders;
import com.bear.hospital.service.OrderService;
import com.bear.hospital.service.RefundRequestService;
import com.bear.hospital.utils.ResponseData;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.ArrayList;

@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Resource
    private RefundRequestService refundRequestService;
    /**
     * 根据id更新挂号信息
     */
    @PostMapping("updateOrder")
    @ResponseBody
    public ResponseData updateOrder(@RequestBody Orders orders) {
        if (this.orderService.updateOrder(orders))
            return ResponseData.success("更新挂号信息成功");

        return ResponseData.fail("更新挂号信息失败！");
    }
    /**
     * 根据id设置缴费状态
     */
    @RequestMapping("updatePrice")
    public ResponseData updatePrice(int oId){
        if (this.orderService.updatePrice(oId))
        return ResponseData.success("根据id设置缴费状态成功");
        return ResponseData.fail("根据id设置缴费状态失败");
    }
    /**
     * 处理收费（含支付方式、发票号、医保报销）
     */
    @PostMapping("processPayment")
    @ResponseBody
    public ResponseData processPayment(@RequestParam int oId,
        @RequestParam(required = false) Integer emrId,
        @RequestParam String paymentMethod, @RequestParam String invoiceNo,
        @RequestParam(required = false) Double insuranceCovered,
        @RequestParam(required = false) Double selfPay,
        @RequestParam(required = false) String operator) {
        // 药费/检查费关联到病历；挂号费仍关联到订单
        this.orderService.processPayment(oId, emrId, paymentMethod, invoiceNo, insuranceCovered, selfPay, operator);
        return ResponseData.success("收费成功");
    }
    /**
     * 查找医生已完成的挂号单
     */
    @RequestMapping("findOrderFinish")
    public ResponseData findOrderFinish(int pageNumber, int size, String query, String dId){
        return ResponseData.success("查找医生已完成的挂号单完成！", this.orderService.findOrderFinish(pageNumber, size, query, dId));
    }
    /**
     * 根据dId查询挂号
     */
    @RequestMapping("findOrderByDid")
    public ResponseData findOrderByDid(int pageNumber, int size, String query, String dId){
        return ResponseData.success("返回挂号信息成功", this.orderService.findOrderByDid(pageNumber, size, query, dId)) ;
    }
    /**
     * 统计今天挂号人数
     */
    @RequestMapping("orderPeople")
    public ResponseData oderPeople(){
        String oStart = TodayUtil.getTodayYmd();
        return ResponseData.success("统计今天挂号人数成功", this.orderService.orderPeople(oStart));
    }
    /**
     * 统计今天某个医生挂号人数
     */
    @RequestMapping("orderPeopleByDid")
    public ResponseData orderPeopleByDid(String dId){
        String oStart = TodayUtil.getTodayYmd();
        return ResponseData.success("统计今天挂号人数成功", this.orderService.orderPeopleByDid(oStart, dId));
    }
    /**
     * 获取过去七天的挂号人数
     */
    @RequestMapping("orderSeven")
    public ResponseData orderSeven(){
        ArrayList<Integer> list = new ArrayList<>();
        String oStart = null;
        for(int i = 20; i > 0;i--){
            oStart = TodayUtil.getPastDate(i);
            int people = this.orderService.orderPeople(oStart);
            list.add(people);
        }
        return ResponseData.success("获取过去20天的挂号人数成功", list);
    }
    /**
     * 统计挂号男女人数
     */
    @RequestMapping("orderGender")
    public ResponseData orderGender(){
        return ResponseData.success("统计挂号男女人数", this.orderService.orderGender());
    }
    /**
     * 增加诊断及医生意见
     */
    @PostMapping("updateOrderByAdd")
    @ResponseBody
    public ResponseData updateOrderByAdd(@RequestBody Orders order){
        if (this.orderService.updateOrderByAdd(order))
            return ResponseData.success("增加诊断及医生意见成功");
        return ResponseData.fail("增加诊断及医生意见失败");
    }
    /**
     * 判断诊断之后再次购买药物是否已缴费
     */
    @RequestMapping("findTotalPrice")
    public ResponseData findTotalPrice(int oId){
       if(this.orderService.findTotalPrice(oId))
           return ResponseData.success("未缴费");
       return ResponseData.fail("无需缴费");
    }
    /**
     * 请求挂号时间段
     */
    @RequestMapping("findOrderTime")
    public ResponseData findOrderTime(String arId){
        return ResponseData.success("请求挂号时间段成功", this.orderService.findOrderTime(arId));

    }
    /**
     * 统计过去20天挂号科室人数
     */
    @RequestMapping("orderSection")
    public ResponseData orderSection(){
        return ResponseData.success("统计过去20天挂号科室人数成功", this.orderService.orderSection());
    }

    /**
     * 统计待缴费订单数量（已就诊但未缴费）
     */
    @RequestMapping("pendingPaymentCount")
    public ResponseData pendingPaymentCount(){
        return ResponseData.success("查询成功", this.orderService.pendingPaymentCount());
    }

    /**
     * 获取每日收入统计（药费+检查费+挂号费）
     */
    @RequestMapping("orderDailyIncome")
    public ResponseData orderDailyIncome(){
        ArrayList<Double> drugIncome = new ArrayList<>();
        ArrayList<Double> regIncome = new ArrayList<>();
        ArrayList<String> dateLabels = new ArrayList<>();
        for (int i = 20; i > 0; i--) {
            String day = TodayUtil.getPastDate(i);
            dateLabels.add(day.substring(5));
            double dSum = 0, rSum = 0;
            String dayStart = day + " 00:00";
            String dayEnd = day + " 23:59";
            // Get orders for this day
            java.util.List<Orders> dayOrders = this.orderService.findOrdersByDate(dayStart, dayEnd);
            for (Orders o : dayOrders) {
                if (o.getOTotalPrice() != null) dSum += o.getOTotalPrice();
                if (o.getORegistrationFee() != null) rSum += o.getORegistrationFee();
            }
            drugIncome.add(dSum);
            regIncome.add(rSum);
        }
        java.util.HashMap<String, Object> result = new java.util.HashMap<>();
        result.put("dates", dateLabels);
        result.put("drugIncome", drugIncome);
        result.put("regIncome", regIncome);
        return ResponseData.success("获取每日收入统计成功", result);
    }

    /**
     * 更新订单状态（状态机）
     */
    @RequestMapping("updateOrderState")
    public ResponseData updateOrderState(@RequestParam int oId, @RequestParam int newState) {
        if (this.orderService.updateOrderState(oId, newState))
            return ResponseData.success("更新状态成功");
        return ResponseData.fail("更新状态失败，状态转换不合法");
    }

    /**
     * 医生完成接诊，统一推进订单到已开单状态
     */
    @PostMapping("finalizeConsultation")
    @ResponseBody
    public ResponseData finalizeConsultation(@RequestParam int oId) {
        if (this.orderService.finalizeConsultation(oId))
            return ResponseData.success("接诊完成，已开单");
        return ResponseData.fail("状态转变失败");
    }

    /**
     * Feature 1: 取消挂号
     */
    @RequestMapping("cancelOrder")
    public ResponseData cancelOrder(@RequestParam int oId, @RequestParam String reason) {
        if (this.orderService.cancelOrder(oId, reason))
            return ResponseData.success("取消挂号成功");
        return ResponseData.fail("取消挂号失败");
    }

    /**
     * Feature 2: 复诊挂号
     */
    @RequestMapping("reRegister")
    public ResponseData reRegister(@RequestParam int oId) {
        if (this.orderService.reRegister(oId))
            return ResponseData.success("复诊挂号成功");
        return ResponseData.fail("复诊挂号失败");
    }

    /**
     * Feature 3: 标记爽约
     */
    @RequestMapping("markMissed")
    public ResponseData markMissed(@RequestParam int oId) {
        if (this.orderService.markMissed(oId))
            return ResponseData.success("标记爽约成功");
        return ResponseData.fail("标记爽约失败");
    }

    /**
     * Feature 3: 查询患者爽约次数
     */
    @RequestMapping("countMissed")
    public ResponseData countMissed(@RequestParam int pId) {
        int count = this.orderService.countMissed(pId);
        return ResponseData.success("查询成功", count);
    }

    /**
     * Feature 4: 医生换诊/代诊
     */
    @RequestMapping("substitute")
    public ResponseData substituteDoctor(@RequestParam String oldDid, @RequestParam String newDid, @RequestParam String date) {
        if (this.orderService.substituteDoctor(oldDid, newDid, date))
            return ResponseData.success("换诊成功");
        return ResponseData.fail("换诊失败");
    }

    /**
     * Feature 10: 患者费用明细
     */
    @RequestMapping("patientBillingDetail")
    public ResponseData patientBillingDetail(@RequestParam int pId) {
        return ResponseData.success("查询成功", this.orderService.patientBillingDetail(pId));
    }

    /**
     * 退款处理：创建退费申请（前端 order/refund 兼容入口）
     */
    @RequestMapping("refund")
    public ResponseData refund(@RequestParam int oId, @RequestParam String reason, @RequestParam(required = false) String operator) {
        com.bear.hospital.pojo.RefundRequest req = new com.bear.hospital.pojo.RefundRequest();
        req.setOId(oId);
        req.setRfReason(reason);
        req.setRfRequester(operator != null ? operator : "收费员");
        req.setRfStatus(com.bear.hospital.pojo.RefundRequest.STATUS_PENDING);
        req.setRfCreateTime(com.bear.hospital.utils.TodayUtil.getToday());
        return refundRequestService.create(req)
            ? ResponseData.success("退费申请已提交，等待审核")
            : ResponseData.fail("退费申请提交失败");
    }

    /**
     * Feature 5: 复诊提醒列表
     */
    @RequestMapping("findOrdersNeedingFollowUp")
    public ResponseData findOrdersNeedingFollowUp() {
        return ResponseData.success("查询成功", this.orderService.findOrdersNeedingFollowUp());
    }
}
