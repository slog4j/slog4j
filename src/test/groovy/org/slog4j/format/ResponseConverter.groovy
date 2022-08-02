package org.slog4j.format

import static org.slog4j.format.TextFormatterSpec.*

class ResponseConverter implements ToPropertiesConverter {

    @Override
    Class<?> getEffectiveType() {
        Response
    }

    @Override
    Iterable<Map.Entry<String, Object>> convert(Object resp) {
        return (resp as Response).with {
            [clntNii: new ShortId(it.clntNii), servNii: new ShortId(it.servNii), seq: it.seq, bodyLen: it.bodyLen].entrySet()
        }
    }
}
