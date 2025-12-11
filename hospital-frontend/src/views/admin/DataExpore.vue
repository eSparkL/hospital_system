<!--
 * 数据统计图
-->
<template>
  <div class="Echarts">
    <div id="orderSection" style="width: 1200px; height: 400px;"></div>
    <div id="orderLast10Days" style="width: 1200px; height: 400px;"></div>

    <!-- 新增 flex 行，用来放 性别 + 年龄 -->
    <div class="gender-age-row">
      <div id="patientGender" class="chart-box"></div>
      <div id="patientAge" class="chart-box"></div>
    </div>
  </div>


</template>
<script>
import request from "@/utils/request.js";
export default {
  name: "dataExpore",
  data() {
    return {
      sevenDate: [],
      sevenOrder: [],
    };
  },
  methods: {
    // 统计患者性别分布
    patientGender() {
      this.$nextTick(() => {
        const dom = document.getElementById("patientGender");

        if (!dom) {
          console.error("找不到 patientGender DOM");
          return;
        }

        const myChart = this.$echarts.init(dom);

        request.get("order/orderGender")
          .then(res => {
            const data = res.data.data || [];

            console.log("性别数据：", data);

            const option = {
              title: {
                text: '患者性别分布',
                left: 'center'
              },
              tooltip: {
                trigger: 'item'
              },
              legend: {
                top: '5%'
              },
              series: [
                {
                  name: '性别',
                  type: 'pie',
                  radius: '65%',
                  data: data.map(item => ({
                    value: item.total,
                    name: item.gender   // ← 正确使用接口返回值
                  }))
                }
              ]
            };

            myChart.setOption(option);
            myChart.resize();
          })
          .catch(err => {
            console.error(err);
          });
      });
    },


    //统计患者年龄分布
    patientAge(){
      var myChart = this.$echarts.init(document.getElementById("patientAge"));
      request.get("patient/patientAge")
      .then(res => {
        var option = {
           title: {
        text: '患者年龄段分布',
        left: 'center'
    },
    tooltip: {
        trigger: 'item'
    },
    legend: {
        top: '5%',
        left: 'center'
    },
    series: [
          {
              name: '年龄段',
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              itemStyle: {
                  borderRadius: 10,
                  borderColor: '#fff',
                  borderWidth: 2
              },
              label: {
                  show: false,
                  position: 'center'
              },
              emphasis: {
                  label: {
                      show: true,
                      fontSize: '40',
                      fontWeight: 'bold'
                  }
              },
              labelLine: {
                  show: false
              },
              data: [
                  {value: res.data.data[0], name: '0-9岁'},
                  {value: res.data.data[1], name: '10-19岁'},
                  {value: res.data.data[2], name: '20-29岁'},
                  {value: res.data.data[3], name: '30-39岁'},
                  {value: res.data.data[4], name: '40-49岁'},
                  {value: res.data.data[5], name: '50-59岁'},
                  {value: res.data.data[6], name: '60-69岁'},
                  {value: res.data.data[7], name: '70-79岁'},
                  {value: res.data.data[8], name: '80-89岁'},
                  {value: res.data.data[9], name: '90-99岁'},
              ]
          }
        ]
    };
       myChart.setOption(option);
      }).catch(err => {
        console.error(err);
      })
    },

    //统计挂号科室人数
    orderSection(){
      let myChart = this.$echarts.init(document.getElementById("orderSection"));
      request.get("order/orderSection")
      .then(res => {
        var option = {
             title: {
                  text: '近 7 日挂号科室人数统计',
                  left: 'center'
               },
                  xAxis: {
                      type: 'category',
                      data: res.data.data.map((item) => item.doctor.dSection),
                      axisLabel: {//解决各个不显示问题
                         interval:0,
                         rotate:10,
                         }

                  },
          yAxis: {
            type: 'value',
            minInterval: 1
          },
                series: [{
                    data: res.data.data.map((item) => item.countSection),
                    type: 'bar',
                    showBackground: true,
                    backgroundStyle: {
                        color: 'rgba(180, 180, 180, 0.2)'
                    }
                }]
          };
          myChart.setOption(option);
      })
      .catch(err => {
        console.error(err);
      })
    },

    //近十日的就诊量趋势
    orderLast10Days(){
      let myChart = this.$echarts.init(document.getElementById("orderLast10Days"));
      request.get("order/orderLast10Days")
          .then(res => {
            var option = {
              title: {
                text: '近 10 日的就诊量趋势',
                left: 'center'
              },
              xAxis: {
                type: 'category',
                data: res.data.data.map((item) => item.date),
                axisLabel: {//解决各个不显示问题
                  interval:0,
                  rotate:10,
                }

              },
              yAxis: {
                type: 'value',
                minInterval: 1,   // ← 不允许小数刻度
                axisLabel: {
                  formatter: '{value}'  // 显示整数
                }
              },
              series: [{
                data: res.data.data.map((item) => item.total),
                type: 'bar',
                showBackground: true,
                backgroundStyle: {
                  color: 'rgba(180, 180, 180, 0.2)'
                }
              }]
            };
            myChart.setOption(option);
          })
          .catch(err => {
            console.error(err);
          })
    },


    //获取过去num天日期
    pastSeven(num) {
      let date = new Date();
      date.setDate(date.getDate() - num);
      let time = date.getMonth() + 1 + "-" + date.getDate();
      return time;
    },

  },
  mounted() {
    this.$nextTick(() => {
      this.orderLast10Days();
      this.orderSection();
      this.patientAge();
      this.patientGender();
    });
  },
  created() {

  },
};
</script>

<style>
.gender-age-row {
  display: flex;
  justify-content: space-between;   /* 两个图左右分开 */
  margin-top: 20px;
}

.chart-box {
  width: 48%;        /* 两图平均分 */
  height: 500px;
}
.gender-age-row {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  width: 1200px; /* 加这一行确保宽度 */
}

</style>
