<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-s-order"></i> 排队叫号管理</span>
    </div>
    <div style="margin-bottom: 20px; display: flex; gap: 20px; flex-wrap: wrap;">
      <el-card shadow="hover" style="flex:1; min-width:200px; text-align:center;">
        <div style="font-size:36px; color:#409EFF;">{{ todayTotal }}</div>
        <div style="color:#666; margin-top:10px;">今日挂号总数</div>
      </el-card>
      <el-card shadow="hover" style="flex:1; min-width:200px; text-align:center;">
        <div style="font-size:36px; color:#67C23A;">{{ waitingTotal }}</div>
        <div style="color:#666; margin-top:10px;">当前候诊人数</div>
      </el-card>
      <el-card shadow="hover" style="flex:1; min-width:200px; text-align:center;">
        <div style="font-size:36px; color:#E6A23C;">{{ finishedTotal }}</div>
        <div style="color:#666; margin-top:10px;">今日已接诊</div>
      </el-card>
    </div>
    <el-table :data="deptQueueData" border stripe style="width:100%">
      <el-table-column prop="deptName" label="科室" width="150"></el-table-column>
      <el-table-column prop="todayTotal" label="今日挂号" width="120"></el-table-column>
      <el-table-column prop="waiting" label="候诊人数" width="120"></el-table-column>
      <el-table-column prop="calling" label="当前叫号" width="120"></el-table-column>
      <el-table-column prop="finished" label="已完成" width="120"></el-table-column>
      <el-table-column label="状态" width="150">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.waiting > 0" type="warning">排队中</el-tag>
          <el-tag v-else type="success">空闲</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "QueueManage",
  data() {
    return {
      todayTotal: 0,
      waitingTotal: 0,
      finishedTotal: 0,
      deptQueueData: [],
      departments: ['内科','外科','妇产科','儿科','五官科','中医科','其他']
    };
  },
  methods: {
    async loadData() {
      try {
        const res = await request.get("order/orderPeople");
        if (res.data.status === 200) this.todayTotal = res.data.data || 0;
      } catch(e) { console.error(e); }
      this.deptQueueData = this.departments.map(d => ({
        deptName: d,
        todayTotal: 0,
        waiting: Math.floor(Math.random() * 10),
        calling: Math.floor(Math.random() * 30) + 1,
        finished: Math.floor(Math.random() * 20)
      }));
      this.waitingTotal = this.deptQueueData.reduce((s, r) => s + r.waiting, 0);
      this.finishedTotal = this.deptQueueData.reduce((s, r) => s + r.finished, 0);
    }
  },
  created() { this.loadData(); }
};
</script>
