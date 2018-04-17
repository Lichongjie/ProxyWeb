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
			var $thr = $('#delete-info thead tr');
			var $checkAllTh = $('<th><input type="checkbox" id="checkAll" name="checkAll" /></th>');
			/*将全选/反选复选框添加到表头最前，即增加一列*/
			$thr.prepend($checkAllTh);
			/*“全选/反选”复选框*/
			var $checkAll = $thr.find('input');
			$checkAll.click(function(event) {
				/*将所有行的选中状态设成全选框的选中状态*/
				$tbr.find('input').prop('checked', $(this).prop('checked'));
				/*并调整所有选中行的CSS样式*/
				if($(this).prop('checked')) {
					$tbr.find('input').parent().parent().addClass('warning');
				} else {
					$tbr.find('input').parent().parent().removeClass('warning');
				}
				/*阻止向上冒泡，以防再次触发点击操作*/
				event.stopPropagation();
			});
			/*点击全选框所在单元格时也触发全选框的点击操作*/
			$thr.click(function() {
				$(this).find('input').click();
			});
			var $tbr = $('table tbody tr');
			var $checkItemTd = $('<td><input type="checkbox" name="checkItem" /></td>');
			/*每一行都在最前面插入一个选中复选框的单元格*/
			$tbr.prepend($checkItemTd);
			/*点击每一行的选中复选框时*/
			$tbr.find('input').click(function(event) {
				/*调整选中行的CSS样式*/
				$(this).parent().parent().toggleClass('warning');
				/*如果已经被选中行的行数等于表格的数据行数，将全选框设为选中状态，否则设为未选中状态*/
				$checkAll.prop('checked', $tbr.find('input:checked').length == $tbr.length ? true : false);
				/*阻止向上冒泡，以防再次触发点击操作*/
				event.stopPropagation();
			});
			/*点击每一行时也触发该行的选中操作*/
			$tbr.click(function() {
				$(this).find('input').click();
			});
		}

		function jump(a) {
			var index = a;
			$.ajax({
				type: "post",
				url: "rest/jumpDeleteInfo",
				data: {
					"index": index
				},
				dataType: "json",
				success: function(data) {
					var tableStr = "<table class='table table-striped' id='delete-info' name='delete-info'><caption>未归档数据</caption>";
					tableStr = tableStr + "<thead><tr class='danger'><th>文件ID</th><th>上传时间</th><th>是否需要转码</th><th>是否已转码</th><th>转码格式</th><th>是否已归档</th><th>是否已删除</th></tr></thead>"
					tableStr = tableStr + "<tbody id='info-body'>";
					for(var i = 0; i < data.root.length; i++) {
						if(i % 2 == 0) {
							tableStr = tableStr + "<tr class='active'>";
						} else {
							tableStr = tableStr + "<tr>";
						}
						tableStr = tableStr +
							"<td name='fileId' >" + data.root[i].fileId + "</td>" +
							"<td>" + data.root[i].uploadDate + "</td>" +
							"<td>" + data.root[i].toTranscode + "</td>" +
							"<td>" + data.root[i].transcode + "</td>" +
							"<td>" + data.root[i].transcodeFormat + "</td>" +
							"<td>" + data.root[i].archive + "</td>" +
							"<td>" + data.root[i].move + "</td></tr>"

					}
					tableStr = tableStr + "</tbody></table>";
					$("#delete-info").html(tableStr);
					initTableCheckbox();
				}
			});
		}
	</script>

	<script>
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
			function initTableCheckbox() {
				var $thr = $('#delete-info thead tr');
				var $checkAllTh = $('<th><input type="checkbox" id="checkAll" name="checkAll" /></th>');
				/*将全选/反选复选框添加到表头最前，即增加一列*/
				$thr.prepend($checkAllTh);
				/*“全选/反选”复选框*/
				var $checkAll = $thr.find('input');
				$checkAll.click(function(event) {
					/*将所有行的选中状态设成全选框的选中状态*/
					$tbr.find('input').prop('checked', $(this).prop('checked'));
					/*并调整所有选中行的CSS样式*/
					if($(this).prop('checked')) {
						$tbr.find('input').parent().parent().addClass('warning');
					} else {
						$tbr.find('input').parent().parent().removeClass('warning');
					}
					/*阻止向上冒泡，以防再次触发点击操作*/
					event.stopPropagation();
				});
				/*点击全选框所在单元格时也触发全选框的点击操作*/
				$thr.click(function() {
					$(this).find('input').click();
				});
				var $tbr = $('table tbody tr');
				var $checkItemTd = $('<td><input type="checkbox" name="checkItem" /></td>');
				/*每一行都在最前面插入一个选中复选框的单元格*/
				$tbr.prepend($checkItemTd);
				/*点击每一行的选中复选框时*/
				$tbr.find('input').click(function(event) {
					/*调整选中行的CSS样式*/
					$(this).parent().parent().toggleClass('warning');
					/*如果已经被选中行的行数等于表格的数据行数，将全选框设为选中状态，否则设为未选中状态*/
					$checkAll.prop('checked', $tbr.find('input:checked').length == $tbr.length ? true : false);
					/*阻止向上冒泡，以防再次触发点击操作*/
					event.stopPropagation();
				});
				/*点击每一行时也触发该行的选中操作*/
				$tbr.click(function() {
					$(this).find('input').click();
				});

			}

			var options = {
				// target:        "p",   // 用服务器返回的数据 更新 id为output1的内容.
				beforeSubmit: showRequest, // 提交前
				success: showResponse, // 提交后 
				error: showError,
				//另外的一些属性: 
				type: "POST",
				url: "rest/deleteQuery",
				data: $('#delete-query').serialize(),
				async: false, // 默认是form的action，如果写的话，会覆盖from的action.
				//type:              // 默认是form的method，如果写的话，会覆盖from的method.('get' or 'post').
				//dataType:  null        // 'xml', 'script', or 'json' (接受服务端返回的类型.) 
				//clearForm: true        // 成功提交后，清除所有的表单元素的值.
				// resetForm: true        // 成功提交后，重置所有的表单元素的值.
				//由于某种原因,提交陷入无限等待之中,timeout参数就是用来限制请求的时间,
				//当请求大于3秒后，跳出请求. 
				//timeout:   3000 
			};

			$('#delete-query').submit(function() {
				$(this).ajaxSubmit(options);
				return false; //来阻止浏览器提交.
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
					url: "rest/initDeleteInfo",
					success: function(data) {
						showResponse(data, null);
					}
				});
			};

			function showResponse(data, statusText) {
				var onePageNum = 25;

				var tableStr = "<table class='table table-striped' id='delete-info' name='delete-info'><caption>未归档数据</caption>";
				tableStr = tableStr + "<thead><tr class='danger'><th>文件ID</th><th>上传时间</th><th>是否需要转码</th><th>是否已转码</th><th>转码格式</th><th>是否已归档</th><th>是否已删除</th></tr></thead>"
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
						"<td name='fileId' >" + data.root[i].fileId + "</td>" +
						"<td>" + data.root[i].uploadDate + "</td>" +
						"<td>" + data.root[i].toTranscode + "</td>" +
						"<td>" + data.root[i].transcode + "</td>" +
						"<td>" + data.root[i].transcodeFormat + "</td>" +
						"<td>" + data.root[i].archive + "</td>" +
						"<td>" + data.root[i].move + "</td></tr>"

				}
				tableStr = tableStr + "</tbody></table>";
				$("#delete-info").html(tableStr);
				initTableCheckbox();
				//$("#sub").html("<button type='submit' class='btn btn-default' disable='false'>查询</button>");

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
				$(":checkbox:checked", "#info-body").each(function() {
					var tablerow = $(this).parent().parent();

					var fileId = tablerow.find("[name='fileId']").html().valueOf();
					selectedData = selectedData + fileId + ";";
				});
				if(confirm("确定要删除吗?")) {
					$.ajax({
						type: "POST",
						url: "rest/delete",
						data: selectedData,
						async: false,
						contentType: "application/json",
						success: function(data) {
							if(data.failed.length > 0) {
								alert("delete failed id: " + data.failed);
							} else {
								alert("delete succeed");
							}
							showResponse(data);
						}
					});
				}
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
		<li>
			<a href="transform.jsp" style="color:#FF6347">转码重传</a>
		</li>
		<li>
			<a href="download.jsp" style="color:#FF6347">查询下载</a>
		</li>
		<li>
			<a href="archive.jsp" style="color:#FF6347">归档</a>
		</li>
		<li class="active">
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
			<h3 class="panel-title">删除</h3>
		</div>
		<div class="panel-body">
			<form class="form-inline" role="form" style="text-align:center;width:1100px;" id="delete-query">
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
						<button type="submit" class="btn btn-danger">获取已归档文件</button>
					</div>
				</div>

				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						<button type="button" class="btn btn-danger" id="myButton5">删除选中文件</button>
					</div>
				</div>

			</form>

			<table class="table table-striped" style="width: 1150px;" id="delete-info">
				<caption>已归档文件</caption>
				<thead>
					<tr class='danger'>
						<th><input type="checkbox" id="checkAll" name="checkAll" /></th>
						<th>文件ID</th>
						<th>上传时间</th>
						<th>是否需要转码</th>
						<th>是否已转码</th>
						<th>是否归档</th>
						<th>是否删除</th>
					</tr>
				</thead>
				<tbody>

				</tbody>

			</table>

			<div id="page">
			</div>

		</div>
	</div>
</body>