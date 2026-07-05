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
          <span style="font-size:18px; margin-left:20px;">{{ currentPatient.p_name }}</span>
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
        <template slot-scope="s">{{ s.row.queueIndex || s.row.q_id }}</template>
      </el-table-column>
      <el-table-column prop="p_name" label="患者姓名" width="90" align="center"></el-table-column>
      <el-table-column prop="q_create_time" label="取号时间" min-width="180"></el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="s"><el-tag :type="s.row.q_state===0?'warning':s.row.q_state===2?'danger':'success'" size="mini">{{ s.row.q_state===0?'等待中':s.row.q_state===2?'已过号':'已就诊' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center">
        <template slot-scope="s"><el-button v-if="s.row.q_state===2" type="primary" size="mini" @click="reQueue(s.row)">重新排入</el-button></template>
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
          this.currentPatient = data.find(p => p.q_state === 1) || null;
          this.waitingList = data.filter(p => p.q_state === 0 || p.q_state === 2);
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
        this.$router.push("/dealOrder?oId=" + this.currentPatient.oId);
      }
    },
    async skipNumber() {
      if (!this.currentPatient) return;
      try {
        const res = await request.get("queue/skipNumber", { params: { qId: this.currentPatient.q_id } });
        if (res.data.status === 200) {
          this.$message.warning("已标记为过号");
          this.loadQueue();
        }
      } catch(e) {}
    },
    async reQueue(row) {
      try {
        const res = await request.get("queue/callNext", { params: { dId: this.dId, reQueue: row.q_id } });
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



