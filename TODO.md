## Design

- [x] Option to include timestamp/level on formatted message
- [x] How to log stacktraces? Use JSON? Is there any alternative format to the standard multi-line format?
- [ ] JSONFormatter
- [ ] Support aggregators other than LogStash

## Implementation

- [x] Clean-up every key

## Documentation

- [ ] Document API
- [ ] Java 1.6
- [ ] cites TechRadar / J. Turnbull
- [ ] spanId/traceId recommendations; type LongId
- [ ] Use a conventional domain for specs and examples
- [ ] Hints on how to keep the `spanId` context-wide available (common cases: Spring Web);
- [ ] How to use with Spring Sleuth;

## Project

- [x] Configure Travis-CI
- [ ] Configure publishing to jcenter
- [ ] Configure [Sonarqube](https://about.sonarcloud.io/news/2016/05/02/continuous-analysis-for-oss-projects.htm) 
- [ ] Use YourKit to profile
