package com.bear.hospital.controller;

import com.bear.hospital.utils.JwtUtil;
import com.bear.hospital.utils.Md5Util;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("staff")
public class StaffController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/login")
    @ResponseBody
    public ResponseData login(@RequestParam("staffId") String staffId,
                              @RequestParam("staffPassword") String staffPassword,
                              @RequestParam("staffRole") String staffRole) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM staff_user WHERE staff_id=? AND role_code=? AND staff_state=1",
                staffId, staffRole);
        if (rows.isEmpty()) return ResponseData.fail("登录失败，账号或角色错误");
        Map<String, Object> staff = rows.get(0);
        String stored = staff.get("staff_password") == null ? "" : String.valueOf(staff.get("staff_password"));
        if (!stored.equals(staffPassword) && !stored.equals(Md5Util.getMD5(staffPassword))) {
            return ResponseData.fail("登录失败，密码错误");
        }
        Map<String, String> claims = new HashMap<>();
        claims.put("staffId", staffId);
        claims.put("staffName", String.valueOf(staff.get("staff_name")));
        claims.put("staffRole", staffRole);
        claims.put("roleName", String.valueOf(staff.get("role_name")));
        String permissions = findPermissions(staffRole);
        claims.put("permissions", permissions);
        String token = JwtUtil.getToken(claims);
        claims.put("token", token);
        return ResponseData.success("登录成功", claims);
    }

    private String findPermissions(String staffRole) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT permissions FROM admin_role_permission WHERE role_code=? ORDER BY update_time DESC LIMIT 1",
                staffRole);
        if (!rows.isEmpty() && rows.get(0).get("permissions") != null) {
            String permissions = String.valueOf(rows.get(0).get("permissions"));
            if (!permissions.trim().isEmpty()) return permissions;
        }
        if ("nurse".equals(staffRole)) return "triage,queue";
        if ("pharmacist".equals(staffRole)) return "pharmacy,health_profile,report,prescription_review";
        if ("cashier".equals(staffRole)) return "cashier,insurance";
        return "";
    }
}
