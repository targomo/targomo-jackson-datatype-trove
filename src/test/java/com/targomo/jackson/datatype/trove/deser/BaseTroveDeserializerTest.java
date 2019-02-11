package com.targomo.jackson.datatype.trove.deser;

import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.datatype.joda.JodaModule;

import com.targomo.jackson.datatype.trove.TroveModule;

import org.junit.Before;

public abstract class BaseTroveDeserializerTest {

    ObjectReader reader;

    final int NO_ENTRY_VALUE_TESTS = Integer.MAX_VALUE;

    @Before
    public void setUp() {
        reader = new ObjectMapper()
                .registerModule(new TroveModule(NO_ENTRY_VALUE_TESTS))
                .registerModule(new JodaModule())
                .reader();
    }
}
