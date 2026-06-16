<template>
  <el-card>
    <div slot="header"><span><i class="el-icon-document"></i> 分诊记录管理</span></div>
    <el-table :data="triageData" border stripe style="width:100%">
      <el-table-column prop="tId" label="编号" width="60"></el-table-column>
      <el-table-column prop="oId" label="订单ID" width="70"></el-table-column>
      <el-table-column prop="pId" label="患者ID" width="70"></el-table-column>
      <el-table-column prop="dId" label="医生ID" width="80"></el-table-column>
      <el-table-column label="分诊级别" width="80">
        <template slot-scope="s">
          <el-tag :type="s.row.tLevel===2?'danger':s.row.tLevel===1?'warning':'primary'" size="mini">
            {{ {0:'普通',1:'优先',2:'急诊'}[s.row.tLevel] || '普通' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template slot-scope="s">
          <el-tag :type="s.row.tStatus===2?'success':s.row.tStatus===1?'primary':'info'" size="mini">
            {{ {0:'待分诊',1:'已分诊',2:'已就诊'}[s.row.tStatus] || '待分诊' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="tNote" label="备注" min-width="260" show-overflow-tooltip></el-table-column>
      <el-table-column prop="tCreateTime" label="创建时间" width="160"></el-table-column>
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
  name: "TriageRecordList",
  data() { return { pageNumber:1, size:10, triageData:[], total:0 }; },
  methods: {
    loadData() {
      request.get("triage/findAll", { params: { pageNumber:this.pageNumber, size:this.size } })
        .then(res => { const d=res.data.data; this.triageData=d.records||[]; this.total=d.total||0; });
    }
  },
  created() { this.loadData(); }
};
</script>
