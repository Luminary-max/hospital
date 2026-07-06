<template>
  <div class="login-wrap">
    <div class="login-container">
      <div class="login-left">
        <img src="@/assets/17.jpg" alt="" class="login-img" />
      </div>
      <div class="login-right">
        <el-form :model="loginForm" class="login-form" :rules="loginRules" ref="ruleForm">
          <div class="login-title">医院门诊管理系统</div>
          <div class="login-subtitle">欢迎登录</div>
          <el-form-item prop="id">
            <el-input v-model="loginForm.id" placeholder="请输入账号" size="large">
              <i slot="prefix" class="el-input__icon el-icon-user"></i>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" size="large" placeholder="请输入密码" clearable show-password>
              <i slot="prefix" class="el-input__icon el-icon-lock"></i>
            </el-input>
          </el-form-item>
          <el-form-item prop="role">
            <el-select v-model="loginForm.role" placeholder="请选择登录角色" size="large" class="role-select" clearable>
              <el-option label="患者" value="患者"></el-option>
              <el-option label="医生" value="医生"></el-option>
              <el-option label="管理员" value="管理员"></el-option>
              <el-option label="护士" value="护士"></el-option>
              <el-option label="药师" value="药师"></el-option>
              <el-option label="收费员" value="收费员"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-btn" size="large" @click="submitLoginForm('ruleForm')">登 录</el-button>
          </el-form-item>
          <div class="login-register">
            还没有账号？
            <span class="link" @click="registerFormVisible = true">立即注册</span>
          </div>
        </el-form>
      </div>
    </div>

    <el-dialog title="患者注册" :visible.sync="registerFormVisible" width="520px" top="5vh">
      <el-form :model="registerForm" :rules="registerRules" ref="registerForm" label-width="80px" size="small">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="账号" prop="pId"><el-input v-model.number="registerForm.pId"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="姓名" prop="pName"><el-input v-model="registerForm.pName"></el-input></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="密码" prop="pPassword"><el-input v-model="registerForm.pPassword" type="password"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="性别"><el-radio v-model="registerForm.pGender" label="男">男</el-radio><el-radio v-model="registerForm.pGender" label="女">女</el-radio></el-form-item></el-col>
        </el-row>
        <el-form-item label="出生日期" prop="pBirthday"><el-date-picker v-model="registerForm.pBirthday" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width:100%"></el-date-picker></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="手机号" prop="pPhone"><el-input v-model="registerForm.pPhone"></el-input></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="邮箱号" prop="pEmail"><el-input v-model="registerForm.pEmail"></el-input></el-form-item></el-col>
        </el-row>
        <el-form-item label="身份证号" prop="pCard"><el-input v-model="registerForm.pCard"></el-input></el-form-item>
      </el-form>
      <div slot="footer"><el-button @click="registerFormVisible=false">取消</el-button><el-button type="primary" @click="registerClick('registerForm')">注册</el-button></div>
    </el-dialog>
  </div>
</template>
<script>
import request from "@/utils/request.js";
import { setToken } from "@/utils/storage.js";
export default {
  name: "Login",
  data() {
    var validateMoblie = (rule, value, callback) => {
      if (!value) { callback(new Error("请输入手机号")); return; }
      if (!/^1(3[0-9]|4[5,7]|5[0,1,2,3,5,6,7,8,9]|6[2,5,6,7]|7[0,1,7,8]|8[0-9]|9[1,8,9])\d{8}$/.test(value)) callback(new Error("手机号格式错误"));
      else callback();
    };
    var validateCard = (rule, value, callback) => {
      if (!value) { callback(new Error("请输入身份证号")); return; }
      if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(value)) callback(new Error("身份证号格式错误"));
      else callback();
    };
    return {
      loginForm: { id: "202601", password: "123456", role: "" },
      loginRules: {
        id: [{required:true,message:'请输入账号/姓名',trigger:'blur'}],
        password: [{required:true,message:'请输入密码',trigger:'blur'}],
        role: [{required:true,message:'请选择登录角色',trigger:'change'}]
      },
      registerFormVisible: false,
      registerForm: { pGender: "男" },
      registerRules: {
        pId: [{required:true,message:'请输入账号',trigger:'blur'},{type:'number',message:'账号必须数字'}],
        pPassword: [{required:true,message:'请输入密码',trigger:'blur'},{min:4,max:50,message:'4-50个字符'}],
        pName: [{required:true,message:'请输入姓名',trigger:'blur'}],
        pEmail: [{required:true,message:'请输入邮箱',trigger:'blur'},{type:'email',message:'邮箱格式错误'}],
        pPhone: [{validator:validateMoblie}],
        pCard: [{validator:validateCard}],
        pBirthday: [{required:true,message:'选择出生日期',trigger:'blur'}]
      }
    };
  },
  methods: {
    registerClick(fn) {
      this.$refs[fn].validate(v => {
        if (!v) return;
        request.get("patient/addPatient", {params:this.registerForm}).then(r => {
          if (r.data.status!==200) return this.$message.error("账号或邮箱已被占用！");
          this.registerFormVisible=false; this.$message.success("注册成功！");
        });
      });
    },
    submitLoginForm(fn) {
      this.$refs[fn].validate(v => {
        if (!v) return;
        var p = new URLSearchParams();
        const role = this.loginForm.role;
        if (role==='管理员') {
          p.append('aId',this.loginForm.id); p.append('aPassword',this.loginForm.password);
          request.post('admin/login',p).then(r=>{if(r.data.status!=200)return this.$message.error("用户名或密码错误");setToken(r.data.data.token);this.$router.push('/adminLayout');}).catch(()=>this.$message.error("登录失败"));}
        else if (role==='护士' || role==='药师' || role==='收费员') {
          var staffMap = {'护士':'nurse','药师':'pharmacist','收费员':'cashier'};
          var targetMap = {'护士':'/triageRecordList','药师':'/pharmacyDispensingList','收费员':'/cashierSettlement'};
          p.append('staffId',this.loginForm.id); p.append('staffPassword',this.loginForm.password); p.append('staffRole',staffMap[role]);
          request.post('staff/login',p).then(r=>{if(r.data.status!=200)return this.$message.error("用户名或密码错误");setToken(r.data.data.token);this.$router.push(targetMap[role]);}).catch(()=>this.$message.error("登录失败"));}
        else if (role==='医生') { p.append('dId',this.loginForm.id); p.append('dPassword',this.loginForm.password);
          request.post('doctor/login',p).then(r=>{if(r.data.status!=200)return this.$message.error("用户名或密码错误");setToken(r.data.data.token);this.$router.push('/doctorLayout');}).catch(()=>this.$message.error("登录失败"));}
        else { p.append('pId',this.loginForm.id); p.append('pPassword',this.loginForm.password);
          request.post('patient/login',p).then(r=>{if(r.data.status!=200)return this.$message.error("用户名或密码错误");setToken(r.data.data.token);this.$router.push('/patientLayout');}).catch(()=>this.$message.error("登录失败"));}
      });
    }
  }
};
</script>
<style scoped>
.login-wrap {
  height:100vh; display:flex; align-items:center; justify-content:center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position:relative;
}
.login-wrap::before {
  content:''; position:absolute; top:0; left:0; right:0; bottom:0;
  background: url("../assets/img/login-bg.svg") center/cover; opacity:0.1;
}
.login-container {
  display:flex; background:#fff; border-radius:16px; overflow:hidden;
  width:780px; min-height:480px; box-shadow:0 20px 60px rgba(0,0,0,0.15);
  position:relative; z-index:1;
}
.login-left { width:380px; overflow:hidden; }
.login-img { width:100%; height:100%; object-fit:cover; }
.login-right { flex:1; display:flex; align-items:center; justify-content:center; padding:40px; }
.login-form { width:100%; max-width:320px; }
.login-title { font-size:24px; font-weight:700; color:#303133; text-align:center; }
.login-subtitle { font-size:14px; color:#909399; text-align:center; margin-bottom:30px; margin-top:4px; }
.login-btn { width:100%; }
.role-select { width:100%; }
.login-register { text-align:center; font-size:13px; color:#909399; margin-top:10px; }
.login-register .link { color:#409EFF; cursor:pointer; }
.login-register .link:hover { color:#66b1ff; }
</style>
