<template>
  <el-card>
    <div slot="header"><span><i class="el-icon-bell"></i> 医院公告</span></div>
    <el-timeline>
      <el-timeline-item v-for="item in list" :key="item.ha_id" :timestamp="item.publish_time">
        <h3>{{ item.title }}</h3>
        <p>{{ item.content }}</p>
      </el-timeline-item>
    </el-timeline>
    <el-empty v-if="list.length === 0" description="暂无公告"></el-empty>
  </el-card>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "AnnouncementBoard",
  data() { return { role:"patient", list:[] }; },
  methods: {
    async loadData() {
      const res = await request.get("smart/announcement/list", { params:{ role:this.role } });
      if (res.data.status === 200) this.list = res.data.data || [];
    },
    detectRole() {
      const t = getToken(); if (!t) return;
      const d = jwtDecode(t);
      if (d.staffRole) this.role = d.staffRole;
      else if (d.dId) this.role = "doctor";
      else if (d.pId) this.role = "patient";
    }
  },
  created() { this.detectRole(); this.loadData(); }
};
</script>
<style scoped>h3{margin:0 0 8px;font-size:16px;}p{margin:0;color:#606266;line-height:1.7;}</style>
