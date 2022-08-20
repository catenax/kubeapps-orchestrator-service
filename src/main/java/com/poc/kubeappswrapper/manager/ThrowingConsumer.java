package com.poc.kubeappswrapper.utility;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T> {

    @Override
    default void accept(final T elem) {
        try {
            acceptThrows(elem);
        } catch (final Exception e) {
            throw LombokTool.sneakyThrow(e);
        }
    }

    void acceptThrows(T elem) throws Exception;

}
