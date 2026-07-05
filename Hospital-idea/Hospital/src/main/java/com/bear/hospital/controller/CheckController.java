package com.bear.hospital.controller;

import com.bear.hospital.pojo.Checks;
import com.bear.hospital.service.CheckService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("check")
public class CheckController {
    @Autowired
    private CheckService checkService;
    /**
     * 分页模糊查询所有检查信息
     */
    @RequestMapping("findAllChecks")
    public ResponseData findAllChecks(int pageNumber, int size, String query){
        return ResponseData.success("返回所有检查信息成功", this.checkService.findAllChecks(pageNumber, size, query));
    }
    /**
     * 根据id查找检查
     */
    @RequestMapping("findCheck")
    public ResponseData findCheck(String chId){
        return ResponseData.success("根据id查找检查成功", this.checkService.findCheck(chId));
    }
    /**
     * 增加检查信息
     */
    @RequestMapping("addCheck")
    @ResponseBody
    public ResponseData addCheck(Checks checks) {
        Boolean bo = this.checkService.addCheck(checks);
        if (bo) {
            return ResponseData.success("增加检查信息成功");
        }
        return ResponseData.fail("增加检查信息失败！账号或已被占用");
    }
    /**
     * 删除药物信息
     */
    @RequestMapping("deleteCheck")
    public ResponseData deleteCheck(@RequestParam(value = "chId") String chId) {
        Boolean bo = this.checkService.deleteCheck(chId);
        if (bo){
            return ResponseData.success("删除检查信息成功");
        }
        return ResponseData.fail("删除检查信息失败");
    }
    /**
     * 修改检查信息
     */
    @RequestMapping("modifyCheck")
    @ResponseBody
    public ResponseData modifyCheck(Checks checks) {
        this.checkService.modifyCheck(checks);
        return ResponseData.success("修改检查项目信息成功");
    }

    // ========== Order Check endpoints ==========

    /**
     * 分页查询检查开单（按病历）
     */
    @RequestMapping("findOrderChecks")
    public ResponseData findOrderChecks(@RequestParam int pageNumber, @RequestParam int size,
        @RequestParam(required = false) Integer emrId,
        @RequestParam(required = false) Integer status) {
        return ResponseData.success("查询成功", this.checkService.findOrderChecks(pageNumber, size, emrId, status));
    }

    /**
     * 医生开检查单（按病历）
     */
    @RequestMapping("createOrderCheck")
    @ResponseBody
    public ResponseData createOrderCheck(@RequestParam int oId, @RequestParam String chId,
        @RequestParam String chName, @RequestParam Double chPrice) {
        if (this.checkService.createOrderCheck(oId, chId, chName, chPrice))
            return ResponseData.success("开检查单成功");
        return ResponseData.fail("开检查单失败");
    }

    /**
     * 批量开检查单（按订单）
     */
    @RequestMapping("batchCreateOrderChecks")
    @ResponseBody
    public ResponseData batchCreateOrderChecks(@RequestParam int oId,
        @RequestBody List<Map<String, Object>> items) {
        if (this.checkService.batchCreateOrderChecks(oId, items))
            return ResponseData.success("批量开检查单成功");
        return ResponseData.fail("批量开检查单失败");
    }

    /**
     * 录入检查结果
     */
    @RequestMapping("updateCheckResult")
    @ResponseBody
    public ResponseData updateCheckResult(@RequestParam Integer ocId, @RequestParam String result,
        @RequestParam(required = false) String attachment,
        @RequestParam String operator) {
        if (this.checkService.updateCheckResult(ocId, result, attachment, operator))
            return ResponseData.success("录入检查结果成功");
        return ResponseData.fail("录入检查结果失败");
    }

    /**
     * 更新检查状态
     */
    @RequestMapping("updateCheckStatus")
    public ResponseData updateCheckStatus(@RequestParam Integer ocId, @RequestParam Integer status) {
        if (this.checkService.updateCheckStatus(ocId, status))
            return ResponseData.success("更新检查状态成功");
        return ResponseData.fail("更新检查状态失败");
    }
}
