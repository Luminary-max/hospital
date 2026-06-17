<template>
  <el-card>
    <div slot="header"><i class="el-icon-monitor"></i> 检查项目管理</div>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query" placeholder="搜索项目名称" clearable size="small" class="search-input" @keyup.enter.native="requestChecks">
          <el-button slot="append" icon="el-icon-search" @click="requestChecks"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
        <el-button type="primary" size="small" @click="addFormVisible = true"><i class="el-icon-plus"></i> 增加项目</el-button>
      </div>
    </div>
    <el-table :data="checkData" stripe border>
      <el-table-column label="编号" prop="chId" width="100"></el-table-column>
      <el-table-column label="项目名称" prop="chName" min-width="200"></el-table-column>
      <el-table-column label="价格(元)" prop="chPrice" width="100"></el-table-column>
      <el-table-column label="操作" width="95" fixed="right" align="center">
        <template slot-scope="s">
          <el-button type="success" size="mini" icon="el-icon-edit" circle @click="modifyDialog(s.row.chId)" title="编辑"></el-button>
          <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteDialog(s.row.chId)" title="删除"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" background
      layout="total,sizes,prev,pager,next,jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[5,10,20,50]" :total="total"></el-pagination>

    <el-dialog title="增加检查项目" :visible.sync="addFormVisible" width="420px">
      <el-form :model="addForm" :rules="rules" ref="addForm" label-width="80px" size="small">
        <el-form-item label="编号" prop="chId"><el-input v-model.number="addForm.chId"></el-input></el-form-item>
        <el-form-item label="名称" prop="chName"><el-input v-model="addForm.chName"></el-input></el-form-item>
        <el-form-item label="价格" prop="chPrice"><el-input v-model="addForm.chPrice"></el-input></el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="addFormVisible=false">取消</el-button><el-button type="primary" @click="addCheck('addForm')">确定</el-button></div>
    </el-dialog>

    <el-dialog title="修改检查项目" :visible.sync="modifyFormVisible" width="420px">
      <el-form :model="modifyForm" :rules="rules" ref="modifyForm" label-width="80px" size="small">
        <el-form-item label="编号"><el-input v-model.number="modifyForm.chId" disabled></el-input></el-form-item>
        <el-form-item label="名称" prop="chName"><el-input v-model="modifyForm.chName"></el-input></el-form-item>
        <el-form-item label="价格" prop="chPrice"><el-input v-model="modifyForm.chPrice"></el-input></el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="modifyFormVisible=false">取消</el-button><el-button type="primary" @click="modifyCheck('modifyForm')">确定</el-button></div>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "CheckList",
  data() {
    return {
      pageNumber:1, size:10, query:"", checkData:[], total:0,
      addFormVisible:false, addForm:{},
      rules: { chId:[{required:true,message:"请输入编号",trigger:"blur"}], chName:[{required:true,message:"请输入名称",trigger:"blur"}], chPrice:[{required:true,message:"请输入价格",trigger:"blur"}] },
      modifyFormVisible:false, modifyForm:{}
    };
  },
  methods: {
    requestChecks() { request.get("check/findAllChecks",{params:{pageNumber:this.pageNumber,size:this.size,query:this.query}}).then(r=>{this.checkData=r.data.data.checks||[];this.total=r.data.data.total||0;}); },
    addCheck(fn) { this.$refs[fn].validate(v=>{if(!v)return;request.get("check/addCheck",{params:this.addForm}).then(r=>{if(r.data.status!==200)return this.$message.error("编号已占用");this.addFormVisible=false;this.requestChecks();this.$message.success("增加成功");});}); },
    modifyDialog(id) { request.get("check/findCheck",{params:{chId:id}}).then(r=>{this.modifyForm=r.data.data;this.modifyFormVisible=true;}); },
    modifyCheck(fn){this.$refs[fn].validate(v=>{if(!v)return;request.get("check/modifyCheck",{params:this.modifyForm}).then(r=>{this.modifyFormVisible=false;this.requestChecks();this.$message.success("修改成功");});});},
    deleteCheck(id){request.get("check/deleteCheck",{params:{chId:id}}).then(()=>this.requestChecks());},
    deleteDialog(id){this.$confirm("确定删除?","提示",{type:"warning"}).then(()=>{this.deleteCheck(id);this.$message.success("删除成功");}).catch(()=>{});},
    handleSizeChange(s){this.size=s;this.requestChecks();},
    handleCurrentChange(p){this.pageNumber=p;this.requestChecks();}
  },
  created(){this.requestChecks();}
};
</script>