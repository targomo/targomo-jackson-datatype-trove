package com.targomo.jackson.datatype.trove.ser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

/**
 * Base class for serializers that have Object values, but primitive keys.
 *
 * @param <T> Type of Object value
 */
abstract class BaseTroveObjectValueMapSerializer<T>
    extends BaseTroveMapSerializer<T>
    implements ContextualSerializer
{
    BaseTroveObjectValueMapSerializer(JavaType type,
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
            // unfortunately can only get serializer ahead of time if type is static
            // (final type, or static typing enabled)
            if (valueUsesStaticTyping(provider.getConfig()) || hasContentTypeAnnotation(provider, property)) {
                ser = provider.findValueSerializer(mapType.getContentType(), property);
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

    /**
     * Helper method used to encapsulate logic for determining whether there is
     * a property annotation that overrides element type; if so, we can
     * and need to statically find the serializer.
     */
    protected boolean hasContentTypeAnnotation(SerializerProvider provider,
            BeanProperty property)
    {
        if (property != null) {
            AnnotationIntrospector intr = provider.getAnnotationIntrospector();
            if (intr != null) {
                if (intr.findSerializationContentType(property.getMember(), property.getType()) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Helper method called to check whether static typing is used with this serializer
     * instance. Should be called from 'createContextual', when property serializer is used
     * for is known (if any; null for root values)
     */
    protected boolean valueUsesStaticTyping(SerializationConfig config)
    {
        if (valueTypeSerializer != null) {
            return false;
        }
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        if (property != null) {
            JsonSerialize.Typing t = intr.findSerializationTyping(property.getMember());
            if (t != null) {
                return (t == JsonSerialize.Typing.STATIC);
            }
        }
        if (config.isEnabled(MapperFeature.USE_STATIC_TYPING)) {
            return true;
        }
        // finally, if value type is final class, that also works
        if (mapType.getContentType().isFinal()) {
            return true;
        }
        // otherwise have to accept that we have a dynamic case
        return false;
    }
}
