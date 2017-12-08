<head>
	<meta charset="utf-8">
	<title>Proxy Web</title>
	<script src="js/jquery.min.js"></script>

	<script src="js/jquery.form.min.js"></script>

	<link rel="stylesheet" href="css/bootstrap.min.css">

	<script src="js/bootstrap.min.js"></script>

	<link href="css/bootstrap-datetimepicker.min.css" rel="stylesheet" />
	<script src="js/moment-with-locales.js"></script>
	<script src="js/bootstrap-datetimepicker.js"></script>
	<script src="http://echarts.baidu.com/build/dist/echarts.js"></script>

	<script type="text/javascript">
		$(function() {
			$('#datetimepicker1').datetimepicker({
				language: 'zh-CN', //显示中文
				format: 'yyyy-mm-dd', //显示格式
				minView: "month", //设置只显示到月份
				initialDate: new Date(),
				autoclose: true, //选中自动关闭
				todayBtn: true, //显示今日按钮
				locale: moment.locale('zh-cn')
			});
			//默认获取当前日期
			var today = new Date();
			var begindate = (today.getFullYear()) + "-" + (today.getMonth() + 1) + "-" + today.getDate();
			//对日期格式进行处理
			var date = new Date(begindate);
			var mon = date.getMonth() + 1;
			var day = date.getDate();
			var mydate = date.getFullYear() + "-" + (mon < 10 ? "0" + mon : mon) + "-" + (day < 10 ? "0" + day : day);
			document.getElementById("begindate").value = mydate;

			$('#datetimepicker2').datetimepicker({
				language: 'zh-CN', //显示中文
				format: 'yyyy-mm-dd', //显示格式
				minView: "month", //设置只显示到月份
				initialDate: new Date(),
				autoclose: true, //选中自动关闭
				todayBtn: true, //显示今日按钮
				locale: moment.locale('zh-cn')
			});
			//默认获取当前日期
			var today = new Date();
			var enddate = (today.getFullYear()) + "-" + (today.getMonth() + 1) + "-" + today.getDate();
			//对日期格式进行处理
			var date = new Date(enddate);
			var mon = date.getMonth() + 1;
			var day = date.getDate();
			var mydate = date.getFullYear() + "-" + (mon < 10 ? "0" + mon : mon) + "-" + (day < 10 ? "0" + day : day);
			document.getElementById("enddate").value = mydate;
		});

		$(function() {
			function init() {

				$.ajax({
					type: "get",
					url: "rest/initHistoryInfo",
					success: function(data) {
						addTable(data, null);
						var l = data.root.length;
						var dateInfo = new Array();
						var uploadAll = new Array();
						var uploadSuccAll = new Array();
						var uploadFailedAll = new Array();
						var downAll = new Array();
						var downSucc = new Array();
						var downFail = new Array();
						var transcodeAll = new Array();
						var transcodeSucc = new Array();
						var transcodeFail = new Array();

						for(var i = 0; i < l; i++) {
							dateInfo[i] = data.root[i].mDate;
							uploadAll[i] = data.root[i].mUploadSum;
							uploadSuccAll[i] = data.root[i].mUploadSuccNum;
							uploadFailedAll[i] = data.root[i].mUploadFailedNum;
							downAll[i] = data.root[i].mDownloadSum;
							downSucc[i] = data.root[i].mDownloadSuccNum;
							downFail[i] = data.root[i].mDownloadFailNum;
							transcodeAll[i] = data.root[i].mTranscodeSum;
							transcodeSucc[i] = data.root[i].mTranscodeSuccNum;
							transcodeFail[i] = data.root[i].mTranscodeFailNum;
						}
						alert(dateInfo);

						require.config({
							paths: {
								echarts: 'http://echarts.baidu.com/build/dist'
							}
						});
						require(
							[
								'echarts',
								'echarts/chart/line', // 使用柱状图就加载bar模块，按需加载
								'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载

							],
							function(ec) {
								// 基于准备好的dom，初始化echarts图表
								var myChart = ec.init(document.getElementById('main'));
								var myChart2 = ec.init(document.getElementById('main2'));
								var myChart3 = ec.init(document.getElementById('main3'));

								var option = {
									title: {
										text: '上传数变化',
										subtext: 'hehe'
									},
									tooltip: {
										trigger: 'axis'
									},
									legend: {
										data: ['上传总数', '上传成功数', '上传失败数']
									},
									toolbox: {
										show: true,
										feature: {
											mark: {
												show: false
											},
											dataView: {
												show: true,
												readOnly: false
											},
											magicType: {
												show: true,
												type: ['line', 'bar']
											},
											restore: {
												show: false
											},
											saveAsImage: {
												show: false
											}
										}
									},
									calculable: true,
									xAxis: [{
										type: 'category',
										boundaryGap: false,
										data: dateInfo
									}],
									yAxis: [{
										type: 'value',
										axisLabel: {
											formatter: '{value} 次'
										}
									}],
									series: [{
											name: '上传总数',
											type: 'line',
											data: uploadAll,
										},
										{
											name: '上传成功数',
											type: 'line',
											data: uploadSuccAll,

										},
										{
											name: '上传失败数',
											type: 'line',
											data: uploadFailedAll,

										}
									]
								};

								// 为echarts对象加载数据 
								myChart.setOption(option);

								var option2 = {
									title: {
										text: '下载数变化',
										subtext: 'hehe'
									},
									tooltip: {
										trigger: 'axis'
									},
									legend: {
										data: ['下载总数', '下载成功数', '下载失败数']
									},
									toolbox: {
										show: true,
										feature: {
											mark: {
												show: false
											},
											dataView: {
												show: true,
												readOnly: false
											},
											magicType: {
												show: true,
												type: ['line', 'bar']
											},
											restore: {
												show: false
											},
											saveAsImage: {
												show: false
											}
										}
									},
									calculable: true,
									xAxis: [{
										type: 'category',
										boundaryGap: false,
										data: dateInfo
									}],
									yAxis: [{
										type: 'value',
										axisLabel: {
											formatter: '{value} 次'
										}
									}],
									series: [{
											name: '下载总数',
											type: 'line',
											data: downAll,
										},
										{
											name: '下载成功数',
											type: 'line',
											data: downSucc,

										},
										{
											name: '下载失败数',
											type: 'line',
											data: downFail,

										}
									]
								};

								var option3 = {
									title: {
										text: '转码数变化',
										subtext: 'hehe'
									},
									tooltip: {
										trigger: 'axis'
									},
									legend: {
										data: ['转码总数', '转码成功数', '转码失败数']
									},
									toolbox: {
										show: true,
										feature: {
											mark: {
												show: false
											},
											dataView: {
												show: true,
												readOnly: false
											},
											magicType: {
												show: true,
												type: ['line', 'bar']
											},
											restore: {
												show: false
											},
											saveAsImage: {
												show: false
											}
										}
									},
									calculable: true,
									xAxis: [{
										type: 'category',
										boundaryGap: false,
										data: dateInfo
									}],
									yAxis: [{
										type: 'value',
										axisLabel: {
											formatter: '{value} 次'
										}
									}],
									series: [{
											name: '转码总数',
											type: 'line',
											data: transcodeAll,
										},
										{
											name: '转码成功数',
											type: 'line',
											data: transcodeSucc,

										},
										{
											name: '转码失败数',
											type: 'line',
											data: transcodeFail,

										}
									]
								};

								myChart2.setOption(option2);
								myChart3.setOption(option3);

							}
						);
					}
				});
			}

			var options = {
				// target:        "p",   // 用服务器返回的数据 更新 id为output1的内容.
				beforeSubmit: showRequest, // 提交前
				success: showResponse, // 提交后 
				error: showError,
				//另外的一些属性: 
				type: "POST",
				url: "rest/history",
				data: $('#history-query').serialize(),
				async: false, // 默认是form的action，如果写的话，会覆盖from的action.
				//type:              // 默认是form的method，如果写的话，会覆盖from的method.('get' or 'post').
				//dataType:  null        // 'xml', 'script', or 'json' (接受服务端返回的类型.) 
				//clearForm: true        // 成功提交后，清除所有的表单元素的值.
				// resetForm: true        // 成功提交后，重置所有的表单元素的值.
				//由于某种原因,提交陷入无限等待之中,timeout参数就是用来限制请求的时间,
				//当请求大于3秒后，跳出请求. 
				//timeout:   3000 
			};

			$('#history-query').submit(function() {
				$(this).ajaxSubmit(options);
				return false; //来阻止浏览器提交.
			});

			function showRequest() {
				$("#sub").html("<button type='submit' class='btn btn-default' disable='true'>查询</button>");
			}

			function showError(data, statusText) {
				alert(data.responseText);
			}

			function addTable(data, statusText) {
				var tableStr = "<table class='table table-striped' id='historyInfo' style='font-size:15px'><caption>监控数据</caption>";
				tableStr = tableStr + "<tr class='danger'><th>日期</th><th>上传总数</th><th>上传成功数</th><th>上传失败数</th><th>下载总数</th><th>下载成功数</th><th>下载失败数</th><th>转码请求发送总数</th><th>转码请求发送成功数</th><th>转码请求发送失败数</th><th>是否归档</th></tr>";
				for(var i = 0; i < data.root.length; i++) {
					if(i % 2 != 0) {
						tableStr = tableStr + "<tr class=' active '>";
					} else {
						tableStr = tableStr + "<tr>";
					}
					//tableStr = tableStr + "<tr>";
					tableStr = tableStr +
						"<td>" + data.root[i].mDate + "</td>" +
						"<td>" + data.root[i].mUploadSum + "</td>" +
						"<td>" + data.root[i].mUploadSuccNum + "</td>" +
						"<td>" + data.root[i].mUploadFailedNum + "</td>" +
						"<td>" + data.root[i].mDownloadSum + "</td>" +
						"<td>" + data.root[i].mDownloadSuccNum + "</td>" +
						"<td>" + data.root[i].mDownloadFailNum + "</td>" +
						"<td>" + data.root[i].mTranscodeSum + "</td>" +
						"<td>" + data.root[i].mTranscodeSuccNum + "</td>" +
						"<td>" + data.root[i].mTranscodeFailNum + "</td>" +
						"<td>" + data.root[i].mDayIsArchive + "</td></tr>";
				}
				if(data.root.length > 10) {

				}

				tableStr = tableStr + "</table>";
				$("#historyInfo")[0].innerHTML = (tableStr); // $.ajax({ // type: "POST", // url: "rest/history", // data: $('#history-query').serialize(), //success: function(data) { // $("#server-info").html(data); //var tableStr = "<table class='table table-striped' id='historyInfo><caption>监控数据</caption>";
				$("#sub").html("<button type='submit' class='btn btn-default' disable='false'>查询</button>");

			}

			function showResponse(data, statusText) {
				addTable(data, statusText);
				var uploadAll = new Array();
				var downAll = new Array();
				var transcodeAll = new Array();
				var dateInfo = new Array();

				for(var i = 0; i < data.root.length; i++) {
					dateInfo[i] = data.root[i].mDate;
					transcodeAll[i] = data.root[i].mTranscodeSum;
					downAll[i] = data.root[i].mDownloadSum;
					uploadAll[i] = data.root[i].mUploadSum;
				}
				require.config({
					paths: {
						echarts: 'http://echarts.baidu.com/build/dist'
					}
				});
				require(
					[
						'echarts',
						'echarts/chart/line', // 使用柱状图就加载bar模块，按需加载
						'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载

					],
					function(ec) {
						// 基于准备好的dom，初始化echarts图表
						var myChart = ec.init(document.getElementById('main4'));
						var option = {
							title: {
								text: '查询数据',
								subtext: 'hehe'
							},
							tooltip: {
								trigger: 'axis'
							},
							legend: {
								data: ['上传总数', '下载总数', '转码总数']
							},
							toolbox: {
								show: true,
								feature: {
									mark: {
										show: false
									},
									dataView: {
										show: true,
										readOnly: false
									},
									magicType: {
										show: true,
										type: ['line', 'bar']
									},
									restore: {
										show: false
									},
									saveAsImage: {
										show: false
									}
								}
							},
							calculable: true,
							xAxis: [{
								type: 'category',
								boundaryGap: false,
								data: dateInfo
							}],
							yAxis: [{
								type: 'value',
								axisLabel: {
									formatter: '{value} 次'
								}
							}],
							series: [{
									name: '上传总数',
									type: 'line',
									data: uploadAll,
								},
								{
									name: '下载总数',
									type: 'line',
									data: downAll,

								},
								{
									name: '转码总数',
									type: 'line',
									data: transcodeAll,

								}
							]
						};

						myChart.setOption(option);

					});
			}
			init();

		});
	</script>

	<style>
		div.panel1 {
			margin-top: 50px;
			margin-bottom: 50px;
			margin-right: 100px;
			margin-left: 30px;
		}
	</style>
	<ul class="nav nav-tabs">
		<li>
			<a href="metrics.jsp" style="color:#FF6347">监控</a>
		</li>
		<li class="active">
			<a href="history.jsp" style="color:#FF6347"><strong>历史详情</strong></a>
		</li>
		<li>
			<a href="transform.jsp" style="color:#FF6347">转码重传</a>
		</li>
		<li>
			<a href="download.jsp" style="color:#FF6347">文件管理</a>
		</li>
		<li>
			<a href="archive.jsp" style="color:#FF6347">归档</a>
		</li>
		<li>
			<a href="delete.jsp" style="color:#FF6347">删除</a>
		</li>
		<p class="text-right" style="font-family:'sans-serif';color:#FF6347;font-size:20px;margin-top: 8px;margin-bottom: 2px;margin-right: 30px;">
			<strong>HTSC Proxy Web</strong>
		</p>
	</ul>
</head>

<body>

	<div class="panel panel-danger panel1" style="width: 1200px;">
		<div class="panel-heading">
			<h3 class="panel-title">历史</h3>
		</div>
		<div class="panel-body">
			<form class="form-inline" role="form" style="text-align:center;width:1100px;" id="history-query">
				<div class="form-group">
					<label class="col-sm-2 control-label" style="float: left; width: 120px;height: 20px; font-size:20px">开始日期</label>
					<a class='input-group date' id='datetimepicker1'>
						<span class="input-group-addon" style="float: left; width: 50px; height: 30px;">
                         <span class="glyphicon glyphicon-calendar"></span>
						</span>
						<input type='text' class="form-control" id='begindate' name='beginDate' style="width: 150px; height: 30px;" />
					</a>
				</div>

				<div class="form-group">
					<label class="col-sm-2 control-label" for="inputSuccess" style="float: left; width: 120px;height: 20px;font-size:20px">
         			结束日期
         		</label>
					<a class='input-group date' id='datetimepicker2'>
						<span class="input-group-addon" style="float: left; width: 50px; height: 30px;">
                         <span class="glyphicon glyphicon-calendar"></span>
						</span>
						<input type='text' class="form-control" id='enddate' name='endDate' style="width: 150px; height: 30px;" />

					</a>
				</div>

				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10" id="sub">
						<button type="submit" class="btn btn-danger">查询</button>
					</div>
				</div>
			</form>

			<div class="container">
				<div class="row">
					<div id="main" style="height:400px;" class='col-xs-6'></div>
					<div id="main2" style="height:400px;" class='col-xs-6'></div>
					<div id="main3" style="height:400px;" class='col-xs-6'></div>
					<div id="main4" style="height:400px;" class='col-xs-6'></div>

				</div>
			</div>

			<table class="table table-striped" id="historyInfo" style="font-size:15px">
				<caption>监控数据</caption>
				<tr class='danger'>
					<th>日期</th>
					<th>上传总数</th>
					<th>上传成功数</th>
					<th>上传失败数</th>
					<th>下载总数</th>
					<th>下载成功数</th>
					<th>下载失败数</th>
					<th>转码请求发送总数</th>
					<th>转码请求发送成功数</th>
					<th>转码请求发送失败数</th>
					<th>是否归档</th>
				</tr>
			</table>

		</div>
	</div>

	<div id="myDiv">
	</div>

	<body>