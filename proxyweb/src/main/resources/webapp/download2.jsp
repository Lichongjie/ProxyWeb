<head>
	<meta charset="utf-8">
	<title>Proxy Web</title>
	<script src="js/jquery.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/jquery.form.min.js"></script>

	<link rel="stylesheet" href="css/bootstrap.min.css">

	<script type="text/javascript">
		function initTableCheckbox() {
			var $thr = $('#fileInfoResult thead tr');
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
				url: "rest/jumpDownloadInfo",
				data: {
					"index": index
				},
				dataType: "json",
				success: function(data) {
					var tableStr = "<table class='table table-striped' id='fileInfoResult'>"
					tableStr = tableStr + "<caption>文件信息</caption><thead><tr class='danger'><th>文件ID</th><th>上传时间</th><th>是否已归档</th><th>是否删除</th></tr></thead>"
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

							"<td>" + data.root[i].archive + "</td>" +
							"<td>" + data.root[i].move + "</td></tr>"
					}
					tableStr = tableStr + "</tbody></table>";
					$("#fileInfoResult").html(tableStr);
					initTableCheckbox();
				}
			});
		}
	</script>

	<script>
		$(function() {
			function initTableCheckbox() {
				//alert("te");
				var $thr = $('table thead tr');
				var $checkAllTh = $('<th><input type="checkbox" id="checkAll" name="checkAll" /></th>');
				$thr.prepend($checkAllTh);
				var $checkAll = $("#checkAll");
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
				// target:        "p",   // 用服务器返回的数据 更新 id为output1的内容.
				//beforeSubmit: showRequest, // 提交前
				success: showResponse, // 提交后
				//另外的一些属性:
				type: "POST",
				url: "rest/fileInfo",
				data: $('#fileInfo').serialize(),
				async: false, // 默认是form的action，如果写的话，会覆盖from的action.
				contentType: "application/x-www-form-urlencoded;charset=UTF-8",
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
						"page": "download2"
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
					url: "rest/initFileInfo",
					success: function(data) {
						showResponse(data, null);
					}
				});
			}

			function showResponse(data, statusText) {

				var onePageNum = 25;

				var tableStr = "<table class='table table-striped' id='fileInfoResult'>"
				tableStr = tableStr + "<caption>文件信息</caption><thead><tr class='danger'><th>文件ID</th><th>上传时间</th><th>是否已归档</th><th>是否删除</th></tr></thead>"
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
						"<td>" + data.root[i].uploadDate + "</td>" +
						"<td>" + data.root[i].archive + "</td>" +
						"<td>" + data.root[i].move + "</td></tr>"
				}
				tableStr = tableStr + "</tbody></table>";
				$("#fileInfoResult").html(tableStr);
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
			init();
		});

		$(function() {
			$("#myButton5").click(function() {
				var selectedData = "";
				var i = 0;
				$(":checkbox:checked", "#info-body").each(function() {
					var tablerow = $(this).parent().parent();
					var fileId = tablerow.find("[name='fileId']").html().valueOf();
					selectedData = selectedData + fileId + ";";
					i = i + 1;
				});
				if(i == 0) {
					alert("请先选中文件");
				} else {
					$.ajax({
						type: "POST",
						url: "rest/downloadCheck",
						data: {
							"num": i
						},
						dataType: "json",
						async: false,
						contentType: "application/json",
						success: function(data) {
							if(data.result == "true") {
								var form = $("<form>");
								form.attr('style', 'display:none');
								form.attr('target', '');
								form.attr('method', 'post');
								form.attr('action', 'rest/download');
								form.attr('Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8');

								var input1 = $('<input>');
								input1.attr('type', 'hidden');
								input1.attr('name', 'id');
								input1.attr('value', selectedData);

								$('body').append(form);
								form.append(input1);

								form.submit();
								form.remove();
							} else {
								alert("too many files to download");
							}
						}
					});
				}

			});
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

		<p class="text-right" style="font-family:'sans-serif';color:#FF6347;font-size:20px;margin-top: 8px;margin-bottom: 2px;margin-right: 30px;">
			<button style="margin-right: 20px;" type="button" class="btn btn-primary btn-sm" id="logOut">log out</button>
			<font style="font-family:'sans-serif';color:#595959;font-size:15px;margin-right: 20px;">user: download</font>
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
					<input type="text" size="80" class="form-control input-xlarge" id="name" name='fileId' placeholder="请输入名称(用分号隔开)">
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
						<th>是否归档</th>
						<th>是否删除</th>
					</tr>
				</thead>
			</table>

			<div id="page">
			</div>

		</div>
	</div>
</body>