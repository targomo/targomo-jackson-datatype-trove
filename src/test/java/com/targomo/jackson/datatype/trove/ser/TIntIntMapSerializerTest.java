package com.targomo.jackson.datatype.trove.ser;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TIntIntMapSerializerTest extends BaseTroveSerializerTest {

    @Test
    public void testSimple() throws IOException
    {
        TIntIntMap map = new TIntIntHashMap();
        map.put(13, 1);

        System.out.println(getString(writer, map));

        assertEquals("{\"13\":1}", getString(writer, map));
    }
}
