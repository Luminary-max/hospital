package com.bear.hospital.service.serviceImpl;

import com.bear.hospital.service.DeepSeekService;
import com.bear.hospital.service.SmartHospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class SmartHospitalServiceImpl implements SmartHospitalService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DeepSeekService deepSeekService;

    @Override
    public Map<String, Object> saveRolePermission(Map<String, Object> payload) {
        String aId = str(payload.get("aId"));
        String roleCode = str(payload.get("roleCode"));
        String roleName = str(payload.get("roleName"));
        String permissions = str(payload.get("permissions"));
        jdbcTemplate.update("INSERT INTO admin_role_permission(a_id,role_code,role_name,permissions) VALUES(?,?,?,?) " +
                        "ON DUPLICATE KEY UPDATE role_name=VALUES(role_name), permissions=VALUES(permissions), update_time=NOW()",
                aId, roleCode, roleName, permissions);
        return one("aId", aId, "roleCode", roleCode, "roleName", roleName, "permissions", permissions);
    }

    @Override
    public List<Map<String, Object>> listRolePermissions() {
        return jdbcTemplate.queryForList("SELECT rp.*, a.a_name FROM admin_role_permission rp LEFT JOIN admini a ON rp.a_id=a.a_id ORDER BY rp.update_time DESC");
    }

    @Override
    public Map<String, Object> currentRolePermission(String roleCode) {
        String permissions = permissionsForRole(roleCode);
        return one("roleCode", roleCode, "permissions", permissions, "permissionList", splitPermissions(permissions));
    }

    @Override
    public String permissionsForRole(String roleCode) {
        if (roleCode == null || roleCode.isEmpty()) return "";
        if ("admin".equals(roleCode)) return "*";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT permissions FROM admin_role_permission WHERE role_code=? ORDER BY update_time DESC LIMIT 1",
                roleCode);
        if (!rows.isEmpty()) {
            String permissions = val(rows.get(0), "permissions");
            if (!permissions.isEmpty()) return permissions;
        }
        if ("nurse".equals(roleCode)) return "triage,queue";
        if ("pharmacist".equals(roleCode)) return "pharmacy,health_profile,report,prescription_review";
        if ("cashier".equals(roleCode)) return "cashier,insurance";
        return "";
    }

    @Override
    public Map<String, Object> buildHealthProfile(Integer pId) {
        Map<String, Object> patient = first("SELECT * FROM patient WHERE p_id=?", pId);
        if (patient == null) return one("message", "患者不存在");
        List<Map<String, Object>> emrs = jdbcTemplate.queryForList(
                "SELECT * FROM outpatient_emr WHERE p_id=? ORDER BY create_time DESC LIMIT 8", pId);
        StringBuilder history = new StringBuilder();
        String allergy = "";
        String lastVisit = null;
        for (Map<String, Object> e : emrs) {
            history.append("主诉:").append(val(e, "chief_complaint")).append("; 诊断:")
                    .append(val(e, "diagnosis")).append("; 既往史:").append(val(e, "past_history")).append("\n");
            if (allergy.isEmpty()) allergy = val(e, "allergy_history");
            if (lastVisit == null && e.get("create_time") != null) lastVisit = String.valueOf(e.get("create_time"));
        }
        String prompt = "请基于患者基础信息与历史病历，生成长期健康档案摘要、慢病/既往史、家族史待补充项、风险等级(低/中/高危)。\n患者:"
                + patient + "\n历史病历:\n" + history;
        String summary = deepSeekService.chat("你是医院信息系统中的健康档案摘要助手，只做辅助总结，不替代医生诊断。", prompt);
        String risk = pickRisk(summary);
        jdbcTemplate.update("INSERT INTO health_profile(p_id,chronic_history,family_history,allergy_history,summary,risk_level,last_visit_time) " +
                        "VALUES(?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE chronic_history=VALUES(chronic_history), family_history=VALUES(family_history), " +
                        "allergy_history=VALUES(allergy_history), summary=VALUES(summary), risk_level=VALUES(risk_level), last_visit_time=VALUES(last_visit_time)",
                pId, history.toString(), "待补充", allergy, summary, risk, lastVisit);
        return one("patient", patient, "history", history.toString(), "summary", summary, "riskLevel", risk);
    }

    @Override
    public List<Map<String, Object>> listHealthProfiles(String query, Integer pId) {
        if (pId != null) {
            return jdbcTemplate.queryForList("SELECT hp.*, p.p_name, p.p_gender, p.p_age, p.p_phone FROM health_profile hp " +
                    "LEFT JOIN patient p ON hp.p_id=p.p_id WHERE hp.p_id=? ORDER BY hp.update_time DESC", pId);
        }
        String like = "%" + (query == null ? "" : query) + "%";
        return jdbcTemplate.queryForList("SELECT hp.*, p.p_name, p.p_gender, p.p_age, p.p_phone FROM health_profile hp " +
                "LEFT JOIN patient p ON hp.p_id=p.p_id WHERE p.p_name LIKE ? OR CAST(hp.p_id AS CHAR) LIKE ? ORDER BY hp.update_time DESC", like, like);
    }

    @Override
    public Map<String, Object> saveAnnouncement(Map<String, Object> payload) {
        jdbcTemplate.update("INSERT INTO hospital_announcement(title,content,target_role,status,publisher) VALUES(?,?,?,?,?)",
                str(payload.get("title")), str(payload.get("content")), def(payload.get("targetRole"), "all"),
                intv(payload.get("status"), 1), str(payload.get("publisher")));
        return one("saved", true);
    }

    @Override
    public List<Map<String, Object>> listAnnouncements(String role) {
        String r = role == null || role.isEmpty() ? "all" : role;
        return jdbcTemplate.queryForList("SELECT * FROM hospital_announcement WHERE status=1 AND (target_role='all' OR target_role=?) ORDER BY publish_time DESC", r);
    }

    @Override
    public Map<String, Object> aiDiagnosis(Map<String, Object> payload) {
        Integer pId = intObj(payload.get("pId"));
        String symptoms = str(payload.get("symptoms"));
        String context = "";
        if (pId != null) {
            context = jdbcTemplate.queryForList("SELECT diagnosis,chief_complaint,past_history,allergy_history,create_time FROM outpatient_emr WHERE p_id=? ORDER BY create_time DESC LIMIT 5", pId).toString();
        }
        String answer = deepSeekService.chat("你是门诊AI辅助诊断助手。输出可能疾病、建议检查、风险等级，只能辅助医生。", "症状:" + symptoms + "\n历史病例:" + context);
        String risk = pickRisk(answer);
        jdbcTemplate.update("INSERT INTO ai_diagnosis_record(p_id,symptoms,history_context,suggestion,risk_level,model_name) VALUES(?,?,?,?,?,?)",
                pId, symptoms, context, answer, risk, "deepseek-chat");
        return one("suggestion", answer, "riskLevel", risk, "historyContext", context);
    }

    @Override
    public Map<String, Object> queuePrediction(String dId, Integer pId) {
        String targetDId = dId == null ? "" : dId.trim();
        if (targetDId.isEmpty() && pId != null) {
            Map<String, Object> order = first("SELECT d_id FROM orders WHERE p_id=? ORDER BY o_id DESC LIMIT 1", pId);
            targetDId = order == null ? "" : val(order, "d_id");
        }
        if (targetDId.isEmpty()) {
            return one("doctorId", "", "waiting", 0, "finished", 0, "avgMinutes", 5.0,
                    "estimateMinutes", 0, "peakHint", "暂无可预测队列", "queue", Collections.emptyList());
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT q.q_id,q.o_id,q.q_state,q.q_create_time,q.q_call_time,q.q_finish_time," +
                        "o.o_queue_number AS q_number,o.d_id,o.p_id,p.p_name " +
                        "FROM queue_number q LEFT JOIN orders o ON q.o_id=o.o_id " +
                        "LEFT JOIN patient p ON o.p_id=p.p_id " +
                        "WHERE o.d_id=? AND DATE(q.q_create_time)=CURDATE() ORDER BY q.q_id",
                targetDId);
        int waiting = 0, finished = 0;
        for (Map<String, Object> r : rows) {
            int state = intv(r.get("q_state"), 0);
            if (state == 0) waiting++;
            if (state == 3) finished++;
        }
        double avg = avgMinutes(targetDId);
        int estimate = (int) Math.ceil(waiting * avg);
        String peak = waiting >= 8 ? "当前高峰" : (waiting >= 4 ? "轻度拥挤" : "平稳");
        return one("doctorId", targetDId, "waiting", waiting, "finished", finished, "avgMinutes", avg, "estimateMinutes", estimate, "peakHint", peak, "queue", rows);
    }

    @Override
    public Map<String, Object> reportAnalysis(Map<String, Object> payload) {
        Integer ocId = intObj(payload.get("ocId"));
        String result = str(payload.get("result"));
        Integer pId = intObj(payload.get("pId"));
        if (ocId != null && result.isEmpty()) {
            Map<String, Object> oc = first("SELECT oc.*, emr.p_id FROM order_check oc LEFT JOIN outpatient_emr emr ON oc.emr_id=emr.emr_id WHERE oc.oc_id=?", ocId);
            if (oc != null) result = val(oc, "oc_result");
            if (oc != null && pId == null) pId = intObj(oc.get("p_id"));
        }
        if (result.isEmpty()) {
            return one("summary", "该检查单暂无报告结果，请先录入检验结果或手动填写报告内容。", "riskLevel", "低", "abnormalFlags", "");
        }
        String answer = deepSeekService.chat("你是检验检查报告解析助手，输出异常项、可能意义、简要结论和风险等级。", result);
        String risk = pickRisk(answer);
        jdbcTemplate.update("INSERT INTO ai_report_analysis(oc_id,p_id,raw_result,abnormal_flags,summary,risk_level) VALUES(?,?,?,?,?,?)",
                ocId, pId, result, findAbnormal(result), answer, risk);
        return one("summary", answer, "riskLevel", risk, "abnormalFlags", findAbnormal(result));
    }

    @Override
    public Map<String, Object> prescriptionReview(Map<String, Object> payload) {
        String content = "患者ID:" + payload.get("pId") + "\n诊断:" + payload.get("diagnosis") + "\n处方:" + payload.get("items") + "\n过敏史:" + payload.get("allergyHistory");
        String answer = deepSeekService.chat("你是处方合理性审查助手，检查药物冲突、剂量异常、过敏提醒，输出通过/需注意/高风险。", content);
        return one("review", answer, "riskLevel", pickRisk(answer));
    }

    @Override
    public Map<String, Object> createReferral(Map<String, Object> payload) {
        jdbcTemplate.update("INSERT INTO referral_record(o_id,p_id,from_dept,to_hospital,to_dept,reason,operator) VALUES(?,?,?,?,?,?,?)",
                intObj(payload.get("oId")), intv(payload.get("pId"), 0), str(payload.get("fromDept")),
                str(payload.get("toHospital")), str(payload.get("toDept")), str(payload.get("reason")), str(payload.get("operator")));
        return one("saved", true);
    }

    @Override
    public List<Map<String, Object>> listReferrals(Integer pId) {
        if (pId == null) return jdbcTemplate.queryForList("SELECT rr.*, p.p_name FROM referral_record rr LEFT JOIN patient p ON rr.p_id=p.p_id ORDER BY rr.create_time DESC");
        return jdbcTemplate.queryForList("SELECT rr.*, p.p_name FROM referral_record rr LEFT JOIN patient p ON rr.p_id=p.p_id WHERE rr.p_id=? ORDER BY rr.create_time DESC", pId);
    }

    @Override
    public Map<String, Object> insuranceEstimate(Map<String, Object> payload) {
        Integer pId = intv(payload.get("pId"), 0);
        Integer oId = intObj(payload.get("oId"));
        double total = doublev(payload.get("totalAmount"), 0);
        if (total <= 0 && oId != null) {
            Map<String, Object> order = first("SELECT o_total_price FROM orders WHERE o_id=?", oId);
            total = order == null ? 0 : doublev(order.get("o_total_price"), 0);
        }
        Map<String, Object> patient = first("SELECT p_insurance_id,p_insurance_type FROM patient WHERE p_id=?", pId);
        String type = patient == null ? "" : val(patient, "p_insurance_type");
        double ratio = type.contains("职工") ? 0.70 : (type.contains("居民") ? 0.50 : 0.0);
        double reimburse = round(total * ratio);
        double selfPay = round(total - reimburse);
        jdbcTemplate.update("INSERT INTO insurance_settlement(o_id,p_id,insurance_no,total_amount,reimburse_ratio,reimburse_amount,self_pay_amount,status) VALUES(?,?,?,?,?,?,?,?)",
                oId, pId, patient == null ? "" : val(patient, "p_insurance_id"), total, ratio, reimburse, selfPay, 1);
        return one("totalAmount", total, "ratio", ratio, "reimburseAmount", reimburse, "selfPayAmount", selfPay, "insuranceType", type);
    }

    private double avgMinutes(String dId) {
        Double avg = jdbcTemplate.queryForObject("SELECT AVG(TIMESTAMPDIFF(MINUTE,q.q_call_time,q.q_finish_time)) " +
                "FROM queue_number q LEFT JOIN orders o ON q.o_id=o.o_id " +
                "WHERE o.d_id=? AND q.q_call_time IS NOT NULL AND q.q_finish_time IS NOT NULL", Double.class, dId);
        if (avg == null || avg <= 0 || avg > 60) return 5.0;
        return Math.max(3.0, Math.round(avg * 10.0) / 10.0);
    }

    private List<String> splitPermissions(String permissions) {
        List<String> list = new ArrayList<>();
        if (permissions == null || permissions.trim().isEmpty()) return list;
        if ("*".equals(permissions.trim())) {
            list.add("*");
            return list;
        }
        for (String p : permissions.split(",")) {
            String item = p.trim();
            if (!item.isEmpty()) list.add(item);
        }
        return list;
    }

    private Map<String, Object> first(String sql, Object... args) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, args);
        return list.isEmpty() ? null : list.get(0);
    }

    private String pickRisk(String text) {
        String t = text == null ? "" : text;
        if (t.contains("高危") || t.contains("高风险") || t.contains("危急")) return "高危";
        if (t.contains("中风险") || t.contains("中等") || t.contains("中")) return "中";
        return "低";
    }

    private String findAbnormal(String text) {
        String t = text == null ? "" : text;
        if (t.contains("↑") || t.contains("↓") || t.contains("异常") || t.contains("阳性") || t.contains("偏高") || t.contains("偏低")) return "发现异常提示";
        return "未发现明显异常标记";
    }

    private Map<String, Object> one(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) m.put(String.valueOf(kv[i]), kv[i + 1]);
        return m;
    }

    private String val(Map<String, Object> m, String key) { Object v = m.get(key); return v == null ? "" : String.valueOf(v); }
    private String str(Object o) { return o == null ? "" : String.valueOf(o); }
    private String def(Object o, String d) { String s = str(o); return s.isEmpty() ? d : s; }
    private Integer intObj(Object o) { if (o == null || str(o).isEmpty()) return null; return Integer.valueOf(str(o)); }
    private int intv(Object o, int d) { try { return o == null ? d : new BigDecimal(str(o)).intValue(); } catch (Exception e) { return d; } }
    private double doublev(Object o, double d) { try { return o == null ? d : new BigDecimal(str(o)).doubleValue(); } catch (Exception e) { return d; } }
    private double round(double n) { return Math.round(n * 100.0) / 100.0; }
}
