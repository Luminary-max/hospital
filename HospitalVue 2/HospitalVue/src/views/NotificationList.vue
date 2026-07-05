<template>
  <el-card>
    <div slot="header"><span><i class="el-icon-bell"></i> 消息通知管理</span></div>
    <el-row style="margin-bottom:12px;">
      <el-button size="small" type="primary" @click="loadData"><i class="el-icon-refresh"></i> 刷新</el-button>
      <el-button size="small" @click="markAllRead" :disabled="unreadCount===0">全部标记已读（{{ unreadCount }}）</el-button>
    </el-row>
    <el-table :data="noticeData" border stripe style="width:100%">
      <el-table-column label="编号" prop="nId" width="60" align="center"></el-table-column>
      <el-table-column label="患者ID" prop="pId" width="70" align="center"></el-table-column>
      <el-table-column label="类型" width="70" align="center">
        <template slot-scope="s"><el-tag :type="{'queue':'primary','payment':'warning','refund':'danger','result':'success','pickup':'','missed':'info','reminder':'','followup':'','system':'info'}[s.row.nType] || 'info'" size="mini">{{ {queue:'就诊',payment:'缴费',refund:'退费',result:'检查结果',pickup:'取药',missed:'爽约',followup:'复诊',reminder:'提醒',system:'系统'}[s.row.nType] || s.row.nType }}</el-tag></template>
      </el-table-column>
      <el-table-column label="标题" prop="nTitle" width="150"></el-table-column>
      <el-table-column label="内容" prop="nContent" min-width="300" show-overflow-tooltip></el-table-column>
      <el-table-column label="状态" width="70" align="center">
        <template slot-scope="s">
          <el-tag :type="s.row.nIsRead===1?'success':'danger'" size="mini">{{ s.row.nIsRead===1?'已读':'未读' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="nCreateTime" width="160"></el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template slot-scope="s">
          <el-button v-if="s.row.nIsRead===0" type="success" size="mini" @click="markRead(s.row.nId)">标记已读</el-button>
          <span v-else style="color:#ccc;">-</span>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}" background
      layout="total,sizes,prev,pager,next,jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[10,20,50]" :total="total" style="margin-top:15px;"></el-pagination>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "NotificationList",
  data() { return { pageNumber:1, size:10, noticeData:[], total:0, unreadCount:0 }; },
  methods: {
    loadData() {
      request.get("notification/findAll",{params:{pageNumber:this.pageNumber,size:this.size}}).then(r=>{
        var d=r.data.data; this.noticeData=d.records||[]; this.total=d.total||0;
        this.unreadCount=this.noticeData.filter(function(n){return n.nIsRead===0}).length;
      });
    },
    markRead(nId) {
      request.get("notification/markRead",{params:{nId}}).then(r=>{if(r.data.status===200){this.$message.success("已标记");this.loadData();}});
    },
    markAllRead() {
      var list=this.noticeData.filter(function(n){return n.nIsRead===0;});
      if(!list.length)return;
      var p=[]; list.forEach(function(n){p.push(request.get("notification/markRead",{params:{nId:n.nId}}));});
      Promise.all(p).then(function(){this.$message.success("全部标记已读");this.loadData();}.bind(this));
    }
  },
  created() { this.loadData(); }
};
</script>


