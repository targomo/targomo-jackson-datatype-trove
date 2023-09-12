package com.targomo.jackson.datatype.trove.ser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.targomo.jackson.datatype.trove.TroveModule;
import org.junit.Before;

import java.io.IOException;
import java.io.StringWriter;

public abstract class BaseTroveSerializerTest {

    ObjectWriter writer;

    final int NO_ENTRY_VALUE_INT_TESTS = Integer.MAX_VALUE;
    final long NO_ENTRY_VALUE_LONG_TESTS = Long.MAX_VALUE;
    final float NO_ENTRY_VALUE_FLOAT_TESTS = Float.MAX_VALUE;

    @Before
    public void setUp() {
        writer = new ObjectMapper()
                .registerModule(new TroveModule(NO_ENTRY_VALUE_INT_TESTS, NO_ENTRY_VALUE_LONG_TESTS, NO_ENTRY_VALUE_FLOAT_TESTS))
                .registerModule(new JodaModule()).writer();
    }

    String getString(ObjectWriter w, Object o) throws IOException {
        StringWriter sw = new StringWriter();
        w.writeValue(sw, o);
        return sw.toString();
    }
}
