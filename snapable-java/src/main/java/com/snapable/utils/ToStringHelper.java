/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.snapable.utils;


public final class ToStringHelper {

    private final String className;
    private ValueHolder holderHead = new ValueHolder();
    private ValueHolder holderTail = holderHead;
    private boolean omitNullValues = false;

    /**
     * Creates an instance of {@link ToStringHelper}.
     *
     * <p>This is helpful for implementing {@link Object#toString()}.
     * Specification by example: <pre>   {@code
     *   // Returns "ClassName{}"
     *   ToStringHelper.getInstance(this)
     *       .toString();
     *
     *   // Returns "ClassName{x=1}"
     *   ToStringHelper.getInstance(this)
     *       .add("x", 1)
     *       .toString();
     *
     *   // Returns "MyObject{x=1}"
     *   ToStringHelper.getInstance("MyObject")
     *       .add("x", 1)
     *       .toString();
     *
     *   // Returns "ClassName{x=1, y=foo}"
     *   ToStringHelper.getInstance(this)
     *       .add("x", 1)
     *       .add("y", "foo")
     *       .toString();</pre>
     *
     * @param self the object to generate the string for (typically {@code this}),
     *        used only for its class name
     */
    public static ToStringHelper getInstance(Object self) {
        return new ToStringHelper(simpleName(self.getClass()));
    }

    /**
     * Creates an instance of {@link ToStringHelper} in the same manner as
     * {@link ToStringHelper#getInstance(Object)}, but using the name of {@code clazz}
     * instead of using an instance's {@link Object#getClass()}.
     *
     * @param clazz the {@link Class} of the instance
     */
    public static ToStringHelper toStringHelper(Class<?> clazz) {
        return new ToStringHelper(simpleName(clazz));
    }

    /**
     * Creates an instance of {@link ToStringHelper} in the same manner as
     * {@link ToStringHelper#getInstance(Object)}, but using {@code className} instead
     * of using an instance's {@link Object#getClass()}.
     *
     * @param className the name of the instance type
     */
    public static ToStringHelper toStringHelper(String className) {
        return new ToStringHelper(className);
    }

    /**
     * {@link Class#getSimpleName()} is not GWT compatible yet, so we
     * provide our own implementation.
     */
    private static String simpleName(Class<?> clazz) {
        String name = clazz.getName();

        // the nth anonymous class has a class name ending in "Outer$n"
        // and local inner classes have names ending in "Outer.$1Inner"
        name = name.replaceAll("\\$[0-9]+", "\\$");

        // we want the name of the inner class all by its lonesome
        int start = name.lastIndexOf('$');

        // if this isn't an inner class, just find the start of the
        // top level class name.
        if (start == -1) {
            start = name.lastIndexOf('.');
        }
        return name.substring(start + 1);
    }

    /**
     * Use {@link ToStringHelper#getInstance(Object)} to create an instance.
     */
    private ToStringHelper(String className) {
        this.className = className;
    }

    /**
     * Configures the {@link ToStringHelper} so {@link #toString()} will ignore
     * properties with null value. The order of calling this method, relative
     * to the {@code add()}/{@code addValue()} methods, is not significant.
     */
    public ToStringHelper omitNullValues() {
        omitNullValues = true;
        return this;
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value}
     * format. If {@code value} is {@code null}, the string {@code "null"}
     * is used, unless {@link #omitNullValues()} is called, in which case this
     * name/value pair will not be added.
     */
    public ToStringHelper add(String name, Object value) {
        return addHolder(name, value);
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value} format.
     */
    public ToStringHelper add(String name, boolean value) {
        return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value} format.
     */
    public ToStringHelper add(String name, char value) {
        return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value} format.
     */
    public ToStringHelper add(String name, double value) {
        return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value} format.
     */
    public ToStringHelper add(String name, float value) {
        return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value} format.
     */
    public ToStringHelper add(String name, int value) {
        return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds a name/value pair to the formatted output in {@code name=value} format.
     */
    public ToStringHelper add(String name, long value) {
        return addHolder(name, String.valueOf(value));
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, Object)} instead
     * and give value a readable name.
     */
    public ToStringHelper addValue(Object value) {
        return addHolder(value);
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, boolean)} instead
     * and give value a readable name.
     */
    public ToStringHelper addValue(boolean value) {
        return addHolder(String.valueOf(value));
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, char)} instead
     * and give value a readable name.
     */
    public ToStringHelper addValue(char value) {
        return addHolder(String.valueOf(value));
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, double)} instead
     * and give value a readable name.
     */
    public ToStringHelper addValue(double value) {
        return addHolder(String.valueOf(value));
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, float)} instead
     * and give value a readable name.
     */
    public ToStringHelper addValue(float value) {
        return addHolder(String.valueOf(value));
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, int)} instead
     * and give value a readable name.
     */
    public ToStringHelper addValue(int value) {
        return addHolder(String.valueOf(value));
    }

    /**
     * Adds an unnamed value to the formatted output.
     *
     * <p>It is strongly encouraged to use {@link #add(String, long)} instead
     * and give value a readable name.
     */
    public ToStringHelper addValue(long value) {
        return addHolder(String.valueOf(value));
    }

    /**
     * Returns a string in the format specified by {@link
     * ToStringHelper#getInstance(Object)}.
     *
     * <p>After calling this method, you can keep adding more properties to later
     * call toString() again and get a more complete representation of the
     * same object; but properties cannot be removed, so this only allows
     * limited reuse of the helper instance. The helper allows duplication of
     * properties (multiple name/value pairs with the same name can be added).
     */
    @Override public String toString() {
        // create a copy to keep it consistent in case value changes
        boolean omitNullValuesSnapshot = omitNullValues;
        String nextSeparator = "";
        StringBuilder builder = new StringBuilder(32).append(className)
                .append('{');
        for (ValueHolder valueHolder = holderHead.next; valueHolder != null;
             valueHolder = valueHolder.next) {
            if (!omitNullValuesSnapshot || valueHolder.value != null) {
                builder.append(nextSeparator);
                nextSeparator = ", ";

                if (valueHolder.name != null) {
                    builder.append(valueHolder.name).append('=');
                }
                builder.append(valueHolder.value);
            }
        }
        return builder.append('}').toString();
    }

    private ValueHolder addHolder() {
        ValueHolder valueHolder = new ValueHolder();
        holderTail = holderTail.next = valueHolder;
        return valueHolder;
    }

    private ToStringHelper addHolder(Object value) {
        ValueHolder valueHolder = addHolder();
        valueHolder.value = value;
        return this;
    }

    private ToStringHelper addHolder(String name, Object value) {
        ValueHolder valueHolder = addHolder();
        valueHolder.value = value;
        valueHolder.name = name;
        return this;
    }

    private static final class ValueHolder {
        String name;
        Object value;
        ValueHolder next;
    }
}
