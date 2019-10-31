package org.fieldsight.naxa.forms.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.fieldsight.naxa.forms.data.local.FieldSightFormsLocalSourcev3;
import org.fieldsight.naxa.forms.data.local.FieldsightFormDetailsv3;
import org.fieldsight.naxa.login.model.Project;

import java.util.List;

public class FieldSightFormViewModel extends ViewModel {

    public LiveData<List<FieldsightFormDetailsv3>> loadForm(String type, String projectId, String siteId, String siteTypeId, String siteRegionId, Project project) {
        return FieldSightFormsLocalSourcev3.getInstance().getFormByType(type, projectId, siteId,siteTypeId, siteRegionId, project);
    }
}
