package com.bear.hospital.service;

import com.bear.hospital.pojo.Orders;

import java.util.HashMap;
import java.util.List;

public interface OrderService {
    /**
     * 分页模糊查询所有挂号信息
     */
    HashMap<String, Object> findAllOrders(int pageNumber, int size, String query);
    /**
     * 真正删除挂号信息
     */
    Boolean deleteOrder(int oId);
    /**
     * 增加挂号信息
     */
    Boolean addOrder(Orders order, String arId);
    /**
     * 根据pId查询挂号
     */
    List<Orders> findOrderByPid(int pId) ;
    /**
     * 查看当天挂号列表
     */
    List<Orders> findOrderByNull(String dId, String oStart) ;
    /**
     * 根据id更新挂号信息
     */
    Boolean updateOrder(Orders orders);
    /**
     * 根据id设置缴费状态
     */
    Boolean updatePrice(int oId);
    /** 处理收费（旧接口兼容，药费/检查费不关联病历） */
    Boolean processPayment(int oId, String paymentMethod, String invoiceNo, Double insuranceCovered, Double selfPay, String operator);
    /** 处理收费（正确业务流程：emrId关联病历，挂号费仍关联订单） */
    Boolean processPayment(int oId, Integer emrId, String paymentMethod, String invoiceNo, Double insuranceCovered, Double selfPay, String operator);
    /**
     * 查找医生已完成的挂号单
     */
    HashMap<String, Object> findOrderFinish(int pageNumber, int size, String query, String dId) ;
    /**
     * 根据dId查询挂号
     */
    HashMap<String, Object> findOrderByDid(int pageNumber, int size, String query, String dId) ;
    /**
     * 统计今天挂号人数
     */
    int orderPeople(String oStart);
    /**
     * 统计今天某个医生挂号人数
     */
    int orderPeopleByDid(String oStart, String dId);
    int orderCompletedToday(String oStart);
    /**
     * 统计挂号男女人数
     */
    List<String> orderGender();
    /**
     * 增加诊断及医生意见
     */
    Boolean updateOrderByAdd(Orders order);
    /**
     * 判断诊断之后再次购买药物是否已缴费
     */
    Boolean findTotalPrice(int oId);
    /**
     * 请求挂号时间段
     */
    HashMap<String, String> findOrderTime(String arId);
    /**
     * 统计过去20天挂号科室人数
     */
    List<String> orderSection();
    /**
     * 根据日期范围查询订单
     */
    List<Orders> findOrdersByDate(String start, String end);
    /**
     * 统计待缴费订单数
     */
    int pendingPaymentCount();

    /**
     * 更新订单状态（带状态机验证）
     */
    Boolean updateOrderState(int oId, int newState);

    // -------- Feature 1: Cancel Appointment (取消挂号) --------
    /** 医生完成全部接诊后统一推进订单状态 */
    Boolean finalizeConsultation(int oId);
    Boolean cancelOrder(int oId, String reason);

    // -------- Feature 2: Re-registration (复诊挂号) --------
    Boolean reRegister(int oId);

    // -------- Feature 3: Missed appointment (爽约记录) --------
    Boolean markMissed(int oId);
    int countMissed(int pId);

    // -------- Feature 4: Doctor substitution (换诊/代诊) --------
    Boolean substituteDoctor(String oldDid, String newDid, String date);

    // -------- Feature 5: Registration type & fee auto-calc --------
    // (handled inside addOrder)

    // -------- Feature 10: Patient billing detail --------
    HashMap<String, Object> patientBillingDetail(int pId);

    /**
     * 查找需要复诊提醒的订单（7/14/30天前的含"复诊"处方）
     */
    List<Orders> findOrdersNeedingFollowUp();
}
