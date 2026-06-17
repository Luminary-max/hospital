<template>
  <div>
    <el-card>
      <el-table :data="orderData" stripe border style="width:100%">
        <el-table-column prop="oId" label="单号" width="80" align="center"></el-table-column>
        <el-table-column prop="pName" label="姓名" width="70" align="center"></el-table-column>
        <el-table-column prop="dName" label="医生" width="80" align="center"></el-table-column>
        <el-table-column prop="oStart" label="挂号时间" min-width="160"></el-table-column>
        <el-table-column prop="oEnd" label="结束时间" min-width="140"></el-table-column>
        <el-table-column prop="oTotalPrice" label="费用" width="80" align="center"></el-table-column>
        <el-table-column label="缴费" width="100" align="center">
          <template slot-scope="s">
            <el-tag v-if="s.row.oPriceState===1" type="success">已缴费</el-tag>
            <el-button v-else-if="s.row.oState===1" type="warning" size="mini" @click="priceClick(s.row.oId,s.row.dId)">点击缴费</el-button>
            <span v-else style="color:#ccc;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="s">
            <el-tag v-if="s.row.oState===1&&s.row.oPriceState===1" type="success">已完成</el-tag>
            <el-tag v-else-if="s.row.oPriceState===0&&s.row.oState===0" type="danger">未完成</el-tag>
            <el-tag v-else type="warning">待缴费</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报告单" width="90" align="center">
          <template slot-scope="s">
            <el-button v-if="s.row.oState===1&&s.row.oPriceState===1" type="success" size="mini" @click="seeReport(s.row.oId)">查看</el-button>
            <span v-else style="color:#ccc;">-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog title="用户评价" :visible.sync="starVisible" width="400px">
      <div style="text-align:center;padding:10px;">
        <h4>请对医生 {{ dName }} 进行评价</h4>
        <el-rate v-model="star" style="margin-top:15px;"></el-rate>
      </div>
      <div slot="footer"><el-button @click="starVisible=false">取消</el-button><el-button type="primary" @click="starClick">确定</el-button></div>
    </el-dialog>
  </div>
</template>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
    name: "MyOrder",
    data() {
        return {
            userId: 1,
            orderData: [],
            star: 5,
            starVisible: false,
            dId: 1,
            dName: "",
        };
    },
    methods: {
        //评价点击确认
        starClick() {
            console.log(this.star);
            console.log(this.dId);
            request
                .get("doctor/updateStar", {
                    params: {
                        dId: this.dId,
                        dStar: this.star,
                    },
                })
                .then((res) => {
                    if (res.data.status !== 200)
                        return this.$message.error("评价失败");
                    this.$message.success("谢谢您的评价");
                    this.starVisible = false;
                });
        },
        //查看报告单
        seeReport(id) {
            window.location.href =
                "http://localhost:9999/patient/pdf?oId=" + id;
        },
        //点击缴费按钮
        priceClick(oId, dId) {
            request
                .get("order/updatePrice", {
                    params: {
                        oId: oId,
                    },
                })
                .then((res) => {
                    if (res.data.status !== 200) {
                        this.$message.error("请求数据失败");
                        return;
                    }
                    this.$message.success("单号 " + oId + " 缴费成功！");
                    request
                        .get("admin/findDoctor", {
                            params: {
                                dId: dId,
                            },
                        })
                        .then((res) => {
                            if (res.data.status !== 200)
                                return this.$message.error("请求数据失败");
                            this.dId = res.data.data.dId;
                            this.dName = res.data.data.dName;
                        });
                    this.starVisible = true;
                    this.requestOrder();
                });
        },
        //请求挂号信息
        requestOrder() {
            request
                .get("patient/findOrderByPid", {
                    params: {
                        pId: this.userId,
                    },
                })
                .then((res) => {
                    if (res.data.status !== 200)
                        this.$message.error("请求数据失败");
                    this.orderData = res.data.data;
                    //this.orderData.dSection = res.data.data.map(item => item.doctor.dSection);
                    //console.log(res.data.data.map(item => item.doctor.dSection));
                    console.log(this.orderData.oId);
                    console.log(this.orderData.pName);
                    console.log(res);
                });
        },
        //token解码
        tokenDecode(token) {
            if (token !== null) return jwtDecode(token);
        },
    },
    created() {
        // 解码token
        //this.orderData.pName = this.tokenDecode(getToken()).pName;
        //this.orderData.pCard = this.tokenDecode(getToken()).pCard;
        this.userId = this.tokenDecode(getToken()).pId;
        console.log(this.orderData.pName);
        //this.orderData.pName = "dasda"
        this.requestOrder();
    },
};
</script>
<style lang="scss" scoped>
.el-dialog div {
    text-align: center;
    margin-bottom: 8px;
}
</style>