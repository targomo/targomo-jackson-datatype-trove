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

    @Before
    public void setUp() {
        writer = new ObjectMapper().registerModule(new TroveModule(Integer.MAX_VALUE)).registerModule(new JodaModule()).writer();
    }

    String getString(ObjectWriter w, Object o) throws IOException {
        StringWriter sw = new StringWriter();
        w.writeValue(sw, o);
        return sw.toString();
    }
}
