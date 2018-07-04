package org.odk.collect.naxa.common;

/**
 * Created on 10/24/17
 * by nishon.tan@gmail.com
 */


public class ObservableBoolean {

    private boolean value;

    private ChangeListener listener;


    public ObservableBoolean(ChangeListener listener) {
        this.listener = listener;
    }

    public void set(boolean value) {
        this.value = value;

        if (listener != null) {
            listener.onChange(value);
        }
    }


    public interface ChangeListener {
        void onChange(boolean value);
    }
}
