/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slog4j.format;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * A {@link Formatter.Result} backed by a stripped-down version of {@link org.apache.commons.lang3.text.StrBuilder}.
 */
public final class StrBuilderResult implements Formatter.Result {

    /**
     * Internal data storage.
     */
    private char[] buffer;

    /**
     * Initial size of the buffer.
     */
    private final int initialCapacity;

    /**
     * Current size of the buffer.
     */
    private int size;

    @Getter
    @Setter
    @Accessors(chain = true)
    private Object attachment;

    /**
     * Constructor that creates an empty builder the specified initial capacity.
     *
     * @param initialCapacity the initial capacity, zero or less will be converted to 32
     */
    public StrBuilderResult(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        buffer = new char[initialCapacity];
    }

    /**
     * Gets a String version of the string builder, creating a new instance
     * each time the method is called.
     * <p>
     * Note that unlike StringBuffer, the string version returned is
     * independent of the string builder.
     *
     * @return the builder as a String
     */
    @Override
    public String getString() {
        return new String(buffer, 0, size);
    }

    /**
     * Appends a separator to the builder if the loop index is greater than zero.
     * The separator is appended using {@link #append(char)}.
     * <p>
     * This method is useful for adding a separator each time around the
     * loop except the first.
     * </p>
     * <pre>
     * for (int i = 0; i &lt; list.size(); i++) {
     *   appendSeparator(",", i);
     *   append(list.get(i));
     * }
     * </pre>
     *
     * @param separator the separator to use
     * @param loopIndex the loop index
     * @return this, to enable chaining
     * @since 2.3
     */
    public StrBuilderResult appendSeparator(final char separator, final int loopIndex) {
        if (loopIndex > 0) {
            append(separator);
        }
        return this;
    }

    /**
     * Appends a separator if the builder is currently non-empty.
     * The separator is appended using {@link #append(char)}.
     * <p>
     * This method is useful for adding a separator each time around the
     * loop except the first.
     * <pre>
     * for (Iterator it = list.iterator(); it.hasNext(); ) {
     *   appendSeparator(',');
     *   append(it.next());
     * }
     * </pre>
     *
     * @param separator  the separator to use
     * @return this, to enable chaining
     * @since 2.3
     */
    public StrBuilderResult appendSeparator(final char separator) {
        if (size > 0) {
            append(separator);
        }
        return this;
    }

    /**
     * Appends a char value to the string builder.
     *
     * @param ch the value to append
     * @return this, to enable chaining
     * @since 3.0
     */
    public StrBuilderResult append(final char ch) {
        final int len = size;
        ensureCapacity(len + 1);
        buffer[size++] = ch;
        return this;
    }

    /**
     * Appends a string to this string builder.
     *
     * @param str the string to append
     * @return this, to enable chaining
     */
    public StrBuilderResult append(final String str) {
        final int strLen = str.length();
        if (strLen > 0) {
            final int len = size;
            ensureCapacity(len + strLen);
            str.getChars(0, strLen, buffer, len);
            size += strLen;
        }
        return this;
    }

    /**
     * Appends a string to this string builder, preceded by a separator if the buffer is empty.
     *
     * @param separator  the separator to use
     * @param str the string to append
     * @return this, to enable chaining
     */
    public StrBuilderResult appendWithSeparator(final char separator, final String str) {
        if (size > 0) {
            append(separator);
        }
        final int strLen = str.length();
        if (strLen > 0) {
            final int len = size;
            ensureCapacity(len + strLen);
            str.getChars(0, strLen, buffer, len);
            size += strLen;
        }
        return this;
    }

    /**
     * Appends a char array to the string builder.
     *
     * @param chars      the char array to append
     * @param startIndex the start index, inclusive, must be valid
     * @param length     the length to append, must be valid
     * @return this, to enable chaining
     */
    public StrBuilderResult append(final char[] chars, final int startIndex, final int length) {
        if (length > 0) {
            final int len = size;
            ensureCapacity(len + length);
            System.arraycopy(chars, startIndex, buffer, len, length);
            size += length;
        }
        return this;
    }

    /**
     * Checks the capacity and ensures that it is at least the size specified.
     *
     * @param capacity the capacity to ensure
     * @return this, to enable chaining
     */
    private Formatter.Result ensureCapacity(final int capacity) {
        if (capacity > buffer.length) {
            final char[] old = buffer;
            buffer = new char[capacity * 2];
            System.arraycopy(old, 0, buffer, 0, size);
        }
        return this;
    }

    /**
     * Clears the string builder and restore its initial size.
     */
    @Override
    public void clear() {
        this.size = 0;
        this.attachment = null;
        minimizeCapacity();
    }

    /**
     * Minimizes the capacity to the initial size of the buffer.
     */
    private void minimizeCapacity() {
        if (buffer.length > initialCapacity) {
            buffer = new char[initialCapacity];
        }
    }
}
