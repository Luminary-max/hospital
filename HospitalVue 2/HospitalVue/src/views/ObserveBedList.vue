<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-office-building"></i> 门诊观察管理</span>
      <el-button type="primary" size="small" style="float:right;" @click="showAddDialog">
        <i class="el-icon-plus"></i> 新增观察床
      </el-button>
    </div>
    <el-table :data="bedList" border stripe style="width:100%">
      <el-table-column prop="b_id" label="编号" width="100"></el-table-column>
      <el-table-column prop="b_type" label="类型" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.b_type === 0 || !scope.row.b_type" type="primary">观察床</el-tag>
          <el-tag v-else type="success">输液椅</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="b_state" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.b_state === 0" type="success">空闲</el-tag>
          <el-tag v-else type="danger">占用</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="p_name" label="患者" width="120"></el-table-column>
      <el-table-column prop="d_name" label="主治医生" width="120"></el-table-column>
      <el-table-column prop="b_start" label="观察开始" width="180"></el-table-column>
      <el-table-column prop="b_obs_note" label="观察记录" width="200" show-overflow-tooltip></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button v-if="scope.row.b_state === 0" type="success" size="mini" @click="editBed(scope.row)">编辑</el-button>
          <el-button v-if="scope.row.b_state === 1" type="warning" size="mini" @click="discharge(scope.row)">结束观察</el-button>
          <el-button type="danger" size="mini" @click="deleteBed(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNumber"
      :page-sizes="[10, 20, 50]"
      :page-size="size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      style="margin-top:20px;">
    </el-pagination>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "ObserveBedList",
  data() {
    return {
      bedList: [],
      pageNumber: 1,
      size: 10,
      total: 0
    };
  },
  methods: {
    async loadData() {
      try {
        const res = await request.get("bed/findAllBeds", {
          params: { pageNumber: this.pageNumber, size: this.size }
        });
        if (res.data.status === 200) {
          this.bedList = res.data.data.records || [];
          this.total = res.data.data.total || 0;
        }
      } catch(e) { console.error(e); }
    },
    handleSizeChange(val) { this.size = val; this.loadData(); },
    handleCurrentChange(val) { this.pageNumber = val; this.loadData(); },
    showAddDialog() { this.$message.info("请通过管理员功能添加观察床位"); },
    editBed(row) { this.$message.info("编辑床位 - " + row.b_id); },
    discharge(row) { this.$message.info("结束观察 - " + row.b_id); },
    deleteBed(row) { this.$message.info("删除床位 - " + row.b_id); }
  },
  created() { this.loadData(); }
};
</script>
