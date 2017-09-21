package io.eliez.slog4j.structmsg

import groovy.transform.TupleConstructor
import spock.lang.Specification
import spock.lang.Unroll

class FormatterSpec extends Specification {

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

    def setupSpec() {
        Formatter.registerValueConverter(ShortId, { ShortId sid -> String.format('0x%04x', sid.id) })
        Formatter.registerObjectConverter(Response, { Response resp ->
            [clntNii: new ShortId(resp.clntNii), servNii: new ShortId(resp.servNii), seq: resp.seq, bodyLen: resp.bodyLen].entrySet()
        })
    }

    @Unroll
    def 'formatações simples: #description'() {
        when:
            def msg = Formatter.format(fields as Object[])

        then:
            msg == expectedMessage

        where:
            description         | fields                                                                                                            || expectedMessage
            'empty fields'      | []                                                                                                                || ''
            'response object'   | [new Response(clntNii: 0x83d9, servNii: 0x0955, seq: 0, bodyLen: 1453)]                                           || 'clntNii=0x83d9 servNii=0x0955 seq=0 bodyLen=1453'
            'kv response kv'    | ['obj', 'server4', new Response(clntNii: 0x83d9, servNii: 0x0955, seq: 0, bodyLen: 1453), 'len', 560]             || 'obj=server4 clntNii=0x83d9 servNii=0x0955 seq=0 bodyLen=1453 len=560'
            'InetSocketAddress' | ['from', new InetSocketAddress('localhost', 9000)]                                                                || 'from=127.0.0.1:9000'
            'kv pairs'          | ['obj', 'server4', 'evt', 'dataSent', 'traceId', 'TID 2', 'len', 560]                                             || "obj=server4 evt=dataSent traceId='TID 2' len=560"
            'value is map'      | ['parameters', [readerFW: '2.100', readerModel: 'RC700', operation: 'AA', readerSN: 2147490583, csn: 1666649158]] || 'parameters=[readerFW=2.100 readerModel=RC700 operation=AA readerSN=2147490583 csn=1666649158]'
    }


    @Unroll
    def 'formatações simples - eventId=#eventId, spanId=#spanId'() {
        when:
            def msg = Formatter.formatTracedEvent(eventId, spanId, fields as Object[])

        then:
            msg == expectedMessage

        where:
            eventId     | spanId              | fields                                                                                                                                                              || expectedMessage
            'respSent'  | 0x1355932dc9fb94cfL | ['response', new Response(clntNii: 0x83d9, servNii: 0x0955, seq: 0, bodyLen: 1453)]                                                                                 || 'evt=respSent spanId=1355932dc9fb94cf response=[clntNii=0x83d9 servNii=0x0955 seq=0 bodyLen=1453]'
            'msgRecv'   | 0x1b2796bac997c13eL | ['from', new InetSocketAddress('localhost', 9000)]                                                                                                                  || 'evt=msgRecv spanId=1b2796bac997c13e from=127.0.0.1:9000'
            'newClient' | 0xfc819b4efe1fc078L | ['port', 4433, 'from', new InetSocketAddress('10.34.21.34', 49694), 'protocol', 'tls1.1', 'cipherSuite', CipherSuite.SSL_RSA_WITH_RC4_128_SHA, 'resumption', false] || 'evt=newClient spanId=fc819b4efe1fc078 port=4433 from=10.34.21.34:49694 protocol=tls1.1 cipherSuite=SSL_RSA_WITH_RC4_128_SHA resumption=false'
    }
}
