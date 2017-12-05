<!DOCTYPE html>
<html>

	<head>
		<meta charset="utf-8">
		<title>Bootstrap 实例 - 条纹表格</title>
		<link rel="stylesheet" href="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/css/bootstrap.min.css">
		<script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
		<script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>

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
							tableStr = tableStr + "<th>类别</th><th>上传总数</th><th>上传成功数</th><th>上传失败数</th><th>下载总数</th><th>下载成功数</th><th>下载失败数</th><th>转码请求发送总数</th><th>转码请求发送成功数</th><th>转码请求发送失败数</th><th>是否归档</th>";
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
        				<a href="metrics.jsp" >监控</a>
        			</li>
        			<li>
        				<a href="history.jsp">历史详情</a>
        			</li>
        			<li>
        				<a href="transform.jsp">转码重传</a>
        			</li>
        			<li>
        				<a href="download.jsp">文件管理</a>
        			</li>
        			<li>
        				<a href="archive.jsp">归档</a>
        			</li>
        		</ul>
	</head>

	<body>


		<div class="panel panel-primary panel1" style="width: 1200px;">
			<div class="panel-heading">
				<h3 class="panel-title">面板标题</h3>
			</div>
			<div class="panel-body">

				<table class="table table-striped" id="metricInfo">
					<caption>监控数据</caption>
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

				
				</table>

				<table class="table table-striped" id="serverInfo">
					<th class="text-center">磁盘总容量</th>
					<th class="text-center">磁盘已使用量 </th>
					<th class="text-center">归档文件总数</th>
					<tr class="text-center">
						<td>server A</td>
						<td>200</td>
						<td>200</td>
					</tr>
				</table>

			</div>
		</div>

		<div id="server-info" style="padding-top:12px; text-align:middle">
			<font color="#00bbff" />
		</div>

	</body>

</html>