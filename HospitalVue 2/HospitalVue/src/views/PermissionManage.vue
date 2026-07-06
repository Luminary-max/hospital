<template>
  <el-card>
    <div slot="header"><span><i class="el-icon-key"></i> 权限管理</span></div>
    <el-form label-width="90px" class="permission-form">
      <el-form-item label="授权人ID"><el-input v-model="roleForm.aId" placeholder="管理员账号，如 202601"></el-input></el-form-item>
      <el-form-item label="岗位角色">
        <el-select v-model="roleForm.roleCode">
          <el-option label="护士" value="nurse"></el-option>
          <el-option label="药师" value="pharmacist"></el-option>
          <el-option label="收费员" value="cashier"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="功能权限">
        <el-checkbox-group v-model="rolePerms">
          <el-checkbox label="triage">分诊</el-checkbox>
          <el-checkbox label="queue">排队协助</el-checkbox>
          <el-checkbox label="pharmacy">发药复核</el-checkbox>
          <el-checkbox label="health_profile">健康档案</el-checkbox>
          <el-checkbox label="report">报告解析</el-checkbox>
          <el-checkbox label="prescription_review">处方审查</el-checkbox>
          <el-checkbox label="cashier">收费结算</el-checkbox>
          <el-checkbox label="insurance">医保试算</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item><el-button type="primary" @click="saveRole">保存权限</el-button></el-form-item>
    </el-form>
    <el-table :data="roleList" border stripe @row-click="editRow">
      <el-table-column prop="a_id" label="账号ID" width="100"></el-table-column>
      <el-table-column prop="a_name" label="姓名" width="100"></el-table-column>
      <el-table-column prop="role_name" label="岗位" width="100"></el-table-column>
      <el-table-column prop="permissions" label="权限"></el-table-column>
      <el-table-column prop="update_time" label="更新时间" width="170"></el-table-column>
    </el-table>
  </el-card>
</template>

<script>
import request from "@/utils/request.js";
export default {
  name: "PermissionManage",
  data() {
    return {
      roleForm: { aId:"202601", roleCode:"nurse" },
      rolePerms: ["triage", "queue"],
      roleList: []
    };
  },
  watch: {
    "roleForm.roleCode"(code) {
      this.rolePerms = this.defaultPerms(code);
    }
  },
  methods: {
    roleName(code) {
      return code === "nurse" ? "护士" : (code === "cashier" ? "收费员" : "药师");
    },
    defaultPerms(code) {
      if (code === "nurse") return ["triage", "queue"];
      if (code === "cashier") return ["cashier", "insurance"];
      return ["pharmacy", "health_profile", "report", "prescription_review"];
    },
    editRow(row) {
      this.roleForm = { aId: row.a_id || "202601", roleCode: row.role_code || "nurse" };
      this.rolePerms = row.permissions ? row.permissions.split(",").map(item => item.trim()).filter(Boolean) : this.defaultPerms(this.roleForm.roleCode);
    },
    async saveRole() {
      const payload = {
        aId: this.roleForm.aId,
        roleCode: this.roleForm.roleCode,
        roleName: this.roleName(this.roleForm.roleCode),
        permissions: this.rolePerms.join(",")
      };
      const res = await request.post("smart/role/save", payload);
      if (res.data.status === 200) { this.$message.success("权限已保存"); this.loadRoles(); }
      else this.$message.error(res.data.msg || "保存失败");
    },
    async loadRoles() {
      const res = await request.get("smart/role/list");
      if (res.data.status === 200) this.roleList = res.data.data || [];
    }
  },
  created() { this.loadRoles(); }
};
</script>

<style scoped>
.permission-form { max-width: 760px; margin-bottom: 18px; }
</style>
