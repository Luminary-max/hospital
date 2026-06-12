<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-document"></i> 我的处方</span>
    </div>
    <el-table :data="prescriptionList" border stripe style="width:100%">
      <el-table-column prop="o_id" label="订单编号" width="120"></el-table-column>
      <el-table-column prop="d_name" label="医生" width="120"></el-table-column>
      <el-table-column prop="o_start" label="就诊日期" width="180"></el-table-column>
      <el-table-column prop="o_drug" label="药品" width="200"></el-table-column>
      <el-table-column prop="o_total_price" label="总价(元)" width="120"></el-table-column>
      <el-table-column label="缴费状态" width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.o_price_state === 0" type="danger">未缴费</el-tag>
          <el-tag v-else type="success">已缴费</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="viewDetail(scope.row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div v-if="prescriptionList.length === 0" style="text-align:center; padding:40px 0; color:#999;">
      暂无处方记录
    </div>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "MyPrescription",
  data() {
    return { pId: null, prescriptionList: [] };
  },
  methods: {
    async loadData() {
      try {
        const res = await request.get("patient/findOrderByPid", { params: { pId: this.pId } });
        if (res.data.status === 200) {
          this.prescriptionList = (res.data.data || []).filter(o => o.o_drug || o.o_total_price > 0);
        }
      } catch(e) { console.error(e); }
    },
    viewDetail(row) {
      this.$alert(
        `<b>诊断：</b>${row.o_record || '无'}<br/>
         <b>药品：</b>${row.o_drug || '无'}<br/>
         <b>检查项目：</b>${row.o_check || '无'}<br/>
         <b>医生建议：</b>${row.o_advice || '无'}`,
        '处方详情',
        { dangerouslyUseHTMLString: true }
      );
    }
  },
  created() {
    const token = getToken();
    if (token) {
      const decoded = jwtDecode(token);
      this.pId = decoded.pId || decoded.sub;
    }
    this.loadData();
  }
};
</script>
