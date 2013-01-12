<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<style type="text/css">
    /* Override some defaults */
    html, body {
        background-color: #eee;
    }
    body {
        margin:0;
        padding:0;
        height: 100%;
    }
    .container > footer p {
        text-align: center; /* center align it with the container */
    }
    .container {

    }
    #loginHere {
        min-width: 700px;
    }

    /* The white background content wrapper */
    .container > .content {
        background-color: #fff;
        /*padding: 20px;*/
        margin: 0 -20px; /* negative indent the amount of the padding to maintain the grid system */
        -webkit-border-radius: 0 0 6px 6px;
        -moz-border-radius: 0 0 6px 6px;
        border-radius: 0 0 6px 6px;
        -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.15);
        -moz-box-shadow: 0 1px 2px rgba(0,0,0,.15);
        box-shadow: 0 1px 2px rgba(0,0,0,.15);
    }

    /* Wrapper for page content to push down footer */
    #wrap {
        padding-top: 100px;
        position:relative;
        padding-left: 10%;
        min-height: 100%;
        height: auto !important;
        height: 100%;
        /* Negative indent footer by it's height */
        margin: 0 auto -60px;
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
                        <li><a href="#about">Chci vědet více</a></li>
                        <li><a href="#contact">Kontakt</a></li>
                    </ul>
                </div><!--/.nav-collapse -->
            </div>
        </div>
    </div>

    <div id="wrap">
        <div class="container">
            <div class="row">
                <div class="span1">
                    <img src="http://oauth.net/images/oauth-2-sm.png" alt="OAuth logo" style="padding-top: 15px"/> 
                </div>
                <div class="span8">
                    <h1>OAuth 2.0 Autorizačňí server ČVUT</h1>
                </div>
            </div>
            <div class="row">
                <div class="span9">
                    <c:if test="${not empty param.authentication_error}">
                        <div class="alert alert-error">
                            <button type="button" class="close" data-dismiss="alert">&times;</button>
                            <h4>Uuups!</h4>
                            Zadané uživatelské jméno nebo heslo nejsou správně.
                        </div>
                    </c:if>
                    <c:if test="${not empty param.authorization_error}">
                        <div class="alert alert-error">
                            <button type="button" class="close" data-dismiss="alert">&times;</button>
                            <h4>Woops!</h4>
                            Nejste oprávněn k přístupu ku tomuto zdroji.
                        </div>
                    </c:if>
                    <div class="alert alert-info">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        We've got a grand total of 1 user: <strong>tomy</strong>. Go ahead
                        and log in. tomy's password is <strong>best</strong>.
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="span8">
                    <form class="form-horizontal" id="loginHere" method='post' action='<c:url value="/login.do"/>'>
                        <fieldset>
                            <legend>Přihlásení</legend>
                            <div class="control-group">
                                <label class="control-label" for="input01">Jméno</label>
                                <div class="controls">
                                    <input type="text" class="input-xlarge" id="user_name" name="j_username" rel="popover" data-content="Enter your first and last name." data-original-title="Full Name"/>

                                </div>
                            </div>

                            <!--                            <div class="control-group">
                                                            <label class="control-label" for="input01">Email</label>
                                                            <div class="controls">
                                                                <input type="text" class="input-xlarge" id="user_email" name="user_email" rel="popover" data-content="What’s your email address?" data-original-title="Email"/>
                            
                                                            </div>
                                                        </div>-->

                            <div class="control-group">
                                <label class="control-label" for="input01">Heslo</label>
                                <div class="controls">
                                    <input type="password" class="input-xlarge" id="pwd" name="j_password" rel="popover" data-content="6 characters or more! Be tricky" data-original-title="Password" />

                                </div>
                            </div>

                            <div class="control-group">
                                <label class="control-label" for="input01"></label>
                                <div class="controls">
                                    <button type="submit" class="btn btn-large btn-success" rel="tooltip" title="first tooltip">Přihlásit se</button>

                                </div>

                            </div>

                        </fieldset>
                    </form>

                </div>
            </div>
        </div>

    </div>

    <script src="js/jquery-1.8.3.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script type="text/javascript" src="http://jzaefferer.github.com/jquery-validation/jquery.validate.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#loginHere").validate({
                rules: {
                    j_username: "required",
                    user_email: {
                        required: true,
                        email: true
                    },
                    j_password: {
                        required: true,
                        minlength: 4
                    }
                },
                messages: {
                    j_username: "Zadejte prosím Váš login",
//                    user_email: {
//                        required: "Enter your email address",
//                        email: "Enter valid email address"
//                    },
                    j_password: {
                        required: "Zadejte prosím Vaše heslo",
                        minlength: "Heslo musí pozestávat z minímalně 6 znaků"
                    }
                },
                errorClass: "help-inline",
                errorElement: "span",
                highlight: function(element, errorClass, validClass) {
                    $(element).parents('.control-group').addClass('error');
                },
                unhighlight: function(element, errorClass, validClass) {
                    $(element).parents('.control-group').removeClass('error');
                }
            });
        });
    </script>


    <%@ include file="/WEB-INF/jspf/footer.jspf" %>
