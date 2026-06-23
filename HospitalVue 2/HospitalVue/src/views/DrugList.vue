<template>
  <el-card>
    <div slot="header"><i class="el-icon-first-aid-kit"></i> 药物信息管理</div>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query" placeholder="名称/通用名/拼音/规格/厂家" clearable size="small" class="search-input" @keyup.enter.native="requestDrugs">
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
        <el-button size="small" type="success" @click="exportExcel">导出</el-button>
        <el-button type="primary" size="small" @click="addFormVisible = true">
          <i class="el-icon-plus"></i> 增加药物
        </el-button>
      </div>
    </div>
    <el-table :data="drugData" stripe border style="width:100%" size="small">
      <el-table-column label="编号" prop="drId" width="65"></el-table-column>
      <el-table-column label="名称" prop="drName" min-width="110"></el-table-column>
      <el-table-column label="分类" width="55"><template slot-scope="s"><el-tag :type="s.row.drType===2?'success':'primary'" size="mini">{{ s.row.drType===2?'中药':'西药' }}</el-tag></template></el-table-column>
      <el-table-column label="细分类" prop="drSubtype" width="80"></el-table-column>
      <el-table-column label="处方" prop="drRxType" width="65"></el-table-column>
      <el-table-column label="医保" prop="drInsuranceType" width="65"></el-table-column>
      <el-table-column label="库存" prop="drNumber" width="55"></el-table-column>
      <el-table-column label="预警" prop="drMinStock" width="55"></el-table-column>
      <el-table-column label="单位" prop="drUnit" width="50"></el-table-column>
      <el-table-column label="单价" prop="drPrice" width="65"></el-table-column>
      <el-table-column label="供应商" prop="drPublisher" min-width="100" show-overflow-tooltip></el-table-column>
      <el-table-column label="规格" prop="drSpec" width="110"></el-table-column>
      <el-table-column label="批准文号" prop="drApprovalNo" width="130"></el-table-column>
      <el-table-column label="剂型" prop="drForm" width="60"></el-table-column>
      <el-table-column label="厂家" prop="drManufacturer" width="120" show-overflow-tooltip></el-table-column>
      <el-table-column label="操作" width="120" fixed="right" align="center">
        <template slot-scope="s">
          <el-button type="primary" size="mini" icon="el-icon-view" circle @click="viewDetail(s.row)" title="查看药学档案"></el-button>
          <el-button type="warning" size="mini" icon="el-icon-s-data" circle @click="showPriceLog(s.row.drId)" title="调价记录"></el-button>
          <el-button type="success" size="mini" icon="el-icon-edit" circle @click="modifyDialog(s.row.drId)" title="编辑"></el-button>
          <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteDialog(s.row.drId)" title="删除"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" background
      layout="total,sizes,prev,pager,next,jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[5,10,20,50]" :total="total"></el-pagination>

    <el-dialog title="增加药物" :visible.sync="addFormVisible" width="760px">
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
        <el-row :gutter="15"><el-col :span="12"><el-form-item label="通用名"><el-input v-model="addForm.drGenericName"></el-input></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="拼音码"><el-input v-model="addForm.drPinyin" placeholder="如 AMXL"></el-input></el-form-item></el-col></el-row>
        <el-row :gutter="15"><el-col :span="8"><el-form-item label="细分类"><el-select v-model="addForm.drSubtype" filterable allow-create style="width:100%"><el-option v-for="c in subtypes" :key="c" :label="c" :value="c"></el-option></el-select></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="处方属性"><el-select v-model="addForm.drRxType" style="width:100%"><el-option label="处方药" value="处方药"></el-option><el-option label="非处方药" value="非处方药"></el-option></el-select></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="医保类别"><el-select v-model="addForm.drInsuranceType" style="width:100%"><el-option v-for="v in ['医保甲类','医保乙类','自费']" :key="v" :label="v" :value="v"></el-option></el-select></el-form-item></el-col></el-row>
        <el-row :gutter="15"><el-col :span="8"><el-form-item label="库存下限"><el-input-number v-model="addForm.drMinStock" :min="0" style="width:100%"></el-input-number></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="抗菌级别"><el-select v-model="addForm.drAntibioticLevel" clearable style="width:100%"><el-option v-for="v in ['非限制','限制','特殊']" :key="v" :label="v" :value="v"></el-option></el-select></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="特殊标记"><el-checkbox v-model="addForm.drControlled" :true-label="1" :false-label="0">管制</el-checkbox><el-checkbox v-model="addForm.drEssential" :true-label="1" :false-label="0">基药</el-checkbox></el-form-item></el-col></el-row>
        <el-form-item label="储存条件"><el-input v-model="addForm.drStorage"></el-input></el-form-item>
        <el-form-item label="适应症"><el-input v-model="addForm.drIndication" type="textarea" :rows="2"></el-input></el-form-item>
        <el-form-item label="禁忌/不良"><el-input v-model="addForm.drContraindication" placeholder="禁忌症"></el-input><el-input v-model="addForm.drAdverseReaction" placeholder="不良反应" style="margin-top:6px"></el-input></el-form-item>
        <template v-if="addForm.drType===2"><el-row :gutter="15"><el-col :span="8"><el-form-item label="药性"><el-input v-model="addForm.drTcmNature"></el-input></el-form-item></el-col><el-col :span="8"><el-form-item label="药味"><el-input v-model="addForm.drTcmFlavor"></el-input></el-form-item></el-col><el-col :span="8"><el-form-item label="归经"><el-input v-model="addForm.drTcmMeridian"></el-input></el-form-item></el-col></el-row><el-form-item label="煎服方法"><el-input v-model="addForm.drDecoctionMethod"></el-input></el-form-item></template>
      </el-form>
      <div slot="footer"><el-button @click="addFormVisible=false">取消</el-button><el-button type="primary" @click="addDrug('addForm')">确定</el-button></div>
    </el-dialog>

    <el-dialog title="修改药物" :visible.sync="modifyFormVisible" width="760px">
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
        <el-row :gutter="15"><el-col :span="12"><el-form-item label="通用名"><el-input v-model="modifyForm.drGenericName"></el-input></el-form-item></el-col><el-col :span="12"><el-form-item label="拼音码"><el-input v-model="modifyForm.drPinyin"></el-input></el-form-item></el-col></el-row>
        <el-row :gutter="15"><el-col :span="8"><el-form-item label="细分类"><el-select v-model="modifyForm.drSubtype" filterable allow-create style="width:100%"><el-option v-for="c in subtypes" :key="c" :label="c" :value="c"></el-option></el-select></el-form-item></el-col><el-col :span="8"><el-form-item label="处方属性"><el-select v-model="modifyForm.drRxType" style="width:100%"><el-option label="处方药" value="处方药"></el-option><el-option label="非处方药" value="非处方药"></el-option></el-select></el-form-item></el-col><el-col :span="8"><el-form-item label="医保类别"><el-select v-model="modifyForm.drInsuranceType" style="width:100%"><el-option v-for="v in ['医保甲类','医保乙类','自费']" :key="v" :label="v" :value="v"></el-option></el-select></el-form-item></el-col></el-row>
        <el-row :gutter="15"><el-col :span="8"><el-form-item label="库存下限"><el-input-number v-model="modifyForm.drMinStock" :min="0" style="width:100%"></el-input-number></el-form-item></el-col><el-col :span="8"><el-form-item label="抗菌级别"><el-select v-model="modifyForm.drAntibioticLevel" clearable style="width:100%"><el-option v-for="v in ['非限制','限制','特殊']" :key="v" :label="v" :value="v"></el-option></el-select></el-form-item></el-col><el-col :span="8"><el-form-item label="特殊标记"><el-checkbox v-model="modifyForm.drControlled" :true-label="1" :false-label="0">管制</el-checkbox><el-checkbox v-model="modifyForm.drEssential" :true-label="1" :false-label="0">基药</el-checkbox></el-form-item></el-col></el-row>
        <el-form-item label="储存条件"><el-input v-model="modifyForm.drStorage"></el-input></el-form-item>
        <el-form-item label="适应症"><el-input v-model="modifyForm.drIndication" type="textarea" :rows="2"></el-input></el-form-item>
        <el-form-item label="禁忌症"><el-input v-model="modifyForm.drContraindication"></el-input></el-form-item>
        <el-form-item label="不良反应"><el-input v-model="modifyForm.drAdverseReaction"></el-input></el-form-item>
        <template v-if="modifyForm.drType===2"><el-row :gutter="15"><el-col :span="8"><el-form-item label="药性"><el-input v-model="modifyForm.drTcmNature"></el-input></el-form-item></el-col><el-col :span="8"><el-form-item label="药味"><el-input v-model="modifyForm.drTcmFlavor"></el-input></el-form-item></el-col><el-col :span="8"><el-form-item label="归经"><el-input v-model="modifyForm.drTcmMeridian"></el-input></el-form-item></el-col></el-row><el-form-item label="煎服方法"><el-input v-model="modifyForm.drDecoctionMethod"></el-input></el-form-item></template>
      </el-form>
      <div slot="footer"><el-button @click="modifyFormVisible=false">取消</el-button><el-button type="primary" @click="modifyDrug('modifyForm')">确定</el-button></div>
    </el-dialog>

    <el-dialog title="药品药学档案" :visible.sync="detailVisible" width="760px">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="药品名称">{{ detail.drName }}</el-descriptions-item>
            <el-descriptions-item label="通用名">{{ detail.drGenericName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="拼音码">{{ detail.drPinyin || '-' }}</el-descriptions-item>
            <el-descriptions-item label="分类">{{ detail.drType===2?'中药':'西药' }} / {{ detail.drSubtype || '-' }}</el-descriptions-item>
            <el-descriptions-item label="处方属性">{{ detail.drRxType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="医保类别">{{ detail.drInsuranceType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="抗菌级别">{{ detail.drAntibioticLevel || '非抗菌药' }}</el-descriptions-item>
            <el-descriptions-item label="特殊管制">{{ detail.drControlled===1?'是':'否' }}</el-descriptions-item>
            <el-descriptions-item label="基本药物">{{ detail.drEssential===1?'是':'否' }}</el-descriptions-item>
            <el-descriptions-item label="规格">{{ detail.drSpec || '-' }}</el-descriptions-item>
            <el-descriptions-item label="剂型">{{ detail.drForm || '-' }}</el-descriptions-item>
            <el-descriptions-item label="储存">{{ detail.drStorage || '-' }}</el-descriptions-item>
            <el-descriptions-item label="适应症" :span="3">{{ detail.drIndication || '-' }}</el-descriptions-item>
            <el-descriptions-item label="禁忌症" :span="3">{{ detail.drContraindication || '-' }}</el-descriptions-item>
            <el-descriptions-item label="不良反应" :span="3">{{ detail.drAdverseReaction || '-' }}</el-descriptions-item>
            <el-descriptions-item v-if="detail.drType===2" label="性味归经" :span="3">{{ [detail.drTcmNature,detail.drTcmFlavor,detail.drTcmMeridian].filter(Boolean).join('；') || '-' }}</el-descriptions-item>
            <el-descriptions-item v-if="detail.drType===2" label="煎服方法" :span="3">{{ detail.drDecoctionMethod || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
        <el-col :span="8" style="text-align:center;">
          <div style="margin-bottom:8px;font-weight:bold;color:#606266;">药品图片</div>
          <div v-if="detail.drImage" style="margin-bottom:8px;">
            <el-image :src="detail.drImage" style="max-width:160px;max-height:160px;border:1px solid #eee;border-radius:4px;" fit="contain"></el-image>
          </div>
          <div v-else style="height:110px;line-height:110px;background:#fafafa;color:#c0c4cc;border:1px dashed #ddd;border-radius:4px;margin-bottom:8px;">暂无图片</div>
          <el-upload :action="''" :auto-upload="false" :show-file-list="false" accept="image/*" :on-change="handleDrugImageChange">
            <el-button size="small" type="primary"><i class="el-icon-upload2"></i> 上传图片</el-button>
          </el-upload>
        </el-col>
      </el-row>
    </el-dialog>

    <!-- 调价记录对话框 -->
    <el-dialog title="药品调价记录" :visible.sync="priceLogVisible" width="650px">
      <el-table :data="priceLogData" border stripe size="small" style="width:100%">
        <el-table-column prop="dplId" label="编号" width="60"></el-table-column>
        <el-table-column prop="drId" label="药品" width="70"></el-table-column>
        <el-table-column label="原价" width="80"><template slot-scope="s">¥{{ s.row.oldPrice }}</template></el-table-column>
        <el-table-column label="新价" width="80"><template slot-scope="s">¥{{ s.row.newPrice }}</template></el-table-column>
        <el-table-column label="变动" width="80">
          <template slot-scope="s">
            <span :class="s.row.newPrice > s.row.oldPrice ? 'success' : 'danger'">
              {{ (s.row.newPrice - s.row.oldPrice) > 0 ? '+' : '' }}{{ (s.row.newPrice - s.row.oldPrice).toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="changeReason" label="原因" min-width="120"></el-table-column>
        <el-table-column prop="operator" label="操作人" width="80"></el-table-column>
        <el-table-column prop="createTime" label="时间" width="160"></el-table-column>
      </el-table>
      <div v-if="priceLogData.length===0" style="text-align:center;padding:30px;color:#999;">暂无调价记录</div>
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
      addFormVisible:false, addForm:{drType:1,drRxType:"处方药",drInsuranceType:"自费",drMinStock:20,drControlled:0,drEssential:0},
      subtypes:["抗感染药","解热镇痛药","心血管药","消化系统药","呼吸系统药","内分泌药","外用药","中成药","中药饮片","中药配方颗粒"],
      rules: { drId:[{required:true,message:"请输入编号",trigger:"blur"}], drName:[{required:true,message:"请输入名称",trigger:"blur"}] },
      modifyFormVisible:false, modifyForm:{}, detailVisible:false, detail:{},
      priceLogVisible:false, priceLogData:[], priceLogDrugId:''
    };
  },
  methods: {
    requestDrugs() { request.get("drug/findAllDrugs",{params:{pageNumber:this.pageNumber,size:this.size,query:this.query,typeFilter:this.typeFilter}}).then(r=>{this.drugData=r.data.data.drugs||[];this.total=r.data.data.total||0;}); },
    exportExcel() {
      if (!this.drugData||!this.drugData.length) return this.$message.warning("暂无数据");
      var csv="﻿编号,名称,分类,库存,单位,单价,供应商,规格,批准文号,剂型,生产厂家\n";
      this.drugData.forEach(function(d){csv+=d.drId+","+d.drName+","+(d.drType===2?"中药":"西药")+","+d.drNumber+","+d.drUnit+","+d.drPrice+","+d.drPublisher+","+(d.drSpec||"")+","+(d.drApprovalNo||"")+","+(d.drForm||"")+","+(d.drManufacturer||"")+"\n";});
      var b=new Blob([csv],{type:"text/csv;charset=utf-8"}); var a=document.createElement("a"); a.href=URL.createObjectURL(b); a.download="药品列表.csv"; a.click();
    },
    addDrug(fn) { this.$refs[fn].validate(v=>{if(!v)return;request.get("drug/addDrug",{params:this.addForm}).then(r=>{if(r.data.status!==200)return this.$message.error("编号已占用");this.addFormVisible=false;this.requestDrugs();this.$message.success("增加成功");});}); },
    modifyDialog(id) { request.get("drug/findDrug",{params:{drId:id}}).then(r=>{this.modifyForm=r.data.data;this.modifyFormVisible=true;}); },
    viewDetail(row) { this.detail=row;this.detailVisible=true; },
    // 药品图片上传 — 转为 base64 通过 API 保存
    handleDrugImageChange(file) {
      if (!file || !file.raw) return;
      const reader = new FileReader();
      reader.onload = (e) => {
        const base64 = e.target.result;
        this.detail.drImage = base64;
        request.get("drug/uploadImage", { params: { drId: this.detail.drId, drImage: base64 } }).then(r => {
          if (r.data.status === 200) this.$message.success("图片上传成功");
          else this.$message.error(r.data.msg || "上传失败");
        });
      };
      reader.readAsDataURL(file.raw);
    },
    modifyDrug(fn) { this.$refs[fn].validate(v=>{if(!v)return;request.get("drug/modifyDrug",{params:this.modifyForm}).then(r=>{this.modifyFormVisible=false;this.requestDrugs();this.$message.success("修改成功");});}); },
    deleteDrug(id) { request.get("drug/deleteDrug",{params:{drId:id}}).then(()=>this.requestDrugs()); },
    deleteDialog(id) { this.$confirm("确定删除?","提示",{type:"warning"}).then(()=>{this.deleteDrug(id);this.$message.success("删除成功");}).catch(()=>{}); },
    showPriceLog(drId) {
      this.priceLogDrugId = drId;
      request.get("drug/findPriceLogs",{params:{drId}}).then(r=>{
        this.priceLogData = r.data.data || [];
        this.priceLogVisible = true;
      });
    },
    handleSizeChange(s){this.size=s;this.requestDrugs();},
    handleCurrentChange(p){this.pageNumber=p;this.requestDrugs();}
  },
  created() { this.requestDrugs(); }
};
</script>

