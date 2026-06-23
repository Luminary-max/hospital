<template>
  <el-card>
    <div slot="header"><i class="el-icon-document"></i> 处方模板管理</div>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query" placeholder="模板名称/科室/诊断" clearable size="small" class="search-input" @keyup.enter.native="loadData">
          <el-button slot="append" icon="el-icon-search" @click="loadData"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
        <el-button type="primary" size="small" @click="openAddDialog"><i class="el-icon-plus"></i> 新增模板</el-button>
      </div>
    </div>
    <el-table :data="templateData" stripe border style="width:100%">
      <el-table-column label="编号"   prop="ptId"   width="70"   align="center" ></el-table-column>
      <el-table-column label="模板名称"   prop="ptName"   min-width="130" ></el-table-column>
      <el-table-column label="医生"   prop="dId"   width="70"   align="center" ></el-table-column>
      <el-table-column label="诊断"   prop="ptDiagnosis"   min-width="150"   show- overflow- tooltip></el-table-column>
      <el-table-column label="科室"   prop="ptDept"   width="100" ></el-table-column>
      <el-table-column label="处 方 内 容 预 览"   min-width="200"   show- overflow- tooltip>
        <template slot-scope="s">
          <span v-if="s.row.ptContent">{{ formatContentPreview(s.row.ptContent) }}</span>
          <span v-else class="no-data">--</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间"   prop="ptCreateTime"   width="160" ></el-table-column>
      <el-table-column label="操作"   width="130"   fixed="right"   align="center" >
        <template slot-scope="s">
          <el-button type="success" size="mini" icon="el-icon-edit" circle @click="openEditDialog(s.row)" title="编辑"></el-button>
          <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteDialog(s.row.ptId)" title="删除"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}" background
      layout="total,sizes,prev,pager,next,jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[10,20,50]" :total="total"></el-pagination>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="form" label-width="90px" size="small">
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="模板名称" prop="ptName"><el-input v-model="form.ptName"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="科室" prop="ptDept"><el-input v-model="form.ptDept"></el-input></el-form-item></el-col>
        </el-row>
        <el-form-item label="诊断" prop="ptDiagnosis"><el-input v-model="form.ptDiagnosis" type="textarea" :rows="2"></el-input></el-form-item>
        <el-form-item label="处方内容" prop="ptContent">
          <el-input v-model="form.ptContent" type="textarea" :rows="6" placeholder='JSON格式，如：[{"drId":1,"drName":"阿莫西林","dosage":"0.5g","frequency":"tid","days":3}]'></el-input>
          <span class="form-tip">请以JSON数组格式填写药品列表</span>
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
  name: "PrescriptionTemplateList",
  data() {
    return {
      pageNumber:1, size:10, query:"", templateData:[], total:0,
      dialogVisible:false, isEdit:false, editingId:null,
      form:{ ptName:"", ptDiagnosis:"", ptDept:"", ptContent:"" },
      rules:{
        ptName:[{required:true,message:"请输入模板名称",trigger:"blur"}],
        ptDept:[{required:true,message:"请输入科室",trigger:"blur"}]
      }
    };
  },
  computed: {
    dialogTitle(){ return this.isEdit ? "编辑处方模板" : "新增处方模板"; }
  },
  methods: {
    loadData() {
      request.get("prescriptionTemplate/findAll",{params:{pageNumber:this.pageNumber,size:this.size,query:this.query}})
        .then(r=>{const d=r.data.data;this.templateData=Array.isArray(d)?d:(d.records||[]);this.total=Array.isArray(d)?d.length:(d.total||0);});
    },
    openAddDialog() {
      this.isEdit=false; this.editingId=null;
      this.form={ ptName:"", ptDiagnosis:"", ptDept:"", ptContent:"" };
      this.dialogVisible=true; this.$nextTick(()=>{this.$refs.form&&this.$refs.form.clearValidate();});
    },
    openEditDialog(row) {
      this.isEdit=true; this.editingId=row.ptId;
      this.form={
        ptName:row.ptName, ptDiagnosis:row.ptDiagnosis||"",
        ptDept:row.ptDept, ptContent:typeof row.ptContent==="string"?row.ptContent:JSON.stringify(row.ptContent||"")
      };
      this.dialogVisible=true; this.$nextTick(()=>{this.$refs.form&&this.$refs.form.clearValidate();});
    },
    submitForm() {
      this.$refs.form.validate(v=>{
        if(!v)return;
        let content=this.form.ptContent;
        if(content && typeof content==="string"){ try{ content=JSON.parse(content); }catch(e){ return this.$message.error("处方内容JSON格式不正确，请检查"); } }
        const params={ ptName:this.form.ptName, ptDiagnosis:this.form.ptDiagnosis, ptDept:this.form.ptDept, ptContent:content };
        if(this.isEdit) params.ptId=this.editingId;
        const api=this.isEdit?"prescriptionTemplate/modify":"prescriptionTemplate/add";
        request.get(api,{params}).then(r=>{
          if(r.data.status!==200) return this.$message.error(r.data.msg||"操作失败");
          this.dialogVisible=false; this.loadData(); this.$message.success(this.isEdit?"修改成功":"新增成功");
        });
      });
    },
    formatContentPreview(content) {
      if(!content) return "--";
      let arr=content;
      if(typeof arr==="string"){ try{ arr=JSON.parse(arr); }catch(e){ return content; } }
      if(Array.isArray(arr)) return arr.map(i=>i.drName||"(未命名)").join("、");
      return content;
    },
    deleteDialog(id) {
      this.$confirm("确定删除该处方模板?","提示",{type:"warning"})
        .then(()=>{request.get("prescriptionTemplate/delete",{params:{ptId:id}}).then(r=>{if(r.data.status===200){this.$message.success("删除成功");this.loadData();}else this.$message.error(r.data.msg||"删除失败");});})
        .catch(()=>{});
    },
    handleSizeChange(s){this.size=s;this.loadData();},
    handleCurrentChange(p){this.pageNumber=p;this.loadData();}
  },
  created(){this.loadData();}
};
</script>











