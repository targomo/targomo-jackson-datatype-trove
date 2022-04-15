package com.targomo.jackson.datatype.trove.deser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Shared base class for serializers to use for both Object-keyed and Object-valued
 * map types.
 */
@SuppressWarnings("serial")
public abstract class BaseTroveMapDeserializer<T>
    extends ContainerDeserializerBase<T>
    implements ContextualDeserializer
{
    protected final JavaType mapType;

    protected final BeanProperty property;

    protected final KeyDeserializer keyDeserializer;
    
    protected final JsonDeserializer<Object> valueDeserializer;

    protected final TypeDeserializer valueTypeDeserializer;

    // // Any properties to ignore if seen?
    
    protected Set<String> ignorableProperties;

    /*
    /**********************************************************
    /* Construction
    /**********************************************************
     */

    @SuppressWarnings("unchecked")
    protected BaseTroveMapDeserializer(JavaType type, BeanProperty property,
            KeyDeserializer keyDeserializer, TypeDeserializer valueTypeDeserializer,
            JsonDeserializer<?> valueDeserializer,
            Set<String> toIgnore)
    {
        super(type);
        mapType = type;
        this.property = property;
        this.keyDeserializer = keyDeserializer;
        this.valueTypeDeserializer = valueTypeDeserializer;
        this.valueDeserializer = (JsonDeserializer<Object>) valueDeserializer;
    }

    
    /**
     * Fluent factory method used to create a copy with slightly
     * different settings.
     */
    protected abstract BaseTroveMapDeserializer<?> withResolved(KeyDeserializer keyDeser,
            TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser,
            Set<String> ignorable);

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException
    {
        KeyDeserializer kd = keyDeserializer;
        if (kd == null) {
            JavaType keyType = mapType.getKeyType();
            if (keyType.isPrimitive()) { // 2.2.0 has issues, add handling
                keyType = ctxt.constructType(ClassUtil.wrapperType(keyType.getRawClass()));
            }
            kd = ctxt.findKeyDeserializer(keyType, property);
        } else {
            if (kd instanceof ContextualKeyDeserializer) {
                kd = ((ContextualKeyDeserializer) kd).createContextual(ctxt, property);
            }
        }
        JsonDeserializer<?> vd = valueDeserializer;
        // #125: May have a content converter
        vd = findConvertingContentDeserializer(ctxt, property, vd);
        if (vd == null) {
            vd = ctxt.findContextualValueDeserializer(mapType.getContentType(), property);
        } else { // if directly assigned, probably not yet contextual, so:
            if (vd instanceof ContextualDeserializer) {
                vd = ((ContextualDeserializer) vd).createContextual(ctxt, property);
            }
        }
        TypeDeserializer vtd = valueTypeDeserializer;
        if (vtd != null) {
            vtd = vtd.forProperty(property);
        }
        Set<String> ignored = ignorableProperties;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null && property != null) {
            JsonIgnoreProperties.Value moreToIgnore = intr.findPropertyIgnoralByName(ctxt.getConfig(), property.getMember());
            if (moreToIgnore != null && !moreToIgnore.getIgnored().isEmpty()) {
                ignored = (ignored == null) ? new HashSet<>() : new HashSet<>(ignored);
                ignored.addAll(moreToIgnore.getIgnored());
            }
        }
        return withResolved(kd, vtd, vd, ignored);
    }

    /*
    /**********************************************************
    /* Standard methods
    /**********************************************************
     */
    
    @Override
    public JavaType getContentType() {
        return mapType.getContentType();
    }

    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return valueDeserializer;
    }
    
    @Override
    public abstract T deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException;

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws IOException, JsonProcessingException
    {
        // In future could check current token... for now this should be enough:
        return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
    }

}
