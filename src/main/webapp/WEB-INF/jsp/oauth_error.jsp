<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

</head>

<body>

<h1>Je nám velice líto, vyskytl se problém ...</h1>

<div id="content">
    <h2>OAuth 2.0 Autorizačňí server ČVUT</h2>
    <p><c:out value="${message}"/>: <br/><br/>
        <b><i>(<c:out value="${error.summary}"/>)</i></b></p>
    <p>Zkuste zopakovat svůj pokus ješte jednou nebo kontaktuje technickou podporu.</p>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
