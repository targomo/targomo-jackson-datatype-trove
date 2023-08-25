package com.targomo.jackson.datatype.trove.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import gnu.trove.map.TObjectLongMap;

import java.io.IOException;

final class TObjectLongMapSerializer extends BaseTroveObjectKeyMapSerializer<TObjectLongMap<?>>
{
    TObjectLongMapSerializer(JavaType type, BeanProperty property,
                             JsonSerializer<Object> keySerializer,
                             TypeSerializer valueTypeSerializer, JsonSerializer<Object> valueSerializer) {
        super(type, property, keySerializer, valueTypeSerializer, valueSerializer);
    }

    @Override
    protected JsonSerializer<TObjectLongMap<?>> withResolved(BeanProperty prop,
            JsonSerializer<Object> newKeySerializer,
            JsonSerializer<Object> newValueSerializer) {
        return new TObjectLongMapSerializer(mapType, prop,
                newKeySerializer, valueTypeSerializer, newValueSerializer);
    }
    
    @Override
    void serializeEntries(TObjectLongMap<?> map, final JsonGenerator jgen, final SerializerProvider provider)
        throws IOExceptionWrapper
    {
        // somewhat easy, as both key and value serializers are statically known
        map.forEachEntry( (key, value) -> {
                try {
                    if (key == null) {
                        provider.findNullKeySerializer(mapType.getKeyType(), property).serialize(null, jgen, provider);
                    } else {
                        keySerializer.serialize(key, jgen, provider);
                    }
                    if (value == map.getNoEntryValue()) {
                        provider.defaultSerializeNull(jgen);
                    } else {
                        if (valueSerializer == null) { // null used as marker to denote standard serializer
                            jgen.writeNumber(value);
                        } else if (valueTypeSerializer == null) {
                            valueSerializer.serialize(value, jgen, provider);
                        } else {
                            valueSerializer.serializeWithType(value, jgen, provider, valueTypeSerializer);
                        }
                    }
                } catch (IOException e) {
                    throw new IOExceptionWrapper(e);
                }
                return true;
            });
    }

    @Override
    public boolean isEmpty(TObjectLongMap<?> value) {
        return value.isEmpty();
    }
}
