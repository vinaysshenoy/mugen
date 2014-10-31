package com.vinaysshenoy.mugen.attachers;

/**
 * Base class for attachers
 *
 * Created by vinaysshenoy on 31/10/14.
 */
public abstract class BaseAttacher<AdapterView> {

    private AdapterView mAdapterView;

    public BaseAttacher(final AdapterView adapterView) {
        mAdapterView = adapterView;
    }
}
