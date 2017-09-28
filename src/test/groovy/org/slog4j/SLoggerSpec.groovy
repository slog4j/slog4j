package org.slog4j

import org.slf4j.Logger
import org.slog4j.format.TextFormatter
import org.slog4j.time.TimeProvider
import org.slog4j.time.TimeProviders
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class SLoggerSpec extends Specification {

    static final TimeProvider BROKEN_CLOCK      = TimeProviders.brokenClock(1506397907801L)
    static final String       BROKEN_CLOCK_TIME = '2017-09-26T00:51:47.801-0300'

    @Shared
    def textFormatter = new TextFormatter()
    @Shared
    def person        = new Person(firstName: 'John', lastName: 'Smith', age: 40)

    static class Person {
        String firstName
        String lastName
        int    age
    }

    def setupSpec() {
        textFormatter.registerToPropertiesConverter(Person, { Person p ->
            [firstName: p.firstName, lastName: p.lastName, age: p.age].entrySet()
        })
    }

    @Unroll
    def 'check all SLogger methods for level=#level'() {

        given:
            def ucLevel = level.toUpperCase()
            def log = Mock(Logger)
            log."is${level.capitalize()}Enabled"() >> true
            def slog = SLoggerFactory.getLogger(log, textFormatter, BROKEN_CLOCK)
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
}
