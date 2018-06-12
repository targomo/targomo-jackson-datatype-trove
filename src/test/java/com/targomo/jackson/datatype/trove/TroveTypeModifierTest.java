package com.targomo.jackson.datatype.trove;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import gnu.trove.map.TDoubleObjectMap;
import gnu.trove.map.TObjectIntMap;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public final class TroveTypeModifierTest {

    private TroveTypeModifier troveTypeModifier;
    private TypeFactory typeFactory;

    @Before
    public void setUp() {
        troveTypeModifier = new TroveTypeModifier();
        typeFactory = new ObjectMapper().getTypeFactory();
    }

    @Test
    public void testStringKeyType() {

        JavaType beanType = typeFactory.constructType(new TypeReference<TObjectIntMap<String>>() {});

        MapLikeType mapLikeType = (MapLikeType) troveTypeModifier.modifyType(beanType, null, null, typeFactory);

        assertEquals(String.class, mapLikeType.getKeyType().getRawClass());
        assertEquals(int.class, mapLikeType.getContentType().getRawClass());
    }

    @Test
    public void testGenericObjectKeyType() {

        JavaType beanType = typeFactory.constructType(new TypeReference<TObjectIntMap<Map<UUID, URL>>>() {});

        MapLikeType mapLikeType = (MapLikeType) troveTypeModifier.modifyType(beanType, null, null, typeFactory);

        JavaType keyType = mapLikeType.getKeyType();
        assertEquals(Map.class, keyType.getRawClass());
        assertEquals(UUID.class, keyType.getKeyType().getRawClass());
        assertEquals(URL.class, keyType.getContentType().getRawClass());

        assertEquals(int.class, mapLikeType.getContentType().getRawClass());
    }

    @Test
    public void testStringValueType() {
        JavaType beanType = typeFactory.constructType(new TypeReference<TDoubleObjectMap<String>>() {});

        MapLikeType mapLikeType = (MapLikeType) troveTypeModifier.modifyType(beanType, null, null, typeFactory);

        assertEquals(double.class, mapLikeType.getKeyType().getRawClass());
        assertEquals(String.class, mapLikeType.getContentType().getRawClass());
    }

    @Test
    public void testGenericObjectValueType() {

        JavaType beanType = typeFactory.constructType(new TypeReference<TDoubleObjectMap<Map<UUID, URL>>>() {});

        MapLikeType mapLikeType = (MapLikeType) troveTypeModifier.modifyType(beanType, null, null, typeFactory);

        assertEquals(double.class, mapLikeType.getKeyType().getRawClass());

        JavaType valType = mapLikeType.getContentType();
        assertEquals(Map.class, valType.getRawClass());
        assertEquals(UUID.class, valType.getKeyType().getRawClass());
        assertEquals(URL.class, valType.getContentType().getRawClass());
    }
}
