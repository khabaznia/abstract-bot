import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.encoder.PatternLayoutEncoder

def LOGS = "./logs"
appender("Console", ConsoleAppender) {
    layout(PatternLayout) {
        pattern = "%boldWhite(%d{ISO8601}) %highlight(%-5level) [%(%t)] %yellow(%C{1.}): %msg%n%throwable"
    }
}

appender("RollingFile", RollingFileAppender) {
    file = "${LOGS}/spring-boot-logger.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d %p %C{1.} [%t] %m%n"
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${LOGS}/archived/spring-boot-logger-%d{yyyy-MM-dd}.%i.log"
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = "300MB"
        }
    }
}
root(INFO, ["RollingFile", "Console"])
logger("com.khabaznia", TRACE, ["RollingFile"])
