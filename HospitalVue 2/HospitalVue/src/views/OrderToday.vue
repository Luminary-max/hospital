<template>
    <el-card>
        <div slot="header">
            <span><i class="el-icon-news"></i> 门诊接诊 — 今日待诊列表</span>
            <el-button type="primary" size="small" style="float:right;" @click="refreshList" icon="el-icon-refresh">刷新</el-button>
        </div>
        <el-table :data="orderData" stripe border style="width:100%">
            <el-table-column label="序号" type="index" width="60" align="center"></el-table-column>
            <el-table-column label="排队号码" prop="oQueueNumber" width="100" align="center">
                <template slot-scope="s"><el-tag v-if="s.row.oQueueNumber" type="warning">{{ s.row.oQueueNumber }}</el-tag><span v-else>---</span></template>
            </el-table-column>
            <el-table-column label="单号" prop="oId" width="80" align="center"></el-table-column>
            <el-table-column label="患者" prop="pName" width="90" align="center"></el-table-column>
            <el-table-column label="医生" prop="dName" width="90" align="center"></el-table-column>
            <el-table-column label="挂号时间" prop="oStart" min-width="180"></el-table-column>
            <el-table-column label="状态" width="80" align="center">
                <template slot-scope="s"><el-tag :type="s.row.oState===0?'warning':'success'" size="mini">{{ s.row.oState===0?'待接诊':'已完成' }}</el-tag></template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="center">
                <template slot-scope="s">
                    <el-button type="warning" size="mini" @click="dealClick(s.row.oId,s.row.pId)" v-if="s.row.oState===0"><i class="el-icon-monitor"></i> 接诊</el-button>
                    <el-button type="primary" size="mini" @click="dealClick(s.row.oId,s.row.pId)" v-if="s.row.oState===3||s.row.oState===4">继续</el-button>
                    <el-button type="success" size="mini" @click="dealAgainClick(s.row.oId,s.row.pId)" v-if="s.row.oState===3||s.row.oState===4">复诊</el-button>
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


