<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-document"></i> 处方管理</span>
    </div>
    <el-table :data="prescriptionData" border stripe style="width:100%">
      <el-table-column prop="o_id" label="订单编号" width="120"></el-table-column>
      <el-table-column prop="p_name" label="患者姓名" width="120"></el-table-column>
      <el-table-column prop="o_start" label="就诊日期" width="180"></el-table-column>
      <el-table-column prop="o_drug" label="药品" width="200"></el-table-column>
      <el-table-column prop="o_total_price" label="总价(元)" width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.o_total_price">¥{{ scope.row.o_total_price }}</span>
          <span v-else>---</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.o_price_state === 0" type="danger">未缴费</el-tag>
          <el-tag v-else type="success">已缴费</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="viewDetail(scope.row)">查看详情</el-button>
          <el-button type="success" size="mini" @click="printPrescription(scope.row)">打印</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNumber"
      :page-sizes="[10, 20, 30]"
      :page-size="size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      style="margin-top:20px;">
    </el-pagination>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "PrescriptionList",
  data() {
    return {
      dId: "",
      prescriptionData: [],
      pageNumber: 1,
      size: 10,
      total: 0
    };
  },
  methods: {
    async loadData() {
      try {
        const res = await request.get("order/findOrderFinish", {
          params: { dId: this.dId, pageNumber: this.pageNumber, size: this.size }
        });
        if (res.data.status === 200) {
          this.prescriptionData = res.data.data.records || [];
          this.total = res.data.data.total || 0;
        }
      } catch(e) { console.error(e); }
    },
    handleSizeChange(val) { this.size = val; this.loadData(); },
    handleCurrentChange(val) { this.pageNumber = val; this.loadData(); },
    viewDetail(row) {
      this.$alert(
        `<b>诊断记录：</b>${row.o_record || '无'}<br/>
         <b>药品：</b>${row.o_drug || '无'}<br/>
         <b>检查：</b>${row.o_check || '无'}<br/>
         <b>医生建议：</b>${row.o_advice || '无'}<br/>
         <b>总价：</b>¥${row.o_total_price || 0}`,
        '处方详情 - 订单#' + row.o_id,
        { dangerouslyUseHTMLString: true }
      );
    },
    printPrescription(row) {
      this.$message.info("打印功能：正在生成处方单...");
    }
  },
  created() {
    const token = getToken();
    if (token) {
      const decoded = jwtDecode(token);
      this.dId = decoded.dId || decoded.sub;
    }
    this.loadData();
  }
};
</script>
