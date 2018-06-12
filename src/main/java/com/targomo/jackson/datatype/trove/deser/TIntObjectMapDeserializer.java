package com.targomo.jackson.datatype.trove.deser;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class TIntObjectMapDeserializer extends BaseTroveObjectValueMapDeserializer<TIntObjectMap<Object>>
{
    private static final long serialVersionUID = 1L;

    public TIntObjectMapDeserializer(JavaType type, BeanProperty property,
            KeyDeserializer keyDeserializer, TypeDeserializer valueTypeDeserializer,
            JsonDeserializer<?> valueDeserializer,
            Set<String> toIgnore)
    {
        super(type, property, keyDeserializer, valueTypeDeserializer, valueDeserializer, toIgnore);
    }

    @Override
    protected TIntObjectMapDeserializer withResolved(KeyDeserializer keyDeserializer,
            TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser,
            Set<String> ignorable) {
        return new TIntObjectMapDeserializer(mapType, property,
                keyDeserializer, valueTypeDeser, valueDeser, ignorable);
    }

    @Override
    public TIntObjectMap<Object> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        return deserialize(jp, ctxt, new TIntObjectHashMap<Object>());
    }

    @Override
    public TIntObjectMap<Object> deserialize(JsonParser jp, DeserializationContext ctxt,
            TIntObjectMap<Object> result)
        throws IOException, JsonProcessingException
    {
        // Ok: must point to START_OBJECT or FIELD_NAME
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        } else  if (t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
            throw ctxt.mappingException(mapType.getRawClass());
        }
        /*
        if (_standardStringKey) {
            _readAndBindStringMap(jp, ctxt, result);
            return result;
        }
        */
        final KeyDeserializer keyDes = keyDeserializer;
        final JsonDeserializer<Object> valueDes = valueDeserializer;
        final TypeDeserializer typeDeser = valueTypeDeserializer;
        for (; t == JsonToken.FIELD_NAME; t = jp.nextToken()) {
            // Must point to field name
            String fieldName = jp.getCurrentName();
            Object key = keyDes.deserializeKey(fieldName, ctxt);
            if (!(key instanceof Number)) {
                throw ctxt.weirdKeyException(mapType.getKeyType().getRawClass(), fieldName, "Unable map JSON key to int");
            }
            int keyValue = ((Number) key).intValue();
            // And then the value...
            t = jp.nextToken();
            if (ignorableProperties != null && ignorableProperties.contains(fieldName)) {
                jp.skipChildren();
                continue;
            }
            // Note: must handle null explicitly here; value deserializers won't
            Object value;            
            if (t == JsonToken.VALUE_NULL) {
                value = null;
            } else if (typeDeser == null) {
                value = valueDes.deserialize(jp, ctxt);
            } else {
                value = valueDes.deserializeWithType(jp, ctxt, typeDeser);
            }
            result.put(keyValue, value);
        }
        return result;
    }
}
