<!DOCTYPE html>
<html>

	<head>
		<meta charset="utf-8">
		<title>Proxy Web</title>
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<script src="js/jquery.min.js"></script>
		<script src="js/bootstrap.min.js"></script>
		<script src="http://echarts.baidu.com/build/dist/echarts.js"></script>

		<style>
			div.panel1 {
				margin-top: 50px;
				margin-bottom: 50px;
				margin-right: 100px;
				margin-left: 30px;
			}
		</style>
		<script type="text/javascript">
			$(function() {
				function refreshMetricInfo() {
					$.ajax({
						type: "get",
						url: "rest/MetricInfo",
						success: function(data) {
							var tableStr = "<table class='table table-striped' id = 'metricInfo'><caption>监控数据</caption>";
							tableStr = tableStr + "<tr class='danger'><th>类别</th><th>上传总数</th><th>上传成功数</th><th>上传失败数</th><th>下载总数</th><th>下载成功数</th><th>下载失败数</th><th>转码请求发送总数</th><th>转码请求发送成功数</th><th>转码请求发送失败数</th><th>是否归档</th></tr>";
							tableStr = tableStr + "<tr><td>当天</td><td>" +
								data.dayInfo.mUploadSum + "</td>" +
								"<td>" + data.dayInfo.mUploadSuccNum + "</td>" +
								"<td>" + data.dayInfo.mUploadFailedNum + "</td>" +
								"<td>" + data.dayInfo.mDownloadSum + "</td>" +
								"<td>" + data.dayInfo.mDownloadSuccNum + "</td>" +
								"<td>" + data.dayInfo.mDownloadFailNum + "</td>" +
								"<td>" + data.dayInfo.mTranscodeSum + "</td>" +
								"<td>" + data.dayInfo.mTranscodeSuccNum + "</td>" +
								"<td>" + data.dayInfo.mTranscodeFailNum + "</td>" +
								"<td>" + data.dayInfo.mDayIsArchive + "</td></tr>";
							tableStr = tableStr + "<tr class='active'><td>总计</td><td>" +
								data.allInfo.mUploadSum + "</td>" +
								"<td>" + data.allInfo.mUploadSuccNum + "</td>" +
								"<td>" + data.allInfo.mUploadFailedNum + "</td>" +
								"<td>" + data.allInfo.mDownloadSum + "</td>" +
								"<td>" + data.allInfo.mDownloadSuccNum + "</td>" +
								"<td>" + data.allInfo.mDownloadFailNum + "</td>" +
								"<td>" + data.allInfo.mTranscodeSum + "</td>" +
								"<td>" + data.allInfo.mTranscodeSuccNum + "</td>" +
								"<td>" + data.allInfo.mTranscodeFailNum + "</td>" +
								"<td>/</td></tr>";
							tableStr = tableStr + "</table>";
							$("#metricInfo").html(tableStr);
							alert(data.allInfo.mUploadSuccNum);

							require.config({
								paths: {
									echarts: 'http://echarts.baidu.com/build/dist'
								}
							});
							require(
								[
									'echarts',
									'echarts/chart/bar' // 使用柱状图就加载bar模块，按需加载
								],
								function(ec) {
									// 基于准备好的dom，初始化echarts图表
									var myChart = ec.init(document.getElementById('main'));

									var option = {
										tooltip: {
											show: true
										},
										legend: {
											data: ['总计']
										},
										xAxis: [{
											type: 'category',
											data: ["上传总数", "上传成功数", "上传失败数", "下载总数", "下载成功数", "下载失败数", "转码请求发送总数", "转码请求发送成功数", "转码请求发送失败数"]
										}],
										yAxis: [{
											type: 'value'
										}],
										series: [{
											"name": "总计",
											"type": "bar",
											"data": [data.allInfo.mUploadSum, data.allInfo.mUploadSuccNum, data.allInfo.mUploadFailedNum, data.allInfo.mDownloadSum, data.allInfo.mDownloadSuccNum, data.allInfo.mDownloadFailNum, data.allInfo.mTranscodeSum, data.allInfo.mTranscodeSuccNum, data.allInfo.mTranscodeFailNum]
										}]
									};

									// 为echarts对象加载数据 
									myChart.setOption(option);

									var myChart2 = ec.init(document.getElementById('main2'));

									var option2 = {
										tooltip: {
											show: true
										},
										legend: {
											data: ['当天']
										},
										xAxis: [{
											type: 'category',
											data: ["上传总数", "上传成功数", "上传失败数", "下载总数", "下载成功数", "下载失败数", "转码请求发送总数", "转码请求发送成功数", "转码请求发送失败数"]
										}],
										yAxis: [{
											type: 'value'
										}],
										series: [{
											"name": "当天",
											"type": "bar",
											"data": [data.dayInfo.mUploadSum, data.dayInfo.mUploadSuccNum, data.dayInfo.mUploadFailedNum, data.dayInfo.mDownloadSum, data.dayInfo.mDownloadSuccNum, data.dayInfo.mDownloadFailNum, data.dayInfo.mTranscodeSum, data.dayInfo.mTranscodeSuccNum, data.dayInfo.mTranscodeFailNum]
										}]
									};

									myChart2.setOption(option2);

								}
							);

						}
					});
				}
				refreshMetricInfo();
			});
		</script>
		<script>
			/*
																		$(function() {
																			function refreshMetricInfo() {
																			    alert("test");
																				$.ajax({
																					type: "get",
																					url: "history.jsp",
																					success: function(data) {
																						var tableStr = "<table class='table table-striped' id = 'metricInfo'><caption>监控数据</caption>";
																						tableStr = tableStr + "<th></th><th>上传总数</th><th>上传成功数</th><th>上传失败数</th><th>下载总数</th><th>下载成功数</th><th>下载失败数</th><th>转码请求发送总数</th><th>转码请求发送成功数</th><th>转码请求发送失败数</th>";
																						tableStr = tableStr + "<tr><td>当天</td><td>" +
																							data.dayInfo.uploadSum + "</td>" +
																							"<td>" + data.dayInfo.uploadSuccNum + "</td>" +
																							"<td>" + data.dayInfo.uploadFailedNum + "</td>" +
																							"<td>" + data.dayInfo.downloadSum + "</td>" +
																							"<td>" + data.dayInfo.downloadSuccNum + "</td>" +
																							"<td>" + data.dayInfo.downloadFailNum + "</td>" +
																							"<td>" + data.dayInfo.transcodeSum + "</td>" +
																							"<td>" + data.dayInfo.transcodeSuccNum + "</td>" +
																							"<td>" + data.dayInfo.transcodeFailNum + "</td>" +
																							"<td>" + data.dayInfo.dayIsArchive + "</td></tr>";
																						tableStr = tableStr + "<tr class='active'><td>" +
																							data.allInfo.uploadSum + "</td>" +
																							"<td>" + data.allInfo.uploadSuccNum + "</td>" +
																							"<td>" + data.allInfo.uploadFailedNum + "</td>" +
																							"<td>" + data.allInfo.downloadSum + "</td>" +
																							"<td>" + data.allInfo.downloadSuccNum + "</td>" +
																							"<td>" + data.allInfo.downloadFailNum + "</td>" +
																							"<td>" + data.allInfo.transcodeSum + "</td>" +
																							"<td>" + data.allInfo.transcodeSuccNum + "</td>" +
																							"<td>" + data.allInfo.transcodeFailNum + "</td>" +
																							"<td>" + data.allInfo.dayIsArchive + "</td></tr>";

																						tableStr = tableStr + "</table>";
																						$("metricInfo").html(tableStr);
																					}
																				});
																			}

																			function refresh() {
																				refreshMetricInfo();
																			}

																			refresh();
																			//setInterval(refresh, 5000);
																		});*/
		</script>
		<ul class="nav nav-tabs">
			<li class="active">
				<a href="metrics.jsp" style="color:#FF6347"><strong>监控</strong></a>
			</li>
			<li>
				<a href="history.jsp" style="color:#FF6347">历史详情</a>
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

		<div class="panel  panel-danger panel1" style="width: 1200px;">
			<div class="panel-heading">
				<h3 class="panel-title">监控</h3>
			</div>
			<div class="panel-body">

				<table class="table table-striped" id="metricInfo">
					<caption>监控数据</caption>
					<tr class='danger'>
						<th>类别</th>
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
<div class="container">
				<div class="row">
					<div id="main" style="height:400px;color:#FF6347" class='col-xs-6'></div>
					<div id="main2" style="height:400px" class="col-xs-6"></div>
				</div>
			</div>
				<table class="table table-striped" id="serverInfo">
					<tr class='danger'>
						<th class="text-center">server ID</th>

						<th class="text-center">磁盘总容量</th>
						<th class="text-center">磁盘已使用量 </th>
						<th class="text-center">归档文件总数</th>
					</tr>
					<tr class="text-center">
						<td>server A</td>
						<td>200</td>
						<td>200</td>
						<td>200</td>

					</tr>
				</table>

			</div>
			

			<!-- ECharts单文件引入 -->
			<script type="text/javascript">
				// 路径配置
			</script>
		</div>

		<div id="server-info" style="padding-top:12px; text-align:middle">
			<font color="#00bbff" />
		</div>

		<!-- 为ECharts准备一个具备大小（宽高）的Dom -->

	</body>

</html>