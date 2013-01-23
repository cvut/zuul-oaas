<%@ page import="org.springframework.security.core.AuthenticationException" %>
<%@ page import="org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException" %>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<link type="text/css" rel="stylesheet"
      href="<c:url value='/css/bootstrap.min.css'/>" />
<link type="text/css" rel="stylesheet"
      href="<c:url value='/css/bootstrap-responsive.min.css'/>" />

<style type="text/css">
    /* Override some defaults */
    html, body {

    }
    body {

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

    <div class="navbar navbar-fixed-top">
        <div class="navbar-inner">
            <div class="container">
                <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </a>
                <a class="brand" href="/auth">OAuth 2.0 Autorizačňí server ČVUT</a>
                <div class="nav-collapse collapse">
                    <ul class="nav">
                        <li><a href="#about">Chci vědet více <i class="icon-question-sign"/></a></li>
                        <li><a href="#contact">Kontakt</a></li>
                    </ul>
                </div><!--/.nav-collapse -->
            </div>
        </div>
    </div>

    <div id="container-main" class="container">
        <% if (session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) != null && !(session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) instanceof UnapprovedClientAuthenticationException)) {%>
        <div class="error">
            <h2>Ups!</h2>

            <p>Access could not be granted. (<%= ((AuthenticationException) session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage()%>)</p>
        </div>
        <% }%>
        <c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION"/>
        <authz:authorize ifAllGranted="ROLE_USER">
            <div class="row">
                <div class="span4">

                    <img src="http://oauth.net/images/oauth-2-sm.png" alt="OAuth logo" style="padding-left: 30%; padding-bottom: 10%"/> 
                    <!--<h3>Proč jsem se tady ocitnul?</h3>-->
                    <ul class="muted" style="font-family: sans-serif; font-weight: 100">
                        <li><strong class="emphasis">Autorizace pomocí nového protokolu <span class="badge badge-info">OAuth 2.0</span></strong> umožňuje uživatelům sdílet data, texty, fotografie a videa uložená na jednom zařízením, s jiným zařízením, <strong class="emphasis">aniž by ste museli vyzradit svoje přístupové údaje.</strong></li>
                        <br/>
                        <li>Protokol <span class="badge badge-info">OAuth 2.0</span> úspěšne používají společnosti jako je <strong class="emphasis">Google, Facebook, Twitter, Yahoo a další.</strong></li>
                        <br/>
                    </ul>
                </div>
                <div class="span1"></div>
                <div class="span7">
                    <div id="form-approve">
                        <!--<h2>OAuth 2.0 Autorizačňí server ČVUT</h2>-->
                        <div class="row">
                            <div class="span7">
                                <h2>Milý uživateli, prosím potvrďte</h2>
                                <br/>
                                <p class="lead">jestli souhlasíte, aby bylo umožneno aplikaci <strong class="emphasis">"<c:out value="${client.getAdditionalInformation()['product-name']}"/>"</strong> přistoupit k nekerým Vašim <strong class="emphasis">chráneným zdrojům.</strong></p>
                            </div>

                        </div>
                        <div class="row">

                            <br/>
                            <div class="span6">
                                <table>
                                    <tr>
                                        <td style="padding-right: 25px">
                                            <form id="confirmationForm" name="confirmationForm" action="<%=request.getContextPath()%>/oauth/authorize" method="post" >
                                                <input name="user_oauth_approval" value="true" type="hidden"/>
                                                <!--<label>-->
                                                <button name="authorize" type="submit" class="btn btn-large btn-success">
                                                    <i class="icon-ok icon-white"></i> Ano, souhlasím
                                                </button>
                                                <!--</label>-->
                                            </form>
                                        </td>

                                        <td>  
                                            <form id="denialForm" name="denialForm" action="<%=request.getContextPath()%>/oauth/authorize" method="post">
                                                <input name="user_oauth_approval" value="false" type="hidden" />
                                                <!--<label>-->
                                                <button name="deny" type="submit" class="btn btn-large btn-danger">
                                                    <i class="icon-ban-circle icon-white"></i> Ne, nesouhlasím
                                                </button>
                                                <!--</label>-->
                                            </form>

                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!--<img src="http://static.simpledesktops.com/uploads/desktops/2012/11/20/Bubbles.png" alt="Image Description" style="opacity: 0.15; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>-->
        <!--<img src="http://www.dottotrains.com/_img/footer/footer3.png" alt="Image Description" style="opacity: 0.28; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>-->
        <!--<img src="http://www.icanbecreative.com/res/freeweb/2011/04/fly-with-me.png" alt="Image Description" style="opacity: 0.2; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>-->
        <!--<img src="http://static.simpledesktops.com/uploads/desktops/2011/06/13/dandelion.png" alt="Image Description" style="opacity: 0.5; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>-->
        <img src="http://static.simpledesktops.com/uploads/desktops/2011/04/12/Franklin.png" alt="Image Description" style="opacity: 0.5; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>
        <!--<img src="http://www.icanbecreative.com/res/freeweb/2011/04/take-note.png" alt="Image Description" style="opacity: 0.2; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>-->
        <!--<img src="http://www.icanbecreative.com/res/freeweb/2011/04/fibonacci.png" alt="Image Description" style="opacity: 0.3; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>-->
        <!--<img src="http://static.simpledesktops.com/uploads/desktops/2011/12/05/Wallpaper1.png" alt="Image Description" style="opacity: 0.3; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>-->
        <!--<img src="http://static.simpledesktops.com/uploads/desktops/2010/11/30/Split.png" alt="Image Description" style="opacity: 0.1; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>-->
        <!--<img src="http://static.simpledesktops.com/uploads/desktops/2011/06/03/Black_Bokeh.png" alt="Image Description" style="opacity: 0.2; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>-->
        <!--<img src="http://static.simpledesktops.com/uploads/desktops/2011/06/02/mono.png" alt="Image Description" style="opacity: 0.2; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>-->
        <!--<img src="http://static.simpledesktops.com/uploads/desktops/2011/03/10/tornado.png" alt="Image Description" style="opacity: 0.2; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>-->
        <!--<img src="http://static.simpledesktops.com/uploads/desktops/2010/12/08/watergrass.png" alt="Image Description" style="opacity: 0.5; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>-->
        <!--<img src="http://static.simpledesktops.com/uploads/desktops/2011/04/12/Edison.png" alt="Image Description" style="opacity: 0.5; position: absolute; bottom: 0px; width:100%;height: auto; z-index: -1"/>-->


    </authz:authorize>


    <script src="<c:url value='/js/jquery-1.8.3.min.js'/>"></script>
    <script src="<c:url value='/js/bootstrap.min.js'/>"></script>

    <%@ include file="/WEB-INF/jspf/footer.jspf" %>
