<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-document"></i> 门诊接诊 — 结构化病历 & 处方开立</span>
      <el-button type="primary" size="small" style="float:right;" @click="submitClick" :loading="submitting">
        <i class="el-icon-upload2"></i> 提交病历
      </el-button>
    </div>

    <!-- 患者信息 -->
    <el-card shadow="hover" style="margin-bottom:20px;">
      <el-descriptions :column="4" border size="small">
        <el-descriptions-item label="患者姓名" :span="1">{{ pName }}</el-descriptions-item>
        <el-descriptions-item label="性别" :span="1">{{ pGender }}</el-descriptions-item>
        <el-descriptions-item label="年龄" :span="1">{{ pAge || '---' }}</el-descriptions-item>
        <el-descriptions-item label="电话" :span="1">{{ pPhone }}</el-descriptions-item>
        <el-descriptions-item label="就诊日期" :span="2">{{ nowDate }}</el-descriptions-item>
        <el-descriptions-item label="医生" :span="1">{{ dName }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 门诊病历 -->
    <el-card shadow="hover" style="margin-bottom:20px;">
      <div slot="header"><span style="font-weight:bold; color:#409EFF;">一、门诊病历</span></div>
      <el-form label-position="top" size="small">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主诉（Chief Complaint）">
              <el-input type="textarea" :rows="3" v-model="emr.chiefComplaint" placeholder="患者主要症状、持续时间"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="现病史（Present Illness）">
              <el-input type="textarea" :rows="3" v-model="emr.presentIllness" placeholder="发病情况、诊治经过"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="既往史（Past History）">
              <el-input type="textarea" :rows="3" v-model="emr.pastHistory" placeholder="既往疾病、手术史、过敏史"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体格检查（Physical Examination）">
              <el-input type="textarea" :rows="3" v-model="emr.physicalExam" placeholder="生命体征、阳性体征"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="诊断（Diagnosis）">
              <el-input type="textarea" :rows="3" v-model="emr.diagnosis" placeholder="初步诊断结果"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理意见（Treatment Plan）">
              <el-input type="textarea" :rows="3" v-model="emr.treatmentPlan" placeholder="进一步检查、复诊建议、注意事项"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 处方开立 -->
    <el-card shadow="hover" style="margin-bottom:20px;">
      <div slot="header">
        <span style="font-weight:bold; color:#E6A23C;">二、处方开立</span>
        <span style="font-size:12px; color:#999; margin-left:10px;">用法可选：口服/注射/外用 &nbsp; 频次：qd(每日1次)/bid(每日2次)/tid(每日3次)/qid(每日4次)</span>
      </div>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form inline size="mini">
            <el-form-item>
              <el-input v-model="queryDrug" placeholder="搜索药品" prefix-icon="el-icon-search" @keyup.enter.native="requestDrug"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="requestDrug">查询</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="drugData" border stripe size="mini" height="250">
            <el-table-column prop="drId" label="编号" width="80"></el-table-column>
            <el-table-column prop="drName" label="药品名称" width="140"></el-table-column>
            <el-table-column prop="drPrice" label="单价" width="80">
              <template slot-scope="s">¥{{ s.row.drPrice }}</template>
            </el-table-column>
            <el-table-column prop="drNumber" label="库存" width="60"></el-table-column>
            <el-table-column prop="drUnit" label="单位" width="60"></el-table-column>
            <el-table-column label="操作" width="80">
              <template slot-scope="s">
                <el-button type="warning" size="mini" @click="showPrescriptionDialog(s.row)">开药</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination @current-change="drugPageChange" :page-size="size" layout="total,prev,pager,next" :total="drugTotal" small></el-pagination>
        </el-col>
        <el-col :span="12">
          <el-tag type="warning" style="margin-bottom:10px;">已选药品（合计：¥{{ drugTotalPrice }}）</el-tag>
          <el-table :data="drugBuyData" border stripe size="mini" height="300">
            <el-table-column prop="drName" label="药品名" width="100"></el-table-column>
            <el-table-column prop="pdUsage" label="用法" width="60"></el-table-column>
            <el-table-column prop="pdDosage" label="用量" width="60"></el-table-column>
            <el-table-column prop="pdFrequency" label="频次" width="60"></el-table-column>
            <el-table-column prop="pdDays" label="天数" width="50"></el-table-column>
            <el-table-column prop="pdQuantity" label="数量" width="50"></el-table-column>
            <el-table-column label="小计" width="80">
              <template slot-scope="s">¥{{ (s.row.pdQuantity * s.row.drPrice).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="50">
              <template slot-scope="s">
                <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteDrug(s.$index)"></el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-card>

    <!-- 检查项目 -->
    <el-card shadow="hover" style="margin-bottom:20px;">
      <div slot="header"><span style="font-weight:bold; color:#67C23A;">三、检查项目</span></div>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form inline size="mini">
            <el-form-item>
              <el-input v-model="queryCheck" placeholder="搜索检查项目" prefix-icon="el-icon-search" @keyup.enter.native="requestCheck"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="requestCheck">查询</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="checkData" border stripe size="mini" height="200">
            <el-table-column prop="chId" label="编号" width="80"></el-table-column>
            <el-table-column prop="chName" label="项目名称" width="200"></el-table-column>
            <el-table-column prop="chPrice" label="价格" width="80">
              <template slot-scope="s">¥{{ s.row.chPrice }}</template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template slot-scope="s">
                <el-button type="success" size="mini" @click="addCheck(s.row)">增加</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination @current-change="checkPageChange" :page-size="checkSize" layout="total,prev,pager,next" :total="checkTotal" small></el-pagination>
        </el-col>
        <el-col :span="12">
          <el-tag type="success" style="margin-bottom:10px;">已选检查（合计：¥{{ checkTotalPrice }}）</el-tag>
          <el-table :data="checkBuyData" border stripe size="mini" height="200">
            <el-table-column prop="chId" label="编号" width="80"></el-table-column>
            <el-table-column prop="chName" label="项目名" width="180"></el-table-column>
            <el-table-column label="价格" width="80">
              <template slot-scope="s">¥{{ s.row.chPrice }}</template>
            </el-table-column>
            <el-table-column label="操作" width="50">
              <template slot-scope="s">
                <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteCheck(s.$index)"></el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-card>

    <!-- 处方明细对话框 -->
    <el-dialog title="开立处方" :visible.sync="prescriptionDialogVisible" width="500px">
      <el-form :model="prescriptionForm" label-width="80px" size="small">
        <el-form-item label="药品名称">
          <el-input v-model="prescriptionForm.drName" disabled></el-input>
        </el-form-item>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="用法">
              <el-select v-model="prescriptionForm.pdUsage" placeholder="选择用法" style="width:100%">
                <el-option label="口服" value="口服"></el-option>
                <el-option label="注射" value="注射"></el-option>
                <el-option label="外用" value="外用"></el-option>
                <el-option label="含服" value="含服"></el-option>
                <el-option label="雾化" value="雾化"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每次用量">
              <el-input v-model="prescriptionForm.pdDosage" placeholder="如：1片、10ml"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="频次">
              <el-select v-model="prescriptionForm.pdFrequency" placeholder="选择频次" style="width:100%">
                <el-option label="qd(每日1次)" value="qd"></el-option>
                <el-option label="bid(每日2次)" value="bid"></el-option>
                <el-option label="tid(每日3次)" value="tid"></el-option>
                <el-option label="qid(每日4次)" value="qid"></el-option>
                <el-option label="qn(每晚1次)" value="qn"></el-option>
                <el-option label="prn(必要时)" value="prn"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用药天数">
              <el-input-number v-model="prescriptionForm.pdDays" :min="1" :max="30" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="prescriptionForm.pdNote" placeholder="如：饭后服用、用药期间忌酒等"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="prescriptionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPrescription">确认开立</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "DealOrder",
  data() {
    return {
      oId: null, pId: null, dId: "", dName: "", pName: "", pGender: "", pPhone: "", pAge: "",
      nowDate: "",
      submitting: false,
      // 门诊病历
      emr: { chiefComplaint: "", presentIllness: "", pastHistory: "", physicalExam: "", diagnosis: "", treatmentPlan: "" },
      // 药品目录
      drugData: [], drugTotal: 0, pageNumber: 1, size: 10, queryDrug: "",
      drugBuyData: [], drugTotalPrice: 0,
      // 检查项目
      checkData: [], checkTotal: 0, checkPageNumber: 1, checkSize: 10, queryCheck: "",
      checkBuyData: [], checkTotalPrice: 0,
      // 处方对话框
      prescriptionDialogVisible: false,
      prescriptionForm: { drId: "", drName: "", drPrice: 0, pdUsage: "", pdDosage: "", pdFrequency: "", pdDays: 3, pdNote: "" }
    };
  },
  methods: {
    nowDay() { const d=new Date(); return d.getFullYear()+"-"+(d.getMonth()+1).toString().padStart(2,'0')+"-"+d.getDate().toString().padStart(2,'0'); },
    async requestPatient() {
      try {
        const res = await request.get("doctor/findPatientById", { params: { pId: this.pId } });
        if (res.data.status === 200) {
          const p = res.data.data;
          this.pName = p.pName; this.pGender = p.pGender; this.pPhone = p.pPhone; this.pAge = p.pAge;
        }
      } catch(e) { console.error(e); }
    },
    async requestDrug() {
      try {
        const res = await request.get("drug/findAllDrugs", { params: { pageNumber: this.pageNumber, size: this.size, query: this.queryDrug } });
        if (res.data.status === 200) {
          const d = res.data.data;
          this.drugData = d.records || d.drugs || []; this.drugTotal = d.total || 0;
        }
      } catch(e) { console.error(e); }
    },
    async requestCheck() {
      try {
        const res = await request.get("check/findAllChecks", { params: { pageNumber: this.checkPageNumber, size: this.checkSize, query: this.queryCheck } });
        if (res.data.status === 200) {
          const d = res.data.data;
          this.checkData = d.records || d.checks || []; this.checkTotal = d.total || 0;
        }
      } catch(e) { console.error(e); }
    },
    drugPageChange(p) { this.pageNumber = p; this.requestDrug(); },
    checkPageChange(p) { this.checkPageNumber = p; this.requestCheck(); },
    // 处方
    showPrescriptionDialog(row) {
      this.prescriptionForm = { drId: row.drId, drName: row.drName, drPrice: row.drPrice, pdUsage: "", pdDosage: "", pdFrequency: "", pdDays: 3, pdNote: "" };
      this.prescriptionDialogVisible = true;
    },
    confirmPrescription() {
      if (!this.prescriptionForm.pdUsage || !this.prescriptionForm.pdDosage || !this.prescriptionForm.pdFrequency) {
        return this.$message.warning("请填写完整的用药信息（用法、用量、频次）");
      }
      const freqNum = { qd: 1, bid: 2, tid: 3, qid: 4, qn: 1, prn: 1 };
      const timesPerDay = freqNum[this.prescriptionForm.pdFrequency] || 1;
      const qty = timesPerDay * this.prescriptionForm.pdDays;
      const idx = this.drugBuyData.findIndex(d => d.drId === this.prescriptionForm.drId);
      if (idx >= 0) {
        this.drugBuyData[idx].pdQuantity += qty;
        this.drugBuyData[idx].pdDays += this.prescriptionForm.pdDays;
      } else {
        this.prescriptionForm.pdQuantity = qty;
        this.drugBuyData.push({ ...this.prescriptionForm });
      }
      this.recalcDrugTotal();
      this.prescriptionDialogVisible = false;
      this.$message.success("已加入处方");
    },
    deleteDrug(idx) {
      this.drugBuyData.splice(idx, 1);
      this.recalcDrugTotal();
    },
    recalcDrugTotal() {
      this.drugTotalPrice = this.drugBuyData.reduce((s, d) => s + d.pdQuantity * d.drPrice, 0);
    },
    // 检查项目
    addCheck(row) {
      if (this.checkBuyData.some(c => c.chId === row.chId)) return this.$message.info("已添加该项目");
      this.checkBuyData.push({ ...row });
      this.checkTotalPrice = this.checkBuyData.reduce((s, c) => s + parseFloat(c.chPrice || 0), 0);
    },
    deleteCheck(idx) {
      this.checkBuyData.splice(idx, 1);
      this.checkTotalPrice = this.checkBuyData.reduce((s, c) => s + parseFloat(c.chPrice || 0), 0);
    },
    // 提交
    async submitClick() {
      this.submitting = true;
      try {
        // 1. 保存门诊病历
        if (this.emr.chiefComplaint || this.emr.diagnosis) {
          await request.post("emr/save", {
            oId: this.oId, pId: this.pId, dId: this.dId,
            chiefComplaint: this.emr.chiefComplaint, presentIllness: this.emr.presentIllness,
            pastHistory: this.emr.pastHistory, physicalExam: this.emr.physicalExam,
            diagnosis: this.emr.diagnosis, treatmentPlan: this.emr.treatmentPlan
          });
        }
        // 2. 保存处方明细
        if (this.drugBuyData.length > 0) {
          await request.post("prescription/save", { oId: this.oId, details: this.drugBuyData });
        }
        // 3. 更新订单到orders表（兼容已有功能）
        const drugStr = this.drugBuyData.map(d => `${d.drName}*${d.drPrice}*${d.pdQuantity} ${d.pdUsage} ${d.pdDosage} ${d.pdFrequency}`).join("；");
        const checkStr = this.checkBuyData.map(c => `${c.chName}*${c.chPrice}`).join("；");
        const totalPrice = this.drugTotalPrice + this.checkTotalPrice;
        await request.post("order/updateOrder", {
          oId: this.oId, pId: this.pId, dId: this.dId,
          oRecord: this.emr.chiefComplaint + "；" + this.emr.diagnosis,
          oDrug: drugStr ? `药品：${drugStr} 总价${this.drugTotalPrice.toFixed(2)}元` : "",
          oCheck: checkStr ? `检查：${checkStr} 总价${this.checkTotalPrice.toFixed(2)}元` : "",
          oTotalPrice: totalPrice,
          oAdvice: this.emr.treatmentPlan
        });
        this.$message.success("病历提交成功！请通知患者登录系统自助缴费！");
        this.$router.push("/orderToday");
      } catch(e) {
        console.error(e);
        this.$message.error("提交失败，请重试");
      }
      this.submitting = false;
    }
  },
  created() {
    this.nowDate = this.nowDay();
    const token = getToken();
    if (token) {
      const decoded = jwtDecode(token);
      this.dName = decoded.dName; this.dId = decoded.dId;
    }
    this.oId = parseInt(this.$route.query.oId);
    this.pId = parseInt(this.$route.query.pId);
    this.requestPatient();
    this.requestDrug();
    this.requestCheck();
  }
};
</script>
