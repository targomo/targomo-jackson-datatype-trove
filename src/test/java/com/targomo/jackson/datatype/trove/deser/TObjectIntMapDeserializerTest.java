package com.targomo.jackson.datatype.trove.deser;

import static org.junit.Assert.assertEquals;
import gnu.trove.map.TObjectIntMap;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

public class TObjectIntMapDeserializerTest extends BaseTroveDeserializerTest
{
    @Test
    public void testSimple() throws IOException
    {
        TObjectIntMap<String> map = reader
                .withType(
                        new TypeReference<TObjectIntMap<String>>() { })
                        .readValue("{\"abc\" : 3, \"foo\" : -7}");
        assertEquals(2, map.size());
        assertEquals(Integer.valueOf(-7), Integer.valueOf(map.get("foo")));
        assertEquals(NO_ENTRY_VALUE_INT_TESTS, map.get("wot"));
    }
}
