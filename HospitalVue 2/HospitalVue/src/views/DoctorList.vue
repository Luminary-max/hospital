<template>
  <el-card>
    <div slot="header"><i class="el-icon-user"></i> 医生信息管理</div>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query" placeholder="搜索医生姓名" clearable size="small" class="search-input" @keyup.enter.native="requestDoctors">
          <el-button slot="append" icon="el-icon-search" @click="requestDoctors"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
        <el-upload action="doctor/uploadExcel" accept=".xlsx,.xls" :limit="1" :show-file-list="false" :on-success="handleSuccess" :on-error="handleError" style="display:inline-block;margin-right:6px;">
          <el-button size="small" type="success">导入</el-button>
        </el-upload>
        <el-button size="small" type="success" @click="exportDoctors">导出</el-button>
        <el-button type="primary" size="small" @click="addFormVisible = true"><i class="el-icon-plus"></i> 增加医生</el-button>
      </div>
    </div>
    <el-table :data="doctorData" stripe border style="width:100%">
      <el-table-column label="账号" prop="dId" width="80"></el-table-column>
      <el-table-column label="姓名" prop="dName" width="80"></el-table-column>
      <el-table-column label="性别" prop="dGender" width="55"></el-table-column>
      <el-table-column label="职位" prop="dPost" width="100"></el-table-column>
      <el-table-column label="科室" prop="dSection" width="100"></el-table-column>
      <el-table-column label="证件号" prop="dCard" width="150"></el-table-column>
      <el-table-column label="手机号" prop="dPhone" width="110"></el-table-column>
      <el-table-column label="邮箱" prop="dEmail" width="160"></el-table-column>
      <el-table-column label="评分" prop="dAvgStar" width="70" align="center"></el-table-column>
      <el-table-column label="挂号费" prop="dPrice" width="70" align="center"></el-table-column>
      <el-table-column label="日限额" prop="dMaxDaily" width="65" align="center"></el-table-column>
      <el-table-column label="状态" width="65"><template slot-scope="s"><el-tag :type="s.row.dState===1?'success':'danger'" size="mini">{{ s.row.dState===1?'在职':'离职' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="95" fixed="right" align="center">
        <template slot-scope="s">
          <el-button type="success" size="mini" icon="el-icon-edit" circle @click="modifyDialog(s.row.dId)" title="编辑"></el-button>
          <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteDialog(s.row.dId)" title="删除"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" background
      layout="total,sizes,prev,pager,next,jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[5,10,20,50]" :total="total"></el-pagination>

    <el-dialog title="增加医生" :visible.sync="addFormVisible" width="520px">
      <el-form :model="addForm" :rules="rules" ref="addForm" label-width="80px" size="small">
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="账号" prop="dId"><el-input v-model.number="addForm.dId"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="姓名" prop="dName"><el-input v-model="addForm.dName"></el-input></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="性别"><el-radio v-model="addForm.dGender" label="男">男</el-radio><el-radio v-model="addForm.dGender" label="女">女</el-radio></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="密码"><el-input v-model="addForm.dPassword" disabled></el-input></el-form-item></el-col>
        </el-row>
        <el-form-item label="职位" prop="dPost"><el-select v-model="addForm.dPost" style="width:100%"><el-option v-for="p in posts" :key="p" :label="p" :value="p"></el-option></el-select></el-form-item>
        <el-form-item label="科室" prop="dSection"><el-select v-model="addForm.dSection" filterable style="width:100%"><el-option v-for="s in sections" :key="s" :label="s" :value="s"></el-option></el-select></el-form-item>
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="身份证号" prop="dCard"><el-input v-model="addForm.dCard"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="手机号" prop="dPhone"><el-input v-model="addForm.dPhone"></el-input></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="邮箱" prop="dEmail"><el-input v-model="addForm.dEmail"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="挂号费" prop="dPrice"><el-input v-model="addForm.dPrice"></el-input></el-form-item></el-col>
        </el-row>
        <el-form-item label="简介"><el-input type="textarea" :rows="3" v-model="addForm.dIntroduction"></el-input></el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="addFormVisible=false">取消</el-button><el-button type="primary" @click="addDoctor('addForm')">确定</el-button></div>
    </el-dialog>

    <el-dialog title="修改医生信息" :visible.sync="modifyFormVisible" width="520px">
      <el-form :model="modifyForm" :rules="rules" ref="modifyForm" label-width="80px" size="small">
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="账号"><el-input v-model.number="modifyForm.dId" disabled></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="姓名" prop="dName"><el-input v-model="modifyForm.dName"></el-input></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="性别"><el-radio v-model="modifyForm.dGender" label="男">男</el-radio><el-radio v-model="modifyForm.dGender" label="女">女</el-radio></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-input v-model="modifyForm.dState" disabled></el-input></el-form-item></el-col>
        </el-row>
        <el-form-item label="职位" prop="dPost"><el-select v-model="modifyForm.dPost" style="width:100%"><el-option v-for="p in posts" :key="p" :label="p" :value="p"></el-option></el-select></el-form-item>
        <el-form-item label="科室" prop="dSection"><el-select v-model="modifyForm.dSection" filterable style="width:100%"><el-option v-for="s in sections" :key="s" :label="s" :value="s"></el-option></el-select></el-form-item>
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="身份证号" prop="dCard"><el-input v-model="modifyForm.dCard"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="手机号" prop="dPhone"><el-input v-model="modifyForm.dPhone"></el-input></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="12"><el-form-item label="邮箱" prop="dEmail"><el-input v-model="modifyForm.dEmail"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="挂号费" prop="dPrice"><el-input v-model="modifyForm.dPrice"></el-input></el-form-item></el-col>
        </el-row>
        <el-form-item label="简介"><el-input type="textarea" :rows="3" v-model="modifyForm.dIntroduction"></el-input></el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="modifyFormVisible=false">取消</el-button><el-button type="primary" @click="modifyDoctor('modifyForm')">确定</el-button></div>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "DoctorList",
  data() {
    var validateMobile = (rule, v, cb) => { if (!v) { cb(new Error("请输入手机号")); } else if (!/^1(3[0-9]|4[5,7]|5[0,1,2,3,5,6,7,8,9]|6[2,5,6,7]|7[0,1,7,8]|8[0-9]|9[1,8,9])\d{8}$/.test(v)) cb(new Error("手机号格式错误")); else cb(); };
    var validateCard = (rule, v, cb) => { if (!v) { cb(new Error("请输入身份证号")); } else if (!/(^\d{17}(\d|X|x)$)/.test(v)) cb(new Error("身份证号格式错误")); else cb(); };
    return {
      pageNumber:1, size:10, query:"", doctorData:[], total:0,
      addFormVisible:false, addForm:{dPassword:123456,dGender:"男"},
      posts:["主任医师","副主任医师","主治医生"],
      sections:["神经内科","内分泌科","呼吸与危重症医学科","消化内科","心血管内科","发热门诊","手足外科","普通外科","肛肠外科","神经外科","骨科","烧伤整形外科","妇科","产科","儿科","耳鼻咽喉科","眼科","中医科","急诊科","皮肤病科","口腔科"],
      rules: {
        dId:[{required:true,message:"请输入账号",trigger:"blur"}],
        dName:[{required:true,message:"请输入姓名",trigger:"blur"},{min:2,max:5,message:"2-5个字符",trigger:"blur"}],
        dPost:[{required:true,message:"请选择职位",trigger:"change"}],
        dSection:[{required:true,message:"请选择科室",trigger:"change"}],
        dEmail:[{required:true,message:"请输入邮箱",trigger:"blur"},{type:"email",message:"邮箱格式错误",trigger:"blur"}],
        dPrice:[{required:true,message:"请输入挂号费",trigger:"blur"}],
        dPhone:[{validator:validateMobile}], dCard:[{validator:validateCard}],
        dIntroduction:[{required:true,message:"请输入简介",trigger:"blur"}]
      },
      modifyFormVisible:false, modifyForm:{}
    };
  },
  methods: {
    exportDoctors() { window.location.href = "http://localhost:9999/doctor/downloadExcel"; },
    handleSuccess() { this.$message.success("数据导入成功"); this.requestDoctors(); },
    handleError() { this.$message.success("数据导入成功"); this.requestDoctors(); },
    requestDoctors() { request.get("admin/findAllDoctors",{params:{pageNumber:this.pageNumber,size:this.size,query:this.query}}).then(r=>{this.doctorData=r.data.data.doctors||[];this.total=r.data.data.total||0;}); },
    addDoctor(fn) { this.$refs[fn].validate(v=>{if(!v)return;request.get("admin/addDoctor",{params:this.addForm}).then(r=>{if(r.data.status!==200)return this.$message.error("账号已占用");this.addFormVisible=false;this.requestDoctors();this.$message.success("增加成功");});}); },
    modifyDialog(id) { request.get("admin/findDoctor",{params:{dId:id}}).then(r=>{this.modifyForm=r.data.data;this.modifyFormVisible=true;}); },
    modifyDoctor(fn){this.$refs[fn].validate(v=>{if(!v)return;request.get("admin/modifyDoctor",{params:{dId:this.modifyForm.dId,dGender:this.modifyForm.dGender,dName:this.modifyForm.dName,dPost:this.modifyForm.dPost,dSection:this.modifyForm.dSection,dPhone:this.modifyForm.dPhone,dEmail:this.modifyForm.dEmail,dCard:this.modifyForm.dCard,dPrice:this.modifyForm.dPrice,dIntroduction:this.modifyForm.dIntroduction}}).then(r=>{this.modifyFormVisible=false;this.requestDoctors();this.$message.success("修改成功");});});},
    deleteDoctor(id){request.get("admin/deleteDoctor",{params:{dId:id}}).then(()=>this.requestDoctors());},
    deleteDialog(id){this.$confirm("确定删除?","提示",{type:"warning"}).then(()=>{this.deleteDoctor(id);this.$message.success("删除成功");}).catch(()=>{});},
    handleSizeChange(s){this.size=s;this.requestDoctors();},
    handleCurrentChange(p){this.pageNumber=p;this.requestDoctors();}
  },
  created(){this.requestDoctors();}
};
</script>
