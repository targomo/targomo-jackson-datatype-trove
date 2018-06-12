package com.targomo.jackson.datatype.trove.ser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

/**
 * Base class for serializers that have object type as key, but value
 * type is primitive.
 *
 * @param <T> Type of Object key
 */
abstract class BaseTroveObjectKeyMapSerializer<T> 
    extends BaseTroveMapSerializer<T>
    implements ContextualSerializer
{
    protected BaseTroveObjectKeyMapSerializer(JavaType type,
            BeanProperty property,
            JsonSerializer<Object> keySerializer,
            TypeSerializer valueTypeSerializer,
            JsonSerializer<Object> valueSerializer)
    {
        super(type, property, keySerializer, valueTypeSerializer, valueSerializer);
    }

    /* This is important method to define so that we can handle per-property
     * annotations for overriding serializers.
     */
    @SuppressWarnings("unchecked")
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider,
            BeanProperty property)
        throws JsonMappingException
    {
        JsonSerializer<?> ser = null;
        JsonSerializer<?> keySer = null;

        // First: if we have a property, may have property-annotation overrides
        if (property != null) {
            AnnotatedMember m = property.getMember();
            if (m != null) {
                Object serDef;
                final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
                serDef = intr.findKeySerializer(m);
                if (serDef != null) {
                    keySer = provider.serializerInstance(m, serDef);
                }
                serDef = intr.findContentSerializer(m);
                if (serDef != null) {
                    ser = provider.serializerInstance(m, serDef);
                }
            }
        }
        if (ser == null) {
            ser = valueSerializer;
        }
        if (ser == null) {
            // Since we want to allow overriding of serializer, need to look up what's registred
            ser = provider.findValueSerializer(mapType.getContentType(), property);
            /* However, in case of std Jackson serializer, we want
             * to instead just inline serialization call, marker by null value for serializer:
             */
            // But note: can't do this if type information is required, so:
            if ((valueTypeSerializer == null) && isJacksonStdImpl(ser)) {
                ser = null;
            }
        } else if (ser instanceof ContextualSerializer) {
            ser = ((ContextualSerializer) ser).createContextual(provider, property);
        }
        if (keySer == null) {
            keySer = keySerializer;
        }
        if (keySer == null) {
            keySer = provider.findKeySerializer(mapType.getKeyType(), property);
        } else if (keySer instanceof ContextualSerializer) {
            keySer = ((ContextualSerializer) keySer).createContextual(provider, property);
        }
        // TODO: could add support for ignoring entries as per annotation?
        return withResolved(property, (JsonSerializer<Object>) keySer, (JsonSerializer<Object>) ser);
    }

    protected abstract JsonSerializer<T> withResolved(BeanProperty prop,
            JsonSerializer<Object> keySer, JsonSerializer<Object> valueSerializer);
}
