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
	<script src="js/echarts.js"></script>

	<script type="text/javascript">
		function jump(a) {
			var index = a;
			$.ajax({
				type: "post",
				url: "rest/jumpHistoryInfo",
				data: {
					"index": index
				},
				dataType: "json",
				success: function(data) {
					//$.session.set('index', index);
					add(data);
				}
			});
		}

		function add(data) {
			var tableStr = "<table class='table table-striped' id='historyInfo' style='font-size:15px'><caption>监控数据</caption>";
			tableStr = tableStr + "<tr class='danger'><th>日期</th><th>上传总数</th><th>上传成功数</th><th>上传失败数</th><th>下载总数</th><th>下载成功数</th><th>下载失败数</th><th>转码请求发送总数</th><th>转码请求发送成功数</th><th>转码请求发送失败数</th><th>转码重传成功数</th><th>是否归档</th></tr>";
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
					"<td><font color='red'>" + data.root[i].mUploadFailedNum + "</font></td>" +
					"<td>" + data.root[i].mDownloadSum + "</td>" +
					"<td>" + data.root[i].mDownloadSuccNum + "</td>" +
					"<td><font color='red'>" + data.root[i].mDownloadFailNum + "</font></td>" +
					"<td>" + data.root[i].mTranscodeSum + "</td>" +
					"<td>" + data.root[i].mTranscodeSuccNum + "</td>" +
					"<td><font color='red'>" + data.root[i].mTranscodeFailNum + "</font></td>" +
					"<td>" + data.root[i].mReTranscodeSuccNum + "</td>" +
					"<td>" + data.root[i].mDayIsArchive + "</td></tr>";
			}
			tableStr = tableStr + "</table>";
			$("#historyInfo")[0].innerHTML = (tableStr); // $.ajax({ // type: "POST", // url: "rest/history", // data: $('#history-query').serialize(), //success: function(data) { // $("#server-info").html(data); //var tableStr = "<table class='table table-striped' id='historyInfo><caption>监控数据</caption>";
		}
	</script>

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
						"page": "history"
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

			function init() {
				authentication();
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

						// 基于准备好的dom，初始化echarts实例
						var myChart = echarts.init(document.getElementById('main'));
						var myChart2 = echarts.init(document.getElementById('main2'));
						var myChart3 = echarts.init(document.getElementById('main3'));

						var option = {
							title: {
								text: '上传数变化',
								subtext: 'upload'
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

						var option2 = {
							title: {
								text: '下载数变化',
								subtext: 'download'
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
								subtext: 'transcode'
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

						// 使用刚指定的配置项和数据显示图表。
						myChart.setOption(option);
						myChart2.setOption(option2);
						myChart3.setOption(option3);
					}
				});
			}

			var options = {
				// target:        "p",   // 用服务器返回的数据 更新 id为output1的内容.
				beforeSubmit: showRequest, 
				success: showResponse,  
				error: showError,
				type: "POST",
				url: "rest/history",
				data: $('#history-query').serialize(),
				contentType: "application/x-www-form-urlencoded;charset=UTF-8",
				dataType: 'json',
				async: false, 
				timeout: 10000
			};

			$('#history-query').submit(function() {
				$(this).ajaxSubmit(options);
				return false;
			});

			function showRequest() {
				$("#sub").html("<button type='submit' class='btn btn-default' disable='true'>查询</button>");
			}

			function showError(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.status + " " + errorThrown);
			}

			function addTable(data, statusText) {
				var onePageNum = 25;
				var tableStr = "<table class='table table-striped' id='historyInfo' style='font-size:15px'><caption>监控数据</caption>";
				tableStr = tableStr + "<tr class='danger'><th>日期</th><th>上传总数</th><th>上传成功数</th><th>上传失败数</th><th>下载总数</th><th>下载成功数</th><th>下载失败数</th><th>转码请求发送总数</th><th>转码请求发送成功数</th><th>转码请求发送失败数</th><th>转码重传成功数</th><th>是否归档</th></tr>";
				if(data.root.length < onePageNum) {
					onePageNum = data.root.length;
				}
				for(var i = 0; i < onePageNum; i++) {
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
						"<td><font color='red'>" + data.root[i].mUploadFailedNum + "</font></td>" +
						"<td>" + data.root[i].mDownloadSum + "</td>" +
						"<td>" + data.root[i].mDownloadSuccNum + "</td>" +
						"<td><font color='red'>" + data.root[i].mDownloadFailNum + "</font></td>" +
						"<td>" + data.root[i].mTranscodeSum + "</td>" +
						"<td>" + data.root[i].mTranscodeSuccNum + "</td>" +
						"<td><font color='red'>" + data.root[i].mTranscodeFailNum + "</font></td>" +
						"<td>" + data.root[i].mReTranscodeSuccNum + "</td>" +
						"<td>" + data.root[i].mDayIsArchive + "</td></tr>";
				}
				if(data.root.length > onePageNum) {
					var page = "<ul class='pagination'>";
					page = page + "<li><a href='javascript:jump(" + "-1" + ")'>&laquo;</a></li>"

					var pageNum = data.root.length / onePageNum;
					if(data.root.length % onePageNum > 0) {
						pageNum = pageNum + 1;
					}
					for(var j = 1; j <= pageNum; j++) {
						page = page + "<li><a href='javascript:jump(" + j + ");' >";
						page = page + j + "</a></li>";
					}
					page = page + "<li><a href='javascript:jump(" + "0" + ")'>&raquo;</a></li></ul>";
					$("#page")[0].innerHTML = (page);
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

				var myChart = echarts.init(document.getElementById('main4'));
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
			}
			init();

		});
	</script>

	<style>
		div.panel1 {
			margin-top: 50px;
			margin-bottom: 50px;
			margin-right: auto;
			margin-left: auto;
		}
	</style>
	<ul class="nav nav-tabs">
		<li>
			<a href="Metric.jsp" style="color:#FF6347">监控</a>
		</li>
		<li class="active">
			<a href="history.jsp" style="color:#FF6347"><strong>历史详情</strong></a>
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
					<th>转码重传成功数</th>
					<th>是否归档</th>
				</tr>
			</table>

			<div id="page">

			</div>

		</div>
	</div>

	<div id="myDiv">
	</div>

	<div id="main" style="width: 600px;height:400px;"></div>
	<script type="text/javascript">
	</script>
</body>