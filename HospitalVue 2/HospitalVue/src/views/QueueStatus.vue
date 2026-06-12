<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-s-order"></i> 排队状态查询</span>
    </div>
    <el-alert title="温馨提示：请根据您的排队号码在候诊区等待叫号。" type="info" show-icon closable style="margin-bottom:20px;"></el-alert>
    <div v-if="queueInfo" style="text-align:center; padding:40px 0;">
      <div style="font-size:60px; font-weight:bold; color:#409EFF;">{{ queueInfo.q_number }}</div>
      <div style="font-size:16px; color:#666; margin:15px 0;">您的排队号码</div>
      <el-descriptions :column="2" border style="margin-top:30px; max-width:500px; margin-left:auto; margin-right:auto;">
        <el-descriptions-item label="当前叫号">{{ queueInfo.current_call || '---' }}</el-descriptions-item>
        <el-descriptions-item label="前面人数">{{ queueInfo.ahead_count || 0 }} 人</el-descriptions-item>
        <el-descriptions-item label="等待科室">{{ queueInfo.dept_name || '---' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="queueInfo.q_state === 0" type="warning">等待叫号</el-tag>
          <el-tag v-else-if="queueInfo.q_state === 1" type="success">正在就诊</el-tag>
          <el-tag v-else-if="queueInfo.q_state === 2" type="danger">已过号</el-tag>
          <el-tag v-else type="info">已完成</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <el-button type="primary" style="margin-top:30px;" @click="refreshStatus" icon="el-icon-refresh">刷新状态</el-button>
    </div>
    <div v-else style="text-align:center; padding:60px 0; color:#999;">
      <i class="el-icon-warning-outline" style="font-size:48px;"></i>
      <p style="margin-top:15px;">暂无排队信息，请先预约挂号</p>
      <el-button type="primary" @click="$router.push('/orderOperate')">去预约挂号</el-button>
    </div>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "QueueStatus",
  data() {
    return { pId: null, queueInfo: null };
  },
  methods: {
    async refreshStatus() {
      if (!this.pId) return;
      try {
        const res = await request.get("queue/listByPatient", { params: { pId: this.pId } });
        if (res.data.status === 200) {
          const list = res.data.data || [];
          this.queueInfo = list.length > 0 ? list[0] : null;
        }
      } catch(e) { console.error(e); }
    }
  },
  created() {
    const token = getToken();
    if (token) {
      const decoded = jwtDecode(token);
      this.pId = decoded.pId || decoded.sub;
    }
    this.refreshStatus();
  }
};
</script>
