<template>
  <div>
    <el-card>
      <div slot="header">
        <span><i class="el-icon-postcard"></i> 我的挂号</span>
        <el-button type="text" style="float:right;" @click="requestOrder"><i class="el-icon-refresh"></i> 刷新</el-button>
      </div>
      <el-table :data="orderData" stripe border style="width:100%">
        <el-table-column prop="oId" label="单号" width="65" align="center"></el-table-column>
        <el-table-column prop="pName" label="姓名" width="55" align="center"></el-table-column>
        <el-table-column prop="dName" label="医生" width="65" align="center"></el-table-column>
        <el-table-column prop="oStart" label="挂号时间" min-width="155"></el-table-column>
        <el-table-column label="挂号费" width="65" align="center">
          <template slot-scope="s">¥{{ s.row.oRegistrationFee || s.row.oregistrationFee || 0 }}</template>
        </el-table-column>
        <el-table-column label="检查费" width="65" align="center">
          <template slot-scope="s">¥{{ s.row._checkFee || s.row.checkFee || 0 }}</template>
        </el-table-column>
        <el-table-column label="药品费" width="80" align="center">
          <template slot-scope="s">¥{{ s.row.ototalPrice || s.row.oTotalPrice || 0 }}</template>
        </el-table-column>
        <el-table-column label="合计" width="70" align="center">
          <template slot-scope="s">¥{{ (parseFloat(s.row.oRegistrationFee||s.row.oregistrationFee||0) + parseFloat(s.row.ototalPrice||s.row.oTotalPrice||0) + parseFloat(s.row._checkFee||0)).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="缴费" width="100" align="center">
          <template slot-scope="s">
            <el-tag v-if="s.row.oPriceState==1||s.row.opriceState==1" type="success">已缴</el-tag>
            <el-button v-else type="warning" size="mini" @click="openPaymentDlg(s.row)">去缴费</el-button>
          </template>
        </el-table-column>
        <el-table-column label="退费" width="100" align="center">
          <template slot-scope="s">
            <el-button v-if="s.row.oPriceState==1||s.row.opriceState==1" type="danger" size="mini" @click="openRefundDlg(s.row)">申请退费</el-button>
            <span v-else style="color:#ccc;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="s">
            <el-tag v-if="s.row.oState==7||s.row.ostate==7" type="success">已完成</el-tag>
            <el-tag v-else-if="s.row.oPriceState==1||s.row.opriceState==1" type="success">已缴费</el-tag>
            <el-tag v-else-if="s.row.oState>=1||s.row.ostate>=1" type="warning">待缴费</el-tag>
            <el-tag v-else type="danger">未完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报告单" width="90" align="center">
          <template slot-scope="s">
            <el-button v-if="s.row.oPriceState==1||s.row.opriceState==1" type="success" size="mini" @click="seeReport(s.row)">查看</el-button>
            <span v-else style="color:#ccc;">-</span>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="orderData.length===0" style="text-align:center;padding:40px;color:#999;">
        <i class="el-icon-document" style="font-size:48px;"></i><p>暂无挂号记录</p>
      </div>
    </el-card>

    <!-- 缴费对话框 -->
    <el-dialog title="在线缴费" :visible.sync="paymentVisible" width="550px" top="10vh">
      <div v-if="paymentLoading" style="text-align:center;padding:30px;"><i class="el-icon-loading" style="font-size:28px;"></i><p>加载费用信息...</p></div>
      <div v-else>
        <el-card shadow="never" style="margin-bottom:15px;">
          <div slot="header"><b>订单 #{{ paymentOrder.oId }}</b></div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="医生">{{ paymentOrder.dName }}</el-descriptions-item>
            <el-descriptions-item label="挂号时间">{{ paymentOrder.oStart }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <div style="margin-bottom:12px;">
          <div style="font-weight:bold;margin-bottom:8px;">
            <el-checkbox v-model="payRegFee" :disabled="paymentOrder.oPriceState===1||paymentOrder.opriceState===1">
              挂号费 ¥{{ (paymentOrder.oRegistrationFee||paymentOrder.oregistrationFee||0).toFixed(2) }}
            </el-checkbox>
          </div>
        </div>

        <el-card shadow="never" style="margin-bottom:15px;" v-if="paymentDetails.length>0">
          <div slot="header">
            <span style="font-weight:bold;">处方药品</span>
            <el-button type="text" size="mini" style="float:right;" @click="toggleAllDrugs">
              {{ allDrugsSelected ? '取消全选' : '全选' }}
            </el-button>
          </div>
          <div v-for="(d, idx) in paymentDetails" :key="idx" style="padding:6px 0;border-bottom:1px solid #f0f0f0;">
            <el-checkbox v-model="d._selected">
              {{ d.drName }} × {{ d.pdQuantity }} {{ d.pdDosage }} — ¥{{ (d.pdQuantity*d.pdPrice).toFixed(2) }}
            </el-checkbox>
          </div>
        </el-card>

        <el-divider></el-divider>
        <div style="font-size:18px;font-weight:bold;text-align:right;margin-bottom:15px;">
          合计：<span style="color:#E6A23C;">¥{{ selectedTotal.toFixed(2) }}</span>
        </div>
        <el-form label-width="100px">
          <el-form-item label="支付方式">
            <el-radio-group v-model="paymentMethod">
              <el-radio label="微信"><i class="el-icon-wechat" style="color:#07C160;"></i> 微信支付</el-radio>
              <el-radio label="支付宝"><i class="el-icon-alipay" style="color:#1677FF;"></i> 支付宝</el-radio>
              <el-radio label="医保">医保支付</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer">
        <el-button @click="paymentVisible=false">取消</el-button>
        <el-button type="warning" @click="processPayment" :loading="paying" :disabled="!paymentMethod || selectedTotal<=0">
          <i class="el-icon-money"></i> 确认支付 ¥{{ selectedTotal.toFixed(2) }}
        </el-button>
      </div>
    </el-dialog>

    <!-- 缴费明细对话框 -->
    <el-dialog title="费用明细" :visible.sync="billingVisible" width="600px" top="10vh">
      <el-table :data="billingData" border stripe size="small" style="width:100%">
        <el-table-column prop="brType" label="费用类型" width="90"></el-table-column>
        <el-table-column prop="brAmount" label="金额" width="80">
          <template slot-scope="s">¥{{ (s.row.brAmount||0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="brPaymentMethod" label="支付方式" width="80"></el-table-column>
        <el-table-column prop="brPayTime" label="缴费时间" min-width="160"></el-table-column>
      </el-table>
      <div v-if="billingData.length===0" style="text-align:center;padding:20px;color:#999;">暂无缴费记录</div>
    </el-dialog>

    <!-- 报告单详情对话框 -->
    <el-dialog title="就诊报告单" :visible.sync="reportVisible" width="750px" top="5vh">
      <div v-if="reportLoading" style="text-align:center;padding:40px;color:#999;">加载中...</div>
      <div v-else>
        <el-card shadow="hover" style="margin-bottom:12px;">
          <div slot="header"><b>基本信息</b></div>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="就诊编号">{{ reportData.oId }}</el-descriptions-item>
            <el-descriptions-item label="患者姓名">{{ reportData.pName }}</el-descriptions-item>
            <el-descriptions-item label="医生">{{ reportData.dName }}</el-descriptions-item>
            <el-descriptions-item label="就诊时间">{{ reportData.oStart }}</el-descriptions-item>
            <el-descriptions-item label="总费用">¥{{ reportData.oTotalPrice || '0' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
        <el-card shadow="hover" style="margin-bottom:12px;" v-if="emrData">
          <div slot="header"><b style="color:#409EFF;">门诊病历</b></div>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="主诉">{{ emrData.chiefComplaint || '无' }}</el-descriptions-item>
            <el-descriptions-item label="诊断"><span style="color:#E6A23C;font-weight:bold;">{{ emrData.diagnosis || '无' }}</span></el-descriptions-item>
            <el-descriptions-item label="处理意见">{{ emrData.treatmentPlan || '无' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
        <el-card shadow="hover" style="margin-bottom:12px;" v-if="prescDetails.length > 0">
          <div slot="header"><b style="color:#E6A23C;">处方明细</b></div>
          <el-table :data="prescDetails" border stripe size="small" style="width:100%">
            <el-table-column prop="drName" label="药品" min-width="120"></el-table-column>
            <el-table-column prop="pdDosage" label="用量" width="60"></el-table-column>
            <el-table-column prop="pdQuantity" label="数量" width="50"></el-table-column>
            <el-table-column label="金额" width="70"><template slot-scope="s">¥{{ (s.row.pdQuantity*s.row.pdPrice).toFixed(2) }}</template></el-table-column>
          </el-table>
        </el-card>
        <el-card shadow="hover" style="margin-bottom:12px;" v-if="checkItems.length > 0">
          <div slot="header"><b style="color:#67C23A;">检查项目</b></div>
          <el-table :data="checkItems" border stripe size="small" style="width:100%">
            <el-table-column prop="chName" label="检查项目" min-width="120"></el-table-column>
            <el-table-column label="金额" width="70"><template slot-scope="s">¥{{ (s.row.chPrice||0).toFixed(2) }}</template></el-table-column>
            <el-table-column label="状态" width="80">
              <template slot-scope="s">
                <el-tag :type="s.row.ocStatus===2?'success':s.row.ocStatus===1?'warning':'info'" size="mini">
                  {{ {0:'未缴费',1:'待检查',2:'已出结果',3:'异常'}[s.row.ocStatus] || '未知' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </el-dialog>
      <!-- 退费对话框 -->
    <el-dialog title="申请退费" :visible.sync="refundVisible" width="400px" top="15vh">
      <el-form label-width="80px" size="small">
        <el-form-item label="订单号">{{ refundOrder.oId }}</el-form-item>
        <el-form-item label="退费原因">
          <el-input v-model="refundReason" type="textarea" :rows="3" placeholder="请说明退费原因"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="refundVisible=false">取消</el-button>
        <el-button type="danger" @click="submitRefund" :loading="refunding">提交退费申请</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "MyOrder",
  data() {
    return {
      userId: null, orderData: [],
      // 缴费
      paymentVisible: false, paymentLoading: false, paymentOrder: {}, paymentDetails: [], totalPayment: 0, paymentMethod: '', paying: false,
      payRegFee: true, allDrugsSelected: true,
      // 退费
      refundVisible: false, refundOrder: {}, refundReason: '', refunding: false,
      // 费用明细
      billingVisible: false, billingData: [],
      // 报告单
      reportVisible: false, reportLoading: false, reportData: {}, emrData: null, prescDetails: [], checkItems: []
    };
  },
  computed: {
    selectedTotal() {
      var regFee = this.payRegFee ? (this.paymentOrder.oRegistrationFee||this.paymentOrder.oregistrationFee||0) : 0;
      var drugTotal = this.paymentDetails.filter(function(d) { return d._selected; }).reduce(function(s, d) { return s + d.pdQuantity * d.pdPrice; }, 0);
      return regFee + drugTotal;
    }
  },
  methods: {
    // ===== 缴费 =====
    async openPaymentDlg(row) {
      this.paymentOrder = row;
      this.paymentMethod = '';
      this.paymentDetails = [];
      this.payRegFee = true;
      this.allDrugsSelected = true;
      this.paymentVisible = true;
      this.paymentLoading = true;
      try {
        const res = await request.get("prescription/findByOrder", { params: { oId: row.oId } });
        if (res.data.status === 200) {
          this.paymentDetails = (res.data.data || []).map(function(d) { d._selected = true; return d; });
        }
      } catch(e) {}
      this.totalPayment = (row.oRegistrationFee||row.oregistrationFee||0) + (row.oTotalPrice||row.ototalPrice||0);
      this.paymentLoading = false;
    },
    toggleAllDrugs() {
      this.allDrugsSelected = !this.allDrugsSelected;
      var self = this;
      this.paymentDetails.forEach(function(d) { d._selected = self.allDrugsSelected; });
    },
    async processPayment() {
      if (!this.paymentMethod) return this.$message.warning("请选择支付方式");
      this.paying = true;
      try {
        const res = await request.post("order/processPayment", null, {
          params: {
            oId: this.paymentOrder.oId,
            paymentMethod: this.paymentMethod,
            invoiceNo: "INV-" + new Date().toISOString().slice(0,10).replace(/-/g,'') + "-" + String(Date.now()).slice(-4),
            operator: "患者自助"
          }
        });
        if (res.data.status === 200) {
          this.$message.success("缴费成功！");
          this.paymentVisible = false;
          this.requestOrder();
        } else {
          this.$message.error(res.data.msg || "缴费失败");
        }
      } catch(e) {
        this.$message.error("缴费请求失败");
      }
      this.paying = false;
    },
    // ===== 退费 =====
    openRefundDlg(row) {
      this.refundOrder = row;
      this.refundReason = '';
      this.refundVisible = true;
    },
    async submitRefund() {
      if (!this.refundReason) return this.$message.warning("请填写退费原因");
      this.refunding = true;
      try {
        const res = await request.post("refundRequest/create", this.refundOrder);
        if (res.data.status === 200) {
          this.$message.success("退费申请已提交，等待审核");
          this.refundVisible = false;
        } else {
          this.$message.error(res.data.msg || "提交失败，请到收费窗口办理");
        }
      } catch(e) {
        this.$message.error("请求失败");
      }
      this.refunding = false;
    },
    // ===== 费用明细 =====
    async showBillingDetail(oId) {
      this.billingVisible = true;
      this.billingData = [];
      try {
        const res = await request.get("billing/findByOrder", { params: { oId } });
        if (res.data.status === 200) this.billingData = res.data.data || [];
      } catch(e) {}
    },
    // ===== 报告单 =====
    async seeReport(row) {
      this.reportData = row;
      this.emrData = null;
      this.prescDetails = [];
      this.checkItems = [];
      this.reportVisible = true;
      this.reportLoading = true;
      try {
        const emrRes = await request.get("emr/findByOrder", { params: { oId: row.oId } });
        if (emrRes.data.status === 200) this.emrData = emrRes.data.data;
      } catch(e) {}
      try {
        const prescRes = await request.get("prescription/findByOrder", { params: { oId: row.oId } });
        if (prescRes.data.status === 200) this.prescDetails = prescRes.data.data || [];
      } catch(e) {}
      try {
        const emrForCheck = this.emrData;
        if (emrForCheck && emrForCheck.emrId) {
          const checkRes = await request.get("check/findOrderChecks", { params: { pageNumber: 1, size: 50, emrId: emrForCheck.emrId } });
          if (checkRes.data.status === 200 && checkRes.data.data && checkRes.data.data.records) {
            this.checkItems = checkRes.data.data.records;
          }
        }
      } catch(e) {}
      this.reportLoading = false;
    },
    async requestOrder() {
      if (!this.userId) return;
      try {
        const res = await request.get("patient/findOrderByPid", { params: { pId: this.userId } });
        if (res.data.status === 200) {
          var orders = res.data.data || [];
          // 对每个订单查检查费（通过emr/findByOrder先找出emrId，再查order_check）
          var self = this;
          var promises = orders.map(function(item) {
            // 先通过emr/findByOrder获取emrId，再查检查费
            return request.get("emr/findByOrder", { params: { oId: item.oId } }).then(function(emrRes) {
              if (emrRes.data.status === 200 && emrRes.data.data && emrRes.data.data.emrId) {
                return request.get("check/findOrderChecks", { params: { pageNumber: 1, size: 50, emrId: emrRes.data.data.emrId } }).then(function(cr) {
                  if (cr.data.status === 200 && cr.data.data && cr.data.data.records) {
                    item._checkFee = cr.data.data.records.reduce(function(s, c) { return s + parseFloat(c.chPrice || 0); }, 0);
                  }
                });
              }
            }).catch(function(){});
          });
          Promise.all(promises).then(function() { self.orderData = orders; });
        }
      } catch(e) {}
    },
    tokenDecode(t) { if(t) return jwtDecode(t); }
  },
  created() {
    const t = getToken();
    if (t) this.userId = this.tokenDecode(t).pId;
    this.requestOrder();
  }
};
</script>
<style scoped>
.el-table { margin-top: 12px; }
</style>
