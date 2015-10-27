<html>

<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->

    <!-- Bootstrap -->
    <link href="${pageContext.request.contextPath}/html/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/html/css/gamestyle.css" rel="stylesheet">

	<title>Score Window</title>
</head>

<body>

	<%@ page import="gammut.*" %>
	<% GameState gs = (GameState) getServletContext().getAttribute("gammut.gamestate"); %>

	<nav class="navbar navbar-inverse navbar-fixed-top">
  		<div class="container-fluid">
    		<div class="navbar-header">
      			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse-1">
      			</button>
    		</div>
      		<div class= "collapse navbar-collapse" id="navbar-collapse-1">
          		<ul class="nav navbar-nav">
            		<a class="navbar-brand" href="/gammut/games">GamMut</a>
          		</ul>
      		</div>
   		</div>
	</nav>



	<div id="splash">
		<% if (gs.getScore(0) > gs.getScore(1)) { %>
			<h1> Attacker Has Won! </h1>
		<% }
	    
	    else if (gs.getScore(0) < gs.getScore(1)) { %>
	    	<h1> Defender Has Won! </h1>
	    <%}
	    
	    else { %>
	    	<h1> It Was A Draw! </h1>
	    <%}%>
	</div>

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="js/bootstrap.min.js"></script>
</body>
</html>