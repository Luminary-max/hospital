<template>
  <div>
    <el-card>
      <div class="head">
        <div>
          <i class="iconfont icon-r-user1" style="margin:5px;font-size:18px;">{{ sectionOpt || '全部' }}医生列表</i>
          <el-select v-model="deptSelect" placeholder="选择科室" size="small" style="width:160px;margin-left:10px;" @change="onDeptChange">
            <el-option v-for="d in allDepts" :key="d" :label="d" :value="d"></el-option>
          </el-select>
        </div>
        <div v-if="monthDays.length > 0">
          <span style="font-size:13px;color:#666;">选择日期查看排班：</span>
          <el-button v-for="d in monthDays" :key="d" size="mini" :type="selectedDate===d?'primary':'default'" @click="dateClick(d)" style="margin:2px;">{{ d }}</el-button>
        </div>
      </div>
      <el-table :data="sectionData" stripe border>
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="dId" label="工号" width="80"></el-table-column>
        <el-table-column prop="dName" label="姓名" width="80"></el-table-column>
        <el-table-column prop="dGender" label="性别" width="55"></el-table-column>
        <el-table-column prop="dPost" label="职位" width="100"></el-table-column>
        <el-table-column prop="dSection" label="科室" width="100"></el-table-column>
        <el-table-column prop="dIntroduction" label="简介" min-width="160"></el-table-column>
        <el-table-column prop="dPrice" label="挂号费" width="70"></el-table-column>
        <el-table-column prop="dAvgStar" label="评分" width="70"></el-table-column>
        <el-table-column label="操作" width="100" v-if="clickTag">
          <template slot-scope="s">
            <el-button type="warning" size="mini" @click="openClick(s.row.dId, s.row.dName)">挂号</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="sectionData.length===0" style="text-align:center;padding:40px;color:#999;">暂无医生数据</div>
    </el-card>

    <el-dialog title="挂号" :visible.sync="orderFormVisible" width="420px">
      <el-form :model="orderForm" ref="orderForm" :rules="orderRules" label-width="100px" size="small">
        <el-form-item label="医生">{{ orderForm.dName }}</el-form-item>
        <el-form-item label="挂号日期">{{ orderForm.orderDate }}</el-form-item>
        <el-form-item label="时间段" prop="oTime">
          <el-select v-model="orderForm.oTime" style="width:100%"><el-option v-for="t in times" :key="t" :label="t" :value="t"></el-option></el-select>
        </el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="orderFormVisible=false">取消</el-button><el-button type="primary" @click="orderSuccess('orderForm')">确定</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import jwtDecode from "jwt-decode";
import request from "@/utils/request.js";
import { getToken } from "@/utils/storage.js";
export default {
  name: "sectionMessage",
  data() {
    return {
      sectionOpt: this.$route.query.sectionOpt || '',
      deptSelect: this.$route.query.sectionOpt || '',
      allDepts: [], sectionData: [], monthDays: [], selectedDate: '',
      clickTag: false, orderFormVisible: false, orderForm: {orderDate:""}, times: [],
      orderRules: { oTime: [{required:true,message:"请选择时间段",trigger:"blur"}] },
      orderDate: "", idTime: ""
    };
  },
  methods: {
    tokenDecode(t) { if(t) return jwtDecode(t); },
    requestTime(id) {
      this.idTime = id + this.orderDate;
      request.get("order/findOrderTime",{params:{arId:this.idTime}}).then(res=>{
        const isToday = new Date(this.orderDate).toDateString() === new Date().toDateString();
        var arr = [];
        var slots = [["08:30-09:30","eTOn","09:30"],["09:30-10:30","nTOt","10:30"],["10:30-11:30","tTOe","11:30"],["14:30-15:30","fTOf","15:30"],["15:30-16:30","fTOs","16:30"],["16:30-17:30","sTOs","17:30"]];
        slots.forEach(s => { if (!this.isAfter(s[2]) || !isToday) arr.push(s[0]+"  余号 "+res.data.data[s[1]]); });
        this.times = arr;
      });
    },
    isAfter(t) { var n=new Date(),p=t.split(":"); return n.getHours()>+p[0]||(n.getHours()==+p[0]&&n.getMinutes()>+p[1]); },
    orderSuccess(fn) {
      this.$refs[fn].validate(v=>{if(!v)return;
        var t=this.orderForm.oTime; if(t.indexOf("  ")>0) t=t.substring(0,11).trim();
        request.get("patient/addOrder",{params:{pId:this.tokenDecode(getToken()).pId,dId:this.orderForm.dId,oStart:this.orderForm.orderDate+" "+t,arId:this.idTime}}).then(r=>{if(r.data.status!=200)return this.$message.error("无号源");this.orderFormVisible=false;this.$message.success("挂号成功");});
      });
    },
    openClick(id,name) {
      this.orderForm.dId=id; this.orderForm.dName=name; this.orderFormVisible=true; this.requestTime(id);
    },
    dateClick(date) {
      this.selectedDate = date;
      var y = new Date().getFullYear();
      this.orderForm.orderDate = y+"-"+date;
      this.orderDate = y+"-"+date;
      request.get("/arrange/findByTime",{params:{arTime:y+"-"+date,dSection:this.deptSelect||this.sectionOpt}}).then(r=>{
        this.sectionData = (r.data.data||[]).map(i=>i.doctor); this.clickTag = true;
      });
    },
    nowDay(num) {
      var d=new Date(); if(d.getHours()>17||(d.getHours()===17&&d.getMinutes()>30)) num++;
      d.setDate(d.getDate()+num);
      this.monthDays.push(String(d.getMonth()+1).padStart(2,'0')+"-"+String(d.getDate()).padStart(2,'0'));
    },
    requestSection() {
      request.get("patient/findDoctorBySection",{params:{dSection:this.sectionOpt|| this.deptSelect}}).then(r=>{if(r.data.status===200)this.sectionData=r.data.data.doctors||[];});
    },
    onDeptChange(val) { this.sectionOpt=val; this.clickTag=false; this.requestSection(); }
  },
  created() {
    for(var i=0;i<7;i++)this.nowDay(i);
    request.get("admin/findAllDoctors",{params:{pageNumber:1,size:200,query:""}}).then(r=>{
      var d=r.data.data.doctors||[]; var map={}; d.forEach(function(x){if(x.dSection)map[x.dSection]=1;});
      this.allDepts=Object.keys(map);
    });
    this.requestSection();
    var t=this.tokenDecode(getToken()); if(t){this.orderForm.pName=t.pName;this.orderForm.pCard=t.pCard;this.orderForm.pId=t.pId;}
  }
};
</script>
<style scoped>
.head{display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:8px;margin-bottom:12px;}
</style>
