<!--
 * 增强版诊断页面 - 支持开药和开检查项目
-->
<template>
  <div>
    <el-card>
      <div slot="header" class="clearfix">
        <span>挂号信息</span>
      </div>
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
    </el-card>

    <!-- 诊断编写区域 -->
    <el-card style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>诊断编写</span>
      </div>

      <el-tabs v-model="activeTab">
        <!-- 病因部分 -->
        <el-tab-pane label="病因" name="reason">
          <el-form :model="diagnosisForm" label-width="80px">
            <el-form-item label="病因">
              <el-input
                type="textarea"
                v-model="diagnosisForm.oRecord"
                :rows="4"
                placeholder="请输入病因">
              </el-input>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 开药部分 -->
        <el-tab-pane label="开药" name="drug">
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="grid-content bg-purple">
                <el-card class="box-card">
                  <div slot="header">
                    <span>药物列表</span>
                    <el-input
                      placeholder="搜索药物"
                      v-model="drugQuery"
                      style="width: 200px; float: right;"
                      @keyup.enter.native="searchDrugs">
                      <el-button slot="append" icon="el-icon-search" @click="searchDrugs"></el-button>
                    </el-input>
                  </div>
                  <el-table
                    :data="drugData"
                    height="300"
                    border
                    @selection-change="handleDrugSelectionChange"
                    ref="drugTable">
                    <el-table-column type="selection" width="55"></el-table-column>
                    <el-table-column prop="drId" label="编号" width="80"></el-table-column>
                    <el-table-column prop="drName" label="药品名"></el-table-column>
                    <el-table-column prop="drPrice" label="单价" width="80"></el-table-column>
                    <el-table-column prop="drUnit" label="单位" width="80"></el-table-column>
                  </el-table>
                  <el-pagination
                    @size-change="handleDrugSizeChange"
                    @current-change="handleDrugCurrentChange"
                    :current-page="drugPageNumber"
                    :page-sizes="[5, 10, 20]"
                    :page-size="drugPageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="drugTotal">
                  </el-pagination>
                </el-card>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="grid-content bg-purple-light">
                <el-card class="box-card">
                  <div slot="header">
                    <span>已选药物</span>
                  </div>
                  <el-table :data="selectedDrugs" height="300" border>
                    <el-table-column prop="drName" label="药品名"></el-table-column>
                    <el-table-column prop="drPrice" label="单价"></el-table-column>
                    <el-table-column prop="quantity" label="数量">
                      <template slot-scope="scope">
                        <el-input-number
                          v-model="scope.row.quantity"
                          :min="1"
                          :max="100"
                          @change="calculateDrugPrice(scope.row)"
                          size="mini"
                          style="width: 95px;"
                        >
                        </el-input-number>
                      </template>
                    </el-table-column>
                    <el-table-column prop="totalPrice" label="小计"></el-table-column>
                    <el-table-column label="操作" width="80">
                      <template slot-scope="scope">
                        <el-button
                          type="danger"
                          size="mini"
                          @click="removeDrug(scope.row)">删除</el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                  <div style="margin-top: 10px;">
                    <strong>药物总价：{{ drugTotalPrice }} 元</strong>
                  </div>
                </el-card>
              </div>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- 检查项目部分 -->
        <el-tab-pane label="检查项目" name="check">
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="grid-content bg-purple">
                <el-card class="box-card">
                  <div slot="header">
                    <span>检查项目列表</span>
                    <el-input
                      placeholder="搜索检查项目"
                      v-model="checkQuery"
                      style="width: 200px; float: right;"
                      @keyup.enter.native="searchChecks">
                      <el-button slot="append" icon="el-icon-search" @click="searchChecks"></el-button>
                    </el-input>
                  </div>
                  <el-table
                    :data="checkData"
                    height="300"
                    border
                    @selection-change="handleCheckSelectionChange"
                    ref="checkTable">
                    <el-table-column type="selection" width="55"></el-table-column>
                    <el-table-column prop="chId" label="编号" width="80"></el-table-column>
                    <el-table-column prop="chName" label="项目名"></el-table-column>
                    <el-table-column prop="chPrice" label="价格" width="100"></el-table-column>
                  </el-table>
                  <el-pagination
                    @size-change="handleCheckSizeChange"
                    @current-change="handleCheckCurrentChange"
                    :current-page="checkPageNumber"
                    :page-sizes="[5, 10, 20]"
                    :page-size="checkPageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="checkTotal">
                  </el-pagination>
                </el-card>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="grid-content bg-purple-light">
                <el-card class="box-card">
                  <div slot="header">
                    <span>已选检查项目</span>
                  </div>
                  <el-table :data="selectedChecks" height="300" border>
                    <el-table-column prop="chName" label="项目名"></el-table-column>
                    <el-table-column prop="chPrice" label="价格"></el-table-column>
                    <el-table-column label="操作" width="80">
                      <template slot-scope="scope">
                        <el-button
                          type="danger"
                          size="mini"
                          @click="removeCheck(scope.row)">删除</el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                  <div style="margin-top: 10px;">
                    <strong>检查总价：{{ checkTotalPrice }} 元</strong>
                  </div>
                </el-card>
              </div>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>

      <!-- 总费用显示 -->
      <el-row style="margin-top: 20px;">
        <el-col :span="24">
          <div style="text-align: right; padding: 10px; background-color: #f5f5f5;">
            <strong>总费用：{{ totalFee }} 元</strong>
          </div>
        </el-col>
      </el-row>

      <!-- 提交按钮 -->
      <el-row style="margin-top: 20px;">
        <el-col :span="24" style="text-align: center;">
          <el-button type="primary" @click="submitClick">提交</el-button>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";

export default {
  name: "dealOrderEnhanced",
  data() {
    return {
      oId: '',
      pId: '',
      dId: 0,
      pName: '',
      pGender: '',
      pPhone: '',
      dName: '',
      activeTab: 'reason',
      diagnosisForm: {
        oRecord: ''
      },

      // 药物相关数据
      drugData: [],
      drugPageNumber: 1,
      drugPageSize: 5,
      drugTotal: 0,
      drugQuery: '',
      selectedDrugs: [],
      drugTotalPrice: 0,

      // 检查项目相关数据
      checkData: [],
      checkPageNumber: 1,
      checkPageSize: 5,
      checkTotal: 0,
      checkQuery: '',
      selectedChecks: [],
      checkTotalPrice: 0
    };
  },
  computed: {
    // 计算总费用
    totalFee() {
      const drugFee = parseFloat(this.drugTotalPrice) || 0;
      const checkFee = parseFloat(this.checkTotalPrice) || 0;
      return (drugFee + checkFee).toFixed(2);
    }
  },
  methods: {
    submitClick() {
      // 提交逻辑
      if (!this.diagnosisForm.oRecord) {
        this.$message.warning('请先填写病因');
        return;
      }

      // 验证患者是否存在
      if (!this.pId) {
        this.$message.error('患者ID不能为空');
        return;
      }


      // 准备要提交的数据
      const submitData = {
        oId: this.oId,
        pId: this.pId,
        dId: this.dId,
        oRecord: this.diagnosisForm.oRecord,
        oEnd: this.getOrderEndTime(), // 使用当前时间作为订单结束时间
        oState: 1, // 设置订单状态为已处理
        oPriceState: 0, // 设置支付状态为未支付
        oTotalPrice: parseFloat(this.totalFee),
        oDrug: this.selectedDrugs.length > 0
          ? this.selectedDrugs.map(drug => `${drug.drName},${drug.quantity}盒,${(drug.drPrice * drug.quantity).toFixed(2)}元`).join(';')
          : '',
        oCheck: this.selectedChecks.length > 0
          ? this.selectedChecks.map(check => `${check.chName},1次,${check.chPrice}元`).join(';')
          : ''
      };

      request.post('/order/updateOrder', submitData).then(res => {
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

    // 获取患者详细信息
    requestPatientInfo() {
      // 使用正确的接口地址和参数
      request.get("doctor/findPatientById", {
        params: {
          pId: this.pId
        },
        headers: {
          token: getToken() // 添加 token 认证
        }
      }).then(res => {
        if (res.data.status !== 200) {
          return this.$message.error("获取患者信息失败");
        }
        const patient = res.data.data;
        this.pName = patient.pName;
        this.pGender = patient.pGender;
        this.pPhone = patient.pPhone;
      }).catch(error => {
        console.error(error);
        this.$message.error("获取患者信息失败");
      });
    },

    //token解码
    tokenDecode(token){
      if (token !== null)
        return jwtDecode(token);
    },

    // 药物相关方法
    searchDrugs() {
      request.get("/drug/findAllDrugs", {
        params: {
          pageNumber: this.drugPageNumber,
          size: this.drugPageSize,
          query: this.drugQuery
        }
      }).then(res => {
        if (res.data.status === 200) {
          this.drugData = res.data.data.drugs;
          this.drugTotal = res.data.data.total;
        } else {
          this.$message.error("获取药物列表失败");
        }
      }).catch(error => {
        console.error(error);
        this.$message.error("获取药物列表失败");
      });
    },

    handleDrugSizeChange(val) {
      this.drugPageSize = val;
      this.searchDrugs();
    },

    handleDrugCurrentChange(val) {
      this.drugPageNumber = val;
      this.searchDrugs();
    },

    handleDrugSelectionChange(selection) {
      // 处理药物选择变化，实现勾选和删除的联动

      // 找出新增选中的项（在新selection中但不在已选列表中）
      const newlySelected = selection.filter(sel =>
        !this.selectedDrugs.some(selected => selected.drId === sel.drId)
      );

      // 找出取消选中的项（在已选列表中但不在新的selection中）
      const deselected = this.selectedDrugs.filter(selected =>
        !selection.some(sel => sel.drId === selected.drId)
      );

      // 添加新增选中的项
      newlySelected.forEach(drug => {
        this.selectedDrugs.push({
          ...drug,
          quantity: 1,
          totalPrice: drug.drPrice
        });
      });

      // 删除取消选中的项
      deselected.forEach(drug => {
        const index = this.selectedDrugs.findIndex(d => d.drId === drug.drId);
        if (index > -1) {
          this.selectedDrugs.splice(index, 1);
        }
      });

      this.calculateDrugTotalPrice();
    },

    calculateDrugPrice(drug) {
      // 计算单个药物的小计
      drug.totalPrice = (drug.drPrice * drug.quantity).toFixed(2);
      this.calculateDrugTotalPrice();
    },

    calculateDrugTotalPrice() {
      // 计算药物总价
      this.drugTotalPrice = this.selectedDrugs.reduce((total, drug) => {
        return total + parseFloat(drug.totalPrice);
      }, 0).toFixed(2);
    },

    removeDrug(drug) {
      // 移除药物时，也要取消表格中的勾选状态
      const index = this.selectedDrugs.findIndex(d => d.drId === drug.drId);
      if (index > -1) {
        this.selectedDrugs.splice(index, 1);
        this.calculateDrugTotalPrice();
      }

      // 同步取消表格中的勾选状态
      this.$nextTick(() => {
        // 取消表格中对应的勾选
        const table = this.$refs.drugTable;
        if (table) {
          // 找到表格数据中对应的项
          const row = this.drugData.find(item => item.drId === drug.drId);
          if (row) {
            table.toggleRowSelection(row, false);
          }
        }
      });
    },

    // 检查项目相关方法
    searchChecks() {
      request.get("/check/findAllChecks", {
        params: {
          pageNumber: this.checkPageNumber,
          size: this.checkPageSize,
          query: this.checkQuery
        }
      }).then(res => {
        if (res.data.status === 200) {
          this.checkData = res.data.data.checks;
          this.checkTotal = res.data.data.total;
        } else {
          this.$message.error("获取检查项目列表失败");
        }
      }).catch(error => {
        console.error(error);
        this.$message.error("获取检查项目列表失败");
      });
    },

    handleCheckSizeChange(val) {
      this.checkPageSize = val;
      this.searchChecks();
    },

    handleCheckCurrentChange(val) {
      this.checkPageNumber = val;
      this.searchChecks();
    },

    handleCheckSelectionChange(selection) {
      // 处理检查项目选择变化，实现勾选和删除的联动

      // 找出新增选中的项（在新selection中但不在已选列表中）
      const newlySelected = selection.filter(sel =>
        !this.selectedChecks.some(selected => selected.chId === sel.chId)
      );

      // 找出取消选中的项（在已选列表中但不在新的selection中）
      const deselected = this.selectedChecks.filter(selected =>
        !selection.some(sel => sel.chId === selected.chId)
      );

      // 添加新增选中的项
      newlySelected.forEach(check => {
        this.selectedChecks.push({...check});
      });

      // 删除取消选中的项
      deselected.forEach(check => {
        const index = this.selectedChecks.findIndex(c => c.chId === check.chId);
        if (index > -1) {
          this.selectedChecks.splice(index, 1);
        }
      });

      this.calculateCheckTotalPrice();
    },

    calculateCheckTotalPrice() {
      // 计算检查总价
      this.checkTotalPrice = this.selectedChecks.reduce((total, check) => {
        return total + parseFloat(check.chPrice);
      }, 0).toFixed(2);
    },

    removeCheck(check) {
      // 移除检查项目时，也要取消表格中的勾选状态
      const index = this.selectedChecks.findIndex(c => c.chId === check.chId);
      if (index > -1) {
        this.selectedChecks.splice(index, 1);
        this.calculateCheckTotalPrice();
      }

      // 同步取消表格中的勾选状态
      this.$nextTick(() => {
        // 取消表格中对应的勾选
        const table = this.$refs.checkTable;
        if (table) {
          // 找到表格数据中对应的项
          const row = this.checkData.find(item => item.chId === check.chId);
          if (row) {
            table.toggleRowSelection(row, false);
          }
        }
      });
    },

    clearSelectedItems() {
      // 清空已选项
      this.selectedDrugs = [];
      this.selectedChecks = [];
      this.drugTotalPrice = 0;
      this.checkTotalPrice = 0;
    },

    // 获取订单结束时间
    getOrderEndTime() {
      // 获取当前时间并格式化为 YYYY-MM-DD HH:mm 格式
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}`;
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

    // 初始化加载药物和检查项目
    this.searchDrugs();
    this.searchChecks();
  },
};
</script>

<style scoped>
.orderInput {
  width: 150px;
}
.box-card {
  margin-bottom: 20px;
}
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}
</style>