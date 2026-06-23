<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-s-grid"></i> 药品分类管理</span>
      <el-button type="primary" size="small" style="float:right;" @click="openAddDialog">
        <i class="el-icon-plus"></i> 新增分类
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query" placeholder="分类名称/编码" size="small" class="search-input" clearable @keyup.enter.native="loadData">
          <el-button slot="append" icon="el-icon-search" @click="loadData"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
      </div>
    </div>

    <el-table :data="categoryData" border stripe style="width:100%">
      <el-table-column prop="dcId" label="编号" width="70" align="center"></el-table-column>
      <el-table-column prop="dcName" label="分类名称" min-width="150">
        <template slot-scope="s">
          <i class="el-icon-folder-opened" style="color:#E6A23C;margin-right:6px;"></i>
          {{ s.row.dcName }}
        </template>
      </el-table-column>
      <el-table-column prop="dcParentId" label="父级ID" width="75" align="center"></el-table-column>
      <el-table-column prop="dcCode" label="分类编码" width="120" align="center">
        <template slot-scope="s">
          <el-tag size="mini" type="info">{{ s.row.dcCode }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="dcNote" label="备注" min-width="200" show-overflow-tooltip></el-table-column>
      <el-table-column prop="dcSort" label="排序" width="65" align="center"></el-table-column>
      <el-table-column label="操作" width="150" fixed="right" align="center">
        <template slot-scope="s">
          <el-button type="success" size="mini" icon="el-icon-edit" circle @click="openEditDialog(s.row)" title="编辑"></el-button>
          <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteDialog(s.row.dcId)" title="删除"></el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}"
      :current-page="pageNumber" :page-sizes="[10,20,30]" :page-size="size"
      layout="total,sizes,prev,pager,next,jumper" :total="total" style="margin-top:15px;">
    </el-pagination>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="isEdit?'编辑药品分类':'新增药品分类'" :visible.sync="dialogVisible" width="520px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px" size="small">
        <el-form-item label="分类名称" prop="dcName">
          <el-input v-model="form.dcName" placeholder="如：抗生素、中成药"></el-input>
        </el-form-item>
        <el-form-item label="父级分类">
          <el-select v-model="form.dcParentId" placeholder="无（顶级分类）" clearable style="width:100%">
            <el-option label="无（顶级分类）" :value="null"></el-option>
            <el-option v-for="item in parentOptions" :key="item.dcId" :label="item.dcName" :value="item.dcId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分类编码" prop="dcCode">
          <el-input v-model="form.dcCode" placeholder="如：ANTI-01"></el-input>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.dcSort" :min="0" :max="999" style="width:100%"></el-input-number>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.dcNote" type="textarea" :rows="3" placeholder="分类描述或备注信息"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "DrugCategoryList",
  data() {
    return {
      pageNumber:1, size:10, total:0, query:'',
      categoryData:[], parentOptions:[],
      dialogVisible:false, isEdit:false,
      form:{ dcName:'', dcParentId:null, dcCode:'', dcNote:'', dcSort:0 },
      rules:{
        dcName:[{required:true,message:"请输入分类名称",trigger:"blur"}],
        dcCode:[{required:true,message:"请输入分类编码",trigger:"blur"}]
      }
    };
  },
  methods: {
    loadData() {
      request.get("drugCategory/findAll", {
        params: { pageNumber:this.pageNumber, size:this.size, query:this.query||null }
      }).then(res => {
        const d=res.data.data;
        this.categoryData=d.records||[];
        this.total=d.total||0;
      });
    },
    // 加载父级分类选项
    loadParentOptions() {
      // API: 加载所有分类供父级选择
      request.get("drugCategory/findAll", {
        params: { pageNumber:1, size:999 }
      }).then(res => {
        const d=res.data.data;
        this.parentOptions=d.records||[];
      });
    },
    openAddDialog() {
      this.isEdit=false;
      this.form={ dcName:'', dcParentId:null, dcCode:'', dcNote:'', dcSort:0 };
      this.dialogVisible=true;
      this.loadParentOptions();
      this.$nextTick(()=>{if(this.$refs.form)this.$refs.form.clearValidate();});
    },
    openEditDialog(row) {
      this.isEdit=true;
      this.form={ ...row };
      this.dialogVisible=true;
      this.loadParentOptions();
      this.$nextTick(()=>{if(this.$refs.form)this.$refs.form.clearValidate();});
    },
    submitForm() {
      this.$refs.form.validate(valid=>{
        if(!valid) return;
        // API: GET drugCategory/addDrugCategory 或 drugCategory/modifyDrugCategory
        const api = this.isEdit ? "drugCategory/modifyDrugCategory" : "drugCategory/addDrugCategory";
        request.get(api, { params: this.form }).then(res => {
          if(res.data.status===200){
            this.$message.success(this.isEdit?"修改成功":"新增成功");
            this.dialogVisible=false;
            this.loadData();
          } else {
            this.$message.error(res.data.msg||"操作失败");
          }
        });
      });
    },
    deleteDialog(id) {
      this.$confirm("确定删除该药品分类？删除后不可恢复。","删除确认",{
        confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"
      }).then(()=>{
        // API: GET drugCategory/deleteDrugCategory
        request.get("drugCategory/deleteDrugCategory", { params: { dcId:id } }).then(res => {
          if(res.data.status===200){
            this.$message.success("删除成功");
            this.loadData();
          } else {
            this.$message.error(res.data.msg||"删除失败");
          }
        });
      }).catch(()=>{});
    }
  },
  created() {
    this.loadData();
  }
};
</script>
<style scoped>
.toolbar { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px; }
.toolbar-left { display:flex; align-items:center; gap:8px; }
.toolbar-right { display:flex; align-items:center; gap:8px; }
.search-input { width:220px; }
.total-tag { margin-left:8px; }
</style>
