<!DOCTYPE html>
<html>

	<head>
		<meta charset="utf-8">
		<title>Proxy Web</title>
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<script src="js/jquery.min.js"></script>
		<script src="js/bootstrap.min.js"></script>
		<script type="text/javascript" src="js/echarts.js">
		</script>

		<style>
			div.panel1 {
				margin-top: 50px;
				margin-bottom: 50px;
				margin-right: auto;
				margin-left: auto;
			}
			
			div.container1 {
				margin-right: auto;
				margin-left: auto;
			}
		</style>
		<script type="text/javascript">
			$(function() {
				function refreshMetricInfo() {
					$.ajax({
						type: "get",
						url: "rest/MetricInfo",
						success: function(data) {
							var tableStr = "<table class='table table-striped' id = 'metricInfo'><caption>proxy数据</caption>";
							tableStr = tableStr + "<tr class='danger'><th>类别</th><th>上传总数</th><th>上传成功数</th><th>上传失败数</th><th>下载总数</th><th>下载成功数</th><th>下载失败数</th><th>转码请求发送总数</th><th>转码请求发送成功数</th><th>转码请求发送失败数</th><th>转码重传成功数</th><th>是否归档</th></tr>";
							tableStr = tableStr + "<tr><td>当天</td><td>" +
								data.dayInfo.mUploadSum + "</td>" +
								"<td>" + data.dayInfo.mUploadSuccNum + "</td>" +
								"<td><font color='red'>" + data.dayInfo.mUploadFailedNum + "</font></td>" +
								"<td>" + data.dayInfo.mDownloadSum + "</td>" +
								"<td>" + data.dayInfo.mDownloadSuccNum + "</td>" +
								"<td><font color='red'>" + data.dayInfo.mDownloadFailNum + "</font></td>" +
								"<td>" + data.dayInfo.mTranscodeSum + "</td>" +
								"<td>" + data.dayInfo.mTranscodeSuccNum + "</td>" +
								"<td><font color='red'>" + data.dayInfo.mTranscodeFailNum + "</font></td>" +
								"<td>" + data.dayInfo.mReTranscodeSuccNum + "</td>" +
								"<td>" + data.dayInfo.mDayIsArchive + "</td></tr>";
							tableStr = tableStr + "<tr class='active'><td>总计</td><td>" +
								data.allInfo.mUploadSum + "</td>" +
								"<td>" + data.allInfo.mUploadSuccNum + "</td>" +
								"<td><font color='red'>" + data.allInfo.mUploadFailedNum + "</font></td>" +
								"<td>" + data.allInfo.mDownloadSum + "</td>" +
								"<td>" + data.allInfo.mDownloadSuccNum + "</td>" +
								"<td><font color='red'>" + data.allInfo.mDownloadFailNum + "</font></td>" +
								"<td>" + data.allInfo.mTranscodeSum + "</td>" +
								"<td>" + data.allInfo.mTranscodeSuccNum + "</td>" +
								"<td><font color='red'>" + data.allInfo.mTranscodeFailNum + "</font></td>" +
								"<td>" + data.allInfo.mReTranscodeSuccNum + "</td>" +
								"<td>归档数总计：" + data.archiveNum + "</td></tr>";
							tableStr = tableStr + "</table>";
							$("#metricInfo").html(tableStr);

							var myChart = echarts.init(document.getElementById('table1'));

							var option = {
								title: {
									text: '历史总计数据',
									subtext: 'sum info'
								},
								tooltip: {
									show: true
								},
								legend: {
									data: ['总计', '总计成功数', '总计失败数']
								},
								xAxis: [{
									type: 'category',
									data: ["上传", "下载", "转码"]
								}],
								yAxis: [{
									type: 'value'
								}],
								series: [{
										"name": "总计",
										"type": "bar",
										"data": [data.allInfo.mUploadSum, data.allInfo.mDownloadSum, data.allInfo.mTranscodeSum]
									},
									{
										"name": "总计成功数",
										"type": "bar",
										"data": [data.allInfo.mUploadSuccNum,
											data.allInfo.mDownloadSuccNum, data.allInfo.mReTranscodeSuccNum
										]

									},
									{
										"name": "总计失败数",
										"type": "bar",
										"data": [data.allInfo.mUploadFailedNum, data.allInfo.mDownloadFailNum, data.allInfo.mTranscodeFailNum]
									}
								]
							};

							myChart.setOption(option);

							var myChart2 = echarts.init(document.getElementById('table2'));

							var option2 = {
								title: {
									text: '当天总计数据',
									subtext: 'day info'
								},
								tooltip: {
									show: true
								},
								legend: {
									data: ['当天总计', '当天成功数', '当天失败数']
								},
								xAxis: [{
									type: 'category',
									data: ["上传", "下载", "转码"]
								}],
								yAxis: [{
									type: 'value'
								}],
								series: [{
										"name": "当天总计",
										"type": "bar",
										"data": [data.dayInfo.mUploadSum, data.dayInfo.mDownloadSum, data.dayInfo.mTranscodeSum]
									},
									{
										"name": "当天成功数",
										"type": "bar",
										"data": [data.dayInfo.mUploadSuccNum, data.dayInfo.mDownloadSuccNum, data.dayInfo.mTranscodeSuccNum, data.dayInfo.mReTranscodeSuccNum]
									},
									{
										"name": "当天失败数",
										"type": "bar",
										"data": [data.dayInfo.mUploadFailedNum, data.dayInfo.mDownloadFailNum, data.dayInfo.mTranscodeFailNum]
									}
								]
							};

							myChart2.setOption(option2);

						},
						error: function(XMLHttpRequest, textStatus, errorThrown) {
							//alert(XMLHttpRequest.status + " " + errorThrown);
						}
					});
				}

				function refreshServerInfo() {
					$.ajax({
						type: "get",
						url: "rest/serverInfo",
						success: function(data) {
							var tableStr = "<table class='table table-striped' id='serverInfo'><caption>Server数据</caption>";
							tableStr = tableStr + "<tr class='danger'><th class='text-center'>磁盘总容量(B)</th><th class='text-center'>磁盘已使用量(B) </th></tr>";
							tableStr = tableStr + "<tr class='text-center'>";
							//tableStr = tableStr + "<tr>";
							tableStr = tableStr +
								"<td>" + data.capacity + "</td>" +
								"<td>" + data.used + "</td></tr>";
							//capAll = capAll + data.capacity;
							//usedAll = usedAll +  data.used;
							var myChart = echarts.init(document.getElementById(data.name));

							var option = {
								title: {
									text: '',
									subtext: 'server数据',
									x: 'center'
								},
								tooltip: {
									trigger: 'item',
									formatter: "{a} <br/>{b} : {c} ({d}%)"
								},
								legend: {
									orient: 'vertical',
									x: 'left',
									data: ['磁盘未使用量', '磁盘已使用量']
								},
								toolbox: {
									show: true,
									feature: {
										mark: {
											show: true
										},
										dataView: {
											show: true,
											readOnly: false
										},
									}
								},
								calculable: true,
								series: [{
									name: '访问来源',
									type: 'pie',
									radius: '55%',
									center: ['50%', '60%'],
									data: [{
											value: data.notUsedBar,
											name: '磁盘未使用量'
										},
										{
											value: data.usedBar,
											name: '磁盘已使用量'
										},
									]
								}]
							};

							myChart.setOption(option);
							tableStr = tableStr + "</table>";
							$("#serverInfo").html(tableStr);
						},
						error: function(XMLHttpRequest, textStatus, errorThrown) {
							//alert(XMLHttpRequest.status + " " +errorThrown);
						}
					});
				}

				$("#logOut").click(function() {
					$.ajax({
						type: "GET",
						url: "rest/logOut",
						async: false,
						success: function(data) {
							location.href = "index.jsp";
						}
					});
				});

				function authentication() {
					$.ajax({
						type: "POST",
						url: "rest/authentication",
						data: {
							"page": "Metric"
						},
						dataType: "json",
						async: false,
						contentType: "application/json",
						success: function(data) {
							if(data.result == "false") {
								alert("Lack of authority")
								location.href = "index.jsp";
							} else if(data.result == "timeout") {
								alert(" login timeout");
								location.href = "index.jsp";
							}
						}
					});
				}

				function refresh() {
					refreshMetricInfo();
					refreshServerInfo();

				}
				authentication();
				refresh();
				setInterval(refresh, 5000);
			});
		</script>

		<ul class="nav nav-tabs" style="color:#FF6347">
			<li class="active">
				<a href="Metric.jsp" style="color:#FF6347"><strong>监控</strong></a>
			</li>
			<li>
				<a href="history.jsp" style="color:#FF6347">历史详情</a>
			</li>
			<li>
				<a href="transform.jsp" style="color:#FF6347">转码重传</a>
			</li>
			<li>
				<a href="download.jsp" style="color:#FF6347">查询下载</a>
			</li>
			<li>
				<a href="archive.jsp" style="color:#FF6347">归档</a>
			</li>
			<li>
				<a href="delete.jsp" style="color:#FF6347">删除</a>
			</li>
			<p class="text-right" style="font-family:'sans-serif';color:#FF6347;font-size:20px;margin-top: 8px;margin-bottom: 2px;margin-right: 30px;">
				<button style="margin-right: 20px;" type="button" class="btn btn-primary btn-sm" id="logOut">log out</button>
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
						<th>转码重传成功数</th>
						<th>是否归档</th>
					</tr>
				</table>
				<div class="container">
					<div class="row">
						<div id="table1" style="height:400px;color:#FF6347" class='col-xs-6'></div>
						<div id="table2" style="height:400px" class="col-xs-6"></div>
					</div>
				</div>
				<table class="table table-striped" id="serverInfo">
					<tr class='danger'>
						<th class="text-center">磁盘总容量</th>
						<th class="text-center">磁盘已使用量 </th>
					</tr>
					<tr class="text-center">
						<td>200</td>
						<td>200</td>
					</tr>
				</table>

				<div class="container, container1">
					<div class="row">
						<div id="main1" style="height:400px;" class='col-xs-6'></div>
					</div>
				</div>

			</div>
		</div>
	</body>
</html>