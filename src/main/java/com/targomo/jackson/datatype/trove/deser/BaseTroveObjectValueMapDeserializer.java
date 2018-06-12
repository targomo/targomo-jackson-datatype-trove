package com.targomo.jackson.datatype.trove.deser;

import java.util.Set;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

@SuppressWarnings("serial")
public abstract class BaseTroveObjectValueMapDeserializer<T>
    extends BaseTroveMapDeserializer<T>
{
    protected BaseTroveObjectValueMapDeserializer(JavaType type, BeanProperty property,
            KeyDeserializer keyDeserializer, TypeDeserializer valueTypeDeserializer,
            JsonDeserializer<?> valueDeserializer, Set<String> toIgnore)
    {
        super(type, property, keyDeserializer, valueTypeDeserializer, valueDeserializer, toIgnore);
    }

    @Override
    protected abstract BaseTroveMapDeserializer<?> withResolved(KeyDeserializer keyDeser,
            TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser,
            Set<String> ignorable);
}
