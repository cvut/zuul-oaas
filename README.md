Autorizační server pro IS ČVUT
==============================

TODO

Konfigurace Tomcat 7
--------------------

### Knihovny

Do Tomcat 7 je nutné přidat následující knihovny (typicky do adresáře `${CATALINA_HOME}/lib`):

* jstl-1.2.jar ([JSTL 1.2](http://repo1.maven.org/maven2/javax/servlet/jstl/1.2/jstl-1.2.jar))
* postgresql-9.1-901.jdbc4.jar ([JDBC4 Postgresql Driver](http://jdbc.postgresql.org/download.html)
* tomcat-jdbc.jar ([JDBC Connection Pool](http://people.apache.org/~fhanik/jdbc-pool/))
