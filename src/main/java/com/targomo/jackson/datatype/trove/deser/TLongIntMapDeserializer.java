package com.targomo.jackson.datatype.trove.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import gnu.trove.impl.Constants;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TLongIntHashMap;

import java.io.IOException;
import java.util.Set;

public class TLongIntMapDeserializer extends BaseTroveMapDeserializer<TLongIntMap>
{
    private static final long serialVersionUID = 1L;
    private final boolean standardValueDeserializer;
    private final int noEntryValue;

    public TLongIntMapDeserializer(JavaType type, BeanProperty property,
                                  KeyDeserializer keyDeserializer, TypeDeserializer valueTypeDeserializer,
                                  JsonDeserializer<?> valueDeserializer,
                                  Set<String> toIgnore, int noEntryValue)
    {
        super(type, property, keyDeserializer, valueTypeDeserializer, valueDeserializer, toIgnore);
        this.standardValueDeserializer = isDefaultDeserializer(valueDeserializer);
        this.noEntryValue = noEntryValue;
    }

    @Override
    protected TLongIntMapDeserializer withResolved(KeyDeserializer keyDeserializer,
                                                  TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser,
                                                  Set<String> ignorable) {
        return new TLongIntMapDeserializer(mapType, property,
                keyDeserializer, valueTypeDeser, valueDeser, ignorable, noEntryValue);
    }

    @Override
    public TLongIntMap deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        return deserialize(jp, ctxt, new TLongIntHashMap(Constants.DEFAULT_CAPACITY,
                Constants.DEFAULT_LOAD_FACTOR, 0, this.noEntryValue));
    }

    @Override
    public TLongIntMap deserialize(JsonParser jp, DeserializationContext ctxt,
                                   TLongIntMap result)
        throws IOException
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
            Object key = keyDeserializer.deserializeKey(fieldName, ctxt);
            if (!(key instanceof Number)) {
                throw ctxt.weirdKeyException(mapType.getKeyType().getRawClass(), fieldName, "Unable map JSON key to long");
            }
            long keyValue = ((Number) key).longValue();
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
            result.put(keyValue, value);
        }
        return result;
    }
}
