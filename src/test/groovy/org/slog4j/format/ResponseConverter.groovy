package org.slog4j.format

import static org.slog4j.format.TextFormatterSpec.*

class ResponseConverter implements ToPropertiesConverter<Response> {

    @Override
    Class<?> getEffectiveType() {
        Response
    }

    @Override
    Iterable<Map.Entry<String, Object>> convert(Response resp) {
        [clntNii: new ShortId(resp.clntNii), servNii: new ShortId(resp.servNii), seq: resp.seq, bodyLen: resp.bodyLen].entrySet()
    }
}
