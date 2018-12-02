package org.bcss.collect.naxa.sync;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class DisposableManager {

    private static CompositeDisposable compositeDisposable;

    public static void add(Disposable disposable) {
        getCompositeDisposable().add(disposable);
    }

    static void dispose() {
        getCompositeDisposable().clear();

    }

    public static int size() {
        return getCompositeDisposable().size();
    }

    private static CompositeDisposable getCompositeDisposable() {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        return compositeDisposable;
    }

    private DisposableManager() {}
}
