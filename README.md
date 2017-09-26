# SLog4j

Structured Event Logging for Java.

Allows you to stop logging prose and to start logging structured events.

[![License](https://img.shields.io/github/license/eliezio/slog4j.svg)](https://opensource.org/licenses/MIT)
[![Build](https://travis-ci.org/eliezio/slog4j.svg?branch=master)](https://travis-ci.org/eliezio/slog4j)
[![Coverage](https://coveralls.io/repos/github/eliezio/slog4j/badge.svg?branch=master)](https://coveralls.io/github/eliezio/slog4j?branch=master)
[![Download](https://api.bintray.com/packages/eliezio/maven/slog4j/images/download.svg)](https://bintray.com/eliezio/maven/slog4j/_latestVersion)
[![Chat](https://img.shields.io/gitter/room/eliezio/slog4j.svg)](https://gitter.im/eliezio/slog4j)

## Adding to your project

The library is published to the Bintray JCenter Maven repository.

### Gradle

```gradle
    compile 'org.slog4j:slog4j:0.1.0'
```

### Maven

```xml
    <dependency>
      <groupId>org.slog4j</groupId>
      <artifactId>slog4j</artifactId>
      <version>0.1.0</version>
    </dependency>
```

# Overview

## Why use structured events

A typical log messages looks like this

    Processed 23 flight records for flight UA1234 for airline United

This is human readable, but very difficult to parse by code to extract semantics.

Instead, we can generate a message like this

    evt='Processed flight records' recordCount=23 airlineCode=UA flightNumber=1234 airlineName=United

This is very easy to parse, the message itself is just a plain description and all the context information is
passed as separate key/value pairs.

When this type of log entry is forwarded to a log aggregation service (such as Splunk, Logstash, etc) it is trivial to
parse it and extract context information from it.
Thus, it is very easy to perform log analytics, which are critical to many open applications (especially multi-tenant
cloud applications).

## What is a structured event

- A message only spans a single line;
- Are formatted as a sequence of attributes `key=value`;
- Every message starts with a zoned timestamp with at least milliseconds precision;
- Always include an attribute to identify the event (by default labeled by `evt`);
- Whenever possible an `spanId` should be included to correlate messages.

# Usage

SLog4j is implemented itself on top of the SLF4J API. Therefore any application that already uses SLF4J can
start using it immediately as it requires no other changes to existing logging configuration.

Instead of the standard SLF4J Logger, you must instantiate the SLog4j Logger:

```java
    private SLogger slog = SLoggerFactory.getLogger(MyClass.class);
```

The **SLogger** interface is very simple and offers these basic methods:

```java
    public interface SLogger {

        // level=ERROR, traced events
        void error(long spanId, String eventId);
        void error(long spanId, String eventId, Object obj);
        void error(long spanId, String eventId, String key, Object value);
        void error(long spanId, String eventId, Object... fields);

        // level=ERROR, untraced events
        void error(String eventId);
        void error(String eventId, Object obj);
        void error(String eventId, String key, Object value);
        void error(String eventId, Object... fields);

        // same for remaining WARN, INFO, DEBUG, and TRACE levels
        // ...
    }
```

## Automatic convertions

Depending on the context, object can be handled as simple or complex.

## Logging key value pairs

Just pass in key/value pairs as parameters (all keys **must** be String, values can be anything convertible to String
through Joda-Convert), e.g.

```java
    slog.info("start",
                "user", securityContext.getPrincipal().getName(),
                "tenantId",securityContext.getTenantId());
```

which would generate a log message like:

    evt=start user=johndoe@gmail.com tenantId=SOME_TENANT_ID

## Enforcing custom logging format per object

## Logging exceptions

## License

Copyright (c) 2017 Eliezio Oliveira. See the LICENSE file for license rights and limitations (MIT).

