<template>
    <div>
        <el-card>
            <el-row type="flex">
                <el-col :span="6">
                    <el-input v-model="query" placeholder="请输入患者id查询">
                        <el-button slot="append" icon="el-icon-search" @click="requestOrders"></el-button>
                    </el-input>
                </el-col>
            </el-row>
            <el-table :data="orderData" stripe border style="width:100%">
                <el-table-column prop="oId" label="挂号单号" width="80"></el-table-column>
                <el-table-column label="本人姓名" width="80">
                    <template slot-scope="s">{{ s.row.dName || s.row.dId }}</template>
                </el-table-column>
                <el-table-column prop="pName" label="患者姓名" width="100"></el-table-column>
                <el-table-column prop="oStart" label="挂号时间" width="190"></el-table-column>
                <el-table-column prop="oEnd" label="结束时间" width="180"></el-table-column>
                <el-table-column prop="oRecord" label="病因" width="400"></el-table-column>
                <el-table-column prop="oDrug" label="药物" width="180"></el-table-column>
                <el-table-column prop="oCheck" label="检查项目" width="180"></el-table-column>
                <el-table-column prop="oTotalPrice" label="需交费用/元" width="80"></el-table-column>
                <el-table-column prop="oPriceState" label="缴费状态" width="100">
                    <template slot-scope="s">
                        <el-tag type="success" v-if="s.row.oPriceState === 1">已缴费</el-tag>
                        <el-tag type="danger" v-else>未缴费</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="oState" label="挂号状态" width="120">
                    <template slot-scope="s">
                        <el-tag v-if="s.row.oState === -1" type="danger">已取消</el-tag>
                        <el-tag v-else-if="s.row.oState === 0" type="warning">已挂号</el-tag>
                        <el-tag v-else-if="s.row.oState === 1" type="primary">已分诊</el-tag>
                        <el-tag v-else-if="s.row.oState === 2" type="">就诊中</el-tag>
                        <el-tag v-else-if="s.row.oState === 3" type="success">已开处方</el-tag>
                        <el-tag v-else-if="s.row.oState === 4" type="warning">待缴费</el-tag>
                        <el-tag v-else-if="s.row.oState === 5" type="success">已缴费</el-tag>
                        <el-tag v-else-if="s.row.oState === 6" type="success">已发药</el-tag>
                        <el-tag v-else-if="s.row.oState === 7" type="success">已完成</el-tag>
                        <el-tag v-else type="info">状态{{ s.row.oState }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="90" fixed="right">
                    <template slot-scope="s">
                        <el-button type="text" size="mini" @click="viewDetail(s.row)">查看详情</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" background
                layout="total, sizes, prev, pager, next, jumper" :current-page="pageNumber" :page-size="size"
                :page-sizes="[1, 2, 4, 8, 16]" :total="total">
            </el-pagination>
        </el-card>
    </div>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
    name: "DoctorOrder",
    data() { return { userId: 1, orderData: [], pageNumber: 1, size: 8, query: "", total: 3 }; },
    methods: {
        viewDetail(row) {
            this.$alert(`患者姓名：${row.pName || row.pId}\n挂号时间：${row.oStart}\n病因：${row.oRecord || '无'}\n药物：${row.oDrug || '无'}\n检查：${row.oCheck || '无'}`, `挂号单 #${row.oId}`, { confirmButtonText: '关闭' });
        },
        handleSizeChange(size) { this.size = size; this.requestOrders(); },
        handleCurrentChange(num) { this.pageNumber = num; this.requestOrders(); },
        requestOrders() {
            request.get("order/findOrderByDid", { params: { dId: this.userId, pageNumber: this.pageNumber, size: this.size, query: this.query } }).then(res => {
                if (res.data.status !== 200) this.$message.error("请求数据失败");
                this.orderData = res.data.data.records;
                this.total = res.data.data.total;
            });
        },
        tokenDecode(token) { if (token !== null) return jwtDecode(token); }
    },
    created() { this.userId = this.tokenDecode(getToken()).dId; this.requestOrders(); }
};
</script>
<style lang="scss" scoped>
.el-table { margin-top: 20px; margin-bottom: 20px; }
</style>
