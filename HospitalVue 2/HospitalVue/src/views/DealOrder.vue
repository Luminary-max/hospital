<template>
  <el-card>
    <div slot="header">
      <span><i class="el-icon-document"></i> 门诊接诊 — 结构化病历 & 处方开立</span>
      <el-button type="primary" size="small" style="float:right;" @click="submitClick" :loading="submitting">
        <i class="el-icon-upload2"></i> 提交病历
      </el-button>
    </div>

    <!-- 患者信息 -->
    <el-card shadow="hover" style="margin-bottom:20px;">
      <el-descriptions :column="4" border size="small">
        <el-descriptions-item label="患者姓名" :span="1">{{ pName }}</el-descriptions-item>
        <el-descriptions-item label="性别" :span="1">{{ pGender }}</el-descriptions-item>
        <el-descriptions-item label="年龄" :span="1">{{ pAge || '---' }}</el-descriptions-item>
        <el-descriptions-item label="电话" :span="1">{{ pPhone }}</el-descriptions-item>
        <el-descriptions-item label="就诊日期" :span="2">{{ nowDate }}</el-descriptions-item>
        <el-descriptions-item label="医生" :span="1">{{ dName }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 门诊病历 -->
    <el-card shadow="hover" style="margin-bottom:20px;">
      <div slot="header"><span style="font-weight:bold; color:#409EFF;">一、门诊病历</span>
        <el-button size="mini" type="primary" plain style="float:right;margin-left:8px;" @click="openEmrTemplateDialog">
          <i class="el-icon-document-copy"></i> 病历模板
        </el-button>
      </div>
      <el-form label-position="top" size="small">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主诉（Chief Complaint）">
              <el-input type="textarea" :rows="3" v-model="emr.chiefComplaint" placeholder="患者主要症状、持续时间"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="现病史（Present Illness）">
              <el-input type="textarea" :rows="3" v-model="emr.presentIllness" placeholder="发病情况、诊治经过"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="既往史（Past History）">
              <el-input type="textarea" :rows="3" v-model="emr.pastHistory" placeholder="既往疾病、手术史、过敏史"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体格检查（Physical Examination）">
              <el-input type="textarea" :rows="3" v-model="emr.physicalExam" placeholder="生命体征、阳性体征"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="诊断（Diagnosis）">
              <el-input type="textarea" :rows="3" v-model="emr.diagnosis" placeholder="初步诊断结果"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理意见（Treatment Plan）">
              <el-input type="textarea" :rows="3" v-model="emr.treatmentPlan" placeholder="进一步检查、复诊建议、注意事项"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 处方开立 -->
    <el-card shadow="hover" style="margin-bottom:20px;">
      <div slot="header">
        <span style="font-weight:bold; color:#E6A23C;">二、处方开立</span>
        <el-tag size="mini" type="warning" style="margin-left:8px;">西药</el-tag>
        <el-tag size="mini" type="success" style="margin-left:4px;">中药</el-tag>
        <el-button size="mini" type="warning" plain style="float:right;" @click="openPrescriptionTemplateDialog">
          <i class="el-icon-document-copy"></i> 处方模板
        </el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form inline size="mini">
            <el-form-item>
              <el-input v-model="queryDrug" placeholder="搜索药品" prefix-icon="el-icon-search" @keyup.enter.native="requestDrug"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="requestDrug">查询</el-button>
            </el-form-item>
            <el-form-item>
              <el-select v-model="drugTypeFilter" placeholder="全部" size="mini" style="width:90px;" @change="requestDrug">
                <el-option label="全部" value=""></el-option>
                <el-option label="西药" value="1"></el-option>
                <el-option label="中药" value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-form>
          <el-table :data="drugData" border stripe size="mini" height="250" style="width:100%">
            <el-table-column prop="drId" label="编号" width="75"></el-table-column>
            <el-table-column prop="drName" label="药品名称" width="130"></el-table-column>
            <el-table-column label="类型" width="55">
              <template slot-scope="s">
                <el-tag :type="s.row.drType === 2 ? 'success' : 'warning'" size="mini">{{ s.row.drType === 2 ? '中药' : '西药' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="drPrice" label="单价" width="70">
              <template slot-scope="s">¥{{ s.row.drPrice }}</template>
            </el-table-column>
            <el-table-column prop="drNumber" label="库存" width="55"></el-table-column>
            <el-table-column prop="drUnit" label="单位" width="55"></el-table-column>
            <el-table-column label="操作" width="65">
              <template slot-scope="s">
                <el-button :type="s.row.drType === 2 ? 'success' : 'warning'" size="mini" @click="showPrescriptionDialog(s.row)">开药</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination @current-change="drugPageChange" :page-size="size" layout="total,prev,pager,next" :total="drugTotal" small></el-pagination>
        </el-col>
        <el-col :span="12">
          <el-tag type="warning" style="margin-bottom:10px;">已选药品（合计：¥{{ drugTotalPrice }}）</el-tag>
          <el-table :data="drugBuyData" border stripe size="mini" height="300" style="width:100%">
            <el-table-column prop="drName" label="药品名" width="90"></el-table-column>
            <el-table-column label="类型" width="50">
              <template slot-scope="s">
                <el-tag :type="s.row.drType === 2 ? 'success' : 'warning'" size="mini">{{ s.row.drType === 2 ? '中药' : '西药' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="用法/煎法" width="80">
              <template slot-scope="s">{{ s.row.drType === 2 ? s.row.pdNote : s.row.pdUsage }}</template>
            </el-table-column>
            <el-table-column label="用量/剂量" width="70">
              <template slot-scope="s">{{ s.row.drType === 2 ? s.row.pdDosage+'g' : s.row.pdDosage }}</template>
            </el-table-column>
            <el-table-column label="频次" width="60">
              <template slot-scope="s">{{ s.row.drType === 2 ? '水煎服' : s.row.pdFrequency }}</template>
            </el-table-column>
            <el-table-column label="剂数/天数" width="65">
              <template slot-scope="s">{{ s.row.drType === 2 ? s.row.pdDays+'剂' : s.row.pdDays+'天' }}</template>
            </el-table-column>
            <el-table-column label="小计" width="70">
              <template slot-scope="s">¥{{ (s.row.pdQuantity * s.row.drPrice).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="50">
              <template slot-scope="s">
                <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteDrug(s.$index)"></el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-card>

    <!-- 检查项目 -->
    <el-card shadow="hover" style="margin-bottom:20px;">
      <div slot="header"><span style="font-weight:bold; color:#67C23A;">三、检查项目</span></div>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form inline size="mini">
            <el-form-item>
              <el-input v-model="queryCheck" placeholder="搜索检查项目" prefix-icon="el-icon-search" @keyup.enter.native="requestCheck"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="requestCheck">查询</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="checkData" border stripe size="mini" height="200" style="width:100%">
            <el-table-column prop="chId" label="编号" width="80"></el-table-column>
            <el-table-column prop="chName" label="项目名称" width="200"></el-table-column>
            <el-table-column prop="chPrice" label="价格" width="80">
              <template slot-scope="s">¥{{ s.row.chPrice }}</template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template slot-scope="s">
                <el-button type="success" size="mini" @click="addCheck(s.row)">增加</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination @current-change="checkPageChange" :page-size="checkSize" layout="total,prev,pager,next" :total="checkTotal" small></el-pagination>
        </el-col>
        <el-col :span="12">
          <el-tag type="success" style="margin-bottom:10px;">已选检查（合计：¥{{ checkTotalPrice }}）</el-tag>
          <el-table :data="checkBuyData" border stripe size="mini" height="200" style="width:100%">
            <el-table-column prop="chId" label="编号" width="80"></el-table-column>
            <el-table-column prop="chName" label="项目名" width="180"></el-table-column>
            <el-table-column label="价格" width="80">
              <template slot-scope="s">¥{{ s.row.chPrice }}</template>
            </el-table-column>
            <el-table-column label="操作" width="50">
              <template slot-scope="s">
                <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteCheck(s.$index)"></el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-card>

    <!-- 西药处方明细对话框 -->
    <el-dialog title="开立西药处方" :visible.sync="westernPrescDlg" width="620px">
      <el-form :model="prescriptionForm" label-width="80px" size="small">
        <el-alert v-if="prescriptionForm.drControlled===1" title="该药品属于特殊管制药品，请核对适应症、剂量和处方权限。" type="error" :closable="false" style="margin-bottom:12px"></el-alert>
        <el-alert v-else-if="prescriptionForm.drAntibioticLevel" :title="'抗菌药级别：'+prescriptionForm.drAntibioticLevel+'，请遵循分级使用要求。'" type="warning" :closable="false" style="margin-bottom:12px"></el-alert>
        <el-form-item label="药品名称">
          <el-input v-model="prescriptionForm.drName" disabled></el-input>
        </el-form-item>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="用法">
              <el-select v-model="prescriptionForm.pdUsage" placeholder="选择用法" style="width:100%">
                <el-option label="口服" value="口服"></el-option>
                <el-option label="注射" value="注射"></el-option>
                <el-option label="外用" value="外用"></el-option>
                <el-option label="含服" value="含服"></el-option>
                <el-option label="雾化" value="雾化"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每次用量">
              <el-input v-model="prescriptionForm.pdDosage" placeholder="如：1片、10ml"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="给药途径">
              <el-select v-model="prescriptionForm.pdRoute" style="width:100%" placeholder="选择途径">
                <el-option v-for="v in ['经口','静脉滴注','肌内注射','皮下注射','外用','吸入','舌下']" :key="v" :label="v" :value="v"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服药时机">
              <el-select v-model="prescriptionForm.pdTiming" style="width:100%" clearable>
                <el-option v-for="v in ['餐前','餐后','餐中','睡前','必要时']" :key="v" :label="v" :value="v"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="用药安全">
          <el-checkbox v-model="prescriptionForm.pdSkinTest" :true-label="1" :false-label="0">需皮试</el-checkbox>
          <span v-if="prescriptionForm.drContraindication" class="safety-note">禁忌：{{ prescriptionForm.drContraindication }}</span>
        </el-form-item>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="频次">
              <el-select v-model="prescriptionForm.pdFrequency" placeholder="选择频次" style="width:100%">
                <el-option label="qd(每日1次)" value="qd"></el-option>
                <el-option label="bid(每日2次)" value="bid"></el-option>
                <el-option label="tid(每日3次)" value="tid"></el-option>
                <el-option label="qid(每日4次)" value="qid"></el-option>
                <el-option label="qn(每晚1次)" value="qn"></el-option>
                <el-option label="prn(必要时)" value="prn"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用药天数">
              <el-input-number v-model="prescriptionForm.pdDays" :min="1" :max="30" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="prescriptionForm.pdNote" placeholder="如：饭后服用、用药期间忌酒等"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="westernPrescDlg = false">取消</el-button>
        <el-button type="primary" @click="confirmWesternPresc">确认开立</el-button>
      </div>
    </el-dialog>

    <!-- 中药处方明细对话框 —— 方剂模式 -->
    <el-dialog title="开立中药处方（方剂模式）" :visible.sync="chinesePrescDlg" width="650px">
      <!-- 上方：选药区 -->
      <el-card shadow="hover" style="margin-bottom:12px;">
        <div slot="header" style="font-size:13px;"><b>选药</b> — 从上方药品列表中双击或点此添加</div>
        <el-row :gutter="10">
          <el-col :span="8">
            <el-select v-model="chineseSelectDrug" filterable placeholder="搜索中药" style="width:100%" @change="onChineseDrugSelected">
              <el-option v-for="d in chineseDrugList" :key="d.drId" :label="d.drName + ' ¥' + d.drPrice + '/' + d.drUnit" :value="d.drId"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-input-number v-model="chineseAddDosage" :min="1" :max="100" style="width:100%"></el-input-number>
          </el-col>
          <el-col :span="4"><span style="line-height:36px;color:#999;">克/剂</span></el-col>
          <el-col :span="6">
            <el-button type="success" size="small" @click="addToFormula">加入方剂</el-button>
          </el-col>
        </el-row>
      </el-card>
      <!-- 方剂列表 -->
      <el-card shadow="hover" style="margin-bottom:12px;">
        <div slot="header" style="font-size:13px;"><b>当前方剂</b>（共 <b>{{ formulaItems.length }}</b> 味药）  <el-tag size="mini" type="success">¥{{ formulaSubtotal }}</el-tag></div>
        <el-table :data="formulaItems" border stripe size="small" max-height="200" style="width:100%">
          <el-table-column prop="drName" label="药品" width="130"></el-table-column>
          <el-table-column label="剂量" width="80"><template slot-scope="s">{{ s.row.dosage }}g</template></el-table-column>
          <el-table-column label="单价" width="60"><template slot-scope="s">¥{{ s.row.drPrice }}</template></el-table-column>
          <el-table-column label="小计/剂" width="80"><template slot-scope="s">¥{{ (s.row.dosage * s.row.drPrice).toFixed(2) }}</template></el-table-column>
          <el-table-column label="操作" width="50">
            <template slot-scope="s"><el-button type="danger" size="mini" icon="el-icon-delete" circle @click="removeFromFormula(s.$index)"></el-button></template>
          </el-table-column>
        </el-table>
        <div v-if="formulaItems.length===0" style="text-align:center;color:#999;padding:30px 0;">尚未添加药品，请从上方搜索加入</div>
      </el-card>
      <!-- 下方：方剂整体设置 -->
      <el-card shadow="hover">
        <div slot="header" style="font-size:13px;"><b>方剂设置</b></div>
        <el-form :model="prescriptionForm" label-width="80px" size="small">
          <el-row :gutter="10">
            <el-col :span="8">
              <el-form-item label="剂数">
                <el-input-number v-model="prescriptionForm.pdDays" :min="1" :max="60" style="width:100%"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="煎法">
                <el-select v-model="prescriptionForm.pdUsage" placeholder="选择煎法" style="width:100%">
                  <el-option label="水煎" value="水煎"></el-option>
                  <el-option label="冲服" value="冲服"></el-option>
                  <el-option label="泡服" value="泡服"></el-option>
                  <el-option label="烊化" value="烊化"></el-option>
                  <el-option label="另煎" value="另煎"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8"><span style="line-height:36px;color:#999;">共 {{ formulaItems.length }} 味 × {{ prescriptionForm.pdDays }} 剂</span></el-col>
          </el-row>
          <el-form-item label="用法备注">
            <el-input v-model="prescriptionForm.pdNote" placeholder="如：饭后温服、先煎后下等"></el-input>
          </el-form-item>
        </el-form>
      </el-card>
      <div slot="footer">
        <el-button @click="chinesePrescDlg = false">取消</el-button>
        <el-button type="success" @click="confirmChinesePresc" :disabled="formulaItems.length===0">确认开立方剂</el-button>
      </div>
    </el-dialog>

    <!-- 病历模板选择对话框 -->
    <el-dialog title="选择病历模板" :visible.sync="emrTemplateDlgVisible" width="700px" top="5vh">
      <div v-if="emrTemplateLoading" style="text-align:center;padding:40px;">
        <i class="el-icon-loading" style="font-size:28px;color:#409EFF;"></i>
        <p style="color:#999;margin-top:10px;">加载模板中...</p>
      </div>
      <div v-else-if="emrTemplateList.length === 0" style="text-align:center;padding:40px;color:#999;">
        <i class="el-icon-document" style="font-size:40px;"></i>
        <p style="margin-top:10px;">暂无病历模板，请联系管理员创建</p>
      </div>
      <el-table v-else :data="emrTemplateList" border stripe size="small" @row-click="applyEmrTemplate" style="width:100%">
        <el-table-column prop="etName" label="模板名称" min-width="130"></el-table-column>
        <el-table-column prop="chiefComplaint" label="主诉" min-width="150" show-overflow-tooltip></el-table-column>
        <el-table-column prop="diagnosis" label="诊断" min-width="120" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template slot-scope="s">
            <el-button type="primary" size="mini" @click="applyEmrTemplate(s.row)">选用</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer">
        <el-button @click="emrTemplateDlgVisible=false">取消</el-button>
      </div>
    </el-dialog>

    <!-- 处方模板选择对话框 -->
    <el-dialog title="选择处方模板" :visible.sync="prescTemplateDlgVisible" width="750px" top="5vh">
      <div v-if="prescTemplateLoading" style="text-align:center;padding:40px;">
        <i class="el-icon-loading" style="font-size:28px;color:#E6A23C;"></i>
        <p style="color:#999;margin-top:10px;">加载模板中...</p>
      </div>
      <div v-else-if="prescTemplateList.length === 0" style="text-align:center;padding:40px;color:#999;">
        <i class="el-icon-document" style="font-size:40px;"></i>
        <p style="margin-top:10px;">暂无处方模板，请联系管理员创建</p>
      </div>
      <el-table v-else :data="prescTemplateList" border stripe size="small" style="width:100%">
        <el-table-column prop="ptName" label="模板名称" width="130"></el-table-column>
        <el-table-column label="药品数" width="70" align="center">
          <template slot-scope="s">
            <el-tag size="mini">{{ parsePrescTemplateDrugCount(s.row.ptContent) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="内容预览" min-width="250" show-overflow-tooltip>
          <template slot-scope="s">{{ parsePrescTemplatePreview(s.row.ptContent) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template slot-scope="s">
            <el-button type="warning" size="mini" @click="applyPrescriptionTemplate(s.row)">选用</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer">
        <el-button @click="prescTemplateDlgVisible=false">取消</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "DealOrder",
  data() {
    return {
      oId: null, pId: null, dId: "", dName: "", pName: "", pGender: "", pPhone: "", pAge: "",
      nowDate: "",
      submitting: false,
      // 门诊病历
      emr: { chiefComplaint: "", presentIllness: "", pastHistory: "", physicalExam: "", diagnosis: "", treatmentPlan: "" },
      // 药品目录
      drugData: [], drugTotal: 0, pageNumber: 1, size: 10, queryDrug: "",
      drugBuyData: [], drugTotalPrice: 0,
      drugTypeFilter: '',
      // 检查项目
      checkData: [], checkTotal: 0, checkPageNumber: 1, checkSize: 10, queryCheck: "",
      checkBuyData: [], checkTotalPrice: 0,
      // 处方对话框
      westernPrescDlg: false,
      chinesePrescDlg: false,
      chineseDosage: 10,
      chineseSelectDrug: '',
      chineseAddDosage: 10,
      chineseDrugList: [],
      formulaItems: [],
      prescriptionForm: { drId: "", drName: "", drPrice: 0, drType: 1, pdUsage: "", pdRoute:"", pdDosage: "", pdFrequency: "", pdTiming:"", pdSkinTest:0, pdDays: 3, pdNote: "" },
      // 病历模板
      emrTemplateDlgVisible: false,
      emrTemplateLoading: false,
      emrTemplateList: [],
      // 处方模板
      prescTemplateDlgVisible: false,
      prescTemplateLoading: false,
      prescTemplateList: []
    };
  },
  computed: {
    formulaSubtotal() {
      return this.formulaItems.reduce((s, item) => s + item.dosage * item.drPrice, 0).toFixed(2);
    }
  },
  methods: {
    nowDay() { const d=new Date(); return d.getFullYear()+"-"+(d.getMonth()+1).toString().padStart(2,'0')+"-"+d.getDate().toString().padStart(2,'0'); },
    async requestPatient() {
      try {
        const res = await request.get("doctor/findPatientById", { params: { pId: this.pId } });
        if (res.data.status === 200) {
          const p = res.data.data;
          this.pName = p.pName; this.pGender = p.pGender; this.pPhone = p.pPhone; this.pAge = p.pAge;
        }
      } catch(e) {}
    },
    async requestDrug() {
      try {
        const res = await request.get("drug/findAllDrugs", { params: { pageNumber: this.pageNumber, size: this.size, query: this.queryDrug, typeFilter: this.drugTypeFilter } });
        if (res.data.status === 200) {
          const d = res.data.data;
          this.drugData = d.drugs || d.records || []; this.drugTotal = d.total || 0;
        }
      } catch(e) {}
    },
    async requestCheck() {
      try {
        const res = await request.get("check/findAllChecks", { params: { pageNumber: this.checkPageNumber, size: this.checkSize, query: this.queryCheck } });
        if (res.data.status === 200) {
          const d = res.data.data;
          this.checkData = d.checks || d.records || []; this.checkTotal = d.total || 0;
        }
      } catch(e) {}
    },
    drugPageChange(p) { this.pageNumber = p; this.requestDrug(); },
    checkPageChange(p) { this.checkPageNumber = p; this.requestCheck(); },
    // 处方
    showPrescriptionDialog(row) {
      // 重置处方表单，避免中西药字段残留污染
      this.prescriptionForm = {
        drId:row.drId,drName:row.drName,drPrice:row.drPrice,drType:row.drType,
        drControlled:row.drControlled,drAntibioticLevel:row.drAntibioticLevel,
        drContraindication:row.drContraindication,
        pdUsage:"",pdRoute:"",pdDosage:"",
        pdFrequency:"",pdTiming:"",pdSkinTest:0,pdDays:3,pdNote:"",
        // 中药专用字段清零
        pdTcmGroupNo:"",pdDecoctionMethod:""
      };
      this.formulaItems = [];
      this.chineseAddDosage = 10;
      this.chineseSelectDrug = '';
      if (row.drType === 2) {
        // 加载所有中药供方剂选择
        request.get("drug/findAllDrugs", { params: { pageNumber:1, size:200, query:"", typeFilter:2 } }).then(res => {
          this.chineseDrugList = res.data.data.drugs || res.data.data.records || [];
        });
        this.chinesePrescDlg = true;
      } else {
        this.westernPrescDlg = true;
      }
    },
    confirmWesternPresc() {
      if (!this.prescriptionForm.pdUsage || !this.prescriptionForm.pdDosage || !this.prescriptionForm.pdFrequency) {
        return this.$message.warning("请填写完整的用药信息（用法、用量、频次）");
      }
      const freqNum = { qd: 1, bid: 2, tid: 3, qid: 4, qn: 1, prn: 1 };
      const timesPerDay = freqNum[this.prescriptionForm.pdFrequency] || 1;
      const qty = timesPerDay * this.prescriptionForm.pdDays;
      const idx = this.drugBuyData.findIndex(d => d.drId === this.prescriptionForm.drId && d.drType !== 2);
      if (idx >= 0) {
        this.drugBuyData[idx].pdQuantity += qty;
        this.drugBuyData[idx].pdDays += this.prescriptionForm.pdDays;
      } else {
        this.prescriptionForm.pdQuantity = qty;
        this.drugBuyData.push({ ...this.prescriptionForm, drType: 1 });
      }
      this.recalcDrugTotal();
      this.westernPrescDlg = false;
      this.$message.success("已加入西药处方");
    },
    // 中药方剂方法
    onChineseDrugSelected(drId) {
      const drug = this.chineseDrugList.find(d => d.drId === drId);
      if (drug) {
        this.prescriptionForm.drId = drug.drId;
        this.prescriptionForm.drName = drug.drName;
        this.prescriptionForm.drPrice = drug.drPrice;
      }
    },
    addToFormula() {
      if (!this.prescriptionForm.drId) return this.$message.warning("请先选择一味中药");
      if (!this.chineseAddDosage || this.chineseAddDosage < 1) return this.$message.warning("请填写剂量");
      const drug = this.chineseDrugList.find(d => d.drId === this.prescriptionForm.drId);
      if (!drug) return this.$message.warning("药品不存在");
      if (this.formulaItems.some(item => item.drId === drug.drId)) return this.$message.warning("该药品已在方剂中");
      this.formulaItems.push({
        drId: drug.drId, drName: drug.drName, drPrice: drug.drPrice,
        dosage: this.chineseAddDosage
      });
      this.chineseSelectDrug = '';
      this.prescriptionForm.drId = '';
      this.$message.success("已加入：" + drug.drName + " " + this.chineseAddDosage + "g");
    },
    removeFromFormula(idx) {
      this.formulaItems.splice(idx, 1);
    },
    confirmChinesePresc() {
      if (this.formulaItems.length === 0) return this.$message.warning("请先加入至少一味中药");
      if (!this.prescriptionForm.pdUsage) return this.$message.warning("请选择煎法");
      if (!this.prescriptionForm.pdDays || this.prescriptionForm.pdDays < 1) return this.$message.warning("请填写剂数");
      // 每味药分别加入 drugBuyData，标记为中药方剂模式
      this.formulaItems.forEach(item => {
        const qty = item.dosage * this.prescriptionForm.pdDays;
        const idx = this.drugBuyData.findIndex(d => d.drId === item.drId && d.drType === 2);
        if (idx >= 0) {
          this.drugBuyData[idx].pdDosage = item.dosage.toString();
          this.drugBuyData[idx].pdDays += this.prescriptionForm.pdDays;
          this.drugBuyData[idx].pdQuantity += qty;
        } else {
          this.drugBuyData.push({
            drId: item.drId, drName: item.drName, drPrice: item.drPrice,
            drType: 2,
            pdUsage: this.prescriptionForm.pdUsage,
            pdRoute: "经口",
            pdDosage: item.dosage.toString(),
            pdFrequency: '',
            pdTiming: this.prescriptionForm.pdTiming || '',
            pdSkinTest: 0,
            pdTcmGroupNo: "TCM-" + this.oId,
            pdDecoctionMethod: this.prescriptionForm.pdUsage,
            pdDays: this.prescriptionForm.pdDays,
            pdQuantity: qty,
            pdNote: this.prescriptionForm.pdNote || ''
          });
        }
      });
      this.recalcDrugTotal();
      const count = this.formulaItems.length;
      const days = this.prescriptionForm.pdDays;
      this.formulaItems = [];
      this.chinesePrescDlg = false;
      this.$message.success("已加入中药方剂（共" + count + "味药×" + days + "剂）");
    },
    deleteDrug(idx) {
      this.drugBuyData.splice(idx, 1);
      this.recalcDrugTotal();
    },
    recalcDrugTotal() {
      this.drugTotalPrice = this.drugBuyData.reduce((s, d) => s + d.pdQuantity * d.drPrice, 0);
    },
    // 检查项目
    addCheck(row) {
      if (this.checkBuyData.some(c => c.chId === row.chId)) return this.$message.info("已添加该项目");
      this.checkBuyData.push({ ...row });
      this.checkTotalPrice = this.checkBuyData.reduce((s, c) => s + parseFloat(c.chPrice || 0), 0);
    },
    deleteCheck(idx) {
      this.checkBuyData.splice(idx, 1);
      this.checkTotalPrice = this.checkBuyData.reduce((s, c) => s + parseFloat(c.chPrice || 0), 0);
    },
    // 病历模板
    openEmrTemplateDialog() {
      this.emrTemplateDlgVisible = true;
      this.emrTemplateLoading = true;
      this.emrTemplateList = [];
      request.get("emrTemplate/findAll").then(res => {
        if (res.data.status === 200) {
          this.emrTemplateList = res.data.data || [];
        } else {
          this.$message.warning(res.data.msg || "获取模板失败");
        }
      }).catch(() => {
        this.$message.error("请求失败");
      }).finally(() => {
        this.emrTemplateLoading = false;
      });
    },
    applyEmrTemplate(row) {
      if (row.chiefComplaint) this.emr.chiefComplaint = row.chiefComplaint;
      if (row.presentIllness) this.emr.presentIllness = row.presentIllness;
      if (row.pastHistory) this.emr.pastHistory = row.pastHistory;
      if (row.physicalExam) this.emr.physicalExam = row.physicalExam;
      if (row.diagnosis) this.emr.diagnosis = row.diagnosis;
      if (row.treatmentPlan) this.emr.treatmentPlan = row.treatmentPlan;
      this.emrTemplateDlgVisible = false;
      this.$message.success("已应用病历模板");
    },
    // 处方模板
    openPrescriptionTemplateDialog() {
      this.prescTemplateDlgVisible = true;
      this.prescTemplateLoading = true;
      this.prescTemplateList = [];
      request.get("prescriptionTemplate/findAll").then(res => {
        if (res.data.status === 200) {
          this.prescTemplateList = res.data.data || [];
        } else {
          this.$message.warning(res.data.msg || "获取模板失败");
        }
      }).catch(() => {
        this.$message.error("请求失败");
      }).finally(() => {
        this.prescTemplateLoading = false;
      });
    },
    parsePrescTemplateDrugCount(content) {
      if (!content) return 0;
      try {
        const arr = typeof content === 'string' ? JSON.parse(content) : content;
        if (Array.isArray(arr)) return arr.length;
        // 也支持逗号分隔格式
        if (typeof content === 'string' && content.includes(',')) return content.split(',').length;
        return 0;
      } catch(e) { return 0; }
    },
    parsePrescTemplatePreview(content) {
      if (!content) return '无内容';
      try {
        const arr = typeof content === 'string' ? JSON.parse(content) : content;
        if (Array.isArray(arr)) return arr.map(function(d) { return d.drName || d.name || ''; }).filter(Boolean).join('、');
        return '无内容';
      } catch(e) {
        // JSON解析失败则将原字符串截取作为预览
        return String(content).substring(0, 50);
      }
    },
    applyPrescriptionTemplate(row) {
      let content = row.ptContent;
      if (!content) { this.$message.warning("模板内容为空"); return; }
      try {
        const drugs = typeof content === 'string' ? JSON.parse(content) : content;
        if (!Array.isArray(drugs) || drugs.length === 0) { this.$message.warning("模板内容为空"); return; }
        drugs.forEach(function(d) {
          const qty = (d.pdDays || 1) * (d.pdQuantity || 1);
          d.pdQuantity = qty;
          d.drPrice = d.drPrice || d.price || 0;
          var existingIdx = -1;
          for (var i = 0; i < this.drugBuyData.length; i++) {
            if (this.drugBuyData[i].drId === d.drId) { existingIdx = i; break; }
          }
          if (existingIdx >= 0) {
            this.drugBuyData[existingIdx].pdQuantity += qty;
            this.drugBuyData[existingIdx].pdDays = (this.drugBuyData[existingIdx].pdDays || 0) + (d.pdDays || 1);
          } else {
            this.drugBuyData.push({ ...d, drType: d.drType || 1 });
          }
        }.bind(this));
        this.recalcDrugTotal();
        this.prescTemplateDlgVisible = false;
        this.$message.success("已应用处方模板，共 " + drugs.length + " 种药品");
      } catch(e) {
        this.$message.error("处方模板数据格式错误");
      }
    },
    // 提交
    async submitClick() {
      this.submitting = true;
      try {
        // 1. 保存门诊病历
        if (this.emr.chiefComplaint || this.emr.diagnosis) {
          await request.post("emr/save", {
            oId: this.oId, pId: this.pId, dId: this.dId,
            chiefComplaint: this.emr.chiefComplaint, presentIllness: this.emr.presentIllness,
            pastHistory: this.emr.pastHistory, physicalExam: this.emr.physicalExam,
            diagnosis: this.emr.diagnosis, treatmentPlan: this.emr.treatmentPlan
          });
        }
        // 2. 保存处方明细
        if (this.drugBuyData.length > 0) {
          await request.post("prescription/save", { oId: this.oId, details: this.drugBuyData });
        }
        // 3. 更新订单到orders表（兼容已有功能）
        const drugStr = this.drugBuyData.map(d => {
          if (d.drType === 2) {
            return `${d.drName} ${d.pdDosage}g/剂 × ${d.pdDays}剂，${d.pdUsage}，${d.pdNote || ''}`;
          }
          return `${d.drName}*${d.drPrice}*${d.pdQuantity} ${d.pdUsage} ${d.pdDosage} ${d.pdFrequency}`;
        }).join("；");
        const checkStr = this.checkBuyData.map(c => `${c.chName}*${c.chPrice}`).join("；");
        const totalPrice = this.drugTotalPrice + this.checkTotalPrice;
        await request.post("order/updateOrder", {
          oId: this.oId, pId: this.pId, dId: this.dId,
          oRecord: this.emr.chiefComplaint + "；" + this.emr.diagnosis,
          oDrug: drugStr ? `药品：${drugStr} 总价${this.drugTotalPrice.toFixed(2)}元` : "",
          oCheck: checkStr ? `检查：${checkStr} 总价${this.checkTotalPrice.toFixed(2)}元` : "",
          oTotalPrice: totalPrice,
          oAdvice: this.emr.treatmentPlan
        });
        this.$message.success("病历提交成功！请通知患者登录系统自助缴费！");
        this.$router.push("/orderToday");
      } catch(e) {
        
        this.$message.error("提交失败，请重试");
      }
      this.submitting = false;
    }
  },
  created() {
    this.nowDate = this.nowDay();
    const token = getToken();
    if (token) {
      const decoded = jwtDecode(token);
      this.dName = decoded.dName; this.dId = decoded.dId;
    }
    this.oId = parseInt(this.$route.query.oId);
    this.pId = parseInt(this.$route.query.pId);
    this.requestPatient();
    this.requestDrug();
    this.requestCheck();
  }
};
</script>




