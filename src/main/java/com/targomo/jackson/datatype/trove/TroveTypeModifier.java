package com.targomo.jackson.datatype.trove;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;
import gnu.trove.map.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Lets TObject*Map be map-like.
 */

class TroveTypeModifier extends TypeModifier {
    private final static Map<Class<?>, Class<?>> valTypes = new HashMap<>();
    static {
        valTypes.put(TObjectIntMap.class, int.class);
        valTypes.put(TObjectShortMap.class, short.class);
        valTypes.put(TObjectLongMap.class, long.class);
        valTypes.put(TObjectFloatMap.class, float.class);
        valTypes.put(TObjectDoubleMap.class, double.class);
        valTypes.put(TObjectByteMap.class, byte.class);
        valTypes.put(TObjectCharMap.class, char.class);
        valTypes.put(TIntIntMap.class, int.class);
    }
    private final static Map<Class<?>, Class<?>> keyTypes = new HashMap<>();
    static {
        keyTypes.put(TIntObjectMap.class, int.class);
        keyTypes.put(TShortObjectMap.class, short.class);
        keyTypes.put(TLongObjectMap.class, long.class);
        keyTypes.put(TFloatObjectMap.class, float.class);
        keyTypes.put(TDoubleObjectMap.class, double.class);
        keyTypes.put(TByteObjectMap.class, byte.class);
        keyTypes.put(TCharObjectMap.class, char.class);
        keyTypes.put(TIntIntMap.class, int.class);
    }

    TroveTypeModifier() {
    }
    
    @Override
    public JavaType modifyType(JavaType type, Type jdkType, TypeBindings context, TypeFactory typeFactory) {

        Class<?> rawClass = type.getRawClass();
        Class<?> finalClass = null;
        JavaType keyType   = null;
        JavaType valueType = null;

        for (Map.Entry<Class<?>, Class<?>> classTypeEntry : valTypes.entrySet()) {
            Class<?> mapLikeClass = classTypeEntry.getKey();
            if (mapLikeClass.isAssignableFrom(rawClass)) {
                finalClass = mapLikeClass;
                valueType = typeFactory.constructType(classTypeEntry.getValue());
            }
        }
        for (Map.Entry<Class<?>, Class<?>> classTypeEntry : keyTypes.entrySet()) {
            Class<?> mapLikeClass = classTypeEntry.getKey();
            if (mapLikeClass.isAssignableFrom(rawClass)) {
                finalClass = mapLikeClass;
                keyType = typeFactory.constructType(classTypeEntry.getValue());
            }
        }
        if( keyType == null && valueType == null )
            return type;
        if( keyType == null )
            keyType = _findTypeParam(typeFactory, type, finalClass, 0);
        if( valueType == null )
            valueType = _findTypeParam(typeFactory, type, finalClass, 0);

        return MapLikeType.construct(finalClass, keyType, valueType);

    }

    protected JavaType _findTypeParam(TypeFactory typeFactory, JavaType baseType, Class<?> targetType,
            int index)
    {
        JavaType[] typeParameters = typeFactory.findTypeParameters(baseType, targetType);
        if (typeParameters == null || typeParameters.length <= index) {
            return TypeFactory.unknownType();
        }
        return typeParameters[index];
    }
}
