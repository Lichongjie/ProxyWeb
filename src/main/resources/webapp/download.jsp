<head>
	<meta charset="utf-8">
	<title>Proxy Web</title>
	<script src="js/jquery.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/jquery.form.min.js"></script>

	<link rel="stylesheet" href="css/bootstrap.min.css">

	<script>
		$(function() {
			function initTableCheckbox() {
				//alert("te");
				var $thr = $('table thead tr');
				var $checkAllTh = $('<th><input type="checkbox" id="checkAll" name="checkAll" /></th>');
				/*将全选/反选复选框添加到表头最前，即增加一列*/
				$thr.prepend($checkAllTh);
				/*“全选/反选”复选框*/
				var $checkAll = $("#checkAll");
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
			var options = {
				// target:        "p",   // 用服务器返回的数据 更新 id为output1的内容.
				//beforeSubmit: showRequest, // 提交前
				success: showResponse, // 提交后 
				//另外的一些属性: 
				type: "POST",
				url: "rest/fileInfo",
				data: $('#fileInfo').serialize(),
				async: false, // 默认是form的action，如果写的话，会覆盖from的action.
				//type:              // 默认是form的method，如果写的话，会覆盖from的method.('get' or 'post').
				//dataType:  null        // 'xml', 'script', or 'json' (接受服务端返回的类型.) 
				//clearForm: true        // 成功提交后，清除所有的表单元素的值.
				// resetForm: true        // 成功提交后，重置所有的表单元素的值.
				//由于某种原因,提交陷入无限等待之中,timeout参数就是用来限制请求的时间,
				//当请求大于3秒后，跳出请求. 
				//timeout:   3000 
			};
			$('#fileInfo').submit(function() {
				$(this).ajaxSubmit(options);
				return false; //来阻止浏览器提交.
			});

			function showRequest() {
				//	$("#sub").html("<button type='submit' class='btn btn-default' disable='true'>查询</button>");
			}
			
			function init() {
				alert("trest");
                
				$.ajax({
					type: "get",
					url: "rest/initFileInfo",
					success: function(data) {
						showResponse(data,null);
					}
				});
			}


			function showResponse(data, statusText) {
				var tableStr = "<table class='table table-striped'>"
				tableStr = tableStr + "<caption>文件信息</caption><thead><tr class='danger'><th>文件ID</th><th>上传时间</th><th>是否转码</th><th>转码是否成功</th><th>转码格式</th><th>是否归档</th><th>是否删除</th></tr></thead>"
				tableStr = tableStr + "<tbody id='info-body'>";
				for(var i = 0; i < data.root.length; i++) {
					if(i % 2 == 0) {
						tableStr = tableStr + "<tr class='active'>";
					} else {
						tableStr = tableStr + "<tr>";
					}
					tableStr = tableStr +
						"<td name='fileId'>" + data.root[i].fileId + "</td>" +
						"<td>" + data.root[i].uploadDate + "</td>" +
						"<td>" + data.root[i].toTranscode + "</td>" +
						"<td>" + data.root[i].transcode + "</td>" +
						"<td>" + data.root[i].transcodeFormat + "</td>" +
						"<td>" + data.root[i].archive + "</td>" +
						"<td>" + data.root[i].move + "</td></tr>"
				}
				tableStr = tableStr + "</tbody></table>";
				$("#fileInfoResult").html(tableStr);
				initTableCheckbox();

				//$("#sub").html("<button type='submit' class='btn btn-default' disable='false'>查询</button>");
			}
			init();


		});
		$(function() {
			
			
			$("#myButton5").click(function() {
				alert("test");

				var selectedData = "";
				$(":checkbox:checked", "#info-body").each(function() {
					var tablerow = $(this).parent().parent();

					var fileId = tablerow.find("[name='fileId']").html().valueOf();
					selectedData = selectedData + fileId + ";";
					alert(fileId);
				
					var form = $("<form>");
					form.attr('style', 'display:none');
					form.attr('target', '');
					form.attr('method', 'post');
					form.attr('action', 'rest/download');
					//form.attr('Content-Type', 'application/json;charset=utf-8');

					var input1 = $('<input>');
					input1.attr('type', 'hidden');
					input1.attr('name', 'id');
					input1.attr('value', fileId);

					$('body').append(form);
					form.append(input1);

					form.submit();
					form.remove();
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
			<a href="metrics.jsp" style="color:#FF6347">监控</a>
		</li>
		<li>
			<a href="history.jsp" style="color:#FF6347">历史详情</a>
		</li>
		<li>
			<a href="transform.jsp" style="color:#FF6347">转码重传</a>
		</li>
		<li class="active">
			<a href="download.jsp" style="color:#FF6347"><strong>文件管理</strong></a>
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
			<h3 class="panel-title">下载</h3>
		</div>
		<div class="panel-body">
			<form class="form-inline" role="form" id="fileInfo">
				<div class="form-group">
					<label class="sr-only" for="name">名称</label>
					<input type="text" class="form-control" id="name" name='fileId' placeholder="请输入名称">
				</div>

				<button type="submit" class="btn btn-danger">查询</button>
				<button type="button" class="btn btn-danger" id="myButton5">下载选中文件</button>
			</form>

			<table class="table table-striped" style="width: 1150px;" id="fileInfoResult">
				<caption>文件信息</caption>
				<thead>
					<tr class='danger'>
						<th><input type="checkbox" id="checkAll" name="checkAll" /></th>
						<th>文件ID</th>
						<th>上传时间</th>
						<th>是否转码</th>
						<th>转码是否成功</th>
						<th>转码格式</th>
						<th>是否归档</th>
						<th>是否删除</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>

	<body>