<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" style="background:#409EFF;"><i class="el-icon-user"></i></div>
            <div class="stat-info"><div class="stat-num">{{ orderPeople }}</div><div class="stat-label">今日挂号</div></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" style="background:#67C23A;"><i class="el-icon-office-building"></i></div>
            <div class="stat-info"><div class="stat-num">{{ bedPeople }}</div><div class="stat-label">留观人数</div></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" style="background:#E6A23C;"><i class="el-icon-s-finance"></i></div>
            <div class="stat-info"><div class="stat-num">¥{{ todayIncome }}</div><div class="stat-label">今日收入</div></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" style="background:#F56C6C;"><i class="el-icon-warning"></i></div>
            <div class="stat-info"><div class="stat-num">{{ pendingCount }}</div><div class="stat-label">待处理</div></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:16px;">
      <el-col :span="12">
        <el-card>
          <div slot="header"><i class="el-icon-s-data"></i> 近7日挂号趋势</div>
          <div id="chartTrend" style="width:100%;height:200px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header"><i class="el-icon-warning-outline"></i> 待办事项</div>
          <el-table :data="todoList" stripe size="small">
            <el-table-column prop="label" label="事项" min-width="140"></el-table-column>
            <el-table-column prop="count" label="数量" width="60" align="center"></el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template slot-scope="s">
                <el-button size="mini" type="primary" @click="s.row.link()">去处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "AdminLayout",
  data() { return { orderPeople:0, bedPeople:0, todayIncome:0, pendingCount:0, todoList:[], pendingDrug:0, pendingPayment:0 }; },
  methods: {
    requestStats() {
      request.get("order/orderPeople").then(r=>{if(r.data.status===200)this.orderPeople=r.data.data||0;});
      request.get("bed/bedPeople").then(r=>{if(r.data.status===200)this.bedPeople=r.data.data||0;});
      request.get("order/orderDailyIncome").then(r=>{
        if(r.data.status===200){ var d=r.data.data; this.todayIncome=d.drugIncome?d.drugIncome[d.drugIncome.length-1]||0:0; }
      });
      request.get("pharmacy/findAll",{params:{pageNumber:1,size:1,status:0}}).then(r=>{if(r.data.status===200)this.pendingDrug=r.data.data.total||0;});
      // 使用专用计数接口代替加载全部订单
      request.get("order/pendingPaymentCount").then(r=>{if(r.data.status===200)this.pendingPayment=r.data.data||0;}).catch(()=>{
        // 降级：通过小数据量查询
        request.get("admin/findAllOrders",{params:{pageNumber:1,size:100,query:""}}).then(r=>{
          if(r.data.status===200){ var list=r.data.data.records||[]; this.pendingPayment=list.filter(o=>o.oPriceState===0&&o.oState===1).length; }
        });
      }).then(()=>{
        this.pendingCount=this.pendingDrug+this.pendingPayment;
        this.todoList=[
          {label:"待发药",count:this.pendingDrug,link:()=>{this.$router.push("/pharmacyDispensingList");}},
          {label:"待缴费订单",count:this.pendingPayment,link:()=>{this.$router.push("/orderList");}}
        ];
      });
      // 近7日趋势图
      request.get("order/orderSeven").then(r=>{
        if(r.data.status!==200)return;
        this.$nextTick(()=>{
          var c=this.$echarts.init(document.getElementById("chartTrend"));
          var labels=[]; var d=new Date(); for(var i=6;i>=0;i--){var t=new Date(d);t.setDate(t.getDate()-i);labels.push((t.getMonth()+1)+"-"+t.getDate());}
          c.setOption({
            tooltip:{trigger:"axis"},grid:{left:30,right:10,bottom:20,top:10},
            xAxis:{type:"category",data:labels,axisLabel:{fontSize:11}},
            yAxis:{type:"value",minInterval:1},
            series:[{type:"line",data:r.data.data.slice(-7),smooth:true,areaStyle:{color:"rgba(64,158,255,0.15)"},lineStyle:{color:"#409EFF"},itemStyle:{color:"#409EFF"}}]
          });
        });
      });
    }
  },
  created(){this.requestStats();}
};
</script>
<style scoped>
.dashboard{padding:10px 0;}
.stat-card{border-radius:8px;}
.stat-inner{display:flex;align-items:center;gap:16px;padding:8px 0;}
.stat-icon{width:50px;height:50px;border-radius:10px;display:flex;align-items:center;justify-content:center;flex-shrink:0;}
.stat-icon i{font-size:24px;color:#fff;}
.stat-num{font-size:22px;font-weight:700;color:#303133;}
.stat-label{font-size:13px;color:#909399;margin-top:2px;}
</style>
