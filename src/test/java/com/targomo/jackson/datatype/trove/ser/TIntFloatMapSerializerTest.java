package com.targomo.jackson.datatype.trove.ser;

import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TIntFloatMapSerializerTest extends BaseTroveSerializerTest {

    @Test
    public void testSimple() throws IOException
    {
        TIntFloatMap map = new TIntFloatHashMap();
        map.put(16, 1.5f);

//        System.out.println(getString(writer, map));

        assertEquals("{\"16\":1.5}", getString(writer, map));
    }

    @Test
    public void testEmptyMap() throws IOException
    {
        TIntFloatMap map = new TIntFloatHashMap();
        assertEquals("{}", getString(writer, map));
    }
}
