package com.targomo.jackson.datatype.trove.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;

import java.io.IOException;
import java.util.Set;

public class TObjectLongMapDeserializer
    extends BaseTroveObjectKeyMapDeserializer<TObjectLongMap<Object>>
{
    private static final long serialVersionUID = 1L;
    private final int noEntryValue;

    public TObjectLongMapDeserializer(JavaType type, BeanProperty property,
                                      KeyDeserializer keyDeserializer, TypeDeserializer valueTypeDeserializer,
                                      JsonDeserializer<?> valueDeserializer,
                                      Set<String> toIgnore, int noEntryValue)
    {
        super(type, property, keyDeserializer, valueTypeDeserializer, valueDeserializer, toIgnore);
        this.noEntryValue = noEntryValue;
    }

    @Override
    protected TObjectLongMapDeserializer withResolved(KeyDeserializer keyDeserializer,
                                                      TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser,
                                                      Set<String> ignorable) {
        return new TObjectLongMapDeserializer(mapType, property,
                keyDeserializer, valueTypeDeser, valueDeser, ignorable, noEntryValue);
    }

    /*
    /**********************************************************
    /* Deserialization
    /**********************************************************
     */

    @Override
    public TObjectLongMap<Object> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        return deserialize(jp, ctxt, new TObjectLongHashMap<>(Constants.DEFAULT_CAPACITY,
                Constants.DEFAULT_LOAD_FACTOR, this.noEntryValue));
    }

    @Override
    public TObjectLongMap<Object> deserialize(JsonParser jp, DeserializationContext ctxt,
                                              TObjectLongMap<Object> result)
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
            long value;
            if (t == JsonToken.VALUE_NULL) {
                value = 0;
            } else if (typeDeser == null) {
                if (standardValueDeserializer) {
                    value = _parseIntPrimitive(jp, ctxt);
                } else {
                    Object ob = valueDeserializer.deserialize(jp, ctxt);
                    value = ((Number) ob).longValue();
                }
            } else {
                Object ob = valueDeserializer.deserializeWithType(jp, ctxt, typeDeser);
                value = ((Number) ob).longValue();
            }
            result.put(key, value);
        }
        return result;
    }
}
