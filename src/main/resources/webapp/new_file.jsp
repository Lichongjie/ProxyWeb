<head>
	<meta charset="utf-8">
	<title>Bootstrap 实例 - 条纹表格</title>
	<script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
	<script src="//oss.maxcdn.com/jquery.form/3.50/jquery.form.min.js"></script>

	<script type="text/javascript">
		$(function() {
			
			function initTableCheckbox() {
				var $thr = $('table thead tr');
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

			//initTableCheckbox();

			$("#myButton4").click(function() {
				$.ajax({
					type: "GET",
					url: "rest/getToTranscode",
					async: false,
					success: function(data) {
						alert(data);
						var tableStr = "<table class='table table-striped' style='width: 1150px;'>"
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
								"<td>" + data.root[i].isTranscode + "</td>" +
								"<td>" + data.root[i].archive + "</td></tr>"
						}
						tableStr = tableStr + "</tbody></table>";
						$("#tarnsform-info").html(tableStr);
						initTableCheckbox();
					}
				});
				//addTable();
			});
		});
	</script>

</head>

<body>

	</div>
	</div>

	<button type="button" class="btn btn-primary" id="myButton4" data-complete-text="Loading finished">请点击我</button>

	<body>