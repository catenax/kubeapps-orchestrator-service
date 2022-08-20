package com.poc.kubeappswrapper.utility;

import java.util.function.Supplier;

public abstract class TailRec<T> {
    public abstract T eval();

    private static class Ret<T> extends TailRec<T> {

        private final T t;

        private Ret(T t) {
            this.t = t;
        }

        @Override
        public T eval() {
            return t;
        }
    }

    private static class Sus<T> extends TailRec<T> {
        private final Supplier<TailRec<T>> supSusT;

        private Sus(Supplier<TailRec<T>> supSusT) {
            this.supSusT = supSusT;
        }
        @Override
        public T eval() {
            var s = supSusT.get();
            while (s instanceof Sus)
                s = ((Sus<T>)s).supSusT.get();
            return s.eval();
        }
    }

    public static <T> TailRec<T> ret(T t) {
        return new Ret<>(t);
    }

    public static <T> TailRec<T> sus(Supplier<TailRec<T>> supSusT) {
        return new Sus<>(supSusT);
    }
}
