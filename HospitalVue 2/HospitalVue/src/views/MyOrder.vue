<template>
  <div>
    <el-card>
      <el-table :data="orderData" stripe border style="width:100%">
        <el-table-column prop="oId" label="单号" width="80" align="center"></el-table-column>
        <el-table-column prop="pName" label="姓名" width="70" align="center"></el-table-column>
        <el-table-column prop="dName" label="医生" width="80" align="center"></el-table-column>
        <el-table-column prop="oStart" label="挂号时间" min-width="160"></el-table-column>
        <el-table-column prop="oEnd" label="结束时间" min-width="140"></el-table-column>
        <el-table-column prop="oTotalPrice" label="费用" width="80" align="center"></el-table-column>
        <el-table-column label="缴费" width="80" align="center">
          <template slot-scope="s">
            <el-tag v-if="s.row.oPriceState===1" type="success">已缴</el-tag>
            <el-button v-else-if="s.row.oState===1" type="warning" size="mini" @click="priceClick(s.row.oId,s.row.dId)">缴费</el-button>
            <span v-else style="color:#ccc;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="70" align="center">
          <template slot-scope="s">
            <el-tag v-if="s.row.oState===1&&s.row.oPriceState===1" type="success">完成</el-tag>
            <el-tag v-else-if="s.row.oPriceState===0&&s.row.oState===0" type="danger">未完成</el-tag>
            <el-tag v-else type="warning">待缴费</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报告单" width="90" align="center">
          <template slot-scope="s">
            <el-button v-if="s.row.oState===1&&s.row.oPriceState===1" type="success" size="mini" @click="seeReport(s.row)">查看</el-button>
            <span v-else style="color:#ccc;">-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 报告单详情对话框 -->
    <el-dialog title="就诊报告单" :visible.sync="reportVisible" width="750px" top="5vh">
      <div v-if="reportLoading" style="text-align:center;padding:40px;color:#999;">加载中...</div>
      <div v-else>
        <el-card shadow="hover" style="margin-bottom:12px;">
          <div slot="header"><b>基本信息</b></div>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="就诊编号">{{ reportData.oId }}</el-descriptions-item>
            <el-descriptions-item label="患者姓名">{{ reportData.pName }}</el-descriptions-item>
            <el-descriptions-item label="医生">{{ reportData.dName }}</el-descriptions-item>
            <el-descriptions-item label="就诊时间">{{ reportData.oStart }}</el-descriptions-item>
            <el-descriptions-item label="结束时间">{{ reportData.oEnd || '---' }}</el-descriptions-item>
            <el-descriptions-item label="总费用">¥{{ reportData.oTotalPrice || '0' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="hover" style="margin-bottom:12px;" v-if="emrData">
          <div slot="header"><b style="color:#409EFF;">门诊病历</b></div>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="主诉">{{ emrData.chiefComplaint || '无' }}</el-descriptions-item>
            <el-descriptions-item label="现病史">{{ emrData.presentIllness || '无' }}</el-descriptions-item>
            <el-descriptions-item label="既往史">{{ emrData.pastHistory || '无' }}</el-descriptions-item>
            <el-descriptions-item label="体格检查">{{ emrData.physicalExam || '无' }}</el-descriptions-item>
            <el-descriptions-item label="诊断"><span style="color:#E6A23C;font-weight:bold;">{{ emrData.diagnosis || '无' }}</span></el-descriptions-item>
            <el-descriptions-item label="处理意见">{{ emrData.treatmentPlan || '无' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="hover" style="margin-bottom:12px;" v-if="prescDetails.length > 0">
          <div slot="header"><b style="color:#E6A23C;">处方明细</b></div>
          <el-table :data="prescDetails" border stripe size="small">
            <el-table-column prop="drName" label="药品" min-width="120"></el-table-column>
            <el-table-column prop="pdDosage" label="用量" width="60"></el-table-column>
            <el-table-column prop="pdUsage" label="用法" width="60"></el-table-column>
            <el-table-column prop="pdFrequency" label="频次" width="60"></el-table-column>
            <el-table-column prop="pdDays" label="天数" width="50"></el-table-column>
            <el-table-column prop="pdQuantity" label="数量" width="50"></el-table-column>
            <el-table-column label="金额" width="70"><template slot-scope="s">¥{{ (s.row.pdQuantity*s.row.pdPrice).toFixed(2) }}</template></el-table-column>
          </el-table>
        </el-card>

        <el-card shadow="hover" style="margin-bottom:12px;" v-if="reportData.oCheck">
          <div slot="header"><b style="color:#67C23A;">检查项目</b></div>
          <div>{{ reportData.oCheck }}</div>
        </el-card>

        <el-card shadow="hover" v-if="reportData.oAdvice">
          <div slot="header"><b>医生建议</b></div>
          <div>{{ reportData.oAdvice }}</div>
        </el-card>
      </div>
    </el-dialog>

    <el-dialog title="用户评价" :visible.sync="starVisible" width="380px">
      <div style="text-align:center;padding:10px;">
        <h4>请对医生 {{ dName }} 进行评价</h4>
        <el-rate v-model="star" style="margin-top:15px;"></el-rate>
      </div>
      <div slot="footer"><el-button @click="starVisible=false">取消</el-button><el-button type="primary" @click="starClick">确定</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "MyOrder",
  data() {
    return {
      userId: 1, orderData: [], star: 5, starVisible: false, dId: 1, dName: "",
      reportVisible: false, reportLoading: false, reportData: {}, emrData: null, prescDetails: []
    };
  },
  methods: {
    starClick() {
      request.get("doctor/updateStar",{params:{dId:this.dId,dStar:this.star}}).then(r=>{if(r.data.status===200){this.$message.success("评价成功");this.starVisible=false;}});
    },
    async seeReport(row) {
      this.reportData = row;
      this.emrData = null;
      this.prescDetails = [];
      this.reportVisible = true;
      this.reportLoading = true;
      // 加载门诊病历
      try {
        const emrRes = await request.get("emr/findByOrder", { params: { oId: row.oId } });
        if (emrRes.data.status === 200) this.emrData = emrRes.data.data;
      } catch(e) {}
      // 加载处方明细
      try {
        const prescRes = await request.get("prescription/findByOrder", { params: { oId: row.oId } });
        if (prescRes.data.status === 200) this.prescDetails = prescRes.data.data || [];
      } catch(e) {}
      this.reportLoading = false;
    },
    priceClick(oId, dId) {
      request.get("order/updatePrice",{params:{oId:oId}}).then(r=>{
        if(r.data.status!==200) return this.$message.error("缴费失败");
        this.$message.success("缴费成功");
        request.get("admin/findDoctor",{params:{dId:dId}}).then(r2=>{if(r2.data.status===200){this.dId=r2.data.data.dId;this.dName=r2.data.data.dName;}});
        this.starVisible = true;
        this.requestOrder();
      });
    },
    requestOrder() {
      request.get("patient/findOrderByPid",{params:{pId:this.userId}}).then(r=>{if(r.data.status===200)this.orderData=r.data.data||[];});
    },
    tokenDecode(t) { if(t) return jwtDecode(t); }
  },
  created() {
    this.userId=this.tokenDecode(getToken()).pId;
    this.requestOrder();
  }
};
</script>
