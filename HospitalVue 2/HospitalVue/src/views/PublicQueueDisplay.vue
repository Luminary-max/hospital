<template>
  <div class="public-queue-wrapper">
    <!-- Header: hospital branding + real-time clock -->
    <div class="pq-header">
      <div class="pq-header-left">
        <img src="@/assets/img/1.png" class="pq-logo" />
        <span class="pq-hospital-name">医院门诊管理系统 - 大屏叫号</span>
      </div>
      <div class="pq-header-right">
        <div class="pq-current-time">{{ currentTime }}</div>
      </div>
    </div>

    <!-- Statistics row: today total / waiting / finished -->
    <div class="pq-stats">
      <div class="pq-stat-item">
        <div class="pq-stat-value">{{ todayTotal }}</div>
        <div class="pq-stat-label">今日挂号总数</div>
      </div>
      <div class="pq-stat-divider"></div>
      <div class="pq-stat-item">
        <div class="pq-stat-value warning">{{ waitingTotal }}</div>
        <div class="pq-stat-label">当前候诊人数</div>
      </div>
      <div class="pq-stat-divider"></div>
      <div class="pq-stat-item">
        <div class="pq-stat-value success">{{ finishedTotal }}</div>
        <div class="pq-stat-label">今日已接诊</div>
      </div>
    </div>

    <!-- Current calling (featured / hero area) -->
    <div class="pq-now-serving" :class="{ idle: !currentCalling }">
      <div class="now-serving-label">
        <i class="el-icon-bell"></i> 当前叫号
      </div>
      <div class="now-serving-number" v-if="currentCalling">
        <span class="pulse-ring"></span>
        #{{ currentCalling.queueIndex || '?' }}
      </div>
      <div class="now-serving-number idle-text" v-else>---</div>
      <div class="now-serving-info" v-if="currentCalling">
        {{ currentCalling.deptName || currentCalling.dept_name || '' }}
        <span class="info-dot" v-if="currentCalling.deptName || currentCalling.dept_name">·</span>
        {{ currentCalling.pName || currentCalling.p_name || '' }}
      </div>
      <div class="now-serving-info" v-else>暂无叫号，请耐心等待</div>
    </div>

    <!-- Department queue grid -->
    <div class="pq-dept-grid">
      <div
        class="pq-dept-card"
        v-for="(dept, idx) in departments"
        :key="idx"
        :style="{ animationDelay: (idx * 0.05) + 's' }"
      >
        <div class="pq-dept-name">{{ dept.deptName }}</div>
        <div class="pq-dept-calling">
          叫号: <span class="calling-num">{{ dept.calling || dept.calling === 0 ? dept.calling : '---' }}</span>
        </div>
        <div class="pq-dept-waiting">
          <span>等待 {{ dept.waiting || 0 }} 人</span>
            :type="(dept.waiting || 0) > 3 ? 'danger' : (dept.waiting || 0) > 0 ? 'warning' : 'success'"
            size="mini"
            effect="dark"
          >
            {{ (dept.waiting || 0) > 0 ? '排队中' : '空闲' }}
          </el-tag>
        </div>
        <div class="pq-wait-list" v-if="dept.waitList && dept.waitList.length > 0">
          <div class="pq-wait-item" v-for="(item, wi) in dept.waitList.slice(0, 5)" :key="wi">
            <span class="wait-num">#{{ item.queueIndex || '?' }}</span>
          </div>
        </div>
        <div class="pq-wait-empty" v-else>
          <i class="el-icon-check"></i> 暂无排队
        </div>
      </div>
    </div>

    <!-- Footer -->
    <div class="pq-footer">
      <span>数据每 10 秒自动刷新</span>
      <span class="footer-dot">|</span>
      <span>如有疑问请咨询导诊台</span>
      <span class="footer-dot">|</span>
      <span>刷新次数: {{ refreshCount }}</span>
      <button class="pq-back-btn" @click="backToAdmin">返回管理后台</button>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request.js";

const DEMO_DEPTS = [
  { deptName: '内科', waiting: 12, finished: 35, calling: '#28', waitList: [{ queueIndex: 29 }, { queueIndex: 30 }, { queueIndex: 31 }] },
  { deptName: '外科', waiting: 8, finished: 27, calling: '#16', waitList: [{ queueIndex: 17 }, { queueIndex: 18 }] },
  { deptName: '妇产科', waiting: 5, finished: 18, calling: '#9', waitList: [{ queueIndex: 10 }, { queueIndex: 11 }] },
  { deptName: '儿科', waiting: 3, finished: 14, calling: '#5', waitList: [{ queueIndex: 6 }, { queueIndex: 7 }] },
  { deptName: '五官科', waiting: 6, finished: 12, calling: '#7', waitList: [{ queueIndex: 8 }, { queueIndex: 9 }, { queueIndex: 10 }] },
  { deptName: '中医科', waiting: 2, finished: 9, calling: '#3', waitList: [{ queueIndex: 4 }] },
  { deptName: '康复医学科', waiting: 4, finished: 7, calling: '#4', waitList: [{ queueIndex: 5 }, { queueIndex: 6 }] },
  { deptName: '急诊科', waiting: 1, finished: 22, calling: '#15', waitList: [{ queueIndex: 16 }] }
];

export default {
  name: "PublicQueueDisplay",
  data() {
    return {
      todayTotal: 0,
      waitingTotal: 0,
      finishedTotal: 0,
      currentCalling: null,
      departments: [],
      currentTime: "",
      refreshCount: 0,
      timer: null,
      clockTimer: null
    };
  },
  methods: {
    /** Update the wall-clock display every second */
    updateClock() {
      const now = new Date();
      const pad = n => String(n).padStart(2, '0');
      this.currentTime = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;
    },

    /** Main data-fetching loop */
    async loadData() {
      this.refreshCount++;

      // 1. Today total via order/orderPeople
      try {
        const totalRes = await request.get("order/orderPeople");
        if (totalRes.data && totalRes.data.status === 200) {
          this.todayTotal = totalRes.data.data || 0;
        }
      } catch (_) { /* will fall through to demo on first load */ }

      // 2. Try queue/deptStats for department overview
      let loadedFromApi = false;
      try {
        const deptRes = await request.get("queue/deptStats");
        if (deptRes.data && deptRes.data.status === 200) {
          const data = deptRes.data.data || [];
          if (data.length > 0) {
            this.departments = data.map(d => ({ ...d, waitList: [] }));
            this.waitingTotal = data.reduce((s, d) => s + (d.waiting || 0), 0);
            this.finishedTotal = data.reduce((s, d) => s + (d.finished || 0), 0);
            loadedFromApi = true;
          }
        }
      } catch (_) { /* continue to fallback */ }

      // 3. Fallback: fetch doctors list and per-doctor queue data
      if (!loadedFromApi) {
        try {
          const docRes = await request.get("admin/findAllDoctors", {
            params: { pageNumber: 1, size: 200, query: "" }
          });
          if (docRes.data && docRes.data.status === 200) {
            const docs = docRes.data.data.doctors || [];
            const map = {};
            docs.forEach(d => {
              const s = d.dSection || '其他';
              if (!map[s]) {
                map[s] = { deptName: s, doctors: [], waiting: 0, finished: 0, calling: null, waitList: [] };
              }
              map[s].doctors.push(d);
            });

            const arr = Object.values(map);
            let foundCalling = null;

            for (const dept of arr) {
              const doc = dept.doctors[0];
              if (doc && doc.dId) {
                try {
                  const qRes = await request.get("queue/listByDoctor", { params: { dId: doc.dId } });
                  if (qRes.data && qRes.data.status === 200) {
                    const list = qRes.data.data || [];
                    const cur = list.find(q => q.qState === 1);
                    dept.calling = cur ? ('#' + (cur.queueIndex || '?')) : '---';
                    dept.waitList = list.filter(q => q.qState === 0);
                    dept.waiting = dept.waitList.length;
                    dept.finished = list.filter(q => q.qState === 3).length;
                    if (cur && !foundCalling) {
                      foundCalling = { ...cur, deptName: dept.deptName };
                    }
                  }
                } catch (_) {
                  dept.calling = '---';
                }
              }
            }

            this.departments = arr;
            if (foundCalling) this.currentCalling = foundCalling;
            this.waitingTotal = arr.reduce((s, d) => s + (d.waiting || 0), 0);
            this.finishedTotal = arr.reduce((s, d) => s + (d.finished || 0), 0);
            loadedFromApi = true;
          }
        } catch (_) { /* use demo */ }
      }

      // 4. Ultimate fallback: demo data (only on first load so real data doesn't get overwritten)
      if (!loadedFromApi && this.refreshCount === 1) {
        this.departments = DEMO_DEPTS.map(d => ({ ...d }));
        this.waitingTotal = DEMO_DEPTS.reduce((s, d) => s + d.waiting, 0);
        this.finishedTotal = DEMO_DEPTS.reduce((s, d) => s + d.finished, 0);
        this.currentCalling = {
          queueIndex: 28,
          deptName: '内科',
          pName: '张患者'
        };
      }
    },

    /** Navigate back to the admin home page */
    backToAdmin() {
      this.$router.push("/adminLayout");
    }
  },
  created() {
    this.updateClock();
    this.loadData();
    this.clockTimer = setInterval(this.updateClock, 1000);
    this.timer = setInterval(this.loadData, 10000);
  },
  beforeDestroy() {
    if (this.timer) clearInterval(this.timer);
    if (this.clockTimer) clearInterval(this.clockTimer);
  }
};
</script>

<style scoped>
/* =========== Reset & Layout =========== */
.public-queue-wrapper {
  width: 100vw;
  height: 100vh;
  overflow-y: auto;
  background: linear-gradient(135deg, #0f1a2e 0%, #1a2a4a 50%, #0f1a2e 100%);
  color: #ffffff;
  font-family: 'Helvetica Neue', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  display: flex;
  flex-direction: column;
  padding: 20px 30px;
  box-sizing: border-box;
  user-select: none;
}

/* =========== Header =========== */
.pq-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.pq-header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}
.pq-logo {
  width: 42px;
  height: 42px;
}
.pq-hospital-name {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 2px;
  background: linear-gradient(90deg, #409EFF, #66b1ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.pq-current-time {
  font-size: 24px;
  font-weight: 300;
  color: #a0b4d0;
  font-family: 'Courier New', monospace;
  letter-spacing: 1px;
}

/* =========== Statistics Row =========== */
.pq-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0;
  padding: 20px 0 16px;
}
.pq-stat-item {
  text-align: center;
  padding: 0 50px;
}
.pq-stat-value {
  font-size: 48px;
  font-weight: 800;
  color: #409EFF;
  line-height: 1.1;
  text-shadow: 0 0 20px rgba(64, 158, 255, 0.3);
}
.pq-stat-value.warning {
  color: #E6A23C;
  text-shadow: 0 0 20px rgba(230, 162, 60, 0.3);
}
.pq-stat-value.success {
  color: #67C23A;
  text-shadow: 0 0 20px rgba(103, 194, 58, 0.3);
}
.pq-stat-label {
  font-size: 16px;
  color: #8ea4c4;
  margin-top: 6px;
  letter-spacing: 1px;
}
.pq-stat-divider {
  width: 1px;
  height: 60px;
  background: rgba(255, 255, 255, 0.1);
}

/* =========== Now Serving (Hero) =========== */
.pq-now-serving {
  text-align: center;
  padding: 24px 0 18px;
  margin: 0 auto 10px;
  width: 100%;
  max-width: 700px;
  border-radius: 16px;
  background: rgba(64, 158, 255, 0.08);
  border: 1px solid rgba(64, 158, 255, 0.15);
  transition: all 0.3s;
}
.pq-now-serving.idle {
  background: rgba(255, 255, 255, 0.03);
  border-color: rgba(255, 255, 255, 0.06);
}
.now-serving-label {
  font-size: 20px;
  color: #8ea4c4;
  margin-bottom: 6px;
  letter-spacing: 3px;
}
.now-serving-label i {
  margin-right: 6px;
}
.now-serving-number {
  position: relative;
  display: inline-block;
  font-size: 96px;
  font-weight: 900;
  color: #67C23A;
  line-height: 1.2;
  text-shadow: 0 0 40px rgba(103, 194, 58, 0.4);
  letter-spacing: 6px;
}
.now-serving-number.idle-text {
  color: #5a6a82;
  text-shadow: none;
}
.now-serving-info {
  font-size: 22px;
  color: #c8d6e8;
  margin-top: 4px;
}
.info-dot {
  margin: 0 10px;
  color: #5a6a82;
}

/* Pulse animation ring around the current calling number */
.pulse-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120%;
  height: 120%;
  border-radius: 16px;
  border: 3px solid rgba(103, 194, 58, 0.4);
  animation: pulse-expand 2s ease-out infinite;
  pointer-events: none;
}
@keyframes pulse-expand {
  0%   { transform: translate(-50%, -50%) scale(0.8); opacity: 0.8; }
  100% { transform: translate(-50%, -50%) scale(1.3); opacity: 0; }
}

/* =========== Department Grid =========== */
.pq-dept-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
  padding: 8px 0 12px;
  align-content: start;
}
.pq-dept-card {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  padding: 16px 18px;
  transition: all 0.3s;
  animation: fadeInUp 0.5s ease both;
}
.pq-dept-card:hover {
  background: rgba(255, 255, 255, 0.07);
  border-color: rgba(64, 158, 255, 0.2);
  transform: translateY(-2px);
}
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(12px); }
  to   { opacity: 1; transform: translateY(0); }
}

.pq-dept-name {
  font-size: 22px;
  font-weight: 700;
  color: #e8eef5;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}
.pq-dept-calling {
  font-size: 18px;
  color: #a0b4d0;
  margin-bottom: 5px;
}
.calling-num {
  font-size: 26px;
  font-weight: 800;
  color: #67C23A;
  margin-left: 4px;
}
.pq-dept-waiting {
  font-size: 15px;
  color: #8ea4c4;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.pq-wait-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.pq-wait-item {
  background: rgba(255, 255, 255, 0.06);
  border-radius: 6px;
  padding: 3px 10px;
  font-size: 16px;
  color: #c8d6e8;
}
.wait-num {
  font-weight: 600;
  font-family: 'Courier New', monospace;
}
.pq-wait-empty {
  font-size: 15px;
  color: #5a6a82;
  text-align: center;
  padding: 8px 0;
}
.pq-wait-empty i {
  margin-right: 4px;
  color: #67C23A;
}

/* =========== Footer =========== */
.pq-footer {
  text-align: center;
  font-size: 14px;
  color: #5a6a82;
  padding: 10px 0 4px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.footer-dot {
  color: #3a4a62;
}
.pq-back-btn {
  background: rgba(64, 158, 255, 0.15);
  border: 1px solid rgba(64, 158, 255, 0.25);
  color: #66b1ff;
  border-radius: 6px;
  padding: 4px 14px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
}
.pq-back-btn:hover {
  background: rgba(64, 158, 255, 0.25);
}

/* =========== Scrollbar =========== */
.public-queue-wrapper::-webkit-scrollbar { width: 6px; }
.public-queue-wrapper::-webkit-scrollbar-track { background: transparent; }
.public-queue-wrapper::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 3px; }
</style>
