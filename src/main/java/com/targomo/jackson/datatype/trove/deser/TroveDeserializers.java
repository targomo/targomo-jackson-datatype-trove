package com.targomo.jackson.datatype.trove.deser;

import java.util.HashSet;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.util.ArrayBuilders;

public class TroveDeserializers extends Deserializers.Base{

    private final int noEntryValue;

    public TroveDeserializers(int noEntryValue){
        this.noEntryValue = noEntryValue;
    }

    @Override
    public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type,
            DeserializationConfig config, BeanDescription beanDesc,
            KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
        throws JsonMappingException
    {
        // Object-key types:
        if (TObjectIntMap.class.isAssignableFrom(type.getRawClass())) {
            return new TObjectIntMapDeserializer(type, null, keyDeserializer,
                    elementTypeDeserializer, elementDeserializer,
                    _findIgnorable(config, beanDesc), this.noEntryValue);
        }
        // Object-value types:
        if (TIntObjectMap.class.isAssignableFrom(type.getRawClass())) {
            return new TIntObjectMapDeserializer(type, null, keyDeserializer,
                    elementTypeDeserializer, elementDeserializer,
                    _findIgnorable(config, beanDesc));
        }
        // int-int types:
        if (TIntIntMap.class.isAssignableFrom(type.getRawClass())) {
            return new TIntIntMapDeserializer(type, null, keyDeserializer,
                    elementTypeDeserializer, elementDeserializer,
                    _findIgnorable(config, beanDesc), this.noEntryValue);
        }
        return null;
    }

    @Override
    public JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType type,
            DeserializationConfig config, BeanDescription beanDesc,
            TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
        throws JsonMappingException
    {
        return null;
    }

    protected HashSet<String> _findIgnorable(DeserializationConfig config,
            BeanDescription beanDesc)
    {
        String[] toIgnore = config.getAnnotationIntrospector().findPropertiesToIgnore(beanDesc.getClassInfo());
        if (toIgnore == null || toIgnore.length == 0) {
            return null;
        }
        return ArrayBuilders.arrayToSet(toIgnore);
    }
}
