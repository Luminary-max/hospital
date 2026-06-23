<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-document"></i> 我的处方</span>
    </div>

    <div v-if="loading" style="text-align:center;padding:60px 0;">
      <i class="el-icon-loading" style="font-size:32px;color:#409EFF;"></i>
      <p style="color:#999;margin-top:10px;">加载中...</p>
    </div>

    <div v-else-if="prescriptionList.length === 0" style="text-align:center;padding:80px 0;color:#999;">
      <i class="el-icon-document" style="font-size:48px;"></i>
      <p style="margin-top:15px;font-size:15px;">暂无处方记录</p>
      <p style="color:#c0c4cc;font-size:13px;">就诊后医生会为您开立处方</p>
    </div>

    <el-timeline v-else>
      <el-timeline-item v-for="(item, index) in prescriptionList" :key="item.oId"
        :timestamp="item.oStart || '---'"
        placement="top"
        :color="item.oPriceState === 0 ? '#F56C6C' : '#67C23A'">
        <el-card shadow="hover" class="presc-card">
          <div class="presc-header">
            <div class="presc-header-left">
              <span class="presc-doctor"><i class="el-icon-user"></i> {{ item.dName || '---' }}</span>
              <span class="presc-id">订单号：{{ item.oId }}</span>
            </div>
            <div class="presc-header-right">
              <el-tag v-if="item.oPriceState === 0" type="danger" size="small">未缴费</el-tag>
              <el-tag v-else type="success" size="small">已缴费</el-tag>
              <el-tag v-if="item.oState === 1" type="info" size="small" style="margin-left:4px;">已完成</el-tag>
              <el-tag v-else type="warning" size="small" style="margin-left:4px;">待就诊</el-tag>
            </div>
          </div>

          <el-divider style="margin:10px 0;"></el-divider>

          <div class="presc-body">
            <div class="presc-info">
              <div class="info-row">
                <span class="info-label">诊断</span>
                <span class="info-value">{{ item.oRecord || '无' }}</span>
              </div>
              <div class="info-row" v-if="item.oDrug">
                <span class="info-label">药品</span>
                <span class="info-value drug-text">{{ item.oDrug }}</span>
              </div>
              <div class="info-row" v-if="item.oCheck">
                <span class="info-label">检查</span>
                <span class="info-value">{{ item.oCheck }}</span>
              </div>
            </div>
          </div>

          <el-divider style="margin:10px 0;"></el-divider>

          <div class="presc-footer">
            <div class="presc-price">
              合计：<span class="total-price" :class="{ unpaid: item.oPriceState === 0 }">¥{{ (item.oTotalPrice || 0).toFixed(2) }}</span>
            </div>
            <div class="presc-actions">
              <el-button type="primary" size="mini" @click="viewDetail(item)">查看详情</el-button>
              <el-button size="mini" plain @click="printOrder(item)"><i class="el-icon-printer"></i> 打印</el-button>
              <el-button v-if="item.oPriceState === 0 && item.oState === 1" type="danger" size="mini" @click="goPay(item)">去缴费</el-button>
            </div>
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>

    <el-dialog title="处方详情" :visible.sync="detailVisible" width="750px" top="5vh">
      <el-card shadow="hover" style="margin-bottom:15px;">
        <div slot="header"><span style="font-weight:bold;">就诊信息</span></div>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="订单号">{{ detailData.oId }}</el-descriptions-item>
          <el-descriptions-item label="医生">{{ detailData.dName || '---' }}</el-descriptions-item>
          <el-descriptions-item label="就诊日期">{{ detailData.oStart || '---' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <el-card shadow="hover" style="margin-bottom:15px;">
        <div slot="header"><span style="font-weight:bold;color:#409EFF;">诊断</span></div>
        <div style="padding:8px;background:#f9f9f9;border-radius:4px;">{{ detailData.oRecord || '无' }}</div>
      </el-card>
      <el-card shadow="hover" style="margin-bottom:15px;" v-if="prescDrugDetails.length > 0">
        <div slot="header">
          <span style="font-weight:bold;color:#E6A23C;">药品明细（共 {{ prescDrugDetails.length }} 项）</span>
          <el-tag size="mini" type="warning" style="margin-left:8px;">合计 ¥{{ prescDrugTotal }}</el-tag>
        </div>
        <el-table :data="prescDrugDetails"border  stripe  size="small" style="width:100%" >
          <el-table-column prop="drName"   label="药品"   width="130" ></el-table-column>
          <el-table-column label="类型"   width="55" >
            <template slot-scope="s">
              <el-tag :type="s.row.drType === 2 ? 'success' : 'warning'" size="mini">{{ s.row.drType === 2 ? '中药' : '西药' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="pdUsage"   label="用法"   width="55" ></el-table-column>
          <el-table-column prop="pdDosage"   label="用量"   width="55" ></el-table-column>
          <el-table-column prop="pdFrequency"   label="频次"   width="55" ></el-table-column>
          <el-table-column prop="pdDays"   label="天数"   width="50" ></el-table-column>
          <el-table-column prop="pdQuantity"   label="数量"   width="50" ></el-table-column>
          <el-table-column label="小计"   width="70"   align="center" >
            <template slot-scope="s">¥{{ (s.row.pdQuantity * (s.row.pdPrice || s.row.drPrice || 0)).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="pdNote"   label="备注"   min-width="80"   show- overflow- tooltip></el-table-column>
        </el-table>
      </el-card>
      <el-card shadow="hover" style="margin-bottom:15px;" v-if="detailData.oCheck">
        <div slot="header"><span style="font-weight:bold;color:#67C23A;">检查项目</span></div>
        <div style="padding:8px;background:#f9f9f9;border-radius:4px;">{{ detailData.oCheck }}</div>
      </el-card>
      <el-card shadow="hover" v-if="detailData.oAdvice">
        <div slot="header"><span style="font-weight:bold;">医生建议</span></div>
        <div style="padding:8px;background:#f9f9f9;border-radius:4px;">{{ detailData.oAdvice }}</div>
      </el-card>
      <div style="text-align:right;margin-top:15px;font-size:18px;font-weight:bold;">
        合计：<span style="color:#E6A23C;">¥{{ (detailData.oTotalPrice || 0).toFixed(2) }}</span>
      </div>
      <div slot="footer">
        <el-button @click="detailVisible=false">关闭</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "MyPrescription",
  data() {
    return {
      pId: null,
      prescriptionList: [],
      detailVisible: false,
      detailData: {},
      prescDrugDetails: [],
      loading: true
    };
  },
  computed: {
    prescDrugTotal() {
      return this.prescDrugDetails.reduce(function(s, d) {
        return s + d.pdQuantity * (d.pdPrice || d.drPrice || 0);
      }, 0).toFixed(2);
    }
  },
  methods: {
    async loadData() {
      if (!this.pId) { this.loading = false; return; }
      try {
        const res = await request.get("patient/findOrderByPid", { params: { pId: this.pId } });
        if (res.data.status === 200) {
          this.prescriptionList = (res.data.data || []).filter(function(o) {
            return o.oDrug || o.oTotalPrice > 0 || o.oCheck;
          });
        }
      } catch(e) {}
      this.loading = false;
    },
    async viewDetail(row) {
      this.detailData = row;
      this.prescDrugDetails = [];
      this.detailVisible = true;
      try {
        const res = await request.get("prescription/findByOrder", { params: { oId: row.oId } });
        if (res.data.status === 200) this.prescDrugDetails = res.data.data || [];
      } catch(e) {}
    },
    printOrder(row) {
      this.detailData = row;
      this.$nextTick(function() {
        window.print();
      });
    },
    goPay(row) {
      this.$router.push("/myOrder");
    }
  },
  created() {
    const t = getToken();
    if (t) { const d = jwtDecode(t); this.pId = d.pId || d.sub; }
    this.loadData();
  }
};
</script>
<style scoped>
.presc-card { transition: all 0.2s; }
.presc-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.presc-header { display: flex; justify-content: space-between; align-items: center; }
.presc-header-left { display: flex; align-items: center; gap: 16px; }
.presc-doctor { font-weight: 600; color: #303133; }
.presc-id { color: #909399; font-size: 12px; }
.info-row { display: flex; margin: 4px 0; gap: 12px; }
.info-label { color: #909399; font-size: 12px; min-width: 50px; flex-shrink: 0; }
.info-value { color: #303133; font-size: 13px; }
.info-value.drug-text { color: #E6A23C; font-weight: 500; }
.presc-footer { display: flex; justify-content: space-between; align-items: center; }
.presc-price { font-size: 14px; color: #606266; }
.total-price { font-size: 20px; font-weight: 700; color: #E6A23C; }
.total-price.unpaid { color: #F56C6C; }
</style>









