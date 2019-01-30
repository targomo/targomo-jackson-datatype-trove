package com.targomo.jackson.datatype.trove.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.targomo.jackson.datatype.trove.TroveModule;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class TIntFloatMapDeserializerTest extends BaseTroveDeserializerTest
{
    @Test
    public void testSimple() throws IOException
    {

        TIntFloatMap map = reader.forType(TIntFloatMap.class)
                .readValue("{\"8\" : 1.0, \"1\" : 2.5}");
        assertEquals(2, map.size());
        assertThat(map.get(8)).isEqualTo(1.0f);
        assertThat(map.get(1)).isEqualTo(2.5f);
        assertThat(map.get(3)).isEqualTo(Integer.MAX_VALUE);
    }


    @Test
    public void testBothWays() throws IOException {

        TIntFloatMap map = new TIntFloatHashMap();
        map.put(13, 1.0f);
        map.put(15, 2.5f);
        map.put(17, -3.886757545f);

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new TroveModule(Integer.MAX_VALUE))
                .registerModule(new JodaModule());
        System.out.println( om.writerWithDefaultPrettyPrinter().writeValueAsString(map) );

        String json = om.writeValueAsString(map);
        TIntFloatMap duplicatedMap = om.readValue(json,TIntFloatMap.class);

        assertThat(duplicatedMap.keys()).containsExactly(map.keys());
        assertThat(duplicatedMap.values()).containsExactly(map.values());
    }
}
