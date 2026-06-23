<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-office-building"></i> 门诊观察管理</span>
      <el-button type="primary" size="small" style="float:right;" @click="showAddDialog">
        <i class="el-icon-plus"></i> 新增观察床/输液椅
      </el-button>
    </div>
    <el-table :data="bedList" border stripe style="width:100%">
      <el-table-column prop="bId" label="编号" width="100"></el-table-column>
      <el-table-column label="类型" width="100">
        <template slot-scope="s">
          <el-tag v-if="s.row.bType === 0 || s.row.bType == null" type="primary">观察床</el-tag>
          <el-tag v-else type="success">输液椅</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="s">
          <el-tag v-if="s.row.bState === 0" type="success">空闲</el-tag>
          <el-tag v-else type="danger">占用</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="pName" label="患者" width="120"></el-table-column>
      <el-table-column prop="dName" label="主治医生" width="120"></el-table-column>
      <el-table-column prop="bStart" label="开始时间" width="180"></el-table-column>
      <el-table-column prop="bObsNote" label="观察记录" width="200" show-overflow-tooltip></el-table-column>
      <el-table-column label="操作" width="220">
        <template slot-scope="s">
          <el-button v-if="s.row.bState === 0" type="success" size="mini" @click="openEditDialog(s.row)">编辑</el-button>
          <el-button v-if="s.row.bState === 1" type="warning" size="mini" @click="discharge(s.row)">结束观察</el-button>
          <el-button type="danger" size="mini" @click="deleteBed(s.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}"
      :current-page="pageNumber" :page-sizes="[10,20,50]" :page-size="size"
      layout="total, sizes, prev, pager, next, jumper" :total="total" style="margin-top:20px;">
    </el-pagination>

    <el-dialog title="编辑床位" :visible.sync="editVisible" width="500px">
      <el-form :model="editForm" label-width="100px" size="small">
        <el-form-item label="编号"><el-input v-model="editForm.bId" disabled></el-input></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editForm.bType" style="width:100%">
            <el-option :label="0" value="0">观察床</el-option>
            <el-option :label="1" value="1">输液椅</el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="观察记录"><el-input type="textarea" v-model="editForm.bObsNote" :rows="3"></el-input></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="editVisible=false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "ObserveBedList",
  data() {
    return { bedList: [], pageNumber: 1, size: 10, total: 0, editVisible: false, editForm: {} };
  },
  methods: {
    async loadData() {
      try {
        const res = await request.get("bed/findAllBeds", { params: { pageNumber: this.pageNumber, size: this.size, query: "" } });
        if (res.data.status === 200) { this.bedList = res.data.data.beds || []; this.total = res.data.data.total || 0; }
      } catch(e) {}
    },
    handleSizeChange(val) { this.size = val; this.loadData(); },
    handleCurrentChange(val) { this.pageNumber = val; this.loadData(); },
    showAddDialog() { this.$router.push("/bedList"); },
    openEditDialog(row) {
      this.editForm = { bId: row.bId, bType: row.bType, bObsNote: row.bObsNote || '' };
      this.editVisible = true;
    },
    async saveEdit() {
      await request.get("bed/updateBed", { params: { bId: this.editForm.bId, bType: this.editForm.bType, bObsNote: this.editForm.bObsNote } });
      this.editVisible = false;
      this.$message.success("保存成功");
      this.loadData();
    },
    async discharge(row) {
      await request.get("bed/emptyBed", { params: { bId: row.bId } });
      this.$message.success("已结束观察");
      this.loadData();
    },
    async deleteBed(row) {
      this.$confirm("确认删除该床位? 此操作不可恢复。", "删除确认", { confirmButtonText:"确定删除", cancelButtonText:"取消", type:"warning" }).then(async () => {
        await request.get("bed/deleteBed", { params: { bId: row.bId } });
        this.$message.success("删除成功");
        this.loadData();
      }).catch(() => {});
    }
  },
  created() { this.loadData(); }
};
</script>



