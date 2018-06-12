package com.targomo.jackson.datatype.trove;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.targomo.jackson.datatype.trove.deser.TroveDeserializers;
import com.targomo.jackson.datatype.trove.ser.TroveSerializers;

public final class TroveModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    private final int noEntryValue;

    public TroveModule(int noEntryValue) {
        super("TroveModule", ModuleVersion.INSTANCE.version());
        this.noEntryValue = noEntryValue;
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);

        context.addTypeModifier(new TroveTypeModifier());

        context.addSerializers(new TroveSerializers());
        context.addDeserializers(new TroveDeserializers(this.noEntryValue));
    }
}
