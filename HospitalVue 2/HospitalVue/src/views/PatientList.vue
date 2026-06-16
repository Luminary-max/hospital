<template>
    <el-card>
        <!-- 搜索栏 -->
        <el-row type="flex">
            <el-col :span="6">
                <el-input v-model="query" placeholder="请输入姓名查询">
                    <el-button slot="append" icon="el-icon-search" @click="requestPatients"></el-button>
                </el-input>
            </el-col>
            <el-col :span="4">
                <el-button type="primary" @click="addFormVisible = true" style="margin-left:10px;">
                    <i class="el-icon-circle-plus-outline"></i> 增加患者
                </el-button>
            </el-col>
        </el-row>
        <!-- 表格 -->
        <el-table :data="patientData" stripe style="width: 100%" border>
            <el-table-column prop="pId" label="账号" width="80"></el-table-column>
            <el-table-column prop="pName" label="姓名" width="80"></el-table-column>
            <el-table-column prop="pGender" label="性别" width="55"></el-table-column>
            <el-table-column prop="pAge" label="年龄" width="55"></el-table-column>
            <el-table-column prop="pCard" label="证件号" width="140"></el-table-column>
            <el-table-column prop="pPhone" label="手机号" width="110"></el-table-column>
            <el-table-column prop="pInsuranceId" label="医保号" width="120"></el-table-column>
            <el-table-column prop="pInsuranceType" label="医保类型" width="80"></el-table-column>
            <el-table-column prop="pNation" label="民族" width="55"></el-table-column>
            <el-table-column prop="pMaritalStatus" label="婚姻" width="55"></el-table-column>
            <el-table-column prop="pBloodType" label="血型" width="55"></el-table-column>
            <el-table-column prop="pAddress" label="地址" min-width="160"></el-table-column>
            <el-table-column prop="pState" label="状态" width="70">
                <template slot-scope="scope">
                    <el-tag type="success" v-if="scope.row.pState === 1">正常</el-tag>
                    <el-tag type="danger" v-else>已删除</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
                <template slot-scope="scope">
                    <el-button type="success" size="mini" icon="el-icon-edit" @click="modifyDialog(scope.row.pId)"></el-button>
                    <el-button type="danger" size="mini" icon="el-icon-delete" @click="deleteDialog(scope.row.pId)"></el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            background
            layout="total, sizes, prev, pager, next, jumper"
            :current-page="pageNumber"
            :page-size="size"
            :page-sizes="[1, 2, 4, 8, 16]"
            :total="total">
        </el-pagination>

        <!-- 增加患者对话框 -->
        <el-dialog title="增加患者" :visible.sync="addFormVisible" width="700px">
            <el-form :model="addForm" :rules="rules" ref="addForm">
                <el-row :gutter="20">
                    <el-col :span="8"><el-form-item label="账号" prop="pId" label-width="60px"><el-input v-model.number="addForm.pId"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="姓名" prop="pName" label-width="60px"><el-input v-model="addForm.pName"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="性别" prop="pGender" label-width="60px"><el-select v-model="addForm.pGender" style="width:100%"><el-option label="男" value="男"></el-option><el-option label="女" value="女"></el-option></el-select></el-form-item></el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="8"><el-form-item label="出生日期" prop="pBirthday" label-width="80px"><el-input v-model="addForm.pBirthday" placeholder="1990-01-01"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="证件号" label-width="60px"><el-input v-model="addForm.pCard"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="手机号" prop="pPhone" label-width="60px"><el-input v-model="addForm.pPhone"></el-input></el-form-item></el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="8"><el-form-item label="邮箱" label-width="60px"><el-input v-model="addForm.pEmail"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="医保号" label-width="60px"><el-input v-model="addForm.pInsuranceId" placeholder="医保卡号"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="医保类型" label-width="70px"><el-select v-model="addForm.pInsuranceType" style="width:100%"><el-option label="城镇职工" value="城镇职工"></el-option><el-option label="城乡居民" value="城乡居民"></el-option><el-option label="自费" value="自费"></el-option></el-select></el-form-item></el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="8"><el-form-item label="民族" label-width="60px"><el-select v-model="addForm.pNation" style="width:100%"><el-option label="汉族" value="汉族"></el-option><el-option label="蒙古族" value="蒙古族"></el-option><el-option label="回族" value="回族"></el-option><el-option label="藏族" value="藏族"></el-option><el-option label="维吾尔族" value="维吾尔族"></el-option><el-option label="苗族" value="苗族"></el-option></el-select></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="婚姻" label-width="60px"><el-select v-model="addForm.pMaritalStatus" style="width:100%"><el-option label="未婚" value="未婚"></el-option><el-option label="已婚" value="已婚"></el-option><el-option label="离异" value="离异"></el-option><el-option label="丧偶" value="丧偶"></el-option></el-select></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="血型" label-width="60px"><el-select v-model="addForm.pBloodType" style="width:100%"><el-option label="A型" value="A"></el-option><el-option label="B型" value="B"></el-option><el-option label="AB型" value="AB"></el-option><el-option label="O型" value="O"></el-option></el-select></el-form-item></el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="8"><el-form-item label="联系人" label-width="60px"><el-input v-model="addForm.pContactPerson"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="联系人电话" label-width="80px"><el-input v-model="addForm.pContactPhone"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="家庭住址" label-width="70px"><el-input v-model="addForm.pAddress"></el-input></el-form-item></el-col>
                </el-row>
            </el-form>
            <div slot="footer">
                <el-button @click="addFormVisible = false">取消</el-button>
                <el-button type="primary" @click="addPatient('addForm')">确定</el-button>
            </div>
        </el-dialog>

        <!-- 修改患者对话框 -->
        <el-dialog title="修改患者信息" :visible.sync="modifyFormVisible" width="700px">
            <el-form :model="modifyForm" :rules="rules" ref="modifyForm">
                <el-row :gutter="20">
                    <el-col :span="8"><el-form-item label="账号" label-width="60px"><el-input v-model.number="modifyForm.pId" disabled></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="姓名" prop="pName" label-width="60px"><el-input v-model="modifyForm.pName"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="性别" label-width="60px"><el-select v-model="modifyForm.pGender" style="width:100%"><el-option label="男" value="男"></el-option><el-option label="女" value="女"></el-option></el-select></el-form-item></el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="8"><el-form-item label="出生日期" label-width="80px"><el-input v-model="modifyForm.pBirthday" placeholder="1990-01-01"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="证件号" label-width="60px"><el-input v-model="modifyForm.pCard"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="手机号" prop="pPhone" label-width="60px"><el-input v-model="modifyForm.pPhone"></el-input></el-form-item></el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="8"><el-form-item label="邮箱" label-width="60px"><el-input v-model="modifyForm.pEmail"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="医保号" label-width="60px"><el-input v-model="modifyForm.pInsuranceId"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="医保类型" label-width="70px"><el-select v-model="modifyForm.pInsuranceType" style="width:100%"><el-option label="城镇职工" value="城镇职工"></el-option><el-option label="城乡居民" value="城乡居民"></el-option><el-option label="自费" value="自费"></el-option></el-select></el-form-item></el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="8"><el-form-item label="民族" label-width="60px"><el-select v-model="modifyForm.pNation" style="width:100%"><el-option label="汉族" value="汉族"></el-option><el-option label="蒙古族" value="蒙古族"></el-option><el-option label="回族" value="回族"></el-option><el-option label="藏族" value="藏族"></el-option><el-option label="维吾尔族" value="维吾尔族"></el-option><el-option label="苗族" value="苗族"></el-option></el-select></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="婚姻" label-width="60px"><el-select v-model="modifyForm.pMaritalStatus" style="width:100%"><el-option label="未婚" value="未婚"></el-option><el-option label="已婚" value="已婚"></el-option><el-option label="离异" value="离异"></el-option><el-option label="丧偶" value="丧偶"></el-option></el-select></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="血型" label-width="60px"><el-select v-model="modifyForm.pBloodType" style="width:100%"><el-option label="A型" value="A"></el-option><el-option label="B型" value="B"></el-option><el-option label="AB型" value="AB"></el-option><el-option label="O型" value="O"></el-option></el-select></el-form-item></el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="8"><el-form-item label="联系人" label-width="60px"><el-input v-model="modifyForm.pContactPerson"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="联系人电话" label-width="80px"><el-input v-model="modifyForm.pContactPhone"></el-input></el-form-item></el-col>
                    <el-col :span="8"><el-form-item label="家庭住址" label-width="70px"><el-input v-model="modifyForm.pAddress"></el-input></el-form-item></el-col>
                </el-row>
            </el-form>
            <div slot="footer">
                <el-button @click="modifyFormVisible = false">取消</el-button>
                <el-button type="primary" @click="modifyPatient('modifyForm')">确定</el-button>
            </div>
        </el-dialog>
    </el-card>
</template>
<script>
import request from "@/utils/request.js";
export default {
    name: "PatientList",
    data() {
        return {
            pageNumber: 1,
            size: 8,
            query: "",
            patientData: [],
            total: 0,
            addFormVisible: false,
            addForm: {},
            rules: {
                pId: [{ required: true, message: "请输入账号", trigger: "blur" }],
                pName: [{ required: true, message: "请输入姓名", trigger: "blur" }],
                pGender: [{ required: true, message: "请选择性别", trigger: "change" }],
                pPhone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
                pBirthday: [{ required: true, message: "请输入出生日期", trigger: "blur" }],
            },
            modifyFormVisible: false,
            modifyForm: {},
        };
    },
    methods: {
        requestPatients() {
            request.get("admin/findAllPatients", { params: { pageNumber: this.pageNumber, size: this.size, query: this.query } })
                .then((res) => {
                    this.patientData = res.data.data.patients;
                    this.total = res.data.data.total;
                });
        },
        addPatient(formName) {
            this.$refs[formName].validate((valid) => {
                if (!valid) return;
                request.get("admin/addPatient", { params: this.addForm })
                    .then((res) => {
                        if (res.data.status !== 200) return this.$message.error(res.data.msg || "增加失败");
                        this.addFormVisible = false;
                        this.requestPatients();
                        this.$message.success("增加患者成功！");
                    });
            });
        },
        modifyDialog(id) {
            request.get("admin/findPatient", { params: { pId: id } })
                .then((res) => {
                    if (res.data.status !== 200) return this.$message.error("请求数据失败");
                    this.modifyForm = res.data.data;
                    this.modifyFormVisible = true;
                });
        },
        modifyPatient(formName) {
            this.$refs[formName].validate((valid) => {
                if (!valid) return;
                request.get("admin/modifyPatient", { params: this.modifyForm })
                    .then((res) => {
                        if (res.data.status !== 200) return this.$message.error("修改失败");
                        this.modifyFormVisible = false;
                        this.requestPatients();
                        this.$message.success("修改患者信息成功！");
                    });
            });
        },
        deletePatient(id) {
            request.get("admin/deletePatient", { params: { pId: id } }).then((res) => { this.requestPatients(); });
        },
        deleteDialog(id) {
            this.$confirm("此操作将删除该患者信息, 是否继续?", "提示", { confirmButtonText: "确定", cancelButtonText: "取消", type: "warning" })
                .then(() => { this.deletePatient(id); this.$message.success("删除成功!"); })
                .catch(() => { this.$message.info("已取消删除"); });
        },
        handleSizeChange(size) { this.size = size; this.requestPatients(); },
        handleCurrentChange(num) { this.pageNumber = num; this.requestPatients(); },
    },
    created() { this.requestPatients(); },
};
</script>
<style scoped lang="scss">
.el-table { margin-top: 20px; margin-bottom: 20px; }
</style>
