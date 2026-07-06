<template>
  <el-card>
    <div slot="header"><span><i class="el-icon-position"></i> 转诊单</span></div>
    <el-form :inline="true" class="compact-form">
      <el-form-item label="患者ID"><el-input v-model="form.pId"></el-input></el-form-item>
      <el-form-item label="订单ID"><el-input v-model="form.oId"></el-input></el-form-item>
      <el-form-item label="转入医院"><el-input v-model="form.toHospital"></el-input></el-form-item>
      <el-form-item label="转入科室"><el-input v-model="form.toDept"></el-input></el-form-item>
      <el-form-item label="原因"><el-input v-model="form.reason"></el-input></el-form-item>
      <el-form-item><el-button type="primary" @click="createReferral">生成转诊单</el-button></el-form-item>
    </el-form>
    <el-table :data="list" border stripe>
      <el-table-column prop="rr_id" label="编号" width="80"></el-table-column>
      <el-table-column prop="p_name" label="患者" width="100"></el-table-column>
      <el-table-column prop="to_hospital" label="转入医院"></el-table-column>
      <el-table-column prop="to_dept" label="科室" width="110"></el-table-column>
      <el-table-column prop="reason" label="原因"></el-table-column>
      <el-table-column prop="create_time" label="创建时间" width="170"></el-table-column>
    </el-table>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "ReferralManage",
  data() { return { form:{ pId:"", oId:"", fromDept:"门诊", toHospital:"上级医院", toDept:"", reason:"", operator:"" }, list:[] }; },
  methods: {
    async createReferral() {
      if (!this.form.pId || !this.form.toDept) return this.$message.warning("请输入患者ID和转入科室");
      const res = await request.post("smart/referral/create", this.form);
      if (res.data.status === 200) { this.$message.success("转诊单已生成"); this.loadData(); }
      else this.$message.error(res.data.msg || "生成失败");
    },
    async loadData() {
      const res = await request.get("smart/referral/list", { params:{ pId:this.form.pId || undefined } });
      if (res.data.status === 200) this.list = res.data.data || [];
    },
    fillUser() {
      const t = getToken(); if (!t) return;
      const d = jwtDecode(t);
      this.form.pId = d.pId || "";
      this.form.operator = d.aName || d.dName || d.pName || "";
    }
  },
  created() { this.fillUser(); this.loadData(); }
};
</script>
<style scoped>.compact-form .el-input{width:150px;}</style>
