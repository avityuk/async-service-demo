<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="en">
<head>
	<title>Asynchronous Service Webapp<</title>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
		   $("#call-sync").click(function(event){
			   lookup("/geocode-sync");
		   });

		   $("#call-async").click(function(event){
			   lookup("/geocode-async");
		   });
		   
		   function lookup(serviceUrl) {
			   $.ajax({
				   url: serviceUrl,
				   data: { address : $("#address").val() },
				   success: function(data) {
					   $("#response").text(data);
					}
			   });
		   }
		 });
	</script>
</head>
<body>
	<input id="address" type="text" size="50" /><br />
	<a id="call-sync" href="#">Call Synchronous Servlet</a><br />
	<a id="call-async" href="#">Call Asynchronous Servlet</a><br />
	
	<textarea id="response" rows="40" cols="80"></textarea>
</body>
</html>
