<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="main" />
    <title><g:message code="default.report.label" /></title>
  	<r:require module="reports"/>

  <!-- TODO get rid of this -->
  <!-- Load Droid Sans remotely from Google Webfonts -->
  <link href='http://fonts.googleapis.com/css?family=Droid+Sans:400,700&subset=latin' rel='stylesheet' type='text/css'>

  %{-- https://developers.google.com/chart/interactive/docs/library_loading_enhancements --}%
  %{-- Note: autoload={'modules':[{'name':'visualization','version':'1','packages':['corechart','geochart']}]} is url encoded --}%
  <script type="text/javascript" src="https://www.google.com/jsapi?autoload=%7B%27modules%27%3A%5B%7B%27name%27%3A%27visualization%27%2C%27version%27%3A%271%27%2C%27packages%27%3A%5B%27corechart%27%2C%27geochart%27%5D%7D%5D%7D"></script>

</head>
  <body>
    <g:render template="${template}"/>
  </body>
</html>
