package org.fieldsight.naxa.v3.forms;

import android.util.Pair;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.common.FieldSightUserSession;
import org.fieldsight.naxa.forms.data.local.FieldSightFormDetails;
import org.fieldsight.naxa.forms.data.local.FieldsightFormDetailsv3;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.utilities.FormDownloader;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class FieldSightFormDownloader extends FormDownloader {
    public FieldSightFormDownloader(boolean isTempDownload) {
        super(isTempDownload);
    }

    private final String urlPrefix = FieldSightUserSession.getServerUrl(Collect.getInstance());

    HashMap<FieldSightFormDetails, String> downloadFieldSightForms(List<FieldSightFormDetails> toDownload) {

        // if form successfully downloaded it returns empty string else failed
        formsDao = new FormsDao();
        int total = toDownload.size();
        int count = 1;

        final HashMap<FieldSightFormDetails, String> result = new HashMap<>();
        for (FieldSightFormDetails fd : toDownload) {

            if (!fd.getDownloadUrl().startsWith("http")) {
                String formURL = urlPrefix + fd.getDownloadUrl();
                fd.setDownloadUrl(formURL);
            }
            if (!fd.getManifestUrl().startsWith("http")) {
                String manifestURL = urlPrefix + fd.getManifestUrl();
                fd.setManifestUrl(manifestURL);
            }

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

    public Pair<FieldsightFormDetailsv3, String> downloadSingleFieldSightForm(FieldsightFormDetailsv3 fieldsightFormDetailsv3) {
        // if form successfully downloaded it returns empty string else failed
        FormDetails fd = fieldsightFormDetailsv3.getFormDetails();
        formsDao = new FormsDao();
        String message;
        Pair<FieldsightFormDetailsv3, String> pair = null;

        if (!fd.getDownloadUrl().startsWith("http") || !fd.getDownloadUrl().startsWith("https")) {
            String formURL = urlPrefix + fd.getDownloadUrl();
            fd.setDownloadUrl(formURL);
        }
        if (!fd.getManifestUrl().startsWith("http") || !fd.getDownloadUrl().startsWith("https")) {
            String manifestURL = urlPrefix + fd.getManifestUrl();
            fd.setManifestUrl(manifestURL);
        }

        try {
            message = processOneForm(1, 1, fd);
            pair = Pair.create(fieldsightFormDetailsv3, message);
            Timber.d("form downloading starts for PROJECT = " + fieldsightFormDetailsv3.getSiteProjectId() + " or " + fieldsightFormDetailsv3.getProject() + " for = " + fd.getFormName());
        } catch (TaskCancelledException e) {
            Timber.e(e);
            pair = Pair.create(fieldsightFormDetailsv3, "Failed to create form download request");
        }
        return pair;
    }

    public Pair<FieldSightFormDetails, String> downloadSingleFieldSightForm(FieldSightFormDetails fd) {
        formsDao = new FormsDao();
        String message;
        Pair<FieldSightFormDetails, String> pair = null;

        if (!fd.getDownloadUrl().contains("http")) {
            String formURL = urlPrefix + fd.getDownloadUrl();
            fd.setDownloadUrl(formURL);
        }
        if (!fd.getManifestUrl().contains("http")) {
            String manifestURL = urlPrefix + fd.getManifestUrl();
            fd.setManifestUrl(manifestURL);
        }

        try {
            message = processOneForm(1, 1, fd);
            pair = Pair.create(fd, message.isEmpty() ?
                    Collect.getInstance().getString(R.string.success) : message);
        } catch (TaskCancelledException e) {
            Timber.e(e);
        }

        return pair;
    }

}
