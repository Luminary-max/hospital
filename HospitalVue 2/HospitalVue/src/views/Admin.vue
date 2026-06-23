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
          <span class="el-dropdown-link">欢迎您，<b>{{ userName }}</b>&nbsp;管理员&nbsp;<i class="el-icon-caret-bottom"></i></span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>
    <el-container class="layout-body">
      <el-aside width="210px" class="layout-aside">
        <el-menu :default-active="activePath" background-color="#304156" text-color="#b8c7ce" active-text-color="#409EFF" router>
          <el-menu-item index="adminLayout" @click="menuClick('adminLayout')"><i class="el-icon-s-home"></i><span>首页</span></el-menu-item>
          <el-submenu index="1">
            <template slot="title"><i class="el-icon-setting"></i><span>基础数据</span></template>
            <el-menu-item index="doctorList" @click="menuClick('doctorList')">医生信息</el-menu-item>
            <el-menu-item index="patientList" @click="menuClick('patientList')">患者信息</el-menu-item>
            <el-menu-item index="drugList" @click="menuClick('drugList')">药物信息</el-menu-item>
            <el-menu-item index="checkList" @click="menuClick('checkList')">检查项目</el-menu-item>
            <el-menu-item index="sectionList" @click="menuClick('sectionList')">科室列表</el-menu-item>
          </el-submenu>
          <el-submenu index="2">
            <template slot="title"><i class="el-icon-document"></i><span>门诊业务</span></template>
            <el-menu-item index="orderList" @click="menuClick('orderList')">挂号管理</el-menu-item>
            <el-menu-item index="arrangeIndex" @click="menuClick('arrangeIndex')">排班管理</el-menu-item>
            <el-menu-item index="queueManage" @click="menuClick('queueManage')">排队叫号</el-menu-item>
            <el-menu-item index="triageRecordList" @click="menuClick('triageRecordList')">分诊记录</el-menu-item>
<el-menu-item index="publicQueue" @click="menuClick('publicQueue')"><i class="el-icon-video-camera"></i>大屏叫号</el-menu-item>
            <el-menu-item index="prescriptionTemplateList" @click="menuClick('prescriptionTemplateList')">处方模板</el-menu-item>
            <el-menu-item index="diagnosisDictList" @click="menuClick('diagnosisDictList')">诊断词库</el-menu-item>
            <el-menu-item index="emrTemplateList" @click="menuClick('emrTemplateList')">病历模板</el-menu-item>
          </el-submenu>
          <el-submenu index="3">
            <template slot="title"><i class="el-icon-first-aid-kit"></i><span>药房管理</span></template>
            <el-menu-item index="pharmacyDispensingList" @click="menuClick('pharmacyDispensingList')"><span>发药管理</span></el-menu-item>
            <el-menu-item index="drugCategoryList" @click="menuClick('drugCategoryList')"><span>药品分类</span></el-menu-item>
            <el-menu-item index="drugBatchList" @click="menuClick('drugBatchList')"><span>药品批次</span></el-menu-item>
            <el-menu-item index="inventoryCenter" @click="menuClick('inventoryCenter')"><span>库存中心</span></el-menu-item>
          </el-submenu>
          <el-submenu index="5">
            <template slot="title"><i class="el-icon-monitor"></i><span>检查检验</span></template>
            <el-menu-item index="checkResultList" @click="menuClick('checkResultList')"><span>检查结果管理</span></el-menu-item>
          </el-submenu>
          <el-submenu index="6">
            <template slot="title"><i class="el-icon-s-finance"></i><span>收费管理</span></template>
            <el-menu-item index="rechargeList" @click="menuClick('rechargeList')"><span>收费管理</span></el-menu-item>
            <el-menu-item index="cashierSettlement" @click="menuClick('cashierSettlement')"><span>收银结算</span></el-menu-item>
            <el-menu-item index="refundApproval" @click="menuClick('refundApproval')"><span>退费审批</span></el-menu-item>
            <el-menu-item index="invoiceManage" @click="menuClick('invoiceManage')"><span>发票管理</span></el-menu-item>
          </el-submenu>
          <el-submenu index="4">
            <template slot="title"><i class="el-icon-office-building"></i><span>床位管理</span></template>
            <el-menu-item index="bedList" @click="menuClick('bedList')">床位列表</el-menu-item>
            <el-menu-item index="observeBedList" @click="menuClick('observeBedList')">观察区</el-menu-item>
          </el-submenu>
          <el-menu-item index="notificationList" @click="menuClick('notificationList')"><i class="el-icon-bell"></i><span>消息通知</span></el-menu-item>
          <el-submenu index="7">
            <template slot="title"><i class="el-icon-s-data"></i><span>数据统计</span></template>
            <el-menu-item index="dataExpore" @click="menuClick('dataExpore')">门诊统计</el-menu-item>
            <el-menu-item index="doctorStatsPanel" @click="menuClick('doctorStatsPanel')">医生统计</el-menu-item>
          </el-submenu>
          <el-menu-item index="auditLogList" @click="menuClick('auditLogList')"><i class="el-icon-document"></i><span>审计日志</span></el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="layout-main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>
<script>
import jwtDecode from "jwt-decode";
import { getToken, clearToken, getActivePath, setActivePath} from "@/utils/storage.js";
export default {
  name: "Admin",
  data() { return { userName: "", activePath:"" }; },
  watch: {
    $route: {
      immediate: true,
      handler(to) {
        var path = to.path.replace("/", "");
        // 子路由映射到父级菜单index
        var parentMap = { sectionIndex:"arrangeIndex", arrangeDoctor:"arrangeIndex" };
        this.activePath = parentMap[path] || path;
        setActivePath(this.activePath);
      }
    }
  },
  methods: {
    handleCommand(command) {
      if (command==="logout") {
        this.$confirm("确定退出登录?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"})
          .then(()=>{clearToken();this.$message.success("退出成功");this.$router.push("login");})
          .catch(()=>{});
      }
    },
    tokenDecode(token){ if(token!==null) return jwtDecode(token); },
    menuClick(path){ this.activePath=path; setActivePath(path); if(this.$route.path!=="/"+path) this.$router.push(path); }
  },
  created() {
    var _t=this.tokenDecode(getToken());this.userName=_t?_t.aName||"":"";
  }
};
</script>
<style scoped>
.layout-container,.layout-body{height:100%;}
.layout-header{display:flex;align-items:center;justify-content:space-between;background:linear-gradient(135deg,#304156,#1d2b3a);color:#fff;padding:0 20px;height:60px!important;border-bottom:1px solid rgba(255,255,255,0.05);}
.header-left{display:flex;align-items:center;gap:12px;}
.header-logo{width:36px;height:36px;}
.header-title{font-size:18px;font-weight:600;letter-spacing:1px;}
.header-right{display:flex;align-items:center;gap:10px;}
.user-avatar img{width:36px;height:36px;border-radius:50%;display:block;}
.el-dropdown-link{color:#fff;cursor:pointer;font-size:14px;}
.layout-aside{background:#304156;overflow-y:auto;}
.layout-aside .el-menu{background:#304156;border-right:none;}
.layout-aside .el-menu-item{height:44px;line-height:44px;}
.layout-aside .el-submenu__title{height:44px;line-height:44px;}
.layout-aside .el-menu-item.is-active{background:rgba(64,158,255,0.15)!important;}
.layout-main{background:#f0f2f5;padding:16px;overflow-y:auto;}
</style>
