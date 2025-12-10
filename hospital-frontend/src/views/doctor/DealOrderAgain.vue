<!--
 * 追诊页面

-->
<template>
  <div>
    <el-card>
      <el-row>
        <el-col :span="7">
          挂号单号：<el-input disabled v-model="oId" class="orderInput"></el-input>
        </el-col>
        <el-col :span="7">
          患者账号：<el-input disabled v-model="pId" class="orderInput"></el-input>
        </el-col>
        <el-col :span="7">
          患者姓名：<el-input disabled v-model="pName" class="orderInput"></el-input>
        </el-col>
        <el-col :span="3">
          <el-button type="primary" size="small" style="font-size: 18px;" icon="iconfont icon-sure-button" @click="submitClick"> 提交</el-button>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="7">
          患者性别：<el-input disabled v-model="pGender" class="orderInput"></el-input>
        </el-col>
        <el-col :span="7">
          联系方式：<el-input disabled v-model="pPhone" class="orderInput"></el-input>
        </el-col>
        <el-col :span="7">
          医生姓名：<el-input disabled v-model="dName" class="orderInput"></el-input>
        </el-col>
        <el-col :span="3">
          <el-button size="small" type="warning" style="font-size: 18px;" @click="openAdvice">
            <i class="iconfont icon-add-button" style="font-size: 20px;"></i>
            诊断/意见
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 病因编写对话框 -->
    <el-dialog title="诊断及医生建议编写" :visible.sync="adviceFormVisible">
      <el-input type="textarea" :rows="8" placeholder="请输入内容" v-model="advice"></el-input>
      <div slot="footer" class="dialog-footer">
        <el-button @click="adviceFormVisible = false" style="font-size: 18px;">
          <i class="iconfont icon-cancel-button" style="font-size: 20px;"></i> 取 消
        </el-button>
        <el-button type="primary" @click="holdAdvice">
          <i class="iconfont icon-sure-button" style="font-size: 20px;"></i>  保 存
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
import request from "@/utils/request.js";
export default {
  name: "dealOrderAgain",
  data() {
    return {
      oId: '',
      pId: '',
      pName: '',
      pGender: '',
      pAge: '',
      pPhone: '',
      cardNumber: '',
      sectionName: '',
      illnessDesc: '',
      dId: 0,
      dName: '',
      advice: '',
      adviceFormVisible: false,
    };
  },
  methods: {
    submitClick() {
      // 提交逻辑
      if (!this.advice) {
        this.$message.warning('请先填写诊断及医生建议');
        return;
      }
      
      request.post('/doctor/updateOrderAgain', {
        oId: this.oId,
        oRecord: this.advice
      }).then(res => {
        if (res.data.status !== 200) {
          this.$message.error(res.data.msg || '提交失败');
          return;
        }
        this.$message.success('提交成功');
        this.$router.push('/doctorOrder');
      }).catch(error => {
        console.error(error);
        this.$message.error('提交失败');
      });
    },
    openAdvice() {
      this.adviceFormVisible = true;
    },
    holdAdvice() {
      // 保存建议逻辑
      if (!this.advice) {
        this.$message.warning('请输入诊断及医生建议');
        return;
      }
      this.adviceFormVisible = false;
    },
    // 获取患者详细信息
    requestPatientInfo() {
      request.get("patient/findById", {
        params: {
          pId: this.pId
        }
      }).then(res => {
        if (res.data.status != 200) 
          return this.$message.error("获取患者信息失败");
        const patient = res.data.data;
        this.pName = patient.pName;
        this.pGender = patient.pGender;
        this.pPhone = patient.pPhone;
      });
    },
    //token解码
    tokenDecode(token){
      if (token !== null)
        return jwtDecode(token);
    }
  },
  created() {
    // 从路由参数中获取传递的值
    this.oId = this.$route.query.oId;
    this.pId = this.$route.query.pId;
    
    // 解码token获取医生信息
    const tokenData = this.tokenDecode(getToken());
    this.dId = tokenData.dId;
    this.dName = tokenData.dName;
    
    // 获取患者详细信息
    this.requestPatientInfo();
  },
};
</script>
<style scoped>
.orderInput {
  width: 150px;
}
</style>