<template>
  <el-card>
    <div slot="header"><span><i class="el-icon-document"></i> 处方管理</span></div>
    <el-table :data="prescriptionData" border stripe style="width:100%">
      <el-table-column prop="oId" label="订单编号" width="100"></el-table-column>
      <el-table-column prop="pName" label="患者姓名" width="90"></el-table-column>
      <el-table-column prop="oStart" label="就诊日期" min-width="160"></el-table-column>
      <el-table-column label="诊断" min-width="160" show-overflow-tooltip>
        <template slot-scope="s">{{ s.row.oRecord || '---' }}</template>
      </el-table-column>
      <el-table-column label="总价(元)" width="100" align="center">
        <template slot-scope="s">
          <span v-if="s.row._drugTotal">¥{{ s.row._drugTotal.toFixed(2) }}</span>
          <span v-else-if="s.row.oTotalPrice">¥{{ s.row.oTotalPrice }}</span>
          <span v-else>---</span>
        </template>
      </el-table-column>
      <el-table-column label="缴费" width="80" align="center">
        <template slot-scope="s">
          <el-tag v-if="s.row.oPriceState === 0" type="danger">未缴费</el-tag>
          <el-tag v-else type="success">已缴费</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template slot-scope="s">
          <el-button type="primary" size="mini" @click="viewDetail(s.row)">查看详情</el-button>
          <el-button type="success" size="mini" @click="printPrescription(s.row)">打印</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="pageNumber" :page-sizes="[10,20,30]" :page-size="size"
      layout="total, sizes, prev, pager, next, jumper" :total="total" style="margin-top:20px;">
    </el-pagination>

    <el-dialog title="处方详情" :visible.sync="detailVisible" width="750px">
      <el-card shadow="hover" style="margin-bottom:15px;">
        <div slot="header"><span style="font-weight:bold;">基本信息</span></div>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="订单号">{{ detailData.oId }}</el-descriptions-item>
          <el-descriptions-item label="患者">{{ detailData.pName || '---' }}</el-descriptions-item>
          <el-descriptions-item label="就诊日期">{{ detailData.oStart || '---' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <el-card shadow="hover" style="margin-bottom:15px;">
        <div slot="header"><span style="font-weight:bold;color:#409EFF;">门诊病历</span></div>
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="诊断">{{ detailData.oRecord || '无' }}</el-descriptions-item>
          <el-descriptions-item label="医生建议">{{ detailData.oAdvice || '无' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <el-card shadow="hover" style="margin-bottom:15px;">
        <div slot="header"><span style="font-weight:bold;color:#E6A23C;">药品明细</span></div>
        <el-table :data="prescDetails" border stripe size="small" v-if="prescDetails.length > 0" style="width:100%">
          <el-table-column prop="drName" label="药品名" width="140"></el-table-column>
          <el-table-column prop="pdUsage" label="用法" width="60"></el-table-column>
          <el-table-column prop="pdDosage" label="用量" width="60"></el-table-column>
          <el-table-column prop="pdFrequency" label="频次" width="60"></el-table-column>
          <el-table-column prop="pdDays" label="天数" width="55"></el-table-column>
          <el-table-column prop="pdQuantity" label="数量" width="55"></el-table-column>
          <el-table-column label="单价" width="60"><template slot-scope="s">¥{{ s.row.pdPrice }}</template></el-table-column>
          <el-table-column label="小计" width="70"><template slot-scope="s">¥{{ (s.row.pdQuantity * s.row.pdPrice).toFixed(2) }}</template></el-table-column>
        </el-table>
        <div v-else style="color:#999;padding:20px 0;text-align:center;">{{ detailData.oDrug || '无药品记录' }}</div>
      </el-card>
      <el-card shadow="hover" v-if="detailData.oCheck">
        <div slot="header"><span style="font-weight:bold;color:#67C23A;">检查项目</span></div>
        <div>{{ detailData.oCheck }}</div>
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
  name: "PrescriptionList",
  data() {
    return {
      dId: "", prescriptionData: [], pageNumber: 1, size: 10, total: 0,
      detailVisible: false, detailData: {}, prescDetails: []
    };
  },
  methods: {
    async loadData() {
      try {
        const res = await request.get("order/findOrderFinish", { params: { dId: this.dId, pageNumber: this.pageNumber, size: this.size, query: "" } });
        if (res.data.status === 200) {
          var records = res.data.data.records || [];
          // 对每个订单查询处方明细，计算真实总价
          var self = this;
          var promises = records.map(function(item) {
            return request.get("prescription/findByOrder", { params: { oId: item.oId } }).then(function(r) {
              if (r.data.status === 200 && r.data.data && r.data.data.length > 0) {
                item._drugTotal = r.data.data.reduce(function(s, d) { return s + d.pdQuantity * (d.pdPrice || 0); }, 0);
              }
              return item;
            }).catch(function() { return item; });
          });
          Promise.all(promises).then(function(results) {
            self.prescriptionData = results;
            self.total = res.data.data.total || 0;
          });
        }
      } catch(e) {}
    },
    handleSizeChange(val) { this.size = val; this.loadData(); },
    handleCurrentChange(val) { this.pageNumber = val; this.loadData(); },
    async viewDetail(row) {
      this.detailData = row;
      this.prescDetails = [];
      try {
        const res = await request.get("prescription/findByOrder", { params: { oId: row.oId } });
        if (res.data.status === 200) this.prescDetails = res.data.data || [];
      } catch(e) {}
      this.detailVisible = true;
    },
    async printPrescription(row) {
      try {
        const res = await request.get("prescription/findByOrder", { params: { oId: row.oId } });
        this.prescDetails = res.data.data || [];
      } catch(e) { this.prescDetails = []; }
      const win = window.open('', '_blank');
      if (!win) return this.$message.warning("请允许弹出窗口");
      const rows = this.prescDetails.length > 0
        ? this.prescDetails.map(d => '<tr><td>'+d.drName+'</td><td>'+d.pdDosage+'</td><td>'+d.pdUsage+'</td><td>'+d.pdFrequency+'</td><td>'+d.pdDays+'</td><td>'+d.pdQuantity+'</td><td>¥'+(d.pdQuantity*d.pdPrice).toFixed(2)+'</td></tr>').join('')
        : '<tr><td colspan="7" style="text-align:center">'+(row.oDrug||'无药品')+'</td></tr>';
      win.document.write(
        '<html><head><meta charset="utf-8"><title>处方单 - #'+row.oId+'</title>'+
        '<style>body{font-family:SimSun;padding:20px}table{width:100%;border-collapse:collapse}td,th{border:1px solid #000;padding:6px;font-size:13px}h2{text-align:center}</style>'+
        '</head><body>'+
        '<h2>处方单</h2>'+
        '<p>订单编号：'+row.oId+' | 患者：'+(row.pName||'')+' | 日期：'+(row.oStart||'')+'</p>'+
        '<table><thead><tr><th>药品</th><th>用量</th><th>用法</th><th>频次</th><th>天数</th><th>数量</th><th>金额</th></tr></thead><tbody>'+rows+'</tbody></table>'+
        '<p><b>诊断：</b>'+(row.oRecord||'无')+'</p>'+
        '<p><b>医生建议：</b>'+(row.oAdvice||'无')+'</p>'+
        '<p style="text-align:right;margin-top:30px;">合计：<b>¥'+(row.oTotalPrice||'0.00')+'</b></p>'+
        '<p style="text-align:right;margin-top:50px;">医生签名：__________  日期：__________</p>'+
        '<script>window.print();<\/script>'+
        '</body></html>'
      );
      win.document.close();
    }
  },
  created() {
    const t = getToken();
    if (t) { const d = jwtDecode(t); this.dId = d.dId || d.sub; }
    this.loadData();
  }
};
</script>



