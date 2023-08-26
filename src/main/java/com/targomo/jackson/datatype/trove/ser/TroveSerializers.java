package com.targomo.jackson.datatype.trove.ser;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import gnu.trove.map.*;

public final class TroveSerializers extends Serializers.Base {
    
    @Override
    public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config, MapLikeType type,
                                                   BeanDescription beanDesc, JsonSerializer<Object> keySerializer,
                                                   TypeSerializer elementTypeSerializer,
                                                   JsonSerializer<Object> elementValueSerializer)
    {
        // LONGS
        // Object-key types:
        if (TObjectLongMap.class.isAssignableFrom(type.getRawClass())) {
            return new TObjectLongMapSerializer(type, null,
                    keySerializer, elementTypeSerializer, elementValueSerializer);
        }

        // Object-value types:
        if (TLongObjectMap.class.isAssignableFrom(type.getRawClass())) {
            return new TLongObjectMapSerializer(type, null,
                    keySerializer, elementTypeSerializer, elementValueSerializer);
        }

        // int-int types:
        if (TLongIntMap.class.isAssignableFrom(type.getRawClass())) {
            return new TLongIntMapSerializer(type, null,
                    keySerializer, elementTypeSerializer, elementValueSerializer);
        }

        // int-float types:
        if (TLongFloatMap.class.isAssignableFrom(type.getRawClass())) {
            return new TLongFloatMapSerializer(type, null,
                    keySerializer, elementTypeSerializer, elementValueSerializer);
        }

        // INTEGERS
        // Object-key types:
        if (TObjectIntMap.class.isAssignableFrom(type.getRawClass())) {
            return new TObjectIntMapSerializer(type, null,
                    keySerializer, elementTypeSerializer, elementValueSerializer);
        }

        // Object-value types:
        if (TIntObjectMap.class.isAssignableFrom(type.getRawClass())) {
            return new TIntObjectMapSerializer(type, null,
                    keySerializer, elementTypeSerializer, elementValueSerializer);
        }

        // int-int types:
        if (TIntIntMap.class.isAssignableFrom(type.getRawClass())) {
            return new TIntIntMapSerializer(type, null,
                    keySerializer, elementTypeSerializer, elementValueSerializer);
        }

        // int-float types:
        if (TIntFloatMap.class.isAssignableFrom(type.getRawClass())) {
            return new TIntFloatMapSerializer(type, null,
                    keySerializer, elementTypeSerializer, elementValueSerializer);
        }

        return null;
    }

    @Override
    public JsonSerializer<?> findCollectionLikeSerializer(SerializationConfig config,
            CollectionLikeType type, BeanDescription beanDesc,
            TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
    {
        // TODO
        return null;
    }
}
