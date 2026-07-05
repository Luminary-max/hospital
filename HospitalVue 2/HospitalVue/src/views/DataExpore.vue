<template>
  <div style="padding:16px;">
    <el-card>
      <div slot="header"><i class="el-icon-s-data"></i> 数据统计</div>
      <el-tabs v-model="activeTab" @tab-click="onTabClick">
        <el-tab-pane label="医院运营统计" name="hospital">
          <div>
            <el-row :gutter="20">
              <el-col :span="6">
                <el-card shadow="hover" style="border-radius:8px;">
                  <div style="display:flex;align-items:center;">
                    <div style="width:50px;height:50px;border-radius:10px;background:#409EFF;display:flex;align-items:center;justify-content:center;"><i class="el-icon-user" style="font-size:24px;color:#fff;"></i></div>
                    <div style="margin-left:16px;"><div style="font-size:22px;font-weight:700;">{{ todayVisits }}</div><div style="font-size:13px;color:#909399;">今日挂号人数</div></div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover" style="border-radius:8px;">
                  <div style="display:flex;align-items:center;">
                    <div style="width:50px;height:50px;border-radius:10px;background:#67C23A;display:flex;align-items:center;justify-content:center;"><i class="el-icon-s-finance" style="font-size:24px;color:#fff;"></i></div>
                    <div style="margin-left:16px;"><div style="font-size:22px;font-weight:700;">{{ todayDrugIncome }}</div><div style="font-size:13px;color:#909399;">药费+检查费</div></div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover" style="border-radius:8px;">
                  <div style="display:flex;align-items:center;">
                    <div style="width:50px;height:50px;border-radius:10px;background:#E6A23C;display:flex;align-items:center;justify-content:center;"><i class="el-icon-s-finance" style="font-size:24px;color:#fff;"></i></div>
                    <div style="margin-left:16px;"><div style="font-size:22px;font-weight:700;">{{ todayRegIncome }}</div><div style="font-size:13px;color:#909399;">今日挂号费</div></div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover" style="border-radius:8px;">
                  <div style="display:flex;align-items:center;">
                    <div style="width:50px;height:50px;border-radius:10px;background:#F56C6C;display:flex;align-items:center;justify-content:center;"><i class="el-icon-user" style="font-size:24px;color:#fff;"></i></div>
                    <div style="margin-left:16px;"><div style="font-size:22px;font-weight:700;">{{ totalPatients }}</div><div style="font-size:13px;color:#909399;">累计患者</div></div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-row :gutter="20" style="margin-top:20px;">
              <el-col :span="12">
                <el-card>
                  <div slot="header"><span>科室挂号占比</span><el-button size="mini" style="float:right;" plain @click="exportSectionData">导出</el-button></div>
                  <div id="deptPieChart" style="width:100%;height:380px;"></div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card>
                  <div slot="header"><span>收入构成</span><el-button size="mini" style="float:right;" plain @click="exportIncomeData">导出</el-button></div>
                  <div id="incomeBarChart" style="width:100%;height:380px;"></div>
                </el-card>
              </el-col>
            </el-row>
            <el-row :gutter="20" style="margin-top:20px;">
              <el-col :span="12"><el-card><div slot="header">近20天挂号趋势</div><div id="visitTrendChart" style="width:100%;height:340px;"></div></el-card></el-col>
              <el-col :span="6"><el-card><div slot="header">患者性别比例</div><div id="genderChart" style="width:100%;height:340px;"></div></el-card></el-col>
              <el-col :span="6"><el-card><div slot="header">患者年龄分布</div><div id="ageChart" style="width:100%;height:340px;"></div></el-card></el-col>
            </el-row>
          </div>
        </el-tab-pane>
        <el-tab-pane label="医生个人统计" name="doctor">
          <div>
            <el-form inline size="small">
              <el-form-item label="医生"><el-select v-model="selectedDoctor" filterable placeholder="选择医生" style="width:200px;" @change="loadDoctorStats"><el-option v-for="d in doctorList" :key="d.dId" :label="d.dName+' - '+d.dSection" :value="d.dId"></el-option></el-select></el-form-item>
              <el-form-item label="周期"><el-select v-model="doctorPeriod" style="width:100px;" @change="loadDoctorStats"><el-option label="7天" value="7"></el-option><el-option label="30天" value="30"></el-option></el-select></el-form-item>
            </el-form>
            <el-row :gutter="20" v-if="doctorStats" style="margin-bottom:20px;">
              <el-col :span="8"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#409EFF;">{{ doctorStats.totalVisits }}</div><div style="color:#909399;">接诊总数</div></div></el-card></el-col>
              <el-col :span="8"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#67C23A;">{{ doctorStats.totalIncome }}</div><div style="color:#909399;">创收总额</div></div></el-card></el-col>
              <el-col :span="8"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#E6A23C;">{{ doctorStats.prescriptionCount }}</div><div style="color:#909399;">处方数</div></div></el-card></el-col>
            </el-row>
            <div v-if="!selectedDoctor" style="text-align:center;padding:60px 0;color:#999;">请选择医生查看统计</div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="药房统计" name="pharmacy">
          <div>
            <el-row :gutter="20">
              <el-col :span="6"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#F56C6C;">{{ pharmStats.expiredCount||0 }}</div><div style="color:#909399;">已过期批次</div></div></el-card></el-col>
              <el-col :span="6"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#E6A23C;">{{ pharmStats.expiringCount||0 }}</div><div style="color:#909399;">临期预警</div></div></el-card></el-col>
              <el-col :span="6"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#409EFF;">{{ pharmStats.drugTypeCount||0 }}</div><div style="color:#909399;">药品品种</div></div></el-card></el-col>
              <el-col :span="6"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#67C23A;">{{ pharmStats.totalStock||0 }}</div><div style="color:#909399;">总库存</div></div></el-card></el-col>
            </el-row>
            <el-row :gutter="20" style="margin-top:20px;">
              <el-col :span="12"><el-card><div slot="header">库存预警</div><el-table :data="pharmStats.lowStockList||[]" size="small" border max-height="300" style="width:100%"><el-table-column prop="drId" label="编号" width="70"></el-table-column><el-table-column prop="drName" label="药品" min-width="120"></el-table-column><el-table-column prop="drNumber" label="库存" width="60"></el-table-column><el-table-column prop="drMinStock" label="下限" width="60"></el-table-column></el-table></el-card></el-col>
              <el-col :span="12"><el-card><div slot="header">临期批次</div><el-table :data="pharmStats.expiringBatches||[]" size="small" border max-height="300" style="width:100%"><el-table-column prop="drId" label="药品" width="70"></el-table-column><el-table-column prop="dbBatchNo" label="批号" width="100"></el-table-column><el-table-column prop="dbExpireDate" label="过期日期" width="100"></el-table-column><el-table-column prop="dbQuantity" label="剩余" width="60"></el-table-column></el-table></el-card></el-col>
            </el-row>
          </div>
        </el-tab-pane>
        <el-tab-pane label="收入分析" name="income">
          <div>
            <el-row :gutter="20">
              <el-col :span="24"><el-card><div slot="header"><span>每日收入趋势</span><el-radio-group v-model="incomePeriod" @change="loadIncomeAnalysis" size="mini" style="float:right;"><el-radio-button label="7">7天</el-radio-button><el-radio-button label="20">20天</el-radio-button></el-radio-group></div><div id="incomeTrendChart" style="width:100%;height:500px;"></div></el-card></el-col>
            </el-row>
            <el-row :gutter="20" style="margin-top:16px;">
              <el-col :span="12"><el-card style="min-height:360px;"><div slot="header">收入构成</div><div id="incomeBreakdownChart" style="width:100%;height:300px;"></div></el-card></el-col>
              <el-col :span="12"><el-card style="min-height:360px;"><div slot="header">收入汇总</div>
                  <div style="padding:30px 20px;">
                  <el-row :gutter="20" style="margin-bottom:16px;">
                    <el-col :span="12"><div style="background:#f0f5ff;border-radius:8px;padding:20px;text-align:center;"><div style="font-size:12px;color:#909399;">总收入</div><div style="font-size:28px;font-weight:700;color:#409EFF;">{{ incomeSummary.total }}</div></div></el-col>
                    <el-col :span="12"><div style="background:#f6ffed;border-radius:8px;padding:20px;text-align:center;"><div style="font-size:12px;color:#909399;">日均收入</div><div style="font-size:28px;font-weight:700;color:#67C23A;">{{ incomeSummary.dailyAvg }}</div></div></el-col>
                  </el-row>
                  <el-row :gutter="20">
                    <el-col :span="12"><div style="background:#fff7e6;border-radius:8px;padding:20px;text-align:center;"><div style="font-size:12px;color:#909399;">挂号费</div><div style="font-size:22px;font-weight:700;color:#E6A23C;">{{ incomeSummary.registration }}</div></div></el-col>
                    <el-col :span="12"><div style="background:#fef0f0;border-radius:8px;padding:20px;text-align:center;"><div style="font-size:12px;color:#909399;">药费+检查费</div><div style="font-size:22px;font-weight:700;color:#F56C6C;">{{ incomeSummary.drugCheck }}</div></div></el-col>
                  </el-row>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-card style="margin-top:16px;"><div slot="header">每日明细</div>
              <el-table :data="incomeDetailList" size="small" border style="width:100%"><el-table-column prop="date" label="日期" width="100"></el-table-column><el-table-column label="挂号费" width="120"><template slot-scope="s">{{ s.row.regFee }}</template></el-table-column><el-table-column label="药费+检查费" width="120"><template slot-scope="s">{{ s.row.drugFee }}</template></el-table-column><el-table-column label="合计" width="120"><template slot-scope="s">{{ (Number(s.row.regFee)+Number(s.row.drugFee)).toFixed(2) }}</template></el-table-column></el-table>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import request from "@/utils/request.js";
export default {
  name: "dataExpore",
  data() {
    return {
      activeTab: "hospital",
      todayVisits: 0, todayDrugIncome: 0, todayRegIncome: 0, totalPatients: 0,
      sevenDate: [],
      selectedDoctor: "", doctorList: [], doctorPeriod: "7", doctorStats: null,
      pharmStats: { lowStockList: [], expiringBatches: [], expiredCount: 0, expiringCount: 0, drugTypeCount: 0, totalStock: 0 },
      drugConsumptionRank: [],
      incomePeriod: "20", incomeDetailList: [],
      incomeSummary: { total: "0.00", registration: "0.00", drugCheck: "0.00", dailyAvg: "0.00", maxDay: "0.00" }
    };
  },
  methods: {
    loadHospitalStats: function() {
      var self = this;
      request.get("order/orderPeople").then(function(r) { if (r.data.status === 200) self.todayVisits = r.data.data || 0; });
      request.get("order/orderDailyIncome").then(function(r) {
        if (r.data.status === 200) {
          var d = r.data.data;
          if (d && d.drugIncome && d.drugIncome.length) {
            self.todayDrugIncome = d.drugIncome[d.drugIncome.length - 1] || 0;
            self.todayRegIncome = d.regIncome ? d.regIncome[d.regIncome.length - 1] || 0 : 0;
          }
        }
      });
      request.get("patient/patientAge").then(function(r) {
        if (r.data.status === 200) { var ages = r.data.data || []; self.totalPatients = ages.reduce(function(s, v) { return s + (parseInt(v) || 0); }, 0); }
      });
      self.$nextTick(function() { self.initDeptChart(); self.initIncomeBarChart(); self.initVisitTrend(); self.initGenderChart(); self.initAgeChart(); });
    },
    initDeptChart: function() {
      var chart = document.getElementById("deptPieChart"); if (!chart) return;
      var myChart = this.$echarts.init(chart);
      var self = this;
      request.get("order/orderSection").then(function(res) {
        var raw = res.data.data || [];
        var data = raw.map(function(item) { return { value: item.countSection, name: item.doctor ? item.doctor.dSection : "未知" }; });
        myChart.setOption({
          tooltip: { trigger: "item", formatter: "{b}: {c}人 ({d}%)" },
          legend: { bottom: 0, type: "scroll" },
          series: [{ type: "pie", radius: ["35%", "60%"], center: ["50%", "45%"], data: data.length ? data : [{ value: 1, name: "暂无数据" }], label: { show: true, formatter: "{b}\n{d}%" } }]
        });
      });
    },
    initIncomeBarChart: function() {
      var chart = document.getElementById("incomeBarChart"); if (!chart) return;
      var myChart = this.$echarts.init(chart);
      request.get("order/orderDailyIncome").then(function(res) {
        var d = res.data.data || {}; var dates = d.dates || [];
        myChart.setOption({
          tooltip: { trigger: "axis" }, legend: { data: ["药费+检查费", "挂号费"], bottom: 0 },
          xAxis: { type: "category", data: dates.slice(-7), axisLabel: { rotate: 20, fontSize: 10 } },
          yAxis: { type: "value" }, grid: { left: 50, right: 20, bottom: 50, top: 20 },
          series: [
            { name: "药费+检查费", type: "bar", data: (d.drugIncome || []).slice(-7), itemStyle: { color: "#E6A23C" }, barWidth: "35%" },
            { name: "挂号费", type: "bar", data: (d.regIncome || []).slice(-7), itemStyle: { color: "#67C23A" }, barWidth: "35%" }
          ]
        });
      });
    },
    initVisitTrend: function() {
      var chart = document.getElementById("visitTrendChart"); if (!chart) return;
      var myChart = this.$echarts.init(chart);
      var self = this;
      request.get("order/orderSeven").then(function(res) {
        myChart.setOption({
          tooltip: { trigger: "axis" }, grid: { left: 45, right: 20, bottom: 25, top: 10 },
          xAxis: { type: "category", data: self.sevenDate }, yAxis: { type: "value", minInterval: 1 },
          series: [{ data: res.data.data || [], type: "line", smooth: true, areaStyle: { color: "rgba(64,158,255,0.2)" }, lineStyle: { color: "#409EFF" }, itemStyle: { color: "#409EFF" } }]
        });
      });
    },
    initGenderChart: function() {
      var chart = document.getElementById("genderChart"); if (!chart) return;
      var myChart = this.$echarts.init(chart);
      request.get("order/orderGender").then(function(res) {
        var d = res.data.data || [];
        var data = d.length ? d.map(function(item) { return { value: item.countGender, name: item.patient ? item.patient.pGender : "未知" }; }) : [{ value: 1, name: "暂无数据" }];
        myChart.setOption({ tooltip: { trigger: "item" }, legend: { bottom: 0 }, series: [{ type: "pie", radius: "55%", data: data }] });
      });
    },
    initAgeChart: function() {
      var chart = document.getElementById("ageChart"); if (!chart) return;
      var myChart = this.$echarts.init(chart);
      request.get("patient/patientAge").then(function(res) {
        var labels = ["0-9","10-19","20-29","30-39","40-49","50-59","60-69","70-79","80-89","90-99"];
        var vals = (res.data.data || []).map(function(v) { return v || 0; });
        myChart.setOption({
          tooltip: { trigger: "item" }, legend: { bottom: 0 },
          series: [{ type: "pie", radius: ["40%", "70%"], data: labels.map(function(l, i) { return { value: vals[i], name: l }; }), label: { show: false }, emphasis: { label: { show: true, fontSize: "16", fontWeight: "bold" } } }]
        });
      });
    },
    exportSectionData: function() {
      var self = this;
      request.get("order/orderSection").then(function(res) {
        var raw = res.data.data || []; var csv = "科室,挂号人数\n"; raw.forEach(function(item) { csv += (item.doctor ? item.doctor.dSection : "未知") + "," + item.countSection + "\n"; }); self.downloadCsv(csv, "科室挂号占比.csv");
      });
    },
    exportIncomeData: function() {
      var self = this;
      request.get("order/orderDailyIncome").then(function(res) {
        var d = res.data.data || {}; var dates = d.dates || []; var csv = "日期,药费+检查费,挂号费\n"; for (var i = 0; i < dates.length; i++) csv += dates[i] + "," + ((d.drugIncome || [])[i] || 0) + "," + ((d.regIncome || [])[i] || 0) + "\n"; self.downloadCsv(csv, "收入构成.csv");
      });
    },
    loadDoctorList: function() {
      request.get("admin/findAllDoctors", { params: { pageNumber: 1, size: 200, query: "" } }).then(function(res) {
        if (res.data.status === 200) { var data = res.data.data; this.doctorList = data.doctors || data.records || []; }
      }.bind(this));
    },
    loadDoctorStats: function() {
      if (!this.selectedDoctor) return; this.doctorStats = null;
      var days = parseInt(this.doctorPeriod) || 7;
      var endDate = new Date(); var startDate = new Date(); startDate.setDate(startDate.getDate() - days);
      var fmt = function(d) { return d.getFullYear() + "-" + String(d.getMonth() + 1).padStart(2, "0") + "-" + String(d.getDate()).padStart(2, "0"); };
      var self = this;
      request.get("order/findOrdersByDate", { params: { start: fmt(startDate) + " 00:00", end: fmt(endDate) + " 23:59" } }).then(function(res) {
        if (res.data.status === 200) {
          var allOrders = res.data.data || [];
          var docOrders = allOrders.filter(function(o) { return String(o.dId) === String(self.selectedDoctor); });
          var totalVisits = docOrders.length;
          var totalIncome = docOrders.reduce(function(s, o) { return s + parseFloat(o.oTotalPrice || 0); }, 0);
          var dailyMap = {};
          docOrders.forEach(function(o) { var day = (o.oStart || "").substring(0, 10); if (!dailyMap[day]) dailyMap[day] = { visits: 0, income: 0 }; dailyMap[day].visits++; dailyMap[day].income += parseFloat(o.oTotalPrice || 0); });
          var dailyList = Object.keys(dailyMap).sort().map(function(k) { return { date: k, visits: dailyMap[k].visits, income: dailyMap[k].income.toFixed(2) }; });
          self.doctorStats = { totalVisits: totalVisits, totalIncome: totalIncome.toFixed(2), prescriptionCount: docOrders.filter(function(o) { return o.oDrug && o.oDrug.length > 0; }).length, dailyList: dailyList };
        }
      }).catch(function() {
        this.doctorStats = { totalVisits: 0, totalIncome: "0.00", prescriptionCount: 0, dailyList: [] };
      }.bind(this));
    },
    loadPharmacyStats: function() {
      var self = this;
      request.get("inventory/dashboard", { params: { expiryDays: 90 } }).then(function(res) {
        if (res.data.status === 200) {
          var d = res.data.data || {}; self.pharmStats.lowStockList = d.lowStock || []; self.pharmStats.expiringBatches = d.expiringBatches || []; self.pharmStats.expiredCount = d.expiredCount || 0; self.pharmStats.expiringCount = (d.expiringBatches || []).length;
        }
      });
      request.get("drug/findAllDrugs", { params: { pageNumber: 1, size: 200, query: "", typeFilter: "" } }).then(function(res) {
        if (res.data.status === 200) {
          var data = res.data.data; var drugs = data.drugs || data.records || []; self.pharmStats.drugTypeCount = drugs.length; self.pharmStats.totalStock = drugs.reduce(function(s, d) { return s + parseInt(d.drNumber || 0); }, 0);
        }
      });
    },
    isExpired: function(dateStr) {
      if (!dateStr) return false; return new Date(dateStr) < new Date();
    },
    loadIncomeAnalysis: function() {
      var n = parseInt(this.incomePeriod) || 20;
      var self = this;
      request.get("order/orderDailyIncome").then(function(res) {
        if (res.data.status !== 200) return;
        var d = res.data.data || {}; var dates = d.dates || []; var drugInc = d.drugIncome || []; var regInc = d.regIncome || [];
        // 过滤掉全为0的数据段(从第一个非零数据开始)，排除demo数据干扰
        var firstNonZero = -1;
        for (var zi = 0; zi < dates.length; zi++) {
          if ((parseFloat(drugInc[zi]||0) + parseFloat(regInc[zi]||0)) > 0) {
            firstNonZero = zi;
            break;
          }
        }
        var sliceDates = dates, sliceDrug = drugInc, sliceReg = regInc;
        if (firstNonZero >= 0 && firstNonZero > 1) {
          var startIdx = Math.max(0, firstNonZero - 1);
          sliceDates = dates.slice(startIdx); sliceDrug = drugInc.slice(startIdx); sliceReg = regInc.slice(startIdx);
        }
        sliceDates = sliceDates.slice(-n); sliceDrug = sliceDrug.slice(-n); sliceReg = sliceReg.slice(-n);
        // 如果过滤后数据太少（少于3天），就用最近几天
        if (sliceDates.length < 3) {
          sliceDates = dates.slice(-Math.min(n, 7));
          sliceDrug = drugInc.slice(-Math.min(n, 7));
          sliceReg = regInc.slice(-Math.min(n, 7));
        }
        self.incomeDetailList = sliceDates.map(function(date, i) { return { date: date, regFee: parseFloat(sliceReg[i] || 0).toFixed(2), drugFee: parseFloat(sliceDrug[i] || 0).toFixed(2) }; });
        var totalDrug = sliceDrug.reduce(function(s, v) { return s + parseFloat(v || 0); }, 0);
        var totalReg = sliceReg.reduce(function(s, v) { return s + parseFloat(v || 0); }, 0);
        var total = totalDrug + totalReg;
        self.incomeSummary = { total: total.toFixed(2), registration: totalReg.toFixed(2), drugCheck: totalDrug.toFixed(2), dailyAvg: (total / (sliceDates.length || 1)).toFixed(2), maxDay: "0.00" };
        self.$nextTick(function() {
          var trendChart = document.getElementById("incomeTrendChart");
          if (trendChart) {
            trendChart.style.width = "100%";
            trendChart.style.height = "500px";
            var old = self.$echarts.getInstanceByDom(trendChart);
            if (old) old.dispose();
            var tc = self.$echarts.init(trendChart);
            // 天数少时显示全部标签，天数多时自动间隔
            var interval = sliceDates.length <= 10 ? 0 : (sliceDates.length <= 20 ? 1 : 2);
            tc.setOption({
              tooltip: { trigger: "axis" },
              legend: { data: ["总收入", "药费+检查费", "挂号费"], bottom: 10 },
              grid: { left: 80, right: 30, bottom: 65, top: 30 },
              xAxis: { type: "category", data: sliceDates, axisLabel: { rotate: 35, fontSize: 10, interval: interval } },
              yAxis: { type: "value", splitLine: { lineStyle: { type: "dashed" } }, name: "金额 (元)" },
              series: [
                { name: "总收入", type: "line", data: sliceDrug.map(function(v, i) { return parseFloat(v || 0) + parseFloat(sliceReg[i] || 0); }), smooth: true, lineStyle: { color: "#409EFF", width: 2 }, itemStyle: { color: "#409EFF" } },
                { name: "药费+检查费", type: "bar", data: sliceDrug, itemStyle: { color: "#E6A23C" }, barWidth: "30%" },
                { name: "挂号费", type: "bar", data: sliceReg, itemStyle: { color: "#67C23A" }, barWidth: "30%" }
              ]
            });
          }
          var pieChart = document.getElementById("incomeBreakdownChart");
          if (pieChart) {
            var pc = self.$echarts.init(pieChart);
            pc.setOption({
              tooltip: { trigger: "item", formatter: "{b}: ¥{c} ({d}%)" }, legend: { bottom: 0 },
              series: [{ type: "pie", radius: ["30%", "55%"], center: ["50%", "45%"], data: [{ value: totalDrug, name: "药费+检查费", itemStyle: { color: "#E6A23C" } }, { value: totalReg, name: "挂号费", itemStyle: { color: "#67C23A" } }], label: { show: true, formatter: "{b}: ¥{c}" } }]
            });
          }
        }.bind(self));
      });
    },
    exportIncomeReport: function() {
      var csv = "日期,挂号费,药费+检查费,合计\n"; this.incomeDetailList.forEach(function(d) { var total = (parseFloat(d.regFee) + parseFloat(d.drugFee)).toFixed(2); csv += d.date + "," + d.regFee + "," + d.drugFee + "," + total + "\n"; }); this.downloadCsv(csv, "收入分析报表.csv");
    },
    downloadCsv: function(content, filename) {
      var bom = "﻿"; var blob = new Blob([bom + content], { type: "text/csv;charset=utf-8" }); var a = document.createElement("a"); a.href = URL.createObjectURL(blob); a.download = filename; a.click(); URL.revokeObjectURL(a.href);
    },
    onTabClick: function(tab) {
      var self = this;
      if (tab.name === "income") {
        self.loadIncomeAnalysis();
        self.$nextTick(function() {
          setTimeout(function() {
            var chart = document.getElementById("incomeTrendChart");
            if (chart) {
              var instance = self.$echarts.getInstanceByDom(chart);
              if (instance) {
                instance.resize();
              } else {
                // 如果实例不存在，重新初始化
                self.loadIncomeAnalysis();
              }
            }
            var pieChart = document.getElementById("incomeBreakdownChart");
            if (pieChart) {
              var pi = self.$echarts.getInstanceByDom(pieChart);
              if (pi) pi.resize();
            }
          }, 300);
        });
      }
    }
  },
  created: function() {
    var d = new Date();
    for (var i = 20; i > 0; i--) { var t = new Date(d); t.setDate(t.getDate() - i); this.sevenDate.push((t.getMonth()+1) + "-" + t.getDate()); }
    this.loadHospitalStats();
    this.loadDoctorList();
    this.loadPharmacyStats();
    this.loadIncomeAnalysis();
  }
};
</script>


