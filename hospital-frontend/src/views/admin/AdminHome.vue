<!--
 * 管理员首页
-->
<template>
  <div>
    <el-row>
      <el-col :span="24">
        <div style="text-align: center;width: 100%;font-size: 32px;">
          <h1><span style="color: rebeccapurple;">欢迎登录医院管理系统</span></h1>
        </div>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <div style="text-align: center;width: 100%;">
          <img src="@/assets/homepage-back.jpg" style="width: 941px;margin-left: 25px;">
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>

export default {
  name: "AdminHome",
  data() {
    return {
    };
  },
  methods: {
  },
  created() {
  },
};
</script>
