package com.targomo.jackson.datatype.trove.deser;

import java.util.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

@SuppressWarnings("serial")
public abstract class BaseTroveObjectKeyMapDeserializer<T>
    extends BaseTroveMapDeserializer<T>
{
    protected final boolean standardStringKey;

    protected final boolean standardValueDeserializer;
    
    protected BaseTroveObjectKeyMapDeserializer(JavaType type, BeanProperty property,
            KeyDeserializer keyDeserializer, TypeDeserializer valueTypeDeserializer,
            JsonDeserializer<?> valueDeserializer, Set<String> toIgnore)
    {
        super(type, property, keyDeserializer, valueTypeDeserializer, valueDeserializer, toIgnore);
        standardStringKey = _isStdKeyDeser(type, keyDeserializer);
        standardValueDeserializer = isDefaultDeserializer(valueDeserializer);
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */
    
    /**
     * Helper method used to check whether we can just use the default key
     * deserialization, where JSON String becomes Java String.
     */
    protected final boolean _isStdKeyDeser(JavaType mapType, KeyDeserializer keyDeser)
    {
        if (keyDeser == null) {
            return true;
        }
        JavaType keyType = mapType.getKeyType();
        if (keyType == null) { // assumed to be Object
            return true;
        }
        Class<?> rawKeyType = keyType.getRawClass();
        return ((rawKeyType == String.class || rawKeyType == Object.class)
                && isDefaultKeyDeserializer(keyDeser));
    }
}
