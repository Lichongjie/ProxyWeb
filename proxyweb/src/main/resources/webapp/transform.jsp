<head>
	<meta charset="utf-8">
	<title>Proxy Web</title>

	<link rel="stylesheet" href="css/bootstrap.min.css">
	<script src="js/jquery.min.js"></script>
	<script src="js/bootstrap.min.js"></script>

	<script src="js/jquery.form.min.js"></script>

	<link href="css/bootstrap-datetimepicker.min.css" rel="stylesheet" />
	<script src="js/moment-with-locales.js"></script>
	<script src="js/bootstrap-datetimepicker.js"></script>

	<script type="text/javascript">
		function initTableCheckbox() {
			var $thr = $('#transform-info thead tr');
			var $checkAllTh = $('<th><input type="checkbox" id="checkAll" name="checkAll" /></th>');
			$thr.prepend($checkAllTh);
			var $checkAll = $thr.find('input');
			$checkAll.click(function(event) {
				$tbr.find('input').prop('checked', $(this).prop('checked'));
				if($(this).prop('checked')) {
					$tbr.find('input').parent().parent().addClass('warning');
				} else {
					$tbr.find('input').parent().parent().removeClass('warning');
				}
				event.stopPropagation();
			});
			$thr.click(function() {
				$(this).find('input').click();
			});
			var $tbr = $('table tbody tr');
			var $checkItemTd = $('<td><input type="checkbox" name="checkItem" /></td>');
			$tbr.prepend($checkItemTd);
			$tbr.find('input').click(function(event) {
				$(this).parent().parent().toggleClass('warning');
				$checkAll.prop('checked', $tbr.find('input:checked').length == $tbr.length ? true : false);
				event.stopPropagation();
			});
			$tbr.click(function() {
				$(this).find('input').click();
			});
		}

		function jump(a) {
			var index = a;
			$.ajax({
				type: "post",
				url: "rest/jumpTranscodeInfo",
				data: {
					"index": index
				},
				dataType: "json",
				success: function(data) {
					var tableStr = "<table class='table table-striped' id='tarnsform-info'>"
					tableStr = tableStr + "<caption>文件信息</caption><thead><tr class='danger'><th>文件ID</th><th>上传时间</th><th>是否已发送转码请求</th><th>转码格式</th></tr></thead>"
					tableStr = tableStr + "<tbody id='info-body'>";
					for(var i = 0; i < data.root.length; i++) {
						if(i % 2 == 0) {
							tableStr = tableStr + "<tr class='active'>";
						} else {
							tableStr = tableStr + "<tr>";
						}
						tableStr = tableStr +
							"<td name='fileId'>" + data.root[i].fileId + "</td>" +
							"<td >" + data.root[i].uploadDate + "</td>" +
							"<td>" + data.root[i].transcode + "</td>" +
							"<td name='format'>" + data.root[i].transcodeFormat + "</td></tr>"

					}
					tableStr = tableStr + "</tbody></table>";
					$("#transform-info").html(tableStr);
					initTableCheckbox();
				}
			});
		}
	</script>

	<script>
		$(function() {
			$('#datetimepicker1').datetimepicker({
				language: 'zh-CN', 
				format: 'yyyy-mm-dd', 
				minView: "month", 
				initialDate: new Date(),
				autoclose: true, 
				todayBtn: true, 
				locale: moment.locale('zh-cn')
			});
			var today = new Date();
			var begindate = (today.getFullYear()) + "-" + (today.getMonth() + 1) + "-" + today.getDate();
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
			var today = new Date();
			var enddate = (today.getFullYear()) + "-" + (today.getMonth() + 1) + "-" + today.getDate();
			var date = new Date(enddate);
			var mon = date.getMonth() + 1;
			var day = date.getDate();
			var mydate = date.getFullYear() + "-" + (mon < 10 ? "0" + mon : mon) + "-" + (day < 10 ? "0" + day : day);
			document.getElementById("enddate").value = mydate;
		});

		$(function() {
			function initTableCheckbox() {
				var $thr = $('#transform-info thead tr');
				var $checkAllTh = $('<th><input type="checkbox" id="checkAll" name="checkAll" /></th>');
				$thr.prepend($checkAllTh);
				var $checkAll = $thr.find('input');
				$checkAll.click(function(event) {
					$tbr.find('input').prop('checked', $(this).prop('checked'));
					if($(this).prop('checked')) {
						$tbr.find('input').parent().parent().addClass('warning');
					} else {
						$tbr.find('input').parent().parent().removeClass('warning');
					}
					event.stopPropagation();
				});
				$thr.click(function() {
					$(this).find('input').click();
				});
				var $tbr = $('table tbody tr');
				var $checkItemTd = $('<td><input type="checkbox" name="checkItem" /></td>');
				$tbr.prepend($checkItemTd);
				$tbr.find('input').click(function(event) {
					$(this).parent().parent().toggleClass('warning');
					$checkAll.prop('checked', $tbr.find('input:checked').length == $tbr.length ? true : false);
					event.stopPropagation();
				});
				$tbr.click(function() {
					$(this).find('input').click();
				});
			}

			var options = {
				// target:        "p",   
				beforeSubmit: showRequest, 
				success: showResponse, 
				error: showError,
				//另外的一些属性:
				type: "POST",
				url: "rest/transcodeQuery",
				data: $('#transcode-query').serialize(),
				async: false, // 默认是form的action，如果写的话，会覆盖from的action.
				//type:              // 默认是form的method，如果写的话，会覆盖from的method.('get' or 'post').
				//dataType:  null        // 'xml', 'script', or 'json' (接受服务端返回的类型.)
				//clearForm: true        // 成功提交后，清除所有的表单元素的值.
				// resetForm: true        // 成功提交后，重置所有的表单元素的值.
				//由于某种原因,提交陷入无限等待之中,timeout参数就是用来限制请求的时间,
				//当请求大于3秒后，跳出请求.
				//timeout:   3000
			};

			$('#transcode-query').submit(function() {
				$(this).ajaxSubmit(options);
				return false; 
			});

			function showRequest() {
				//$("#sub").html("<button type='submit' class='btn btn-default' disable='true'>查询</button>");
			}

			function showError(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.status + " " + errorThrown);
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
					url: "rest/download",
					data: {
						"page": "delete"
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
					url: "rest/initTranscodeInfo",
					success: function(data) {
						showResponse(data, null);
					}
				});
			};

			function showResponse(data, statusText) {
				var onePageNum = 25;

				var tableStr = "<table class='table table-striped' id='transform-info'>"
				tableStr = tableStr + "<caption>文件信息</caption><thead><tr class='danger'><th>文件ID</th><th>上传时间</th><th>是否已发送转码请求</th><th>转码格式</th></tr></thead>"
				tableStr = tableStr + "<tbody id='info-body'>";
				if(data.root.length < onePageNum) {
					onePageNum = data.root.length;
				}
				for(var i = 0; i < onePageNum; i++) {
					if(i % 2 == 0) {
						tableStr = tableStr + "<tr class='active'>";
					} else {
						tableStr = tableStr + "<tr>";
					}
					tableStr = tableStr +
						"<td name='fileId'>" + data.root[i].fileId + "</td>" +
						"<td >" + data.root[i].uploadDate + "</td>" +
						"<td>" + data.root[i].transcode + "</td>" +
						"<td name='format'>" + data.root[i].transcodeFormat + "</td></tr>"

				}
				tableStr = tableStr + "</tbody></table>";
				$("#transform-info").html(tableStr);
				initTableCheckbox();

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
			}

			$("#myButton5").click(function() {
				var selectedData = "";
				var allData = "";
				$(":checkbox:checked", "#info-body").each(function() {
					var tablerow = $(this).parent().parent();

					var fileId = tablerow.find("[name='fileId']").html().valueOf();
					var format = tablerow.find("[name='format']").html().valueOf();

					selectedData = selectedData + fileId + ":" + format + ";";
				});

				$.ajax({
					type: "POST",
					url: "rest/reTranscode",
					data: selectedData,
					async: false,
					contentType: "application/json",
					success: function(data) {
						if(data.failed.length > 0) {
							alert("failed reTranscode id: " + data.failed);
						} else {
							alert("reTranscode succeed");
						}
						addTable(data);
					},
					error: function(XMLHttpRequest, textStatus, errorThrown) {
						alert(XMLHttpRequest.status + " " + errorThrown);
					},

				});

			});

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
		<li>
			<a href="history.jsp" style="color:#FF6347">历史详情</a>
		</li>
		<li class="active">
			<a href="transform.jsp" style="color:#FF6347">转码重传</a>
		</li>
		<li>
			<a href="download.jsp" style="color:#FF6347">查询下载</a>
		</li>
		<li>
			<a href="archive.jsp" style="color:#FF6347">归档</a>
		</li>
		<li>
			<a href="delete.jsp" style="color:#FF6347"><strong>删除</strong></a>
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
			<h3 class="panel-title">转码重传</h3>
		</div>
		<div class="panel-body">
			<form class="form-inline" role="form" style="text-align:center;width:1100px;" id="transcode-query">
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
					<div class="col-sm-offset-2 col-sm-10">
						<button type="submit" class="btn btn-danger">获取需要转码文件</button>
					</div>
				</div>

				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						<button type="button" class="btn btn-danger" id="myButton5">重新发送转码请求</button>
					</div>
				</div>

			</form>

			<table class="table table-striped" style="width: 1150px;" id="transform-info">
				<caption>已归档文件</caption>
				<thead>
					<tr class='danger'>
						<th><input type="checkbox" id="checkAll" name="checkAll" /></th>
						<th>文件ID</th>
						<th>上传时间</th>
						<th>是否已发送转码请求</th>
						<th>转码格式</th>
					</tr>
				</thead>
				<tbody>

				</tbody>

			</table>

			<div id="page">
			</div>

		</div>
	</div>

	<body>