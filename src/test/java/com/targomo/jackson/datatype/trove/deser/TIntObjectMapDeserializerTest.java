package com.targomo.jackson.datatype.trove.deser;

import static org.junit.Assert.assertEquals;

import gnu.trove.map.TIntObjectMap;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

public class TIntObjectMapDeserializerTest extends BaseTroveDeserializerTest
{
    @Test
    public void testSimple() throws IOException
    {
        TIntObjectMap<String> map = reader.forType(
                new TypeReference<TIntObjectMap<String>>() { })
                .readValue("{\"8\" : \"Foo\", \"1\" : \"x\"}");
        assertEquals(2, map.size());
        assertEquals("Foo", map.get(8));
    }
}
