////// Logback configuration //////

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.*
import ch.qos.logback.core.rolling.*
import ch.qos.logback.core.status.OnErrorConsoleStatusListener
import org.springframework.boot.logging.logback.*

import static ch.qos.logback.classic.Level.*


context.name = 'zuul-oaas'

// Print logback's status messages on stderr.
statusListener OnErrorConsoleStatusListener

// Export MBeans via JMX for monitoring.
jmxConfigurator()

// Conversion rule from Spring Boot.
conversionRule 'wex', WhitespaceThrowableProxyConverter

// Define convenient alias.
def env = System.&getProperty

// Variables LOG_FILE and LOG_PATH are set by Spring Boot.
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
