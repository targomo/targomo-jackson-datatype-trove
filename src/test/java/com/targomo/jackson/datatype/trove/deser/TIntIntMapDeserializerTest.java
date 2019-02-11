package com.targomo.jackson.datatype.trove.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.targomo.jackson.datatype.trove.TroveModule;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

public class TIntIntMapDeserializerTest extends BaseTroveDeserializerTest
{
    @Test
    public void testSimple() throws IOException
    {

        TIntIntMap map = reader.forType(TIntIntMap.class)
                .readValue("{\"8\" : 1, \"1\" : 2}");
        assertEquals(2, map.size());
        assertEquals(1, map.get(8));
        assertEquals(NO_ENTRY_VALUE_TESTS, map.get(3));
    }


    @Test
    public void testBothWays() throws IOException {

        TIntIntMap map = new TIntIntHashMap();
        map.put(13, 1);
        map.put(15, 2);
        map.put(17, 3);

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new TroveModule(NO_ENTRY_VALUE_TESTS))
                .registerModule(new JodaModule());
//        System.out.println( om.writerWithDefaultPrettyPrinter().writeValueAsString(map) );

        String json = om.writeValueAsString(map);
        TIntIntMap duplicatedMap = om.readValue(json,TIntIntMap.class);

        assertThat(duplicatedMap.keys()).containsExactly(map.keys());
        assertThat(duplicatedMap.values()).containsExactly(map.values());
    }
}
