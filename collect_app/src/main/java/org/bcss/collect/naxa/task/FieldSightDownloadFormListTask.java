/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.bcss.collect.naxa.task;

import android.os.AsyncTask;

import org.bcss.collect.android.listeners.FormListDownloaderListener;
import org.bcss.collect.android.logic.FormDetails;
import org.bcss.collect.android.utilities.DownloadFormListUtils;
import org.bcss.collect.naxa.common.FieldSightDownloadFormListUtils;
import org.bcss.collect.naxa.onboarding.XMLForm;

import java.util.HashMap;

/**
 * Background task for downloading forms from urls or a formlist from a url. We overload this task
 * a bit so that we don't have to keep track of two separate downloading tasks and it simplifies
 * interfaces. If LIST_URL is passed to doInBackground(), we fetch a form list. If a hashmap
 * containing form/url pairs is passed, we download those forms.
 *
 * @author carlhartung
 */
public class FieldSightDownloadFormListTask extends AsyncTask<Void, String, HashMap<String, FormDetails>> {

    private FormListDownloaderListener stateListener;
    private XMLForm xmlForm;

    public FieldSightDownloadFormListTask(XMLForm xmlForm) {
        this.xmlForm = xmlForm;
    }

    public FieldSightDownloadFormListTask() {

    }

    @Override
    protected HashMap<String, FormDetails> doInBackground(Void... values) {
        return FieldSightDownloadFormListUtils.downloadFormList(false, xmlForm);
    }

    @Override
    protected void onPostExecute(HashMap<String, FormDetails> value) {
        synchronized (this) {
            if (stateListener != null) {
                stateListener.formListDownloadingComplete(value);
            }
        }
    }

    public void setDownloaderListener(FormListDownloaderListener sl) {
        synchronized (this) {
            stateListener = sl;
        }
    }

}
