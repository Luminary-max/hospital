package com.bear.hospital.service;

import com.bear.hospital.pojo.Checks;
import com.bear.hospital.pojo.OrderCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CheckService {
    /**
     * 分页模糊查询所有检查信息
     */
    HashMap<String, Object> findAllChecks(int pageNumber, int size, String query);
    /**
     * 根据id查找药物
     */
    Checks findCheck(String chId);
    /**
     * 增加检查信息
     */
    Boolean addCheck(Checks checks);
    /**
     * 删除检查信息
     */
    Boolean deleteCheck(String chId);
    /**
     * 修改检查信息
     */
    Boolean modifyCheck(Checks checks);

    // ========== Order Check operations ==========

    /**
     * 分页查询检查开单
     */
    HashMap<String, Object> findOrderChecks(int pageNumber, int size, Integer oId, Integer status);

    /**
     * 医生开检查单
     */
    Boolean createOrderCheck(int oId, String chId, String chName, Double chPrice);

    /**
     * 批量开检查单
     */
    Boolean batchCreateOrderChecks(int oId, List<Map<String, Object>> items);

    /**
     * 录入检查结果
     */
    Boolean updateCheckResult(Integer ocId, String result, String attachment, String operator);

    /**
     * 更新检查状态
     */
    Boolean updateCheckStatus(Integer ocId, Integer status);
}
