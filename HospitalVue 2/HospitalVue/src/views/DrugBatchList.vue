<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-box"></i> 药品批次管理</span>
      <el-button type="primary" size="small" style="float:right;" @click="receiveVisible = true">
        <i class="el-icon-plus"></i> 采购入库
      </el-button>
    </div>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query" placeholder="搜索批号/药品ID/供应商" size="small" class="search-input" clearable @keyup.enter.native="loadData">
          <el-button slot="append" icon="el-icon-search" @click="loadData"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
      </div>
    </div>
    <el-table :data="batchData" border stripe style="width:100%">
      <el-table-column prop="dbId" label="编号" width="60" align="center"></el-table-column>
      <el-table-column prop="drId" label="药品ID" width="70" align="center"></el-table-column>
      <el-table-column prop="dbBatchNo" label="批号" width="120"></el-table-column>
      <el-table-column prop="dbExpireDate" label="有效期" width="95" align="center"></el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="s">
          <el-tag v-if="s.row.dbExpireDate && new Date(s.row.dbExpireDate) > new Date(Date.now()+90*86400000)" type="success" size="mini">正常</el-tag>
          <el-tag v-else-if="s.row.dbExpireDate && new Date(s.row.dbExpireDate) > new Date()" type="warning" size="mini">即将过期</el-tag>
          <el-tag v-else type="danger" size="mini">已过期</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="dbQuantity" label="库存" width="60" align="center"></el-table-column>
      <el-table-column prop="dbPurchasePrice" label="进价" width="70" align="center">
        <template slot-scope="s">¥{{ s.row.dbPurchasePrice }}</template>
      </el-table-column>
      <el-table-column prop="dbSupplier" label="供应商" width="120" show-overflow-tooltip></el-table-column>
      <el-table-column prop="dbCreateTime" label="入库时间" width="155"></el-table-column>
    </el-table>
    <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}"
      :current-page="pageNumber" :page-sizes="[10,20,30]" :page-size="size"
      layout="total,sizes,prev,pager,next,jumper" :total="total" style="margin-top:15px;">
    </el-pagination>

    <!-- 采购入库弹窗 -->
    <el-dialog title="采购入库" :visible.sync="receiveVisible" width="560px">
      <el-alert title="新批次入库会同时增加药品总库存。请确保药品编号正确。" type="info" :closable="false" style="margin-bottom:16px;"></el-alert>
      <el-form :model="form" :rules="rules" ref="form" label-width="90px" size="small">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="药品" prop="drId">
              <el-select v-model="form.drId" filterable placeholder="搜索药品编号" style="width:100%">
                <el-option v-for="d in drugList" :key="d.drId" :label="d.drId + ' - ' + d.drName" :value="d.drId"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生产批号" prop="dbBatchNo">
              <el-input v-model="form.dbBatchNo" placeholder="如 20260601"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="有效期" prop="dbExpireDate">
              <el-date-picker v-model="form.dbExpireDate" value-format="yyyy-MM-dd" type="date" placeholder="选择日期" style="width:100%"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="入库数量" prop="dbQuantity">
              <el-input-number v-model="form.dbQuantity" :min="1" :max="99999" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="采购单价">
              <el-input-number v-model="form.dbPurchasePrice" :min="0" :precision="2" :step="0.1" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="供应商">
              <el-input v-model="form.dbSupplier" placeholder="如 XX医药公司"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作人" prop="operator">
              <el-input v-model="form.operator"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.note" type="textarea" :rows="2" placeholder="入库备注（可选）"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="receiveVisible=false">取消</el-button>
        <el-button type="primary" @click="submitReceive" :loading="submitting">确认入库</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "DrugBatchList",
  data() {
    return {
      pageNumber:1, size:10, query:'', batchData:[], total:0,
      receiveVisible:false, submitting:false, drugList:[],
      form:{ drId:'', dbBatchNo:'', dbExpireDate:'', dbQuantity:1, dbPurchasePrice:0, dbSupplier:'', operator:'管理员', note:'' },
      rules:{
        drId:[{required:true,message:'请选择药品',trigger:'change'}],
        dbBatchNo:[{required:true,message:'请输入生产批号',trigger:'blur'}],
        dbExpireDate:[{required:true,message:'请选择有效期',trigger:'change'}],
        dbQuantity:[{required:true,message:'请输入入库数量',trigger:'change'}],
        operator:[{required:true,message:'请输入操作人',trigger:'blur'}]
      }
    };
  },
  methods: {
    loadData() {
      request.get("drugBatch/findAll", { params: { pageNumber:this.pageNumber, size:this.size, query:this.query||null } })
        .then(res => { const d=res.data.data; this.batchData=d.records||[]; this.total=d.total||0; });
    },
    loadDrugs() {
      request.get("drug/findAllDrugs", { params: { pageNumber:1, size:999, query:'' } })
        .then(res => { this.drugList = res.data.data.drugs || res.data.data.records || []; });
    },
    submitReceive() {
      this.$refs.form.validate(ok => {
        if (!ok) return;
        this.submitting = true;
        request.post("inventory/receive", this.form).then(res => {
          this.submitting = false;
          if (res.data.status !== 200) return this.$message.error(res.data.msg || '入库失败');
          this.$message.success(`入库成功！药品 ${this.form.drId} 批次 ${this.form.dbBatchNo} +${this.form.dbQuantity} 件`);
          this.receiveVisible = false;
          this.loadData();
        }).catch(() => { this.submitting = false; });
      });
    }
  },
  created() { this.loadData(); this.loadDrugs(); }
};
</script>
<style scoped>
.toolbar { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px; }
.toolbar-left { display:flex; align-items:center; gap:8px; }
.toolbar-right { display:flex; align-items:center; gap:8px; }
.search-input { width:280px; }
</style>


