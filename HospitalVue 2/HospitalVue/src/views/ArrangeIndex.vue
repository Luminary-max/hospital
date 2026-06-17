<template>
  <div>
    <el-card>
      <div slot="header"><i class="el-icon-date"></i> 排班日历</div>
      <div style="margin-bottom:16px;display:flex;align-items:center;flex-wrap:wrap;gap:8px;">
        <el-button size="small" @click="prevMonth"><i class="el-icon-arrow-left"></i> 上月</el-button>
        <span style="font-size:16px;font-weight:600;margin:0 12px;">{{ currentYear }}年{{ currentMonth }}月</span>
        <el-button size="small" @click="nextMonth">下月 <i class="el-icon-arrow-right"></i></el-button>
        <el-button size="small" type="primary" @click="today">今天</el-button>
        <el-tag type="success" style="margin-left:12px;">已排班</el-tag>
        <el-tag type="danger">未排班</el-tag>
        <el-tag type="warning">部分排班</el-tag>
      </div>

      <!-- 日历表头 -->
      <table class="cal-table" style="width:100%;border-collapse:collapse;">
        <thead><tr>
          <th v-for="d in weekDays" :key="d" style="padding:8px;background:#f5f7fa;border:1px solid #ebeef5;font-size:13px;">{{ d }}</th>
        </tr></thead>
        <tbody>
          <tr v-for="(week, wi) in calWeeks" :key="wi">
            <td v-for="(day, di) in week" :key="di" style="padding:4px;border:1px solid #ebeef5;vertical-align:top;min-height:70px;height:70px;width:14.28%;cursor:pointer;"
              :class="dayClass(day)"
              @click="day && dateClick(day)">
              <div v-if="day" style="font-size:13px;font-weight:600;margin-bottom:2px;">{{ day }}</div>
              <div v-if="day && arrangeMap[day]" style="font-size:11px;color:#67C23A;background:#f0f9eb;border-radius:3px;padding:1px 4px;display:inline-block;">
                {{ arrangeMap[day].count }}人
              </div>
              <div v-else-if="day && isFuture(day)" style="font-size:11px;color:#ccc;">空缺</div>
            </td>
          </tr>
        </tbody>
      </table>

      <div style="margin-top:16px;">
        <el-button type="primary" @click="goSectionIndex"><i class="el-icon-view"></i> 查看详细排班</el-button>
      </div>
    </el-card>
  </div>
</template>
<script>
import request from "@/utils/request.js";
import { getActivePath, setActivePath } from "@/utils/storage.js";
const ARRANGEDATE = "arrangeDate";
export default {
  name: "ArrangeIndex",
  data() {
    return {
      currentYear: 2026, currentMonth: 6,
      weekDays: ["一","二","三","四","五","六","日"],
      monthDays: [], calWeeks: [],
      arrangeMap: {}
    };
  },
  methods: {
    prevMonth() { this.currentMonth--; if(this.currentMonth<1){this.currentMonth=12;this.currentYear--;} this.renderCalendar(); },
    nextMonth() { this.currentMonth++; if(this.currentMonth>12){this.currentMonth=1;this.currentYear++;} this.renderCalendar(); },
    today() { var d=new Date(); this.currentYear=d.getFullYear(); this.currentMonth=d.getMonth()+1; this.renderCalendar(); },
    isFuture(day) {
      var d=new Date(this.currentYear,this.currentMonth-1,day);
      var n=new Date(); n.setHours(0,0,0,0);
      return d>n;
    },
    dayClass(day) {
      if(!day) return "cal-empty";
      var d=new Date(this.currentYear,this.currentMonth-1,day);
      var n=new Date(); n.setHours(0,0,0,0);
      var key=this.currentYear+"-"+(String(this.currentMonth).padStart(2,"0"))+"-"+String(day).padStart(2,"0");
      var a=this.arrangeMap[key];
      if(a && a.doctors>=9) return "cal-full";
      if(a && a.doctors>0) return "cal-partial";
      if(d<n) return "cal-past";
      return "cal-empty";
    },
    renderCalendar() {
      var first=new Date(this.currentYear,this.currentMonth-1,1).getDay()||7;
      var days=new Date(this.currentYear,this.currentMonth,0).getDate();
      var weeks=[]; var week=[];
      for(var i=1;i<first;i++){week.push(null);}
      for(var d=1;d<=days;d++){
        week.push(d);
        if(week.length===7){weeks.push(week);week=[];}
      }
      while(week.length<7){week.push(null);}
      if(week.length) weeks.push(week);
      this.calWeeks=weeks;
      this.loadArrange();
    },
    loadArrange() {
      var m=String(this.currentMonth).padStart(2,"0");
      var start=this.currentYear+"-"+m+"-01";
      var end=this.currentYear+"-"+m+"-"+new Date(this.currentYear,this.currentMonth,0).getDate().toString().padStart(2,"0")+" 23:59";
      var map={};
      request.get("admin/findAllOrders",{params:{pageNumber:1,size:500,query:""}}).then(r=>{
        if(r.data.status===200&&r.data.data.records){
          r.data.data.records.forEach(function(o){
            if(o.oStart && o.oStart.indexOf(m)>0){
              var k=o.oStart.substring(0,10);
              if(!map[k]) map[k]={count:0,doctors:{}};
              map[k].count++;
              map[k].doctors[o.dId]=true;
            }
          });
        }
        // 同时也从arrange表获取排班数据
        for(var k in map){ map[k].doctors=Object.keys(map[k].doctors).length; }
        this.arrangeMap=map;
      });
      // 额外从admin/findAllDoctors获取医生总数做参考
    },
    dateClick(day) {
      var dateStr=this.currentYear+"-"+String(this.currentMonth).padStart(2,"0")+"-"+String(day).padStart(2,"0");
      sessionStorage.setItem(ARRANGEDATE,dateStr);
      if(this.$route.path!=="/sectionIndex") this.$router.push("sectionIndex");
    },
    goSectionIndex() {
      if(this.$route.path!=="/sectionIndex") this.$router.push("sectionIndex");
    }
  },
  created() { var d=new Date(); this.currentYear=d.getFullYear(); this.currentMonth=d.getMonth()+1; this.renderCalendar(); }
};
</script>
<style scoped>
.cal-table td { transition:background .2s; }
.cal-table td:hover { background:#f5f7fa; }
.cal-empty { background:#fff; }
.cal-partial { background:#fdf6ec; }
.cal-full { background:#f0f9eb; }
.cal-past { background:#fafafa; color:#ccc; }
</style>
