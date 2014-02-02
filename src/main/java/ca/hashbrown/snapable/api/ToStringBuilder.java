/*
 * Copyright 2011 CodeGist.org
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *  ===================================================================
 *
 *  More information at http://www.codegist.org.
 */

package ca.hashbrown.snapable.api;

import java.util.Arrays;

/**
 * @author Laurent Gilles (laurent.gilles@codegist.org)
 */
public class ToStringBuilder {
    private boolean first = true;
    private boolean done = false;
    private final StringBuilder sb;

    public ToStringBuilder(Object o) {
        this(o.getClass().getSimpleName());
    }
    public ToStringBuilder(String name) {
        this.sb = new StringBuilder(name + "[");
    }

    public ToStringBuilder append(String name, short v) {
        appendComa();
        sb.append(name).append("=").append(v);
        return this;
    }

    public ToStringBuilder append(String name, int v) {
        appendComa();
        sb.append(name).append("=").append(v);
        return this;
    }

    public ToStringBuilder append(String name, long v) {
        appendComa();
        sb.append(name).append("=").append(v);
        return this;
    }

    public ToStringBuilder append(String name, float v) {
        appendComa();
        sb.append(name).append("=").append(v);
        return this;
    }

    public ToStringBuilder append(String name, double v) {
        appendComa();
        sb.append(name).append("=").append(v);
        return this;
    }

    public ToStringBuilder append(String name, boolean v) {
        appendComa();
        sb.append(name).append("=").append(v);
        return this;
    }

    public ToStringBuilder append(String name, byte v) {
        appendComa();
        sb.append(name).append("=").append(v);
        return this;
    }

    public ToStringBuilder append(String name, char v) {
        appendComa();
        sb.append(name).append("=").append(v);
        return this;
    }

    public ToStringBuilder append(String name, short[] v) {
        appendComa();
        sb.append(name).append("=").append(Arrays.toString(v));
        return this;
    }

    public ToStringBuilder append(String name, int[] v) {
        appendComa();
        sb.append(name).append("=").append(Arrays.toString(v));
        return this;
    }

    public ToStringBuilder append(String name, long[] v) {
        appendComa();
        sb.append(name).append("=").append(Arrays.toString(v));
        return this;
    }

    public ToStringBuilder append(String name, float[] v) {
        appendComa();
        sb.append(name).append("=").append(Arrays.toString(v));
        return this;
    }

    public ToStringBuilder append(String name, double[] v) {
        appendComa();
        sb.append(name).append("=").append(Arrays.toString(v));
        return this;
    }

    public ToStringBuilder append(String name, boolean[] v) {
        appendComa();
        sb.append(name).append("=").append(Arrays.toString(v));
        return this;
    }

    public ToStringBuilder append(String name, byte[] v) {
        appendComa();
        sb.append(name).append("=").append(Arrays.toString(v));
        return this;
    }

    public ToStringBuilder append(String name, char[] v) {
        appendComa();
        sb.append(name).append("=").append(Arrays.toString(v));
        return this;
    }

    public ToStringBuilder append(String name, Object[] field) {
        appendComa();
        sb.append(name).append("=").append(Arrays.toString(field));
        return this;
    }

    public ToStringBuilder append(String name, Object field) {
        if(field == null || !field.getClass().isArray()) {
            appendComa();
            sb.append(name).append("=").append(field);
            return this;
        } else if (field instanceof long[]) {
            return append(name, (long[]) field);
        } else if (field instanceof int[]) {
            return append(name, (int[]) field);
        } else if (field instanceof short[]) {
            return append(name, (short[]) field);
        } else if (field instanceof char[]) {
            return append(name, (char[]) field);
        } else if (field instanceof byte[]) {
            return append(name, (byte[]) field);
        } else if (field instanceof double[]) {
            return append(name, (double[]) field);
        } else if (field instanceof float[]) {
            return append(name, (float[]) field);
        } else if (field instanceof boolean[]) {
            return append(name, (boolean[]) field);
        } else {
            // Not an array of primitives
            return append(name, (Object[]) field);
        }
    }

    private void appendComa() {
        if (!first) {
            sb.append(",");
        }
        first = false;
    }

    public String toString() {
        if (!done) {
            sb.append("]");
            done = true;
        }
        return sb.toString();
    }
}