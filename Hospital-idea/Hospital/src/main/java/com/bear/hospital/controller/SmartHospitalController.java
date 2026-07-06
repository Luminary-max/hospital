package com.bear.hospital.controller;

import com.bear.hospital.service.SmartHospitalService;
import com.bear.hospital.utils.JwtUtil;
import com.bear.hospital.utils.ResponseData;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("smart")
public class SmartHospitalController {
    @Autowired
    private SmartHospitalService smartHospitalService;

    @RequestMapping("role/save")
    public ResponseData saveRole(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        if (!hasRole(request, "admin")) return ResponseData.fail("无权限：仅管理员可分配权限");
        return ResponseData.success("保存权限成功", smartHospitalService.saveRolePermission(payload));
    }

    @RequestMapping("role/list")
    public ResponseData listRole(HttpServletRequest request) {
        if (!hasRole(request, "admin")) return ResponseData.fail("无权限：仅管理员可查看权限");
        return ResponseData.success("查询权限成功", smartHospitalService.listRolePermissions());
    }

    @RequestMapping("role/current")
    public ResponseData currentRoleInfo(HttpServletRequest request) {
        String role = currentRole(request);
        Map<String, Object> data = smartHospitalService.currentRolePermission(role);
        data.put("role", role);
        data.put("pId", currentPId(request));
        data.put("dId", claim(request, "dId"));
        return ResponseData.success("查询当前权限成功", data);
    }

    @RequestMapping("health/build")
    public ResponseData buildHealth(@RequestParam Integer pId, HttpServletRequest request) {
        if (!hasAnyRole(request, "doctor", "patient", "pharmacist")) return ResponseData.fail("无权限：健康档案仅患者、医生、药师可用");
        if (hasRole(request, "pharmacist") && !hasPermission(request, "health_profile")) return ResponseData.fail("无权限：当前药师角色未开通健康档案");
        if (hasRole(request, "patient")) pId = currentPId(request);
        return ResponseData.success("生成健康档案成功", smartHospitalService.buildHealthProfile(pId));
    }

    @RequestMapping("health/list")
    public ResponseData listHealth(@RequestParam(required = false) String query, HttpServletRequest request) {
        if (!hasAnyRole(request, "doctor", "patient", "pharmacist")) return ResponseData.fail("无权限：健康档案仅患者、医生、药师可用");
        if (hasRole(request, "pharmacist") && !hasPermission(request, "health_profile")) return ResponseData.fail("无权限：当前药师角色未开通健康档案");
        Integer pId = hasRole(request, "patient") ? currentPId(request) : null;
        return ResponseData.success("查询健康档案成功", smartHospitalService.listHealthProfiles(query, pId));
    }

    @RequestMapping("announcement/save")
    public ResponseData saveAnnouncement(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        if (!hasRole(request, "admin")) return ResponseData.fail("无权限：仅管理员可发布公告");
        return ResponseData.success("发布公告成功", smartHospitalService.saveAnnouncement(payload));
    }

    @RequestMapping("announcement/list")
    public ResponseData listAnnouncement(@RequestParam(required = false) String role, HttpServletRequest request) {
        if (hasRole(request, "admin")) return ResponseData.fail("无权限：公告查看入口仅面向非管理员角色");
        return ResponseData.success("查询公告成功", smartHospitalService.listAnnouncements(role));
    }

    @RequestMapping("ai/diagnosis")
    public ResponseData aiDiagnosis(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        if (!hasRole(request, "doctor")) return ResponseData.fail("无权限：AI辅助诊断仅医生可用");
        return ResponseData.success("AI辅助诊断完成", smartHospitalService.aiDiagnosis(payload));
    }

    @RequestMapping("queue/predict")
    public ResponseData queuePredict(@RequestParam(required = false) String dId, HttpServletRequest request) {
        if (!hasRole(request, "patient")) return ResponseData.fail("无权限：智能排队预测仅患者可用");
        return ResponseData.success("排队预测完成", smartHospitalService.queuePrediction(dId, currentPId(request)));
    }

    @RequestMapping("report/analyze")
    public ResponseData reportAnalyze(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        if (!hasAnyRole(request, "doctor", "patient", "pharmacist")) return ResponseData.fail("无权限：报告解析仅患者、医生、药师可用");
        if (hasRole(request, "pharmacist") && !hasPermission(request, "report")) return ResponseData.fail("无权限：当前药师角色未开通报告解析");
        if (hasRole(request, "patient")) payload.put("pId", currentPId(request));
        return ResponseData.success("报告解析完成", smartHospitalService.reportAnalysis(payload));
    }

    @RequestMapping("prescription/review")
    public ResponseData prescriptionReview(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        if (!hasAnyRole(request, "doctor", "pharmacist")) return ResponseData.fail("无权限：处方合理性检查仅医生、药师可用");
        if (hasRole(request, "pharmacist") && !hasPermission(request, "prescription_review")) return ResponseData.fail("无权限：当前药师角色未开通处方审查");
        return ResponseData.success("处方审查完成", smartHospitalService.prescriptionReview(payload));
    }

    @RequestMapping("referral/create")
    public ResponseData createReferral(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        if (hasRole(request, "admin")) return ResponseData.fail("无权限：转诊单入口仅面向非管理员角色");
        return ResponseData.success("转诊单生成成功", smartHospitalService.createReferral(payload));
    }

    @RequestMapping("referral/list")
    public ResponseData listReferral(@RequestParam(required = false) Integer pId, HttpServletRequest request) {
        if (hasRole(request, "admin")) return ResponseData.fail("无权限：转诊记录入口仅面向非管理员角色");
        return ResponseData.success("查询转诊记录成功", smartHospitalService.listReferrals(pId));
    }

    @RequestMapping("insurance/estimate")
    public ResponseData insuranceEstimate(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        if (!hasAnyRole(request, "patient", "cashier")) return ResponseData.fail("无权限：医保试算仅患者、收费员可用");
        if (hasRole(request, "cashier") && !hasPermission(request, "insurance")) return ResponseData.fail("无权限：当前收费员角色未开通医保试算");
        if (hasRole(request, "patient")) payload.put("pId", currentPId(request));
        return ResponseData.success("医保试算完成", smartHospitalService.insuranceEstimate(payload));
    }

    private boolean hasPermission(HttpServletRequest request, String permission) {
        String role = currentRole(request);
        if ("admin".equals(role)) return true;
        String permissions = smartHospitalService.permissionsForRole(role);
        if ("*".equals(permissions)) return true;
        List<String> list = Arrays.asList(permissions.split(","));
        return list.stream().map(String::trim).anyMatch(permission::equals);
    }

    private boolean hasAnyRole(HttpServletRequest request, String... roles) {
        String role = currentRole(request);
        List<String> allowed = Arrays.asList(roles);
        return allowed.contains(role);
    }

    private boolean hasRole(HttpServletRequest request, String role) {
        return role.equals(currentRole(request));
    }

    private String currentRole(HttpServletRequest request) {
        try {
            DecodedJWT jwt = JwtUtil.verify(request.getHeader("token"));
            if (jwt.getClaim("staffRole").asString() != null) return jwt.getClaim("staffRole").asString();
            if (jwt.getClaim("dId").asString() != null) return "doctor";
            if (jwt.getClaim("pId").asString() != null) return "patient";
            if (jwt.getClaim("aId").asString() != null) return "admin";
        } catch (Exception ignored) {
        }
        return "guest";
    }

    private Integer currentPId(HttpServletRequest request) {
        String pId = claim(request, "pId");
        if (pId == null || pId.isEmpty()) return null;
        try {
            return Integer.valueOf(pId);
        } catch (Exception e) {
            return null;
        }
    }

    private String claim(HttpServletRequest request, String name) {
        try {
            DecodedJWT jwt = JwtUtil.verify(request.getHeader("token"));
            return jwt.getClaim(name).asString();
        } catch (Exception e) {
            return null;
        }
    }
}
