<%@ page import="org.springframework.security.core.AuthenticationException" %>
<%@ page import="org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException" %>

<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>OAuth 2.0 Autorizačňí server ČVUT</h1>

<div id="content">

    <% if (session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) != null && !(session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) instanceof UnapprovedClientAuthenticationException)) {%>
    <div class="error">
        <h2>Ups!</h2>

        <p>Access could not be granted. (<%= ((AuthenticationException) session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage()%>)</p>
    </div>
    <% }%>
    <c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION"/>

    <authz:authorize ifAllGranted="ROLE_USER">
        <h2>Milý uživateli, prosím potvrďte</h2>

        <p>jestli souhlasíte aby bylo umožneno "<c:out value="${client.clientId}"/>" přistoupit k nekerým Vašim chráneným zdrojům.</p>

        <form id="confirmationForm" name="confirmationForm" action="<%=request.getContextPath()%>/oauth/authorize" method="post">
            <input name="user_oauth_approval" value="true" type="hidden"/>
            <label>
                <input name="authorize" value="Ano, souhlasím" type="submit" />
            </label>
        </form>
        <form id="denialForm" name="denialForm" action="<%=request.getContextPath()%>/oauth/authorize" method="post">
            <input name="user_oauth_approval" value="false" type="hidden" />
            <label>
                <input name="deny" value="Ne, nesouhlasím" type="submit" />
            </label>
        </form>
    </authz:authorize>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
