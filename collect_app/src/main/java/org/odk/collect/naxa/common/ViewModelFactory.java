package org.odk.collect.naxa.common;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import org.odk.collect.naxa.generalforms.GeneralFormViewModel;
import org.odk.collect.naxa.generalforms.data.GeneralFormLocalSource;
import org.odk.collect.naxa.generalforms.data.GeneralFormRemoteSource;
import org.odk.collect.naxa.generalforms.data.GeneralFormRepository;
import org.odk.collect.naxa.project.data.ProjectLocalSource;
import org.odk.collect.naxa.project.data.ProjectRepository;
import org.odk.collect.naxa.project.data.ProjectSitesRemoteSource;
import org.odk.collect.naxa.project.data.ProjectViewModel;
import org.odk.collect.naxa.scheduled.data.ScheduledFormRepository;
import org.odk.collect.naxa.scheduled.data.ScheduledFormViewModel;
import org.odk.collect.naxa.scheduled.data.ScheduledFormsLocalSource;
import org.odk.collect.naxa.scheduled.data.ScheduledFormsRemoteSource;
import org.odk.collect.naxa.site.CreateSiteViewModel;
import org.odk.collect.naxa.site.db.SiteLocalSource;
import org.odk.collect.naxa.site.db.SiteRemoteSource;
import org.odk.collect.naxa.site.db.SiteRepository;
import org.odk.collect.naxa.stages.StageFormRepository;
import org.odk.collect.naxa.stages.StageViewModel;
import org.odk.collect.naxa.stages.data.StageLocalSource;
import org.odk.collect.naxa.stages.data.StageRemoteSource;
import org.odk.collect.naxa.substages.SubStageViewModel;
import org.odk.collect.naxa.substages.data.SubStageLocalSource;
import org.odk.collect.naxa.substages.data.SubStageRepository;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;

    private final GeneralFormRepository generalFormRepository;
    private final ScheduledFormRepository scheduledFormRepository;
    private final StageFormRepository stageFormRepository;
    private final SubStageRepository subStageRepository;
    private final ProjectRepository projectRepository;
    private final SiteRepository siteRepository;


    private final Application application;

    public ViewModelFactory(Application application,
                            GeneralFormRepository repository,
                            ScheduledFormRepository scheduledFormRepository,
                            StageFormRepository stageFormRepository,
                            SubStageRepository subStageRepository,
                            ProjectRepository projectRepository,
                            SiteRepository siteRepository) {
        this.application = application;
        this.generalFormRepository = repository;
        this.scheduledFormRepository = scheduledFormRepository;
        this.stageFormRepository = stageFormRepository;
        this.subStageRepository = subStageRepository;
        this.projectRepository = projectRepository;
        this.siteRepository = siteRepository;
    }

    public static ViewModelFactory getInstance(Application application) {

        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    GeneralFormRepository generalFormRepository = GeneralFormRepository.getInstance(
                            GeneralFormLocalSource.getInstance(), GeneralFormRemoteSource.getInstance());
                    ScheduledFormRepository scheduledFormRepository = ScheduledFormRepository.getInstance(
                            ScheduledFormsLocalSource.getInstance(), ScheduledFormsRemoteSource.getInstance());

                    StageFormRepository stageFormRepository = StageFormRepository.getInstance(StageLocalSource.getInstance(), StageRemoteSource.getInstance());
                    SubStageRepository subStageRepository = SubStageRepository.getInstance(SubStageLocalSource.getInstance(), StageRemoteSource.getInstance());
                    ProjectRepository projectRepository = ProjectRepository.getInstance(ProjectLocalSource.getInstance(), ProjectSitesRemoteSource.getInstance());
                    SiteRepository siteRepository = SiteRepository.getInstance(SiteLocalSource.getInstance(), SiteRemoteSource.getInstance());

                    INSTANCE = new ViewModelFactory(application, generalFormRepository, scheduledFormRepository, stageFormRepository, subStageRepository, projectRepository, siteRepository);
                }
            }
        }
        return INSTANCE;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GeneralFormViewModel.class)) {
            //noinspection unchecked
            return (T) new GeneralFormViewModel(generalFormRepository);
        } else if (modelClass.isAssignableFrom(ScheduledFormViewModel.class)) {
            //noinspection unchecked
            return (T) new ScheduledFormViewModel(scheduledFormRepository);
        } else if (modelClass.isAssignableFrom(StageViewModel.class)) {
            //noinspection unchecked
            return (T) new StageViewModel(stageFormRepository);
        } else if (modelClass.isAssignableFrom(SubStageViewModel.class)) {
            //noinspection unchecked
            return (T) new SubStageViewModel(subStageRepository);
        } else if (modelClass.isAssignableFrom(ProjectViewModel.class)) {
            //noinspection unchecked
            return (T) new ProjectViewModel(projectRepository);
        } else if (modelClass.isAssignableFrom(CreateSiteViewModel.class)) {
            //noinspection unchecked
            return (T) new CreateSiteViewModel(siteRepository);
        }

        throw new IllegalArgumentException("Unknown ViewModel class" + modelClass.getName());
    }
}
