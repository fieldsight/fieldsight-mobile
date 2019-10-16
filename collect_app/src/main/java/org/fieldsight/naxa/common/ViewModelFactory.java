package org.fieldsight.naxa.common;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.fieldsight.naxa.contact.ContactLocalSource;
import org.fieldsight.naxa.contact.ContactRepository;
import org.fieldsight.naxa.contact.ProjectContactViewModel;
import org.fieldsight.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.fieldsight.naxa.forms.viewmodel.FieldSightFormViewModel;
import org.fieldsight.naxa.generalforms.GeneralFormViewModel;
import org.fieldsight.naxa.generalforms.data.GeneralFormLocalSource;
import org.fieldsight.naxa.generalforms.data.GeneralFormRemoteSource;
import org.fieldsight.naxa.generalforms.data.GeneralFormRepository;
import org.fieldsight.naxa.migrate.MigrateFieldSightViewModel;
import org.fieldsight.naxa.notificationslist.FieldSightNotificationRepository;
import org.fieldsight.naxa.notificationslist.NotificationListViewModel;
import org.fieldsight.naxa.project.data.ProjectLocalSource;
import org.fieldsight.naxa.project.data.ProjectRepository;
import org.fieldsight.naxa.project.data.ProjectSitesRemoteSource;
import org.fieldsight.naxa.project.data.ProjectViewModel;
import org.fieldsight.naxa.scheduled.data.ScheduledFormRepository;
import org.fieldsight.naxa.scheduled.data.ScheduledFormViewModel;
import org.fieldsight.naxa.scheduled.data.ScheduledFormsLocalSource;
import org.fieldsight.naxa.scheduled.data.ScheduledFormsRemoteSource;
import org.fieldsight.naxa.site.CreateSiteViewModel;
import org.fieldsight.naxa.site.FragmentHostViewModel;
import org.fieldsight.naxa.site.db.SiteLocalSource;
import org.fieldsight.naxa.site.db.SiteRepository;
import org.fieldsight.naxa.stages.StageFormRepository;
import org.fieldsight.naxa.stages.StageViewModel;
import org.fieldsight.naxa.stages.data.StageLocalSource;
import org.fieldsight.naxa.stages.data.StageRemoteSource;
import org.fieldsight.naxa.substages.SubStageViewModel;
import org.fieldsight.naxa.substages.data.SubStageLocalSource;
import org.fieldsight.naxa.substages.data.SubStageRepository;
import org.fieldsight.naxa.survey.SurveyFormLocalSource;
import org.fieldsight.naxa.survey.SurveyFormRepository;
import org.fieldsight.naxa.survey.SurveyFormViewModel;
import org.fieldsight.naxa.sync.DownloadViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @SuppressLint("StaticFieldLeak")
    private static ViewModelFactory viewModelFactory;

    private final GeneralFormRepository generalFormRepository;
    private final ScheduledFormRepository scheduledFormRepository;
    private final StageFormRepository stageFormRepository;
    private final SubStageRepository subStageRepository;
    private final ProjectRepository projectRepository;
    private final SiteRepository siteRepository;
    private final SurveyFormRepository surveyFormRepository;
    private final FieldSightNotificationRepository notificationRepository;
    private final ContactRepository contactRepository;


    public ViewModelFactory(
            GeneralFormRepository repository,
            ScheduledFormRepository scheduledFormRepository,
            StageFormRepository stageFormRepository,
            SubStageRepository subStageRepository,
            ProjectRepository projectRepository,
            SiteRepository siteRepository,
            SurveyFormRepository surveyFormRepository,
            FieldSightNotificationRepository notificationRepository,
            ContactRepository contactRepository
    ) {

        this.generalFormRepository = repository;
        this.scheduledFormRepository = scheduledFormRepository;
        this.stageFormRepository = stageFormRepository;
        this.subStageRepository = subStageRepository;
        this.projectRepository = projectRepository;
        this.siteRepository = siteRepository;
        this.surveyFormRepository = surveyFormRepository;
        this.notificationRepository = notificationRepository;
        this.contactRepository = contactRepository;
    }

    public static synchronized ViewModelFactory getInstance() {

        if (viewModelFactory == null) {

            GeneralFormRepository generalFormRepository = GeneralFormRepository.getInstance(
                    GeneralFormLocalSource.getInstance(), GeneralFormRemoteSource.getInstance());
            ScheduledFormRepository scheduledFormRepository = ScheduledFormRepository.getInstance(
                    ScheduledFormsLocalSource.getInstance(), ScheduledFormsRemoteSource.getInstance());

            StageFormRepository stageFormRepository = StageFormRepository.getInstance(StageLocalSource.getInstance(), StageRemoteSource.getInstance());
            SubStageRepository subStageRepository = SubStageRepository.getInstance(SubStageLocalSource.getInstance());
            ProjectRepository projectRepository = ProjectRepository.getInstance(ProjectLocalSource.getInstance(), ProjectSitesRemoteSource.getInstance());
            SiteRepository siteRepository = SiteRepository.getInstance(SiteLocalSource.getInstance());
            SurveyFormRepository surveyFormRepository = SurveyFormRepository.getInstance(SurveyFormLocalSource.getInstance());
            FieldSightNotificationRepository notificationRepository = FieldSightNotificationRepository.getInstance(FieldSightNotificationLocalSource.getInstance());
            ContactRepository contactRepository = ContactRepository.getInstance(ContactLocalSource.getInstance());


            viewModelFactory = new ViewModelFactory(generalFormRepository, scheduledFormRepository,
                    stageFormRepository, subStageRepository, projectRepository, siteRepository,
                    surveyFormRepository, notificationRepository, contactRepository);

        }
        return viewModelFactory;
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
        } else if (modelClass.isAssignableFrom(ProjectContactViewModel.class)) {
            //noinspection unchecked
            return (T) new ProjectContactViewModel(contactRepository);
        } else if (modelClass.isAssignableFrom(MigrateFieldSightViewModel.class)) {
            //noinspection unchecked
            return (T) new MigrateFieldSightViewModel();

        } else if (modelClass.isAssignableFrom(DownloadViewModel.class)) {
            //noinspection unchecked
            return (T) new DownloadViewModel();
        } else if (modelClass.isAssignableFrom(FragmentHostViewModel.class)) {
            //noinspection unchecked
            return (T) new FragmentHostViewModel();
        } else if (modelClass.isAssignableFrom(FieldSightFormViewModel.class)) {
            //noinspection unchecked
            return (T) new FieldSightFormViewModel();
        }
        throw new IllegalArgumentException("Unknown ViewModel class" + modelClass.getName());
    }


}
