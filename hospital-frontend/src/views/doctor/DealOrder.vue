<!--
 * 诊断页面
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
          <el-button size="small" type="primary" @click="submitClick">提交</el-button>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="7">
          患者性别：<el-input disabled  v-model="pGender" class="orderInput"></el-input>
        </el-col>
        <el-col :span="7">
          联系方式：<el-input disabled v-model="pPhone" class="orderInput"></el-input>
        </el-col>
        <el-col :span="7">
          医生姓名：<el-input disabled v-model="dName" class="orderInput"></el-input>
        </el-col>
        <el-col :span="3">
          <el-button size="small" type="warning" @click="openReason">病因编写</el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 病因编写对话框 -->
    <el-dialog title="病因编写" :visible.sync="reasonFormVisible" width="50%">
      <el-form :model="reasonForm" label-width="80px">
        <el-form-item label="病因">
          <el-input type="textarea" v-model="reasonForm.oRecord" :rows="4"></el-input>
        </el-form-item>
        <el-form-item label="检查项目">
          <el-select v-model="reasonForm.oCheck" placeholder="请选择检查项目">
            <el-option
              v-for="item in checkOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="总费用">
          <el-input v-model="reasonForm.oTotalPrice" type="number"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="reasonFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitReason">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
  name: "dealOrder",
  data() {
    return {
      oId: '',
      pId: '',
      dId: 0,
      pName: '',
      pGender: '',
      pPhone: '',
      dName: '',
      reasonFormVisible: false,
      reasonForm: {
        oRecord: '',
        oCheck: '',
        oTotalPrice: ''
      },
      checkOptions: [
        {label: '血常规检查', value: '血常规检查'},
        {label: '尿常规检查', value: '尿常规检查'},
        {label: '心电图检查', value: '心电图检查'},
        {label: 'B超检查', value: 'B超检查'},
        {label: 'CT检查', value: 'CT检查'}
      ]
    };
  },
  methods: {
    submitClick() {
      // 提交逻辑
      if (!this.reasonForm.oRecord || !this.reasonForm.oCheck || !this.reasonForm.oTotalPrice) {
        this.$message.warning('请先填写完整病因信息');
        return;
      }
      
      request.post('/doctor/updateOrder', {
        oId: this.oId,
        oRecord: this.reasonForm.oRecord,
        oCheck: this.reasonForm.oCheck,
        oTotalPrice: this.reasonForm.oTotalPrice
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
    openReason() {
      // 打开病因编写对话框逻辑
      this.reasonFormVisible = true;
    },
    submitReason() {
      // 提交病因表单
      if (!this.reasonForm.oRecord || !this.reasonForm.oCheck || !this.reasonForm.oTotalPrice) {
        this.$message.warning('请填写完整信息');
        return;
      }
      this.reasonFormVisible = false;
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