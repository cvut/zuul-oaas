<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<%@ include file="/WEB-INF/jspf/header-css.jspf" %>
<link type="text/css" rel="stylesheet"
      href="<c:url value='css/cerulean.min.css'/>" />
<link type="text/css" rel="stylesheet"
      href="<c:url value='css/bootstrap-responsive.min.css'/>" />
</head>

<body>

    <h1>OAuth 2.0 Autorizačňí server ČVUT</h1>

    <div id="content">
        <h2>Home</h2>

        <p>You have successfully authorized the request for a protected resource.</p>
    </div>

    <%@ include file="/WEB-INF/jspf/footer.jspf" %>
