package com.example.carservice.base;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.carservice.logging.aop.LoggerAspectJ;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Optional;


public abstract class AbstractLoggerAspectTestLogConfig {

    protected final LogTracker logTracker = new LogTracker();

    @BeforeEach
    void initLogTracker() {
        Logger logger = (Logger) LoggerFactory.getLogger(LoggerAspectJ.class);
        logger.setLevel(Level.INFO);
        logger.addAppender(logTracker);

        logTracker.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logTracker.start();
    }

    protected static class LogTracker extends ListAppender<ILoggingEvent> {

        public Optional<String> checkMessage(Level level, String messageFragment) {
            if (CollectionUtils.isEmpty(list)) {
                return Optional.empty();
            }

            return list.stream()
                    .filter(log -> log.getLevel().equals(level))
                    .map(ILoggingEvent::getFormattedMessage)
                    .filter(message -> message.contains(messageFragment))
                    .findFirst();
        }

    }

}
