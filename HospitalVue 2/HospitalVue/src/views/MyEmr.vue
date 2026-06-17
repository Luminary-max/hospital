<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-reading"></i> 我的病历</span>
    </div>
    <el-table :data="emrList" border stripe style="width:100%">
      <el-table-column prop="oId" label="就诊编号" width="100"></el-table-column>
      <el-table-column prop="dName" label="医生" width="90"></el-table-column>
      <el-table-column prop="createTime" label="就诊日期" width="150"></el-table-column>
      <el-table-column prop="chiefComplaint" label="主诉" show-overflow-tooltip></el-table-column>
      <el-table-column prop="diagnosis" label="诊断" show-overflow-tooltip></el-table-column>
      <el-table-column label="操作" width="1%">
        <template slot-scope="s">
          <el-button type="primary" size="mini" @click="viewDetail(s.row)" style="white-space:nowrap;">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handlePageChange" layout="total,prev,pager,next" :total="emrList.length" :page-size="10" v-if="emrList.length > 10" style="margin-top:15px;"></el-pagination>
    <div v-if="emrList.length === 0" style="text-align:center;padding:60px 0;color:#999;">暂无病历记录</div>

    <!-- 病历详情对话框 -->
    <el-dialog title="门诊病历详情" :visible.sync="detailVisible" width="800px">
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
          <el-descriptions-item label="诊断" contentStyle="white-space:pre-wrap;font-weight:bold;color:#E6A23C;">{{ detailData.diagnosis || detailData.diagnosis || '无' }}</el-descriptions-item>
          <el-descriptions-item label="处理意见" contentStyle="white-space:pre-wrap;">{{ detailData.treatmentPlan || detailData.treatment_plan || '无' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <el-card shadow="hover" style="margin-top:15px;" v-if="prescDetails.length > 0">
        <div slot="header"><span style="font-weight:bold;color:#67C23A;">处方记录</span></div>
        <el-table :data="prescDetails" border stripe size="small">
          <el-table-column prop="drName" label="药品" width="140"></el-table-column>
          <el-table-column prop="pdUsage" label="用法" width="60"></el-table-column>
          <el-table-column prop="pdDosage" label="用量" width="60"></el-table-column>
          <el-table-column prop="pdFrequency" label="频次" width="60"></el-table-column>
          <el-table-column prop="pdDays" label="天数" width="55"></el-table-column>
          <el-table-column prop="pdQuantity" label="数量" width="55"></el-table-column>
          <el-table-column label="小计" width="80"><template slot-scope="s">¥{{ (s.row.pdQuantity * s.row.pdPrice).toFixed(2) }}</template></el-table-column>
        </el-table>
      </el-card>
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
    return { pId: null, emrList: [], detailVisible: false, detailData: {}, prescDetails: [] };
  },
  methods: {
    async loadData() {
      if (!this.pId) return;
      try {
        const res = await request.get("emr/findByPatient", { params: { pId: this.pId } });
        if (res.data.status === 200) this.emrList = res.data.data || [];
      } catch(e) { console.error(e); }
    },
    handlePageChange(p) {},
    async viewDetail(row) {
      this.detailData = row;
      this.prescDetails = [];
      try {
        const res = await request.get("prescription/findByOrder", { params: { oId: row.oId || row.o_id } });
        if (res.data.status === 200) this.prescDetails = res.data.data || [];
      } catch(e) { console.error(e); }
      this.detailVisible = true;
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
.el-table { width: 100% !important; }
</style>
