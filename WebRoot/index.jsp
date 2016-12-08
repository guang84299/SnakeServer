<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
    <h1>更改配置</h1>

<div style="margin:20px 0px;">
<form action="user_uploadConfig" method="post" style="margin: 0px;" enctype="multipart/form-data" class="g_from">
<table width="800" cellpadding="4" cellspacing="0" border="0">

<tr  >
	<td>路径:</td>
	<td><input type="file" id="config" name="config" value="浏览" style="width:280px;" /> </td>
</tr>



<br/>

<tr>
	
	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td><input type="submit" value="提交" /></td>
</tr>
</table> 
</form>
</div>

<h1>${requestScope.uploadConfig }</h1>


<h1>更改源码（程序专用）</h1>

<div style="margin:20px 0px;">
<form action="user_uploadSource" method="post" style="margin: 0px;" enctype="multipart/form-data" class="g_from">
<table width="800" cellpadding="4" cellspacing="0" border="0">

<tr  >
	<td>路径:</td>
	<td><input type="file" id="source" name="source" value="浏览" style="width:280px;" /> </td>
</tr>



<br/>

<tr>
	
	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td><input type="submit" value="提交" /></td>
</tr>
</table> 
</form>
</div>
<h1>${requestScope.uploadSource }</h1>
  </body>
</html>
