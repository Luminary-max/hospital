<template>
  <el-card>
    <div slot="header">
      <i class="el-icon-first-aid-kit"></i> 检查结果管理
      <el-button type="primary" size="small" style="float:right;" @click="refreshData"><i class="el-icon-refresh"></i> 刷新</el-button>
    </div>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-select v-model="statusFilter" placeholder="检查状态" size="small" class="filter-select" @change="loadData">
          <el-option label="全部" value="-1"></el-option>
          <el-option label="未缴费" value="0"></el-option>
          <el-option label="待检查" value="1"></el-option>
          <el-option label="已完成" value="2"></el-option>
          <el-option label="异常" value="3"></el-option>
        </el-select>
        <el-input v-model="oIdQuery" placeholder="订单ID" size="small" class="search-input" clearable @keyup.enter.native="loadData">
          <el-button slot="append" icon="el-icon-search" @click="loadData"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
      </div>
    </div>
    <el-table :data="checkOrders" border stripe style="width:100%">
      <el-table-column prop="ocId" label="编号" width="65" align="center"></el-table-column>
      <el-table-column prop="emrId" label="病历编号" width="75" align="center"></el-table-column>
      <el-table-column prop="chName" label="检查项目" min-width="150"></el-table-column>
      <el-table-column prop="chPrice" label="价格" width="80" align="center">
        <template slot-scope="s">¥{{ s.row.chPrice || '0' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="s">
          <el-tag :type="s.row.ocStatus===0?'danger':s.row.ocStatus===1?'warning':s.row.ocStatus===2?'success':'info'" size="mini">
            {{ {0:'未缴费',1:'待检查',2:'已完成',3:'异常'}[s.row.ocStatus] || '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="ocResult" label="检查结果" min-width="180" show-overflow-tooltip></el-table-column>
      <el-table-column prop="ocOperator" label="操作人" width="80" align="center"></el-table-column>
      <el-table-column prop="ocCreateTime" label="创建时间" width="155"></el-table-column>
      <el-table-column prop="ocResultTime" label="结果时间" width="155"></el-table-column>
      <el-table-column label="操作" width="120" fixed="right" align="center">
        <template slot-scope="s">
          <el-button type="success" size="mini" v-if="s.row.ocStatus===1" @click="openResultDialog(s.row)">
            <i class="el-icon-edit"></i> 录入结果
          </el-button>
          <el-button type="primary" size="mini" v-else-if="s.row.ocStatus>=2" @click="viewResult(s.row)">
            <i class="el-icon-view"></i> 查看报告
          </el-button>
          <el-tag type="info" v-else size="mini">未缴费</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}"
      :current-page="pageNumber" :page-sizes="[10,20,30]" :page-size="size"
      layout="total,sizes,prev,pager,next,jumper" :total="total" style="margin-top:15px;">
    </el-pagination>

    <!-- 录入结果对话框 -->
    <el-dialog title="录入检查结果" :visible.sync="resultDialogVisible" width="550px">
      <el-form :model="resultForm" label-width="90px" size="small">
        <el-form-item label="检查项目"><el-tag>{{ resultForm.chName }}</el-tag></el-form-item>
        <el-form-item label="检查结果" required>
          <el-input v-model="resultForm.result" type="textarea" :rows="4" placeholder="请输入检查结果描述..."></el-input>
        </el-form-item>
        <el-form-item label="异常标记">
          <el-switch v-model="resultForm.isAbnormal" active-text="异常" inactive-text="正常"></el-switch>
        </el-form-item>
        <el-form-item label="附件">
          <el-upload :action="''" :auto-upload="false" :show-file-list="false" accept="image/*,.pdf" :on-change="handleAttachChange">
            <el-button size="small" type="primary"><i class="el-icon-upload2"></i> 选择文件</el-button>
          </el-upload>
          <span v-if="resultForm.attachment" style="margin-left:8px;color:#67C23A;">已选择：{{ resultForm.attachmentFileName || resultForm.attachment }}</span>
        </el-form-item>
        <el-form-item label="操作人" required>
          <el-input v-model="resultForm.operator" placeholder="操作人员姓名"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="resultDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitResult">确认录入</el-button>
      </div>
    </el-dialog>

    <!-- 查看报告对话框 -->
    <el-dialog title="检查报告" :visible.sync="reportDialogVisible" width="600px">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="检查项目" :span="2">{{ reportData.chName }}</el-descriptions-item>
        <el-descriptions-item label="价格" :span="1">¥{{ reportData.chPrice }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="reportData.ocStatus===2?'success':'danger'" size="mini">{{ reportData.ocStatus===2?'正常':'异常' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="检查结果" :span="2">{{ reportData.ocResult || '无' }}</el-descriptions-item>
        <el-descriptions-item label="附件">
          <template v-if="reportData.ocAttachment && reportData.ocAttachment.startsWith('data:')">
            <el-image :src="reportData.ocAttachment" style="max-width:200px;max-height:200px;" fit="contain" preview-teleported :preview-src-list="[reportData.ocAttachment]"></el-image>
          </template>
          <span v-else>{{ reportData.ocAttachment || '无' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="操作人">{{ reportData.ocOperator || '-' }}</el-descriptions-item>
        <el-descriptions-item label="结果时间" :span="2">{{ reportData.ocResultTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "CheckResultList",
  data() {
    return {
      pageNumber:1, size:10, total:0,
      statusFilter:'-1', oIdQuery:'', checkOrders:[],
      resultDialogVisible:false, reportDialogVisible:false,
      resultForm:{ ocId:null, chName:'', result:'', isAbnormal:false, attachment:'', operator:'' },
      reportData:{}
    };
  },
  methods: {
    loadData() {
      const params = {
        pageNumber:this.pageNumber, size:this.size,
        emrId:this.oIdQuery||null,
        status:this.statusFilter==='-1'?null:parseInt(this.statusFilter)
      };
      request.get("check/findOrderChecks", { params }).then(res=>{
        if(res.data.status===200) {
          const d=res.data.data;
          this.checkOrders=d.records||[];
          this.total=d.total||0;
        }
      });
    },
    refreshData() { this.loadData(); },
    openResultDialog(row) {
      this.resultForm = { ocId:row.ocId, chName:row.chName, result:'', isAbnormal:false, attachment:'', attachmentFileName:'', operator:'' };
      this.resultDialogVisible = true;
    },
    // 检查附件上传 — 转 base64 保存
    handleAttachChange(file) {
      if (!file || !file.raw) return;
      this.resultForm.attachmentFileName = file.name;
      const reader = new FileReader();
      reader.onload = (e) => { this.resultForm.attachment = e.target.result; };
      reader.readAsDataURL(file.raw);
    },
    submitResult() {
      if (!this.resultForm.result) return this.$message.warning("请输入检查结果");
      if (!this.resultForm.operator) return this.$message.warning("请输入操作人");
      const finalStatus = this.resultForm.isAbnormal ? 3 : 2;
      request.get("check/updateCheckResult", {
        params: { ocId:this.resultForm.ocId, result:this.resultForm.result, attachment:this.resultForm.attachment||null, operator:this.resultForm.operator }
      }).then(res=>{
        if (res.data.status===200) {
          // Also update status if abnormal
          if (this.resultForm.isAbnormal) {
            request.get("check/updateCheckStatus", { params: { ocId:this.resultForm.ocId, status:3 } });
          }
          this.$message.success("检查结果录入成功");
          this.resultDialogVisible = false;
          this.loadData();
        } else this.$message.error("录入失败");
      });
    },
    viewResult(row) {
      this.reportData = row;
      this.reportDialogVisible = true;
    }
  },
  created() { this.loadData(); }
};
</script>
<style scoped>
.toolbar { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px; }
.toolbar-left { display:flex; align-items:center; gap:8px; }
.toolbar-right { display:flex; align-items:center; gap:8px; }
.search-input { width:200px; }
.filter-select { width:120px; }
</style>


