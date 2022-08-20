package com.poc.kubeappswrapper.utility;

import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<R> extends Supplier<R> {

    default R get() {
        try {
            return getThrows();
        } catch (final Exception e) {
            throw LombokTool.sneakyThrow(e);
        }
    }

    R getThrows() throws Exception;

}
