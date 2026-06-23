<template>
  <el-card>
    <div slot="header"><i class="el-icon-document"></i> 发票管理</div>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-select v-model="statusFilter" placeholder="发票状态" size="small" class="filter-select" @change="loadData" clearable>
          <el-option label="全部" value=""></el-option>
          <el-option label="正常" value="0"></el-option>
          <el-option label="已作废" value="1"></el-option>
          <el-option label="已红冲" value="2"></el-option>
        </el-select>
        <el-input v-model="query" placeholder="发票号/订单ID" clearable size="small" class="search-input" @keyup.enter.native="loadData">
          <el-button slot="append" icon="el-icon-search" @click="loadData"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
      </div>
    </div>
    <el-table :data="invoiceData"stripe  border style="width:100%">
      <el-table-column label="编号"   prop="invId"   width="70"   align="center" ></el-table-column>
      <el-table-column label="发票号"   prop="invNo"   min-width="160" ></el-table-column>
      <el-table-column label="关联订单"   prop="oId"   width="80"   align="center" ></el-table-column>
      <el-table-column label="发票类型"   prop="invType"   width="90"   align="center" ></el-table-column>
      <el-table-column label="金额"   width="90"   align="center" >
        <template slot-scope="s">¥{{ s.row.invAmount || '0' }}</template>
      </el-table-column>
      <el-table-column label="状态"   width="80"   align="center" >
        <template slot-scope="s">
          <el-tag v-if="s.row.invStatus===0||s.row.invStatus==='0'" type="success" size="mini">正常</el-tag>
          <el-tag v-else-if="s.row.invStatus===1||s.row.invStatus==='1'" type="danger" size="mini">已作废</el-tag>
          <el-tag v-else type="warning" size="mini">已红冲</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作人"   prop="invOperator"   width="90"   align="center" ></el-table-column>
      <el-table-column label="开票时间"   prop="invCreateTime"   width="160" ></el-table-column>
      <el-table-column label="操作"   width="120"   fixed="right"   align="center" >
        <template slot-scope="s">
          <el-button v-if="s.row.invStatus===0||s.row.invStatus==='0'" type="danger" size="mini" @click="voidDialog(s.row)">作废</el-button>
          <el-tag v-else type="info" size="mini">已处理</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}" background
      layout="total,sizes,prev,pager,next,jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[10,20,50]" :total="total"></el-pagination>

    <el-dialog title="发票作废" :visible.sync="voidDlgVisible" width="420px">
      <el-form :model="voidForm" label-width="80px" size="small">
        <el-form-item label="发票号"><el-tag>{{ voidForm.invNo }}</el-tag></el-form-item>
        <el-form-item label="金额">¥{{ voidForm.invAmount }}</el-form-item>
        <el-form-item label="作废原因" prop="reason"><el-input v-model="voidForm.reason" type="textarea" :rows="3" placeholder="请输入作废原因"></el-input></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="voidDlgVisible=false">取消</el-button>
        <el-button type="danger" @click="doVoid">确认作废</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "InvoiceManage",
  data() {
    return {
      pageNumber:1, size:10, query:"", statusFilter:"", invoiceData:[], total:0,
      voidDlgVisible:false, voidForm:{ invNo:"", invAmount:0, reason:"" }, voidingId:null
    };
  },
  methods: {
    loadData() {
      request.get("invoiceRecord/findByDate",{params:{date:""}})
        .then(r=>{const d=r.data.data;this.invoiceData=Array.isArray(d)?d:(d.records||[]);this.total=Array.isArray(d)?d.length:(d.total||0);});
    },
    voidDialog(row) {
      this.voidingId=row.invId;
      this.voidForm={ invNo:row.invNo, invAmount:row.invAmount, reason:"" };
      this.voidDlgVisible=true;
    },
    doVoid() {
      if(!this.voidForm.reason) return this.$message.warning("请输入作废原因");
      request.get("invoiceRecord/void",{params:{invId:this.voidingId, reason:this.voidForm.reason}}).then(r=>{
        if(r.data.status===200){ this.$message.success("发票已作废"); this.voidDlgVisible=false; this.loadData(); }
        else this.$message.error(r.data.msg||"操作失败");
      });
    },
    handleSizeChange(s){this.size=s;this.loadData();},
    handleCurrentChange(p){this.pageNumber=p;this.loadData();}
  },
  created(){this.loadData();}
};
</script>









