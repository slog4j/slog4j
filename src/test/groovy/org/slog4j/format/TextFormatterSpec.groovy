package org.slog4j.format

import groovy.transform.Immutable
import groovy.transform.TupleConstructor
import org.joda.convert.ToStringConverter
import org.slf4j.event.Level
import org.slog4j.types.LongId
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class TextFormatterSpec extends Specification {

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
        int seq
        int bodyLen
    }

    static class Person {
        String firstName
        String lastName
        int age
    }


    @Shared
    def person = new Person(firstName: 'John', lastName: 'Smith', age: 40)

    @Unroll
    def 'event: #eventId // #description'() {
        given:
            def textFormatter = new TextFormatter()
            textFormatter.eventIdLabel('event')

        when: 'text-formatting event with INFO level'
            def msg = textFormatter.format(Level.INFO, eventId, props as Object[]).getString()

        then: 'result must match expected text-format'
            msg == expectedMessage

        where:
            description                | eventId          | props                                                                                                                                                               || expectedMessage
            'no properties'            | 'restart'        | []                                                                                                                                                                  || 'event=restart'
            'a simple byte array'      | 'TOKEN_RECEIVED' | ['token', [1, 2, 3, 4, 5, 6, 7, 8] as byte[]]                                                                                                                       || "event=TOKEN_RECEIVED token=AQIDBAUGBwg="
            'response object'          | 'respSent'       | [new Response(clntNii: 0x83d9, servNii: 0x0955, seq: 0, bodyLen: 1453)]                                                                                             || 'event=respSent clntNii=0x83d9 servNii=0x0955 seq=0 bodyLen=1453'
            'kv response kv'           | 'respSent'       | ['obj', 'server4', new Response(clntNii: 0x83d9, servNii: 0x0955, seq: 0, bodyLen: 1453), 'len', 560]                                                               || 'event=respSent obj=server4 clntNii=0x83d9 servNii=0x0955 seq=0 bodyLen=1453 len=560'
            'kv pairs'                 | 'dataSent'       | ['obj', 'server4', 'traceId', 'TID 2', 'len', 560]                                                                                                                  || "event=dataSent obj=server4 traceId='TID 2' len=560"
            'ignore null, LongId'      | 'dataSent'       | ['obj', 'server4', null, 'traceId', new LongId(0x69e3d3a6db5b8241L), 'len', 560]                                                                                    || "event=dataSent obj=server4 traceId=69e3d3a6db5b8241 len=560"
            'value w/ special chars'   | 'procError'      | ['errno', 1001, 'message', "['the' error]"]                                                                                                                         || "event=procError errno=1001 message='[\\'the\\' error]'"
            'value is map'             | 'requestRecv'    | ['parameters', [readerFW: '2.100', readerModel: 'RC700', operation: 'AA', readerSN: 2147490583, csn: 1666649158]]                                                   || 'event=requestRecv parameters=[readerFW=2.100 readerModel=RC700 operation=AA readerSN=2147490583 csn=1666649158]'
            'InetSocketAddress'        | 'msgRecv'        | ['from', new InetSocketAddress('localhost', 9000)]                                                                                                                  || 'event=msgRecv from=127.0.0.1:9000'
            'InetSocketAddress + Enum' | 'newClient'      | ['port', 4433, 'from', new InetSocketAddress('10.34.21.34', 49694), 'protocol', 'tls1.1', 'cipherSuite', CipherSuite.SSL_RSA_WITH_RC4_128_SHA, 'resumption', false] || 'event=newClient port=4433 from=10.34.21.34:49694 protocol=tls1.1 cipherSuite=SSL_RSA_WITH_RC4_128_SHA resumption=false'
    }

    def 'common properties printed in value-only format'() {
        given:
            def formatter = new TextFormatter()

        when:
            def msg = formatter.format(Level.TRACE, 'start', 'aName', 'aValue').getString()

        then:
            msg == 'evt=start aName=aValue'
    }


    def 'property with null value'() {
        given:
            def formatter = new TextFormatter()

        when:
            def msg = formatter.format(Level.INFO, 'start', 'aName', null).getString()

        then:
            msg == "evt=start aName=_NULL_"
    }


    def 'property with non-convertible value'() {
        given:
            def formatter = new TextFormatter()

        when:
            def msg = formatter.format(Level.INFO, 'start', 'aName', person).getString()

        then:
            msg == "evt=start aName=[org.slog4j.format.TextFormatterSpec\$Person#_NO_CONVERTER_]"
    }


    def 'non-convertible object'() {
        given:
            def formatter = new TextFormatter()

        when:
            def msg = formatter.format(Level.INFO, 'start', person).getString()

        then:
            msg == "evt=start org.slog4j.format.TextFormatterSpec\$Person#_NO_CONVERTER_"
    }

    @Immutable
    static class GeoLocation {
        double latitude
        double longitude
    }

    def 'example borrowed from graylog main page (https://www.graylog.org)'() {
        given:
            def formatter = new TextFormatter()
            formatter.registerToStringConverter(Inet4Address, { Inet4Address addr ->
                addr.toString().substring(1)
            } as ToStringConverter)
            formatter.registerToStringConverter(GeoLocation, { GeoLocation gl ->
                String.format(Locale.ENGLISH, "%.4f,%.4f", gl.latitude, gl.longitude)
            } as ToStringConverter)
            def srcAddress = InetAddress.getByAddress([10, 10, 15, 250] as byte[])
            def dstAddress = InetAddress.getByAddress([54, 225, 214, 228] as byte[])
            def dstGeoLocation = new GeoLocation(39.0481f, -77.4728f)

        when:
            def msg = formatter.format(Level.INFO, 'accept',
                'message', 'ACCEPT TCP 10.10.15.250:38028 -> 54.225.214.228:443',
                'log_type', 'netflow',
                'protocol', 'TCP',
                'src_addr', srcAddress,
                'src_port', 38028,
                'src_addr_threat_indicated', false,
                'src_addr_is_internal', true,
                'dst_addr', dstAddress,
                'dst_port', 443,
                'dst_addr_threat_indicated', false,
                'dst_addr_is_internal', false,
                'dst_addr_whois_country_code', 'US',
                'dst_addr_geolocation', dstGeoLocation,
                'dst_addr_whois_organization', 'Amazon Technologies Inc.',
                'mac_address', '1C:C1:DE:26:9D:C4'
            ).getString()

        then:
            msg == expectedMessage

        where:
            expectedMessage << ["evt=accept " +
                                    "message='ACCEPT TCP 10.10.15.250:38028 -> 54.225.214.228:443' log_type=netflow " +
                                    "protocol=TCP src_addr=10.10.15.250 src_port=38028 " +
                                    "src_addr_threat_indicated=false src_addr_is_internal=true dst_addr=54.225.214.228 " +
                                    "dst_port=443 dst_addr_threat_indicated=false dst_addr_is_internal=false " +
                                    "dst_addr_whois_country_code=US dst_addr_geolocation=39.0481,-77.4728 " +
                                    "dst_addr_whois_organization='Amazon Technologies Inc.' mac_address=1C:C1:DE:26:9D:C4"]
    }
}
