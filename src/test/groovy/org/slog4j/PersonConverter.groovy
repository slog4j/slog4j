package org.slog4j

import org.slog4j.format.ToPropertiesConverter

class PersonConverter implements ToPropertiesConverter {
    @Override
    Class<?> getEffectiveType() {
        return SLoggerSpec.Person.class
    }

    @Override
    Iterable convert(Object p) {
        return (p as SLoggerSpec.Person).with {
            [firstName: it.firstName, lastName: it.lastName, age: it.age].entrySet()
        }
    }
}
