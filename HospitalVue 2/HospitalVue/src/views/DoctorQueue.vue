<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-s-promotion"></i> 医生叫号面板</span>
      <el-button type="success" size="small" style="float:right; margin-left:10px;" @click="callNext"
        :disabled="waitingList.length === 0">
        <i class="el-icon-phone"></i> 呼叫下一号
      </el-button>
      <el-button type="warning" size="small" style="float:right;" @click="refreshQueue">
        <i class="el-icon-refresh"></i> 刷新
      </el-button>
    </div>
    <!-- 当前呼叫患者 -->
    <el-card shadow="always" v-if="currentPatient" style="margin-bottom:20px; background:#f0f9eb;">
      <div style="display:flex; align-items:center; justify-content:space-between;">
        <div>
          <span style="font-size:24px; font-weight:bold; color:#67C23A;">#{{ currentPatient.queueIndex || '?' }}</span>
          <span style="font-size:18px; margin-left:20px;">{{ currentPatient.pName }}</span>
          <el-tag type="success" style="margin-left:10px;">正在就诊</el-tag>
        </div>
        <div>
          <el-button type="primary" size="small" @click="startConsult">开始接诊</el-button>
          <el-button type="danger" size="small" @click="skipNumber">过号处理</el-button>
        </div>
      </div>
    </el-card>
    <el-alert v-else title="暂无呼叫患者，请点击「呼叫下一号」" type="info" show-icon style="margin-bottom:20px;"></el-alert>
    <!-- 候诊列表 -->
    <h4>候诊列表（{{ waitingList.length }} 人等待）</h4>
    <el-table :data="waitingList" border stripe style="width:100%">
      <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
      <el-table-column label="序号" width="80" align="center">
        <template slot-scope="s">{{ s.row.queueIndex || s.row.qId }}</template>
      </el-table-column>
      <el-table-column prop="pName" label="患者姓名" width="90" align="center"></el-table-column>
      <el-table-column prop="qCreateTime" label="取号时间" min-width="180"></el-table-column>
      <el-table-column label="状态" width="150" align="center">
        <template slot-scope="s">
          <el-tag v-if="s.row.tLevel === 2" type="danger" effect="dark" size="mini" style="font-weight:bold;">急诊</el-tag>
          <el-tag v-else-if="s.row.tLevel === 1" type="warning" effect="dark" size="mini" style="font-weight:bold;">优先</el-tag>
          <el-tag v-else-if="s.row.oState === 1" type="" size="mini">已分诊</el-tag>
          <el-tag v-else-if="s.row.qState===0" type="warning" size="mini">等待中</el-tag>
          <el-tag v-else-if="s.row.qState===2" type="danger" size="mini">已过号</el-tag>
          <el-tag v-else size="mini">已就诊</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center">
        <template slot-scope="s"><el-button v-if="s.row.qState===2" type="primary" size="mini" @click="reQueue(s.row)">重新排入</el-button></template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "DoctorQueue",
  data() {
    return {
      dId: "",
      currentPatient: null,
      waitingList: []
    };
  },
  methods: {
    refreshQueue() {
      this.loadQueue();
    },
    async loadQueue() {
      try {
        const res = await request.get("queue/listByDoctor", { params: { dId: this.dId } });
        if (res.data.status === 200) {
          const data = res.data.data || [];
          this.currentPatient = data.find(p => p.qState === 1) || null;
          // 去重
          var seen = {};
          var unique = [];
          data.forEach(function(item) {
            if (item.qState !== 1 && item.qState !== 3) {
              if (!seen[item.oId]) {
                seen[item.oId] = true;
                unique.push(item);
              }
            }
          });
          this.waitingList = unique;
        }
      } catch(e) {}
    },
    async callNext() {
      try {
        const res = await request.get("queue/callNext", { params: { dId: this.dId } });
        if (res.data.status === 200) {
          this.$message.success("呼叫成功！");
          this.loadQueue();
        }
      } catch(e) {}
    },
    startConsult() {
      if (this.currentPatient) {
        this.$router.push("/dealOrder?oId=" + this.currentPatient.oId + "&pId=" + (this.currentPatient.pId || ''));
      }
    },
    async skipNumber() {
      if (!this.currentPatient) return;
      try {
        const res = await request.get("queue/skipNumber", { params: { qId: this.currentPatient.qId } });
        if (res.data.status === 200) {
          this.$message.warning("已标记为过号");
          this.loadQueue();
        }
      } catch(e) {}
    },
    async reQueue(row) {
      try {
        const res = await request.get("queue/callNext", { params: { dId: this.dId, reQueue: row.qId } });
        if (res.data.status === 200) {
          this.$message.success("已重新排入队列");
          this.loadQueue();
        }
      } catch(e) {}
    }
  },
  created() {
    const token = getToken();
    if (token) {
      const decoded = jwtDecode(token);
      this.dId = decoded.dId || decoded.sub;
    }
    this.loadQueue();
  }
};
</script>







