package com.targomo.jackson.datatype.trove.ser;

import java.io.IOException;

@SuppressWarnings("serial")
public class IOExceptionWrapper extends RuntimeException
{
    public IOExceptionWrapper(IOException e) {
        super(e);
    }

    // let's use co-variance to get proper type
    @Override
    public IOException getCause() {
        return (IOException) super.getCause();
    }
}
