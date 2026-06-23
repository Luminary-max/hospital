<template>
  <el-card>
    <div slot="header"><i class="el-icon-document"></i> 诊断词库管理</div>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query" placeholder="诊断名称/拼音码" clearable size="small" class="search-input" @keyup.enter.native="loadData">
          <el-button slot="append" icon="el-icon-search" @click="loadData"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
        <el-button type="primary" size="small" @click="openAddDialog"><i class="el-icon-plus"></i> 增加诊断</el-button>
      </div>
    </div>
    <el-table :data="dictData" stripe border style="width:100%">
      <el-table-column label="编号" prop="ddId" width="80" align="center"></el-table-column>
      <el-table-column label="诊断编码" prop="ddCode" width="120"></el-table-column>
      <el-table-column label="诊断名称" prop="ddName" min-width="200" show-overflow-tooltip></el-table-column>
      <el-table-column label="所属科室" prop="ddDept" width="120"></el-table-column>
      <el-table-column label="拼音码" prop="ddPinyin" width="100"></el-table-column>
      <el-table-column label="排序" prop="ddSort" width="70" align="center"></el-table-column>
      <el-table-column label="操作" width="130" fixed="right" align="center">
        <template slot-scope="s">
          <el-button type="success" size="mini" icon="el-icon-edit" circle @click="openEditDialog(s.row)" title="编辑"></el-button>
          <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteDialog(s.row.ddId)" title="删除"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}" background
      layout="total,sizes,prev,pager,next,jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[10,20,50]" :total="total"></el-pagination>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="520px">
      <el-form :model="form" :rules="rules" ref="form" label-width="90px" size="small">
        <el-form-item label="诊断编码" prop="ddCode"><el-input v-model="form.ddCode" placeholder="如 ICD-10 编码"></el-input></el-form-item>
        <el-form-item label="诊断名称" prop="ddName"><el-input v-model="form.ddName"></el-input></el-form-item>
        <el-form-item label="所属科室" prop="ddDept"><el-input v-model="form.ddDept" placeholder="关联科室名称"></el-input></el-form-item>
        <el-form-item label="拼音码" prop="ddPinyin"><el-input v-model="form.ddPinyin" placeholder="如 GZJY"></el-input></el-form-item>
        <el-form-item label="排序" prop="ddSort"><el-input-number v-model="form.ddSort" :min="0" style="width:100%"></el-input-number></el-form-item>
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
  name: "DiagnosisDictList",
  data() {
    return {
      pageNumber:1, size:10, query:"", dictData:[], total:0,
      dialogVisible:false, isEdit:false, editingId:null,
      form:{ ddCode:"", ddName:"", ddDept:"", ddPinyin:"", ddSort:0 },
      rules:{
        ddCode:[{required:true,message:"请输入诊断编码",trigger:"blur"}],
        ddName:[{required:true,message:"请输入诊断名称",trigger:"blur"}]
      }
    };
  },
  computed: {
    dialogTitle(){ return this.isEdit ? "编辑诊断" : "增加诊断"; }
  },
  methods: {
    loadData() {
      request.get("diagnosisDict/findAll",{params:{pageNumber:this.pageNumber,size:this.size,query:this.query}})
        .then(r=>{const d=r.data.data;this.dictData=Array.isArray(d)?d:(d.records||[]);this.total=Array.isArray(d)?d.length:(d.total||0);});
    },
    openAddDialog() {
      this.isEdit=false; this.editingId=null;
      this.form={ ddCode:"", ddName:"", ddDept:"", ddPinyin:"", ddSort:0 };
      this.dialogVisible=true; this.$nextTick(()=>{this.$refs.form&&this.$refs.form.clearValidate();});
    },
    openEditDialog(row) {
      this.isEdit=true; this.editingId=row.ddId;
      this.form={ ddCode:row.ddCode, ddName:row.ddName, ddDept:row.ddDept, ddPinyin:row.ddPinyin, ddSort:row.ddSort };
      this.dialogVisible=true; this.$nextTick(()=>{this.$refs.form&&this.$refs.form.clearValidate();});
    },
    submitForm() {
      this.$refs.form.validate(v=>{
        if(!v)return;
        const params={...this.form};
        if(this.isEdit) params.ddId=this.editingId;
        const api=this.isEdit?"diagnosisDict/modify":"diagnosisDict/add";
        request.get(api,{params}).then(r=>{
          if(r.data.status!==200) return this.$message.error(r.data.msg||"操作失败");
          this.dialogVisible=false; this.loadData(); this.$message.success(this.isEdit?"修改成功":"增加成功");
        });
      });
    },
    deleteDialog(id) {
      this.$confirm("确定删除该诊断?","提示",{type:"warning"})
        .then(()=>{request.get("diagnosisDict/delete",{params:{ddId:id}}).then(r=>{if(r.data.status===200){this.$message.success("删除成功");this.loadData();}else this.$message.error(r.data.msg||"删除失败");});})
        .catch(()=>{});
    },
    handleSizeChange(s){this.size=s;this.loadData();},
    handleCurrentChange(p){this.pageNumber=p;this.loadData();}
  },
  created(){this.loadData();}
};
</script>

