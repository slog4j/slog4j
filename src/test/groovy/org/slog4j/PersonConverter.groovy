package org.slog4j

import org.slog4j.format.ToPropertiesConverter

class PersonConverter implements ToPropertiesConverter<SLoggerSpec.Person> {
    @Override
    Class<?> getEffectiveType() {
        return SLoggerSpec.Person.class
    }

    @Override
    Iterable convert(SLoggerSpec.Person p) {
        return [firstName: p.firstName, lastName: p.lastName, age: p.age].entrySet()
    }
}
