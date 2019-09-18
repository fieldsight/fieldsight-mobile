package org.fieldsight.naxa.forms.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.fieldsight.naxa.forms.data.local.FieldSightForm;
import org.fieldsight.naxa.forms.data.local.FieldSightFormDetails;
import org.fieldsight.naxa.forms.data.local.FieldSightFormsLocalSource;

import java.util.List;

public class FieldSightFormViewModel extends ViewModel {

    public LiveData<List<FieldSightFormDetails>> loadForm(String type, String projectId, String siteId) {
        return FieldSightFormsLocalSource.getInstance().getFormByType(type, projectId, siteId);
    }
}
