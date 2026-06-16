<template>
  <el-card>
    <div slot="header"><span><i class="el-icon-medicine"></i> 药房发药管理</span></div>
    <el-row style="margin-bottom:15px;">
      <el-col :span="20">
        <el-radio-group v-model="statusFilter" @change="loadData" size="small">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button :label="0">待发药</el-radio-button>
          <el-radio-button :label="1">已发药</el-radio-button>
        </el-radio-group>
      </el-col>
    </el-row>
    <el-table :data="dispensingData" border stripe style="width:100%">
      <el-table-column prop="pdId" label="编号" width="60"></el-table-column>
      <el-table-column prop="oId" label="订单ID" width="80"></el-table-column>
      <el-table-column prop="drId" label="药品ID" width="80"></el-table-column>
      <el-table-column prop="pdQuantity" label="数量" width="60"></el-table-column>
      <el-table-column label="状态" width="80">
        <template slot-scope="s">
          <el-tag v-if="s.row.pdStatus===0" type="danger">待发药</el-tag>
          <el-tag v-else type="success">已发药</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="pdCreateTime" label="创建时间" width="160"></el-table-column>
      <el-table-column prop="pdDispenseTime" label="发药时间" width="160"></el-table-column>
      <el-table-column prop="pdDispenseBy" label="发药人" width="80"></el-table-column>
      <el-table-column prop="pdNote" label="备注" min-width="160"></el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template slot-scope="s">
          <el-button type="success" size="mini" v-if="s.row.pdStatus===0" @click="dispenseDrug(s.row.pdId)">确认发药</el-button>
          <el-tag type="info" v-else size="mini">已完成</el-tag>
        </template>
      </el-table-column>
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
  name: "PharmacyDispensingList",
  data() {
    return { pageNumber:1, size:10, statusFilter:'', dispensingData:[], total:0 };
  },
  methods: {
    loadData() {
      request.get("pharmacy/findAll", { params: { pageNumber:this.pageNumber, size:this.size, status:this.statusFilter===''?null:this.statusFilter } })
        .then(res => { const d=res.data.data; this.dispensingData=d.records||[]; this.total=d.total||0; });
    },
    dispenseDrug(pdId) {
      this.$prompt("请输入发药人姓名", "确认发药", { inputPlaceholder:"药剂师姓名" }).then(({ value })=> {
        if (!value) return this.$message.warning("请输入发药人");
        request.get("pharmacy/dispense", { params: { pdId, dispenseBy:value } }).then(res => {
          if (res.data.status===200) { this.$message.success("发药成功！"); this.loadData(); }
          else this.$message.error("发药失败");
        });
      }).catch(()=>{});
    }
  },
  created() { this.loadData(); }
};
</script>
