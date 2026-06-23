<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-s-finance"></i> 收费员日结/交接班报表</span>
      <el-button size="small" type="primary" plain style="float:right;" @click="exportReport" :disabled="!summary">
        <i class="el-icon-download"></i> 导出报表
      </el-button>
    </div>
    <el-form inline size="small" style="margin-bottom:16px;">
      <el-form-item label="日期">
        <el-date-picker v-model="reportDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" @change="loadSummary"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadSummary"><i class="el-icon-refresh"></i> 查询</el-button>
      </el-form-item>
    </el-form>

    <div v-if="loading" style="text-align:center;padding:60px 0;"><i class="el-icon-loading" style="font-size:28px;"></i><p>加载中...</p></div>

    <template v-else-if="summary">
      <el-row :gutter="20">
        <el-col :span="6"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#409EFF;">¥{{ summary.totalIncome.toFixed(2) }}</div><div style="font-size:13px;color:#909399;">总收入</div></div></el-card></el-col>
        <el-col :span="6"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#67C23A;">¥{{ summary.regIncome.toFixed(2) }}</div><div style="font-size:13px;color:#909399;">挂号费</div></div></el-card></el-col>
        <el-col :span="6"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#E6A23C;">¥{{ summary.drugIncome.toFixed(2) }}</div><div style="font-size:13px;color:#909399;">药费</div></div></el-card></el-col>
        <el-col :span="6"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#F56C6C;">¥{{ summary.checkIncome.toFixed(2) }}</div><div style="font-size:13px;color:#909399;">检查费</div></div></el-card></el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top:20px;">
        <el-col :span="12">
          <el-card>
            <div slot="header">支付方式统计</div>
            <el-table :data="paymentRows" border stripe size="small">
              <el-table-column prop="method" label="支付方式"></el-table-column>
              <el-table-column prop="count" label="笔数" width="80" align="center"></el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <div slot="header">订单统计</div>
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="收费订单数">{{ summary.orderCount }}</el-descriptions-item>
              <el-descriptions-item label="退费笔数">{{ summary.refundCount }}</el-descriptions-item>
              <el-descriptions-item label="交接日期">{{ summary.date }}</el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <el-empty v-else-if="!loading" description="暂无该日数据"></el-empty>
  </el-card>
</template>

<script>
import request from "@/utils/request.js";
export default {
  name: "CashierSettlement",
  data() {
    const d = new Date();
    const today = d.getFullYear()+"-"+String(d.getMonth()+1).padStart(2,"0")+"-"+String(d.getDate()).padStart(2,"0");
    return { reportDate: today, summary: null, loading: false };
  },
  computed: {
    paymentRows() {
      if (!this.summary) return [];
      return [
        { method: "现金", count: this.summary.cashCount || 0 },
        { method: "微信", count: this.summary.wechatCount || 0 },
        { method: "支付宝", count: this.summary.alipayCount || 0 },
        { method: "银行卡", count: this.summary.bankCount || 0 },
        { method: "医保", count: this.summary.insuranceCount || 0 },
      ].filter(r => r.count > 0);
    }
  },
  methods: {
    async loadSummary() {
      if (!this.reportDate) return;
      this.loading = true;
      try {
        const res = await request.get("billing/dailySummary", { params: { date: this.reportDate } });
        if (res.data.status === 200) this.summary = res.data.data;
        else this.$message.error(res.data.msg);
      } catch(e) { this.$message.error("请求失败"); }
      this.loading = false;
    },
    exportReport() {
      if (!this.summary) return;
      const s = this.summary;
      let csv = "项目,金额\n";
      csv += "总收入," + s.totalIncome.toFixed(2) + "\n";
      csv += "挂号费," + s.regIncome.toFixed(2) + "\n";
      csv += "药费," + s.drugIncome.toFixed(2) + "\n";
      csv += "检查费," + s.checkIncome.toFixed(2) + "\n";
      csv += "现金笔数," + (s.cashCount||0) + "\n";
      csv += "微信笔数," + (s.wechatCount||0) + "\n";
      csv += "支付宝笔数," + (s.alipayCount||0) + "\n";
      csv += "银行卡笔数," + (s.bankCount||0) + "\n";
      csv += "医保笔数," + (s.insuranceCount||0) + "\n";
      csv += "订单数," + s.orderCount + "\n";
      const bom = "﻿";
      const blob = new Blob([bom + csv], { type: "text/csv;charset=utf-8" });
      const a = document.createElement("a");
      a.href = URL.createObjectURL(blob);
      a.download = "交接班报表_" + this.reportDate + ".csv";
      a.click();
      URL.revokeObjectURL(a.href);
    }
  },
  created() { this.loadSummary(); }
};
</script>
