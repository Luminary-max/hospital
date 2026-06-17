<template>
  <el-card>
    <div slot="header"><i class="el-icon-user"></i> 个人信息</div>
    <el-descriptions :column="2" border size="small">
      <el-descriptions-item label="姓名">{{ patientData.pName || '---' }}</el-descriptions-item>
      <el-descriptions-item label="账号">{{ patientData.pId || '---' }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ patientData.pGender || '---' }}</el-descriptions-item>
      <el-descriptions-item label="年龄">{{ patientData.pAge || '---' }}</el-descriptions-item>
      <el-descriptions-item label="出生日期">{{ patientData.pBirthday || '---' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ patientData.pPhone || '---' }}</el-descriptions-item>
      <el-descriptions-item label="证件号">{{ patientData.pCard || '---' }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ patientData.pEmail || '---' }}</el-descriptions-item>
      <el-descriptions-item label="医保号">{{ patientData.pInsuranceId || '---' }}</el-descriptions-item>
      <el-descriptions-item label="医保类型">{{ patientData.pInsuranceType || '---' }}</el-descriptions-item>
      <el-descriptions-item label="民族">{{ patientData.pNation || '---' }}</el-descriptions-item>
      <el-descriptions-item label="婚姻状况">{{ patientData.pMaritalStatus || '---' }}</el-descriptions-item>
      <el-descriptions-item label="血型">{{ patientData.pBloodType || '---' }}</el-descriptions-item>
      <el-descriptions-item label="联系人">{{ patientData.pContactPerson || '---' }}</el-descriptions-item>
      <el-descriptions-item label="联系人电话">{{ patientData.pContactPhone || '---' }}</el-descriptions-item>
      <el-descriptions-item label="家庭住址" :span="2">{{ patientData.pAddress || '---' }}</el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>
<script>
import jwtDecode from "jwt-decode";
import { getToken} from "@/utils/storage.js";
import request from "@/utils/request.js";
export default {
  name: "PatientCard",
  data() { return { userId:"", patientData:{} } },
  methods: {
    requestPatient(){ request.get("doctor/findPatientById",{params:{pId:this.userId}}).then(r=>{if(r.data.status===200)this.patientData=r.data.data;}); },
    tokenDecode(t){if(t)return jwtDecode(t);}
  },
  created(){ this.userId=this.tokenDecode(getToken()).pId; this.requestPatient(); }
};
</script>
