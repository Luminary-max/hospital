<template>
  <el-card>
    <div slot="header"><span><i class="el-icon-bank-card"></i> 医保试算</span></div>
    <el-form label-width="90px" style="max-width:520px">
      <el-form-item label="患者ID"><el-input v-model="form.pId"></el-input></el-form-item>
      <el-form-item label="订单ID"><el-input v-model="form.oId" placeholder="可选，填写后可自动取订单金额"></el-input></el-form-item>
      <el-form-item label="总金额"><el-input v-model="form.totalAmount"></el-input></el-form-item>
      <el-form-item><el-button type="primary" @click="estimate">医保费用试算</el-button></el-form-item>
    </el-form>
    <el-descriptions v-if="result.totalAmount !== undefined" border :column="4">
      <el-descriptions-item label="医保类型">{{ result.insuranceType || '自费' }}</el-descriptions-item>
      <el-descriptions-item label="总额">¥{{ result.totalAmount }}</el-descriptions-item>
      <el-descriptions-item label="报销">¥{{ result.reimburseAmount }}</el-descriptions-item>
      <el-descriptions-item label="自付">¥{{ result.selfPayAmount }}</el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "InsuranceEstimate",
  data() { return { form:{ pId:"", oId:"", totalAmount:"" }, result:{} }; },
  methods: {
    async estimate() {
      if (!this.form.pId) return this.$message.warning("请输入患者ID");
      const res = await request.post("smart/insurance/estimate", this.form);
      if (res.data.status === 200) this.result = res.data.data || {};
      else this.$message.error(res.data.msg || "试算失败");
    }
  },
  created() { const t = getToken(); if (t) this.form.pId = jwtDecode(t).pId || ""; }
};
</script>
