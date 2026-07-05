<template>
  <el-card>
    <div slot="header"><span><i class="el-icon-medicine"></i> 药房发药管理</span></div>

    <!-- 状态筛选 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-radio-group v-model="statusFilter" @change="loadData" size="small">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button :label="0">待发药</el-radio-button>
          <el-radio-button :label="1">待复核</el-radio-button>
          <el-radio-button :label="2">已发药</el-radio-button>
          <el-radio-button :label="3">已退药</el-radio-button>
        </el-radio-group>
        <el-input v-model="query" placeholder="搜索处方明细ID" size="small" class="search-input" clearable @keyup.enter.native="loadData">
          <el-button slot="append" icon="el-icon-search" @click="loadData"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
      </div>
    </div>

    <el-table :data="dispensingData" border stripe style="width:100%">
      <el-table-column prop="pdId" label="编号" width="65" align="center"></el-table-column>
      <el-table-column prop="prescDetailId" label="处方明细ID" width="90" align="center"></el-table-column>
      <el-table-column prop="dbId" label="批次ID" width="65" align="center"></el-table-column>
      <el-table-column prop="pdQuantity" label="数量" width="60" align="center"></el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template slot-scope="s">
          <el-tag v-if="s.row.pdStatus===0" type="danger" size="mini">待发药</el-tag>
          <el-tag v-else-if="s.row.pdStatus===1" type="warning" size="mini">待复核</el-tag>
          <el-tag v-else-if="s.row.pdStatus===2" type="success" size="mini">已发药</el-tag>
          <el-tag v-else type="info" size="mini">已退药</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="pdCreateTime" label="创建时间" width="155"></el-table-column>
      <el-table-column prop="pdDispenseTime" label="发药时间" width="155"></el-table-column>
      <el-table-column prop="pdDispenseBy" label="发药人" width="80" align="center"></el-table-column>
      <el-table-column prop="pdReviewBy" label="复核药师" width="85" align="center"></el-table-column>
      <el-table-column prop="pdNote" label="备注" min-width="120" show-overflow-tooltip></el-table-column>
      <el-table-column label="操作" width="200" fixed="right" align="center">
        <template slot-scope="s">
          <el-button type="success" size="mini" v-if="s.row.pdStatus===0" @click="dispenseDrug(s.row)">
            确认发药
          </el-button>
          <el-button type="primary" size="mini" v-else-if="s.row.pdStatus===1" @click="reviewDrug(s.row)">
            复核通过
          </el-button>
          <el-button type="warning" size="mini" v-else-if="s.row.pdStatus===2" @click="returnDrug(s.row)">
            办理退药
          </el-button>
          <el-tag type="info" v-else size="mini">已退回</el-tag>
          <el-button v-if="s.row.pdStatus===2" type="info" size="mini" plain @click="showDrugGuide(s.row)">
            用药指导
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="s=>{size=s;loadData()}" @current-change="p=>{pageNumber=p;loadData()}"
      :current-page="pageNumber" :page-sizes="[10,20,30]" :page-size="size"
      layout="total,sizes,prev,pager,next,jumper" :total="total" style="margin-top:15px;">
    </el-pagination>

    <!-- 用药指导对话框 -->
    <el-dialog title="用药指导单" :visible.sync="guideVisible" width="680px" top="5vh">
      <div v-if="guideLoading" style="text-align:center;padding:40px;">
        <i class="el-icon-loading" style="font-size:28px;color:#409EFF;"></i>
        <p style="color:#999;margin-top:10px;">加载用药指导信息...</p>
      </div>
      <div v-else id="guidePrintArea" class="guide-print">
        <div class="guide-header">
          <h2 style="text-align:center;margin:0 0 8px 0;">用药指导单</h2>
          <p style="text-align:center;color:#909399;font-size:13px;margin:0 0 16px 0;">
            请仔细阅读以下用药说明，如有疑问请咨询医生或药师
          </p>
        </div>
        <el-divider></el-divider>
        <div class="guide-section" v-if="guideData.drugName">
          <h4>药品信息</h4>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="药品名称" :span="2">{{ guideData.drugName || guideData.drName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="用法">{{ guideData.pdUsage || '--' }}</el-descriptions-item>
            <el-descriptions-item label="用量">{{ guideData.pdDosage || '--' }}</el-descriptions-item>
            <el-descriptions-item label="频次">{{ guideData.pdFrequency || '--' }}</el-descriptions-item>
            <el-descriptions-item label="给药途径">{{ guideData.pdRoute || '--' }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <div class="guide-section">
          <h4>注意事项</h4>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="禁忌症">{{ guideData.drContraindication || '无特殊禁忌' }}</el-descriptions-item>
            <el-descriptions-item label="不良反应">{{ guideData.drAdverseReactions || '--' }}</el-descriptions-item>
            <el-descriptions-item label="储存条件">{{ guideData.drStorage || '--' }}</el-descriptions-item>
            <el-descriptions-item label="使用提示">{{ guideData.drUsageTips || '--' }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <div style="text-align:center;color:#c0c4cc;font-size:11px;margin-top:20px;border-top:1px dashed #eee;padding-top:12px;">
          本指导单由医院门诊管理系统自动生成，仅供用药参考
        </div>
      </div>
      <div slot="footer">
        <el-button @click="guideVisible=false">关闭</el-button>
        <el-button type="primary" icon="el-icon-printer" @click="printGuide">打印</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "PharmacyDispensingList",
  data() {
    return {
      pageNumber:1, size:10, statusFilter:'', query:'', dispensingData:[], total:0,
      guideVisible: false, guideLoading: false, guideData: {}
    };
  },
  methods: {
    loadData() {
      const params = {
        pageNumber:this.pageNumber, size:this.size,
        status:this.statusFilter===''?null:this.statusFilter,
        query:this.query||null
      };
      request.get("pharmacy/findAll", { params })
        .then(res => {
          const d=res.data.data;
          this.dispensingData=d.records||[];
          this.total=d.total||0;
        });
    },
    // 待发药 → 待复核（确认发药）
    dispenseDrug(row) {
      this.$prompt("请输入发药人姓名", "确认发药", { inputPlaceholder:"药剂师姓名" }).then(({ value })=> {
        if (!value) return this.$message.warning("请输入发药人");
        // API: GET pharmacy/dispense — 设置pdStatus=1(待复核)
        request.get("pharmacy/dispense", { params: { pdId:row.pdId, dispenseBy:value } }).then(res => {
          if (res.data.status===200) {
            this.$message.success("发药成功，等待复核！");
            this.loadData();
          } else this.$message.error(res.data.msg||"发药失败");
        }).catch(()=>this.$message.error("请求失败"));
      }).catch(()=>{});
    },
    // 待复核 → 已发药（复核通过）
    reviewDrug(row) {
      this.$prompt("请输入复核药师姓名", "复核确认", { inputPlaceholder:"复核药师" }).then(({ value })=> {
        if (!value) return this.$message.warning("请输入复核药师");
        // API: GET pharmacy/review — 需要后端实现
        request.get("pharmacy/review", { params: { pdId:row.pdId, reviewer:value } }).then(res => {
          if (res.data.status===200) {
            this.$message.success("复核通过，发药完成！");
            this.loadData();
          } else this.$message.error(res.data.msg||"复核失败");
        }).catch(()=>this.$message.error("请求失败"));
      }).catch(()=>{});
    },
    // 已发药 → 已退药（办理退药）
    returnDrug(row) {
      this.$prompt("请输入退药操作人", "办理退药", { inputPlaceholder:"药剂师姓名", inputValidator:v=>!!v||"请输入操作人" }).then(({value})=>{
        return this.$confirm("退药后库存将回补到原发药批次，确认继续？","退药确认",{type:"warning"}).then(()=>{
          // API: GET pharmacy/returnDrug
          request.get("pharmacy/returnDrug",{params:{pdId:row.pdId,returnBy:value}}).then(res=>{
            if(res.data.status===200){
              this.$message.success("退药成功，库存已回补");
              this.loadData();
            } else this.$message.error(res.data.msg||"退药失败");
          }).catch(()=>this.$message.error("请求失败"));
        });
      }).catch(()=>{});
    },
    // 用药指导
    showDrugGuide(row) {
      this.guideVisible = true;
      this.guideLoading = true;
      this.guideData = {};
      request.get("pharmacy/printGuide", { params: { pdId: row.pdId } }).then(res => {
        if (res.data.status === 200) {
          this.guideData = res.data.data || {};
        } else {
          this.$message.error(res.data.msg || "获取用药指导失败");
        }
      }).catch(() => {
        this.$message.error("请求失败");
      }).finally(() => {
        this.guideLoading = false;
      });
    },
    printGuide() {
      this.$nextTick(() => {
        const el = document.getElementById("guidePrintArea");
        if (!el) return;
        // Strip potential script elements before printing (XSS defense-in-depth)
        const safeEl = el.cloneNode(true);
        safeEl.querySelectorAll("script").forEach(s => s.remove());
        const printContents = safeEl.innerHTML;
        const win = window.open("", "_blank", "width=780,height=900");
        win.document.write(`
          <html>
          <head>
            <title>用药指导单</title>
            <style>
              body { font-family: "Microsoft YaHei", sans-serif; padding: 40px; color: #333; }
              .guide-header { text-align: center; margin-bottom: 20px; }
              .guide-header h2 { margin: 0 0 8px 0; }
              .guide-header p { color: #909399; font-size: 13px; margin: 0 0 16px 0; }
              .guide-section { margin-bottom: 20px; }
              .guide-section h4 { color: #409EFF; border-left: 3px solid #409EFF; padding-left: 10px; margin: 16px 0 10px 0; }
              table { width: 100%; border-collapse: collapse; font-size: 13px; }
              table td, table th { border: 1px solid #ddd; padding: 8px 12px; text-align: left; }
              table th { background: #f5f7fa; font-weight: 600; width: 100px; }
              .footer { text-align: center; color: #c0c4cc; font-size: 11px; margin-top: 30px; border-top: 1px dashed #eee; padding-top: 12px; }
            </style>
          </head>
          <body>${printContents}</body>
          </html>
        `);
        win.document.close();
        setTimeout(() => { win.print(); }, 300);
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
.total-tag { margin-left:8px; }
.guide-section { margin-bottom:16px; }
.guide-section h4 { color:#409EFF; border-left:3px solid #409EFF; padding-left:10px; margin:12px 0 10px 0; }
@media print { .guide-print { padding: 20px; } }
</style>


