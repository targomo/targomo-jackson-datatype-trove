package com.targomo.jackson.datatype.trove.ser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import gnu.trove.map.TIntObjectMap;
import org.junit.Test;

import gnu.trove.map.hash.TIntObjectHashMap;

public class TIntObjectMapSerializerTest extends BaseTroveSerializerTest {

    @Test
    public void testSimple() throws IOException
    {
        TIntObjectHashMap<String> map = new TIntObjectHashMap<String>();
        map.put(13, "foo");

        assertEquals("{\"13\":\"foo\"}", getString(writer, map));
    }

    @Test
    public void testArray() throws IOException
    {
        TIntObjectMap<double[][]> map = new TIntObjectHashMap<>();
        map.put(13, new double[][]{{1.0,2.0,4.0},{3.0,6.0,7.0}});

        System.out.println( getString(writer, map) );

        assertEquals("{\"13\":[[1.0,2.0,4.0],[3.0,6.0,7.0]]}", getString(writer, map));
    }
}
