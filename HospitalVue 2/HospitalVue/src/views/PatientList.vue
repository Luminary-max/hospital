<template>
  <el-card>
    <div slot="header"><i class="el-icon-user-solid"></i> 患者信息管理</div>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query" placeholder="搜索患者姓名" clearable size="small" class="search-input" @keyup.enter.native="requestPatients">
          <el-button slot="append" icon="el-icon-search" @click="requestPatients"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
        <el-button size="small" type="success" @click="exportExcel">导出</el-button>
        <el-button type="primary" size="small" @click="addFormVisible = true"><i class="el-icon-plus"></i> 增加患者</el-button>
      </div>
    </div>
    <el-table :data="patientData" stripe border>
      <el-table-column label="账号" prop="pId" width="70"></el-table-column>
      <el-table-column label="姓名" prop="pName" width="80"></el-table-column>
      <el-table-column label="性别" prop="pGender" width="55"></el-table-column>
      <el-table-column label="年龄" prop="pAge" width="55"></el-table-column>
      <el-table-column label="证件号" prop="pCard" width="150"></el-table-column>
      <el-table-column label="手机号" prop="pPhone" width="110"></el-table-column>
      <el-table-column label="医保号" prop="pInsuranceId" width="120"></el-table-column>
      <el-table-column label="医保类型" prop="pInsuranceType" width="80"></el-table-column>
      <el-table-column label="民族" prop="pNation" width="55"></el-table-column>
      <el-table-column label="婚姻" prop="pMaritalStatus" width="55"></el-table-column>
      <el-table-column label="血型" prop="pBloodType" width="55"></el-table-column>
      <el-table-column label="地址" prop="pAddress" min-width="160" show-overflow-tooltip></el-table-column>
      <el-table-column label="状态" width="65"><template slot-scope="s"><el-tag :type="s.row.pState===1?'success':'danger'" size="mini">{{ s.row.pState===1?'正常':'删除' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="95" fixed="right" align="center">
        <template slot-scope="s">
          <el-button type="success" size="mini" icon="el-icon-edit" circle @click="modifyDialog(s.row.pId)" title="编辑"></el-button>
          <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteDialog(s.row.pId)" title="删除"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="s=>{size=s;requestPatients()}" @current-change="p=>{pageNumber=p;requestPatients()}" background
      layout="total,sizes,prev,pager,next,jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[5,10,20,50]" :total="total"></el-pagination>

    <el-dialog title="增加患者" :visible.sync="addFormVisible" width="650px">
      <el-form :model="addForm" :rules="rules" ref="addForm" label-width="80px" size="small">
        <el-row :gutter="15">
          <el-col :span="8"><el-form-item label="账号" prop="pId"><el-input v-model.number="addForm.pId"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="姓名" prop="pName"><el-input v-model="addForm.pName"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="性别"><el-select v-model="addForm.pGender" style="width:100%"><el-option label="男" value="男"></el-option><el-option label="女" value="女"></el-option></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="8"><el-form-item label="出生日期"><el-input v-model="addForm.pBirthday" placeholder="1990-01-01"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="证件号"><el-input v-model="addForm.pCard"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="手机号" prop="pPhone"><el-input v-model="addForm.pPhone"></el-input></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="8"><el-form-item label="邮箱"><el-input v-model="addForm.pEmail"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="医保号"><el-input v-model="addForm.pInsuranceId"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="医保类型"><el-select v-model="addForm.pInsuranceType" style="width:100%"><el-option label="城镇职工" value="城镇职工"></el-option><el-option label="城乡居民" value="城乡居民"></el-option><el-option label="自费" value="自费"></el-option></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="8"><el-form-item label="民族"><el-select v-model="addForm.pNation" style="width:100%"><el-option label="汉族" value="汉族"></el-option><el-option label="蒙古族" value="蒙古族"></el-option><el-option label="回族" value="回族"></el-option><el-option label="藏族" value="藏族"></el-option><el-option label="维吾尔族" value="维吾尔族"></el-option><el-option label="苗族" value="苗族"></el-option></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="婚姻"><el-select v-model="addForm.pMaritalStatus" style="width:100%"><el-option label="未婚" value="未婚"></el-option><el-option label="已婚" value="已婚"></el-option><el-option label="离异" value="离异"></el-option><el-option label="丧偶" value="丧偶"></el-option></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="血型"><el-select v-model="addForm.pBloodType" style="width:100%"><el-option label="A型" value="A"></el-option><el-option label="B型" value="B"></el-option><el-option label="AB型" value="AB"></el-option><el-option label="O型" value="O"></el-option></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="8"><el-form-item label="联系人"><el-input v-model="addForm.pContactPerson"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="联系人电话"><el-input v-model="addForm.pContactPhone"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="住址"><el-input v-model="addForm.pAddress"></el-input></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer"><el-button @click="addFormVisible=false">取消</el-button><el-button type="primary" @click="addPatient('addForm')">确定</el-button></div>
    </el-dialog>

    <el-dialog title="修改患者信息" :visible.sync="modifyFormVisible" width="650px">
      <el-form :model="modifyForm" :rules="rules" ref="modifyForm" label-width="80px" size="small">
        <el-row :gutter="15">
          <el-col :span="8"><el-form-item label="账号"><el-input v-model.number="modifyForm.pId" disabled></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="姓名" prop="pName"><el-input v-model="modifyForm.pName"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="性别"><el-select v-model="modifyForm.pGender" style="width:100%"><el-option label="男" value="男"></el-option><el-option label="女" value="女"></el-option></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="8"><el-form-item label="出生日期"><el-input v-model="modifyForm.pBirthday"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="证件号"><el-input v-model="modifyForm.pCard"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="手机号" prop="pPhone"><el-input v-model="modifyForm.pPhone"></el-input></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="8"><el-form-item label="邮箱"><el-input v-model="modifyForm.pEmail"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="医保号"><el-input v-model="modifyForm.pInsuranceId"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="医保类型"><el-select v-model="modifyForm.pInsuranceType" style="width:100%"><el-option label="城镇职工" value="城镇职工"></el-option><el-option label="城乡居民" value="城乡居民"></el-option><el-option label="自费" value="自费"></el-option></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="8"><el-form-item label="民族"><el-select v-model="modifyForm.pNation" style="width:100%"><el-option label="汉族" value="汉族"></el-option><el-option label="蒙古族" value="蒙古族"></el-option><el-option label="回族" value="回族"></el-option><el-option label="藏族" value="藏族"></el-option><el-option label="维吾尔族" value="维吾尔族"></el-option><el-option label="苗族" value="苗族"></el-option></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="婚姻"><el-select v-model="modifyForm.pMaritalStatus" style="width:100%"><el-option label="未婚" value="未婚"></el-option><el-option label="已婚" value="已婚"></el-option><el-option label="离异" value="离异"></el-option><el-option label="丧偶" value="丧偶"></el-option></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="血型"><el-select v-model="modifyForm.pBloodType" style="width:100%"><el-option label="A型" value="A"></el-option><el-option label="B型" value="B"></el-option><el-option label="AB型" value="AB"></el-option><el-option label="O型" value="O"></el-option></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="8"><el-form-item label="联系人"><el-input v-model="modifyForm.pContactPerson"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="联系人电话"><el-input v-model="modifyForm.pContactPhone"></el-input></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="住址"><el-input v-model="modifyForm.pAddress"></el-input></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer"><el-button @click="modifyFormVisible=false">取消</el-button><el-button type="primary" @click="modifyPatient('modifyForm')">确定</el-button></div>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "PatientList",
  data() {
    return {
      pageNumber:1, size:10, query:"", patientData:[], total:0,
      addFormVisible:false, addForm:{pGender:"男"},
      rules: { pId:[{required:true,message:"请输入账号",trigger:"blur"}], pName:[{required:true,message:"请输入姓名",trigger:"blur"}], pPhone:[{required:true,message:"请输入手机号",trigger:"blur"}] },
      modifyFormVisible:false, modifyForm:{}
    };
  },
  methods: {
    requestPatients() { request.get("admin/findAllPatients",{params:{pageNumber:this.pageNumber,size:this.size,query:this.query}}).then(r=>{this.patientData=r.data.data.patients||[];this.total=r.data.data.total||0;}); },
    exportExcel() {
      if (!this.patientData||!this.patientData.length) return this.$message.warning("暂无数据");
      var csv="﻿账号,姓名,性别,年龄,证件号,手机号,医保号,医保类型,民族,婚姻,血型,地址\n";
      this.patientData.forEach(function(d){csv+=d.pId+","+d.pName+","+d.pGender+","+d.pAge+","+d.pCard+","+d.pPhone+","+(d.pInsuranceId||"")+","+(d.pInsuranceType||"")+","+(d.pNation||"")+","+(d.pMaritalStatus||"")+","+(d.pBloodType||"")+","+(d.pAddress||"")+"\n";});
      var b=new Blob([csv],{type:"text/csv;charset=utf-8"}); var a=document.createElement("a"); a.href=URL.createObjectURL(b); a.download="患者列表.csv"; a.click();
    },
    addPatient(fn) { this.$refs[fn].validate(v=>{if(!v)return;request.get("admin/addPatient",{params:this.addForm}).then(r=>{if(r.data.status!==200)return this.$message.error("账号已存在");this.addFormVisible=false;this.requestPatients();this.$message.success("增加成功");});}); },
    modifyDialog(id) { request.get("admin/findPatient",{params:{pId:id}}).then(r=>{this.modifyForm=r.data.data;this.modifyFormVisible=true;}); },
    modifyPatient(fn) { this.$refs[fn].validate(v=>{if(!v)return;request.get("admin/modifyPatient",{params:this.modifyForm}).then(r=>{this.modifyFormVisible=false;this.requestPatients();this.$message.success("修改成功");});}); },
    deletePatient(id){request.get("admin/deletePatient",{params:{pId:id}}).then(()=>this.requestPatients());},
    deleteDialog(id){this.$confirm("确定删除?","提示",{type:"warning"}).then(()=>{this.deletePatient(id);this.$message.success("删除成功");}).catch(()=>{});},
  },
  created(){this.requestPatients();}
};
</script>