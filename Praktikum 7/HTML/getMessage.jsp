<%-- 
    Document   : getMessage
    Created on : 14.05.2020, 15:43:38
    Author     : benno
--%>
<%@ page import="chatsystem.servlet.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="refresh" content="1; URL=/projekt/Chat?action=getMessage">
        <title>JSP Page</title>
    </head>
    <body>
        <textarea readonly cols="50" rows="10"><%
            ClientProxyImpl clientProxy = (ClientProxyImpl)session.getAttribute(Chat.CLIENT_PROXY_ATTR);
            for(ClientProxyImpl.Message message: clientProxy.messages){
                out.print(message.toString());
            }
        %></textarea>
    </body>
</html>
