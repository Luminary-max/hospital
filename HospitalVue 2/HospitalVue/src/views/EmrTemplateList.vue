<template>
  <el-card>
    <div slot="header"><i class="el-icon-document-copy"></i> 病历模板管理</div>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query" placeholder="模板名称/科室" clearable size="small" class="search-input" @keyup.enter.native="loadData">
          <el-button slot="append" icon="el-icon-search" @click="loadData"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
        <el-button type="primary" size="small" @click="openAddDialog"><i class="el-icon-plus"></i> 新增模板</el-button>
      </div>
    </div>
    <el-table :data="templateData" stripe border style="width:100%">
      <el-table-column label="编号"   prop="etId"   width="70"   align="center" ></el-table-column>
      <el-table-column label="模板名称"   prop="etName"   min-width="140" ></el-table-column>
      <el-table-column label="科室"   prop="etDept"   width="110" ></el-table-column>
      <el-table-column label="主诉"   prop="etChiefComplaint"   min-width="200"   show- overflow- tooltip></el-table-column>
      <el-table-column label="创建时间"   prop="etCreateTime"   width="160" ></el-table-column>
      <el-table-column label="操作"   width="180"   fixed="right"   align="center" >
        <template slot-scope="s">
          <el-button type="primary" size="mini" @click="useTemplate(s.row)">使用模板</el-button>
          <el-button type="success" size="mini" icon="el-icon-edit" circle @click="openEditDialog(s.row)" title="编辑"></el-button>
          <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteDialog(s.row.etId)" title="删除"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}" background
      layout="total,sizes,prev,pager,next,jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[10,20,50]" :total="total"></el-pagination>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="720px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px" size="small">
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="模板名称" prop="etName"><el-input v-model="form.etName"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="科室" prop="etDept"><el-input v-model="form.etDept"></el-input></el-form-item></el-col>
        </el-row>
        <el-form-item label="主诉" prop="etChiefComplaint"><el-input v-model="form.etChiefComplaint" type="textarea" :rows="2"></el-input></el-form-item>
        <el-form-item label="现病史" prop="etPresentIllness"><el-input v-model="form.etPresentIllness" type="textarea" :rows="2"></el-input></el-form-item>
        <el-form-item label="既往史" prop="etPastHistory"><el-input v-model="form.etPastHistory" type="textarea" :rows="2"></el-input></el-form-item>
        <el-form-item label="体格检查" prop="etPhysicalExam"><el-input v-model="form.etPhysicalExam" type="textarea" :rows="2"></el-input></el-form-item>
        <el-form-item label="诊断" prop="etDiagnosis"><el-input v-model="form.etDiagnosis" type="textarea" :rows="2"></el-input></el-form-item>
        <el-form-item label="治疗方案" prop="etTreatmentPlan"><el-input v-model="form.etTreatmentPlan" type="textarea" :rows="2"></el-input></el-form-item>
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
  name: "EmrTemplateList",
  data() {
    return {
      pageNumber:1, size:10, query:"", templateData:[], total:0,
      dialogVisible:false, isEdit:false, editingId:null,
      form:{
        etName:"", etDept:"", etChiefComplaint:"", etPresentIllness:"",
        etPastHistory:"", etPhysicalExam:"", etDiagnosis:"", etTreatmentPlan:""
      },
      rules:{
        etName:[{required:true,message:"请输入模板名称",trigger:"blur"}],
        etDept:[{required:true,message:"请输入科室",trigger:"blur"}]
      }
    };
  },
  computed: {
    dialogTitle(){ return this.isEdit ? "编辑病历模板" : "新增病历模板"; }
  },
  methods: {
    loadData() {
      request.get("emrTemplate/findAll",{params:{pageNumber:this.pageNumber,size:this.size,query:this.query}})
        .then(r=>{const d=r.data.data;this.templateData=Array.isArray(d)?d:(d.records||[]);this.total=Array.isArray(d)?d.length:(d.total||0);});
    },
    openAddDialog() {
      this.isEdit=false; this.editingId=null;
      this.form={ etName:"", etDept:"", etChiefComplaint:"", etPresentIllness:"", etPastHistory:"", etPhysicalExam:"", etDiagnosis:"", etTreatmentPlan:"" };
      this.dialogVisible=true; this.$nextTick(()=>{this.$refs.form&&this.$refs.form.clearValidate();});
    },
    openEditDialog(row) {
      this.isEdit=true; this.editingId=row.etId;
      this.form={
        etName:row.etName, etDept:row.etDept, etChiefComplaint:row.etChiefComplaint||"",
        etPresentIllness:row.etPresentIllness||"", etPastHistory:row.etPastHistory||"",
        etPhysicalExam:row.etPhysicalExam||"", etDiagnosis:row.etDiagnosis||"",
        etTreatmentPlan:row.etTreatmentPlan||""
      };
      this.dialogVisible=true; this.$nextTick(()=>{this.$refs.form&&this.$refs.form.clearValidate();});
    },
    submitForm() {
      this.$refs.form.validate(v=>{
        if(!v)return;
        const params={...this.form};
        if(this.isEdit) params.etId=this.editingId;
        const api=this.isEdit?"emrTemplate/modify":"emrTemplate/add";
        request.get(api,{params}).then(r=>{
          if(r.data.status!==200) return this.$message.error(r.data.msg||"操作失败");
          this.dialogVisible=false; this.loadData(); this.$message.success(this.isEdit?"修改成功":"新增成功");
        });
      });
    },
    useTemplate(row) {
      this.$emit("useTemplate", row);
      this.$message.success("已加载模板："+row.etName);
    },
    deleteDialog(id) {
      this.$confirm("确定删除该模板?","提示",{type:"warning"})
        .then(()=>{request.get("emrTemplate/delete",{params:{etId:id}}).then(r=>{if(r.data.status===200){this.$message.success("删除成功");this.loadData();}else this.$message.error(r.data.msg||"删除失败");});})
        .catch(()=>{});
    },
    handleSizeChange(s){this.size=s;this.loadData();},
    handleCurrentChange(p){this.pageNumber=p;this.loadData();}
  },
  created(){this.loadData();}
};
</script>











