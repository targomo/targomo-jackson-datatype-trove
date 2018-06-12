package com.targomo.jackson.datatype.trove.ser;

import com.fasterxml.jackson.core.type.TypeReference;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public final class TObjectIntMapSerializerTest extends BaseTroveSerializerTest {

    private static final String EXPECTED_DATETIME_KEY_MAP;
    private static final String EXPECTED_STRING_KEY_MAP = "{\"three\":3,\"one\":1,\"two\":2}";

    static {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        StringBuilder sb = new StringBuilder().append('{');
        getDateTimeKeyMap().forEachEntry( (date,intVal) ->
                sb.append('"').append(date.toString(fmt))
                    .append("\":").append(intVal).append(",") != null);
        sb.deleteCharAt(sb.length()-1); //remove last comma
        EXPECTED_DATETIME_KEY_MAP = sb.append('}').toString();
    }

    @Test
    public void testSerializeStringKeyTypeRef() throws IOException {

        TObjectIntHashMap<String> map = getStringKeyMap();

        assertEquals(EXPECTED_STRING_KEY_MAP,
            getString(writer.withType(new TypeReference<TObjectIntMap<String>>() {}), map));

    }

    @Test
    public void testSerializeDatetimeKeyTypeRef() throws IOException {

        TObjectIntHashMap<DateTime> map = getDateTimeKeyMap();

        assertEquals(EXPECTED_DATETIME_KEY_MAP,
            getString(writer.withType(new TypeReference<TObjectIntMap<DateTime>>() {}), map));
    }

    @Test
    public void testSerializeStringKeyNoTypeRef() throws IOException {

        TObjectIntHashMap<String> map = getStringKeyMap();

        assertEquals(EXPECTED_STRING_KEY_MAP, getString(writer, map));

    }

    @Test
    public void testSerializeDatetimeKeyNoTypeRef() throws IOException {

        TObjectIntHashMap<DateTime> map = getDateTimeKeyMap();

        System.out.println( EXPECTED_DATETIME_KEY_MAP );

        assertEquals(EXPECTED_DATETIME_KEY_MAP, getString(writer, map));

    }

    private TObjectIntHashMap<String> getStringKeyMap() {
        TObjectIntHashMap<String> map = new TObjectIntHashMap<>();

        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        return map;
    }

    private static TObjectIntHashMap<DateTime> getDateTimeKeyMap() {
        TObjectIntHashMap<DateTime> map = new TObjectIntHashMap<>();

        DateTime now = new DateTime("2012-08-12T03:42:30.797Z");
        map.put(now, 1);
        map.put(now.plusDays(2), 2);
        map.put(now.plusDays(3), 3);
        return map;
    }

}
