<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-s-finance"></i> 收费管理</span>
    </div>

    <!-- 筛选栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-select v-model="payFilter" placeholder="缴费状态" size="small" class="filter-select" @change="loadOrders" clearable>
          <el-option label="全部" value=""></el-option>
          <el-option label="待缴费" value="0"></el-option>
          <el-option label="已缴费" value="1"></el-option>
        </el-select>
        <el-input v-model="query" placeholder="订单ID/患者ID" size="small" class="search-input" clearable @keyup.enter.native="loadOrders">
          <el-button slot="append" icon="el-icon-search" @click="loadOrders"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
      </div>
    </div>

    <el-table :data="orderData" stripe border highlight-current-row style="width:100%">
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
      <el-table-column prop="oId" label="订单号" width="80" align="center"></el-table-column>
      <el-table-column prop="pId" label="患者ID" width="65" align="center"></el-table-column>
      <el-table-column prop="dId" label="医生ID" width="65" align="center"></el-table-column>
      <el-table-column prop="oStart" label="挂号时间" min-width="155"></el-table-column>
      <el-table-column label="费用明细" min-width="180">
        <template slot-scope="s">
          <div class="fee-row"><span class="fee-label">挂号</span><span class="fee-value">¥{{ s.row.oRegistrationFee || '0' }}</span></div>
          <div class="fee-row"><span class="fee-label">药品</span><span class="fee-value">¥{{ s.row.oDrugFee || '0' }}</span></div>
          <div class="fee-row"><span class="fee-label">检查</span><span class="fee-value">¥{{ s.row.oCheckFee || '0' }}</span></div>
          <el-divider style="margin:4px 0;"></el-divider>
          <div class="fee-row"><span class="fee-label total">合计</span><span class="fee-total">¥{{ s.row.oTotalPrice || '0' }}</span></div>
        </template>
      </el-table-column>
      <el-table-column label="支付信息" width="140" align="center">
        <template slot-scope="s">
          <div v-if="s.row.oPaymentMethod">
            <el-tag size="mini" type="info">{{ s.row.oPaymentMethod }}</el-tag>
            <div class="pay-detail">医保¥{{ s.row.oInsuranceCovered || '0' }} 自付¥{{ s.row.oSelfPay || '0' }}</div>
          </div>
          <span v-else class="no-data">---</span>
        </template>
      </el-table-column>
      <el-table-column label="缴费状态" width="85" align="center">
        <template slot-scope="s">
          <el-tag v-if="s.row.oPriceState === 1" type="success" size="small">已缴费</el-tag>
          <el-tag v-else type="danger" size="small">待缴费</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="130" fixed="right" align="center">
        <template slot-scope="s">
          <el-button type="primary" size="mini" icon="el-icon-document" circle @click="viewBilling(s.row.oId)" title="收费记录"></el-button>
          <el-button v-if="s.row.oPriceState === 1" type="warning" size="mini" icon="el-icon-refund" circle @click="refundDialog(s.row)" title="退款"></el-button>
          <el-button v-else type="success" size="mini" icon="el-icon-money" circle @click="openPaymentDlg(s.row)" title="收费"></el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="s=>{size=s;loadOrders()}" @current-change="p=>{pageNumber=p;loadOrders()}" background
      layout="total, sizes, prev, pager, next, jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[5,10,20,50]" :total="total" style="margin-top:15px;">
    </el-pagination>

    <!-- 收费对话框 -->
    <el-dialog title="收费处理" :visible.sync="paymentDlgVisible" width="480px">
      <el-form :model="paymentForm" label-width="110px" size="small">
        <el-form-item label="订单编号"><el-tag>{{ paymentForm.oId }}</el-tag></el-form-item>
        <el-row :gutter="10">
          <el-col :span="8"><el-form-item label="挂号费"><el-tag type="info">¥{{ paymentForm.oRegistrationFee || '0' }}</el-tag></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="药费"><el-tag type="warning">¥{{ paymentForm.oDrugFee || '0' }}</el-tag></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="检查费"><el-tag type="success">¥{{ paymentForm.oCheckFee || '0' }}</el-tag></el-form-item></el-col>
        </el-row>
        <el-form-item label="合计"><span class="total-amount">¥{{ totalForPayment }}</span></el-form-item>
        <el-divider></el-divider>
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-select v-model="paymentForm.paymentMethod" placeholder="选择支付方式" style="width:100%">
            <el-option v-for="m in ['现金','微信','支付宝','银行卡','医保']" :key="m" :label="m" :value="m"></el-option>
          </el-select>
        </el-form-item>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="医保报销">
              <el-input-number v-model="paymentForm.insuranceCovered" :min="0" :max="Number(totalForPayment)" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="自付金额">
              <el-input :value="selfPayAmount" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="发票号">
          <el-input v-model="paymentForm.invoiceNo" placeholder="INV-自动生成"></el-input>
        </el-form-item>
        <el-form-item label="操作员">
          <el-input v-model="paymentForm.operator" placeholder="收费员姓名"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="paymentDlgVisible=false">取消</el-button>
        <el-button type="primary" @click="processPayment">确认收费</el-button>
      </div>
    </el-dialog>

    <!-- 收费记录对话框 -->
    <el-dialog title="收费明细" :visible.sync="billingDlgVisible" width="680px">
      <el-table :data="billingData" border stripe size="small" style="width:100%">
        <el-table-column prop="brId" label="流水号" width="70" align="center"></el-table-column>
        <el-table-column prop="brType" label="收费类型" width="100"></el-table-column>
        <el-table-column label="金额" width="90" align="center">
          <template slot-scope="s">¥{{ s.row.brAmount }}</template>
        </el-table-column>
        <el-table-column prop="brPaymentMethod" label="支付方式" width="85"></el-table-column>
        <el-table-column prop="brInvoiceNo" label="发票号" width="140"></el-table-column>
        <el-table-column prop="brPayTime" label="收费时间" min-width="155"></el-table-column>
        <el-table-column prop="brOperator" label="操作员" width="80" align="center"></el-table-column>
        <el-table-column prop="brNote" label="备注" min-width="100" show-overflow-tooltip></el-table-column>
      </el-table>
      <div v-if="billingData.length===0" class="empty-state">暂无收费记录</div>
    </el-dialog>

    <!-- 退款确认对话框 -->
    <el-dialog title="退款确认" :visible.sync="refundDlgVisible" width="400px">
      <el-alert title="退款后该订单将被标记为已退款状态，相关药品库存将回补。是否继续？" type="warning" :closable="false" show-icon style="margin-bottom:15px;"></el-alert>
      <el-form :model="refundForm" label-width="80px" size="small">
        <el-form-item label="订单号"><el-tag>{{ refundForm.oId }}</el-tag></el-form-item>
        <el-form-item label="原金额">¥{{ refundForm.total }}</el-form-item>
        <el-form-item label="退款原因">
          <el-input v-model="refundForm.reason" type="textarea" :rows="3" placeholder="请输入退款原因"></el-input>
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="refundForm.operator" placeholder="收费员姓名"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="refundDlgVisible=false">取消</el-button>
        <el-button type="danger" @click="processRefund">确认退款</el-button>
      </div>
    </el-dialog>

    <!-- 日结统计 -->
    <el-card shadow="hover" style="margin-top:20px;">
      <div slot="header">
        <span><i class="el-icon-s-order"></i> 日结统计</span>
        <el-button size="mini" type="primary" style="float:right;" @click="loadSettlement">刷新</el-button>
      </div>
      <el-descriptions :column="5" border size="small">
        <el-descriptions-item label="今日收费笔数">{{ settlementData.count || 0 }}</el-descriptions-item>
        <el-descriptions-item label="现金">{{ settlementData.cash || 0 }}</el-descriptions-item>
        <el-descriptions-item label="微信">{{ settlementData.wechat || 0 }}</el-descriptions-item>
        <el-descriptions-item label="支付宝">{{ settlementData.alipay || 0 }}</el-descriptions-item>
        <el-descriptions-item label="银行卡/医保">{{ settlementData.card || 0 }}</el-descriptions-item>
        <el-descriptions-item label="医保报销总额">¥{{ settlementData.insuranceTotal || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="自付总额">¥{{ settlementData.selfPayTotal || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="实收总额" :span="2">
          <span style="font-weight:700;font-size:16px;color:#E6A23C;">¥{{ settlementData.realIncome || '0.00' }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "RechargeList",
  data() {
    return {
      pageNumber:1, size:10, query:'', payFilter:'',
      orderData:[], total:0,
      // 收费
      paymentDlgVisible:false,
      paymentForm:{oId:null,oRegistrationFee:0,oDrugFee:0,oCheckFee:0,oTotalPrice:0,paymentMethod:'',insuranceCovered:0,invoiceNo:'',operator:''},
      // 收费记录浏览
      billingDlgVisible:false, billingData:[],
      // 退款
      refundDlgVisible:false,
      refundForm:{oId:null,total:0,reason:'',operator:''},
      // 日结
      settlementData:{count:0,cash:0,wechat:0,alipay:0,card:0,insuranceTotal:0,selfPayTotal:0,realIncome:0}
    };
  },
  computed: {
    totalForPayment() {
      return (parseFloat(this.paymentForm.oRegistrationFee||0) + parseFloat(this.paymentForm.oDrugFee||0) + parseFloat(this.paymentForm.oCheckFee||0)).toFixed(2);
    },
    selfPayAmount() {
      return Math.max(0, parseFloat(this.totalForPayment) - parseFloat(this.paymentForm.insuranceCovered||0)).toFixed(2);
    }
  },
  methods: {
    loadOrders() {
      request.get("admin/findAllOrders", {
        params: { pageNumber:this.pageNumber, size:this.size, query:this.query||null }
      }).then(res => {
        const d = res.data.data;
        this.orderData = d.records || d.orders || [];
        this.total = d.total || 0;
      });
    },
    openPaymentDlg(row) {
      this.paymentForm = {
        oId: row.oId,
        oRegistrationFee: row.oRegistrationFee||0,
        oDrugFee: row.oDrugFee || row.oTotalPrice || 0,
        oCheckFee: row.oCheckFee || 0,
        oTotalPrice: row.oTotalPrice||0,
        paymentMethod: '',
        insuranceCovered: 0,
        invoiceNo: "INV-"+Date.now(),
        operator: ''
      };
      this.paymentDlgVisible = true;
    },
    processPayment() {
      if (!this.paymentForm.paymentMethod) return this.$message.warning("请选择支付方式");
      const total = parseFloat(this.totalForPayment);
      const selfPay = parseFloat(this.selfPayAmount);
      request.post("order/processPayment", null, {
        params: {
          oId: this.paymentForm.oId,
          paymentMethod: this.paymentForm.paymentMethod,
          invoiceNo: this.paymentForm.invoiceNo||"INV-"+Date.now(),
          insuranceCovered: this.paymentForm.insuranceCovered||0,
          selfPay: selfPay,
          operator: this.paymentForm.operator||"收费员"
        }
      }).then(res => {
        if (res.data.status!==200) return this.$message.error("收费失败");
        this.paymentDlgVisible = false;
        this.loadOrders();
        this.loadSettlement();
        this.$message.success("收费成功！");
      }).catch(() => this.$message.error("收费请求失败"));
    },
    // 查看收费记录
    viewBilling(oId) {
      // API: GET billing/findByOrder
      request.get("billing/findByOrder", {params:{oId}}).then(res=>{
        this.billingData = res.data.data || [];
        this.billingDlgVisible = true;
      }).catch(()=>this.$message.error("获取收费记录失败"));
    },
    // 退款对话框
    refundDialog(row) {
      this.refundForm = {
        oId: row.oId,
        total: row.oTotalPrice || 0,
        reason: '',
        operator: ''
      };
      this.refundDlgVisible = true;
    },
    processRefund() {
      if (!this.refundForm.reason) return this.$message.warning("请填写退款原因");
      if (!this.refundForm.operator) return this.$message.warning("请输入操作人");
      // API: GET order/refund — 需要后端实现
      request.get("order/refund", {
        params: {
          oId: this.refundForm.oId,
          reason: this.refundForm.reason,
          operator: this.refundForm.operator
        }
      }).then(res => {
        if (res.data.status===200) {
          this.refundDlgVisible = false;
          this.loadOrders();
          this.loadSettlement();
          this.$message.success("退款成功！");
        } else {
          this.$message.error(res.data.msg||"退款失败");
        }
      }).catch(() => this.$message.error("退款请求失败"));
    },
    // 日结统计
    loadSettlement() {
      // API: GET billing/dailySettlement — 需要后端实现
      request.get("billing/dailySettlement").then(res => {
        if (res.data.status===200) {
          this.settlementData = res.data.data || this.settlementData;
        }
      }).catch(() => {
        // 降级：从收费记录简单统计
        this.settlementData.count = this.orderData.filter(o => o.oPriceState===1).length;
      });
    }
  },
  created() {
    this.loadOrders();
    this.loadSettlement();
  }
};
</script>
<style scoped>
.toolbar { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px; }
.toolbar-left { display:flex; align-items:center; gap:8px; }
.toolbar-right { display:flex; align-items:center; gap:8px; }
.search-input { width:220px; }
.filter-select { width:120px; }
.total-tag { margin-left:8px; }
.expand-body { padding:15px; background:#fafafa; }
.fee-row { display:flex; justify-content:space-between; padding:1px 0; }
.fee-label { color:#999; font-size:12px; }
.fee-label.total { font-weight:600; color:#606266; }
.fee-value { font-size:12px; }
.fee-total { font-weight:700; color:#E6A23C; }
.pay-detail { font-size:11px; color:#999; margin-top:2px; }
.no-data { color:#ccc; }
.total-amount { font-size:20px; font-weight:700; color:#E6A23C; }
.empty-state { text-align:center; padding:30px; color:#999; }
</style>

