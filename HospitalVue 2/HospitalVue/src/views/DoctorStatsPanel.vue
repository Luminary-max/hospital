<template>
  <div style="padding:4px 0;">
    <el-card>
      <div slot="header">
        <i class="el-icon-s-data"></i> 医生个人统计
        <el-button size="mini" type="primary" plain style="float:right;" @click="exportReport">
          <i class="el-icon-download"></i> 导出报表
        </el-button>
      </div>

      <div style="margin-bottom:16px;display:flex;gap:10px;">
        <el-date-picker v-model="month" type="month" placeholder="选择月份" value-format="yyyy-MM" size="small" style="width:160px;" @change="loadStats"></el-date-picker>
        <el-select v-model="selectedDoctor" filterable placeholder="选择医生" size="small" style="width:220px;" @change="loadStats">
          <el-option v-for="d in doctorList" :key="d.dId" :label="d.dName+' ('+d.dSection+')'" :value="d.dId"></el-option>
        </el-select>
      </div>

      <el-row :gutter="16" style="margin-bottom:16px;">
        <el-col :span="6">
          <el-card shadow="hover" style="border-radius:8px;text-align:center;">
            <div style="font-size:30px;font-weight:700;color:#409EFF;line-height:1.2;">{{ stats.visitCount }}</div>
            <div style="font-size:13px;color:#909399;margin-top:4px;">接诊人次</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" style="border-radius:8px;text-align:center;">
            <div style="font-size:30px;font-weight:700;color:#67C23A;line-height:1.2;">{{ stats.prescriptionCount }}</div>
            <div style="font-size:13px;color:#909399;margin-top:4px;">处方数量</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" style="border-radius:8px;text-align:center;">
            <div style="font-size:30px;font-weight:700;color:#E6A23C;line-height:1.2;">{{ stats.checkCount }}</div>
            <div style="font-size:13px;color:#909399;margin-top:4px;">检查开具</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" style="border-radius:8px;text-align:center;">
            <div style="font-size:30px;font-weight:700;color:#F56C6C;line-height:1.2;">¥{{ stats.avgCost }}</div>
            <div style="font-size:13px;color:#909399;margin-top:4px;">平均费用</div>
          </el-card>
        </el-col>
      </el-row>

      <el-card>
        <div slot="header">每日接诊趋势</div>
        <div id="chartMonthly" style="width:100%;height:320px;"></div>
      </el-card>
    </el-card>
  </div>
</template>
<script>
import request from "@/utils/request.js";
import {getToken} from "@/utils/storage.js";
import jwtDecode from "jwt-decode";
export default {
  name: "DoctorStatsPanel",
  data() {
    return {
      month: "", selectedDoctor: '', doctorList: [],
      stats:{ visitCount:0, prescriptionCount:0, checkCount:0, avgCost:"0.00" },
      chartData:[]
    };
  },
  methods: {
    getDoctorId() {
      if (this.selectedDoctor) return this.selectedDoctor;
      try{ const t=getToken(); if(t){ const d=jwtDecode(t); return d.dId||""; } }catch(e){}
      return "";
    },
    loadDefaultMonth() {
      const d=new Date(); this.month=d.getFullYear()+"-"+String(d.getMonth()+1).padStart(2,'0'); this.loadStats();
    },
    loadDoctors() {
      request.get("admin/findAllDoctors",{params:{pageNumber:1,size:200,query:""}}).then(r=>{
        if(r.data.status===200){ this.doctorList=r.data.data.doctors||r.data.data.records||[]; }
      });
    },
    loadStats() {
      if(!this.month){ this.loadDefaultMonth(); return; }
      this.stats={ visitCount:0, prescriptionCount:0, checkCount:0, avgCost:"0.00" };
      this.chartData=[];
      const dId=this.getDoctorId();
      if(!dId) return this.$message.warning("无法获取医生信息");
      request.get("order/findOrderFinish",{params:{pageNumber:1,size:999,query:this.month,dId}}).then(r=>{
        const records=r.data.data.records||[];
        this.stats.visitCount=records.length;
        const totalFee=records.reduce((s,o)=>s+Number(o.oTotalPrice||0),0);
        this.stats.avgCost=this.stats.visitCount>0?(totalFee/this.stats.visitCount).toFixed(2):"0.00";
        this.stats.prescriptionCount=records.filter(o=>(o.oDrug&&o.oDrug!=="无")||(Number(o.oDrugFee||0)>0)).length;
        this.stats.checkCount=records.filter(o=>(o.oCheck&&o.oCheck!=="无")||(Number(o.oCheckFee||0)>0)).length;
        this.buildChart(records);
      }).catch(()=>{
        this.$message.error("获取统计数据失败");
      });
    },
    buildChart(records) {
      if(!this.month) return;
      const ym=this.month.split("-");
      const year=parseInt(ym[0]), month=parseInt(ym[1]);
      const daysInMonth=new Date(year,month,0).getDate();
      const daily=[];
      for(let d=1;d<=daysInMonth;d++){ daily.push({day:d,count:0}); }
      records.forEach(o=>{
        if(!o.oStart) return;
        const day=parseInt(o.oStart.substring(8,10));
        if(day>=1&&day<=daysInMonth) daily[day-1].count++;
      });
      this.chartData=daily;
      this.$nextTick(()=>this.renderChart());
    },
    renderChart() {
      const c=this.$echarts.init(document.getElementById("chartMonthly"));
      c.setOption({
        tooltip:{trigger:"axis"},
        grid:{left:40,right:20,bottom:30,top:20},
        xAxis:{type:"category",data:this.chartData.map(d=>d.day+"日"),axisLabel:{fontSize:11}},
        yAxis:{type:"value",minInterval:1},
        series:[{type:"bar",data:this.chartData.map(d=>d.count),itemStyle:{color:"#409EFF",borderRadius:[4,4,0,0]},barMaxWidth:30}]
      });
    },
    exportReport() {
      if(!this.stats.visitCount&&!this.stats.prescriptionCount) return this.$message.warning("暂无统计数据");
      const lines=["医生个人工作报表","统计月份："+this.month,"","接诊人次,"+this.stats.visitCount,"处方数量,"+this.stats.prescriptionCount,"检查开具数量,"+this.stats.checkCount,"平均费用(元),"+this.stats.avgCost,"","日期,接诊量"];
      this.chartData.forEach(d=>{ lines.push(d.day+"日,"+d.count); });
      const csv=lines.join("\n"); const b=new Blob(["﻿"+csv],{type:"text/csv;charset=utf-8"}); const a=document.createElement("a"); a.href=URL.createObjectURL(b); a.download="医生工作报表_"+this.month+".csv"; a.click();
    }
  },
  created(){ this.loadDoctors(); this.loadDefaultMonth(); }
};
</script>
