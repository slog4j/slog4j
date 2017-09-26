package org.slog4j

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class SLoggerSpec extends Specification {

    static final TimeProvider BROKEN_CLOCK      = TimeProviders.brokenClock(1506397907801L)
    static final String       BROKEN_CLOCK_TIME = '2017-09-26T00:51:47.801-0300'

    @Shared
    TextFormatter textFormatter = new TextFormatter()

    static class Person {
        String firstName
        String lastName
        int    age
    }

    def setupSpec() {
        textFormatter.registerObjectConverter(Person, { Person p ->
            [firstName: p.firstName, lastName: p.lastName, age: p.age].entrySet()
        })
    }

    def 'check all SLogger methods'() {

        given:
            def person = new Person(firstName: 'John', lastName: 'Smith', age: 40)
            def appender = Mock(Appender)
            def rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
            rootLogger.addAppender(appender)
            rootLogger.setLevel(Level.TRACE)
            def slog = SLoggerFactory.getLogger(rootLogger).withFormatter(textFormatter).withTimeProvider(BROKEN_CLOCK)
            def spanId = 299792458L

        when:
            // level=ERROR, untraced events
            slog.error('errorNakedEvent')
            slog.error('errorEventWithAnObject', person)
            slog.error('errorEventWithKeySimpleValue', 'aKey', 'aValue')
            slog.error('errorEventWithKeyComplexValue', 'person', person)
            slog.error('errorEventWithFields', person, 'aKey', 'aValue')
            // level=ERROR, traced events
            slog.error(spanId, 'errorNakedEvent')
            slog.error(spanId, 'errorEventWithAnObject', person)
            slog.error(spanId, 'errorEventWithKeySimpleValue', 'aKey', 'aValue')
            slog.error(spanId, 'errorEventWithKeyComplexValue', 'person', person)
            slog.error(spanId, 'errorEventWithFields', person, 'aKey', 'aValue')
            // level=WARN, untraced events
            slog.warn('errorNakedEvent')
            slog.warn('errorEventWithAnObject', person)
            slog.warn('errorEventWithKeySimpleValue', 'aKey', 'aValue')
            slog.warn('errorEventWithKeyComplexValue', 'person', person)
            slog.warn('errorEventWithFields', person, 'aKey', 'aValue')
            // level=WARN, traced events
            slog.warn(spanId, 'errorNakedEvent')
            slog.warn(spanId, 'errorEventWithAnObject', person)
            slog.warn(spanId, 'errorEventWithKeySimpleValue', 'aKey', 'aValue')
            slog.warn(spanId, 'errorEventWithKeyComplexValue', 'person', person)
            slog.warn(spanId, 'errorEventWithFields', person, 'aKey', 'aValue')
            // level=INFO, untraced events
            slog.info('errorNakedEvent')
            slog.info('errorEventWithAnObject', person)
            slog.info('errorEventWithKeySimpleValue', 'aKey', 'aValue')
            slog.info('errorEventWithKeyComplexValue', 'person', person)
            slog.info('errorEventWithFields', person, 'aKey', 'aValue')
            // level=INFO, traced events
            slog.info(spanId, 'errorNakedEvent')
            slog.info(spanId, 'errorEventWithAnObject', person)
            slog.info(spanId, 'errorEventWithKeySimpleValue', 'aKey', 'aValue')
            slog.info(spanId, 'errorEventWithKeyComplexValue', 'person', person)
            slog.info(spanId, 'errorEventWithFields', person, 'aKey', 'aValue')
            // level=DEBUG, untraced events
            slog.debug('errorNakedEvent')
            slog.debug('errorEventWithAnObject', person)
            slog.debug('errorEventWithKeySimpleValue', 'aKey', 'aValue')
            slog.debug('errorEventWithKeyComplexValue', 'person', person)
            slog.debug('errorEventWithFields', person, 'aKey', 'aValue')
            // level=DEBUG, traced events
            slog.debug(spanId, 'errorNakedEvent')
            slog.debug(spanId, 'errorEventWithAnObject', person)
            slog.debug(spanId, 'errorEventWithKeySimpleValue', 'aKey', 'aValue')
            slog.debug(spanId, 'errorEventWithKeyComplexValue', 'person', person)
            slog.debug(spanId, 'errorEventWithFields', person, 'aKey', 'aValue')
            // level=TRACE, untraced events
            slog.trace('errorNakedEvent')
            slog.trace('errorEventWithAnObject', person)
            slog.trace('errorEventWithKeySimpleValue', 'aKey', 'aValue')
            slog.trace('errorEventWithKeyComplexValue', 'person', person)
            slog.trace('errorEventWithFields', person, 'aKey', 'aValue')
            // level=TRACE, traced events
            slog.trace(spanId, 'errorNakedEvent')
            slog.trace(spanId, 'errorEventWithAnObject', person)
            slog.trace(spanId, 'errorEventWithKeySimpleValue', 'aKey', 'aValue')
            slog.trace(spanId, 'errorEventWithKeyComplexValue', 'person', person)
            slog.trace(spanId, 'errorEventWithFields', person, 'aKey', 'aValue')

        then:
            // level=ERROR, untraced events
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.ERROR
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=ERROR evt=errorNakedEvent"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.ERROR
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=ERROR evt=errorEventWithAnObject firstName=John lastName=Smith age=40"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.ERROR
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=ERROR evt=errorEventWithKeySimpleValue aKey=aValue"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.ERROR
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=ERROR evt=errorEventWithKeyComplexValue person=[firstName=John lastName=Smith age=40]"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.ERROR
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=ERROR evt=errorEventWithFields firstName=John lastName=Smith age=40 aKey=aValue"
            }

            // level=ERROR, traced events
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.ERROR
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=ERROR evt=errorNakedEvent spanId=0000000011de784a"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.ERROR
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=ERROR evt=errorEventWithAnObject spanId=0000000011de784a firstName=John lastName=Smith age=40"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.ERROR
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=ERROR evt=errorEventWithKeySimpleValue spanId=0000000011de784a aKey=aValue"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.ERROR
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=ERROR evt=errorEventWithKeyComplexValue spanId=0000000011de784a person=[firstName=John lastName=Smith age=40]"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.ERROR
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=ERROR evt=errorEventWithFields spanId=0000000011de784a firstName=John lastName=Smith age=40 aKey=aValue"
            }

            // level=WARN, untraced events
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.WARN
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=WARN evt=errorNakedEvent"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.WARN
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=WARN evt=errorEventWithAnObject firstName=John lastName=Smith age=40"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.WARN
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=WARN evt=errorEventWithKeySimpleValue aKey=aValue"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.WARN
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=WARN evt=errorEventWithKeyComplexValue person=[firstName=John lastName=Smith age=40]"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.WARN
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=WARN evt=errorEventWithFields firstName=John lastName=Smith age=40 aKey=aValue"
            }

            // level=WARN, traced events
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.WARN
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=WARN evt=errorNakedEvent spanId=0000000011de784a"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.WARN
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=WARN evt=errorEventWithAnObject spanId=0000000011de784a firstName=John lastName=Smith age=40"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.WARN
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=WARN evt=errorEventWithKeySimpleValue spanId=0000000011de784a aKey=aValue"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.WARN
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=WARN evt=errorEventWithKeyComplexValue spanId=0000000011de784a person=[firstName=John lastName=Smith age=40]"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.WARN
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=WARN evt=errorEventWithFields spanId=0000000011de784a firstName=John lastName=Smith age=40 aKey=aValue"
            }

            // level=INFO, untraced events
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.INFO
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=INFO evt=errorNakedEvent"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.INFO
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=INFO evt=errorEventWithAnObject firstName=John lastName=Smith age=40"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.INFO
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=INFO evt=errorEventWithKeySimpleValue aKey=aValue"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.INFO
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=INFO evt=errorEventWithKeyComplexValue person=[firstName=John lastName=Smith age=40]"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.INFO
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=INFO evt=errorEventWithFields firstName=John lastName=Smith age=40 aKey=aValue"
            }

            // level=INFO, traced events
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.INFO
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=INFO evt=errorNakedEvent spanId=0000000011de784a"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.INFO
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=INFO evt=errorEventWithAnObject spanId=0000000011de784a firstName=John lastName=Smith age=40"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.INFO
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=INFO evt=errorEventWithKeySimpleValue spanId=0000000011de784a aKey=aValue"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.INFO
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=INFO evt=errorEventWithKeyComplexValue spanId=0000000011de784a person=[firstName=John lastName=Smith age=40]"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.INFO
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=INFO evt=errorEventWithFields spanId=0000000011de784a firstName=John lastName=Smith age=40 aKey=aValue"
            }

            // level=DEBUG, untraced events
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.DEBUG
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=DEBUG evt=errorNakedEvent"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.DEBUG
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=DEBUG evt=errorEventWithAnObject firstName=John lastName=Smith age=40"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.DEBUG
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=DEBUG evt=errorEventWithKeySimpleValue aKey=aValue"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.DEBUG
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=DEBUG evt=errorEventWithKeyComplexValue person=[firstName=John lastName=Smith age=40]"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.DEBUG
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=DEBUG evt=errorEventWithFields firstName=John lastName=Smith age=40 aKey=aValue"
            }

            // level=DEBUG, traced events
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.DEBUG
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=DEBUG evt=errorNakedEvent spanId=0000000011de784a"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.DEBUG
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=DEBUG evt=errorEventWithAnObject spanId=0000000011de784a firstName=John lastName=Smith age=40"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.DEBUG
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=DEBUG evt=errorEventWithKeySimpleValue spanId=0000000011de784a aKey=aValue"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.DEBUG
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=DEBUG evt=errorEventWithKeyComplexValue spanId=0000000011de784a person=[firstName=John lastName=Smith age=40]"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.DEBUG
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=DEBUG evt=errorEventWithFields spanId=0000000011de784a firstName=John lastName=Smith age=40 aKey=aValue"
            }

            // level=TRACE, untraced events
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.TRACE
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=TRACE evt=errorNakedEvent"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.TRACE
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=TRACE evt=errorEventWithAnObject firstName=John lastName=Smith age=40"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.TRACE
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=TRACE evt=errorEventWithKeySimpleValue aKey=aValue"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.TRACE
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=TRACE evt=errorEventWithKeyComplexValue person=[firstName=John lastName=Smith age=40]"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.TRACE
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=TRACE evt=errorEventWithFields firstName=John lastName=Smith age=40 aKey=aValue"
            }

            // level=TRACE, traced events
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.TRACE
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=TRACE evt=errorNakedEvent spanId=0000000011de784a"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.TRACE
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=TRACE evt=errorEventWithAnObject spanId=0000000011de784a firstName=John lastName=Smith age=40"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.TRACE
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=TRACE evt=errorEventWithKeySimpleValue spanId=0000000011de784a aKey=aValue"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.TRACE
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=TRACE evt=errorEventWithKeyComplexValue spanId=0000000011de784a person=[firstName=John lastName=Smith age=40]"
            }
            1 * appender.doAppend(_) >> { LoggingEvent e ->
                assert e.level == Level.TRACE
                assert e.formattedMessage == "time=$BROKEN_CLOCK_TIME level=TRACE evt=errorEventWithFields spanId=0000000011de784a firstName=John lastName=Smith age=40 aKey=aValue"
            }
    }
}
