<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" style="background:#409EFF;"><i class="el-icon-date"></i></div>
            <div class="stat-info"><div class="stat-num">{{ orderPeople }}</div><div class="stat-label">今日预约挂号</div></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" style="background:#67C23A;"><i class="el-icon-s-order"></i></div>
            <div class="stat-info"><div class="stat-num">{{ todayFinished }}</div><div class="stat-label">今日已接诊</div></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" style="background:#E6A23C;"><i class="el-icon-s-data"></i></div>
            <div class="stat-info"><div class="stat-num">{{ waitingCount }}</div><div class="stat-label">排队候诊</div></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" style="background:#F56C6C;"><i class="el-icon-user"></i></div>
            <div class="stat-info"><div class="stat-num">{{ pendingPatients }}</div><div class="stat-label">待处理患者</div></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top:20px;"><div style="text-align:center;padding:30px;color:#999;">欢迎使用医生工作站</div></el-card>
  </div>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "DoctorLayout",
  data() { return { userId: "", orderPeople: 0, todayFinished: 0, waitingCount: 0, pendingPatients: 0 }; },
  methods: {
    tokenDecode(t) { if(t) return jwtDecode(t); },
    requestStats() {
      request.get("order/orderPeopleByDid",{params:{dId:this.userId}}).then(r=>{if(r.data.status===200)this.orderPeople=r.data.data||0;});
      request.get("order/findOrderFinish",{params:{pageNumber:1,size:1,query:"",dId:this.userId}}).then(r=>{if(r.data.status===200)this.todayFinished=r.data.data.total||0;});
      request.get("queue/listByDoctor",{params:{dId:this.userId}}).then(r=>{if(r.data.status===200)this.waitingCount=Array.isArray(r.data.data)?r.data.data.filter(function(q){return q.qStatus===0||q.qStatus===1;}).length:0;});
      request.get("order/findOrderByDid",{params:{pageNumber:1,size:1,query:"",dId:this.userId}}).then(r=>{if(r.data.status===200)this.pendingPatients=r.data.data.total||0;});
    }
  },
  created() { const d=this.tokenDecode(getToken()); if(d) this.userId=d.dId; this.requestStats(); }
};
</script>
<style scoped>
.dashboard{padding:10px 0;}
.stat-card{border-radius:8px;}
.stat-inner{display:flex;align-items:center;gap:20px;padding:10px 0;}
.stat-icon{width:60px;height:60px;border-radius:12px;display:flex;align-items:center;justify-content:center;flex-shrink:0;}
.stat-icon i{font-size:30px;color:#fff;}
.stat-num{font-size:28px;font-weight:700;color:#303133;}
.stat-label{font-size:14px;color:#909399;margin-top:4px;}
</style>
