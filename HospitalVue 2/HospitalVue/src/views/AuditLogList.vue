<template>
  <el-card>
    <div slot="header"><i class="el-icon-document"></i> 操作审计日志</div>
    <el-table :data="logData" border stripe style="width:100%">
      <el-table-column prop="alId" label="编号" width="60" align="center"></el-table-column>
      <el-table-column prop="alUserId" label="用户ID" width="80"></el-table-column>
      <el-table-column prop="alUserRole" label="角色" width="70" align="center"></el-table-column>
      <el-table-column prop="alAction" label="操作" width="70" align="center"></el-table-column>
      <el-table-column prop="alTarget" label="操作对象" width="120"></el-table-column>
      <el-table-column prop="alDetail" label="详情" show-overflow-tooltip min-width="200"></el-table-column>
      <el-table-column prop="alIp" label="IP" width="130"></el-table-column>
      <el-table-column prop="alCreateTime" label="操作时间" width="160"></el-table-column>
    </el-table>
    <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}" background
      layout="total,sizes,prev,pager,next,jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[10,20,50]" :total="total"></el-pagination>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "AuditLogList",
  data() { return { pageNumber:1, size:10, logData:[], total:0 }; },
  methods: {
    loadData() { request.get("auditLog/findAll",{params:{pageNumber:this.pageNumber,size:this.size}}).then(r=>{const d=r.data.data;this.logData=d.records||[];this.total=d.total||0;}); }
  },
  created() { this.loadData(); }
};
</script>


