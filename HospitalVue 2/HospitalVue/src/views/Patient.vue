<template>
  <el-container class="layout-container">
    <el-header class="layout-header">
      <div class="header-left">
        <img src="@/assets/img/1.png" class="header-logo" />
        <span class="header-title">医院门诊管理系统</span>
      </div>
      <div class="header-right">
        <div class="user-avatar"><img src="../assets/11.jpg" /></div>
        <el-dropdown @command="handleCommand" trigger="click">
          <span class="el-dropdown-link">欢迎您，<b>{{ userName }}</b>&nbsp;<i class="el-icon-caret-bottom"></i></span>
          <el-dropdown-menu slot="dropdown"><el-dropdown-item command="logout">退出登录</el-dropdown-item></el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>
    <el-container class="layout-body">
      <el-aside width="200px" class="layout-aside">
        <el-menu :default-active="activePath" background-color="#304156" text-color="#b8c7ce" active-text-color="#409EFF" router>
          <el-menu-item index="patientLayout" @click="menuClick('patientLayout')"><i class="el-icon-s-home"></i><span>首页</span></el-menu-item>
          <el-menu-item index="orderOperate" @click="menuClick('orderOperate')"><i class="el-icon-plus"></i><span>在线挂号</span></el-menu-item>
          <el-menu-item index="myOrder" @click="menuClick('myOrder')"><i class="el-icon-postcard"></i><span>我的挂号</span></el-menu-item>
          <el-menu-item index="myPrescription" @click="menuClick('myPrescription')"><i class="el-icon-document"></i><span>我的处方</span></el-menu-item>
          <el-menu-item index="myEmr" @click="menuClick('myEmr')"><i class="el-icon-reading"></i><span>我的病历</span></el-menu-item>
          <el-menu-item index="queueStatus" @click="menuClick('queueStatus')"><i class="el-icon-s-order"></i><span>排队状态</span></el-menu-item>
          <el-menu-item index="patientCard" @click="menuClick('patientCard')"><i class="el-icon-user"></i><span>个人信息</span></el-menu-item>
          <el-menu-item index="myNotificationList" @click="menuClick('myNotificationList')"><i class="el-icon-bell"></i><span>消息通知</span></el-menu-item>
          <el-menu-item index="patientDelivery" @click="menuClick('patientDelivery')"><i class="el-icon-truck"></i><span>送药申请</span></el-menu-item>
          <el-menu-item index="patientReports" @click="menuClick('patientReports')"><i class="el-icon-download"></i><span>报告下载</span></el-menu-item>
          <el-menu-item index="patientSmartHospital" @click="menuClick('patientSmartHospital')"><i class="el-icon-cpu"></i><span>智慧服务</span></el-menu-item>
          <el-menu-item index="patientReferral" @click="menuClick('patientReferral')"><i class="el-icon-position"></i><span>转诊单</span></el-menu-item>
          <el-menu-item index="patientInsuranceEstimate" @click="menuClick('patientInsuranceEstimate')"><i class="el-icon-bank-card"></i><span>医保试算</span></el-menu-item>
          <el-menu-item index="patientAnnouncement" @click="menuClick('patientAnnouncement')"><i class="el-icon-bell"></i><span>医院公告</span></el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="layout-main"><router-view></router-view></el-main>
    </el-container>
  </el-container>
</template>
<script>
import jwtDecode from "jwt-decode";
import { getToken, clearToken, getActivePath, setActivePath} from "@/utils/storage.js";
export default {
  name: "Patient",
  data() { return { userName: "", activePath:"" }; },
  methods: {
    handleCommand(c) { if(c==="logout"){clearToken();this.$message.success("退出成功");this.$router.push("login");} },
    tokenDecode(t){if(t!==null)return jwtDecode(t);},
    menuClick(p){this.activePath=p;setActivePath(p);if(this.$route.path!=="/"+p)this.$router.push(p);}
  },
  created() { this.activePath=getActivePath(); var d=this.tokenDecode(getToken());this.userName=d?d.pName||"":""; }
};
</script>
<style scoped>
.layout-container,.layout-body{height:100%;}
.layout-header{display:flex;align-items:center;justify-content:space-between;background:linear-gradient(135deg,#304156,#1d2b3a);color:#fff;padding:0 20px;height:60px!important;}
.header-left{display:flex;align-items:center;gap:12px;}
.header-logo{width:36px;height:36px;}
.header-title{font-size:18px;font-weight:600;}
.header-right{display:flex;align-items:center;gap:10px;}
.user-avatar img{width:36px;height:36px;border-radius:50%;display:block;}
.el-dropdown-link{color:#fff;cursor:pointer;font-size:14px;}
.layout-aside{background:#304156;overflow-y:auto;}
.layout-aside .el-menu{background:#304156;border-right:none;}
.layout-aside .el-menu-item{height:44px;line-height:44px;}
.layout-main{background:#f0f2f5;padding:16px;overflow-y:auto;}
</style>
