package org.bcss.collect.naxa.onboarding;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created on 11/19/17
 * by nishon.tan@gmail.com
 */

public class XMLFormDownloadReceiver extends ResultReceiver {
    private XMLFormDownloadReceiver.Receiver resultReceiver;

    public XMLFormDownloadReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(XMLFormDownloadReceiver.Receiver receiver) {
        resultReceiver = receiver;
    }

    public interface Receiver {
        void onReceiveXMLFormDownloadResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultReceiver != null) {
            resultReceiver.onReceiveXMLFormDownloadResult(resultCode, resultData);
        }
    }
}
