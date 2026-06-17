<template>
  <el-card>
    <div slot="header"><i class="el-icon-user"></i> 个人信息</div>
    <el-descriptions :column="2" border size="small">
      <el-descriptions-item label="姓名">{{ doctorData.dName || '---' }}</el-descriptions-item>
      <el-descriptions-item label="账号">{{ doctorData.dId || '---' }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ doctorData.dGender || '---' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ doctorData.dPhone || '---' }}</el-descriptions-item>
      <el-descriptions-item label="证件号">{{ doctorData.dCard || '---' }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ doctorData.dEmail || '---' }}</el-descriptions-item>
      <el-descriptions-item label="职位">{{ doctorData.dPost || '---' }}</el-descriptions-item>
      <el-descriptions-item label="所属科室">{{ doctorData.dSection || '---' }}</el-descriptions-item>
      <el-descriptions-item label="挂号费">{{ doctorData.dPrice || '---' }}</el-descriptions-item>
      <el-descriptions-item label="评分">{{ doctorData.dAvgStar || '---' }}</el-descriptions-item>
      <el-descriptions-item label="简介" :span="2">{{ doctorData.dIntroduction || '---' }}</el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>
<script>
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
import request from "@/utils/request.js";
export default {
  name: "DoctorCard",
  data() { return { userId:"", doctorData:{} } },
  methods: {
    requestDoctor(){ request.get("admin/findDoctor",{params:{dId:this.userId}}).then(r=>{if(r.data.status===200)this.doctorData=r.data.data;}); },
    tokenDecode(t){if(t)return jwtDecode(t);}
  },
  created(){ this.userId=this.tokenDecode(getToken()).dId; this.requestDoctor(); }
};
</script>
