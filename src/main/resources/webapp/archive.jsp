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
				var $thr = $('table thead tr');
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

			//initTableCheckbox();

			$("#myButton5").click(function() {

				alert("归档成功");
			});

			var options = {
				// target:        "p",   // 用服务器返回的数据 更新 id为output1的内容.
				beforeSubmit: showRequest, // 提交前
				success: showResponse, // 提交后 
				error: showError,
				//另外的一些属性: 
				type: "POST",
				url: "rest/archiveQuery",
				data: $('#archive-query').serialize(),
				async: false, // 默认是form的action，如果写的话，会覆盖from的action.
				//type:              // 默认是form的method，如果写的话，会覆盖from的method.('get' or 'post').
				//dataType:  null        // 'xml', 'script', or 'json' (接受服务端返回的类型.) 
				//clearForm: true        // 成功提交后，清除所有的表单元素的值.
				// resetForm: true        // 成功提交后，重置所有的表单元素的值.
				//由于某种原因,提交陷入无限等待之中,timeout参数就是用来限制请求的时间,
				//当请求大于3秒后，跳出请求. 
				//timeout:   3000 
			};

			$('#archive-query').submit(function() {
				$(this).ajaxSubmit(options);
				return false; //来阻止浏览器提交.
			});

			function showRequest() {
				//$("#sub").html("<button type='submit' class='btn btn-default' disable='true'>查询</button>");
			}

			function showError(data, statusText) {
				alert(data);
			}

			function showResponse(data, statusText) {
				var tableStr = "<table class='table table-striped' id='historyInfo'><caption>未归档数据</caption>";
				tableStr = tableStr + "<caption>文件信息</caption><thead><th>文件ID</th><th>上传时间</th><th>是否转码</th><th>转码是否成功</th><th>转码格式</th><th>是否归档</th></thead>"
				tableStr = tableStr + "<tbody>";
				for(var i = 0; i < data.root.length; i++) {
					if(i % 2 == 0) {
						tableStr = tableStr + "<tr class='active'>";
					} else {
						tableStr = tableStr + "<tr>";
					}
					tableStr = tableStr +
						"<td>" + data.root[i].fileId + "</td>" +
						"<td>" + data.root[i].uploadDate + "</td>" +
						"<td>" + data.root[i].toTranscode + "</td>" +
						"<td>" + data.root[i].transcode + "</td>" +
						"<td>" + data.root[i].transcodeFormat + "</td>" +
						"<td>" + data.root[i].archive + "</td></tr>"
				}
				tableStr = tableStr + "</tbody></table>";
				$("#archive-info").html(tableStr);
				initTableCheckbox();
				//$("#sub").html("<button type='submit' class='btn btn-default' disable='false'>查询</button>");

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
		<li>
			<a href="history.jsp">历史详情</a>
		</li>
		<li>
			<a href="transform.jsp">转码重传</a>
		</li>
		<li>
			<a href="download.jsp">文件管理</a>
		</li>
		<li class="active">
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
			<form class="form-inline" role="form" style="text-align:center;width:1100px;" id="archive-query">
				<div class="form-group">
					<label class="col-sm-2 control-label" style="float: left; width: 120px;height: 20px; font-size:20px">开始日期</label>
					<a class='input-group date' id='datetimepicker1'>
						<span class="input-group-addon" style="float: left; width: 50px; height: 30px;">
                         <span class="glyphicon glyphicon-calendar"></span>
						</span>
						<input type='text' class="form-control" id='begindate' style="width: 150px; height: 30px;" />
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
						<input type='text' class="form-control" id='enddate' style="width: 150px; height: 30px;" />

					</a>
				</div>

				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						<button type="submit" class="btn btn-default">获取未归档文件</button>
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						<button type="button" class="btn btn-default" id="myButton5">归档选中文件</button>
					</div>
				</div>

				<table class="table table-striped" style="width: 1150px;" id="archive-info">
					<thead>
						<th>文件ID</th>
						<th>上传时间</th>
						<th>是否转码</th>
						<th>转码是否成功</th>
						<th>是否归档</th>
					</thead>
				
				</table>
			</form>

		</div>
	</div>

	<body>