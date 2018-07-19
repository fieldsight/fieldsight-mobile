package org.odk.collect.naxa.generalforms.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.odk.collect.naxa.generalforms.GeneralForm;
import org.odk.collect.naxa.login.model.Project;

import java.util.List;

public class GeneralFormViewModel extends AndroidViewModel {

    private GeneralFormRepository generalFormRepository;
    private List<Project> allProjects;

    public GeneralFormViewModel(@NonNull Application application) {
        super(application);
        this.generalFormRepository = new GeneralFormRepository(application);
    }

    public LiveData<List<GeneralForm>> getGeneralFormsByProjectID(Project project) {
        return generalFormRepository.getFromProjectId(project.getId());
    }

}
