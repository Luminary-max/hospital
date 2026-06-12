<template>
    <el-card>
        <div slot="header">
            <span><i class="el-icon-news"></i> 门诊接诊 — 今日待诊列表</span>
            <el-button type="primary" size="small" style="float:right;" @click="refreshList" icon="el-icon-refresh">刷新</el-button>
        </div>
        <el-table :data="orderData" stripe border>
            <el-table-column label="序号" type="index" width="60"></el-table-column>
            <el-table-column label="排队号码" prop="oQueueNumber" width="120">
                <template slot-scope="scope">
                    <el-tag type="warning" v-if="scope.row.oQueueNumber">{{ scope.row.oQueueNumber }}</el-tag>
                    <span v-else>---</span>
                </template>
            </el-table-column>
            <el-table-column label="挂号单号" prop="oId" width="100"></el-table-column>
            <el-table-column label="患者姓名" prop="pName" width="120"></el-table-column>
            <el-table-column label="医生姓名" prop="dName" width="120"></el-table-column>
            <el-table-column label="挂号时间" prop="oStart" width="200"></el-table-column>
            <el-table-column label="状态" width="100">
                <template slot-scope="scope">
                    <el-tag type="warning" v-if="scope.row.oState === 0">待接诊</el-tag>
                    <el-tag type="success" v-else>已完成</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
                <template slot-scope="scope">
                    <el-button type="warning" @click="dealClick(scope.row.oId, scope.row.pId)">
                        <i class="el-icon-monitor"></i> 处理
                    </el-button>
                    <el-button type="success" @click="dealAgainClick(scope.row.oId, scope.row.pId)" size="mini">
                        复诊
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
</template>
<script>
import jwtDecode from "jwt-decode";
import { getToken} from "@/utils/storage.js";
import request from "@/utils/request.js";
export default {
    name: "OrderToday",
    data() {
        return {
            userId: "",
            userName: "",
            today: "",
            orderData: [],
        }
    },
    methods: {
        dealClick(oId, pId){
            this.$router.push({ path: "/dealOrder", query: { oId, pId } });
        },
        dealAgainClick(oId, pId){
            this.$router.push({ path: "/dealOrderAgain", query: { oId, pId } });
        },
        refreshList() { this.requestOrder(); },
        requestOrder(){
            request.get("doctor/findOrderByNull", {
                params: { dId: this.userId, oStart: this.today }
            }).then(res => {
                if(res.data.status !== 200) return this.$message.error("获取数据失败");
                this.orderData = res.data.data;
            });
        },
        tokenDecode(token){
            return token ? jwtDecode(token) : null;
        },
        nowDay(){
            const nowDate = new Date();
            let date = {
                year: nowDate.getFullYear(),
                month: (nowDate.getMonth() + 1).toString().padStart(2, '0'),
                date: nowDate.getDate().toString().padStart(2, '0'),
            };
            this.today = date.year+"-"+date.month+"-"+date.date;
        },
    },
    created(){
        const decoded = this.tokenDecode(getToken());
        this.userId = decoded.dId;
        this.userName = decoded.dName;
        this.nowDay();
        this.requestOrder();
    },
}
</script>
