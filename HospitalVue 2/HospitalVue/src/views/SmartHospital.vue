<template>
  <div class="smart-page">
    <el-row :gutter="16" class="summary-row" v-if="metrics.length">
      <el-col :span="6" v-for="item in metrics" :key="item.label">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-label">{{ item.label }}</div>
          <div class="metric-value">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="main-card">
      <div slot="header" class="card-header">
        <span><i class="el-icon-cpu"></i> 智慧医疗扩展中心</span>
      </div>

      <el-alert
        v-if="lastError"
        class="page-alert"
        type="error"
        :title="lastError"
        show-icon
        :closable="true"
        @close="lastError=''"
      />

      <el-tabs v-if="visibleTabs.length" v-model="activeTab" type="border-card">
        <el-tab-pane v-if="canUse('diagnosis')" label="AI辅助诊断" name="diagnosis">
          <el-form label-width="90px">
            <el-form-item label="患者ID">
              <el-input v-model="diagnosisForm.pId" placeholder="可选，填写后会结合历史病历"></el-input>
            </el-form-item>
            <el-form-item label="症状描述">
              <el-input type="textarea" :rows="5" v-model="diagnosisForm.symptoms" placeholder="例如：发热2天、咽痛、咳嗽，最高体温38.8℃"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-magic-stick" :loading="loading.diagnosis" @click="runDiagnosis">生成辅助判断</el-button>
            </el-form-item>
          </el-form>
          <result-box title="诊断建议" :risk="diagnosisResult.riskLevel" :content="diagnosisResult.content" :status="diagnosisResult.status"></result-box>
        </el-tab-pane>

        <el-tab-pane v-if="canUse('health')" label="健康档案" name="health">
          <div class="toolbar" v-if="isPatient">
            <el-tag type="info">仅展示当前登录患者档案</el-tag>
            <el-button type="primary" icon="el-icon-document-add" :loading="loading.health" @click="buildHealth">生成/更新我的档案</el-button>
            <el-button icon="el-icon-refresh" @click="loadHealth">刷新</el-button>
          </div>
          <div class="toolbar" v-else>
            <el-input v-model="healthQuery" placeholder="患者姓名或ID" clearable style="width:260px"></el-input>
            <el-input v-model="healthPatientId" placeholder="输入患者ID生成档案" style="width:200px"></el-input>
            <el-button type="primary" icon="el-icon-document-add" :loading="loading.health" @click="buildHealth">生成/更新</el-button>
            <el-button icon="el-icon-search" @click="loadHealth">查询</el-button>
          </div>
          <el-table :data="healthList" border stripe empty-text="暂无健康档案" @row-click="showHealthDetail">
            <el-table-column prop="p_id" label="患者ID" width="90"></el-table-column>
            <el-table-column prop="p_name" label="姓名" width="90"></el-table-column>
            <el-table-column prop="risk_level" label="风险" width="90">
              <template slot-scope="s"><el-tag :type="riskTag(s.row.risk_level)">{{ s.row.risk_level || '低' }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="summary" label="档案摘要" show-overflow-tooltip></el-table-column>
            <el-table-column prop="update_time" label="更新时间" width="170"></el-table-column>
            <el-table-column label="操作" width="90" fixed="right">
              <template slot-scope="s">
                <el-button type="text" size="mini" @click.stop="showHealthDetail(s.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <detail-card
            v-if="healthDetail.content"
            title="健康档案详情"
            :items="healthDetail.items"
            :content="healthDetail.content"
          ></detail-card>
          <result-box title="健康档案结果" :risk="healthResult.riskLevel" :content="healthResult.content" :status="healthResult.status"></result-box>
        </el-tab-pane>

        <el-tab-pane v-if="canUse('queue')" label="智能排队" name="queue">
          <div class="toolbar">
            <el-input v-model="queueDoctorId" placeholder="医生ID，可留空自动按最近挂号预测" style="width:280px"></el-input>
            <el-button type="primary" icon="el-icon-timer" :loading="loading.queue" @click="predictQueue">预测等待时间</el-button>
          </div>
          <result-box title="排队预测结果" :risk="queueResult.riskLevel" :content="queueResult.content" :status="queueResult.status"></result-box>
          <el-table :data="queueResult.queue || []" border stripe style="margin-top:14px" empty-text="暂无今日队列" @row-click="showQueueDetail">
            <el-table-column prop="q_number" label="号码" width="90">
              <template slot-scope="s">{{ s.row.q_number || s.row.q_id }}</template>
            </el-table-column>
            <el-table-column prop="p_name" label="患者" width="100"></el-table-column>
            <el-table-column prop="q_state" label="状态" width="90">
              <template slot-scope="s">{{ queueState(s.row.q_state) }}</template>
            </el-table-column>
            <el-table-column prop="q_create_time" label="取号时间"></el-table-column>
            <el-table-column label="操作" width="90" fixed="right">
              <template slot-scope="s">
                <el-button type="text" size="mini" @click.stop="showQueueDetail(s.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <detail-card
            v-if="queueDetail.content"
            title="排队详情"
            :items="queueDetail.items"
            :content="queueDetail.content"
          ></detail-card>
        </el-tab-pane>

        <el-tab-pane v-if="canUse('report')" label="报告解析" name="report">
          <el-form label-width="90px">
            <el-form-item label="检查单ID">
              <el-input v-model="reportForm.ocId" placeholder="可选，输入后会尝试读取已有检查结果"></el-input>
            </el-form-item>
            <el-form-item label="患者ID">
              <el-input v-model="reportForm.pId" :disabled="isPatient" placeholder="可选"></el-input>
            </el-form-item>
            <el-form-item label="检验结果">
              <el-input type="textarea" :rows="5" v-model="reportForm.result" placeholder="录入血常规、影像描述或报告结论，异常值可写 ↑ ↓ 偏高 阳性"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-document-checked" :loading="loading.report" @click="analyzeReport">自动解析</el-button>
            </el-form-item>
          </el-form>
          <result-box title="报告结论" :risk="reportResult.riskLevel" :content="reportResult.content" :status="reportResult.status"></result-box>
        </el-tab-pane>

        <el-tab-pane v-if="canUse('prescription')" label="处方审查" name="prescription">
          <el-form label-width="90px">
            <el-form-item label="患者ID"><el-input v-model="prescriptionForm.pId"></el-input></el-form-item>
            <el-form-item label="诊断"><el-input v-model="prescriptionForm.diagnosis"></el-input></el-form-item>
            <el-form-item label="过敏史"><el-input v-model="prescriptionForm.allergyHistory"></el-input></el-form-item>
            <el-form-item label="处方明细">
              <el-input type="textarea" :rows="5" v-model="prescriptionForm.items" placeholder="药名、剂量、频次、天数"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-first-aid-kit" :loading="loading.prescription" @click="reviewPrescription">合理性检查</el-button>
            </el-form-item>
          </el-form>
          <result-box title="审查结果" :risk="prescriptionResult.riskLevel" :content="prescriptionResult.content" :status="prescriptionResult.status"></result-box>
        </el-tab-pane>
      </el-tabs>

      <el-empty v-else description="当前角色暂无智慧扩展功能"></el-empty>
    </el-card>
  </div>
</template>

<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";

const ResultBox = {
  props: ["title", "risk", "content", "status"],
  computed: {
    tag() {
      if (this.status === "error") return "danger";
      return this.risk === "高危" ? "danger" : (this.risk === "中" ? "warning" : "success");
    },
    statusText() {
      return this.status === "error" ? "失败" : "成功";
    }
  },
  render(h) {
    if (!this.content) return null;
    const header = h("div", { slot: "header" }, [
      h("b", this.title),
      h("el-tag", { props: { size: "mini", type: this.tag }, style: { marginLeft: "10px" } }, this.statusText),
      this.risk ? h("el-tag", { props: { size: "mini", type: this.tag }, style: { marginLeft: "6px" } }, this.risk) : null
    ]);
    return h("el-card", { class: "result-box", props: { shadow: "never" } }, [
      header,
      h("pre", String(this.content))
    ]);
  }
};

const DetailCard = {
  props: ["title", "items", "content"],
  render(h) {
    const header = h("div", { slot: "header" }, [h("b", this.title)]);
    const details = h("div", { class: "detail-grid" }, (this.items || []).map(item =>
      h("div", { class: "detail-item", key: item.label }, [
        h("span", item.label),
        h("strong", item.value || "-")
      ])
    ));
    const body = [header, details];
    if (this.content) body.push(h("pre", String(this.content)));
    return h("el-card", { class: "detail-card", props: { shadow: "never" } }, body);
  }
};

export default {
  name: "SmartHospital",
  components: { ResultBox, DetailCard },
  data() {
    return {
      currentRole: "guest",
      currentUser: {},
      permissions: [],
      activeTab: "",
      lastError: "",
      roleTabs: {
        admin: [],
        doctor: ["diagnosis", "health", "report", "prescription"],
        patient: ["health", "queue", "report"],
        pharmacist: ["health", "report", "prescription"],
        nurse: [],
        cashier: []
      },
      loading: { diagnosis:false, health:false, queue:false, report:false, prescription:false },
      diagnosisForm: { pId:"", symptoms:"" },
      diagnosisResult: {},
      healthQuery: "",
      healthPatientId: "",
      healthList: [],
      healthResult: {},
      healthDetail: {},
      queueDoctorId: "",
      queueResult: {},
      queueDetail: {},
      reportForm: { ocId:"", pId:"", result:"" },
      reportResult: {},
      prescriptionForm: { pId:"", diagnosis:"", allergyHistory:"", items:"" },
      prescriptionResult: {}
    };
  },
  computed: {
    isPatient() {
      return this.currentRole === "patient";
    },
    visibleTabs() {
      const tabs = this.roleTabs[this.currentRole] || [];
      if (this.currentRole !== "pharmacist") return tabs;
      return tabs.filter(tab => {
        if (tab === "health") return this.hasPermission("health_profile");
        if (tab === "report") return this.hasPermission("report");
        if (tab === "prescription") return this.hasPermission("prescription_review");
        return true;
      });
    },
    metrics() {
      const labels = {
        diagnosis: ["AI辅助诊断", "医生端"],
        health: ["健康档案", this.isPatient ? "本人档案" : "医患药师"],
        report: ["报告解析", "异常提示"],
        prescription: ["处方审查", "合理用药"],
        queue: ["排队预测", "患者端"]
      };
      return this.visibleTabs.slice(0, 4).map(tab => ({ label: labels[tab][0], value: labels[tab][1] }));
    }
  },
  methods: {
    canUse(tab) {
      return this.visibleTabs.indexOf(tab) >= 0;
    },
    hasPermission(permission) {
      return this.permissions.indexOf("*") >= 0 || this.permissions.indexOf(permission) >= 0;
    },
    riskTag(r) {
      return r === "高危" ? "danger" : (r === "中" ? "warning" : "success");
    },
    queueState(s) {
      return ["等待中", "就诊中", "已过号", "已完成"][Number(s)] || "未知";
    },
    cleanLlmText(text) {
      return text === undefined || text === null ? "" : String(text).replace(/\*\*/g, "");
    },
    value(row, key) {
      return row && row[key] !== undefined && row[key] !== null ? this.cleanLlmText(row[key]) : "";
    },
    resultContent(data, fields) {
      if (!data) return "";
      for (let i = 0; i < fields.length; i++) {
        if (data[fields[i]]) return this.cleanLlmText(data[fields[i]]);
      }
      if (data.message) return this.cleanLlmText(data.message);
      return this.cleanLlmText(JSON.stringify(data, null, 2));
    },
    successCard(content, riskLevel) {
      return { status: "success", riskLevel: riskLevel || "", content: content || "操作成功" };
    },
    errorCard(content) {
      return { status: "error", riskLevel: "", content: content || "操作失败" };
    },
    splitPermissions(value) {
      if (!value) return [];
      if (Array.isArray(value)) return value;
      return String(value).split(",").map(item => item.trim()).filter(Boolean);
    },
    handleResponse(res) {
      const body = res && res.data ? res.data : {};
      if (body.status !== 200) {
        this.lastError = body.msg || "操作失败";
        this.$message.error(this.lastError);
        return { ok: false, message: this.lastError, data: body.data || null };
      }
      this.lastError = "";
      return { ok: true, message: body.msg || "操作成功", data: body.data || {} };
    },
    handleError(error) {
      const msg = error && error.response && error.response.data && error.response.data.msg
        ? error.response.data.msg
        : "请求失败，请检查后端服务或网络";
      this.lastError = msg;
      this.$message.error(msg);
      return msg;
    },
    async runDiagnosis() {
      if (!this.diagnosisForm.symptoms) {
        this.diagnosisResult = this.errorCard("请输入症状后再提交。");
        return this.$message.warning("请输入症状");
      }
      this.loading.diagnosis = true;
      try {
        const res = await request.post("smart/ai/diagnosis", this.diagnosisForm);
        const result = this.handleResponse(res);
        if (result.ok) {
          const data = result.data;
          this.diagnosisResult = this.successCard(this.resultContent(data, ["suggestion", "answer"]), data.riskLevel);
        } else {
          this.diagnosisResult = this.errorCard(result.message);
        }
      } catch (e) {
        this.diagnosisResult = this.errorCard(this.handleError(e));
      } finally {
        this.loading.diagnosis = false;
      }
    },
    async buildHealth() {
      if (!this.healthPatientId) {
        this.healthResult = this.errorCard("请输入患者ID后再生成健康档案。");
        return this.$message.warning("请输入患者ID");
      }
      this.loading.health = true;
      try {
        const res = await request.get("smart/health/build", { params:{ pId:this.healthPatientId } });
        const result = this.handleResponse(res);
        if (result.ok) {
          const data = result.data;
          this.healthResult = this.successCard(this.resultContent(data, ["summary", "message"]), data.riskLevel);
          this.$message.success("健康档案已更新");
          await this.loadHealth();
        } else {
          this.healthResult = this.errorCard(result.message);
        }
      } catch (e) {
        this.healthResult = this.errorCard(this.handleError(e));
      } finally {
        this.loading.health = false;
      }
    },
    async loadHealth() {
      try {
        const params = this.isPatient ? {} : { query:this.healthQuery };
        const res = await request.get("smart/health/list", { params });
        const result = this.handleResponse(res);
        if (result.ok) {
          this.healthList = result.data || [];
          if (!this.healthList.length) this.healthDetail = {};
        }
      } catch (e) {
        this.handleError(e);
      }
    },
    showHealthDetail(row) {
      if (!row) return;
      const content = [
        "档案摘要：" + (this.value(row, "summary") || "暂无"),
        "慢病/既往史：" + (this.value(row, "chronic_history") || "暂无"),
        "家族史：" + (this.value(row, "family_history") || "暂无"),
        "过敏史：" + (this.value(row, "allergy_history") || "暂无"),
        "最近就诊时间：" + (this.value(row, "last_visit_time") || "暂无")
      ].join("\n");
      this.healthDetail = {
        items: [
          { label: "患者ID", value: this.value(row, "p_id") },
          { label: "姓名", value: this.value(row, "p_name") },
          { label: "性别", value: this.value(row, "p_gender") },
          { label: "年龄", value: this.value(row, "p_age") },
          { label: "风险等级", value: this.value(row, "risk_level") || "低" },
          { label: "更新时间", value: this.value(row, "update_time") }
        ],
        content
      };
    },
    async predictQueue() {
      this.loading.queue = true;
      try {
        const params = this.queueDoctorId ? { dId:this.queueDoctorId } : {};
        const res = await request.get("smart/queue/predict", { params });
        const result = this.handleResponse(res);
        if (result.ok) {
          const data = result.data;
          const content = data.peakHint === "暂无可预测队列"
            ? "暂无可预测队列，请先完成挂号或输入医生ID。"
            : "医生 " + data.doctorId + " 当前候诊 " + data.waiting + " 人，预计等待 " + data.estimateMinutes + " 分钟，状态：" + data.peakHint;
          this.queueResult = Object.assign({}, data, this.successCard(content, ""));
          this.queueDetail = {};
        } else {
          this.queueResult = this.errorCard(result.message);
          this.queueDetail = {};
        }
      } catch (e) {
        this.queueResult = this.errorCard(this.handleError(e));
        this.queueDetail = {};
      } finally {
        this.loading.queue = false;
      }
    },
    showQueueDetail(row) {
      if (!row) return;
      const content = [
        "当前状态：" + this.queueState(row.q_state),
        "取号时间：" + (this.value(row, "q_create_time") || "暂无"),
        "叫号时间：" + (this.value(row, "q_call_time") || "暂无"),
        "完成时间：" + (this.value(row, "q_finish_time") || "暂无")
      ].join("\n");
      this.queueDetail = {
        items: [
          { label: "队列号", value: this.value(row, "q_number") || this.value(row, "q_id") },
          { label: "订单ID", value: this.value(row, "o_id") },
          { label: "患者ID", value: this.value(row, "p_id") },
          { label: "患者姓名", value: this.value(row, "p_name") },
          { label: "医生ID", value: this.value(row, "d_id") },
          { label: "状态", value: this.queueState(row.q_state) }
        ],
        content
      };
    },
    async analyzeReport() {
      if (!this.reportForm.result && !this.reportForm.ocId) {
        this.reportResult = this.errorCard("请输入检查结果或检查单ID后再提交。");
        return this.$message.warning("请输入检查结果或检查单ID");
      }
      this.loading.report = true;
      try {
        const res = await request.post("smart/report/analyze", this.reportForm);
        const result = this.handleResponse(res);
        if (result.ok) {
          const data = result.data;
          this.reportResult = this.successCard(this.resultContent(data, ["summary", "conclusion"]), data.riskLevel);
        } else {
          this.reportResult = this.errorCard(result.message);
        }
      } catch (e) {
        this.reportResult = this.errorCard(this.handleError(e));
      } finally {
        this.loading.report = false;
      }
    },
    async reviewPrescription() {
      if (!this.prescriptionForm.items) {
        this.prescriptionResult = this.errorCard("请输入处方明细后再提交。");
        return this.$message.warning("请输入处方明细");
      }
      this.loading.prescription = true;
      try {
        const res = await request.post("smart/prescription/review", this.prescriptionForm);
        const result = this.handleResponse(res);
        if (result.ok) {
          const data = result.data;
          this.prescriptionResult = this.successCard(this.resultContent(data, ["review", "summary"]), data.riskLevel);
        } else {
          this.prescriptionResult = this.errorCard(result.message);
        }
      } catch (e) {
        this.prescriptionResult = this.errorCard(this.handleError(e));
      } finally {
        this.loading.prescription = false;
      }
    },
    fillIdentity() {
      const token = getToken();
      if (!token) return;
      const d = jwtDecode(token);
      this.currentUser = d || {};
      if (this.$route.path.indexOf("doctor") >= 0 || d.dId) this.currentRole = "doctor";
      else if (this.$route.path.indexOf("patient") >= 0 || d.pId) this.currentRole = "patient";
      else if (d.staffRole) this.currentRole = d.staffRole;
      else if (d.aId) this.currentRole = "admin";

      this.permissions = this.splitPermissions(d.permissions);
      this.diagnosisForm.pId = d.pId || "";
      this.healthPatientId = d.pId || "";
      this.reportForm.pId = d.pId || "";
      this.prescriptionForm.pId = d.pId || "";
      this.queueDoctorId = d.dId || "";
    },
    async loadCurrentPermissions() {
      try {
        const res = await request.get("smart/role/current");
        if (res.data.status === 200 && res.data.data) {
          this.permissions = res.data.data.permissionList || this.splitPermissions(res.data.data.permissions);
        }
      } catch (e) {
        // Keep token permissions or role defaults in the UI; backend still enforces access.
      }
    },
    normalizeActiveTab() {
      if (this.visibleTabs.indexOf(this.activeTab) < 0) this.activeTab = this.visibleTabs[0] || "";
    }
  },
  async created() {
    this.fillIdentity();
    await this.loadCurrentPermissions();
    this.normalizeActiveTab();
    if (this.canUse("health")) this.loadHealth();
  }
};
</script>

<style scoped>
.smart-page { min-height: 100%; }
.summary-row { margin-bottom: 16px; }
.metric-card { border-radius: 6px; }
.metric-label { color: #909399; font-size: 13px; }
.metric-value { margin-top: 8px; color: #303133; font-size: 24px; font-weight: 700; }
.main-card { border-radius: 6px; }
.card-header { display:flex; justify-content:space-between; align-items:center; }
.page-alert { margin-bottom: 14px; }
.toolbar { display:flex; gap:10px; align-items:center; margin: 0 0 14px; flex-wrap:wrap; }
.result-box { margin-top: 14px; background: #fbfdff; }
.result-box pre { margin:0; white-space:pre-wrap; line-height:1.7; font-family: inherit; color:#303133; }
</style>
