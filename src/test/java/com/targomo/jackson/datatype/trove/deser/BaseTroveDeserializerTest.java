package com.targomo.jackson.datatype.trove.deser;

import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.datatype.joda.JodaModule;

import com.targomo.jackson.datatype.trove.TroveModule;

import org.junit.Before;

public abstract class BaseTroveDeserializerTest {
    ObjectReader reader;

    @Before
    public void setUp() {
        reader = new ObjectMapper().registerModule(new TroveModule(Integer.MAX_VALUE)).registerModule(new JodaModule()).reader();
    }
}
