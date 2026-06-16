<template>
  <el-card>
    <div slot="header"><span><i class="el-icon-bell"></i> 消息通知管理</span></div>
    <el-table :data="noticeData" border stripe style="width:100%">
      <el-table-column prop="nId" label="编号" width="60"></el-table-column>
      <el-table-column prop="pId" label="患者ID" width="70"></el-table-column>
      <el-table-column prop="nType" label="类型" width="80">
        <template slot-scope="s">
          <el-tag :type="s.row.nType==='queue'?'primary':s.row.nType==='payment'?'warning':s.row.nType==='refund'?'danger':'info'" size="mini">
            {{ {queue:'就诊',payment:'缴费',refund:'退费',reminder:'提醒'}[s.row.nType] || s.row.nType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="nTitle" label="标题" width="150"></el-table-column>
      <el-table-column prop="nContent" label="内容" min-width="300" show-overflow-tooltip></el-table-column>
      <el-table-column label="已读" width="60">
        <template slot-scope="s"><el-tag :type="s.row.nIsRead===1?'success':'danger'" size="mini">{{ s.row.nIsRead===1?'是':'否' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="nCreateTime" label="创建时间" width="160"></el-table-column>
    </el-table>
    <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}"
      :current-page="pageNumber" :page-sizes="[10,20,30]" :page-size="size"
      layout="total,sizes,prev,pager,next,jumper" :total="total" style="margin-top:15px;">
    </el-pagination>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "NotificationList",
  data() { return { pageNumber:1, size:10, noticeData:[], total:0 }; },
  methods: {
    loadData() {
      request.get("notification/findAll", { params: { pageNumber:this.pageNumber, size:this.size } })
        .then(res => { const d=res.data.data; this.noticeData=d.records||[]; this.total=d.total||0; });
    }
  },
  created() { this.loadData(); }
};
</script>
