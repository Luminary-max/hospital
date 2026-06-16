<template>
    <div>
        <el-card>
            <div slot="header">
                <span><i class="el-icon-office-building"></i> 我的留观/输液记录</span>
            </div>
            <el-table :data="bedData" border stripe v-if="bedData.length > 0">
                <el-table-column label="编号" prop="bId"></el-table-column>
                <el-table-column label="类型" prop="bType">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.bType === 0 || scope.row.bType == null" type="primary">观察床</el-tag>
                        <el-tag v-else type="success">输液椅</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="医生" prop="dName"></el-table-column>
                <el-table-column label="原因" prop="bReason"></el-table-column>
                <el-table-column label="开始时间" prop="bStart"></el-table-column>
                <el-table-column label="状态" prop="bState">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.bState === 1" type="danger">使用中</el-tag>
                        <el-tag v-else type="success">已结束</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="观察记录" prop="bObsNote" show-overflow-tooltip></el-table-column>
            </el-table>
            <el-empty v-else description="暂无留观/输液记录"></el-empty>
        </el-card>
    </div>
</template>
<script>
import jwtDecode from "jwt-decode";
import request from "@/utils/request.js";
import { getToken } from "@/utils/storage.js";
export default {
    name: "MyBed",
    data() {
        return {
            bedData:[],
            userId:1,
        }
    },
    methods: {
        //请求留观/输液信息
        requestBed(){
            request.get("bed/findBedByPid", {
                params: {
                    pId: this.userId
                }
            })
            .then(res => {
                if(res.data.status !== 200)
                return this.$message.error("请求数据失败");
                this.bedData = res.data.data;
            })

        },
           //token解码
    tokenDecode(token){
      if (token !== null)
      return jwtDecode(token);
    },

    },
    created(){
           // 解码token
            this.userId = this.tokenDecode(getToken()).pId;
            this.requestBed();
    }
}
</script>
<style scoped lang="scss">
.el-table {
    margin-top: 20px;
    margin-bottom: 20px;
}
</style>