package com.targomo.jackson.datatype.trove.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import gnu.trove.map.TLongObjectMap;

import java.io.IOException;

public class TLongObjectMapSerializer extends BaseTroveObjectValueMapSerializer<TLongObjectMap<?>>
{
    TLongObjectMapSerializer(JavaType type, BeanProperty property,
                             JsonSerializer<Object> keySerializer,
                             TypeSerializer valueTypeSerializer, JsonSerializer<Object> valueSerializer) {
        super(type, property, keySerializer, valueTypeSerializer, valueSerializer);
    }

    @Override
    protected JsonSerializer<TLongObjectMap<?>> withResolved(BeanProperty prop,
            JsonSerializer<Object> newKeySerializer,
            JsonSerializer<Object> newValueSerializer) {
        return new TLongObjectMapSerializer(mapType, prop,
                newKeySerializer, valueTypeSerializer, newValueSerializer);
    }
    
    @Override
    void serializeEntries(TLongObjectMap<?> map, final JsonGenerator jgen, final SerializerProvider provider)
        throws IOExceptionWrapper
    {
        // somewhat easy, as both key and value serializers are statically known
        map.forEachEntry ( (key, value) -> {
                try {
                    keySerializer.serialize(key, jgen, provider);
                    if (value == null) {
                        provider.defaultSerializeNull(jgen);
                    } else {
                        JsonSerializer<Object> ser = valueSerializer;
                        if (ser == null) {
                            ser = provider.findValueSerializer(value.getClass(), property);
                        }
                        if (valueTypeSerializer != null) {
                            ser.serializeWithType(value, jgen, provider, valueTypeSerializer);
                        } else { // must be dynamically located
                            ser.serialize(value, jgen, provider);
                        }
                    }
                } catch (IOException e) {
                    throw new IOExceptionWrapper(e);
                }
                return true;
            });
    }

    @Override
    public boolean isEmpty(TLongObjectMap<?> value) {
        return value.isEmpty();
    }
}
