package com.targomo.jackson.datatype.trove.deser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import java.util.Collections;

import java.util.Set;

public class TroveDeserializers extends Deserializers.Base{

    private final int noEntryValueInt;
    private final float noEntryValueFloat;

    public TroveDeserializers(int noEntryValueInt, float noEntryValueFloat){
        this.noEntryValueInt = noEntryValueInt;
        this.noEntryValueFloat = noEntryValueFloat;
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
                    _findIgnorable(config, beanDesc), this.noEntryValueInt);
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
                    _findIgnorable(config, beanDesc), this.noEntryValueInt);
        }
        // int-float types:
        if (TIntFloatMap.class.isAssignableFrom(type.getRawClass())) {
            return new TIntFloatMapDeserializer(type, null, keyDeserializer,
                    elementTypeDeserializer, elementDeserializer,
                    _findIgnorable(config, beanDesc), this.noEntryValueFloat);
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

    protected Set<String> _findIgnorable(DeserializationConfig config,
                                         BeanDescription beanDesc)
    {
        JsonIgnoreProperties.Value toIgnore = config.getAnnotationIntrospector().findPropertyIgnoralByName(config, beanDesc.getClassInfo());
        if (toIgnore == null || toIgnore.getIgnored().isEmpty()) {
            return Collections.emptySet();
        }

        return toIgnore.getIgnored();
    }
}
