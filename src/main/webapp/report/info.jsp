<%--
  Created by IntelliJ IDEA.
  User: 86183
  Date: 2022/11/13
  Time: 16:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="col-md-9">
    <div class="data_list">
      <div class="data_list_title"><span class="glyphicon glyphicon-signal"></span>&nbsp;数据报表 </div>
        <div class="container-fluid">
            <div class="row" style="padding-top: 20px;">
                <div class="col-md-12">
                    <%--插入柱状图的容器--%>
                  <div id="monthChart" style="height: 500px"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="statics/echarts/echarts.min.js"></script>

<script type="text/javascript">
    $.ajax({
        type:get,
        url:"report",
        data:{
            actionName:month,
        },
        success:function (result){
            console.log(result);
            //得到月份（得到x轴对应的数据）
            var monthArray = result.result.monthArray;
            //得到月份对应的云记数量（Y轴的数据）
            var dataArray = result.result.dataArray;
            //加载柱状图
            loadMonthChart(monthArray,dataArray);
        }

    })

    /**
     * 加载柱状图方法
     */
    function loadMonthChart(monthArray,dataArray){
       // 基于准备好的dom，初始化echarts实例
       var myChart = echarts.init(document.getElementById('monthChart'));

       // 指定图表的配置项和数据
       // prettier-ignore
        //x轴名称
       let dataAxis = monthArray;
       // prettier-ignore
        //x轴数据
       let data = dataArray;
       let yMax = 30;
       let dataShadow = [];
       for (let i = 0; i < data.length; i++) {
           dataShadow.push(yMax);
       }
       option = {
           //标题
           title: {
               text: '按月统计',//主标题
               subtext: '按月份统计对应的云数量',//幅标题
               left:'center'//标题对齐方式
           },
           //提示框
           tooltip:{},
          /* legend:{
               data:['月份'],
           },*/
           //x轴
           xAxis: {
               data: dataAxis,
               axisLabel: {
                   inside: true,
                   color: '#fff'
               },
               axisTick: {
                   show: false
               },
               axisLine: {
                   show: false
               },
               z: 10
           },
           yAxis: {
               axisLine: {
                   show: false
               },
               axisTick: {
                   show: false
               },
               axisLabel: {
                   color: '#999'
               }
           },
           dataZoom: [
               {
                   type: 'inside'
               }
           ],
           series: [
               {
                   type: 'bar',//柱状图
                   showBackground: true,
                   itemStyle: {
                       color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                           { offset: 0, color: '#83bff6' },
                           { offset: 0.5, color: '#188df0' },
                           { offset: 1, color: '#188df0' }
                       ])
                   },
                   emphasis: {
                       itemStyle: {
                           color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                               { offset: 0, color: '#2378f7' },
                               { offset: 0.7, color: '#2378f7' },
                               { offset: 1, color: '#83bff6' }
                           ])
                       }
                   },
                   data: data,
                   /*name:'月份'*/
               }
           ]
       };
       // 使用刚指定的配置项和数据显示图表。
       myChart.setOption(option);
   }
</script>