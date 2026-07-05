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
          <span class="el-dropdown-link">欢迎您，<b>{{ userName }}</b>&nbsp;医生&nbsp;<i class="el-icon-caret-bottom"></i></span>
          <el-dropdown-menu slot="dropdown"><el-dropdown-item command="logout">退出登录</el-dropdown-item></el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>
    <el-container class="layout-body">
      <el-aside width="200px" class="layout-aside">
        <el-menu :default-active="activePath" background-color="#304156" text-color="#b8c7ce" active-text-color="#409EFF" router>
          <el-menu-item index="doctorLayout" @click="menuClick('doctorLayout')"><i class="el-icon-s-home"></i><span>首页</span></el-menu-item>
          <el-menu-item index="orderToday" @click="menuClick('orderToday')"><i class="el-icon-date"></i><span>今日挂号</span></el-menu-item>
          <el-menu-item index="doctorOrder" @click="menuClick('doctorOrder')"><i class="el-icon-postcard"></i><span>挂号查询</span></el-menu-item>
          <el-menu-item index="prescriptionList" @click="menuClick('prescriptionList')"><i class="el-icon-document"></i><span>处方管理</span></el-menu-item>
          <el-menu-item index="doctorQueue" @click="menuClick('doctorQueue')"><i class="el-icon-s-order"></i><span>叫号面板</span></el-menu-item>
          <el-menu-item index="doctorCard" @click="menuClick('doctorCard')"><i class="el-icon-user"></i><span>个人信息</span></el-menu-item>
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
  name: "Doctor",
  data() { return { userName: "", activePath:"" }; },
  methods: {
    handleCommand(c) { if(c==="logout"){clearToken();this.$message.success("退出成功");this.$router.push("login");} },
    tokenDecode(t){if(t!==null)return jwtDecode(t);},
    menuClick(p){this.activePath=p;setActivePath(p);if(this.$route.path!=="/"+p)this.$router.push(p);}
  },
  created() { this.activePath=getActivePath(); var d=this.tokenDecode(getToken());this.userName=d?d.dName||"":""; }
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
