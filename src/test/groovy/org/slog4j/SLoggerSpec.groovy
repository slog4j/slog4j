package org.slog4j

import org.slf4j.Logger
import org.slog4j.format.FormatterFactory
import org.slog4j.format.PureTextFormatter
import org.slog4j.format.ToPropertiesConverter
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.text.SimpleDateFormat
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class SLoggerSpec extends Specification {

    static final long             BROKEN_INSTANT    = 1506397907801L
    static final Clock            BROKEN_CLOCK      = Clock.fixed(Instant.ofEpochMilli(BROKEN_INSTANT), ZoneOffset.UTC)
    static final SimpleDateFormat SDF               = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    static final String           BROKEN_CLOCK_TIME = SDF.format(BROKEN_INSTANT)

    @Shared
    def person = new Person(firstName: 'John', lastName: 'Smith', age: 40)

    static class Person {
        String firstName
        String lastName
        int    age
    }

    static class PersonConverter implements ToPropertiesConverter<Person> {
        @Override
        Iterable convert(Person p) {
            return [firstName: p.firstName, lastName: p.lastName, age: p.age].entrySet()
        }
    }

    def 'logger with default configuration should format pre-configured classes'() {
        given:
            def log = Mock(Logger)
            log.isInfoEnabled() >> true
            def formatter = FormatterFactory.getInstance()
            def slog = SLoggerFactory.getLogger(log, formatter)
            def spanId = 0x19d46be5a54ab65aL

        when:
            slog.info(spanId,"EVENT_WITH_KEY_SIMPLE_VALUE", 'person', person, 'aKey', 'aValue')

        then:
            1 * log.info("evt=EVENT_WITH_KEY_SIMPLE_VALUE spanId=19d46be5a54ab65a person=[firstName=John lastName=Smith age=40] aKey=aValue")
    }

    @Unroll
    def 'check all SLogger methods for level=#level'() {

        given:
            def ucLevel = level.toUpperCase()
            def log = Mock(Logger)
            log."is${level.capitalize()}Enabled"() >> true
            def formatter = new PureTextFormatter(BROKEN_CLOCK)
            formatter.registerToPropertiesConverter(Person, new PersonConverter())
            def slog = SLoggerFactory.getLogger(log, formatter)
            def spanId = 299792458L

        when:
            // untraced events
            slog."$level"("${level}NakedEvent")
            slog."$level"("${level}EventWithAnObject", person)
            slog."$level"("${level}EventWithKeySimpleValue", 'aKey', 'aValue')
            slog."$level"("${level}EventWithKeyComplexValue", 'person', person)
            slog."$level"("${level}EventWithProps", person, 'aKey', 'aValue')
            // traced events
            slog."$level"(spanId, "${level}NakedEvent")
            slog."$level"(spanId, "${level}EventWithAnObject", person)
            slog."$level"(spanId, "${level}EventWithKeySimpleValue", 'aKey', 'aValue')
            slog."$level"(spanId, "${level}EventWithKeyComplexValue", 'person', person)
            slog."$level"(spanId, "${level}EventWithProps", person, 'aKey', 'aValue')

        then:
            // untraced events
            1 * log."$level"("time=$BROKEN_CLOCK_TIME level=$ucLevel evt=${level}NakedEvent")
            1 * log."$level"("time=$BROKEN_CLOCK_TIME level=$ucLevel evt=${level}EventWithAnObject firstName=John lastName=Smith age=40")
            1 * log."$level"("time=$BROKEN_CLOCK_TIME level=$ucLevel evt=${level}EventWithKeySimpleValue aKey=aValue")
            1 * log."$level"("time=$BROKEN_CLOCK_TIME level=$ucLevel evt=${level}EventWithKeyComplexValue person=[firstName=John lastName=Smith age=40]")
            1 * log."$level"("time=$BROKEN_CLOCK_TIME level=$ucLevel evt=${level}EventWithProps firstName=John lastName=Smith age=40 aKey=aValue")

            // traced events
            1 * log."$level"("time=$BROKEN_CLOCK_TIME level=$ucLevel evt=${level}NakedEvent spanId=0000000011de784a")
            1 * log."$level"("time=$BROKEN_CLOCK_TIME level=$ucLevel evt=${level}EventWithAnObject spanId=0000000011de784a firstName=John lastName=Smith age=40")
            1 * log."$level"("time=$BROKEN_CLOCK_TIME level=$ucLevel evt=${level}EventWithKeySimpleValue spanId=0000000011de784a aKey=aValue")
            1 * log."$level"("time=$BROKEN_CLOCK_TIME level=$ucLevel evt=${level}EventWithKeyComplexValue spanId=0000000011de784a person=[firstName=John lastName=Smith age=40]")
            1 * log."$level"("time=$BROKEN_CLOCK_TIME level=$ucLevel evt=${level}EventWithProps spanId=0000000011de784a firstName=John lastName=Smith age=40 aKey=aValue")

        where:
            level << ['error', 'warn', 'info', 'debug', 'trace']
    }

    def 'standard log not-convertible throwable'() {
        given:
            def ucLevel = level.toUpperCase()
            def log = Mock(Logger)
            log."is${level.capitalize()}Enabled"() >> true
            def formatter = new PureTextFormatter(BROKEN_CLOCK)
            def slog = SLoggerFactory.getLogger(log, formatter)
            def re = new RuntimeException("an ordinary unchecked exception")

        when:
            slog."$level"('user_creation_failed', 'user', 'james@example.com', re)

        then:
            1 * log."$level"("time=$BROKEN_CLOCK_TIME level=$ucLevel evt=user_creation_failed user=james@example.com", re)

        where:
            level << ['error', 'warn', 'info', 'debug', 'trace']
    }
}
