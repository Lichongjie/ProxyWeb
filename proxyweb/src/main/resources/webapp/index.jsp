<!DOCTYPE html>
<html>

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
			$(function() {
				var options = {
					beforeSubmit: showRequest, 
					success: showResponse, 
					error: showError,
					type: "POST",
					url: "rest/login",
					data: $('#loginform').serialize(),
					contentType: "application/x-www-form-urlencoded;charset=UTF-8",
					async: false, 
				};

				$('#loginform').submit(function() {
					$(this).ajaxSubmit(options);
					return false; 
				});

				function showRequest() {
					//$("#sub").html("<button type='submit' class='btn btn-default' disable='true'>查询</button>");
				}

				function showError(XMLHttpRequest, textStatus, errorThrown) {
					alert(XMLHttpRequest.status + " " + errorThrown);
				}

				function showResponse(data) {
					if(data.level == "admin") {
						location.href = "Metric.jsp";
					} else {
						location.href = "download2.jsp";
					}
				}

				function authentication() {
					$.ajax({
						type: "POST",
						url: "rest/authentication",
						data: {
							"page": "index"
						},
						dataType: "json",
						async: false,
						contentType: "application/json",
						success: function(data) {
							if(data.result == "timeout") {
								alert(" login timeout");
								location.href = "index.jsp";
							} else if(data.result == "admin") {
								location.href = "Metric.jsp";
							} else if(data.result == "download") {
								location.href = "download2.jsp";
							}
						}
					});
				}
				authentication();
			});
		</script>

		<style>
			div.panel1 {
				margin-top: 50px;
				margin-bottom: 50px;
				margin-right: auto;
				margin-left: auto;
			}
			
			div.container1 {
				margin-right: auto;
				margin-left: auto;
			}
		</style>

		<ul class="nav nav-tabs" style="color:#FF6347">
			<p class="text-right" style="font-family:'sans-serif';color:#FF6347;font-size:20px;margin-top: 8px;margin-bottom: 2px;margin-right: 30px;">
				<strong>HTSC Proxy Web</strong>
			</p>
		</ul>
	</head>

	<body>

		<div class="panel  panel-danger panel1" style="width:400px;">
			<div class="panel-heading">
				<h3 class="panel-title">登录</h3>
			</div>
			<div class="panel-body">
				<form class="form-horizontal" role="form" id="loginform">
					<div class="form-group">
						<label for="firstname" class="col-sm-3 control-label">用户名</label>
						<div class="col-sm-8">
							<input type="text" class="form-control" id="userName" name="userName" placeholder="请输入用户名">
						</div>
					</div>
					<div class="form-group">
						<label for="lastname" class="col-sm-3 control-label">密码</label>
						<div class="col-sm-8">
							<input type="text" class="form-control" id="password" name="password" placeholder="请输入密码">
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-offset-3 col-sm-10">
							<button type="submit" class="btn btn-default">登录</button>
						</div>
					</div>
				</form>

			</div>
		</div>

	</body>

</html>