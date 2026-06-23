<template>
  <div class="inventory-center">
    <el-row :gutter="12" class="summary-row">
      <el-col :span="8">
        <div class="summary summary-danger">
          <i class="el-icon-warning-outline"></i>
          <div><strong>{{ lowStock.length }}</strong><span>低库存药品</span></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="summary summary-warning">
          <i class="el-icon-time"></i>
          <div><strong>{{ expiringBatches.length }}</strong><span>近 {{ expiryDays }} 天到期批次</span></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="summary summary-dark">
          <i class="el-icon-circle-close"></i>
          <div><strong>{{ expiredCount }}</strong><span>已过期批次</span></div>
        </div>
      </el-col>
    </el-row>

    <el-card class="workspace">
      <div slot="header" class="header-line">
        <span><i class="el-icon-box"></i> 药品库存中心</span>
        <div>
          <el-button size="small" icon="el-icon-refresh" @click="refreshAll">刷新</el-button>
          <el-button size="small" type="warning" icon="el-icon-sort" @click="openAdjust">库存调整</el-button>
          <el-button size="small" type="primary" icon="el-icon-plus" @click="receiveVisible=true">采购入库</el-button>
        </div>
      </div>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="库存预警" name="alerts">
          <el-row :gutter="16">
            <el-col :span="12">
              <h3>低库存药品</h3>
              <el-table :data="lowStock" border stripe size="small" height="390" style="width:100%">
                <el-table-column prop="drId" label="编号" width="76"></el-table-column>
                <el-table-column prop="drName" label="药品"></el-table-column>
                <el-table-column prop="drSubtype" label="细分类" width="90"></el-table-column>
                <el-table-column prop="drNumber" label="现存" width="62">
                  <template slot-scope="s"><strong class="danger">{{ s.row.drNumber }}</strong></template>
                </el-table-column>
                <el-table-column prop="drMinStock" label="下限" width="62"></el-table-column>
              </el-table>
            </el-col>
            <el-col :span="12">
              <div class="table-title">
                <h3>近效期与过期批次</h3>
                <el-select v-model="expiryDays" size="mini" @change="loadDashboard">
                  <el-option :value="30" label="30天"></el-option>
                  <el-option :value="90" label="90天"></el-option>
                  <el-option :value="180" label="180天"></el-option>
                </el-select>
              </div>
              <el-table :data="expiringBatches" border stripe size="small" height="390" style="width:100%">
                <el-table-column prop="drId" label="药品" width="76"></el-table-column>
                <el-table-column prop="dbBatchNo" label="批号"></el-table-column>
                <el-table-column prop="dbExpireDate" label="有效期" width="100"></el-table-column>
                <el-table-column prop="dbQuantity" label="库存" width="62"></el-table-column>
                <el-table-column label="状态" width="76">
                  <template slot-scope="s">
                    <el-tag :type="isExpired(s.row.dbExpireDate)?'danger':'warning'" size="mini">
                      {{ isExpired(s.row.dbExpireDate)?'已过期':'近效期' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="72" align="center">
                  <template slot-scope="s">
                    <el-button v-if="isExpired(s.row.dbExpireDate)" type="danger" size="mini" @click="writeOffBatch(s.row)">报损</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-col>
          </el-row>
        </el-tab-pane>
        <el-tab-pane label="药品盘点" name="count">
          <div class="filter-line">
            <el-input v-model="countQuery" placeholder="搜索药品编号/名称" clearable size="small" style="width:260px" @keyup.enter.native="loadCountData">
              <el-button slot="append" icon="el-icon-search" @click="loadCountData"></el-button>
            </el-input>
            <el-button size="small" type="primary" @click="loadCountData"><i class="el-icon-refresh"></i> 加载待盘点</el-button>
          </div>
          <el-alert title="输入实际盘点数量，系统自动计算差异并生成调整记录。盘盈为正数，盘亏为负数。" type="info" :closable="false" style="margin-bottom:12px;"></el-alert>
          <el-table :data="countData" border stripe size="small" style="width:100%">
            <el-table-column prop="drId" label="编号" width="76"></el-table-column>
            <el-table-column prop="drName" label="药品名称" width="140"></el-table-column>
            <el-table-column prop="drSpec" label="规格" width="120"></el-table-column>
            <el-table-column prop="drUnit" label="单位" width="55"></el-table-column>
            <el-table-column prop="drNumber" label="系统库存" width="80" align="center">
              <template slot-scope="s"><strong>{{ s.row.drNumber }}</strong></template>
            </el-table-column>
            <el-table-column label="盘点数量" width="120" align="center">
              <template slot-scope="s">
                <el-input-number v-model="s.row.countActual" :min="0" size="mini" controls-position="right" style="width:100px"></el-input-number>
              </template>
            </el-table-column>
            <el-table-column label="差异" width="80" align="center">
              <template slot-scope="s">
                <span v-if="s.row.countActual !== null && s.row.countActual !== undefined" :class="(s.row.countActual - s.row.drNumber) > 0 ? 'success' : (s.row.countActual - s.row.drNumber) < 0 ? 'danger' : ''">
                  {{ (s.row.countActual - s.row.drNumber) > 0 ? '+' : '' }}{{ s.row.countActual - s.row.drNumber || 0 }}
                </span>
                <span v-else class="no-data">-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="90" align="center">
              <template slot-scope="s">
                <el-button v-if="s.row.countActual !== null && s.row.countActual !== s.row.drNumber" type="warning" size="mini" @click="submitCount(s.row)">提交盘点</el-button>
                <el-tag v-else type="info" size="mini">无差异</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination :current-page="countPage" :page-size="countSize" :total="countTotal"
            layout="total,prev,pager,next" @current-change="p=>{countPage=p;loadCountData()}" style="margin-top:12px;"></el-pagination>
        </el-tab-pane>
        <el-tab-pane label="库存流水" name="transactions">
          <div class="filter-line">
            <el-input v-model="txDrugId" placeholder="输入药品编号" clearable size="small" style="width:220px" @keyup.enter.native="loadTransactions">
              <el-button slot="append" icon="el-icon-search" @click="loadTransactions"></el-button>
            </el-input>
          </div>
          <el-table :data="transactions" border stripe size="small" style="width:100%">
            <el-table-column prop="itId" label="流水号" width="76"></el-table-column>
            <el-table-column prop="drId" label="药品" width="80"></el-table-column>
            <el-table-column prop="dbId" label="批次ID" width="76"></el-table-column>
            <el-table-column prop="itType" label="业务类型" width="82"></el-table-column>
            <el-table-column prop="itQuantity" label="变动" width="70">
              <template slot-scope="s"><span :class="s.row.itQuantity>0?'success':'danger'">{{ s.row.itQuantity>0?'+':'' }}{{ s.row.itQuantity }}</span></template>
            </el-table-column>
            <el-table-column label="库存变化" width="105">
              <template slot-scope="s">{{ s.row.itBeforeQuantity }} → {{ s.row.itAfterQuantity }}</template>
            </el-table-column>
            <el-table-column prop="itReference" label="业务单号" width="130"></el-table-column>
            <el-table-column prop="itOperator" label="操作人" width="90"></el-table-column>
            <el-table-column prop="itNote" label="备注" min-width="150" show-overflow-tooltip></el-table-column>
            <el-table-column prop="itCreateTime" label="时间" width="160"></el-table-column>
          </el-table>
          <el-pagination :current-page="txPage" :page-size="txSize" :total="txTotal"
            layout="total,prev,pager,next" @current-change="p=>{txPage=p;loadTransactions()}"></el-pagination>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog title="采购入库" :visible.sync="receiveVisible" width="560px">
      <el-form :model="receiveForm" :rules="receiveRules" ref="receiveForm" label-width="88px" size="small">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="药品编号" prop="drId"><el-input v-model="receiveForm.drId"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="生产批号" prop="dbBatchNo"><el-input v-model="receiveForm.dbBatchNo"></el-input></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="有效期" prop="dbExpireDate"><el-date-picker v-model="receiveForm.dbExpireDate" value-format="yyyy-MM-dd" type="date" style="width:100%"></el-date-picker></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="入库数量" prop="dbQuantity"><el-input-number v-model="receiveForm.dbQuantity" :min="1" style="width:100%"></el-input-number></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="采购单价"><el-input-number v-model="receiveForm.dbPurchasePrice" :min="0" :precision="2" style="width:100%"></el-input-number></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="供应商"><el-input v-model="receiveForm.dbSupplier"></el-input></el-form-item></el-col>
        </el-row>
        <el-form-item label="操作人" prop="operator"><el-input v-model="receiveForm.operator"></el-input></el-form-item>
        <el-form-item label="备注"><el-input v-model="receiveForm.note" type="textarea"></el-input></el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="receiveVisible=false">取消</el-button><el-button type="primary" @click="submitReceive">确认入库</el-button></div>
    </el-dialog>

    <el-dialog title="库存盘点调整" :visible.sync="adjustVisible" width="500px">
      <el-alert title="盘盈输入正数，盘亏或报损输入负数；选择批次后会同步调整批次库存。" type="info" :closable="false"></el-alert>
      <el-form :model="adjustForm" :rules="adjustRules" ref="adjustForm" label-width="88px" size="small" style="margin-top:16px">
        <el-form-item label="药品编号" prop="drId"><el-input v-model="adjustForm.drId"></el-input></el-form-item>
        <el-form-item label="批次ID"><el-input-number v-model="adjustForm.dbId" :min="1" controls-position="right"></el-input-number></el-form-item>
        <el-form-item label="调整类型" prop="type"><el-select v-model="adjustForm.type"><el-option label="盘盈" value="盘盈"></el-option><el-option label="盘亏" value="盘亏"></el-option><el-option label="报损" value="报损"></el-option></el-select></el-form-item>
        <el-form-item label="变动数量" prop="quantity"><el-input-number v-model="adjustForm.quantity" :min="-99999" :max="99999"></el-input-number></el-form-item>
        <el-form-item label="操作人" prop="operator"><el-input v-model="adjustForm.operator"></el-input></el-form-item>
        <el-form-item label="原因" prop="note"><el-input v-model="adjustForm.note" type="textarea"></el-input></el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="adjustVisible=false">取消</el-button><el-button type="primary" @click="submitAdjust">确认调整</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import request from "@/utils/request.js";
export default {
  name: "InventoryCenter",
  data() {
    return {
      activeTab:"alerts", expiryDays:90, lowStock:[], expiringBatches:[], expiredCount:0,
      transactions:[], txDrugId:"", txPage:1, txSize:10, txTotal:0,
      receiveVisible:false, adjustVisible:false,
      receiveForm:{drId:"",dbBatchNo:"",dbExpireDate:"",dbQuantity:1,dbPurchasePrice:0,dbSupplier:"",operator:"管理员",note:""},
      adjustForm:{drId:"",dbId:null,type:"盘盈",quantity:1,operator:"管理员",note:""},
      // 盘点
      countQuery:'', countPage:1, countSize:10, countTotal:0, countData:[],
      receiveRules:{
        drId:[{required:true,message:"请输入药品编号",trigger:"blur"}],
        dbBatchNo:[{required:true,message:"请输入生产批号",trigger:"blur"}],
        dbExpireDate:[{required:true,message:"请选择有效期",trigger:"change"}],
        dbQuantity:[{required:true,message:"请输入数量",trigger:"change"}],
        operator:[{required:true,message:"请输入操作人",trigger:"blur"}]
      },
      adjustRules:{
        drId:[{required:true,message:"请输入药品编号",trigger:"blur"}],
        type:[{required:true,message:"请选择类型",trigger:"change"}],
        quantity:[{required:true,message:"请输入变动数量",trigger:"change"}],
        operator:[{required:true,message:"请输入操作人",trigger:"blur"}],
        note:[{required:true,message:"请填写调整原因",trigger:"blur"}]
      }
    };
  },
  watch:{activeTab(v){if(v==="transactions")this.loadTransactions();else if(v==="count")this.loadCountData();}},
  methods:{
    isExpired(date){return date && new Date(date).getTime()<new Date().setHours(0,0,0,0);},
    loadDashboard(){
      request.get("inventory/dashboard",{params:{expiryDays:this.expiryDays}}).then(r=>{
        if(r.data.status!==200)return;
        const d=r.data.data||{};this.lowStock=d.lowStock||[];this.expiringBatches=d.expiringBatches||[];this.expiredCount=d.expiredCount||0;
      });
    },
    loadTransactions(){
      request.get("inventory/transactions",{params:{pageNumber:this.txPage,size:this.txSize,drId:this.txDrugId||null}}).then(r=>{
        const d=r.data.data||{};this.transactions=d.records||[];this.txTotal=d.total||0;
      });
    },
    loadCountData() {
      request.get("drug/findAllDrugs",{params:{pageNumber:this.countPage,size:this.countSize,query:this.countQuery,typeFilter:0}}).then(r=>{
        if(r.data.status!==200)return;
        const d=r.data.data||{};const list=d.drugs||d.records||[];
        list.forEach(function(item){item.countActual=item.drNumber;});
        this.countData=list;this.countTotal=d.total||0;
      });
    },
    submitCount(row) {
      const diff = row.countActual - row.drNumber;
      if (diff === 0) return;
      const type = diff > 0 ? '盘盈' : '盘亏';
      this.$confirm(`药品 ${row.drId} ${row.drName}：系统库存 ${row.drNumber}，盘点 ${row.countActual}，差异 ${diff>0?'+':''}${diff}，确认提交盘点？`,'盘点确认',{type:'warning'}).then(()=>{
        request.post("inventory/adjust",{drId:row.drId,dbId:null,quantity:diff,type:type,operator:'管理员',note:`药品盘点：系统${row.drNumber}，实际${row.countActual}`}).then(r=>{
          if(r.data.status!==200)return this.$message.error(r.data.msg||'盘点提交失败');
          this.$message.success(`盘点成功：${row.drName} ${diff>0?'+':''}${diff}`);
          this.loadCountData();
        });
      }).catch(()=>{});
    },
    refreshAll(){this.loadDashboard();if(this.activeTab==="transactions")this.loadTransactions();},
    openAdjust(){this.adjustVisible=true;},
    writeOffBatch(row){
      this.$confirm(`确定报损批次 ${row.dbBatchNo}（${row.drId}）的 ${row.dbQuantity} 件库存？此操作不可撤销。`,"报损确认",{type:"warning",confirmButtonText:"确定报损",cancelButtonText:"取消"}).then(()=>{
        request.post("inventory/writeOff",{dbId:row.dbId,operator:"管理员",note:"过期报损"}).then(r=>{
          if(r.data.status!==200)return this.$message.error(r.data.msg||"报损失败");
          this.$message.success("报损成功");this.loadDashboard();
        });
      }).catch(()=>{});
    },
    submitReceive(){
      this.$refs.receiveForm.validate(ok=>{if(!ok)return;request.post("inventory/receive",this.receiveForm).then(r=>{
        if(r.data.status!==200)return this.$message.error(r.data.msg||"入库失败");
        this.$message.success("入库成功");this.receiveVisible=false;this.loadDashboard();
      });});
    },
    submitAdjust(){
      this.$refs.adjustForm.validate(ok=>{if(!ok)return;if(!this.adjustForm.quantity)return this.$message.warning("变动数量不能为0");
        request.post("inventory/adjust",this.adjustForm).then(r=>{
          if(r.data.status!==200)return this.$message.error(r.data.msg||"调整失败");
          this.$message.success("库存调整成功");this.adjustVisible=false;this.refreshAll();
        });
      });
    }
  },
  created(){this.loadDashboard();}
};
</script>

<style scoped>
.summary-row{margin-bottom:12px}.summary{height:86px;display:flex;align-items:center;padding:0 22px;color:#fff;border-radius:6px}.summary i{font-size:32px;margin-right:18px}.summary strong{display:block;font-size:25px}.summary span{font-size:13px;opacity:.9}.summary-danger{background:#c45656}.summary-warning{background:#b88230}.summary-dark{background:#52616b}.header-line,.table-title,.filter-line{display:flex;align-items:center;justify-content:space-between}.workspace{border-radius:6px}h3{font-size:15px;margin:8px 0 12px}.danger{color:#f56c6c}.success{color:#67c23a}.filter-line{justify-content:flex-start;margin-bottom:12px}.el-pagination{margin-top:14px}
</style>

