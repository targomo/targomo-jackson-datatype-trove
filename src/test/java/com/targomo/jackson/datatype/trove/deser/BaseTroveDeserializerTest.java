package com.targomo.jackson.datatype.trove.deser;

import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.datatype.joda.JodaModule;

import com.targomo.jackson.datatype.trove.TroveModule;

import org.junit.Before;

public abstract class BaseTroveDeserializerTest {

    ObjectReader reader;

    final int NO_ENTRY_VALUE_INT_TESTS = Integer.MAX_VALUE;
    final float NO_ENTRY_VALUE_FLOAT_TESTS = Float.MAX_VALUE;
    final long NO_ENTRY_VALUE_LONG_TESTS = Long.MAX_VALUE;

    @Before
    public void setUp() {
        reader = new ObjectMapper()
                .registerModule(new TroveModule(NO_ENTRY_VALUE_INT_TESTS, NO_ENTRY_VALUE_LONG_TESTS, NO_ENTRY_VALUE_FLOAT_TESTS))
                .registerModule(new JodaModule())
                .reader();
    }
}
