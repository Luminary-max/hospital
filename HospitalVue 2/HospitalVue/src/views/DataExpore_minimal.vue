<template>
  <div style="padding:16px;">
    <el-card>
      <div slot="header"><i class="el-icon-s-data"></i> 数据统计</div>
      <el-tabs v-model="activeTab" @tab-click="onTabClick">
        <el-tab-pane label="医院运营统计" name="hospital">
          <div>
            <el-row :gutter="20">
              <el-col :span="6"><el-card shadow="hover" style="border-radius:8px;"><div style="display:flex;align-items:center;"><div style="width:50px;height:50px;border-radius:10px;background:#409EFF;display:flex;align-items:center;justify-content:center;"><i class="el-icon-user" style="font-size:24px;color:#fff;"></i></div><div style="margin-left:16px;"><div style="font-size:22px;font-weight:700;">{{ todayVisits }}</div><div style="font-size:13px;color:#909399;">今日挂号人数</div></div></div></el-card></el-col>
              <el-col :span="6"><el-card shadow="hover" style="border-radius:8px;"><div style="display:flex;align-items:center;"><div style="width:50px;height:50px;border-radius:10px;background:#67C23A;display:flex;align-items:center;justify-content:center;"><i class="el-icon-s-finance" style="font-size:24px;color:#fff;"></i></div><div style="margin-left:16px;"><div style="font-size:22px;font-weight:700;">{{ todayDrugIncome }}</div><div style="font-size:13px;color:#909399;">药费+检查费</div></div></div></el-card></el-col>
              <el-col :span="6"><el-card shadow="hover" style="border-radius:8px;"><div style="display:flex;align-items:center;"><div style="width:50px;height:50px;border-radius:10px;background:#E6A23C;display:flex;align-items:center;justify-content:center;"><i class="el-icon-s-finance" style="font-size:24px;color:#fff;"></i></div><div style="margin-left:16px;"><div style="font-size:22px;font-weight:700;">{{ todayRegIncome }}</div><div style="font-size:13px;color:#909399;">今日挂号费</div></div></div></el-card></el-col>
              <el-col :span="6"><el-card shadow="hover" style="border-radius:8px;"><div style="display:flex;align-items:center;"><div style="width:50px;height:50px;border-radius:10px;background:#F56C6C;display:flex;align-items:center;justify-content:center;"><i class="el-icon-user" style="font-size:24px;color:#fff;"></i></div><div style="margin-left:16px;"><div style="font-size:22px;font-weight:700;">{{ totalPatients }}</div><div style="font-size:13px;color:#909399;">累计患者</div></div></div></el-card></el-col>
            </el-row>
            <el-row :gutter="20" style="margin-top:20px;">
              <el-col :span="12"><el-card><div slot="header">科室挂号占比<el-button size="mini" style="float:right;" plain @click="exportSectionData">导出</el-button></div><div id="deptPieChart" style="width:100%;height:380px;"></div></el-card></el-col>
              <el-col :span="12"><el-card><div slot="header">收入构成<el-button size="mini" style="float:right;" plain @click="exportIncomeData">导出</el-button></div><div id="incomeBarChart" style="width:100%;height:380px;"></div></el-card></el-col>
            </el-row>
            <el-row :gutter="20" style="margin-top:20px;">
              <el-col :span="12"><el-card><div slot="header">近20天挂号趋势</div><div id="visitTrendChart" style="width:100%;height:340px;"></div></el-card></el-col>
              <el-col :span="6"><el-card><div slot="header">患者性别比例</div><div id="genderChart" style="width:100%;height:340px;"></div></el-card></el-col>
              <el-col :span="6"><el-card><div slot="header">患者年龄分布</div><div id="ageChart" style="width:100%;height:340px;"></div></el-card></el-col>
            </el-row>
          </div>
        </el-tab-pane>
        <el-tab-pane label="医生个人统计" name="doctor">
          <div><el-form inline size="small"><el-form-item label="医生"><el-select v-model="selectedDoctor" filterable placeholder="选择医生" style="width:200px;" @change="loadDoctorStats"><el-option v-for="d in doctorList" :key="d.dId" :label="d.dName+' - '+d.dSection" :value="d.dId"></el-option></el-select></el-form-item><el-form-item label="周期"><el-select v-model="doctorPeriod" style="width:100px;" @change="loadDoctorStats"><el-option label="7天" value="7"></el-option><el-option label="30天" value="30"></el-option></el-select></el-form-item></el-form>
            <el-row :gutter="20" v-if="doctorStats"><el-col :span="8"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#409EFF;">{{ doctorStats.totalVisits }}</div><div style="color:#909399;">接诊总数</div></div></el-card></el-col>
              <el-col :span="8"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#67C23A;">¥{{ doctorStats.totalIncome }}</div><div style="color:#909399;">创收总额</div></div></el-card></el-col>
              <el-col :span="8"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#E6A23C;">{{ doctorStats.prescriptionCount }}</div><div style="color:#909399;">处方数</div></div></el-card></el-col>
            </el-row>
            <div v-if="!selectedDoctor" style="text-align:center;padding:60px 0;color:#999;">请选择医生查看统计</div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="药房统计" name="pharmacy">
          <div>
            <el-row :gutter="20"><el-col :span="6"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#F56C6C;">{{ pharmStats.expiredCount||0 }}</div><div style="color:#909399;">已过期批次</div></div></el-card></el-col>
              <el-col :span="6"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#E6A23C;">{{ pharmStats.expiringCount||0 }}</div><div style="color:#909399;">临期预警</div></div></el-card></el-col>
              <el-col :span="6"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#409EFF;">{{ pharmStats.drugTypeCount||0 }}</div><div style="color:#909399;">药品品种</div></div></el-card></el-col>
              <el-col :span="6"><el-card shadow="hover"><div style="text-align:center;"><div style="font-size:28px;font-weight:700;color:#67C23A;">{{ pharmStats.totalStock||0 }}</div><div style="color:#909399;">总库存</div></div></el-card></el-col>
            </el-row>
            <el-row :gutter="20" style="margin-top:20px;">
              <el-col :span="12"><el-card><div slot="header">库存预警</div><el-table pharmStats. lowStockList| | [ ] "   size="small"   border  max-height= " 300 " style="width:100%" ><el-table-column prop="drId"   label=" 编 号 "   width=" 70 " ></el-table-column><el-table-column prop="drName"   label=" 药 品 "   min-width=" 120 " ></el-table-column><el-table-column prop="drNumber"   label=" 库 存 "   width=" 60 " ></el-table-column><el-table-column prop="drMinStock"   label=" 下 限 "   width=" 60 " ></el-table-column></el-table></el-card></el-col>
              <el-col :span="12"><el-card><div slot="header">临期批次</div><el-table pharmStats. expiringBatches| | [ ] "   size="small"   border  max-height= " 300 " style="width:100%" ><el-table-column prop="drId"   label=" 药 品 "   width=" 70 " ></el-table-column><el-table-column prop="dbBatchNo"   label=" 批 号 "   width=" 100 " ></el-table-column><el-table-column prop="dbExpireDate"   label=" 过 期 日 期 "   width=" 100 " ></el-table-column><el-table-column prop="dbQuantity"   label=" 剩 余 "   width=" 60 " ></el-table-column></el-table></el-card></el-col>
            </el-row>
          </div>
        </el-tab-pane>
        <el-tab-pane label="收入分析" name="income">
          <div>
            <el-row :gutter="20"><el-col :span="14"><el-card><div slot="header">每日收入趋势<el-radio-group v-model="incomePeriod" @change="loadIncomeAnalysis" size="mini" style="float:right;"><el-radio-button label="7">7天</el-radio-button><el-radio-button label="20">20天</el-radio-button></el-radio-group></div><div id="incomeTrendChart" style="width:100%;height:400px;"></div></el-card></el-col>
              <el-col :span="10"><el-card style="margin-bottom:16px;"><div slot="header">收入构成</div><div id="incomeBreakdownChart" style="width:100%;height:260px;"></div></el-card>
                <el-card><div slot="header">收入汇总</div><el-row :gutter="10"><el-col :span="12" style="margin-bottom:8px;"><div style="font-size:12px;color:#909399;">总收入</div><div style="font-size:20px;font-weight:700;color:#409EFF;">¥{{ incomeSummary.total }}</div></el-col><el-col :span="12"><div style="font-size:12px;color:#909399;">日均收入</div><div style="font-size:16px;font-weight:600;">¥{{ incomeSummary.dailyAvg }}</div></el-col></el-row><el-row :gutter="10"><el-col :span="12"><div style="font-size:12px;color:#909399;">挂号费</div><div style="font-size:16px;font-weight:600;">¥{{ incomeSummary.registration }}</div></el-col><el-col :span="12"><div style="font-size:12px;color:#909399;">药费+检查费</div><div style="font-size:16px;font-weight:600;">¥{{ incomeSummary.drugCheck }}</div></el-col></el-row></el-card></el-col>
            </el-row>
            <el-card style="margin-top:16px;"><div slot="header">每日明细</div><el-table incomeDetailList"   size="small"   border style="width:100%"><el-table-column prop="date"   label=" 日 期 "   width=" 100 " ></el-table-column><el-table-column label=" 挂 号 费 "   width=" 120 " ><template slot-scope="s">¥{{ s.row.regFee }}</template></el-table-column><el-table-column label=" 药 费 + 检 查 费 "   width=" 120 " ><template slot-scope="s">¥{{ s.row.drugFee }}</template></el-table-column><el-table-column label=" 合 计 "   width=" 120 " ><template slot-scope="s">¥{{ (Number(s.row.regFee)+Number(s.row.drugFee)).toFixed(2) }}</template></el-table-column></el-table></el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>







