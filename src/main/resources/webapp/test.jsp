<html>
<link href="css/bootstrap-datetimepicker.min.css" rel="stylesheet" />
	<script src="js/moment-with-locales.js"></script>
	<script src="js/bootstrap-datetimepicker.js"></script>
	<head>
		<script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
		<script type="text/javascript">
			$(function() {
				function test() {
					htmlobj = $.ajax({
						url: "rest/hello",
						async: false
					});
					$("#myDiv").html(htmlobj.responseText);
				}
				test();
			});
			
			<script type="text/javascript">
		$(function() {
			$('#history-query').submit(function() {
				htmlobj = $.ajax({
					type: "POST",
					url: "rest/history",
					data: $('#history-query').serialize(),
					async: false
				});
				$("#myDiv").html(htmlobj.responseText); // $.ajax({ // type: "POST", // url: "rest/history", // data: $('#history-query').serialize(), //success: function(data) { // $("#server-info").html(data); //var tableStr = "<table class='table table-striped' id='historyInfo><caption>监控数据</caption>";
				//tableStr = tableStr + "<th>日期</th><th>上传总数</th><th>上传成功数</th><th>上传失败数</th><th>下载总数</th><th>下载成功数</th><th>下载失败数</th><th>转码请求发送总数</th><th>转码请求发送成功数</th><th>转码请求发送失败数</th><th>是否归档</th>";
				/*
				for(var i = 0; i < data.root.length; i++) {
				//	if(i % 2 == 0) {
				//		tableStr = tableStr + "<tr class=' active '>";
				//	} else {
				//		tableStr = tableStr + "<tr>";
				//	}
					tableStr = tableStr + "<tr>";
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
				tableStr = tableStr + "</table>";*/

				//});
				//return false;
			});

		});
	</script>
		</script>
	</head>

	<body>

		<div id="myDiv">
			<h2>通过 AJAX 改变文本</h2></div>
		<button id="b01" type="button">改变内容</button>

	</body>

</html>