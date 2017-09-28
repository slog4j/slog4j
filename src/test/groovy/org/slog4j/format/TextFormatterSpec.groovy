package org.slog4j.format

import groovy.transform.TupleConstructor
import org.slf4j.event.Level
import org.slog4j.time.TimeProvider
import org.slog4j.time.TimeProviders
import org.slog4j.types.LongId
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class TextFormatterSpec extends Specification {

    static final TimeProvider BROKEN_CLOCK      = TimeProviders.brokenClock(1506397907801L)
    static final String       BROKEN_CLOCK_TIME = '2017-09-26T00:51:47.801-0300'

    enum CipherSuite {
        SSL_RSA_WITH_RC4_128_SHA
    }

    @TupleConstructor
    static class ShortId {
        short id
    }

    static class Response {
        short clntNii
        short servNii
        int   seq
        int   bodyLen
    }

    static class Person {
        String firstName
        String lastName
        int    age
    }

    @Shared
    TextFormatter textFormatter = new TextFormatter()
    @Shared
    def           person        = new Person(firstName: 'John', lastName: 'Smith', age: 40)

    def setupSpec() {
        textFormatter.commonPropertiesFormat(TextFormatter.PropertyFormat.OMIT).eventIdLabel('event')
        textFormatter.registerToStringConverter(ShortId, { ShortId sid -> String.format('0x%04x', sid.id) })
        textFormatter.registerToPropertiesConverter(Response, { Response resp ->
            [clntNii: new ShortId(resp.clntNii), servNii: new ShortId(resp.servNii), seq: resp.seq, bodyLen: resp.bodyLen].entrySet()
        })
    }

    @Unroll
    def 'untraced event: #eventId // #description'() {
        when:
            def msg = textFormatter.format(TimeProviders.system(), Level.INFO, eventId, props as Object[])

        then:
            msg == expectedMessage

        where:
            description              | eventId       | props                                                                                                             || expectedMessage
            'no properties'          | 'restart'     | []                                                                                                                || 'event=restart'
            'response object'        | 'respSent'    | [new Response(clntNii: 0x83d9, servNii: 0x0955, seq: 0, bodyLen: 1453)]                                           || 'event=respSent clntNii=0x83d9 servNii=0x0955 seq=0 bodyLen=1453'
            'kv response kv'         | 'respSent'    | ['obj', 'server4', new Response(clntNii: 0x83d9, servNii: 0x0955, seq: 0, bodyLen: 1453), 'len', 560]             || 'event=respSent obj=server4 clntNii=0x83d9 servNii=0x0955 seq=0 bodyLen=1453 len=560'
            'InetSocketAddress'      | 'connect'     | ['from', new InetSocketAddress('localhost', 9000)]                                                                || 'event=connect from=127.0.0.1:9000'
            'kv pairs'               | 'dataSent'    | ['obj', 'server4', 'traceId', 'TID 2', 'len', 560]                                                                || "event=dataSent obj=server4 traceId='TID 2' len=560"
            'ignore null, LongId'    | 'dataSent'    | ['obj', 'server4', null, 'traceId', new LongId(0x69e3d3a6db5b8241L), 'len', 560]                                  || "event=dataSent obj=server4 traceId=69e3d3a6db5b8241 len=560"
            'value w/ special chars' | 'procError'   | ['errno', 1001, 'message', "['the' error]"]                                                                       || "event=procError errno=1001 message='\\[\\'the\\' error\\]'"
            'value is map'           | 'requestRecv' | ['parameters', [readerFW: '2.100', readerModel: 'RC700', operation: 'AA', readerSN: 2147490583, csn: 1666649158]] || 'event=requestRecv parameters=[readerFW=2.100 readerModel=RC700 operation=AA readerSN=2147490583 csn=1666649158]'
    }


    @Unroll
    def 'traced event: #eventId, spanId: #spanId'() {
        when:
            def msg = textFormatter.format(TimeProviders.system(), Level.INFO, spanId, eventId, props as Object[])

        then:
            msg == expectedMessage

        where:
            eventId     | spanId              | props                                                                                                                                                               || expectedMessage
            'respSent'  | 0x1355932dc9fb94cfL | ['response', new Response(clntNii: 0x83d9, servNii: 0x0955, seq: 0, bodyLen: 1453)]                                                                                 || 'event=respSent spanId=1355932dc9fb94cf response=[clntNii=0x83d9 servNii=0x0955 seq=0 bodyLen=1453]'
            'msgRecv'   | 0x1b2796bac997c13eL | ['from', new InetSocketAddress('localhost', 9000)]                                                                                                                  || 'event=msgRecv spanId=1b2796bac997c13e from=127.0.0.1:9000'
            'newClient' | 0xfc819b4efe1fc078L | ['port', 4433, 'from', new InetSocketAddress('10.34.21.34', 49694), 'protocol', 'tls1.1', 'cipherSuite', CipherSuite.SSL_RSA_WITH_RC4_128_SHA, 'resumption', false] || 'event=newClient spanId=fc819b4efe1fc078 port=4433 from=10.34.21.34:49694 protocol=tls1.1 cipherSuite=SSL_RSA_WITH_RC4_128_SHA resumption=false'
    }


    def 'common properties printed in value-only format'() {
        given:
            def formatter = new TextFormatter().commonPropertiesFormat(TextFormatter.PropertyFormat.VALUE_ONLY)

        when:
            def msg = formatter.format(BROKEN_CLOCK, Level.TRACE, 'start', 'aKey', 'aValue')

        then:
            msg == "$BROKEN_CLOCK_TIME TRACE evt=start aKey=aValue"
    }


    def 'property with null value'() {
        given:
            def formatter = new TextFormatter()

        when:
            def msg = formatter.format(BROKEN_CLOCK, Level.INFO, 'start', 'aKey', null)

        then:
            msg == "time=$BROKEN_CLOCK_TIME level=INFO evt=start aKey=_NULL_"
    }


    def 'property with non-convertible value'() {
        given:
            def formatter = new TextFormatter()

        when:
            def msg = formatter.format(BROKEN_CLOCK, Level.INFO, 'start', 'aKey', person)

        then:
            msg == "time=$BROKEN_CLOCK_TIME level=INFO evt=start aKey=[org.slog4j.format.TextFormatterSpec\$Person#_NO_CONVERTER_]"
    }


    def 'non-convertible object'() {
        given:
            def formatter = new TextFormatter()

        when:
            def msg = formatter.format(BROKEN_CLOCK, Level.INFO, 'start', person)

        then:
            msg == "time=$BROKEN_CLOCK_TIME level=INFO evt=start org.slog4j.format.TextFormatterSpec\$Person#_NO_CONVERTER_"
    }
}
