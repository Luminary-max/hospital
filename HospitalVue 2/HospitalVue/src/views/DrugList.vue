<template>
  <el-card>
    <div slot="header"><i class="el-icon-first-aid-kit"></i> 药物信息管理</div>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query" placeholder="搜索药品名称" clearable size="small" class="search-input" @keyup.enter.native="requestDrugs">
          <el-button slot="append" icon="el-icon-search" @click="requestDrugs"></el-button>
        </el-input>
        <el-select v-model="typeFilter" placeholder="药品分类" size="small" class="filter-select" @change="requestDrugs">
          <el-option label="全部药品" value=""></el-option>
          <el-option label="西药" value="1"></el-option>
          <el-option label="中药" value="2"></el-option>
        </el-select>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
        <el-button type="primary" size="small" @click="addFormVisible = true">
          <i class="el-icon-plus"></i> 增加药物
        </el-button>
      </div>
    </div>
    <el-table :data="drugData" stripe border>
      <el-table-column label="编号" prop="drId" width="80"></el-table-column>
      <el-table-column label="名称" prop="drName" min-width="120"></el-table-column>
      <el-table-column label="分类" width="60"><template slot-scope="s"><el-tag :type="s.row.drType===2?'success':'primary'" size="mini">{{ s.row.drType===2?'中药':'西药' }}</el-tag></template></el-table-column>
      <el-table-column label="库存" prop="drNumber" width="60"></el-table-column>
      <el-table-column label="单位" prop="drUnit" width="55"></el-table-column>
      <el-table-column label="单价" prop="drPrice" width="70"></el-table-column>
      <el-table-column label="供应商" prop="drPublisher" width="120" show-overflow-tooltip></el-table-column>
      <el-table-column label="规格" prop="drSpec" width="120"></el-table-column>
      <el-table-column label="批准文号" prop="drApprovalNo" width="160"></el-table-column>
      <el-table-column label="剂型" prop="drForm" width="70"></el-table-column>
      <el-table-column label="厂家" prop="drManufacturer" width="140" show-overflow-tooltip></el-table-column>
      <el-table-column label="操作" width="95" fixed="right" align="center">
        <template slot-scope="s">
          <el-button type="success" size="mini" icon="el-icon-edit" circle @click="modifyDialog(s.row.drId)" title="编辑"></el-button>
          <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteDialog(s.row.drId)" title="删除"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" background
      layout="total,sizes,prev,pager,next,jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[5,10,20,50]" :total="total"></el-pagination>

    <el-dialog title="增加药物" :visible.sync="addFormVisible" width="520px">
      <el-form :model="addForm" :rules="rules" ref="addForm" label-width="90px" size="small">
        <el-row :gutter="15"><el-col :span="12"><el-form-item label="编号" prop="drId"><el-input v-model.number="addForm.drId"></el-input></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="名称" prop="drName"><el-input v-model="addForm.drName"></el-input></el-form-item></el-col></el-row>
        <el-row :gutter="15"><el-col :span="8"><el-form-item label="数量"><el-input-number v-model="addForm.drNumber" :min="0" :max="9999"></el-input-number></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="单位" prop="drUnit"><el-select v-model="addForm.drUnit" style="width:100%"><el-option v-for="u in ['盒','袋','片','粒','支','g','ml','丸']" :key="u" :label="u" :value="u"></el-option></el-select></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="单价"><el-input v-model="addForm.drPrice"></el-input></el-form-item></el-col></el-row>
        <el-row :gutter="15"><el-col :span="12"><el-form-item label="药品分类"><el-radio v-model="addForm.drType" :label="1">西药</el-radio><el-radio v-model="addForm.drType" :label="2">中药</el-radio></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="剂型"><el-select v-model="addForm.drForm" style="width:100%"><el-option v-for="f in ['片剂','胶囊','注射液','颗粒','口服液','凝胶','丸剂','滴丸']" :key="f" :label="f" :value="f"></el-option></el-select></el-form-item></el-col></el-row>
        <el-form-item label="供应商"><el-input v-model="addForm.drPublisher"></el-input></el-form-item>
        <el-row :gutter="15"><el-col :span="12"><el-form-item label="规格"><el-input v-model="addForm.drSpec" placeholder="如 0.25g×12片"></el-input></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="批准文号"><el-input v-model="addForm.drApprovalNo" placeholder="国药准字"></el-input></el-form-item></el-col></el-row>
        <el-form-item label="生产厂家"><el-input v-model="addForm.drManufacturer"></el-input></el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="addFormVisible=false">取消</el-button><el-button type="primary" @click="addDrug('addForm')">确定</el-button></div>
    </el-dialog>

    <el-dialog title="修改药物" :visible.sync="modifyFormVisible" width="520px">
      <el-form :model="modifyForm" :rules="rules" ref="modifyForm" label-width="90px" size="small">
        <el-row :gutter="15"><el-col :span="12"><el-form-item label="编号"><el-input v-model.number="modifyForm.drId" disabled></el-input></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="名称" prop="drName"><el-input v-model="modifyForm.drName"></el-input></el-form-item></el-col></el-row>
        <el-row :gutter="15"><el-col :span="8"><el-form-item label="数量"><el-input-number v-model="modifyForm.drNumber" :min="0" :max="9999"></el-input-number></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="单位"><el-select v-model="modifyForm.drUnit" style="width:100%"><el-option v-for="u in ['盒','袋','片','粒','支','g','ml','丸']" :key="u" :label="u" :value="u"></el-option></el-select></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="单价"><el-input v-model="modifyForm.drPrice"></el-input></el-form-item></el-col></el-row>
        <el-row :gutter="15"><el-col :span="12"><el-form-item label="药品分类"><el-radio v-model="modifyForm.drType" :label="1">西药</el-radio><el-radio v-model="modifyForm.drType" :label="2">中药</el-radio></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="剂型"><el-select v-model="modifyForm.drForm" style="width:100%"><el-option v-for="f in ['片剂','胶囊','注射液','颗粒','口服液','凝胶','丸剂','滴丸']" :key="f" :label="f" :value="f"></el-option></el-select></el-form-item></el-col></el-row>
        <el-form-item label="供应商"><el-input v-model="modifyForm.drPublisher"></el-input></el-form-item>
        <el-row :gutter="15"><el-col :span="12"><el-form-item label="规格"><el-input v-model="modifyForm.drSpec" placeholder="如 0.25g×12片"></el-input></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="批准文号"><el-input v-model="modifyForm.drApprovalNo" placeholder="国药准字"></el-input></el-form-item></el-col></el-row>
        <el-form-item label="生产厂家"><el-input v-model="modifyForm.drManufacturer"></el-input></el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="modifyFormVisible=false">取消</el-button><el-button type="primary" @click="modifyDrug('modifyForm')">确定</el-button></div>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "DrugList",
  data() {
    return {
      pageNumber:1, size:10, query:"", typeFilter:"", drugData:[], total:0,
      addFormVisible:false, addForm:{drType:1},
      rules: { drId:[{required:true,message:"请输入编号",trigger:"blur"}], drName:[{required:true,message:"请输入名称",trigger:"blur"}] },
      modifyFormVisible:false, modifyForm:{}
    };
  },
  methods: {
    requestDrugs() { request.get("drug/findAllDrugs",{params:{pageNumber:this.pageNumber,size:this.size,query:this.query,typeFilter:this.typeFilter}}).then(r=>{this.drugData=r.data.data.drugs||[];this.total=r.data.data.total||0;}); },
    addDrug(fn) { this.$refs[fn].validate(v=>{if(!v)return;request.get("drug/addDrug",{params:this.addForm}).then(r=>{if(r.data.status!==200)return this.$message.error("编号已占用");this.addFormVisible=false;this.requestDrugs();this.$message.success("增加成功");});}); },
    modifyDialog(id) { request.get("drug/findDrug",{params:{drId:id}}).then(r=>{this.modifyForm=r.data.data;this.modifyFormVisible=true;}); },
    modifyDrug(fn) { this.$refs[fn].validate(v=>{if(!v)return;request.get("drug/modifyDrug",{params:this.modifyForm}).then(r=>{this.modifyFormVisible=false;this.requestDrugs();this.$message.success("修改成功");});}); },
    deleteDrug(id) { request.get("drug/deleteDrug",{params:{drId:id}}).then(()=>this.requestDrugs()); },
    deleteDialog(id) { this.$confirm("确定删除?","提示",{type:"warning"}).then(()=>{this.deleteDrug(id);this.$message.success("删除成功");}).catch(()=>{}); },
    handleSizeChange(s){this.size=s;this.requestDrugs();},
    handleCurrentChange(p){this.pageNumber=p;this.requestDrugs();}
  },
  created() { this.requestDrugs(); }
};
</script>
