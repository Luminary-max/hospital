<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-reading"></i> 我的病历</span>
      <el-button size="mini" type="primary" plain style="float:right;" @click="printCurrent" v-if="emrList.length > 0">
        <i class="el-icon-printer"></i> 打印
      </el-button>
    </div>

    <div v-if="loading" style="text-align:center;padding:60px 0;">
      <i class="el-icon-loading" style="font-size:32px;color:#409EFF;"></i>
      <p style="color:#999;margin-top:10px;">加载中...</p>
    </div>

    <div v-else-if="emrList.length === 0" style="text-align:center;padding:80px 0;color:#999;">
      <i class="el-icon-document" style="font-size:48px;"></i>
      <p style="margin-top:15px;font-size:15px;">暂无病历记录</p>
      <p style="color:#c0c4cc;font-size:13px;">就诊后医生会为您创建门诊病历</p>
    </div>

    <el-timeline v-else>
      <el-timeline-item v-for="(item, index) in emrList" :key="index"
        :timestamp="item.createTime || item.oStart || '---'"
        placement="top"
        :color="index === 0 ? '#409EFF' : '#E4E7ED'"
        :size="index === 0 ? 'large' : 'normal'">
        <el-card shadow="hover" class="emr-card" :class="{ 'emr-card-latest': index === 0 }">
          <div class="emr-header">
            <div class="emr-header-left">
              <el-tag type="primary" size="small" v-if="index === 0" effect="dark">最新</el-tag>
              <span class="emr-doctor"><i class="el-icon-user"></i> {{ item.dName || item.d_name || '---' }}</span>
              <span class="emr-date"><i class="el-icon-time"></i> {{ item.createTime || item.create_time || '---' }}</span>
            </div>
            <div class="emr-header-right">
              <el-button type="primary" size="mini" plain @click="viewDetail(item)">查看详情</el-button>
              <el-button size="mini" plain @click="exportSingle(item)"><i class="el-icon-download"></i> 导出</el-button>
            </div>
          </div>
          <el-divider style="margin:10px 0;"></el-divider>
          <div class="emr-summary">
            <div class="summary-item">
              <span class="summary-label">主诉</span>
              <span class="summary-text">{{ item.chiefComplaint || item.chief_complaint || '无' }}</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">诊断</span>
              <span class="summary-text diagnosis">{{ item.diagnosis || '无' }}</span>
            </div>
            <div class="summary-item" v-if="item.treatmentPlan || item.treatment_plan">
              <span class="summary-label">处理意见</span>
              <span class="summary-text">{{ item.treatmentPlan || item.treatment_plan || '无' }}</span>
            </div>
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>

    <!-- 病历详情对话框 -->
    <el-dialog title="门诊病历详情" :visible.sync="detailVisible" width="800px" top="5vh">
      <el-card shadow="hover" style="margin-bottom:15px;">
        <div slot="header"><span style="font-weight:bold;">就诊信息</span></div>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="患者姓名">{{ detailData.pName || detailData.p_name || '---' }}</el-descriptions-item>
          <el-descriptions-item label="医生">{{ detailData.dName || detailData.d_name || '---' }}</el-descriptions-item>
          <el-descriptions-item label="就诊时间">{{ detailData.createTime || detailData.create_time || '---' }}</el-descriptions-item>
          <el-descriptions-item label="就诊日期">{{ detailData.oStart || detailData.o_start || '---' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <el-card shadow="hover">
        <div slot="header"><span style="font-weight:bold;color:#409EFF;">病历内容</span></div>
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="主诉" contentStyle="white-space:pre-wrap;">{{ detailData.chiefComplaint || detailData.chief_complaint || '无' }}</el-descriptions-item>
          <el-descriptions-item label="现病史" contentStyle="white-space:pre-wrap;">{{ detailData.presentIllness || detailData.present_illness || '无' }}</el-descriptions-item>
          <el-descriptions-item label="既往史" contentStyle="white-space:pre-wrap;">{{ detailData.pastHistory || detailData.past_history || '无' }}</el-descriptions-item>
          <el-descriptions-item label="体格检查" contentStyle="white-space:pre-wrap;">{{ detailData.physicalExam || detailData.physical_exam || '无' }}</el-descriptions-item>
          <el-descriptions-item label="诊断" contentStyle="white-space:pre-wrap;font-weight:bold;color:#E6A23C;">{{ detailData.diagnosis || '无' }}</el-descriptions-item>
          <el-descriptions-item label="处理意见" contentStyle="white-space:pre-wrap;">{{ detailData.treatmentPlan || detailData.treatment_plan || '无' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <el-card shadow="hover" style="margin-top:15px;" v-if="prescDetails.length > 0">
        <div slot="header"><span style="font-weight:bold;color:#67C23A;">处方记录（共 {{ prescDetails.length }} 项）</span></div>
        <el-table :data="prescDetails"border  stripe  size="small" style="width:100%" >
          <el-table-column prop="drName"   label="药品"   width="140" ></el-table-column>
          <el-table-column prop="pdUsage"   label="用法"   width="60" ></el-table-column>
          <el-table-column prop="pdDosage"   label="用量"   width="60" ></el-table-column>
          <el-table-column prop="pdFrequency"   label="频次"   width="60" ></el-table-column>
          <el-table-column prop="pdDays"   label="天数"   width="55" ></el-table-column>
          <el-table-column prop="pdQuantity"   label="数量"   width="55" ></el-table-column>
          <el-table-column label="小计"   width="80" ><template slot-scope="s">¥{{ (s.row.pdQuantity * (s.row.pdPrice || s.row.drPrice || 0)).toFixed(2) }}</template></el-table-column>
        </el-table>
      </el-card>
      <div slot="footer">
        <el-button type="primary" plain @click="exportSingle(detailData)"><i class="el-icon-download"></i> 导出</el-button>
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
  name: "MyEmr",
  data() {
    return { pId: null, emrList: [], detailVisible: false, detailData: {}, prescDetails: [], loading: true };
  },
  methods: {
    async loadData() {
      if (!this.pId) { this.loading = false; return; }
      try {
        const res = await request.get("emr/findByPatient", { params: { pId: this.pId } });
        if (res.data.status === 200) {
          this.emrList = (res.data.data || []).sort(function(a, b) {
            var ta = a.createTime || a.oStart || "";
            var tb = b.createTime || b.oStart || "";
            return tb.localeCompare(ta);
          });
        }
      } catch(e) {}
      this.loading = false;
    },
    handlePageChange(p) {},
    async viewDetail(row) {
      this.detailData = row;
      this.prescDetails = [];
      this.detailVisible = true;
      try {
        const res = await request.get("prescription/findByOrder", { params: { oId: row.oId || row.o_id } });
        if (res.data.status === 200) this.prescDetails = res.data.data || [];
      } catch(e) {}
    },
    async exportSingle(row) {
      if (row.emrId) {
        window.open("http://localhost:9999/emr/pdf?emrId=" + row.emrId, "_blank");
      } else if (row.oId) {
        window.open("http://localhost:9999/patient/pdf?oId=" + row.oId, "_blank");
      } else {
        this.$message.info("导出功能需要病历编号");
      }
    },
    printCurrent() {
      window.print();
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
.emr-card { border-left: 3px solid #E4E7ED; transition: all 0.2s; }
.emr-card:hover { border-left-color: #409EFF; box-shadow: 0 2px 12px rgba(64,158,255,0.1); }
.emr-card-latest { border-left-color: #409EFF; background: linear-gradient(135deg, rgba(64,158,255,0.02), transparent); }
.emr-header { display: flex; justify-content: space-between; align-items: center; }
.emr-header-left { display: flex; align-items: center; gap: 12px; }
.emr-doctor { font-weight: 600; color: #303133; font-size: 14px; }
.emr-date { color: #909399; font-size: 12px; }
.summary-item { display: flex; margin: 6px 0; gap: 12px; }
.summary-label { color: #909399; font-size: 12px; min-width: 60px; flex-shrink: 0; }
.summary-text { color: #303133; font-size: 13px; }
.summary-text.diagnosis { font-weight: 600; color: #E6A23C; }
</style>








