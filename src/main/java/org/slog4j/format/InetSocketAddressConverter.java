package org.slog4j.format;

import java.net.InetSocketAddress;

public class InetSocketAddressConverter implements TypedToStringConverter<InetSocketAddress> {

    @Override
    public Class<?> getEffectiveType() {
        return InetSocketAddress.class;
    }

    @Override
    public String convertToString(InetSocketAddress value) {
        String text = value.toString();
        int sep;
        if ((sep = text.indexOf('/')) >= 0) {
            return text.substring(sep + 1);
        }
        return text;
    }
}
