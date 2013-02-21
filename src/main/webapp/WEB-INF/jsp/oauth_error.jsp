<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<link type="text/css" rel="stylesheet"
      href="<c:url value='/css/bootstrap-cosmo.css'/>" />
<link type="text/css" rel="stylesheet"
      href="<c:url value='/css/bootstrap-responsive.min.css'/>" />

<style type="text/css">
    /* Override some defaults */
    html, body {
    }
    body {
        min-height 100%;
    }

    .well {
        /*padding-right: 20px;*/
    }

    #container-main{
        z-index: 100;
        padding-top: 6%;
        /*min-width: 900px;*/
    }
    #form-approve{
        /*width: 70%;*/
        /*min-width: 510px;*/
        /*padding-left: 40px;*/
        padding-top: 7%;
        /*padding-bottom: 7%;*/
    }
    footer{
        position:absolute;
        bottom:0;
        height:60px;   /* Height of the footer */
    }
</style>
<style>
    .emphasis{
        font-family: sans-serif; font-weight: normal;
    }
    body,
    input,
    select,
    textarea,
    .navbar-search .search-query {
        font-family: sans-serif;
        font-weight: 100;
    }
    h2{
        font-family: sans-serif;
        font-weight: 100;
    }
</style>

<!-- Le fav and touch icons -->
<link rel="shortcut icon" href="http://oauth.net/images/oauth-2-sm.png"/>
<link rel="apple-touch-icon" href="images/apple-touch-icon.png"/>
<link rel="apple-touch-icon" sizes="72x72" href="images/apple-touch-icon-72x72.png"/>
<link rel="apple-touch-icon" sizes="114x114" href="images/apple-touch-icon-114x114.png"/>
</head>

<body>

    <!--<div class="row">-->
    <div class="container" style="padding-top: 5%">

        <h1>Je nám velice líto, vyskytl se problém ...</h1>

        <div id="content">
            <h2>OAuth 2.0 Autorizačňí server ČVUT</h2>
            <div><c:out value="${message}"/>: <br/><br/>
                <div class="alert alert-block">
                    <b>(<c:out value="${error.summary}"/>)</b>
                </div>
            </div>
            <p>Zkuste zopakovat svůj pokus <a href="" onclick="history.back();">ješte jednou</a> nebo <a href="www.cvut.cz/support">nás kontaktuje.</a></p>
        </div>

        <!-- FOOTER -->
        <footer style="font-size: 80%;">
            <p >&copy; 2012 České vysoké učení technické v Praze. &middot; <a href="#">Soukromí</a> &middot; <a href="#">Podmínky</a></p>
        </footer>
    </div>
    <!--</div>-->
    <script src="<c:url value='/js/jquery-1.8.3.min.js'/>"></script>
    <script src="<c:url value='/js/bootstrap.min.js'/>"></script>

    <%@ include file="/WEB-INF/jspf/footer.jspf" %>
