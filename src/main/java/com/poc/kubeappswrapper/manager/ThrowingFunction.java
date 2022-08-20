package com.poc.kubeappswrapper.utility;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R> extends Function<T, R> {

    default R apply(T t) {
        try {
            return applyThrows(t);
        } catch (final Exception e) {
            throw LombokTool.sneakyThrow(e);
        }
    }

    R applyThrows(T elem) throws Exception;

}
