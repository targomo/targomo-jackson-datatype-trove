package com.targomo.jackson.datatype.trove.deser;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class TObjectIntMapDeserializer
    extends BaseTroveObjectKeyMapDeserializer<TObjectIntMap<Object>>
{
    private static final long serialVersionUID = 1L;
    private final int noEntryValue;
    
    public TObjectIntMapDeserializer(JavaType type, BeanProperty property,
            KeyDeserializer keyDeserializer, TypeDeserializer valueTypeDeserializer,
            JsonDeserializer<?> valueDeserializer,
            Set<String> toIgnore, int noEntryValue)
    {
        super(type, property, keyDeserializer, valueTypeDeserializer, valueDeserializer, toIgnore);
        this.noEntryValue = noEntryValue;
    }

    @Override
    protected TObjectIntMapDeserializer withResolved(KeyDeserializer keyDeserializer,
            TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser,
            Set<String> ignorable) {
        return new TObjectIntMapDeserializer(mapType, property,
                keyDeserializer, valueTypeDeser, valueDeser, ignorable, noEntryValue);
    }

    /*
    /**********************************************************
    /* Deserialization
    /**********************************************************
     */

    @Override
    public TObjectIntMap<Object> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        return deserialize(jp, ctxt, new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY,
                Constants.DEFAULT_LOAD_FACTOR, this.noEntryValue));
    }

    @Override
    public TObjectIntMap<Object> deserialize(JsonParser jp, DeserializationContext ctxt,
            TObjectIntMap<Object> result)
        throws IOException, JsonProcessingException
    {
        // Ok: must point to START_OBJECT or FIELD_NAME
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        } else  if (t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
            throw ctxt.mappingException(mapType.getRawClass());
        }

        final TypeDeserializer typeDeser = valueTypeDeserializer;
        for (; t == JsonToken.FIELD_NAME; t = jp.nextToken()) {
            // Must point to field name
            String fieldName = jp.getCurrentName();
            Object key = standardStringKey ? fieldName : keyDeserializer.deserializeKey(fieldName, ctxt);
            // And then the value...
            t = jp.nextToken();
            if (ignorableProperties != null && ignorableProperties.contains(fieldName)) {
                jp.skipChildren();
                continue;
            }
            // Note: must handle null explicitly here; value deserializers won't
            int value;
            if (t == JsonToken.VALUE_NULL) {
                value = 0;
            } else if (typeDeser == null) {
                if (standardValueDeserializer) {
                    value = _parseIntPrimitive(jp, ctxt);
                } else {
                    Object ob = valueDeserializer.deserialize(jp, ctxt);
                    value = ((Number) ob).intValue();
                }
            } else {
                Object ob = valueDeserializer.deserializeWithType(jp, ctxt, typeDeser);
                value = ((Number) ob).intValue();
            }
            result.put(key, value);
        }
        return result;
    }
}
