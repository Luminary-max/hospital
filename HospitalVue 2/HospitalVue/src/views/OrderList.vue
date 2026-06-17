<template>
  <el-card>
    <div slot="header">
      <i class="el-icon-postcard"></i> 门诊挂号管理
    </div>

    <div class="toolbar">
      <el-input v-model="query" placeholder="输入患者ID搜索" clearable size="small" class="search-input" @keyup.enter.native="requestOrders">
        <el-button slot="append" icon="el-icon-search" @click="requestOrders"></el-button>
      </el-input>
      <el-tag class="total-tag">共 {{ total }} 条</el-tag>
    </div>

    <el-table :data="orderData" stripe border highlight-current-row>
      <el-table-column type="expand">
        <template slot-scope="s">
          <div class="expand-body">
            <el-descriptions :column="4" border size="mini">
              <el-descriptions-item label="诊断/病因" :span="4">{{ s.row.oRecord || '无' }}</el-descriptions-item>
              <el-descriptions-item label="药品明细" :span="2">{{ s.row.oDrug || '无' }}</el-descriptions-item>
              <el-descriptions-item label="检查项目" :span="2">{{ s.row.oCheck || '无' }}</el-descriptions-item>
              <el-descriptions-item label="医生建议" :span="4">{{ s.row.oAdvice || '无' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="oId" label="单号" width="80" align="center"></el-table-column>
      <el-table-column prop="pId" label="患者" width="65" align="center"></el-table-column>
      <el-table-column prop="dId" label="医生" width="75" align="center"></el-table-column>
      <el-table-column prop="oTriage" label="分诊" width="75" align="center">
        <template slot-scope="s">
          <span :class="'tag-' + (s.row.oTriage==='急诊'?'emerg':s.row.oTriage==='专家门诊'?'expert':'normal')">{{ s.row.oTriage || '普通' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="oStart" label="挂号时间" min-width="160"></el-table-column>
      <el-table-column label="费用" width="110" align="center">
        <template slot-scope="s">
          <div class="fee-row"><span class="fee-label">挂号</span><span class="fee-value">¥{{ s.row.oRegistrationFee || '0' }}</span></div>
          <div class="fee-row"><span class="fee-label">合计</span><span class="fee-total">¥{{ s.row.oTotalPrice || '0' }}</span></div>
        </template>
      </el-table-column>
      <el-table-column label="支付" width="120" align="center">
        <template slot-scope="s">
          <div v-if="s.row.oPaymentMethod">
            <el-tag size="mini" type="info">{{ s.row.oPaymentMethod }}</el-tag>
            <div class="pay-detail">医保¥{{ s.row.oInsuranceCovered || '0' }} 自付¥{{ s.row.oSelfPay || '0' }}</div>
          </div>
          <span v-else class="no-data">---</span>
        </template>
      </el-table-column>
      <el-table-column label="缴费" width="85" align="center">
        <template slot-scope="s">
          <el-tag v-if="s.row.oPriceState === 1" type="success" size="small">已缴费</el-tag>
          <el-button v-else-if="s.row.oState === 1" type="danger" size="mini" @click="openPaymentDlg(s.row)">去收费</el-button>
          <span v-else class="no-data">-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="75" align="center">
        <template slot-scope="s">
          <span :class="'status-' + (s.row.oState === 1 && s.row.oPriceState === 1 ? 'done' : s.row.oState === 1 ? 'wait' : 'fail')">
            {{ s.row.oState === 1 && s.row.oPriceState === 1 ? '已完成' : s.row.oState === 1 ? '待缴费' : '未完成' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="95" fixed="right" align="center">
        <template slot-scope="s">
          <el-button type="primary" size="mini" icon="el-icon-document" circle @click="viewBilling(s.row.oId)" title="收费记录"></el-button>
          <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteDialog(s.row.oId)" title="删除"></el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="s=>{size=s;requestOrders()}" @current-change="p=>{pageNumber=p;requestOrders()}" background
      layout="total, sizes, prev, pager, next, jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[5,10,20,50]" :total="total">
    </el-pagination>

    <el-dialog title="收费处理" :visible.sync="paymentDlgVisible" width="460px">
      <el-form :model="paymentForm" label-width="110px" size="small">
        <el-form-item label="订单编号"><el-tag>{{ paymentForm.oId }}</el-tag></el-form-item>
        <el-row :gutter="10">
          <el-col :span="10"><el-form-item label="挂号费"><el-tag type="info">¥{{ paymentForm.oRegistrationFee || '0' }}</el-tag></el-form-item></el-col>
          <el-col :span="14"><el-form-item label="药费+检查费"><el-tag type="warning">¥{{ paymentForm.oTotalPrice || '0' }}</el-tag></el-form-item></el-col>
        </el-row>
        <el-form-item label="合计"><span class="total-amount">¥{{ totalForPayment }}</span></el-form-item>
        <el-divider></el-divider>
        <el-form-item label="支付方式" required>
          <el-select v-model="paymentForm.paymentMethod" placeholder="选择支付方式">
            <el-option v-for="m in ['现金','微信','支付宝','银行卡','医保']" :key="m" :label="m" :value="m"></el-option>
          </el-select>
        </el-form-item>
        <el-row :gutter="10">
          <el-col :span="12"><el-form-item label="医保报销"><el-input-number v-model="paymentForm.insuranceCovered" :min="0" :max="Number(totalForPayment)"></el-input-number></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="自付金额"><el-input :value="selfPayAmount" disabled></el-input></el-form-item></el-col>
        </el-row>
        <el-form-item label="发票号"><el-input v-model="paymentForm.invoiceNo" placeholder="INV-自动生成"></el-input></el-form-item>
        <el-form-item label="操作员"><el-input v-model="paymentForm.operator" placeholder="收费员姓名"></el-input></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="paymentDlgVisible=false">取消</el-button>
        <el-button type="primary" @click="processPayment">确认收费</el-button>
      </div>
    </el-dialog>

    <el-dialog title="收费明细" :visible.sync="billingDlgVisible" width="650px">
      <el-table :data="billingData" border stripe size="small">
        <el-table-column prop="brType" label="收费类型" width="120"></el-table-column>
        <el-table-column prop="brAmount" label="金额" width="100"><template slot-scope="s">¥{{ s.row.brAmount }}</template></el-table-column>
        <el-table-column prop="brPaymentMethod" label="支付方式" width="90"></el-table-column>
        <el-table-column prop="brInvoiceNo" label="发票号" width="140"></el-table-column>
        <el-table-column prop="brPayTime" label="收费时间" width="160"></el-table-column>
        <el-table-column prop="brOperator" label="操作员" width="80"></el-table-column>
      </el-table>
      <div v-if="billingData.length===0" class="empty-state">暂无收费记录</div>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "OrderList",
  data() {
    return {
      pageNumber: 1, size: 10, query: "", orderData: [], total: 0,
      paymentDlgVisible: false,
      paymentForm: { oId: null, oRegistrationFee: 0, oTotalPrice: 0, paymentMethod: "", insuranceCovered: 0, invoiceNo: "", operator: "" },
      billingDlgVisible: false, billingData: [],
    };
  },
  computed: {
    totalForPayment() { return (parseFloat(this.paymentForm.oRegistrationFee||0) + parseFloat(this.paymentForm.oTotalPrice||0)).toFixed(2); },
    selfPayAmount() { return Math.max(0, parseFloat(this.totalForPayment) - parseFloat(this.paymentForm.insuranceCovered||0)).toFixed(2); },
  },
  methods: {
    requestOrders() {
      request.get("admin/findAllOrders", { params: { pageNumber: this.pageNumber, size: this.size, query: this.query } }).then(res => {
        const d = res.data.data; this.orderData = d.records || d.orders || []; this.total = d.total || 0;
      });
    },
    openPaymentDlg(row) {
      this.paymentForm = { oId: row.oId, oRegistrationFee: row.oRegistrationFee||0, oTotalPrice: row.oTotalPrice||0, paymentMethod: "", insuranceCovered: 0, invoiceNo: "INV-"+Date.now(), operator: "" };
      this.paymentDlgVisible = true;
    },
    processPayment() {
      if (!this.paymentForm.paymentMethod) return this.$message.warning("请选择支付方式");
      request.post("order/processPayment", null, { params: { oId: this.paymentForm.oId, paymentMethod: this.paymentForm.paymentMethod, invoiceNo: this.paymentForm.invoiceNo||"INV-"+Date.now(), insuranceCovered: this.paymentForm.insuranceCovered||0, selfPay: parseFloat(this.selfPayAmount), operator: this.paymentForm.operator||"管理员" } }).then(res => {
        if (res.data.status!==200) return this.$message.error("收费失败");
        this.paymentDlgVisible = false; this.requestOrders(); this.$message.success("收费成功！");
      });
    },
    viewBilling(oId) { request.get("billing/findByOrder", {params:{oId}}).then(res=>{ this.billingData=res.data.data||[]; this.billingDlgVisible=true; }); },
    deleteOrder(id) { request.get("admin/deleteOrder",{params:{oId:id}}).then(()=>this.requestOrders()); },
    deleteDialog(id) {
      this.$confirm("此操作将永久删除该挂号信息, 是否继续?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"})
        .then(()=>{this.deleteOrder(id);this.$message.success("删除成功!");}).catch(()=>{this.$message.info("已取消删除");});
    },
  },
  created() { this.requestOrders(); },
};
</script>
<style scoped>
.toolbar { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px; }
.search-input { width:260px; }
.total-tag { margin-left:auto; }
.expand-body { padding:15px; background:#fafafa; }
.fee-row { display:flex; justify-content:space-between; padding:1px 0; }
.fee-label { color:#999; font-size:12px; }
.fee-value { font-size:12px; }
.fee-total { font-weight:700; color:#E6A23C; }
.pay-detail { font-size:11px; color:#999; margin-top:2px; }
.no-data { color:#ccc; }
.total-amount { font-size:20px; font-weight:700; color:#E6A23C; }
.tag-emerg { display:inline-block; background:#fef0f0; color:#f56c6c; padding:2px 8px; border-radius:4px; font-size:12px; }
.tag-expert { display:inline-block; background:#fdf6ec; color:#e6a23c; padding:2px 8px; border-radius:4px; font-size:12px; }
.tag-normal { display:inline-block; background:#ecf5ff; color:#409eff; padding:2px 8px; border-radius:4px; font-size:12px; }
.status-done { display:inline-block; background:#f0f9eb; color:#67c23a; padding:2px 8px; border-radius:4px; font-size:12px; }
.status-wait { display:inline-block; background:#fdf6ec; color:#e6a23c; padding:2px 8px; border-radius:4px; font-size:12px; }
.status-fail { display:inline-block; background:#fef0f0; color:#f56c6c; padding:2px 8px; border-radius:4px; font-size:12px; }
.empty-state { text-align:center; padding:30px; color:#999; }
</style>
