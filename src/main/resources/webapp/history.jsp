<head>
	<meta charset="utf-8">
	<title>Bootstrap 实例 - 条纹表格</title>
	<script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
	<script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<script src="//oss.maxcdn.com/jquery.form/3.50/jquery.form.min.js"></script>

	<link rel="stylesheet" href="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/css/bootstrap.min.css">

	<link href="css/bootstrap-datetimepicker.min.css" rel="stylesheet" />
	<script src="js/moment-with-locales.js"></script>
	<script src="js/bootstrap-datetimepicker.js"></script>

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

			var options = {
				// target:        "p",   // 用服务器返回的数据 更新 id为output1的内容.
				beforeSubmit:  showRequest,  // 提交前
				success: showResponse, // 提交后 
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


			function showResponse(data, statusText) {
				var tableStr = "<table class='table table-striped' id='historyInfo'><caption>监控数据</caption>";
				tableStr = tableStr + "<th>日期</th><th>上传总数</th><th>上传成功数</th><th>上传失败数</th><th>下载总数</th><th>下载成功数</th><th>下载失败数</th><th>转码请求发送总数</th><th>转码请求发送成功数</th><th>转码请求发送失败数</th><th>是否归档</th>";
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

				tableStr = tableStr + "</table>";
				$("#historyInfo")[0].innerHTML = (tableStr); // $.ajax({ // type: "POST", // url: "rest/history", // data: $('#history-query').serialize(), //success: function(data) { // $("#server-info").html(data); //var tableStr = "<table class='table table-striped' id='historyInfo><caption>监控数据</caption>";
				$("#sub").html("<button type='submit' class='btn btn-default' disable='false'>查询</button>");

			}

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
			<a href="metrics.jsp">监控</a>
		</li>
		<li class="active">
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
					<div class="col-sm-offset-2 col-sm-10" id = "sub">
						<button type="submit" class="btn btn-default" >查询</button>
					</div>
				</div>
			</form>

			<table class="table table-striped" id="historyInfo">

			</table>

		</div>
	</div>

	<div id="myDiv">
	</div>

	<body>