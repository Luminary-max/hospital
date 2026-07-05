<template>
  <el-card>
    <div slot="header">
      <i class="el-icon-first-aid-kit"></i> 检查开单
      <el-button type="primary" size="small" style="float:right;" @click="batchSave" :loading="saving">
        <i class="el-icon-upload2"></i> 批量保存
      </el-button>
    </div>

    <!-- 患者信息 -->
    <el-card shadow="hover" style="margin-bottom:16px;">
      <div slot="header"><span style="font-weight:bold;">就诊信息</span></div>
      <el-descriptions :column="4" border size="small">
        <el-descriptions-item label="患者姓名">{{ patientName || '---' }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ orderId || '---' }}</el-descriptions-item>
        <el-descriptions-item label="医生">{{ doctorName }}</el-descriptions-item>
        <el-descriptions-item label="日期">{{ nowDate }}</el-descriptions-item>
      </el-descriptions>
      <div style="margin-top:10px;color:#909399;font-size:12px;text-align:center;">请从 <b>今日挂号</b> 或 <b>叫号面板</b> 点击「处理」或「接诊」后进入此页面，订单号会自动传入</div>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover" style="margin-bottom:16px;">
          <div slot="header"><span style="font-weight:bold;color:#409EFF;">可选检查项目</span></div>
          <el-form inline size="mini" style="margin-bottom:10px;">
            <el-form-item>
              <el-input v-model="query" placeholder="搜索检查项目" prefix-icon="el-icon-search" @keyup.enter.native="loadChecks" clearable></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="mini" @click="loadChecks">查询</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="checkItems" border stripe size="mini" height="350" style="width:100%">
            <el-table-column prop="chId" label="编号" width="70"></el-table-column>
            <el-table-column prop="chName" label="项目名称" min-width="160"></el-table-column>
            <el-table-column label="价格" width="90" align="center">
              <template slot-scope="s">¥{{ s.row.chPrice }}</template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="s">
                <el-button v-if="!isSelected(s.row.chId)" type="success" size="mini" @click="addCheck(s.row)">选择</el-button>
                <el-button v-else type="danger" size="mini" @click="removeCheck(s.row.chId)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination @current-change="p=>{pageNumber=p;loadChecks()}" :page-size="size" layout="total,prev,pager,next" :total="total" small style="margin-top:8px;"></el-pagination>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" style="margin-bottom:16px;">
          <div slot="header">
            <span style="font-weight:bold;color:#67C23A;">已选检查项目</span>
            <el-tag type="success" size="mini" style="margin-left:8px;">共 {{ selectedChecks.length }} 项</el-tag>
            <el-tag type="warning" size="mini" style="margin-left:4px;">合计 ¥{{ totalPrice }}</el-tag>
          </div>
          <el-table :data="selectedChecks" border stripe size="mini" height="350" style="width:100%">
            <el-table-column prop="chId" label="编号" width="70"></el-table-column>
            <el-table-column prop="chName" label="项目名称" min-width="160"></el-table-column>
            <el-table-column label="价格" width="90" align="center">
              <template slot-scope="s">¥{{ s.row.chPrice }}</template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="s">
                <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="removeCheck(s.row.chId)"></el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="selectedChecks.length === 0" style="text-align:center;padding:60px 0;color:#999;">请从左侧选择检查项目</div>
        </el-card>
      </el-col>
    </el-row>
  </el-card>
</template>

<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";

export default {
  name: "DoctorCheckOrder",
  data() {
    return {
      orderId: null, patientName: "", doctorName: "", doctorId: "",
      nowDate: "",
      query: "", pageNumber: 1, size: 10, total: 0,
      checkItems: [],
      selectedChecks: [],
      saving: false
    };
  },
  computed: {
    totalPrice() {
      return this.selectedChecks.reduce(function(s, c) { return s + parseFloat(c.chPrice || 0); }, 0).toFixed(2);
    }
  },
  methods: {
    loadChecks() {
      request.get("check/findAllChecks", { params: { pageNumber: this.pageNumber, size: this.size, query: this.query } }).then(res => {
        if (res.data.status === 200) {
          var d = res.data.data;
          this.checkItems = d.checks || d.records || [];
          this.total = d.total || 0;
        }
      });
    },
    isSelected(chId) {
      return this.selectedChecks.some(function(c) { return c.chId === chId; });
    },
    addCheck(row) {
      if (this.isSelected(row.chId)) return this.$message.info("该项目已选择");
      this.selectedChecks.push({ chId: row.chId, chName: row.chName, chPrice: row.chPrice });
    },
    removeCheck(chId) {
      var idx = this.selectedChecks.findIndex(function(c) { return c.chId === chId; });
      if (idx >= 0) this.selectedChecks.splice(idx, 1);
    },
    batchSave() {
      if (this.selectedChecks.length === 0) return this.$message.warning("请至少选择一个检查项目");
      if (!this.orderId) return this.$message.warning("订单号无效");
      this.saving = true;
      var details = this.selectedChecks.map(function(c) {
        return { chId: c.chId, chName: c.chName, chPrice: c.chPrice };
      });
      var self = this;
      // axios request.post(url, data) 会以JSON body方式发送
      // 这里直接用原始axios方式：POST body = details数组, params = {oId}
      request({
        method: "post",
        url: "check/batchCreateOrderChecks",
        params: { oId: self.orderId },
        data: details,
        headers: { "Content-Type": "application/json" }
      }).then(function(res) {
        if (res.data.status === 200) {
          self.$message.success("检查开单成功，共 " + self.selectedChecks.length + " 项");
          self.selectedChecks = [];
        } else {
          self.$message.error(res.data.msg || "开单失败");
        }
      }).catch(function() {
        self.$message.error("网络错误");
      }).finally(function() {
        self.saving = false;
      });
    },
    getToday() {
      var d = new Date();
      return d.getFullYear() + "-" + String(d.getMonth() + 1).padStart(2, "0") + "-" + String(d.getDate()).padStart(2, "0");
    }
  },
  created() {
    this.nowDate = this.getToday();
    var token = getToken();
    if (token) {
      var decoded = jwtDecode(token);
      this.doctorName = decoded.dName || "";
      this.doctorId = decoded.dId || "";
    }
    this.orderId = this.$route.query.oId ? parseInt(this.$route.query.oId) : null;
    this.patientName = this.$route.query.pName || "";
    this.loadChecks();
  }
};
</script>
<style scoped>
</style>

