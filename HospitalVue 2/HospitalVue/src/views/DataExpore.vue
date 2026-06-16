<template>
  <div class="Echarts" style="padding:20px;">
    <el-row :gutter="20">
      <el-col :span="12"><div id="orderPeople" style="width:100%;height:360px;background:#fff;border-radius:8px;padding:10px;"></div></el-col>
      <el-col :span="12"><div id="incomeTrend" style="width:100%;height:360px;background:#fff;border-radius:8px;padding:10px;"></div></el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px;">
      <el-col :span="12"><div id="orderSection" style="width:100%;height:360px;background:#fff;border-radius:8px;padding:10px;"></div></el-col>
      <el-col :span="12"><div id="drugCategory" style="width:100%;height:360px;background:#fff;border-radius:8px;padding:10px;"></div></el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px;">
      <el-col :span="8"><div id="orderGender" style="width:100%;height:400px;background:#fff;border-radius:8px;padding:10px;"></div></el-col>
      <el-col :span="8"><div id="patientAge" style="width:100%;height:400px;background:#fff;border-radius:8px;padding:10px;"></div></el-col>
      <el-col :span="8">
        <div style="background:#fff;border-radius:8px;padding:15px;height:400px;overflow:auto;">
          <h4 style="text-align:center;margin:0 0 15px;">药品库存预警</h4>
          <el-table :data="lowStockDrugs" size="small" stripe style="width:100%">
            <el-table-column prop="drName" label="药品" width="100"></el-table-column>
            <el-table-column prop="drNumber" label="库存" width="60"></el-table-column>
            <el-table-column prop="drSpec" label="规格" width="100"></el-table-column>
            <el-table-column label="状态" width="70">
              <template slot-scope="s">
                <el-tag type="danger" size="mini" v-if="s.row.drNumber < 10">告急</el-tag>
                <el-tag type="warning" size="mini" v-else>偏低</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="lowStockDrugs.length === 0" style="text-align:center;color:#999;padding:60px 0;">暂无库存预警</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "dataExpore",
  data() {
    return {
      sevenDate: [], lowStockDrugs: []
    };
  },
  methods: {
    pastSeven(num) {
      var date = new Date();
      date.setDate(date.getDate() - num);
      return (date.getMonth() + 1) + "-" + date.getDate();
    },
    // 1. 近20天挂号人数折线图
    orderPeople() {
      var myChart = this.$echarts.init(document.getElementById("orderPeople"));
      request.get("order/orderSeven").then(res => {
        myChart.setOption({
          title: { text: "近20天挂号人数", left: "5%", textStyle:{fontSize:14} },
          tooltip: { trigger: "axis" },
          xAxis: { type: "category", data: this.sevenDate },
          yAxis: { type: "value", minInterval: 1 },
          series: [{ data: res.data.data, type: "line", smooth: true, areaStyle: { color: "rgba(64,158,255,0.2)" }, lineStyle: { color: "#409EFF" }, itemStyle: { color: "#409EFF" } }]
        });
      });
    },
    // 2. 近20天收入趋势图
    incomeTrend() {
      var myChart = this.$echarts.init(document.getElementById("incomeTrend"));
      request.get("order/orderDailyIncome").then(res => {
        var d = res.data.data;
        myChart.setOption({
          title: { text: "近20天收入趋势", left: "5%", textStyle:{fontSize:14} },
          tooltip: { trigger: "axis" },
          legend: { data: ["药费+检查费", "挂号费"], bottom: 0 },
          xAxis: { type: "category", data: d.dates, axisLabel: { rotate: 30 } },
          yAxis: { type: "value" },
          series: [
            { name: "药费+检查费", type: "bar", data: d.drugIncome, itemStyle: { color: "#E6A23C" } },
            { name: "挂号费", type: "bar", data: d.regIncome, itemStyle: { color: "#67C23A" } }
          ]
        });
      });
    },
    // 3. 挂号科室人数统计
    orderSection() {
      var myChart = this.$echarts.init(document.getElementById("orderSection"));
      request.get("order/orderSection").then(res => {
        var data = res.data.data.map(item => ({ value: item.countSection, name: item.doctor.dSection }));
        myChart.setOption({
          title: { text: "挂号科室人数统计", left: "center", textStyle:{fontSize:14} },
          tooltip: { trigger: "item" },
          xAxis: { type: "category", data: data.map(d => d.name), axisLabel: { interval: 0, rotate: 25, fontSize: 10 } },
          yAxis: { type: "value" },
          series: [{ type: "bar", data: data.map(d => d.value), itemStyle: { color: "#409EFF" }, showBackground: true, backgroundStyle: { color: "rgba(180,180,180,0.2)" } }]
        });
      });
    },
    // 4. 药品分类占比饼图
    drugCategory() {
      var myChart = this.$echarts.init(document.getElementById("drugCategory"));
      request.get("drug/findAllDrugs", { params: { pageNumber:1, size:100, query:"", typeFilter:"" } }).then(res => {
        var drugs = res.data.data.drugs || [];
        var western = drugs.filter(d => d.drType === 1).length;
        var chinese = drugs.filter(d => d.drType === 2).length;
        myChart.setOption({
          title: { text: "药品分类占比", left: "center", textStyle:{fontSize:14} },
          tooltip: { trigger: "item" },
          legend: { bottom: 0 },
          series: [{
            type: "pie", radius: ["40%", "70%"],
            data: [
              { value: western, name: "西药("+western+")", itemStyle:{color:"#E6A23C"} },
              { value: chinese, name: "中药("+chinese+")", itemStyle:{color:"#67C23A"} }
            ],
            label: { show: true, formatter: "{b}" }
          }]
        });
      });
      // 加载库存预警
      request.get("drug/findAllDrugs", { params: { pageNumber:1, size:100, query:"", typeFilter:"" } }).then(res => {
        var all = res.data.data.drugs || [];
        this.lowStockDrugs = all.filter(d => d.drNumber < 50).sort((a,b) => a.drNumber - b.drNumber);
      });
    },
    // 5. 患者性别比例
    orderGender() {
      var myChart = this.$echarts.init(document.getElementById("orderGender"));
      request.get("order/orderGender").then(res => {
        var d = res.data.data;
        myChart.setOption({
          title: { text: "患者性别比例", left: "center", textStyle:{fontSize:14} },
          tooltip: { trigger: "item" },
          legend: { bottom: 0 },
          series: [{
            type: "pie", radius: "50%",
            data: [
              { value: d[0].countGender, name: d[0].patient.pGender, itemStyle:{color:"#409EFF"} },
              { value: d[1].countGender, name: d[1].patient.pGender, itemStyle:{color:"#F56C6C"} }
            ]
          }]
        });
      });
    },
    // 6. 患者年龄段分布
    patientAge() {
      var myChart = this.$echarts.init(document.getElementById("patientAge"));
      request.get("patient/patientAge").then(res => {
        var labels = ['0-9','10-19','20-29','30-39','40-49','50-59','60-69','70-79','80-89','90-99'];
        var vals = res.data.data.map(v => v || 0);
        myChart.setOption({
          title: { text: "患者年龄段分布", left: "center", textStyle:{fontSize:14} },
          tooltip: { trigger: "item" },
          legend: { bottom: 0 },
          series: [{
            type: "pie", radius: ["40%","70%"],
            data: labels.map((l,i) => ({ value: vals[i], name: l })),
            label: { show: false }, emphasis: { label: { show: true, fontSize: "20", fontWeight: "bold" } }
          }]
        });
      });
    }
  },
  mounted() {
    this.orderPeople();
    this.incomeTrend();
    this.orderSection();
    this.drugCategory();
    this.orderGender();
    this.patientAge();
  },
  created() {
    for (var i = 20; i > 0; i--) { this.sevenDate.push(this.pastSeven(i)); }
  }
};
</script>
<style scoped>
.Echarts { background:#f0f2f5; min-height:100vh; }
#orderPeople,#incomeTrend,#orderSection,#drugCategory,#orderGender,#patientAge { box-shadow:0 2px 12px rgba(0,0,0,0.06); }
</style>
