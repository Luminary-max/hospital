<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-download"></i> 报告下载</span>
      <el-button type="primary" size="small" style="float:right;" @click="batchDownload" :disabled="reportList.length === 0" :loading="batchLoading">
        <i class="el-icon-download"></i> 批量下载
      </el-button>
    </div>

    <div v-if="loading" style="text-align:center;padding:60px 0;">
      <i class="el-icon-loading" style="font-size:32px;color:#409EFF;"></i>
      <p style="color:#999;margin-top:10px;">加载中...</p>
    </div>

    <div v-else-if="reportList.length === 0" style="text-align:center;padding:80px 0;color:#999;">
      <i class="el-icon-document" style="font-size:48px;"></i>
      <p style="margin-top:15px;font-size:15px;">暂无报告记录</p>
      <p style="color:#c0c4cc;font-size:13px;">就诊后医生生成的病历和处方可在此处下载PDF</p>
    </div>

    <el-timeline v-else>
      <el-timeline-item v-for="(item, index) in reportList" :key="item.emrId || index"
        :timestamp="item.createTime || item.oStart || '---'"
        placement="top"
        :color="item.emrId ? '#409EFF' : '#E4E7ED'">
        <el-card shadow="hover" class="report-card">
          <div class="report-header">
            <div class="report-header-left">
              <el-tag v-if="item.emrId" type="primary" size="mini" effect="dark">PDF</el-tag>
              <el-tag v-else type="info" size="mini">处方</el-tag>
              <span class="report-doctor"><i class="el-icon-user"></i> {{ item.dName || item.d_name || '---' }}</span>
              <span class="report-date"><i class="el-icon-time"></i> {{ item.createTime || item.create_time || item.oStart || '---' }}</span>
            </div>
            <div class="report-header-right">
              <el-button type="primary" size="mini" plain :disabled="!item.emrId" @click="downloadPdf(item)">
                <i class="el-icon-download"></i> 下载PDF
              </el-button>
            </div>
          </div>
          <el-divider style="margin:10px 0;"></el-divider>
          <div class="report-summary">
            <div class="summary-row">
              <span class="summary-label">诊断</span>
              <span class="summary-value diagnosis">{{ item.diagnosis || item.oRecord || '无' }}</span>
            </div>
            <div class="summary-row" v-if="item.chiefComplaint || item.chief_complaint">
              <span class="summary-label">主诉</span>
              <span class="summary-value">{{ item.chiefComplaint || item.chief_complaint || '无' }}</span>
            </div>
            <div class="summary-row" v-if="prescriptionMap[item.emrId || item.oId]">
              <span class="summary-label">处方</span>
              <span class="summary-value drug-text">{{ prescriptionMap[item.emrId || item.oId] }}</span>
            </div>
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>

    <el-pagination @current-change="p=>{pageNumber=p;loadData()}"
      :page-size="size" layout="total,prev,pager,next,jumper" :total="total"
      style="margin-top:15px;" v-if="total>0">
    </el-pagination>
  </el-card>
</template>

<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "PatientReports",
  data() {
    return {
      pId: null,
      loading: true,
      reportList: [],
      prescriptionMap: {},
      total: 0,
      pageNumber: 1,
      size: 10,
      batchLoading: false
    };
  },
  methods: {
    async loadData() {
      if (!this.pId) { this.loading = false; return; }
      try {
        const [emrRes, orderRes] = await Promise.all([
          request.get("emr/findByPatient", { params: { pId: this.pId } }),
          request.get("patient/findOrderByPid", { params: { pId: this.pId } })
        ]);
        var emrList = [];
        if (emrRes.data.status === 200) {
          emrList = (emrRes.data.data || []).sort(function(a, b) {
            var ta = a.createTime || a.oStart || "";
            var tb = b.createTime || b.oStart || "";
            return tb.localeCompare(ta);
          });
        }
        var orderList = [];
        if (orderRes.data.status === 200) {
          orderList = orderRes.data.data || [];
        }
        // Build prescription summary map
        var pMap = {};
        var orderPromises = [];
        orderList.forEach(function(o) {
          if (o.oDrug) {
            pMap[o.oId] = o.oDrug;
          }
          if (o.oId) {
            orderPromises.push(
              request.get("prescription/findByOrder", { params: { oId: o.oId } }).then(function(pRes) {
                if (pRes.data.status === 200) {
                  var drugs = pRes.data.data || [];
                  if (drugs.length > 0) {
                    var names = drugs.map(function(d) { return d.drName; }).filter(Boolean).join("、");
                    if (names) {
                      pMap[o.oId] = pMap[o.oId] ? pMap[o.oId] + "；" + names : names;
                    }
                  }
                }
              }).catch(function() {})
            );
          }
        });
        await Promise.all(orderPromises);
        this.prescriptionMap = pMap;
        // Merge EMR list with order info
        this.reportList = emrList.map(function(e) {
          var order = orderList.find(function(o) { return o.oId === (e.oId || e.o_id); });
          return {
            emrId: e.emrId || e.emr_id,
            oId: e.oId || e.o_id,
            dName: e.dName || e.d_name,
            createTime: e.createTime || e.create_time,
            oStart: e.oStart || e.o_start,
            chiefComplaint: e.chiefComplaint || e.chief_complaint,
            diagnosis: e.diagnosis,
            treatmentPlan: e.treatmentPlan || e.treatment_plan,
            // fallback from order
            oRecord: order ? order.oRecord : null
          };
        });
        // If no EMR records, show orders as report items
        if (this.reportList.length === 0) {
          this.reportList = orderList.map(function(o) {
            return {
              oId: o.oId,
              dName: o.dName,
              createTime: o.oStart,
              oStart: o.oStart,
              diagnosis: o.oRecord,
              oRecord: o.oRecord,
              emrId: null
            };
          });
        }
        this.total = this.reportList.length;
      } catch(e) {}
      this.loading = false;
    },
    downloadPdf(item) {
      if (!item.emrId) {
        this.$message.info("该记录暂无PDF可下载");
        return;
      }
      window.open("/emr/pdf?emrId=" + item.emrId, "_blank");
    },
    batchDownload() {
      var emrItems = this.reportList.filter(function(item) { return item.emrId; });
      if (emrItems.length === 0) {
        this.$message.info("暂无可用PDF");
        return;
      }
      this.batchLoading = true;
      var idx = 0;
      var openNext = function() {
        if (idx >= emrItems.length) {
          this.batchLoading = false;
          this.$message.success("已打开 " + emrItems.length + " 个PDF文件");
          return;
        }
        window.open("/emr/pdf?emrId=" + emrItems[idx].emrId, "_blank");
        idx++;
        setTimeout(openNext.bind(this), 500);
      }.bind(this);
      this.$confirm("将依次打开 " + emrItems.length + " 个PDF文件，确认继续？", "批量下载", { type: "info" }).then(function() {
        openNext();
      }).catch(function() { this.batchLoading = false; }.bind(this));
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
.report-card { transition: all 0.2s; }
.report-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.report-header { display: flex; justify-content: space-between; align-items: center; }
.report-header-left { display: flex; align-items: center; gap: 12px; }
.report-doctor { font-weight: 600; color: #303133; font-size: 14px; }
.report-date { color: #909399; font-size: 12px; }
.summary-row { display: flex; margin: 6px 0; gap: 12px; }
.summary-label { color: #909399; font-size: 12px; min-width: 50px; flex-shrink: 0; }
.summary-value { color: #303133; font-size: 13px; }
.summary-value.diagnosis { font-weight: 600; color: #E6A23C; }
.summary-value.drug-text { color: #409EFF; }
</style>
