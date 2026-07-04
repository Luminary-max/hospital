<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-s-order"></i> 排队状态查询</span>
      <el-button size="mini" type="primary" plain style="float:right;" @click="refreshStatus" :loading="refreshing">
        <i class="el-icon-refresh"></i> 刷新
      </el-button>
    </div>

    <el-alert title="温馨提示：请根据您的排队号码在候诊区等待叫号。系统每30秒自动刷新。" type="info" show-icon :closable="false" style="margin-bottom:20px;"></el-alert>

    <div v-if="loading" style="text-align:center;padding:60px 0;">
      <i class="el-icon-loading" style="font-size:32px;color:#409EFF;"></i>
      <p style="color:#999;margin-top:10px;">正在查询排队信息...</p>
    </div>

    <div v-else-if="queueInfo" class="queue-card">
      <!-- 排队号码大展示 -->
      <div class="queue-number-section">
        <div class="queue-label">您的排队序号</div>
        <div class="queue-number" :class="'q-state-' + queueInfo.q_state">#{{ queueInfo.queueIndex || '---' }}</div>
      </div>

      <el-divider></el-divider>

      <div class="queue-stats">
        <div class="queue-stat-item">
          <div class="queue-stat-value">{{ queueInfo.ahead_count || queueInfo.aheadCount || 0 }}</div>
          <div class="queue-stat-label">前面人数</div>
        </div>
        <div class="queue-stat-divider"></div>
        <div class="queue-stat-item">
          <div class="queue-stat-value" v-if="estimatedWait">{{ estimatedWait }}<span style="font-size:16px;">分钟</span></div>
          <div class="queue-stat-value" v-else>---</div>
          <div class="queue-stat-label">预计等待</div>
        </div>
        <div class="queue-stat-divider"></div>
        <div class="queue-stat-item">
          <div class="queue-stat-value" :class="'q-state-' + queueInfo.q_state">
            {{ queueStatusMap[queueInfo.q_state] || '未知' }}
          </div>
          <div class="queue-stat-label">当前状态</div>
        </div>
      </div>

      <el-divider></el-divider>

      <el-descriptions :column="2" border size="small" style="max-width:500px;margin:0 auto;">
        <el-descriptions-item label="当前叫号">{{ currentCall || '---' }}</el-descriptions-item>
        <el-descriptions-item label="等待科室">{{ queueInfo.dept_name || queueInfo.deptName || queueInfo.dSection || '---' }}</el-descriptions-item>
        <el-descriptions-item label="医生">{{ queueInfo.dName || '---' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="queueInfo.q_state === 0 || queueInfo.qState === 0" type="warning">等待叫号</el-tag>
          <el-tag v-else-if="queueInfo.q_state === 1 || queueInfo.qState === 1" type="success">正在就诊</el-tag>
          <el-tag v-else-if="queueInfo.q_state === 2 || queueInfo.qState === 2" type="danger">已过号</el-tag>
          <el-tag v-else type="info">已完成</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <div class="queue-actions">
        <el-button type="primary" size="medium" @click="refreshStatus" icon="el-icon-refresh">刷新状态</el-button>
        <el-button size="medium" plain @click="$router.push('/myOrder')">查看挂号记录</el-button>
      </div>
    </div>

    <div v-else style="text-align:center;padding:80px 0;color:#999;">
      <i class="el-icon-warning-outline" style="font-size:56px;"></i>
      <p style="margin-top:15px;font-size:15px;">暂无排队信息，请先预约挂号</p>
      <p style="color:#c0c4cc;font-size:13px;margin-bottom:20px;">预约挂号后系统会自动为您分配排队号码</p>
      <el-button type="primary" size="medium" @click="$router.push('/orderOperate')">去预约挂号</el-button>
    </div>

    <!-- 自动刷新倒计时 -->
    <div class="auto-refresh-hint" v-if="queueInfo">
      <i class="el-icon-time"></i> 下次刷新：{{ countdown }}秒
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
    return {
      pId: null,
      queueInfo: null,
      currentCall: null,
      estimatedWait: null,
      loading: true,
      refreshing: false,
      countdown: 30,
      timer: null,
      queueStatusMap: { 0: "等待叫号", 1: "正在就诊", 2: "已过号", 3: "已完成" }
    };
  },
  methods: {
    async refreshStatus() {
      if (!this.pId) { this.loading = false; return; }
      this.refreshing = true;
      try {
        const res = await request.get("queue/listByPatient", { params: { pId: this.pId } });
        if (res.data.status === 200) {
          const list = res.data.data || [];
          this.queueInfo = list.length > 0 ? list[0] : null;
          if (this.queueInfo) {
            this.estimatedWait = this.queueInfo.ahead_count || this.queueInfo.aheadCount || 0;
            this.estimatedWait = this.estimatedWait * 5; // 平均每人5分钟
            // 获取当前叫号
            if (this.queueInfo.dId) {
              try {
                const dr = await request.get("queue/listByDoctor", { params: { dId: this.queueInfo.dId } });
                if (dr.data.status === 200) {
                  const qList = dr.data.data || [];
                  const current = qList.find(function(q) { return q.qState === 1 || q.q_status === 1; });
                  this.currentCall = current ? ("#" + (current.queueIndex || '?')) : "暂无叫号";
                }
              } catch(e) {}
            }
          } else {
            this.currentCall = null;
            this.estimatedWait = null;
          }
        }
      } catch(e) {}
      this.refreshing = false;
      this.loading = false;
      this.countdown = 30; // 重置倒计时
    },
    startAutoRefresh() {
      this.timer = setInterval(function() {
        this.countdown--;
        if (this.countdown <= 0) {
          this.refreshStatus();
        }
      }.bind(this), 1000);
    }
  },
  created() {
    const token = getToken();
    if (token) {
      const decoded = jwtDecode(token);
      this.pId = decoded.pId || decoded.sub;
    }
    this.refreshStatus();
    this.startAutoRefresh();
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  }
};
</script>
<style scoped>
.queue-card { text-align: center; padding: 20px 0; }
.queue-number-section { padding: 20px 0; }
.queue-label { font-size: 14px; color: #909399; margin-bottom: 10px; }
.queue-number { font-size: 72px; font-weight: 800; color: #409EFF; line-height: 1; letter-spacing: 4px; }
.queue-number.q-state-1 { color: #67C23A; }
.queue-number.q-state-2 { color: #F56C6C; }
.queue-number.q-state-3 { color: #909399; }
.queue-stats { display: flex; justify-content: center; align-items: center; gap: 0; padding: 10px 0; }
.queue-stat-item { padding: 0 30px; }
.queue-stat-value { font-size: 32px; font-weight: 700; color: #303133; }
.queue-stat-value.q-state-1 { color: #67C23A; }
.queue-stat-value.q-state-2 { color: #F56C6C; }
.queue-stat-label { font-size: 12px; color: #909399; margin-top: 4px; }
.queue-stat-divider { width: 1px; height: 50px; background: #E4E7ED; }
.queue-actions { margin-top: 25px; display: flex; justify-content: center; gap: 12px; }
.auto-refresh-hint { text-align: center; padding: 10px 0 0; font-size: 12px; color: #c0c4cc; }
</style>
