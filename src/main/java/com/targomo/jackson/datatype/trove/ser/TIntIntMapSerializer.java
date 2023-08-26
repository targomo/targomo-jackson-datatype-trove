package com.targomo.jackson.datatype.trove.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import gnu.trove.map.TIntIntMap;

import java.io.IOException;

public class TIntIntMapSerializer extends BaseTroveMapSerializer<TIntIntMap> implements ContextualSerializer
{
    TIntIntMapSerializer(JavaType type, BeanProperty property,
                         JsonSerializer<Object> keySerializer,
                         TypeSerializer valueTypeSerializer,
                         JsonSerializer<Object> valueSerializer) {
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

    /**
     * Helper method used to encapsulate logic for determining whether there is
     * a property annotation that overrides element type; if so, we can
     * and need to statically find the serializer.
     */
    protected boolean hasContentTypeAnnotation(SerializerProvider provider,
                                               BeanProperty property) throws JsonMappingException {
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

    protected JsonSerializer<TIntIntMap> withResolved(BeanProperty prop,
                                                      JsonSerializer<Object> newKeySerializer,
                                                      JsonSerializer<Object> newValueSerializer) {
        return new TIntIntMapSerializer(mapType, prop,
                newKeySerializer, valueTypeSerializer, newValueSerializer);
    }
    
    @Override
    void serializeEntries(TIntIntMap map, final JsonGenerator jgen, final SerializerProvider provider)
        throws IOExceptionWrapper
    {
        // somewhat easy, as both key and value serializers are statically known
        map.forEachEntry( (key, value) -> {
                try {
                    keySerializer.serialize(key, jgen, provider);
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
    public boolean isEmpty(TIntIntMap value) {
        return value.isEmpty();
    }
}
