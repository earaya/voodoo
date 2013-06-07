package com.earaya.voodoo.async;

import java.util.concurrent.Callable;

public interface UncheckedCallable<T> extends Callable<T> {

    T call();
}
