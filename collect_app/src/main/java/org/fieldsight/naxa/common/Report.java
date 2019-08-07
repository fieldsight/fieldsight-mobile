package org.fieldsight.naxa.common;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

public class Report {

    public static void report(CustomEvent customEvent) {
        Answers.getInstance()
                .logCustom(customEvent);
    }

}
