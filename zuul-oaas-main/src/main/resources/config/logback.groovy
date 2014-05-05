////// Logback configuration //////

context.name = 'zuul-oaas'

// export MBeans via JMX for monitoring
jmxConfigurator()

// conversion rule from Spring Boot
conversionRule 'wex', org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter

// define alias
def env = System.&getProperty

// variables LOG_FILE and LOG_PATH are set by Spring Boot
def logfile = env('LOG_FILE', "${env('LOG_PATH', '/tmp')}/${context.name}.log")


////// Appenders //////

appender 'CONSOLE', ConsoleAppender, {
    encoder PatternLayoutEncoder, {
        pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level --- %-50.50logger{49} : %msg%n%wex"
    }
}

appender 'FILE', RollingFileAppender, {
    file = logfile
    rollingPolicy TimeBasedRollingPolicy, {
        fileNamePattern = "${logfile}-%d{yyyyMMdd}.gz"
    }
    encoder PatternLayoutEncoder, {
        pattern = '%d{HH:mm:ss.SSS} %-5level --- %-50.50logger{49} : %msg%n%wex'
    }
}

////// Loggers //////

logger 'ch.qos.logback', WARN
logger 'cz.cvut.zuul.oaas', INFO
logger 'org.springframework', INFO

root INFO, ['CONSOLE', 'FILE']
