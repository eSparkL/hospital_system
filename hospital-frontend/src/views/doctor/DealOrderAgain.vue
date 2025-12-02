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
      </el-row>
      <el-row>
        <el-col :span="24">
          <div style="margin-top: 20px;">
            <div style="font-weight: bold; margin-bottom: 10px;">诊断及医生建议：</div>
            <el-input
              type="textarea"
              :rows="8"
              placeholder="请输入诊断及医生建议"
              v-model="advice">
            </el-input>
          </div>
        </el-col>
      </el-row>
      <el-row style="margin-top: 20px;">
        <el-col :span="24" style="text-align: center;">
          <el-button type="primary" size="small" style="font-size: 18px; padding: 12px 30px;" icon="iconfont icon-sure-button" @click="submitClick"> 提交</el-button>
        </el-col>
      </el-row>
    </el-card>
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
      dId: 0,
      pName: '',
      pGender: '',
      pPhone: '',
      dName: '',
      advice: '',
    };
  },
  methods: {
    submitClick() {
      // 提交逻辑
      if (!this.advice) {
        this.$message.warning('请先填写诊断及医生建议');
        return;
      }

      // 直接使用完整的API调用，包含所有必要参数
      request.post('order/updateOrder', {
        oId: this.oId,
        pId: this.pId,
        dId: this.dId,
        oAdvice: this.advice
      }).then(res => {
        // 注意这里我们不需要检查响应状态，因为这是附加的操作
        this.$message.success('提交成功');
        this.$router.push('/doctorOrder');
      }).catch(error => {
        console.error(error);
        this.$message.error('提交失败');
      });
    },
    // 获取患者详细信息
    requestPatientInfo() {
      request.get("doctor/findPatientById", {
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