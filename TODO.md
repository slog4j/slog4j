## Design

- [ ] How to log stacktraces? Use JSON? Is there any alternative format to the standard multi-line format?
- [ ] Support aggregators other than LogStash
- [ ] Option to include timestamp/level on formatted message

## Implementation

- [ ] Clean-up every key
- [ ] Check appender?

## Documentation

- [ ] Document API
- [ ] cites TechRadar / J. Turnbull
- [ ] spanId/traceId recommendations
- [ ] More conventional examples (tests included)
- [ ] Hints on how to keep the `spanId` context-wide available (common cases: Spring Web);
- [ ] How to use with Spring Sleuth;

## Project

- [x] Configure Travis-CI
- [ ] Configure publishing to jcenter
- [ ] Use YourKit to profile
