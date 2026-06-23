<template>
  <div style="padding:4px 0;">
    <el-card>
      <div slot="header"><i class="el-icon-refund"></i> 退费审批管理</div>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-radio-group v-model="statusTab" @change="switchTab" size="small">
            <el-radio-button :label="0">待审核</el-radio-button>
            <el-radio-button :label="1">已通过</el-radio-button>
            <el-radio-button :label="2">已拒绝</el-radio-button>
          </el-radio-group>
          <el-input v-model="query" placeholder="搜索退费单号/订单ID" clearable size="small" class="search-input" @keyup.enter.native="loadData">
            <el-button slot="append" icon="el-icon-search" @click="loadData"></el-button>
          </el-input>
        </div>
        <div class="toolbar-right"><el-tag>共 {{ total }} 条</el-tag></div>
      </div>
      <el-table :data="refundData" stripe border style="width:100%;">
        <el-table-column label="编号" prop="rfId" width="70" align="center"></el-table-column>
        <el-table-column label="订单ID" prop="oId" width="75" align="center"></el-table-column>
        <el-table-column label="退费金额" width="90" align="center">
          <template slot-scope="s">¥{{ s.row.rfAmount || '0' }}</template>
        </el-table-column>
        <el-table-column label="退费原因" prop="rfReason" min-width="160" show-overflow-tooltip></el-table-column>
        <el-table-column label="申请人" prop="rfRequester" width="80" align="center"></el-table-column>
        <el-table-column label="申请时间" prop="rfCreateTime" width="155"></el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="s">
            <el-tag :type="s.row.rfStatus===0?'warning':s.row.rfStatus===1?'success':'info'" size="mini">
              {{ {0:'待审核',1:'已通过',2:'已拒绝'}[s.row.rfStatus] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template slot-scope="s">
            <el-button v-if="s.row.rfStatus===0" type="success" size="mini" @click="approveRefund(s.row)">通过</el-button>
            <el-button v-if="s.row.rfStatus===0" type="danger" size="mini" @click="rejectRefund(s.row)">拒绝</el-button>
            <el-tag v-else type="info" size="mini">已处理</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}"
        :current-page="pageNumber" :page-sizes="[10,20,30]" :page-size="size"
        layout="total,sizes,prev,pager,next,jumper" :total="total" style="margin-top:15px;">
      </el-pagination>
    </el-card>
  </div>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "RefundApproval",
  data() { return { pageNumber:1, size:10, total:0, refundData:[], query:'', statusTab:0 }; },
  methods: {
    loadData() {
      request.get("refundRequest/findAll",{params:{pageNumber:this.pageNumber,size:this.size,query:this.query,status:this.statusTab}})
        .then(r=>{const d=r.data.data;this.refundData=Array.isArray(d)?d:(d.records||[]);this.total=Array.isArray(d)?d.length:(d.total||0);});
    },
    switchTab() { this.pageNumber=1; this.loadData(); },
    approveRefund(row) { this.$confirm('确认通过？','审批',{type:'warning'}).then(()=>{request.post("refundRequest/approve",null,{params:{rfId:row.rfId,approver:'管理员'}}).then(r=>{if(r.data.status===200){this.$message.success('已批准');this.loadData();}else this.$message.error(r.data.msg);});}).catch(()=>{});},
    rejectRefund(row) { this.$prompt('请输入拒绝原因','拒绝',{inputValidator:v=>!!v||'请输入'}).then(({value})=>{request.post("refundRequest/reject",null,{params:{rfId:row.rfId,approver:'管理员',reason:value}}).then(r=>{if(r.data.status===200){this.$message.success('已拒绝');this.loadData();}else this.$message.error(r.data.msg);});}).catch(()=>{});}
  },
  created(){ this.loadData(); }
};
</script>

