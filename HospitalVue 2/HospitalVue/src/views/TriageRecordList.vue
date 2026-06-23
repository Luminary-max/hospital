<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-document"></i> 分诊记录管理</span>
      <el-button type="primary" size="small" style="float:right;" @click="openAddDialog">
        <i class="el-icon-plus"></i> 新增分诊
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-select v-model="statusFilter" placeholder="分诊状态" size="small" class="filter-select" @change="loadData" clearable>
          <el-option label="全部状态" value=""></el-option>
          <el-option label="待分诊" value="0"></el-option>
          <el-option label="已分诊" value="1"></el-option>
          <el-option label="已就诊" value="2"></el-option>
        </el-select>
        <el-select v-model="levelFilter" placeholder="分诊级别" size="small" class="filter-select" @change="loadData" clearable>
          <el-option label="全部级别" value=""></el-option>
          <el-option label="普通" value="0"></el-option>
          <el-option label="优先" value="1"></el-option>
          <el-option label="急诊" value="2"></el-option>
        </el-select>
        <el-input v-model="query" placeholder="搜索订单ID/患者ID" size="small" class="search-input" clearable @keyup.enter.native="loadData">
          <el-button slot="append" icon="el-icon-search" @click="loadData"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
      </div>
    </div>

    <el-table :data="triageData" border stripe style="width:100%">
      <el-table-column prop="tId" label="编号" width="65" align="center"></el-table-column>
      <el-table-column prop="oId" label="订单ID" width="70" align="center"></el-table-column>
      <el-table-column prop="pId" label="患者ID" width="65" align="center"></el-table-column>
      <el-table-column prop="dId" label="医生ID" width="65" align="center"></el-table-column>
      <el-table-column label="分诊级别" width="80" align="center">
        <template slot-scope="s">
          <el-tag :type="s.row.tLevel===2?'danger':s.row.tLevel===1?'warning':'primary'" size="mini" effect="dark">
            {{ {0:'普通',1:'优先',2:'急诊'}[s.row.tLevel] || '普通' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template slot-scope="s">
          <el-tag :type="s.row.tStatus===2?'success':s.row.tStatus===1?'primary':'info'">
            {{ {0:'待分诊',1:'已分诊',2:'已就诊'}[s.row.tStatus] || '待分诊' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="tChiefComplaint" label="主诉" min-width="150" show-overflow-tooltip></el-table-column>
      <el-table-column label="生命体征" min-width="200">
        <template slot-scope="s">
          <span class="vital-sign">
            <i class="el-icon-temperature"></i> {{ s.row.tTemperature ? s.row.tTemperature+'℃' : '-' }}
          </span>
          <span class="vital-sign">
            <i class="el-icon-s-data"></i> {{ s.row.tBloodPressure || '-' }}
          </span>
          <span class="vital-sign">
            <i class="el-icon-heart"></i> {{ s.row.tHeartRate ? s.row.tHeartRate+'bpm' : '-' }}
          </span>
          <span class="vital-sign">
            <i class="el-icon-rank"></i> {{ s.row.tWeight ? s.row.tWeight+'kg' : '-' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="tNote" label="备注" min-width="120" show-overflow-tooltip></el-table-column>
      <el-table-column prop="tCreateTime" label="创建时间" width="155"></el-table-column>
      <el-table-column label="操作" width="80" fixed="right" align="center">
        <template slot-scope="s">
          <el-button type="primary" size="mini" icon="el-icon-edit" circle @click="openEditDialog(s.row)" title="编辑"></el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}"
      :current-page="pageNumber" :page-sizes="[10,20,30]" :page-size="size"
      layout="total,sizes,prev,pager,next,jumper" :total="total" style="margin-top:15px;">
    </el-pagination>

    <!-- 新增/编辑分诊对话框 -->
    <el-dialog :title="isEdit?'编辑分诊':'新增分诊'" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px" size="small">
        <el-row :gutter="15">
          <el-col :span="8">
            <el-form-item label="订单ID" prop="oId">
              <el-input v-model.number="form.oId" :disabled="isEdit"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="患者ID" prop="pId">
              <el-input v-model.number="form.pId"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="医生ID" prop="dId">
              <el-input v-model="form.dId"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="分诊级别" prop="tLevel">
          <el-radio-group v-model="form.tLevel">
            <el-radio :label="0">普通</el-radio>
            <el-radio :label="1">优先</el-radio>
            <el-radio :label="2">急诊</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-divider content-position="left">生命体征</el-divider>
        <el-row :gutter="15">
          <el-col :span="8">
            <el-form-item label="体温(℃)">
              <el-input-number v-model="form.tTemperature" :min="34" :max="43" :step="0.1" :precision="1" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="血压(mmHg)">
              <el-input v-model="form.tBloodPressure" placeholder="如 120/80"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="心率(bpm)">
              <el-input-number v-model="form.tHeartRate" :min="20" :max="250" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="15">
          <el-col :span="8">
            <el-form-item label="体重(kg)">
              <el-input-number v-model="form.tWeight" :min="1" :max="300" :step="0.5" :precision="1" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">其他</el-divider>
        <el-form-item label="主诉">
          <el-input v-model="form.tChiefComplaint" type="textarea" :rows="2" placeholder="患者主要症状描述"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.tNote" type="textarea" :rows="2" placeholder="其他备注信息"></el-input>
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
  name: "TriageRecordList",
  data() {
    return {
      pageNumber:1, size:10, total:0, triageData:[],
      statusFilter:'', levelFilter:'', query:'',
      dialogVisible:false, isEdit:false,
      form:{
        oId:null, pId:null, dId:null, tLevel:0,
        tTemperature:null, tBloodPressure:'', tHeartRate:null, tWeight:null,
        tChiefComplaint:'', tNote:''
      },
      rules:{
        oId:[{required:true,message:"请输入订单ID",trigger:"blur"},{type:'number',message:"请输入数字",trigger:"blur"}],
        pId:[{required:true,message:"请输入患者ID",trigger:"blur"},{type:'number',message:"请输入数字",trigger:"blur"}],
        dId:[{required:true,message:"请输入医生ID",trigger:"blur"},{type:'number',message:"请输入数字",trigger:"blur"}]
      }
    };
  },
  methods: {
    loadData() {
      const params = {
        pageNumber:this.pageNumber, size:this.size,
        status:this.statusFilter||null,
        level:this.levelFilter||null,
        query:this.query||null
      };
      request.get("triage/findAll", { params })
        .then(res => {
          const d=res.data.data;
          this.triageData=d.records||[];
          this.total=d.total||0;
        });
    },
    openAddDialog() {
      this.isEdit=false;
      this.form={oId:null,pId:null,dId:null,tLevel:0,tTemperature:null,tBloodPressure:'',tHeartRate:null,tWeight:null,tChiefComplaint:'',tNote:''};
      this.dialogVisible=true;
      this.$nextTick(()=>{if(this.$refs.form)this.$refs.form.clearValidate();});
    },
    openEditDialog(row) {
      this.isEdit=true;
      this.form={...row};
      this.dialogVisible=true;
      this.$nextTick(()=>{if(this.$refs.form)this.$refs.form.clearValidate();});
    },
    submitForm() {
      this.$refs.form.validate(valid=>{
        if(!valid) return;
        // API: POST triage/create — 需要后端实现
        // 编辑复用同一接口，携带tId进行更新
        request.post("triage/create", this.form).then(res=>{
          if(res.data.status===200){
            this.$message.success(this.isEdit?"更新成功":"新增成功");
            this.dialogVisible=false;
            this.loadData();
          } else {
            this.$message.error(res.data.msg||"操作失败");
          }
        });
      });
    }
  },
  created() { this.loadData(); }
};
</script>
<style scoped>
.toolbar { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px; }
.toolbar-left { display:flex; align-items:center; gap:8px; flex-wrap:wrap; }
.toolbar-right { display:flex; align-items:center; gap:8px; }
.search-input { width:220px; }
.filter-select { width:130px; }
.total-tag { margin-left:8px; }
.vital-sign { display:inline-block; margin-right:10px; font-size:12px; color:#606266; }
.vital-sign i { margin-right:2px; color:#909399; }
</style>
