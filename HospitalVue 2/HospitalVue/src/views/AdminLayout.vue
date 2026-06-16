<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" style="background:#409EFF;"><i class="el-icon-user"></i></div>
            <div class="stat-info"><div class="stat-num">{{ orderPeople }}</div><div class="stat-label">今日预约挂号总人数</div></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" style="background:#67C23A;"><i class="el-icon-office-building"></i></div>
            <div class="stat-info"><div class="stat-num">{{ bedPeople }}</div><div class="stat-label">今日留观总人数</div></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" style="background:#E6A23C;"><i class="el-icon-s-order"></i></div>
            <div class="stat-info"><div class="stat-num">{{ orderPeople }}</div><div class="stat-label">今日门诊量</div></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top:20px;"><div style="text-align:center;padding:30px;color:#999;">欢迎使用医院门诊管理系统</div></el-card>
  </div>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "AdminLayout",
  data() { return { orderPeople: 0, bedPeople: 0 }; },
  methods: {
    requestPeople() { request.get("order/orderPeople").then(r=>{if(r.data.status===200)this.orderPeople=r.data.data||0;}); },
    requestBed() { request.get("bed/bedPeople").then(r=>{if(r.data.status===200)this.bedPeople=r.data.data||0;}); }
  },
  created() { this.requestPeople(); this.requestBed(); }
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
