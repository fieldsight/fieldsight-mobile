package org.fieldsight.naxa.v3.forms;

import android.util.Pair;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.network.APIEndpoint;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.utilities.FormDownloader;

import java.util.HashMap;
import java.util.List;

public class FieldSightFormDownloader extends FormDownloader {
    public FieldSightFormDownloader(boolean isTempDownload) {
        super(isTempDownload);
    }


    HashMap<FieldSightFormDetails, String> downloadFieldSightForms(List<FieldSightFormDetails> toDownload) {
        formsDao = new FormsDao();
        int total = toDownload.size();
        int count = 1;

        final HashMap<FieldSightFormDetails, String> result = new HashMap<>();

        for (FieldSightFormDetails fd : toDownload) {

//            if (!fd.getDownloadUrl().contains("http")) {
//                String formURL = APIEndpoint.BASE_URL + fd.getDownloadUrl();
//                fd.setDownloadUrl(formURL);
//            }
//            if (!fd.getManifestUrl().contains("http")) {
//                String manifestURL = APIEndpoint.BASE_URL + fd.getDownloadUrl();
//                fd.setManifestUrl(manifestURL);
//            }

            try {
                String message = processOneForm(total, count++, fd);
                result.put(fd, message.isEmpty() ?
                        Collect.getInstance().getString(R.string.success) : message);
            } catch (TaskCancelledException cd) {
                break;
            }
        }

        return result;
    }

    public Pair<FieldSightFormDetails, String> downloadSingleFieldSightForm(FieldSightFormDetails fd) {
        formsDao = new FormsDao();
        String message;
        Pair<FieldSightFormDetails, String> pair = null;
//
//        if (!fd.getDownloadUrl().contains("http")) {
//            String formURL = APIEndpoint.BASE_URL + fd.getDownloadUrl();
//            fd.setDownloadUrl(formURL);
//        }
//        if (!fd.getManifestUrl().contains("http")) {
//            String manifestURL = APIEndpoint.BASE_URL + fd.getDownloadUrl();
//            fd.setManifestUrl(manifestURL);
//        }

        try {
            message = processOneForm(1, 1, fd);
            pair = Pair.create(fd, message.isEmpty() ?
                    Collect.getInstance().getString(R.string.success) : message);
        } catch (TaskCancelledException e) {
            e.printStackTrace();
        }

        return pair;
    }

}
