package org.fieldsight.naxa.forms.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.forms.source.local.FieldSightForm;
import org.fieldsight.naxa.forms.source.local.FieldSightFormsLocalSource;

import java.util.List;

public class FieldSightFormViewModel extends ViewModel {

    public LiveData<List<FieldSightForm>> loadForm(String type,String projectId, String siteId) {
        LiveData<List<FieldSightForm>> forms;
        switch (type) {
            case Constant.FormType.GENERAl:
                forms = FieldSightFormsLocalSource.getInstance().getGeneralForms(projectId,siteId);
                break;
            case Constant.FormType.SCHEDULE:
                forms = FieldSightFormsLocalSource.getInstance().getScheduledForms(projectId,siteId);
                break;
            case Constant.FormType.STAGED:
                forms = FieldSightFormsLocalSource.getInstance().getStagedForms(projectId,siteId);
                break;
            default:
                throw new IllegalArgumentException("Form type is unknown");
        }

        return forms;
    }
}
