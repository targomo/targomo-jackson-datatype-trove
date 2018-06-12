package com.targomo.jackson.datatype.trove.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.util.ClassUtil;

/**
 * Shared base class for serializers to use for both Object-keyed and Object-valued
 * map types.
 */
abstract class BaseTroveMapSerializer<T> extends StdSerializer<T>
{
    protected final JavaType mapType;

    protected final BeanProperty property;

    protected final JsonSerializer<Object> keySerializer;
    
    protected final JsonSerializer<Object> valueSerializer;

    protected final TypeSerializer valueTypeSerializer;

    BaseTroveMapSerializer(JavaType type, BeanProperty property,
            JsonSerializer<Object> keySerializer, TypeSerializer valueTypeSerializer,
            JsonSerializer<Object> valueSerializer)
    {
        super(type.getRawClass(), false);
        mapType = type;
        this.property = property;
        this.keySerializer = keySerializer;
        this.valueTypeSerializer = valueTypeSerializer;
        this.valueSerializer = valueSerializer;
    }

    @Override
    public void serialize(T value, final JsonGenerator jgen,
            final SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        if (!isEmpty(value)) {
            if (provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
                value = orderMap(value);
            }
            try {
                serializeEntries(value, jgen, provider);
            } catch (IOExceptionWrapper w) {
                throw w.getCause();
            }
        }
        jgen.writeEndObject();
    }

    @Override
    public void serializeWithType(T value, JsonGenerator jgen, SerializerProvider provider,
            TypeSerializer typeSer)
        throws IOException, JsonGenerationException
    {
        typeSer.writeTypePrefixForObject(value, jgen);
        if (!isEmpty(value)) {
            if (provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
                value = orderMap(value);
            }
            try {
                serializeEntries(value, jgen, provider);
            } catch (IOExceptionWrapper w) {
                throw w.getCause();
            }
        }
        typeSer.writeTypeSuffixForObject(value, jgen);
    }

    @Override
    public abstract boolean isEmpty(T value);
    
    abstract void serializeEntries(T value, JsonGenerator jgen, SerializerProvider provider)
            throws IOExceptionWrapper;

    /**
     * Overridable method that should handle ordering of values; called if automatic (re)ordering
     * is requested by annotations.
     * Default 
     */
    protected T orderMap(T value) {
        return value;
    }

    protected boolean isJacksonStdImpl(JsonSerializer<?> ser) {
        return ClassUtil.isJacksonStdImpl(ser);
    }
}
