<head>
	<meta charset="utf-8">
	<title>Bootstrap 实例 - 条纹表格</title>
	<script src="http://cdn.static.runoob.com/libs/jquery/1.11.3/jquery.min.js"></script>
	<script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>

	<link rel="stylesheet" href="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/css/bootstrap.min.css">

	<link href="css/bootstrap-datetimepicker.min.css" rel="stylesheet" />
	<script src="js/moment-with-locales.js"></script>
	<script src="js/bootstrap-datetimepicker.js"></script>

	<script>
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

			$("#myButton4").click(function() {
				$.ajax({
					type: "GET",
					url: "rest/getToTranscode",
					async: false,
					success: function(data) {
						var tableStr = "<table class='table table-striped'>"
						tableStr = tableStr + "<caption>文件信息</caption><thead><th>文件ID</th><th>上传时间</th><th>是否转码</th><th>是否归档</th></thead>"
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
								"<td>" + data.root[i].transcode + "</td>" +
								"<td>" + data.root[i].archive + "</td></tr>"
						}
						tableStr = tableStr + "</tbody></table>";
						$("#tarnsform-info").html(tableStr);
						initTableCheckbox();
					}
				});
			});
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
		<li class="active">
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
			<form class="form-inline" role="form">
				<div class="form-group">
					<label class="sr-only" for="name">名称</label>
				</div>

				<button type="submit" class="btn btn-default">提交</button>
				<button type="button" class="btn btn-primary" id="myButton4" data-complete-text="Loading finished">请点击我</button>

				<table class="table table-striped"  id="tarnsform-info">
					<caption>文件信息</caption>
				</table>
		</div>
	</div>
	</form>

	<body>