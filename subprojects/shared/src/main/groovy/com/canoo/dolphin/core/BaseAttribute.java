/*
 * Copyright 2012 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canoo.dolphin.core;

import groovy.lang.GString;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The value may be null as long as the BaseAttribute is used as a "placeholder".
 */

public abstract class BaseAttribute extends AbstractObservable implements Attribute {

    static final public Class[] SUPPORTED_VALUE_TYPES = { Character.class, String.class, Number.class, Boolean.class, Date.class };
    static final private Logger log = Logger.getLogger(BaseAttribute.class.getName());

    private final String propertyName;
    private Object value;
    private Object initialValue;
    private boolean dirty = false;

    private long id = System.identityHashCode(this); // todo: dk: has to change to tell client from server
    private String qualifier; // application specific semantics apply

    public BaseAttribute(String propertyName) {
        this(propertyName, null);
    }

    public BaseAttribute(String propertyName, Object initialValue) {
        this.propertyName = propertyName;
        this.initialValue = initialValue;
        this.value = initialValue;
    }

    public boolean isDirty() {
        return dirty;
    }

    public Object getInitialValue() {
        return initialValue;
    }

    public Object getValue() {
        return value;
    }

    /** Check whether value is of allowed type and convert to an allowed type if possible. */
    static Object checkValue(Object value) {
        if (null == value) return null;
        Object result = value;
        if (value instanceof GString) value = value.toString();
        if (value instanceof BaseAttribute) {
            if (log.isLoggable(Level.WARNING)) log.warning("An Attribute may not itself contain an attribute as a value. Assuming you forgot to call getValue().");
            value = checkValue((((BaseAttribute) value).getValue()));
        }
        boolean ok = false;
        for (Class type : SUPPORTED_VALUE_TYPES ) {
            if (type.isAssignableFrom(value.getClass())) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            throw new IllegalArgumentException("Attribute values of this type are not allowed: " + value.getClass().getSimpleName());
        }
        return result;
    }

    // todo dk: think about specific method versions for each allowed type
    public void setValue(Object value) {
        value = checkValue(value);
        setDirty(initialValue == null ? value != null : !initialValue.equals(value));
        firePropertyChange(VALUE, this.value, this.value = value);
    }

    private void setDirty(boolean dirty) {
        firePropertyChange(DIRTY_PROPERTY, this.dirty, this.dirty = dirty);
    }

    private void setInitialValue(Object initialValue) {
        setDirty(initialValue == null ? value != null : !initialValue.equals(value));
        firePropertyChange(INITIAL_VALUE, this.initialValue, this.initialValue = initialValue);
    }

    public void rebase() {
        setInitialValue(getValue());
    }

    public void reset() {
        setValue(getInitialValue());
        setDirty(false);
    }

    public String toString() {
        return new StringBuilder()
                .append(id)
                .append(" : ")
                .append(propertyName)
                .append(" (")
                .append(qualifier).append(") ")
                .append(value).toString();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        firePropertyChange(QUALIFIER_PROPERTY, this.qualifier, this.qualifier = qualifier);
    }

    public void syncWith(Attribute source) {
        if (this == source || null == source) return;
        setInitialValue(source.getInitialValue());
        setQualifier(source.getQualifier());
        setValue(source.getValue());
    }
}
