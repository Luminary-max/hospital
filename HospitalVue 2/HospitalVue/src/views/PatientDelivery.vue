<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-truck"></i> 送药申请</span>
      <el-button type="primary" size="small" style="float:right;" @click="openCreateDialog" :disabled="orderList.length === 0">
        <i class="el-icon-plus"></i> 申请送药
      </el-button>
    </div>

    <div v-if="loading" style="text-align:center;padding:60px 0;">
      <i class="el-icon-loading" style="font-size:32px;color:#409EFF;"></i>
      <p style="color:#999;margin-top:10px;">加载中...</p>
    </div>

    <div v-else-if="deliveryList.length === 0" style="text-align:center;padding:80px 0;color:#999;">
      <i class="el-icon-truck" style="font-size:48px;"></i>
      <p style="margin-top:15px;font-size:15px;">暂无送药记录</p>
      <p style="color:#c0c4cc;font-size:13px;">缴费后可在处方页面申请送药上门</p>
    </div>

    <el-table v-else :data="deliveryList" border stripe style="width:100%">
      <el-table-column prop="deliveryId" label="编号" width="65" align="center"></el-table-column>
      <el-table-column prop="oId" label="订单号" width="75" align="center"></el-table-column>
      <el-table-column label="代办人" min-width="100">
        <template slot-scope="s">{{ s.row.agentName || '--' }}</template>
      </el-table-column>
      <el-table-column label="代办人电话" width="120">
        <template slot-scope="s">{{ s.row.agentPhone || '--' }}</template>
      </el-table-column>
      <el-table-column label="代办人身份证" width="160">
        <template slot-scope="s">{{ s.row.agentIdCard || '--' }}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="155"></el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="s">
          <el-tag v-if="s.row.status===0" type="warning" size="mini">待取药</el-tag>
          <el-tag v-else-if="s.row.status===1" type="success" size="mini">已取药</el-tag>
          <el-tag v-else type="info" size="mini">已取消</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="note" label="备注" min-width="120" show-overflow-tooltip></el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template slot-scope="s">
          <el-button v-if="s.row.status===0" type="danger" size="mini" @click="cancelDelivery(s.row)">
            取消申请
          </el-button>
          <el-tag v-else type="info" size="mini">--</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @current-change="p=>{pageNumber=p;loadData()}"
      :page-size="size" layout="total,prev,pager,next,jumper" :total="total"
      style="margin-top:15px;" v-if="total>0">
    </el-pagination>

    <!-- 创建送药申请对话框 -->
    <el-dialog title="申请送药" :visible.sync="createVisible" width="600px">
      <el-form :model="deliveryForm" label-width="110px" size="small">
        <el-form-item label="关联订单" required>
          <el-select v-model="deliveryForm.oId" placeholder="选择要送药的订单" style="width:100%" @change="onOrderSelected">
            <el-option v-for="o in orderList" :key="o.oId" :label="'订单#' + o.oId + ' - ' + (o.dName || '医生') + ' ¥' + (o.oTotalPrice||0).toFixed(2)" :value="o.oId"></el-option>
          </el-select>
        </el-form-item>
        <el-card shadow="hover" v-if="selectedOrder" style="margin-bottom:15px;">
          <div slot="header"><span style="font-weight:bold;">订单信息</span></div>
          <el-descriptions :column="2" border size="mini">
            <el-descriptions-item label="医生">{{ selectedOrder.dName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="就诊日期">{{ selectedOrder.oStart || '--' }}</el-descriptions-item>
            <el-descriptions-item label="诊断" :span="2">{{ selectedOrder.oRecord || '--' }}</el-descriptions-item>
            <el-descriptions-item label="药品" :span="2" v-if="selectedOrder.oDrug">{{ selectedOrder.oDrug }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
        <el-divider></el-divider>
        <el-form-item label="代办人姓名" required>
          <el-input v-model="deliveryForm.agentName" placeholder="请输入代办人姓名" maxlength="20"></el-input>
        </el-form-item>
        <el-form-item label="代办人身份证" required>
          <el-input v-model="deliveryForm.agentIdCard" placeholder="请输入代办人身份证号" maxlength="18"></el-input>
        </el-form-item>
        <el-form-item label="代办人电话" required>
          <el-input v-model="deliveryForm.agentPhone" placeholder="请输入代办人手机号" maxlength="11"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="deliveryForm.note" type="textarea" :rows="2" placeholder="其他备注信息（可选）"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="createVisible=false">取消</el-button>
        <el-button type="primary" @click="submitDelivery" :loading="submitting">提交申请</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "PatientDelivery",
  data() {
    return {
      pId: null,
      loading: true,
      deliveryList: [],
      orderList: [],
      total: 0,
      pageNumber: 1,
      size: 10,
      createVisible: false,
      submitting: false,
      selectedOrder: null,
      deliveryForm: {
        oId: "",
        agentName: "",
        agentIdCard: "",
        agentPhone: "",
        note: ""
      }
    };
  },
  methods: {
    async loadData() {
      if (!this.pId) { this.loading = false; return; }
      try {
        const res = await request.get("delivery/findByPatient", { params: { pId: this.pId, pageNumber: this.pageNumber, size: this.size } });
        if (res.data.status === 200) {
          const d = res.data.data;
          this.deliveryList = d.records || d || [];
          this.total = d.total || (Array.isArray(d) ? d.length : 0);
        }
      } catch(e) {}
      this.loading = false;
    },
    async loadOrders() {
      if (!this.pId) return;
      try {
        const res = await request.get("patient/findOrderByPid", { params: { pId: this.pId } });
        if (res.data.status === 200) {
          this.orderList = (res.data.data || []).filter(function(o) {
            return (o.oDrug || o.oTotalPrice > 0) && o.oPriceState === 1;
          });
        }
      } catch(e) {}
    },
    openCreateDialog() {
      this.deliveryForm = { oId: "", agentName: "", agentIdCard: "", agentPhone: "", note: "" };
      this.selectedOrder = null;
      this.loadOrders();
      this.createVisible = true;
    },
    onOrderSelected(oId) {
      this.selectedOrder = this.orderList.find(function(o) { return o.oId === oId; }) || null;
    },
    async submitDelivery() {
      if (!this.deliveryForm.oId) { this.$message.warning("请选择关联订单"); return; }
      if (!this.deliveryForm.agentName) { this.$message.warning("请输入代办人姓名"); return; }
      if (!this.deliveryForm.agentIdCard) { this.$message.warning("请输入代办人身份证号"); return; }
      if (!this.deliveryForm.agentPhone) { this.$message.warning("请输入代办人电话"); return; }
      this.submitting = true;
      try {
        const res = await request.post("delivery/create", {
          pId: this.pId,
          oId: this.deliveryForm.oId,
          agentName: this.deliveryForm.agentName,
          agentIdCard: this.deliveryForm.agentIdCard,
          agentPhone: this.deliveryForm.agentPhone,
          note: this.deliveryForm.note || ""
        });
        if (res.data.status === 200) {
          this.$message.success("送药申请已提交");
          this.createVisible = false;
          this.loadData();
        } else {
          this.$message.error(res.data.msg || "提交失败");
        }
      } catch(e) {
        this.$message.error("请求失败");
      }
      this.submitting = false;
    },
    async cancelDelivery(row) {
      this.$confirm("确定取消该送药申请吗？", "取消申请", { type: "warning" }).then(async function() {
        try {
          const res = await request.post("delivery/cancel", { deliveryId: row.deliveryId });
          if (res.data.status === 200) {
            this.$message.success("已取消");
            this.loadData();
          } else {
            this.$message.error(res.data.msg || "取消失败");
          }
        } catch(e) {
          this.$message.error("请求失败");
        }
      }.bind(this)).catch(function() {});
    }
  },
  created() {
    const t = getToken();
    if (t) { const d = jwtDecode(t); this.pId = d.pId || d.sub; }
    this.loadData();
  }
};
</script>
<style scoped>
</style>


