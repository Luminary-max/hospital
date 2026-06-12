<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-reading"></i> 我的病历</span>
    </div>
    <el-table :data="emrList" border stripe style="width:100%">
      <el-table-column prop="o_id" label="就诊编号" width="120"></el-table-column>
      <el-table-column prop="d_name" label="医生" width="120"></el-table-column>
      <el-table-column prop="create_time" label="就诊日期" width="180"></el-table-column>
      <el-table-column prop="chief_complaint" label="主诉" width="200" show-overflow-tooltip></el-table-column>
      <el-table-column prop="diagnosis" label="诊断" width="200" show-overflow-tooltip></el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="viewDetail(scope.row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div v-if="emrList.length === 0" style="text-align:center; padding:40px 0; color:#999;">
      暂无病历记录
    </div>
    <!-- 病历详情对话框 -->
    <el-dialog title="门诊病历" :visible.sync="detailVisible" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="患者姓名" :span="1">{{ detailData.p_name || '---' }}</el-descriptions-item>
        <el-descriptions-item label="就诊日期" :span="1">{{ detailData.create_time || '---' }}</el-descriptions-item>
        <el-descriptions-item label="主诉" :span="2">{{ detailData.chief_complaint || '无' }}</el-descriptions-item>
        <el-descriptions-item label="现病史" :span="2">{{ detailData.present_illness || '无' }}</el-descriptions-item>
        <el-descriptions-item label="既往史" :span="2">{{ detailData.past_history || '无' }}</el-descriptions-item>
        <el-descriptions-item label="体格检查" :span="2">{{ detailData.physical_exam || '无' }}</el-descriptions-item>
        <el-descriptions-item label="诊断" :span="2">{{ detailData.diagnosis || '无' }}</el-descriptions-item>
        <el-descriptions-item label="处理意见" :span="2">{{ detailData.treatment_plan || '无' }}</el-descriptions-item>
      </el-descriptions>
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
    return { pId: null, emrList: [], detailVisible: false, detailData: {} };
  },
  methods: {
    async loadData() {
      try {
        const res = await request.get("emr/findByPatient", { params: { pId: this.pId } });
        if (res.data.status === 200) {
          this.emrList = res.data.data || [];
        }
      } catch(e) { console.error(e); }
    },
    viewDetail(row) {
      this.detailData = row;
      this.detailVisible = true;
    }
  },
  created() {
    const token = getToken();
    if (token) {
      const decoded = jwtDecode(token);
      this.pId = decoded.pId || decoded.sub;
    }
    this.loadData();
  }
};
</script>
