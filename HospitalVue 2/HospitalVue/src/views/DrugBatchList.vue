<template>
  <el-card>
    <div slot="header"><span><i class="el-icon-box"></i> 药品批次管理</span></div>
    <el-table :data="batchData" border stripe style="width:100%">
      <el-table-column prop="dbId" label="编号" width="60"></el-table-column>
      <el-table-column prop="drId" label="药品ID" width="80"></el-table-column>
      <el-table-column prop="dbBatchNo" label="批号" width="130"></el-table-column>
      <el-table-column prop="dbExpireDate" label="有效期" width="100"></el-table-column>
      <el-table-column label="有效期状态" width="100">
        <template slot-scope="s">
          <el-tag v-if="s.row.dbExpireDate && new Date(s.row.dbExpireDate) > new Date(Date.now()+90*86400000)" type="success" size="mini">正常</el-tag>
          <el-tag v-else-if="s.row.dbExpireDate && new Date(s.row.dbExpireDate) > new Date()" type="warning" size="mini">即将过期</el-tag>
          <el-tag v-else type="danger" size="mini">已过期</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="dbQuantity" label="批次库存" width="80"></el-table-column>
      <el-table-column prop="dbPurchasePrice" label="进货价" width="80">
        <template slot-scope="s">¥{{ s.row.dbPurchasePrice }}</template>
      </el-table-column>
      <el-table-column prop="dbSupplier" label="供应商" min-width="120"></el-table-column>
      <el-table-column prop="dbCreateTime" label="入库时间" width="160"></el-table-column>
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
  name: "DrugBatchList",
  data() { return { pageNumber:1, size:10, batchData:[], total:0 }; },
  methods: {
    loadData() {
      request.get("drugBatch/findAll", { params: { pageNumber:this.pageNumber, size:this.size } })
        .then(res => { const d=res.data.data; this.batchData=d.records||[]; this.total=d.total||0; });
    }
  },
  created() { this.loadData(); }
};
</script>
