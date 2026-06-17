<template>
  <el-card>
    <div slot="header"><i class="el-icon-office-building"></i> 留观/输液位管理</div>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query" placeholder="搜索患者ID" clearable size="small" class="search-input" @keyup.enter.native="requestBeds">
          <el-button slot="append" icon="el-icon-search" @click="requestBeds"></el-button>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-tag class="total-tag">共 {{ total }} 条</el-tag>
        <el-button type="primary" size="small" @click="addFormVisible = true"><i class="el-icon-plus"></i> 增加床位</el-button>
      </div>
    </div>
    <el-table :data="bedData" stripe border>
      <el-table-column label="编号" prop="bId" width="90"></el-table-column>
      <el-table-column label="类型" width="80"><template slot-scope="s"><el-tag :type="s.row.bType===1?'success':'primary'" size="mini">{{ s.row.bType===1?'输液椅':'观察床' }}</el-tag></template></el-table-column>
      <el-table-column label="患者" prop="pId" width="80"><template slot-scope="s"><span v-if="s.row.pId!==-1">{{ s.row.pId }}</span><span v-else>-</span></template></el-table-column>
      <el-table-column label="开始时间" prop="bStart" width="160"></el-table-column>
      <el-table-column label="申请理由" prop="bReason" min-width="160" show-overflow-tooltip></el-table-column>
      <el-table-column label="状态" width="80"><template slot-scope="s"><el-tag :type="s.row.bState===1?'danger':'success'" size="mini">{{ s.row.bState===1?'已占用':'空闲' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="95" fixed="right" align="center">
        <template slot-scope="s">
          <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="deleteDialog(s.row.bId)" title="删除"></el-button>
          <el-button type="warning" size="mini" icon="el-icon-refresh" circle @click="emptyDialog(s.row.bId)" title="清空"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" background
      layout="total,sizes,prev,pager,next,jumper" :current-page="pageNumber" :page-size="size"
      :page-sizes="[5,10,20,50]" :total="total"></el-pagination>

    <el-dialog title="增加床位" :visible.sync="addFormVisible" width="400px">
      <el-form :model="addForm" :rules="rules" ref="addForm" label-width="80px" size="small">
        <el-form-item label="编号" prop="bId"><el-input v-model="addForm.bId"></el-input></el-form-item>
        <el-form-item label="类型"><el-select v-model="addForm.bType" style="width:100%"><el-option :value="0" label="观察床"></el-option><el-option :value="1" label="输液椅"></el-option></el-select></el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="addFormVisible=false">取消</el-button><el-button type="primary" @click="addBed('addForm')">确定</el-button></div>
    </el-dialog>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "BedList",
  data() {
    return {
      pageNumber:1, size:10, query:"", bedData:[], total:0,
      addFormVisible:false, addForm:{bType:0},
      rules: { bId:[{required:true,message:"请输入编号",trigger:"blur"}] }
    };
  },
  methods: {
    requestBeds() { request.get("bed/findAllBeds",{params:{pageNumber:this.pageNumber,size:this.size,query:this.query}}).then(r=>{this.bedData=r.data.data.beds||[];this.total=r.data.data.total||0;}); },
    addBed(fn) { this.$refs[fn].validate(v=>{if(!v)return;request.get("bed/addBed",{params:{bId:this.addForm.bId,pId:-1,dId:-1,bType:this.addForm.bType}}).then(r=>{if(r.data.status!==200)return this.$message.error("编号已占用");this.addFormVisible=false;this.requestBeds();this.$message.success("增加成功");});}); },
    emptyBed(id){request.get("bed/emptyBed",{params:{bId:id}}).then(()=>this.requestBeds());},
    emptyDialog(id){this.$confirm("确定清空?","提示",{type:"warning"}).then(()=>{this.emptyBed(id);this.$message.success("清空成功");}).catch(()=>{});},
    deleteBed(id){request.get("bed/deleteBed",{params:{bId:id}}).then(()=>this.requestBeds());},
    deleteDialog(id){this.$confirm("确定删除?","提示",{type:"warning"}).then(()=>{this.deleteBed(id);this.$message.success("删除成功");}).catch(()=>{});},
    handleSizeChange(s){this.size=s;this.requestBeds();},
    handleCurrentChange(p){this.pageNumber=p;this.requestBeds();}
  },
  created(){this.requestBeds();}
};
</script>