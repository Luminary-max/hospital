<template>
  <el-card>
    <div slot="header"><span><i class="el-icon-office-building"></i> 科室列表</span></div>
    <el-table :data="deptList" border stripe style="width:100%">
      <el-table-column prop="deId" label="科室编号" width="120"></el-table-column>
      <el-table-column prop="deName" label="科室名称" width="240"></el-table-column>
      <el-table-column prop="deNumber" label="医生人数" width="100"></el-table-column>
    </el-table>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "SectionList",
  data() { return { deptList: [] }; },
  created() {
    request.get("admin/findAllDoctors", { params: { pageNumber: 1, size: 200, query: "" } }).then(res => {
      if (res.data.status === 200) {
        const doctors = res.data.data.doctors || [];
        const deptMap = {};
        doctors.forEach(d => {
          if (d.dSection) {
            if (!deptMap[d.dSection]) deptMap[d.dSection] = { deName: d.dSection, count: 0 };
            deptMap[d.dSection].count++;
          }
        });
        this.deptList = Object.values(deptMap).map((v, i) => ({ deId: 'DE' + String(i + 1).padStart(4, '0'), deName: v.deName, deNumber: v.count }));
      }
    });
  }
};
</script>

