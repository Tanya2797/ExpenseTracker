<%-- <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.cloud.datastore.QueryResults" %>
<%@ page import="com.controller.UserBasicOps.UserBasicOperations" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<style>
    table, th, td{
    border:1px solid black;
    }
    table {
      margin-left: auto;
      margin-right: auto;
      border-collapse: collapse;
    }
 </style>
<body>
<%
try{
Iterable<Entity> results = UserBasicOperations.viewAllExpenses();
%>

<table>

<tr>
<td>Name</td>
<td>ID</td>
<td>Date</td>
<td>Category</td>
<td>Title</td>
<td>Amount(in Rs.)</td>
<td>Notes</td>
</tr>

<%
for(Entity e:results)
{ 
%>
<tr>
<td><%=e.getProperty("name").toString() %>
<td><%=e.getKey() %></td>
<td><%=e.getProperty("date").toString() %></td>
<td><%=e.getProperty("category").toString() %></td>
<td><%=e.getProperty("title").toString() %></td>
<td><%=e.getProperty("amount").toString() %></td>
<td><%=e.getProperty("notes").toString() %></td>
</tr>



<%
		}
%>
</table>
<%
}catch(NullPointerException e)
{
	e.printStackTrace();
}
%>
</body>
</html> --%>