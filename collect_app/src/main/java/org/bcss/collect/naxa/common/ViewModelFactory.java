package org.bcss.collect.naxa.common;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;


import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.bcss.collect.naxa.generalforms.GeneralFormViewModel;
import org.bcss.collect.naxa.generalforms.data.GeneralFormLocalSource;
import org.bcss.collect.naxa.generalforms.data.GeneralFormRemoteSource;
import org.bcss.collect.naxa.generalforms.data.GeneralFormRepository;
import org.bcss.collect.naxa.notificationslist.FieldSightNotificationRepository;
import org.bcss.collect.naxa.notificationslist.NotificationListViewModel;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.project.data.ProjectRepository;
import org.bcss.collect.naxa.project.data.ProjectSitesRemoteSource;
import org.bcss.collect.naxa.project.data.ProjectViewModel;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormRepository;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormViewModel;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsLocalSource;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsRemoteSource;
import org.bcss.collect.naxa.site.CreateSiteViewModel;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.site.db.SiteRemoteSource;
import org.bcss.collect.naxa.site.db.SiteRepository;
import org.bcss.collect.naxa.stages.StageFormRepository;
import org.bcss.collect.naxa.stages.StageViewModel;
import org.bcss.collect.naxa.stages.data.StageLocalSource;
import org.bcss.collect.naxa.stages.data.StageRemoteSource;
import org.bcss.collect.naxa.substages.SubStageViewModel;
import org.bcss.collect.naxa.substages.data.SubStageLocalSource;
import org.bcss.collect.naxa.substages.data.SubStageRepository;
import org.bcss.collect.naxa.survey.SurveyFormLocalSource;
import org.bcss.collect.naxa.survey.SurveyFormRepository;
import org.bcss.collect.naxa.survey.SurveyFormViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;

    private final GeneralFormRepository generalFormRepository;
    private final ScheduledFormRepository scheduledFormRepository;
    private final StageFormRepository stageFormRepository;
    private final SubStageRepository subStageRepository;
    private final ProjectRepository projectRepository;
    private final SiteRepository siteRepository;
    private final SurveyFormRepository surveyFormRepository;
    private final FieldSightNotificationRepository notificationRepository;


    private final Application application;

    public ViewModelFactory(Application application,
                            GeneralFormRepository repository,
                            ScheduledFormRepository scheduledFormRepository,
                            StageFormRepository stageFormRepository,
                            SubStageRepository subStageRepository,
                            ProjectRepository projectRepository,
                            SiteRepository siteRepository,
                            SurveyFormRepository surveyFormRepository,
                            FieldSightNotificationRepository notificationRepository
    ) {
        this.application = application;
        this.generalFormRepository = repository;
        this.scheduledFormRepository = scheduledFormRepository;
        this.stageFormRepository = stageFormRepository;
        this.subStageRepository = subStageRepository;
        this.projectRepository = projectRepository;
        this.siteRepository = siteRepository;
        this.surveyFormRepository = surveyFormRepository;
        this.notificationRepository = notificationRepository;
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
                    SurveyFormRepository surveyFormRepository = SurveyFormRepository.getInstance(SurveyFormLocalSource.getInstance());
                    FieldSightNotificationRepository notificationRepository = FieldSightNotificationRepository.getInstance(FieldSightNotificationLocalSource.getInstance());


                    INSTANCE = new ViewModelFactory(application, generalFormRepository, scheduledFormRepository,
                            stageFormRepository, subStageRepository, projectRepository, siteRepository,
                            surveyFormRepository, notificationRepository);
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
        } else if (modelClass.isAssignableFrom(SurveyFormViewModel.class)) {
            //noinspection unchecked
            return (T) new SurveyFormViewModel(surveyFormRepository);
        } else if (modelClass.isAssignableFrom(NotificationListViewModel.class)) {
            //noinspection unchecked
            return (T) new NotificationListViewModel(notificationRepository);
        }

        throw new IllegalArgumentException("Unknown ViewModel class" + modelClass.getName());
    }
}
