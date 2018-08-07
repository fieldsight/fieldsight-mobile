package org.bcss.collect.android.widgets;

import android.support.annotation.NonNull;

import org.bcss.collect.android.widgets.base.GeneralSelectMultiWidgetTest;
import org.robolectric.RuntimeEnvironment;

/**
 * @author James Knight
 */

public class SelectMultiWidgetTest extends GeneralSelectMultiWidgetTest<SelectMultiWidget> {
    @NonNull
    @Override
    public SelectMultiWidget createWidget() {
        return new SelectMultiWidget(RuntimeEnvironment.application, formEntryPrompt);
    }
}
