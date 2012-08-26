<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>OAuth 2.0 Autorizačňí server ČVUT</h1>

<div id="content">
    <h2>Dokumentace:</h2>
    <br />
    <p>
        <a href="http://oauth.net/2/" target="_blank">OAuth protocol reference</a>
    </p>
    <p>
        <a href="https://gitlab.fit.cvut.cz/cvut-oauth-server" target="_blank">zdrojové kódy</a>
    </p>
    <p>
        Na vyskusanie mozme zadat do prehliadaca:<br/>
        <a href="oauth/authorize?client_id=foodlovers&amp;redirect_uri=http%3A%2F%2Fexample.org&amp;response_type=code&amp;scope=read&amp;state=qB4ND7">priklad 1</a>
        <br />

        Zadanie takehto url do prehliadaca bude viest k chybe, pretoze client z id "veryverybadclient" neexistuje:<br/>
        <a href="oauth/authorize?client_id=veryverybadclient&amp;redirect_uri=http%3A%2F%2Fexample.org&amp;response_type=code&amp;scope=read&amp;state=qB4ND7">priklad 2</a>
        <br />
    </p>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
