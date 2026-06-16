<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-document"></i> 我的处方</span>
    </div>
    <el-table :data="prescriptionList" border stripe style="width:100%">
      <el-table-column prop="oId" label="订单编号" width="110"></el-table-column>
      <el-table-column prop="dName" label="医生" width="100"></el-table-column>
      <el-table-column prop="oStart" label="就诊日期" width="160"></el-table-column>
      <el-table-column prop="oDrug" label="药品" width="200" show-overflow-tooltip></el-table-column>
      <el-table-column prop="oTotalPrice" label="总价(元)" width="80"></el-table-column>
      <el-table-column label="缴费" width="80">
        <template slot-scope="s">
          <el-tag v-if="s.row.oPriceState === 0" type="danger">未缴费</el-tag>
          <el-tag v-else type="success">已缴费</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template slot-scope="s">
          <el-button type="primary" size="mini" @click="viewDetail(s.row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div v-if="prescriptionList.length === 0" style="text-align:center;padding:60px 0;color:#999;">暂无处方记录</div>

    <el-dialog title="处方详情" :visible.sync="detailVisible" width="700px">
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
        <div>{{ detailData.oRecord || '无' }}</div>
      </el-card>
      <el-card shadow="hover" style="margin-bottom:15px;">
        <div slot="header"><span style="font-weight:bold;color:#E6A23C;">药品明细</span></div>
        <div v-if="detailData.oDrug">{{ detailData.oDrug }}</div>
        <div v-else style="color:#999;">无</div>
      </el-card>
      <el-card shadow="hover" v-if="detailData.oCheck">
        <div slot="header"><span style="font-weight:bold;color:#67C23A;">检查项目</span></div>
        <div>{{ detailData.oCheck }}</div>
      </el-card>
      <el-card shadow="hover" style="margin-top:15px;" v-if="detailData.oAdvice">
        <div slot="header"><span style="font-weight:bold;">医生建议</span></div>
        <div>{{ detailData.oAdvice }}</div>
      </el-card>
      <div style="text-align:right;margin-top:15px;font-size:16px;font-weight:bold;">
        合计：<span style="color:#E6A23C;">¥{{ detailData.oTotalPrice || '0.00' }}</span>
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
  data() { return { pId: null, prescriptionList: [], detailVisible: false, detailData: {} }; },
  methods: {
    async loadData() {
      try {
        const res = await request.get("patient/findOrderByPid", { params: { pId: this.pId } });
        if (res.data.status === 200) this.prescriptionList = (res.data.data || []).filter(o => o.oDrug || o.oTotalPrice > 0);
      } catch(e) { console.error(e); }
    },
    viewDetail(row) { this.detailData = row; this.detailVisible = true; }
  },
  created() { const t = getToken(); if (t) { const d = jwtDecode(t); this.pId = d.pId || d.sub; } this.loadData(); }
};
</script>
